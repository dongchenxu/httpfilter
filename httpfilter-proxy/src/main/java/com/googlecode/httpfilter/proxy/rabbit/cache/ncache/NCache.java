package com.googlecode.httpfilter.proxy.rabbit.cache.ncache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import com.googlecode.httpfilter.proxy.rabbit.cache.Cache;
import com.googlecode.httpfilter.proxy.rabbit.cache.CacheConfiguration;
import com.googlecode.httpfilter.proxy.rabbit.cache.CacheEntry;
import com.googlecode.httpfilter.proxy.rabbit.cache.CacheException;
import com.googlecode.httpfilter.proxy.rabbit.cache.utils.CacheConfigurationBase;
import com.googlecode.httpfilter.proxy.rabbit.cache.utils.CacheUtils;
import com.googlecode.httpfilter.proxy.rabbit.io.FileHelper;
import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;

/**
 * The NCache is like a Map in lookup/insert/delete The NCache is persistent
 * over sessions (saves itself to disk). The NCache is selfcleaning, that is it
 * removes old stuff.
 * 
 * @param <K>
 *            the key type of the cache
 * @param <V>
 *            the data resource
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class NCache<K, V> implements Cache<K, V>, Runnable {
	private static final String DIR = "/tmp/rabbit/cache"; // standard dir.
	private static final String DEFAULT_CLEAN_LOOP = "60"; // 1 minute

	private static final String CACHEINDEX = "cache.index"; // the indexfile.

	private Configuration configuration = new Configuration();
	private boolean changed = false; // have we changed?
	private Thread cleaner = null; // remover of old stuff.
	private int cleanLoopTime = 60 * 1000; // sleeptime between cleanups.

	private long fileNo = 0;
	private long currentSize = 0;
	private File dir = null;
	private Map<FiledKey<K>, NCacheData<K, V>> htab = null;
	private List<NCacheData<K, V>> vec = null;

	private File tempdir = null;
	private final Object dirLock = new Object();

	private final Logger logger = Logger.getLogger(getClass().getName());

	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private final Lock r = rwl.readLock();
	private final Lock w = rwl.writeLock();

	private final FileHandler<K> fhk;
	private final FileHandler<V> fhv;

	private volatile boolean running = true;

	/**
	 * Create a cache that uses default values. Note that you must call start to
	 * have the cache fully up.
	 * 
	 * @param props
	 *            the configuration of the cache
	 * @param fhk
	 *            the FileHandler for the cache keys
	 * @param fhv
	 *            the FileHandler for the cache values
	 * @throws IOException
	 *             if the cache file directory can not be configured
	 */
	public NCache(SProperties props, FileHandler<K> fhk, FileHandler<V> fhv)
			throws IOException {
		this.fhk = fhk;
		this.fhv = fhv;
		htab = new HashMap<FiledKey<K>, NCacheData<K, V>>();
		vec = new ArrayList<NCacheData<K, V>>();
		setup(props);
	}

	/**
	 * Start the thread that cleans the cache.
	 */
	public void start() {
		cleaner = new Thread(this, getClass().getName() + ".cleaner");
		cleaner.setDaemon(true);
		cleaner.start();
	}

	public CacheConfiguration getCacheConfiguration() {
		return configuration;
	}

	private class Configuration extends CacheConfigurationBase {
		public URL getCacheDir() {
			r.lock();
			try {
				if (dir == null)
					return null;
				return dir.toURI().toURL();
			} catch (MalformedURLException e) {
				return null;
			} finally {
				r.unlock();
			}
		}

		/**
		 * Sets the cachedir. This will flush the cache and make it try to read
		 * in the cache from the new dir.
		 * 
		 * @param newDir
		 *            the name of the new directory to use.
		 * @throws IOException
		 *             if the new cache file directory can not be configured
		 */
		private void setCacheDir(String newDir) throws IOException {
			w.lock();
			try {
				// save old cachedir.
				if (dir != null)
					writeCacheIndex();

				// does new dir exist?
				dir = new File(newDir);
				File dirtest = dir;
				boolean readCache = true;
				if (!dirtest.exists()) {
					FileHelper.mkdirs(dirtest);
					if (!dirtest.exists()) {
						logger.warning("could not create cachedir: " + dirtest);
					}
					readCache = false;
				} else if (dirtest.isFile()) {
					logger.warning("Cachedir: " + dirtest + " is a file");
				}

				synchronized (dirLock) {
					tempdir = new File(dirtest, CacheUtils.TEMPDIR);
					if (!tempdir.exists()) {
						FileHelper.mkdirs(tempdir);
						if (!tempdir.exists()) {
							logger.warning("could not create cache tempdir: "
									+ tempdir);
						}
					} else if (tempdir.isFile()) {
						logger.warning("Cache temp dir is a file: " + tempdir);
					}
				}
				if (readCache)
					// move to new dir.
					readCacheIndex();
			} finally {
				w.unlock();
			}
		}
	}

	/**
	 * Get how long time the cleaner sleeps between cleanups.
	 * 
	 * @return the number of millis between cleanups
	 */
	public int getCleanLoopTime() {
		return cleanLoopTime;
	}

	/**
	 * Set how long time the cleaner sleeps between cleanups.
	 * 
	 * @param newCleanLoopTime
	 *            the number of miliseconds to sleep.
	 */
	public void setCleanLoopTime(int newCleanLoopTime) {
		cleanLoopTime = newCleanLoopTime;
	}

	/**
	 * Get the current size of the cache
	 * 
	 * @return the current size of the cache in bytes.
	 */
	public long getCurrentSize() {
		r.lock();
		try {
			return currentSize;
		} finally {
			r.unlock();
		}
	}

	/**
	 * Get the current number of entries in the cache.
	 * 
	 * @return the current number of entries in the cache.
	 */
	public long getNumberOfEntries() {
		r.lock();
		try {
			return htab.size();
		} finally {
			r.unlock();
		}
	}

	/**
	 * Check that the data hook exists.
	 * 
	 * @param e
	 *            the NCacheEntry to check
	 * @return true if the cache data is valid, false otherwise
	 */
	private boolean checkHook(NCacheData<K, V> e) {
		FiledHook<V> hook = e.getDataHook();
		if (hook != null) {
			File entryName = getEntryName(e.getId(), true, "hook");
			if (!entryName.exists())
				return false;
		}
		// no hook is legal.
		return true;
	}

	/**
	 * Get the CacheEntry assosiated with given object.
	 * 
	 * @param k
	 *            the key.
	 * @return the CacheEntry or null (if not found).
	 */
	public CacheEntry<K, V> getEntry(K k) throws CacheException {
		NCacheData<K, V> cacheEntry = getCurrentData(k);
		if (cacheEntry != null && !checkHook(cacheEntry)) {
			// bad entry...
			remove(k);
		}
		/*
		 * If you want to implement LRU or something like that: if (cacheEntry
		 * != null) cacheEntry.setVisited (System.currentTimeMillis ());
		 */
		return getEntry(cacheEntry);
	}

	public File getEntryName(long id, boolean real, String extension) {
		return CacheUtils.getEntryName(dir, id, real, extension);
	}

	/**
	 * Reserve space for a CacheEntry with key o.
	 * 
	 * @param k
	 *            the key for the CacheEntry.
	 * @return a new CacheEntry initialized for the cache.
	 */
	public CacheEntry<K, V> newEntry(K k) {
		long newId = 0;
		// allocate the id for the new entry.
		w.lock();
		try {
			newId = fileNo;
			fileNo++;
		} finally {
			w.unlock();
		}
		long now = System.currentTimeMillis();
		long expires = now + configuration.getCacheTime();
		return new NCacheEntry<K, V>(newId, now, expires, 0, k, null);
	}

	/**
	 * Get the file handler for the keys.
	 * 
	 * @return the FileHandler for the key objects
	 */
	FileHandler<K> getKeyFileHandler() {
		return fhk;
	}

	/**
	 * Get the file handler for the values.
	 * 
	 * @return the FileHandler for the values
	 */
	FileHandler<V> getHookFileHandler() {
		return fhv;
	}

	/**
	 * Insert a CacheEntry into the cache.
	 * 
	 * @param ent
	 *            the CacheEntry to store.
	 */
	public void addEntry(CacheEntry<K, V> ent) throws CacheException {
		if (ent == null)
			return;
		NCacheEntry<K, V> nent = (NCacheEntry<K, V>) ent;
		addEntry(nent);
	}

	private void addEntry(NCacheEntry<K, V> ent) throws CacheException {
		File cfile = getEntryName(ent.getId(), false, null);
		if (!cfile.exists())
			return;

		File newName = getEntryName(ent.getId(), true, null);
		File cacheDir = newName.getParentFile();
		synchronized (dirLock) {
			ensureCacheDirIsValid(cacheDir);
			if (!cfile.renameTo(newName))
				logger.severe("Failed to renamve file from: "
						+ cfile.getAbsolutePath() + " to"
						+ newName.getAbsolutePath());
		}
		cfile = newName;
		NCacheData<K, V> data = getData(ent, cfile);
		w.lock();
		try {
			remove(ent.getKey());
			htab.put(data.getKey(), data);
			currentSize += data.getSize() + data.getKeySize()
					+ data.getHookSize();
			vec.add(data);
		} finally {
			w.unlock();
		}

		changed = true;
	}

	private void ensureCacheDirIsValid(File f) {
		if (f.exists()) {
			if (f.isFile())
				logger.warning("Wanted cachedir is a file: " + f);
			// good situation...
		} else {
			try {
				FileHelper.mkdirs(f);
			} catch (IOException e) {
				logWarning("Could not create directory: " + f, e);
			}
		}
	}

	/**
	 * Signal that a cache entry have changed.
	 */
	public void entryChanged(CacheEntry<K, V> ent, K newKey, V newHook)
			throws CacheException {
		NCacheData<K, V> data = getCurrentData(ent.getKey());
		if (data == null) {
			Thread.dumpStack();
			logger.warning("Failed to find changed entry so ignoring: "
					+ ent.getId());
			return;
		}
		try {
			data.updateExpireAndSize(ent);
			long id = ent.getId();
			FiledWithSize<FiledKey<K>> fkws = storeKey(newKey, id);
			data.setKey(fkws.t, fkws.size);
			FiledWithSize<FiledHook<V>> fhws = storeHook(newHook, id);
			data.setDataHook(fhws.t, fhws.size);
		} catch (IOException e) {
			throw new CacheException("Failed to update entry: entry: " + ent
					+ ", newKey: " + newKey, e);
		} finally {
			changed = true;
		}
	}

	private NCacheData<K, V> getCurrentData(K key) {
		MemoryKey<K> mkey = new MemoryKey<K>(key);
		r.lock();
		try {
			return htab.get(mkey);
		} finally {
			r.unlock();
		}
	}

	private void removeHook(File base, String extension) throws IOException {
		String hookName = base.getName() + extension;
		// remove possible hook before file...
		File hfile = new File(base.getParentFile(), hookName);
		if (hfile.exists())
			FileHelper.delete(hfile);
	}

	/**
	 * Remove the Entry with key k from the cache.
	 * 
	 * @param k
	 *            the key for the CacheEntry.
	 */
	public void remove(K k) throws CacheException {
		NCacheData<K, V> r;
		w.lock();
		try {
			if (k == null) {
				// Odd, but seems to happen. Probably removed
				// by someone else before enumeration gets to it.
				return;
			}
			FiledKey<K> fk = new MemoryKey<K>(k);
			r = htab.get(fk);
			if (r != null) {
				// remove entries while it is still in htab.
				vec.remove(r);
				currentSize -= (r.getSize() + r.getKeySize() + r.getHookSize());
				htab.remove(fk);
			}
		} finally {
			w.unlock();
		}

		if (r != null) {
			// this removes the key => htab.remove can not work..
			File entryName = getEntryName(r.getId(), true, null);
			try {
				removeHook(entryName, ".hook");
				removeHook(entryName, ".key");
				r.setDataHook(null, 0);
				File cfile = entryName;
				if (cfile.exists()) {
					File p = cfile.getParentFile();
					FileHelper.delete(cfile);
					// Until NT does rename in a nice manner check for tempdir.
					synchronized (dirLock) {
						if (p.exists() && !p.equals(tempdir)) {
							String ls[] = p.list();
							if (ls != null && ls.length == 0)
								FileHelper.delete(p);
						}
					}
				}
			} catch (IOException e) {
				throw new CacheException("Failed to remove file, key: " + k, e);
			}
		}
	}

	/**
	 * Clear the Cache from files.
	 */
	public void clear() throws CacheException {
		ArrayList<FiledKey<K>> ls;
		w.lock();
		try {
			ls = new ArrayList<FiledKey<K>>(htab.keySet());
			for (FiledKey<K> k : ls) {
				try {
					remove(k.getData());
				} catch (IOException e) {
					throw new CacheException("Failed to remove entry, key: "
							+ k, e);
				}
			}
			vec.clear(); // just to be safe.
			currentSize = 0;
			changed = true;
		} finally {
			w.unlock();
		}
	}

	/**
	 * Get the CacheEntries in the cache. Note! some entries may be invalid if
	 * you have a corruct cache.
	 * 
	 * @return a Collection of the CacheEntries.
	 */
	public Iterable<NCacheEntry<K, V>> getEntries() {
		// Defensive copy so that nothing happen when the user iterates
		r.lock();
		try {
			return new NCacheIterator(htab.values());
		} finally {
			r.unlock();
		}
	}

	private class NCacheIterator implements Iterable<NCacheEntry<K, V>>,
			Iterator<NCacheEntry<K, V>> {
		private Iterator<NCacheData<K, V>> dataIterator;

		public NCacheIterator(Collection<NCacheData<K, V>> c) {
			dataIterator = new ArrayList<NCacheData<K, V>>(c).iterator();
		}

		public Iterator<NCacheEntry<K, V>> iterator() {
			return this;
		}

		public NCacheEntry<K, V> next() {
			try {
				return getEntry(dataIterator.next());
			} catch (CacheException e) {
				throw new RuntimeException("Failed to get entry", e);
			}
		}

		public boolean hasNext() {
			return dataIterator.hasNext();
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Read the info from an old cache.
	 */
	private void readCacheIndex() {
		try {
			File index = new File(dir, CACHEINDEX);
			if (index.exists())
				readCacheIndex(index);
			else
				logger.info("No cache index found: " + index
						+ ", treating as empty cache");
		} catch (IOException e) {
			logWarning("Couldnt read " + dir + File.separator + CACHEINDEX
					+ ". This is bad (but not serius).\nTreating as empty. ", e);
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE, "Couldn't find classes", e);
		}
	}

	@SuppressWarnings("unchecked")
	private void readCacheIndex(File index) throws IOException,
			ClassNotFoundException {
		long fileNo;
		long currentSize;
		FileInputStream fis = new FileInputStream(index);
		ObjectInputStream is = new ObjectInputStream(new GZIPInputStream(fis));
		fileNo = is.readLong();
		currentSize = is.readLong();
		int size = is.readInt();
		Map<FiledKey<K>, NCacheData<K, V>> htab = new HashMap<FiledKey<K>, NCacheData<K, V>>(
				(int) (size * 1.2));
		for (int i = 0; i < size; i++) {
			FiledKey<K> fk = (FiledKey<K>) is.readObject();
			fk.setCache(this);
			NCacheData<K, V> entry = (NCacheData<K, V>) is.readObject();
			htab.put(fk, entry);
		}
		List<NCacheData<K, V>> vec = (List<NCacheData<K, V>>) is.readObject();
		is.close();

		// Only set internal state if we managed to get it all.
		this.fileNo = fileNo;
		this.currentSize = currentSize;
		this.htab = htab;
		this.vec = vec;

	}

	/**
	 * Make sure that the cache is written to the disk.
	 */
	public void flush() {
		writeCacheIndex();
	}

	/**
	 * Store the cache to disk so we can reuse it later.
	 */
	private void writeCacheIndex() {
		try {
			String name = dir + File.separator + CACHEINDEX;

			FileOutputStream fos = new FileOutputStream(name);
			ObjectOutputStream os = new ObjectOutputStream(
					new GZIPOutputStream(fos));

			r.lock();
			try {
				os.writeLong(fileNo);
				os.writeLong(currentSize);
				os.writeInt(htab.size());
				for (Map.Entry<FiledKey<K>, NCacheData<K, V>> me : htab
						.entrySet()) {
					os.writeObject(me.getKey());
					os.writeObject(me.getValue());
				}
				os.writeObject(vec);
			} finally {
				r.unlock();
			}
			os.close();
		} catch (IOException e) {
			logWarning("Couldnt write " + dir + File.separator + CACHEINDEX
					+ ", This is serious!\n", e);
		}
	}

	/**
	 * Loop in a cleaning loop.
	 */
	public void run() {
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		while (running) {
			try {
				Thread.sleep(cleanLoopTime);
			} catch (InterruptedException e) {
				// System.err.println ("Cache interrupted");
			}
			if (!running)
				continue;

			// actually for a busy cache this will lag...
			// but I dont care for now...
			long milis = System.currentTimeMillis();
			Map<FiledKey<K>, NCacheData<K, V>> hc;
			r.lock();
			try {
				hc = new HashMap<FiledKey<K>, NCacheData<K, V>>(htab);
			} finally {
				r.unlock();
			}
			for (Map.Entry<FiledKey<K>, NCacheData<K, V>> ce : hc.entrySet()) {
				try {
					long exp = ce.getValue().getExpires();
					if (exp < milis)
						removeKey(ce.getKey());
				} catch (IOException e) {
					logWarning("Failed to remove expired entry", e);
				} catch (CacheException e) {
					logWarning("Failed to remove expired entry", e);
				}
			}

			// IF SIZE IS TO BIG REMOVE A RANDOM AMOUNT OF OBJECTS.
			// What we have to be careful about: we must not remove the same
			// elements two times in a row, this method remove the "oldest" in
			// a sense.

			long maxSize = configuration.getMaxSize();
			if (getCurrentSize() > maxSize)
				changed = true;
			while (getCurrentSize() > maxSize) {
				w.lock();
				try {
					removeKey(vec.get(0).getKey());
				} catch (IOException e) {
					logWarning("Failed to remove entry", e);
				} catch (CacheException e) {
					logWarning("Failed to remove entry", e);
				} finally {
					w.unlock();
				}
			}

			if (changed) {
				writeCacheIndex();
				changed = false;
			}
		}
	}

	private void removeKey(FiledKey<K> fk) throws IOException, CacheException {
		fk.setCache(this);
		remove(fk.getData());
	}

	public void stop() {
		running = false;
		if (cleaner != null) {
			try {
				cleaner.interrupt();
				cleaner.join();
			} catch (InterruptedException e) {
				// ignore
			}
		}
	}

	/**
	 * Configure the cache system from the given config.
	 * 
	 * @param config
	 *            the properties describing the cache settings.
	 * @throws IOException
	 *             if the new cache can not be configured correctly
	 */
	public void setup(SProperties config) throws IOException {
		if (config == null)
			config = new SProperties();
		String cachedir = config.getProperty("directory", DIR);
		configuration.setCacheDir(cachedir);
		configuration.setup(logger, config);
		String ct = config.getProperty("cleanloop", DEFAULT_CLEAN_LOOP);
		try {
			setCleanLoopTime(Integer.parseInt(ct) * 1000); // in seconds.
		} catch (NumberFormatException e) {
			logger.warning("Bad number for cache cleanloop: '" + ct + "'");
		}
	}

	public Logger getLogger() {
		return logger;
	}

	private NCacheEntry<K, V> getEntry(NCacheData<K, V> data)
			throws CacheException {
		if (data == null)
			return null;
		try {
			FiledKey<K> key = data.getKey();
			key.setCache(this);
			K keyData = key.getData();
			V hook = data.getDataHook().getData(this, data, getLogger());
			return new NCacheEntry<K, V>(data.getId(), data.getCacheTime(),
					data.getExpires(), data.getSize(), keyData, hook);
		} catch (IOException e) {
			throw new CacheException("Failed to get: entry: " + data, e);
		}
	}

	private NCacheData<K, V> getData(NCacheEntry<K, V> entry, File cacheFile)
			throws CacheException {
		long id = entry.getId();
		long size = cacheFile.length();
		try {
			FiledWithSize<FiledKey<K>> fkws = storeKey(entry.getKey(), id);
			FiledWithSize<FiledHook<V>> fhws = storeHook(entry.getDataHook(),
					id);
			return new NCacheData<K, V>(id, entry.getCacheTime(),
					entry.getExpires(), size, fkws.t, fkws.size, fhws.t,
					fhws.size);
		} catch (IOException e) {
			// TODO: do we need to clean anything up?
			throw new CacheException("Failed to store data", e);
		}
	}

	private FiledWithSize<FiledKey<K>> storeKey(K realKey, long id)
			throws IOException {
		FiledKey<K> fk = new FiledKey<K>();
		long size = fk.storeKey(this, id, realKey, logger);
		return new FiledWithSize<FiledKey<K>>(fk, size);
	}

	private FiledWithSize<FiledHook<V>> storeHook(V hook, long id)
			throws IOException {
		if (hook == null)
			return null;
		FiledHook<V> fh = new FiledHook<V>();
		long size = fh.storeHook(this, id, getHookFileHandler(), hook, logger);
		return new FiledWithSize<FiledHook<V>>(fh, size);
	}

	private static class FiledWithSize<T> {
		private final T t;
		private final long size;

		public FiledWithSize(T t, long size) {
			this.t = t;
			this.size = size;
		}
	}

	private void logWarning(String s, Exception e) {
		logger.log(Level.WARNING, s, e);
	}
}

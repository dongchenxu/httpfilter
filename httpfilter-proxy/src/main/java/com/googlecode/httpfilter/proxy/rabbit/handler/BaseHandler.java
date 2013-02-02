package com.googlecode.httpfilter.proxy.rabbit.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.googlecode.httpfilter.proxy.rabbit.cache.Cache;
import com.googlecode.httpfilter.proxy.rabbit.cache.CacheEntry;
import com.googlecode.httpfilter.proxy.rabbit.cache.CacheException;
import com.googlecode.httpfilter.proxy.rabbit.http.ContentRangeParser;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpDateParser;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.httpio.BlockListener;
import com.googlecode.httpfilter.proxy.rabbit.httpio.BlockSender;
import com.googlecode.httpfilter.proxy.rabbit.httpio.BlockSentListener;
import com.googlecode.httpfilter.proxy.rabbit.httpio.ChunkEnder;
import com.googlecode.httpfilter.proxy.rabbit.httpio.HttpHeaderSender;
import com.googlecode.httpfilter.proxy.rabbit.httpio.HttpHeaderSentListener;
import com.googlecode.httpfilter.proxy.rabbit.httpio.ResourceSource;
import com.googlecode.httpfilter.proxy.rabbit.httpio.TransferHandler;
import com.googlecode.httpfilter.proxy.rabbit.httpio.TransferListener;
import com.googlecode.httpfilter.proxy.rabbit.io.BufferHandle;
import com.googlecode.httpfilter.proxy.rabbit.io.FileHelper;
import com.googlecode.httpfilter.proxy.rabbit.proxy.Connection;
import com.googlecode.httpfilter.proxy.rabbit.proxy.HttpProxy;
import com.googlecode.httpfilter.proxy.rabbit.proxy.PartialCacher;
import com.googlecode.httpfilter.proxy.rabbit.proxy.TrafficLoggerHandler;
import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;

/**
 * This class is an implementation of the Handler interface. This handler does
 * no filtering, it only sends the data as effective as it can.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class BaseHandler implements Handler, HandlerFactory,
		HttpHeaderSentListener, BlockListener, BlockSentListener {
	/** The Connection handling the request. */
	protected Connection con;
	/** The traffic logger handler. */
	protected TrafficLoggerHandler tlh;
	/** The actual request made. */
	protected HttpHeader request;
	/** The actual response. */
	protected HttpHeader response;
	/** The resource */
	protected ResourceSource content;

	/** The cache entry if available. */
	protected CacheEntry<HttpHeader, HttpHeader> entry = null;
	/** The cache channel. */
	protected WritableByteChannel cacheChannel;

	/** May we cache this request. */
	protected boolean mayCache;
	/** May we filter this request */
	protected boolean mayFilter;
	/** The length of the data beeing handled or -1 if unknown. */
	protected long size = -1;
	/** The total amount of data that we read. */
	protected long totalRead = 0;

	/** The flag for the last empty chunk */
	private boolean emptyChunkSent = false;

	private final Logger logger = Logger.getLogger(getClass().getName());

	/**
	 * For creating the factory.
	 */
	public BaseHandler() {
		// empty
	}

	/**
	 * Create a new BaseHandler for the given request.
	 * 
	 * @param con
	 *            the Connection handling the request.
	 * @param tlh
	 *            the TrafficLoggerHandler to update with traffic information
	 * @param request
	 *            the actual request made.
	 * @param response
	 *            the actual response.
	 * @param content
	 *            the resource.
	 * @param mayCache
	 *            May we cache this request?
	 * @param mayFilter
	 *            May we filter this request?
	 * @param size
	 *            the size of the data beeing handled.
	 */
	public BaseHandler(Connection con, TrafficLoggerHandler tlh,
			HttpHeader request, HttpHeader response, ResourceSource content,
			boolean mayCache, boolean mayFilter, long size) {
		this.con = con;
		this.tlh = tlh;
		this.request = request;
		this.response = response;
		if (!request.isDot9Request() && response == null)
			throw new IllegalArgumentException("response may not be null");
		this.content = content;
		this.mayCache = mayCache;
		this.mayFilter = mayFilter;
		this.size = size;
	}

	public Handler getNewInstance(Connection con, TrafficLoggerHandler tlh,
			HttpHeader header, HttpHeader webHeader, ResourceSource content,
			boolean mayCache, boolean mayFilter, long size) {
		return new BaseHandler(con, tlh, header, webHeader, content, mayCache,
				mayFilter, size);
	}

	protected Logger getLogger() {
		return logger;
	}

	/**
	 * Handle the request. A request is made in these steps: <xmp> sendHeader
	 * (); addCache (); prepare (); send (); finishData (); finish (); </xmp>
	 * Note that finish is always called, no matter what exceptions are thrown.
	 * The middle steps are most probably only performed if the previous steps
	 * have all succeded
	 */
	public void handle() {
		if (request.isDot9Request())
			send();
		else
			sendHeader();
	}

	/**
	 * ®return false if this handler never modifies the content.
	 */
	public boolean changesContentSize() {
		return false;
	}

	protected void sendHeader() {
		try {
			HttpHeaderSender hhs = new HttpHeaderSender(con.getChannel(),
					con.getNioHandler(), tlh.getClient(), response, false, this);
			hhs.sendHeader();
		} catch (IOException e) {
			failed(e);
		}
	}

	public void httpHeaderSent() {
		addCache();
		prepare();
	}

	/**
	 * This method is used to prepare the data for the resource being sent. This
	 * method does nothing here.
	 */
	protected void prepare() {
		send();
	}

	/**
	 * This method is used to finish the data for the resource being sent. This
	 * method will send an end chunk if needed and then call finish
	 */
	protected void finishData() {
		if (con.getChunking() && !emptyChunkSent) {
			emptyChunkSent = true;
			BlockSentListener bsl = new Finisher();
			ChunkEnder ce = new ChunkEnder();
			ce.sendChunkEnding(con.getChannel(), con.getNioHandler(),
					tlh.getClient(), bsl);
		} else {
			finish(true);
		}
	}

	private void removePrivateParts(HttpHeader header, String type) {
		for (String val : header.getHeaders("Cache-Control")) {
			int j = val.indexOf(type);
			if (j >= 0) {
				String p = val.substring(j + type.length());
				StringTokenizer st = new StringTokenizer(p, ",\"");
				while (st.hasMoreTokens()) {
					String t = st.nextToken();
					header.removeHeader(t);
				}
			}
		}
	}

	private void removePrivateParts(HttpHeader header) {
		removePrivateParts(header, "private=");
		removePrivateParts(header, "no-cache=");
	}

	/**
	 * Mark the current response as a partial response.
	 * 
	 * @param shouldbe
	 *            the number of byte that the resource ought to be
	 */
	protected void setPartialContent(long shouldbe) {
		response.setHeader("RabbIT-Partial", "" + shouldbe);
	}

	/**
	 * Close nesseccary channels and adjust the cached files. If you override
	 * this one, remember to call super.finish ()!
	 * 
	 * @param good
	 *            if true then the connection may be restarted, if false then
	 *            the connection may not be restared
	 */
	protected void finish(boolean good) {
		boolean ok = false;
		try {
			if (content != null)
				content.release();
			if (cacheChannel != null) {
				try {
					cacheChannel.close();
				} catch (IOException e) {
					failed(e);
				}
			}

			finishCache();
			if (response != null
					&& response.getHeader("Content-Length") != null)
				con.setContentLength(response.getHeader("Content-length"));

			ok = true;
		} finally {
			// and clean up...
			request = null;
			response = null;
			content = null;
			entry = null;
			cacheChannel = null;
		}
		// Not sure why we need this, seems to call finish multiple times.
		if (con != null) {
			if (good && ok)
				con.logAndTryRestart();
			else
				con.logAndClose();
		}
		tlh = null;
		con = null;
	}

	private void finishCache() {
		if (entry == null || !mayCache)
			return;
		Cache<HttpHeader, HttpHeader> cache = con.getProxy().getCache();
		File entryName = cache.getEntryName(entry.getId(), false, null);
		long filesize = entryName.length();
		String cl = response.getHeader("Content-Length");
		if (cl == null) {
			response.removeHeader("Transfer-Encoding");
			response.setHeader("Content-Length", "" + filesize);
		}
		removePrivateParts(response);
		try {
			cache.addEntry(entry);
		} catch (CacheException e) {
			getLogger().log(Level.WARNING,
					"Failed to add cache entry: " + request.getRequestURI(), e);
		}
	}

	/**
	 * Try to use the resource size to decide if we may cache or not. If the
	 * size is known and the size is bigger than the maximum cache size, then we
	 * dont want to cache the resource.
	 * 
	 * @return true if the current resource may be cached, false otherwise
	 */
	protected boolean mayCacheFromSize() {
		Cache<HttpHeader, HttpHeader> cache = con.getProxy().getCache();
		long maxSize = cache.getCacheConfiguration().getMaxSize();
		return !(maxSize == 0 || (size > 0 && size > maxSize));
	}

	/**
	 * Check if this handler may force the cached resource to be less than the
	 * cache max size.
	 * 
	 * @return true
	 */
	protected boolean mayRestrictCacheSize() {
		return true;
	}

	/**
	 * Set the expire time on the cache entry. If the expire time is 0 then the
	 * cache is not written.
	 */
	private void setCacheExpiry() {
		String expires = response.getHeader("Expires");
		if (expires != null) {
			Date exp = HttpDateParser.getDate(expires);
			// common case, handle it...
			if (exp == null && expires.equals("0"))
				exp = new Date(0);
			if (exp != null) {
				long now = System.currentTimeMillis();
				if (now > exp.getTime()) {
					getLogger().config(
							"expire date in the past: '" + expires + "'");
					entry = null;
					return;
				}
				entry.setExpires(exp.getTime());
			} else {
				getLogger().config(
						"unable to parse expire date: '" + expires
								+ "' for URI: '" + request.getRequestURI()
								+ "'");
				entry = null;
			}
		}
	}

	private void updateRange(CacheEntry<HttpHeader, HttpHeader> old,
			PartialCacher pc, Cache<HttpHeader, HttpHeader> cache)
			throws CacheException {
		HttpHeader oldRequest = old.getKey();
		HttpHeader oldResponse = old.getDataHook();
		String cr = oldResponse.getHeader("Content-Range");
		if (cr == null) {
			String cl = oldResponse.getHeader("Content-Length");
			if (cl != null) {
				long size = Long.parseLong(cl);
				cr = "bytes 0-" + (size - 1) + "/" + size;
			}
		}
		ContentRangeParser crp = new ContentRangeParser(cr);
		if (crp.isValid()) {
			long start = crp.getStart();
			long end = crp.getEnd();
			long total = crp.getTotal();
			String t = total < 0 ? "*" : Long.toString(total);
			if (end == pc.getStart() - 1) {
				oldRequest.setHeader("Range", "bytes=" + start + "-" + end);
				oldResponse.setHeader("Content-Range", "bytes " + start + "-"
						+ pc.getEnd() + "/" + t);
			} else {
				oldRequest.addHeader("Range", "bytes=" + start + "-" + end);
				oldResponse.addHeader("Content-Range", "bytes " + start + "-"
						+ pc.getEnd() + "/" + t);
			}
			cache.entryChanged(old, oldRequest, oldResponse);
		}
	}

	private void setupPartial(CacheEntry<HttpHeader, HttpHeader> oldEntry,
			CacheEntry<HttpHeader, HttpHeader> entry, File entryName,
			Cache<HttpHeader, HttpHeader> cache) throws IOException {
		if (oldEntry != null) {
			File oldName = cache.getEntryName(oldEntry.getId(), true, null);
			PartialCacher pc = new PartialCacher(oldName, response);
			cacheChannel = pc.getChannel();
			try {
				updateRange(oldEntry, pc, cache);
			} catch (CacheException e) {
				getLogger()
						.log(Level.WARNING,
								"Failed to update range: "
										+ request.getRequestURI(), e);
			}
			return;
		}
		entry.setDataHook(response);
		PartialCacher pc = new PartialCacher(entryName, response);
		cacheChannel = pc.getChannel();
	}

	/**
	 * Set up the cache stream if available.
	 */
	protected void addCache() {
		if (mayCache && mayCacheFromSize()) {
			Cache<HttpHeader, HttpHeader> cache = con.getProxy().getCache();
			try {
				entry = cache.newEntry(request);
			} catch (CacheException e) {
				getLogger().log(
						Level.WARNING,
						"Failed to create new entry for: " + request
								+ ", will not cache", e);
			}
			setCacheExpiry();
			if (entry == null) {
				getLogger().config("Expiry =< 0 set on entry, will not cache");
				return;
			}
			File entryName = cache.getEntryName(entry.getId(), false, null);
			if (response.getStatusCode().equals("206")) {
				CacheEntry<HttpHeader, HttpHeader> oldEntry = null;
				try {
					oldEntry = cache.getEntry(request);
				} catch (CacheException e) {
					getLogger().log(
							Level.WARNING,
							"Failed to get old entry: "
									+ request.getRequestURI(), e);
				}
				try {
					setupPartial(oldEntry, entry, entryName, cache);
				} catch (IOException e) {
					getLogger().log(Level.WARNING,
							"Got IOException, not updating cache", e);
					entry = null;
					cacheChannel = null;
				}
			} else {
				entry.setDataHook(response);
				try {
					FileOutputStream cacheStream = new FileOutputStream(
							entryName);
					/*
					 * TODO: implement this: if (mayRestrictCacheSize ())
					 * cacheStream = new MaxSizeOutputStream (cacheStream,
					 * cache.getMaxSize ());
					 */
					cacheChannel = cacheStream.getChannel();
				} catch (IOException e) {
					getLogger().log(Level.WARNING,
							"Got IOException, not caching", e);
					entry = null;
					cacheChannel = null;
				}
			}
		}
	}

	/**
	 * Check if this handler supports direct transfers.
	 * 
	 * @return this handler always return true.
	 */
	protected boolean mayTransfer() {
		return true;
	}

	protected void send() {
		if (mayTransfer() && content.length() > 0 && content.supportsTransfer()) {
			TransferListener tl = new ContentTransferListener();
			TransferHandler th = new TransferHandler(con.getNioHandler(),
					content, con.getChannel(), tlh.getCache(), tlh.getClient(),
					tl);
			th.transfer();
		} else {
			content.addBlockListener(this);
		}
	}

	private class ContentTransferListener implements TransferListener {
		public void transferOk() {
			finishData();
		}

		public void failed(Exception cause) {
			BaseHandler.this.failed(cause);
		}
	}

	protected void writeCache(ByteBuffer buf) throws IOException {
		// TODO: another thread?
		int currentPosition = buf.position();
		while (buf.hasRemaining())
			cacheChannel.write(buf);
		buf.position(currentPosition);
		tlh.getCache().write(buf.remaining());
	}

	public void bufferRead(BufferHandle bufHandle) {
		if (con == null) {
			// not sure why this can happen, client has closed connection.
			return;
		}
		try {
			// TODO: do this in another thread?
			ByteBuffer buffer = bufHandle.getBuffer();
			if (cacheChannel != null)
				writeCache(buffer);
			totalRead += buffer.remaining();
			BlockSender bs = new BlockSender(con.getChannel(),
					con.getNioHandler(), tlh.getClient(), bufHandle,
					con.getChunking(), this);
			bs.write();
		} catch (IOException e) {
			failed(e);
		}
	}

	public void blockSent() {
		content.addBlockListener(BaseHandler.this);
	}

	public void finishedRead() {
		if (size > 0 && totalRead != size)
			setPartialContent(size);
		finishData();
	}

	private class Finisher implements BlockSentListener {
		public void blockSent() {
			finish(true);
		}

		public void failed(Exception cause) {
			BaseHandler.this.failed(cause);
		}

		public void timeout() {
			BaseHandler.this.timeout();
		}
	}

	String getStackTrace(Exception cause) {
		StringWriter sw = new StringWriter();
		PrintWriter ps = new PrintWriter(sw);
		cause.printStackTrace(ps);
		return sw.toString();
	}

	protected void deleteFile(File f) {
		try {
			FileHelper.delete(f);
		} catch (IOException e) {
			getLogger().log(Level.WARNING, "Failed to delete file", e);
		}
	}

	protected void removeCache() {
		if (cacheChannel != null) {
			try {
				cacheChannel.close();
				Cache<HttpHeader, HttpHeader> cache = con.getProxy().getCache();
				File entryName = cache.getEntryName(entry.getId(), false, null);
				deleteFile(entryName);
				entry = null;
			} catch (IOException e) {
				getLogger().log(Level.WARNING,
						"failed to remove cache entry: ", e);
			} finally {
				cacheChannel = null;
			}
		}
	}

	public void failed(Exception cause) {
		if (con != null) {
			String st;
			if (cause instanceof IOException) {
				IOException ioe = (IOException) cause;
				String msg = ioe.getMessage();
				if ("Broken pipe".equals(msg))
					st = ioe.toString() + ", probably cancelled pipeline";
				else if ("Connection reset by peer".equals(msg))
					st = ioe.toString() + ", client aborted connection";
				else
					st = getStackTrace(cause);
			} else {
				st = getStackTrace(cause);
			}
			getLogger().warning(
					"BaseHandler: error handling request: "
							+ request.getRequestURI() + ": " + st);
			con.setStatusCode("500");
			String ei = con.getExtraInfo();
			ei = ei == null ? cause.toString() : (ei + ", " + cause);
			con.setExtraInfo(ei);
		}
		removeCache();
		finish(false);
	}

	public void timeout() {
		if (con != null)
			getLogger().warning(
					"BaseHandler: timeout: uri: " + request.getRequestURI());
		removeCache();
		finish(false);
	}

	public void setup(SProperties properties, HttpProxy proxy) {
		// nothing to do.
	}
}

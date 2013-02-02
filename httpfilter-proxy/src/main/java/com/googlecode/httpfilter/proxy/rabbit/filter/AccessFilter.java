package com.googlecode.httpfilter.proxy.rabbit.filter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl.Closer;
import com.googlecode.httpfilter.proxy.rabbit.util.IPAccess;
import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;

/**
 * This is a class that filters access based on ip address.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class AccessFilter implements IPAccessFilter {
	private List<IPAccess> allowed = new ArrayList<IPAccess>();
	private List<IPAccess> denied = new ArrayList<IPAccess>();
	private static final String DEFAULTCONFIG = "conf/access";

	private final Logger logger = Logger.getLogger(getClass().getName());

	/**
	 * Filter based on a socket.
	 * 
	 * @param s
	 *            the SocketChannel to check.
	 * @return true if the Socket should be allowed, false otherwise.
	 */
	public boolean doIPFiltering(SocketChannel s) {
		int l = denied.size();
		for (int i = 0; i < l; i++)
			if (denied.get(i).inrange(s.socket().getInetAddress()))
				return false;

		l = allowed.size();
		for (int i = 0; i < l; i++)
			if (allowed.get(i).inrange(s.socket().getInetAddress()))
				return true;
		return false;
	}

	/**
	 * Setup this class.
	 * 
	 * @param properties
	 *            the Properties to get the settings from.
	 */
	public void setup(SProperties properties) {
		String file = properties.getProperty("accessfile", DEFAULTCONFIG);
		loadAccess(file);
	}

	/**
	 * Read the data (accesslists) from a file.
	 * 
	 * @param filename
	 *            the name of the file to read from.
	 */
	private void loadAccess(String filename) {
		filename = filename.replace('/', File.separatorChar);

		FileInputStream is = null;
		try {
			is = new FileInputStream(filename);
			Reader r = new InputStreamReader(is, "UTF-8");
			try {
				loadAccess(r);
			} finally {
				Closer.close(r, logger);
			}
		} catch (IOException e) {
			logger.log(Level.WARNING, "Accessfile '" + filename
					+ "' not found: no one allowed", e);
		} finally {
			Closer.close(is, logger);
		}
	}

	/**
	 * Loads in the accessess allowed from the given Reader
	 * 
	 * @param r
	 *            the Reader were data is available
	 */
	public void loadAccess(Reader r) throws IOException {
		List<IPAccess> allowed = new ArrayList<IPAccess>();
		List<IPAccess> denied = new ArrayList<IPAccess>();
		LineNumberReader br = new LineNumberReader(r);
		String line;
		while ((line = br.readLine()) != null) {
			// remove comments....
			int index = line.indexOf('#');
			if (index >= 0)
				line = line.substring(0, index);
			line = line.trim();
			if (line.equals(""))
				continue;
			boolean accept = true;
			if (line.charAt(0) == '-') {
				accept = false;
				line = line.substring(1);
			}
			StringTokenizer st = new StringTokenizer(line);
			if (st.countTokens() != 2) {
				logger.warning("Bad line in accessconf:" + br.getLineNumber());
				continue;
			}
			String low = st.nextToken();
			InetAddress lowip = getInetAddress(low, logger, br);
			String high = st.nextToken();
			InetAddress highip = getInetAddress(high, logger, br);

			if (lowip != null && highip != null) {
				if (accept)
					allowed.add(new IPAccess(lowip, highip));
				else
					denied.add(new IPAccess(lowip, highip));
			}
		}
		br.close();
		this.allowed = allowed;
		this.denied = denied;
	}

	private InetAddress getInetAddress(String text, Logger logger,
			LineNumberReader br) {
		InetAddress ip = null;
		try {
			ip = InetAddress.getByName(text);
		} catch (UnknownHostException e) {
			logger.warning("Bad host: " + text + " at line:"
					+ br.getLineNumber());
		}
		return ip;
	}

	/**
	 * Get the list of allowed ips
	 */
	public List<IPAccess> getAllowList() {
		return allowed;
	}

	/**
	 * Get the list of denied ips
	 */
	public List<IPAccess> getDenyList() {
		return denied;
	}
}

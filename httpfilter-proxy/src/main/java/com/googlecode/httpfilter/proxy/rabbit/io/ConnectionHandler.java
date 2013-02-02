package com.googlecode.httpfilter.proxy.rabbit.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.NioHandler;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.ReadHandler;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.util.Counter;
import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;

/**
 * A class to handle the connections to the net. Tries to reuse connections
 * whenever possible.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class ConnectionHandler {
	// The logger to use
	private final Logger logger = Logger.getLogger(getClass().getName());

	// The counter to use.
	private final Counter counter;

	// The resolver to use
	private final ProxyChain proxyChain;

	// The available connections.
	private final Map<Address, List<WebConnection>> activeConnections;

	// The channels waiting for closing
	private final Map<WebConnection, CloseListener> wc2closer;

	// the keepalivetime.
	private long keepaliveTime = 1000;

	// should we use pipelining...
	private boolean usePipelining = true;

	// the nio handler
	private final NioHandler nioHandler;

	// the socket binder
	private SocketBinder socketBinder = new DefaultBinder();

	/**
	 * Create a new ConnectionHandler.
	 * 
	 * @param counter
	 *            the Counter to update with statistics
	 * @param proxyChain
	 *            the ProxyChain to use when doing dns lookups
	 * @param nioHandler
	 *            the NioHandler to use for network and background tasks
	 */
	public ConnectionHandler(Counter counter, ProxyChain proxyChain,
			NioHandler nioHandler) {
		this.counter = counter;
		this.proxyChain = proxyChain;
		this.nioHandler = nioHandler;

		activeConnections = new HashMap<Address, List<WebConnection>>();
		wc2closer = new ConcurrentHashMap<WebConnection, CloseListener>();
	}

	/**
	 * Set the keep alive time for this handler.
	 * 
	 * @param milis
	 *            the keep alive time in miliseconds.
	 */
	public void setKeepaliveTime(long milis) {
		keepaliveTime = milis;
	}

	/**
	 * Get the current keep alive time.
	 * 
	 * @return the keep alive time in miliseconds.
	 */
	public long getKeepaliveTime() {
		return keepaliveTime;
	}

	/**
	 * Get a copy of the current connections.
	 * 
	 * @return the current connections
	 */
	public Map<Address, List<WebConnection>> getActiveConnections() {
		Map<Address, List<WebConnection>> ret = new HashMap<Address, List<WebConnection>>();
		synchronized (activeConnections) {
			for (Map.Entry<Address, List<WebConnection>> me : activeConnections
					.entrySet()) {
				ret.put(me.getKey(),
						Collections.unmodifiableList(me.getValue()));
			}
		}
		return ret;
	}

	/**
	 * Get a WebConnection for the given header.
	 * 
	 * @param header
	 *            the HttpHeader containing the URL to connect to.
	 * @param wcl
	 *            the Listener that wants the connection.
	 */
	public void getConnection(final HttpHeader header,
			final WebConnectionListener wcl) {
		// TODO: should we use the Host: header if its available? probably...
		String requri = header.getRequestURI();
		URL url;
		try {
			url = new URL(requri);
		} catch (MalformedURLException e) {
			wcl.failed(e);
			return;
		}
		Resolver resolver = proxyChain.getResolver(requri);
		int port = url.getPort() > 0 ? url.getPort() : 80;
		final int rport = resolver.getConnectPort(port);

		resolver.getInetAddress(url, new InetAddressListener() {
			public void lookupDone(InetAddress ia) {
				Address a = new Address(ia, rport);
				getConnection(header, wcl, a);
			}

			public void unknownHost(Exception e) {
				wcl.failed(e);
			}
		});
	}

	private SocketBinder getSocketBinder() {
		return socketBinder;
	}

	private void getConnection(HttpHeader header, WebConnectionListener wcl,
			Address a) {
		WebConnection wc;
		counter.inc("WebConnections used");
		String method = header.getMethod();

		if (method != null) {
			// since we should not retry POST (and other) we
			// have to get a fresh connection for them..
			method = method.trim();
			if (!(method.equals("GET") || method.equals("HEAD"))) {
				wc = new WebConnection(a, getSocketBinder(), counter);
			} else {
				wc = getPooledConnection(a, activeConnections);
				if (wc == null)
					wc = new WebConnection(a, getSocketBinder(), counter);
			}
			try {
				wc.connect(nioHandler, wcl);
			} catch (IOException e) {
				wcl.failed(e);
			}
		} else {
			String err = "No method specified: " + header;
			wcl.failed(new IllegalArgumentException(err));
		}
	}

	private WebConnection getPooledConnection(Address a,
			Map<Address, List<WebConnection>> conns) {
		synchronized (conns) {
			List<WebConnection> pool = conns.get(a);
			if (pool != null) {
				if (pool.size() > 0) {
					WebConnection wc = pool.remove(pool.size() - 1);
					if (pool.isEmpty())
						conns.remove(a);
					return unregister(wc);
				}
			}
		}
		return null;
	}

	private WebConnection unregister(WebConnection wc) {
		CloseListener closer;
		closer = wc2closer.remove(wc);
		if (closer != null)
			nioHandler.cancel(wc.getChannel(), closer);
		return wc;
	}

	private void removeFromPool(WebConnection wc,
			Map<Address, List<WebConnection>> conns) {
		synchronized (conns) {
			List<WebConnection> pool = conns.get(wc.getAddress());
			if (pool != null) {
				pool.remove(wc);
				if (pool.isEmpty())
					conns.remove(wc.getAddress());
			}
		}
	}

	/**
	 * Return a WebConnection to the pool so that it may be reused.
	 * 
	 * @param wc
	 *            the WebConnection to return.
	 */
	public void releaseConnection(WebConnection wc) {
		counter.inc("WebConnections released");
		if (!wc.getChannel().isOpen()) {
			return;
		}

		Address a = wc.getAddress();
		if (!wc.getKeepalive()) {
			closeWebConnection(wc);
			return;
		}

		synchronized (wc) {
			wc.setReleased();
		}
		synchronized (activeConnections) {
			List<WebConnection> pool = activeConnections.get(a);
			if (pool == null) {
				pool = new ArrayList<WebConnection>();
				activeConnections.put(a, pool);
			} else {
				if (pool.contains(wc)) {
					String err = "web connection already added to pool: " + wc;
					throw new IllegalStateException(err);
				}
			}
			pool.add(wc);
			CloseListener cl = new CloseListener(wc);
			wc2closer.put(wc, cl);
			cl.register();
		}
	}

	private void closeWebConnection(WebConnection wc) {
		if (wc == null)
			return;
		if (!wc.getChannel().isOpen())
			return;
		try {
			wc.close();
		} catch (IOException e) {
			logger.warning("Failed to close WebConnection: " + wc);
		}
	}

	private class CloseListener implements ReadHandler {
		private final WebConnection wc;
		private Long timeout;

		public CloseListener(WebConnection wc) {
			this.wc = wc;
		}

		public void register() {
			timeout = nioHandler.getDefaultTimeout();
			nioHandler.waitForRead(wc.getChannel(), this);
		}

		public void read() {
			closeChannel();
		}

		public void closed() {
			closeChannel();
		}

		public void timeout() {
			closeChannel();
		}

		public Long getTimeout() {
			return timeout;
		}

		private void closeChannel() {
			try {
				wc2closer.remove(wc);
				removeFromPool(wc, activeConnections);
				wc.close();
			} catch (IOException e) {
				String err = "CloseListener: Failed to close web connection: "
						+ e;
				logger.warning(err);
			}
		}

		public boolean useSeparateThread() {
			return false;
		}

		public String getDescription() {
			return "ConnectionHandler$CloseListener: address: "
					+ wc.getAddress();
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + "{wc: " + wc + "}@"
					+ Integer.toString(hashCode(), 16);
		}
	}

	/**
	 * Mark a WebConnection ready for pipelining.
	 * 
	 * @param wc
	 *            the WebConnection to mark ready for pipelining.
	 */
	public void markForPipelining(WebConnection wc) {
		if (!usePipelining)
			return;
		synchronized (wc) {
			if (wc.getKeepalive())
				wc.setMayPipeline(true);
		}
	}

	/**
	 * Configure this ConnectionHandler using the given properties.
	 * 
	 * @param config
	 *            the properties to read the configuration from
	 */
	public void setup(SProperties config) {
		if (config == null)
			return;
		String kat = config.getProperty("keepalivetime", "1000");
		try {
			setKeepaliveTime(Long.parseLong(kat));
		} catch (NumberFormatException e) {
			String err = "Bad number for ConnectionHandler keepalivetime: '"
					+ kat + "'";
			logger.warning(err);
		}
		String up = config.get("usepipelining");
		if (up == null)
			up = "true";
		usePipelining = up.equalsIgnoreCase("true");

		String bindIP = config.getProperty("bind_ip");
		if (bindIP != null) {
			try {
				InetAddress ia = InetAddress.getByName(bindIP);
				if (ia != null) {
					logger.info("Will bind to: " + ia + " for outgoing traffic");
					socketBinder = new BoundBinder(ia);
				}
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Failed to find inet address for: "
						+ bindIP, e);
			}
		}
	}
}

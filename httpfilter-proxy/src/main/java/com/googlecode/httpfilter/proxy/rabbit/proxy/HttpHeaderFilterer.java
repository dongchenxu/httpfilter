package com.googlecode.httpfilter.proxy.rabbit.proxy;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.googlecode.httpfilter.proxy.rabbit.filter.HttpFilter;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.util.Config;

/**
 * A class to load and run the HttpFilters.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class HttpHeaderFilterer {
	private final List<HttpFilter> httpInFilters;
	private final List<HttpFilter> httpOutFilters;
	private final List<HttpFilter> connectFilters;

	public HttpHeaderFilterer(String in, String out, String connect,
			Config config, HttpProxy proxy) {
		httpInFilters = new ArrayList<HttpFilter>();
		loadHttpFilters(in, httpInFilters, config, proxy);

		httpOutFilters = new ArrayList<HttpFilter>();
		loadHttpFilters(out, httpOutFilters, config, proxy);

		connectFilters = new ArrayList<HttpFilter>();
		loadHttpFilters(connect, connectFilters, config, proxy);
	}

	private static interface FilterHandler {
		HttpHeader filter(HttpFilter hf, SocketChannel channel, HttpHeader in,
				Connection con);
	}

	private HttpHeader filter(Connection con, SocketChannel channel,
			HttpHeader in, List<HttpFilter> filters, FilterHandler fh) {
		for (int i = 0, s = filters.size(); i < s; i++) {
			HttpFilter hf = filters.get(i);
			HttpHeader badresponse = fh.filter(hf, channel, in, con);
			if (badresponse != null)
				return badresponse;
		}
		return null;
	}

	private static class InFilterer implements FilterHandler {
		public HttpHeader filter(HttpFilter hf, SocketChannel channel,
				HttpHeader in, Connection con) {
			return hf.doHttpInFiltering(channel, in, con);
		}
	}

	private static class OutFilterer implements FilterHandler {
		public HttpHeader filter(HttpFilter hf, SocketChannel channel,
				HttpHeader in, Connection con) {
			return hf.doHttpOutFiltering(channel, in, con);
		}
	}

	private static class ConnectFilterer implements FilterHandler {
		public HttpHeader filter(HttpFilter hf, SocketChannel channel,
				HttpHeader in, Connection con) {
			return hf.doConnectFiltering(channel, in, con);
		}
	}

	/**
	 * Runs all input filters on the given header.
	 * 
	 * @param con
	 *            the Connection handling the request
	 * @param channel
	 *            the SocketChannel for the client
	 * @param in
	 *            the request.
	 * @return null if all is ok, a HttpHeader if this request is blocked.
	 */
	public HttpHeader filterHttpIn(Connection con, SocketChannel channel,
			HttpHeader in) {
		return filter(con, channel, in, httpInFilters, new InFilterer());
	}

	/**
	 * Runs all output filters on the given header.
	 * 
	 * @param con
	 *            the Connection handling the request
	 * @param channel
	 *            the SocketChannel for the client
	 * @param in
	 *            the response.
	 * @return null if all is ok, a HttpHeader if this request is blocked.
	 */
	public HttpHeader filterHttpOut(Connection con, SocketChannel channel,
			HttpHeader in) {
		return filter(con, channel, in, httpOutFilters, new OutFilterer());
	}

	/**
	 * Runs all connect filters on the given header.
	 * 
	 * @param con
	 *            the Connection handling the request
	 * @param channel
	 *            the SocketChannel for the client
	 * @param in
	 *            the response.
	 * @return null if all is ok, a HttpHeader if this request is blocked.
	 */
	public HttpHeader filterConnect(Connection con, SocketChannel channel,
			HttpHeader in) {
		return filter(con, channel, in, connectFilters, new ConnectFilterer());
	}

	private void loadHttpFilters(String filters, List<HttpFilter> ls,
			Config config, HttpProxy proxy) {
		Logger log = Logger.getLogger(getClass().getName());
		String[] filterArray = filters.split(",");
		for (String className : filterArray) {
			className = className.trim();
			if (className.isEmpty())
				continue;
			try {
				className = className.trim();
				Class<? extends HttpFilter> cls = proxy.load3rdPartyClass(
						className, HttpFilter.class);
				HttpFilter hf = cls.newInstance();
				hf.setup(config.getProperties(className), proxy);
				ls.add(hf);
			} catch (ClassNotFoundException ex) {
				log.log(Level.WARNING, "Could not load http filter class: '"
						+ className + "'", ex);
			} catch (InstantiationException ex) {
				log.log(Level.WARNING, "Could not instansiate http filter: '"
						+ className + "'", ex);
			} catch (IllegalAccessException ex) {
				log.log(Level.WARNING, "Could not access http filter: '"
						+ className + "'", ex);
			}
		}
	}

	public List<HttpFilter> getHttpInFilters() {
		return Collections.unmodifiableList(httpInFilters);
	}

	public List<HttpFilter> getHttpOutFilters() {
		return Collections.unmodifiableList(httpOutFilters);
	}

	public List<HttpFilter> getConnectFilters() {
		return Collections.unmodifiableList(connectFilters);
	}
}

package com.googlecode.httpfilter.proxy.rabbit.filter;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import com.googlecode.httpfilter.proxy.rabbit.filter.authenticate.AuthUserInfo;
import com.googlecode.httpfilter.proxy.rabbit.filter.authenticate.Authenticator;
import com.googlecode.httpfilter.proxy.rabbit.filter.authenticate.PlainFileAuthenticator;
import com.googlecode.httpfilter.proxy.rabbit.filter.authenticate.SQLAuthenticator;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.proxy.Connection;
import com.googlecode.httpfilter.proxy.rabbit.proxy.HttpGenerator;
import com.googlecode.httpfilter.proxy.rabbit.proxy.HttpProxy;
import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;

/**
 * This is a filter that requires users to use proxy-authentication.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class ProxyAuth implements HttpFilter {
	private final Logger logger = Logger.getLogger(getClass().getName());
	private Authenticator authenticator;
	private int cacheTime;
	private boolean oneIpOnly;
	private Pattern noAuthPattern;

	/** Username to user info */
	private final Map<String, AuthUserInfo> cache = new ConcurrentHashMap<String, AuthUserInfo>();

	/**
	 * Check that the user has been authenticated..
	 * 
	 * @param socket
	 *            the SocketChannel that made the request.
	 * @param header
	 *            the actual request made.
	 * @param con
	 *            the Connection handling the request.
	 * @return null if everything is fine or a HttpHeader describing the error
	 *         (a 407).
	 */
	public HttpHeader doHttpInFiltering(SocketChannel socket,
			HttpHeader header, Connection con) {
		if (con.getMeta())
			return null;
		if (noAuthRequired(header))
			return null;
		String username = con.getUserName();
		String token = authenticator.getToken(header, con);
		if (username == null || token == null)
			return getError(header, con);
		SocketChannel channel = con.getChannel();
		AuthUserInfo ce = cache.get(username);
		if (hasValidCache(token, ce)) {
			if (oneIpOnly) {
				InetAddress ia = channel.socket().getInetAddress();
				if (!ce.correctSocketAddress(ia))
					return getError(header, con);
			}
			return null;
		}

		if (!authenticator.authenticate(username, token))
			return getError(header, con);
		if (cacheTime > 0)
			storeInCache(username, token, channel);
		return null;
	}

	private boolean noAuthRequired(HttpHeader header) {
		if (noAuthPattern == null)
			return false;
		return noAuthPattern.matcher(header.getRequestURI()).find();
	}

	private boolean hasValidCache(String token, AuthUserInfo ce) {
		return ce != null && ce.stillValid() && ce.correctToken(token);
	}

	private void storeInCache(String user, String token, SocketChannel channel) {
		long timeout = System.currentTimeMillis() + 60000L * cacheTime;
		InetAddress sa = channel.socket().getInetAddress();
		AuthUserInfo ce = new AuthUserInfo(token, timeout, sa);
		cache.put(user, ce);
	}

	private HttpHeader getError(HttpHeader header, Connection con) {
		HttpGenerator hg = con.getHttpGenerator();
		String url = header.getRequestURI();
		try {
			return hg.get407(new URL(url), "internet");
		} catch (MalformedURLException e) {
			logger.log(Level.WARNING, "Bad url: " + header.getRequestURI(), e);
			return hg.get400(e);
		}
	}

	public HttpHeader doHttpOutFiltering(SocketChannel socket,
			HttpHeader header, Connection con) {
		return null;
	}

	public HttpHeader doConnectFiltering(SocketChannel socket,
			HttpHeader header, Connection con) {
		return null;
	}

	/**
	 * Setup this class with the given properties.
	 * 
	 * @param properties
	 *            the new configuration of this class.
	 */
	public void setup(SProperties properties, HttpProxy proxy) {
		String ct = properties.getProperty("cachetime", "0");
		cacheTime = Integer.parseInt(ct);
		String ra = properties.getProperty("one_ip_only", "true");
		oneIpOnly = Boolean.parseBoolean(ra);
		String allow = properties.getProperty("allow_without_auth");
		if (allow != null)
			noAuthPattern = Pattern.compile(allow);
		String authType = properties.getProperty("authenticator", "plain");
		if ("plain".equalsIgnoreCase(authType)) {
			authenticator = new PlainFileAuthenticator(properties);
		} else if ("sql".equalsIgnoreCase(authType)) {
			authenticator = new SQLAuthenticator(properties);
		} else {
			try {
				Class<? extends Authenticator> clz = proxy.load3rdPartyClass(
						authType, Authenticator.class);
				authenticator = clz.newInstance();
			} catch (ClassNotFoundException e) {
				logger.warning("Failed to find class: '" + authType + "'");
			} catch (InstantiationException e) {
				logger.warning("Failed to instantiate: '" + authType + "'");
			} catch (IllegalAccessException e) {
				logger.warning("Failed to instantiate: '" + authType + "': "
						+ e);
			}
		}
	}
}

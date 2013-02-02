package com.googlecode.httpfilter.proxy.rabbit.proxy;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import com.googlecode.httpfilter.proxy.rabbit.html.HtmlEscapeUtils;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpDateParser;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeaderWithContent;
import com.googlecode.httpfilter.proxy.rabbit.http.StatusCode;
import com.googlecode.httpfilter.proxy.rabbit.util.Config;
import com.googlecode.httpfilter.proxy.rabbit.util.StackTraceUtil;

import static com.googlecode.httpfilter.proxy.rabbit.http.StatusCode.*;

/**
 * A class that can create standard response headers.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class StandardResponseHeaders implements HttpGenerator {
	/** The identity of the server. */
	private final String serverIdentity;
	/** The connection handling the response. */
	private final Connection con;

	public StandardResponseHeaders(String serverIdentity, Connection con) {
		this.serverIdentity = serverIdentity;
		this.con = con;
	}

	public String getServerIdentity() {
		return serverIdentity;
	}

	public Connection getConnection() {
		return con;
	}

	public HttpProxy getProxy() {
		return con.getProxy();
	}

	/**
	 * Get a new HttpHeader. This is the same as getHeader ("HTTP/1.0 200 OK");
	 * 
	 * @return a new HttpHeader.
	 */
	public HttpHeaderWithContent getHeader() {
		return getHeader(_200);
	}

	/**
	 * Get a new HttpHeader initialized with some data.
	 * 
	 * @param sc
	 *            the StatusCode to get a header for
	 * @return a new HttpHeader.
	 */
	public HttpHeaderWithContent getHeader(StatusCode sc) {
		HttpHeaderWithContent ret = new HttpHeaderWithContent();
		ret.setStatusLine(sc.getStatusLine("HTTP/1.1"));
		ret.setHeader("Server", serverIdentity);
		ret.setHeader("Content-type", "text/html; charset=utf-8");
		ret.setHeader("Cache-Control", "no-cache");
		// Set pragma for compatibility with old browsers.
		ret.setHeader("Pragma", "no-cache");
		ret.setHeader("Date", HttpDateParser.getDateString(new Date()));
		return ret;
	}

	/**
	 * Get a 200 Ok header
	 * 
	 * @return a 200 HttpHeader .
	 */
	public HttpHeaderWithContent get200() {
		return getHeader(_200);
	}

	private void copyHeaderIfExists(String type, HttpHeader from, HttpHeader to) {
		String d = from.getHeader(type);
		if (d != null)
			to.setHeader(type, d);
	}

	public HttpHeader get206(String ifRange, HttpHeader header) {
		HttpHeader ret = new HttpHeader();
		ret.setStatusLine(_206.getStatusLine("HTTP/1.1"));
		boolean tiny = ifRange != null;
		if (tiny) {
			String etag = header.getHeader("ETag");
			if (etag != null && ETagUtils.checkStrongEtag(ifRange, etag))
				tiny = false;
		}
		if (tiny) {
			copyHeaderIfExists("Date", header, ret);
			copyHeaderIfExists("ETag", header, ret);
			copyHeaderIfExists("Content-Location", header, ret);
			// copyHeaderIfExists ("Expires", header, ret);
			/*
			 * should do this also in certain conditions... copyHeadersIfExists
			 * ("Cache-Control", header, ret); copyHeadersIfExists ("Vary",
			 * header, ret);
			 */
		} else {
			header.copyHeader(ret);
		}
		return ret;
	}

	/**
	 * Get a 304 Not Modified header for the given old header
	 * 
	 * @param oldresp
	 *            the cached header.
	 * @return a 304 HttpHeader .
	 */
	public HttpHeader get304(HttpHeader oldresp) {
		HttpHeader header = getHeader(_304);
		copyHeaderIfExists("Date", oldresp, header);
		copyHeaderIfExists("Content-Location", oldresp, header);
		copyHeaderIfExists("ETag", oldresp, header);
		String etag = header.getHeader("Etag");
		if (etag != null && !ETagUtils.isWeak(etag))
			copyHeaderIfExists("Expires", oldresp, header);
		List<String> ccs = oldresp.getHeaders("Cache-Control");
		for (int i = 0, s = ccs.size(); i < s; i++)
			header.addHeader("Cache-Control", ccs.get(i));
		ccs = oldresp.getHeaders("Vary");
		for (int i = 0, s = ccs.size(); i < s; i++)
			header.addHeader("Vary", ccs.get(i));
		return header;
	}

	/**
	 * Get a 400 Bad Request header for the given exception.
	 * 
	 * @param exception
	 *            the Exception handled.
	 * @return a HttpHeader for the exception.
	 */
	public HttpHeader get400(Exception exception) {
		// in most cases we should have a header out already, but to be sure...
		HttpHeaderWithContent header = getHeader(_400);
		String page = HtmlPage.getPageHeader(con, _400)
				+ "Unable to handle request:<br><b><pre>\n"
				+ HtmlEscapeUtils.escapeHtml(exception.toString())
				+ "</pre></b></body></html>\n";
		header.setContent(page, "UTF-8");
		return header;
	}

	/**
	 * Get a 401 Authentication Required for the given realm and url.
	 * 
	 * @param realm
	 *            the realm that requires auth.
	 * @param url
	 *            the URL of the request made.
	 * @return a suitable HttpHeader.
	 */
	public HttpHeader get401(URL url, String realm) {
		return getAuthorizationHeader(realm, url, _401, "WWW");
	}

	private HttpHeader getAuthorizationHeader(String realm, URL url,
			StatusCode sc, String type) {
		HttpHeaderWithContent header = getHeader(sc);
		header.setHeader(type + "-Authenticate", "Basic realm=\"" + realm
				+ "\"");
		String page = HtmlPage.getPageHeader(con, sc) + "Access to: <b>"
				+ HtmlEscapeUtils.escapeHtml(url.toString())
				+ "</b><br>requires some authentication\n</body></html>\n";
		header.setContent(page, "UTF-8");
		return header;
	}

	/**
	 * Get a 403 Forbidden header.
	 * 
	 * @return a HttpHeader.
	 */
	public HttpHeader get403() {
		// in most cases we should have a header out already, but to be sure...
		HttpHeaderWithContent header = getHeader(_403);
		String page = HtmlPage.getPageHeader(con, _403)
				+ "That is forbidden</body></html>";
		header.setContent(page, "UTF-8");
		return header;
	}

	/**
	 * Get a 404 File not found.
	 * 
	 * @return a HttpHeader.
	 */
	public HttpHeader get404(String file) {
		// in most cases we should have a header out already, but to be sure...
		HttpHeaderWithContent header = getHeader(_404);
		String page = HtmlPage.getPageHeader(con, _404) + "File '"
				+ HtmlEscapeUtils.escapeHtml(file)
				+ "' not found.</body></html>";
		header.setContent(page, "UTF-8");
		return header;
	}

	/**
	 * Get a 407 Proxy Authentication Required for the given realm and url.
	 * 
	 * @param realm
	 *            the realm that requires auth.
	 * @param url
	 *            the URL of the request made.
	 * @return a suitable HttpHeader.
	 */
	public HttpHeader get407(URL url, String realm) {
		return getAuthorizationHeader(realm, url, _407, "Proxy");
	}

	/**
	 * Get a 412 Precondition Failed header.
	 * 
	 * @return a suitable HttpHeader.
	 */
	public HttpHeader get412() {
		HttpHeaderWithContent header = getHeader(_412);
		String page = HtmlPage.getPageHeader(con, _412) + "</body></html>\n";
		header.setContent(page, "UTF-8");
		return header;
	}

	/**
	 * Get a 414 Request-URI Too Long
	 * 
	 * @return a suitable HttpHeader.
	 */
	public HttpHeader get414() {
		HttpHeaderWithContent header = getHeader(_414);
		String page = HtmlPage.getPageHeader(con, _414) + "</body></html>\n";
		header.setContent(page, "UTF-8");
		return header;
	}

	/**
	 * Get a Requested Range Not Satisfiable for the given exception.
	 * 
	 * @param exception
	 *            the Exception made.
	 * @return a suitable HttpHeader.
	 */
	public HttpHeader get416(Throwable exception) {
		HttpHeaderWithContent header = getHeader(_416);
		String page = HtmlPage.getPageHeader(con, _416)
				+ "Request out of range: "
				+ HtmlEscapeUtils.escapeHtml(exception.toString())
				+ ".</b>\n</body></html>\n";
		header.setContent(page, "UTF-8");
		return header;
	}

	/**
	 * Get a 417 Expectation Failed header.
	 * 
	 * @param expectation
	 *            the expectation that failed.
	 * @return a suitable HttpHeader.
	 */
	public HttpHeader get417(String expectation) {
		HttpHeaderWithContent header = getHeader(_417);
		String page = HtmlPage.getPageHeader(con, _417)
				+ "RabbIT does not handle the '"
				+ HtmlEscapeUtils.escapeHtml(expectation)
				+ "' kind of expectations yet.</b>\n</body></html>\n";
		header.setContent(page, "UTF-8");
		return header;
	}

	/**
	 * Get a 500 Internal Server Error header for the given exception.
	 * 
	 * @param exception
	 *            the Exception made.
	 * @return a suitable HttpHeader.
	 */
	public HttpHeader get500(String url, Throwable exception) {
		// in most cases we should have a header out already, but to be sure...
		// normally this only thrashes the page... Too bad.
		HttpHeaderWithContent header = getHeader(_500);
		Properties props = System.getProperties();
		HttpProxy proxy = getProxy();
		Config config = proxy.getConfig();
		String page = HtmlPage.getPageHeader(con, _500)
				+ "You have found a bug in RabbIT please report this"
				+ "(together with the URL you tried to visit) to the "
				+ "<a href=\"http://www.khelekore.org/rabbit/\" target ="
				+ "\"_top\">RabbIT</a> crew.<br><br>\n"
				+ "<font size = 4>Connection status</font><br><hr noshade>\n"
				+ con.getDebugInfo().replaceAll("\n", "<br>\n")
				+ "<br>\n<font size = 4>Proxy status</font><br>\n<hr noshade>\n"
				+ "Proxy version: " + HttpProxy.VERSION + "<br>\n"
				+ "Proxy identity: " + proxy.getServerIdentity() + "<br>\n"
				+ "Server host: " + proxy.getHost() + "<br>\n"
				+ "Server port: " + proxy.getPort() + "<br>\n"
				+ "Access filters: "
				+ config.getProperty("Filters", "accessfilters")
				+ "<br>\nHttp in filters: "
				+ config.getProperty("Filters", "httpinfilters")
				+ "<br>\nHttp out filters:"
				+ config.getProperty("Filters", "httpoutfilters")
				+ "<br>\n<br>\n<font size = 4>System properties</font><br>\n"
				+ "<hr noshade>\n" + "java.version: "
				+ props.getProperty("java.version") + "<br>\n"
				+ "java.vendor: " + props.getProperty("java.vendor") + "<br>\n"
				+ "os.name: " + props.getProperty("os.name") + "<br>\n"
				+ "os.version: " + props.getProperty("os.version") + "<br>\n"
				+ "os.arch: " + props.getProperty("os.arch") + "<br>\n"
				+ "Error is:<BR><pre>\n"
				+ StackTraceUtil.getStackTrace(exception)
				+ "</pre><br><hr noshade>\n</body></html>\n";
		header.setContent(page, "UTF-8");
		return header;
	}

	private static final String[][] placeTransformers = { { "www.", "" },
			{ "", ".com" }, { "www.", ".com" }, { "", ".org" },
			{ "www.", ".org" }, { "", ".net" }, { "www.", ".net" } };

	/**
	 * Get a 504 Gateway Timeout for the given exception.
	 * 
	 * @param e
	 *            the Exception made.
	 * @return a suitable HttpHeader.
	 */
	public HttpHeader get504(String uri, Throwable e) {
		HttpHeaderWithContent header = getHeader(_504);
		try {
			boolean dnsError = (e instanceof UnknownHostException);
			URL u = new URL(uri);
			StringBuilder content = new StringBuilder(HtmlPage.getPageHeader(
					con, _504));
			if (dnsError)
				content.append("Server not found");
			else
				content.append("Unable to handle request");

			content.append(":<br><b>"
					+ HtmlEscapeUtils.escapeHtml(e.getMessage()));

			content.append("\n\n<br>Did you mean to go to: ");
			content.append(getPlaces(u));
			String message = "";
			if (!dnsError)
				message = "<xmp>" + StackTraceUtil.getStackTrace(e) + "</xmp>";
			content.append("</b><br>" + message + "</body></html>\n");

			header.setContent(content.toString(), "UTF-8");
		} catch (MalformedURLException ex) {
			throw new RuntimeException(ex);
		}

		return header;
	}

	public StringBuilder getPlaces(URL u) {
		StringBuilder content = new StringBuilder();
		content.append("<ul>");
		Set<String> places = new HashSet<String>();
		for (int i = 0; i < placeTransformers.length; i++) {
			String pre = placeTransformers[i][0];
			String suf = placeTransformers[i][1];
			String place = getPlace(u, pre, suf);
			if (place != null && !places.contains(place)) {
				content.append("<li><a href=\""
						+ HtmlEscapeUtils.escapeHtml(place) + "\">"
						+ HtmlEscapeUtils.escapeHtml(place) + "</a></li>\n");
				places.add(place);
			}
		}
		content.append("</ul>");
		return content;
	}

	private String getPlace(URL u, String hostPrefix, String hostSuffix) {
		String host = u.getHost();
		if (host.startsWith(hostPrefix))
			hostPrefix = "";
		if (host.endsWith(hostSuffix))
			hostSuffix = "";
		if (hostPrefix.equals("") && hostSuffix.equals(""))
			return null;
		return u.getProtocol() + "://" + hostPrefix + u.getHost() + hostSuffix
				+ (u.getPort() == -1 ? "" : ":" + u.getPort()) + u.getFile();
	}
}

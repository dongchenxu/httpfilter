package com.googlecode.httpfilter.proxy.rabbit.proxy;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.meta.MetaHandler;
import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;
import com.googlecode.httpfilter.proxy.rabbit.util.TrafficLogger;

class MetaHandlerHandler {

	/**
	 * Handle a meta page.
	 * 
	 * @param con
	 *            the Connection serving the request
	 * @param header
	 *            the request being made.
	 * @param tlProxy
	 *            the TrafficLogger for proxy traffic
	 * @param tlClient
	 *            the TrafficLogger for the client traffic
	 * @throws IOException
	 *             if the actual meta handler fails to handle the request
	 */
	public void handleMeta(Connection con, HttpHeader header,
			TrafficLogger tlProxy, TrafficLogger tlClient) throws IOException {
		con.getCounter().inc("Meta pages requested");
		URL url;
		try {
			url = new URL(header.getRequestURI());
		} catch (MalformedURLException e) {
			// this should not happen since HTTPBaseHandler managed to do it...
			con.doError(500, "Failed to create url: " + e);
			return;
		}
		String file = url.getFile().substring(1); // remove initial '/'
		if (file.length() == 0)
			file = "FileSender/";

		int index;
		String args = "";
		if ((index = file.indexOf("?")) >= 0) {
			args = file.substring(index + 1);
			file = file.substring(0, index);
		}
		SProperties htab = splitArgs(args);
		if ((index = file.indexOf("/")) >= 0) {
			String fc = file.substring(index + 1);
			file = file.substring(0, index);
			htab.put("argstring", fc);
		}
		String error = null;
		try {
			if (file.startsWith("favicon.ico")) {
				con.doError(404, "");
				return;
			}
			if (file.indexOf(".") < 0)
				file = "rabbit.meta." + file;

			Class<? extends MetaHandler> cls = con.getProxy()
					.load3rdPartyClass(file, MetaHandler.class);
			MetaHandler mh;
			mh = cls.newInstance();
			mh.handle(header, htab, con, tlProxy, tlClient);
			con.getCounter().inc("Meta pages handled");
			// Now take care of every error...
		} catch (NoSuchMethodError e) {
			error = "Given metahandler doesnt have a public no-arg constructor:"
					+ file + ", " + e;
		} catch (ClassCastException e) {
			error = "Given metapage is not a MetaHandler:" + file + ", " + e;
		} catch (ClassNotFoundException e) {
			error = "Couldnt find class:" + file + ", " + e;
		} catch (InstantiationException e) {
			error = "Couldnt instantiate metahandler:" + file + ", " + e;
		} catch (IllegalAccessException e) {
			error = "Que? metahandler access violation?:" + file + ", " + e;
		} catch (IllegalArgumentException e) {
			error = "Strange name of metapage?:" + file + ", " + e;
		}
		if (error != null) {
			Logger.getLogger(getClass().getName()).warning(error);
			con.doError(400, error);
		}
	}

	/**
	 * Splits the CGI-paramsstring into variables and values. put these values
	 * into a hashtable for easy retrival
	 * 
	 * @param params
	 *            the CGI-querystring.
	 * @return a map with type->value maps for the CGI-querystring
	 */
	public SProperties splitArgs(String params) {
		SProperties htab = new SProperties();
		StringTokenizer st = new StringTokenizer(params, "=&", true);
		String key = null;
		while (st.hasMoreTokens()) {
			String next = st.nextToken();
			if (next.equals("=")) {
				// nah..
			} else if (next.equals("&")) {
				if (key != null) {
					htab.put(key, "");
					key = null;
				}
			} else if (key == null) {
				key = next;
			} else {
				try {
					next = URLDecoder.decode(next, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					Logger log = Logger.getLogger(getClass().getName());
					log.log(Level.WARNING, "Failed to get utf-8", e);
				}
				htab.put(key, next);
				key = null;
			}
		}
		return htab;
	}
}

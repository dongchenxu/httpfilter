package com.googlecode.httpfilter.proxy.rabbit.proxy;

import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.proxy.HttpProxy;
import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;
import com.googlecode.httpfilter.proxy.rabbit.util.TrafficLogger;

/**
 * A simple ClientTrafficLogger that just writes simple network usage to
 * standard out.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class StandardOutTrafficLogger implements ClientTrafficLogger {

	public void logTraffic(String user, HttpHeader request,
			TrafficLogger client, TrafficLogger network, TrafficLogger cache,
			TrafficLogger proxy) {
		System.out.println("user: " + user + ", url: "
				+ request.getRequestURI() + ", client read: " + client.read()
				+ ", client write: " + client.write() + ", network read: "
				+ network.read() + ", network write: " + network.write());
	}

	public void setup(SProperties properties, HttpProxy proxy) {
		// empty
	}
}
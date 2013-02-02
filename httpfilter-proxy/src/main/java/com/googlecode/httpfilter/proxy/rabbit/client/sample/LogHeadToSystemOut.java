package com.googlecode.httpfilter.proxy.rabbit.client.sample;

import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;

/**
 * A http HEAD response handler that just logs the response to System.out.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class LogHeadToSystemOut implements HeadResponseListener {
	public void response(HttpHeader request, HttpHeader response) {
		System.out.print(request.getRequestURI() + "\n" + response);
	}
}

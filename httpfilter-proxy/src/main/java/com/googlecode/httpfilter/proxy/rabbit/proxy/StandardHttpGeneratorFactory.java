package com.googlecode.httpfilter.proxy.rabbit.proxy;

import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;

/**
 * A HttpGeneratorFactory that creates StandardResponseHeaders instances.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class StandardHttpGeneratorFactory implements HttpGeneratorFactory {
	public HttpGenerator create(String identity, Connection con) {
		return new StandardResponseHeaders(identity, con);
	}

	public void setup(SProperties props) {
		// nothing to do
	}
}

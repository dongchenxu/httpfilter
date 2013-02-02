package com.googlecode.httpfilter.proxy.rabbit.filter.authenticate;

import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.proxy.Connection;
import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;
import com.googlecode.httpfilter.proxy.rabbit.util.SimpleUserHandler;

/**
 * An authenticator that reads username and passwords from a plain text file.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class PlainFileAuthenticator implements Authenticator {
	private final SimpleUserHandler userHandler;

	/**
	 * Create a new PlainFileAuthenticator that will be configured using the
	 * given properties.
	 * 
	 * @param props
	 *            the configuration for this authenticator
	 */
	public PlainFileAuthenticator(SProperties props) {
		String userFile = props.getProperty("userfile", "conf/allowed");
		userHandler = new SimpleUserHandler();
		userHandler.setFile(userFile);
	}

	public String getToken(HttpHeader header, Connection con) {
		return con.getPassword();
	}

	public boolean authenticate(String user, String pwd) {
		return userHandler.isValidUser(user, pwd);
	}
}

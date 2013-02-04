package com.googlecode.httpfilter.proxy.rabbit.filter.authenticate;

import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.proxy.Connection;

/**
 * Something that can authenticate users using some kind of database.
 */
public interface Authenticator {

	/**
	 * Find the token used to authenticate
	 * 
	 * @param header
	 *            the request
	 * @param con
	 *            the Connection handling the request
	 * @return the authentication token
	 */
	String getToken(HttpHeader header, Connection con);

	/**
	 * Try to authenticate the user.
	 * 
	 * @param user
	 *            the username
	 * @param pwd
	 *            the password of the user
	 * @return true if authentication succeeded, false otherwise.
	 */
	boolean authenticate(String user, String pwd);
}

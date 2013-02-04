package com.googlecode.httpfilter.proxy.rabbit.html;

/**
 * The different types of tokens.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public enum TokenType {
	/** This token is empty */
	EMPTY,
	/** This token is text. */
	TEXT,
	/** This token is a tag. */
	TAG,
	/** This token is a comment. */
	COMMENT,
	/** This token is a script */
	SCRIPT
}

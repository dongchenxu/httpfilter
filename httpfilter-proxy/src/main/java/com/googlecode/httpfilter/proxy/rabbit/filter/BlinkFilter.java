package com.googlecode.httpfilter.proxy.rabbit.filter;

import com.googlecode.httpfilter.proxy.rabbit.html.HtmlBlock;
import com.googlecode.httpfilter.proxy.rabbit.html.Tag;
import com.googlecode.httpfilter.proxy.rabbit.html.TagType;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.proxy.Connection;

/**
 * A filter that removes the blink and /blink tags.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class BlinkFilter extends SimpleTagFilter {

	// For the factory.
	public BlinkFilter() {
	}

	/**
	 * Create a new BlinkFilter for the given request, response pair.
	 * 
	 * @param con
	 *            the Connection handling the request.
	 * @param request
	 *            the actual request made.
	 * @param response
	 *            the actual response being sent.
	 */
	public BlinkFilter(Connection con, HttpHeader request, HttpHeader response) {
		super(con, request, response);
	}

	public HtmlFilter newFilter(Connection con, HttpHeader request,
			HttpHeader response) {
		return new BlinkFilter(con, request, response);
	}

	/**
	 * Remove blink tags.
	 * 
	 * @param block
	 *            the part of the html page we are filtering.
	 */
	@Override
	public void handleTag(Tag tag, HtmlBlock block, int tokenIndex) {
		TagType tt = tag.getTagType();
		if (tt == TagType.BLINK || tt == TagType.SBLINK)
			block.removeToken(tokenIndex);
	}
}

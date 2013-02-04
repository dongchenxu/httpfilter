package com.googlecode.httpfilter.proxy.rabbit.filter;

import java.util.List;
import com.googlecode.httpfilter.proxy.rabbit.html.HtmlBlock;
import com.googlecode.httpfilter.proxy.rabbit.html.Token;
import com.googlecode.httpfilter.proxy.rabbit.html.TokenType;
import com.googlecode.httpfilter.proxy.rabbit.html.Tag;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.proxy.Connection;

/**
 * A class that inserts some text and links at the top of a page. Useful for
 * inserting links to unfiltered page.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public abstract class SimpleTagFilter extends HtmlFilter {

	// For the factory.
	public SimpleTagFilter() {
	}

	/**
	 * Create a new SimpleTagFilter for the given request, response pair.
	 * 
	 * @param con
	 *            the Connection handling the request.
	 * @param request
	 *            the actual request made.
	 * @param response
	 *            the actual response being sent.
	 */
	public SimpleTagFilter(Connection con, HttpHeader request,
			HttpHeader response) {
		super(con, request, response);
	}

	/**
	 * Iterate over all tags and call handleTag on them.
	 * 
	 * @param block
	 *            the part of the html page we are filtering.
	 */
	@Override
	public void filterHtml(HtmlBlock block) {
		List<Token> tokens = block.getTokens();
		int tsize = tokens.size();
		for (int i = 0; i < tsize; i++) {
			Token t = tokens.get(i);
			if (t.getType() == TokenType.TAG) {
				Tag tag = t.getTag();
				handleTag(tag, block, i);
			}
		}
	}

	/**
	 * Handle a tag.
	 * 
	 * @param tag
	 *            the Tag to handle.
	 * @param block
	 *            the current HtmlBlock
	 * @param tokenIndex
	 *            the index of the current Token
	 */
	public abstract void handleTag(Tag tag, HtmlBlock block, int tokenIndex);
}

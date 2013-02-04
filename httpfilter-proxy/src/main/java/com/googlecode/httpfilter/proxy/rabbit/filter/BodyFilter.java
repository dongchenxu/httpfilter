package com.googlecode.httpfilter.proxy.rabbit.filter;

import java.net.MalformedURLException;
import java.net.URL;
import com.googlecode.httpfilter.proxy.rabbit.html.HtmlBlock;
import com.googlecode.httpfilter.proxy.rabbit.html.Tag;
import com.googlecode.httpfilter.proxy.rabbit.html.TagType;
import com.googlecode.httpfilter.proxy.rabbit.html.Token;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.proxy.Connection;
import com.googlecode.httpfilter.proxy.rabbit.util.Config;

/**
 * A class that inserts some text and links at the top of a page. Useful for
 * inserting links to unfiltered page.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class BodyFilter extends SimpleTagFilter {
	private boolean done = false;

	/** the identifier for pages filtered with this proxy. */
	private static final String PREFIX = "This page is filtered by RabbIT ";
	/** the string to append after our advertising. */
	private static final String POSTFIX = "<br>";
	/** the link string. */
	private static final String LINK = "unfiltered page";

	// for the factory.
	public BodyFilter() {
	}

	/**
	 * Create a new BodyFilter for the given request, response pair.
	 * 
	 * @param con
	 *            the Connection handling the request.
	 * @param request
	 *            the actual request made.
	 * @param response
	 *            the actual response being sent.
	 */
	public BodyFilter(Connection con, HttpHeader request, HttpHeader response) {
		super(con, request, response);
	}

	public HtmlFilter newFilter(Connection con, HttpHeader request,
			HttpHeader response) {
		return new BodyFilter(con, request, response);
	}

	/**
	 * Insert some text at the top of the html page.
	 * 
	 * @param block
	 *            the part of the html page we are filtering.
	 */
	@Override
	public void filterHtml(HtmlBlock block) {
		if (!done) {
			super.filterHtml(block);
		}
	}

	@Override
	public void handleTag(Tag tag, HtmlBlock block, int tokenIndex) {
		if (tag.getTagType() == TagType.BODY) {
			insertTokens(block, tokenIndex + 1);
			done = true;
		}
	}

	/**
	 * Insert the links in an ordered fashion.
	 * 
	 * @param block
	 *            the html block were filtering.
	 * @param pos
	 *            the position in the block were inserting stuff at.
	 * @return the new position in the block.
	 */
	protected int insertTokens(HtmlBlock block, int pos) {
		Config config = con.getProxy().getConfig();
		block.insertToken(
				new Token(config.getProperty(getClass().getName(), "prefix",
						PREFIX)), pos++);
		if (config.getProperty(getClass().getName(), "unfilteredlink", "true")
				.toLowerCase().equals("true")) {
			Tag a = new Tag("A");
			try {
				URL url = new URL(request.getRequestURI());
				a.addArg("HREF", getHref(url));
				block.insertToken(new Token(a), pos++);
				block.insertToken(
						new Token(config.getProperty(getClass().getName(),
								"link", LINK)), pos++);
				Tag slasha = new Tag("/A");
				block.insertToken(new Token(slasha), pos++);
			} catch (MalformedURLException e) {
				// ignore
			}
		}
		block.insertToken(
				new Token(config.getProperty(getClass().getName(), "postfix",
						POSTFIX)), pos++);
		return pos;
	}

	private String getHref(URL url) {
		StringBuilder sb = new StringBuilder();
		sb.append("\"");
		sb.append(url.getProtocol());
		sb.append("://noproxy.");
		sb.append(url.getHost());
		sb.append((url.getPort() > 0) ? ":" + url.getPort() : "");
		sb.append(url.getFile());
		if (url.getRef() != null)
			sb.append("#").append(url.getRef());
		sb.append("\"");
		return sb.toString();
	}
}

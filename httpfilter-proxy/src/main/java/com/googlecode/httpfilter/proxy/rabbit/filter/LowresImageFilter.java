package com.googlecode.httpfilter.proxy.rabbit.filter;

import com.googlecode.httpfilter.proxy.rabbit.html.HtmlBlock;
import com.googlecode.httpfilter.proxy.rabbit.html.Tag;
import com.googlecode.httpfilter.proxy.rabbit.html.TagType;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.proxy.Connection;

/**
 * This filter removes the &quot;<tt>lowsrc=some_image.gif</tt>&quot; attributes
 * from the &lt;img&gt; tags.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class LowresImageFilter extends SimpleTagFilter {

	// For the factory.
	public LowresImageFilter() {
	}

	/**
	 * Create a new LowresImageFilter for the given request, response pair.
	 * 
	 * @param con
	 *            the Connection handling the request.
	 * @param request
	 *            the actual request made.
	 * @param response
	 *            the actual response being sent.
	 */
	public LowresImageFilter(Connection con, HttpHeader request,
			HttpHeader response) {
		super(con, request, response);
	}

	public HtmlFilter newFilter(Connection con, HttpHeader request,
			HttpHeader response) {
		return new LowresImageFilter(con, request, response);
	}

	/**
	 * remove the lowres tags.
	 * 
	 * @param block
	 *            the part of the html page we are filtering.
	 */
	public void handleTag(Tag tag, HtmlBlock block, int tokenIndex) {
		if (tag.getTagType() == TagType.IMG)
			tag.removeAttribute("lowsrc");
	}
}

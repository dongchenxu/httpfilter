package com.googlecode.httpfilter.proxy.rabbit.filter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.googlecode.httpfilter.proxy.rabbit.html.HtmlBlock;
import com.googlecode.httpfilter.proxy.rabbit.html.Tag;
import com.googlecode.httpfilter.proxy.rabbit.html.TagType;
import com.googlecode.httpfilter.proxy.rabbit.html.Token;
import com.googlecode.httpfilter.proxy.rabbit.html.TokenType;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.proxy.Connection;
import com.googlecode.httpfilter.proxy.rabbit.proxy.HttpProxy;
import com.googlecode.httpfilter.proxy.rabbit.util.Config;

/**
 * This class switches advertising images into another image.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class AdFilter extends HtmlFilter {
	/** the image we replace ads with */
	private static final String ADREPLACER = "http://$proxy/FileSender/public/NoAd.gif";

	/** the actual imagelink. */
	private String adreplacer = null;

	/** The pattern. */
	private Pattern adPattern;

	/**
	 * Create a new AdFilter factory
	 */
	public AdFilter() {
		// empty
	}

	/**
	 * Create a new AdFilter for the given request, response pair.
	 * 
	 * @param con
	 *            the Connection handling the request
	 * @param request
	 *            the actual request made.
	 * @param response
	 *            the actual response being sent.
	 */
	public AdFilter(Connection con, HttpHeader request, HttpHeader response) {
		super(con, request, response);
		int idx;
		HttpProxy proxy = con.getProxy();
		adreplacer = proxy.getConfig().getProperty(getClass().getName(),
				"adreplacer", ADREPLACER);
		while ((idx = adreplacer.indexOf("$proxy")) > -1) {
			adreplacer = adreplacer.substring(0, idx)
					+ proxy.getHost().getHostName() + ":" + proxy.getPort()
					+ adreplacer.substring(idx + "$proxy".length());
		}
	}

	public HtmlFilter newFilter(Connection con, HttpHeader request,
			HttpHeader response) {
		return new AdFilter(con, request, response);
	}

	/**
	 * Check if the given tag ends the current a-tag. Some sites have broken
	 * html (linuxtoday.com!).
	 * 
	 * @param tt
	 *            the TagType to check
	 * @return true if the tag is an end tag
	 */
	private boolean isAEnder(TagType tt) {
		return tt == TagType.SA || tt == TagType.STD || tt == TagType.STR;
	}

	/**
	 * Removes advertising from the given block.
	 * 
	 * @param block
	 *            the part of the html page we are filtering.
	 */
	@Override
	public void filterHtml(HtmlBlock block) {
		int astart;

		List<Token> tokens = block.getTokens();
		int tsize = tokens.size();
		for (int i = 0; i < tsize; i++) {
			Token t = tokens.get(i);
			if (t.getType() == TokenType.TAG) {
				Tag tag = t.getTag();
				TagType tagtype = tag.getTagType();
				if (tagtype == TagType.A) {
					astart = i;
					int ttsize = tokens.size();
					for (; i < ttsize; i++) {
						Token tk2 = tokens.get(i);
						if (tk2.getType() == TokenType.TAG) {
							Tag tag2 = tk2.getTag();
							TagType t2tt = tag2.getTagType();
							if (t2tt != null && isAEnder(t2tt))
								break;
							else if (t2tt != null && t2tt == TagType.IMG
									&& isEvil(tag.getAttribute("href")))
								tag2.setAttribute("src", adreplacer);
						}
					}
					if (i == tsize && astart < i) {
						block.setRest((tokens.get(astart)).getStartIndex());
					}
				} else if (tagtype == TagType.LAYER
						|| tagtype == TagType.SCRIPT) {
					String src = tag.getAttribute("src");
					if (isEvil(src))
						tag.setAttribute("src", adreplacer);
				}
			}
		}
	}

	/**
	 * Check if a string is evil (that is its probably advertising).
	 * 
	 * @param str
	 *            the String to check.
	 * @return true if the given string seems to contain advertising links
	 */
	public boolean isEvil(String str) {
		if (str == null)
			return false;
		if (adPattern == null) {
			Config conf = con.getProxy().getConfig();
			String adLinks = conf.getProperty(getClass().getName(), "adlinks",
					"[/.]ad[/.]");
			adPattern = Pattern.compile(adLinks);
		}
		Matcher m = adPattern.matcher(str);
		return (m.find());
	}
}

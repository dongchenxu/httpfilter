package com.googlecode.httpfilter.proxy.rabbit.filter;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.googlecode.httpfilter.common.utils.ParameterUtils;
import com.googlecode.httpfilter.proxy.rabbit.html.HtmlBlock;
import com.googlecode.httpfilter.proxy.rabbit.html.Tag;
import com.googlecode.httpfilter.proxy.rabbit.html.TagType;
import com.googlecode.httpfilter.proxy.rabbit.html.Token;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.proxy.Connection;

/**
 * ×¢Èë×·×ÙÕß
 * @author vlinux
 *
 */
public class TracerHtmlFilter extends HtmlFilter {

	public TracerHtmlFilter() {
		
	}
	
	private TracerHtmlFilter(Connection con, HttpHeader request,
			HttpHeader response) {
		super(con, request, response);
	}
	
	@Override
	public HtmlFilter newFilter(Connection con, HttpHeader request,
			HttpHeader response) {
		return new TracerHtmlFilter(con, request, response);
	}

	@Override
	public void filterHtml(HtmlBlock block) {

		final String uri = request.getRequestURI();
		final String tracer = ParameterUtils.getAsString(uri, "__httpfilter_tracer");
		if( !StringUtils.isBlank(tracer) ) {
			return;
		}
		
		final List<Token> tokens = block.getTokens();
		final int tsize = tokens.size();
		for (int index = 0; index < tsize; index++) {
			final Token token = tokens.get(index);
			if( null == token
					|| null == token.getTag()) {
				continue;
			}
			Tag tag = token.getTag();
			replaceATag(tag, tracer);
			replaceImgTag(tag, tracer);
		}
		
	}
	
	private void replaceATag(Tag tag, String tracer) {
		if( tag.getTagType() == TagType.A ) {
			final String url = ParameterUtils.setAsString(tag.getAttribute("href"), "__httpfilter_tracer", tracer);
			tag.setAttribute("href", url);
		}
	}
	
	private void replaceImgTag(Tag tag, String tracer) {
		if( tag.getTagType() == TagType.IMG ) {
			final String url = ParameterUtils.setAsString(tag.getAttribute("src"), "__httpfilter_tracer", tracer);
			tag.setAttribute("src", url);
		}
	}
	

}

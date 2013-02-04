package com.googlecode.httpfilter.proxy.rabbit.filter;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.googlecode.httpfilter.common.Configer;
import com.googlecode.httpfilter.common.utils.ParameterUtils;
import com.googlecode.httpfilter.proxy.rabbit.html.HtmlBlock;
import com.googlecode.httpfilter.proxy.rabbit.html.Tag;
import com.googlecode.httpfilter.proxy.rabbit.html.TagType;
import com.googlecode.httpfilter.proxy.rabbit.html.Token;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.proxy.Connection;

/**
 * 注入Firebug
 * @author luanjia
 *
 */
public class FirebugHtmlFilter extends HtmlFilter {

	public FirebugHtmlFilter() {
		
	}
	
	private FirebugHtmlFilter(Connection con, HttpHeader request,
			HttpHeader response) {
		super(con, request, response);
	}
	
	@Override
	public HtmlFilter newFilter(Connection con, HttpHeader request,
			HttpHeader response) {
		return new FirebugHtmlFilter(con, request, response);
	}

	@Override
	public void filterHtml(HtmlBlock block) {
		
		final String uri = request.getRequestURI();
		if( !StringUtils.equals("true", ParameterUtils.getAsString(uri, "__httpfilter_firebug")) ) {
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
			final TagType tagType = token.getTag().getTagType();
			enableDebug(token, tagType);
			insertFirebug(block, tagType, index);
		}
		
	}
	
	/**
	 * 启用debug, 启用的方式是在HTML标签中增加debug='true'
	 * @param token
	 * @param tagType
	 */
	private void enableDebug(Token token, TagType tagType) {
		if( tagType == TagType.HTML ) {
			token.getTag().setAttribute("debug", "'true'");
		}
	}
	
	/**
	 * 注入firebug
	 * @param block
	 * @param token
	 * @param tagType
	 */
	private void insertFirebug(HtmlBlock block, TagType tagType, int index) {
		if( tagType == TagType.HEAD ) {
			Tag scriptTag = new Tag("script");
			scriptTag.addArg("type", "'text/javascript'");
//			scriptTag.addArg("src", "'https://getfirebug.com/firebug-lite.js'");
//			scriptTag.addArg("src", "'http://127.0.0.1:8080/httpfilter-web/javascript/firebug-lite/build/firebug-lite.js'");
			scriptTag.addArg("src", "'"+Configer.getDefault().getFirebugPath()+"'");
			block.insertToken(new Token(scriptTag), ++index);
			block.insertToken(new Token(new Tag("/script")), ++index);
		}
	}
	

}

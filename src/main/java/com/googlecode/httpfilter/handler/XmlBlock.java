package com.googlecode.httpfilter.handler;

import java.nio.charset.Charset;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.parser.XmlTreeBuilder;

/**
 * XML块
 * @author vlinux
 *
 */
public class XmlBlock extends TextBlock {

	/*
	 * xml文档
	 */
	private final Document document;
	
	public XmlBlock(String text, Charset charset) {
		super(text, charset);
		this.document = Jsoup.parse(text, "", new Parser(new XmlTreeBuilder()));
	}

	/**
	 * 获取当前xml文档
	 * @return
	 */
	public Document getDocument() {
		return document.clone();
	}
	
}

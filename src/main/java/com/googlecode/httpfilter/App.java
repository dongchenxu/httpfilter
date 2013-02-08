package com.googlecode.httpfilter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class App {

	public static void main(String... args) throws MalformedURLException, IOException {
		
		Document doc = Jsoup.parse(new URL("http://detail.tmall.com/item.htm?id=15302760537"), 6000);
		
		doc.select("head").first().prepend("<script type='text/javascript' src='https://getfirebug.com/firebug-lite.js'></script>");
		
		//<meta charset="gbk"/>
		final Iterator<Element> it = doc.select("meta[HTTP-EQUIV=content-type]").iterator();
		while( it.hasNext() ) {
			final Element e = it.next();
			System.out.println( e.attr("content") );
		}
		
		System.out.println( doc.select("meta[charset]").first().attr("charset1").length() );
		
	}
	
}

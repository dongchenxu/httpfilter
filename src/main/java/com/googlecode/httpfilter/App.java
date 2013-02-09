package com.googlecode.httpfilter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jetty.util.IO;

public class App {

	public static void main(String... args) throws MalformedURLException, IOException {
		
		byte[] datas = IO.readBytes(new URL("http://baike.baidu.com/view/868685.htm").openStream());
		final String html = new String(datas, "gbk");
		System.out.println( html );
		
	}
	
}

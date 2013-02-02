package com.googlecode.httpfilter.proxy.rabbit.client.sample;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import com.googlecode.httpfilter.proxy.rabbit.client.ClientBase;
import com.googlecode.httpfilter.proxy.rabbit.client.ClientListenerAdapter;
import com.googlecode.httpfilter.proxy.rabbit.client.CountingClientBaseStopper;
import com.googlecode.httpfilter.proxy.rabbit.client.FileSaver;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.httpio.WebConnectionResourceSource;

/**
 * A class to download a set of resources. Given a set of urls this class will
 * download all of them concurrently using a standard ClientBase. This is mostly
 * an example of how to use the rabbit client classes.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class WGet {
	private final ClientBase clientBase;
	private final CountingClientBaseStopper ccbs;

	/**
	 * Download all urls given in the args arrays.
	 * 
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		try {
			WGet wget = new WGet();
			if (args.length > 0)
				wget.get(args);
			else
				wget.clientBase.shutdown();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create a new WGet that can be used to download resources.
	 * 
	 * @throws IOException
	 *             if starting the NioHandler fails
	 */
	public WGet() throws IOException {
		clientBase = new ClientBase();
		ccbs = new CountingClientBaseStopper(clientBase);
	}

	/**
	 * Add a set of urls to download.
	 * 
	 * @param urls
	 *            the URL:s to download
	 * @throws IOException
	 *             if the get fails at startup
	 */
	public void get(String[] urls) throws IOException {
		for (String url : urls)
			get(url);
	}

	/**
	 * Add an url to the set of urls to be downloaded
	 * 
	 * @param url
	 *            the URL to download
	 * @throws IOException
	 *             if the get fail at startup
	 */
	public void get(String url) throws IOException {
		ccbs.sendRequest(clientBase.getRequest("GET", url), new WGetListener());
	}

	private class WGetListener extends ClientListenerAdapter {
		@Override
		public void redirectedTo(String url) throws IOException {
			get(url);
		}

		@Override
		public void handleResponse(HttpHeader request, HttpHeader response,
				WebConnectionResourceSource wrs) {
			try {
				File f = new File(getFileName(request));
				if (f.exists())
					throw new IOException("File already exists: " + f.getName());
				FileSaver blockHandler = new FileSaver(request, clientBase,
						this, wrs, f);
				wrs.addBlockListener(blockHandler);
			} catch (IOException e) {
				wrs.release();
				handleFailure(request, e);
			}
		}

		@Override
		public void requestDone(HttpHeader request) {
			ccbs.requestDone();
		}
	}

	private String getFileName(HttpHeader request) throws IOException {
		URL u = new URL(request.getRequestURI());
		String s = u.getFile();
		int i = s.lastIndexOf('/');
		if (i > -1)
			s = s.substring(i + 1);
		if (s.equals(""))
			return "index.html";
		return s;
	}
}

package com.googlecode.httpfilter.proxy.rabbit.proxy;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * A class that starts up proxies.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class ProxyStarter {

	private static final String DEFAULT_CONFIG = "conf/rabbit.conf";

	/**
	 * Create the ProxyStarter and let it parse the command line arguments.
	 * 
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		ProxyStarter ps = new ProxyStarter();
		args = new String[]{"-f","/Users/vlinux/Workspaces/java/svnchina/httpfilter/httpfilter-proxy/conf/rabbit.conf"};
		ps.start(args);
	}

	/**
	 * print out the helptext to the user.
	 */
	private void printHelp() {
		try {
			byte[] b = new byte[4096];
			int i;
			InputStream f = ProxyStarter.class.getResourceAsStream("/Help.txt");
			try {
				while ((i = f.read(b)) > 0)
					System.out.write(b, 0, i);
			} finally {
				f.close();
			}
		} catch (IOException e) {
			System.err.println("Could not read help text: " + e);
		}
	}

	public void start(String[] args) {
		List<String> configs = new ArrayList<String>();
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-?") || args[i].equals("-h")
					|| args[i].equals("--help")) {
				printHelp();
				return;
			} else if (args[i].equals("-f") || args[i].equals("--file")) {
				i++;
				if (args.length > i) {
					configs.add(args[i]);
				} else {
					System.err.println("Missing config file on command line");
					return;
				}
			} else if (args[i].equals("-v") || args[i].equals("--version")) {
				System.out.println(HttpProxy.VERSION);
				return;
			}
		}
		if (configs.size() == 0)
			configs.add(DEFAULT_CONFIG);
		for (String conf : configs)
			startProxy(conf);
	}

	private void startProxy(String conf) {
		try {
			HttpProxy p = new HttpProxy();
			p.setConfig(conf);
			p.start();
		} catch (IOException e) {
			System.err.println("failed to configure proxy, ignoring: " + e);
			e.printStackTrace();
		}
	}
}

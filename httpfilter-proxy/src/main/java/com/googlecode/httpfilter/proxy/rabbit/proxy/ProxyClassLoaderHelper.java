package com.googlecode.httpfilter.proxy.rabbit.proxy;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class ProxyClassLoaderHelper {
	private final Logger logger = Logger.getLogger(getClass().getName());

	public ClassLoader get3rdPartyClassLoader(String dirLine) {
		String[] dirs = dirLine.split(File.pathSeparator);
		List<URL> urls = new ArrayList<URL>();
		FileFilter jarFilter = new JarFilter();
		for (String dir : dirs) {
			try {
				File d = new File(dir);
				if (!d.exists()) {
					logger.warning(d.getCanonicalPath()
							+ " does not exist, skipping it from "
							+ "3:rd party list");
					continue;
				}
				if (!d.isDirectory()) {
					logger.warning(d.getCanonicalPath()
							+ " is not a directory, skipping it from "
							+ "3:rd party list");
					continue;
				}
				for (File f : d.listFiles(jarFilter))
					urls.add(f.toURI().toURL());
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Failed to setup classloading", e);
			}
		}
		URL[] urlArray = urls.toArray(new URL[urls.size()]);
		if( urlArray.length == 0 ) {
			return Thread.currentThread().getContextClassLoader();
		}
		return new URLClassLoader(urlArray);
	}

	private static class JarFilter implements FileFilter {
		public boolean accept(File path) {
			String name = path.getName();
			return name.toLowerCase().endsWith(".jar");
		}
	}
}
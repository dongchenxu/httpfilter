package com.googlecode.httpfilter.proxy.rabbit.util;

// $Id: Config.java,v 1.7 2005/08/03 17:00:55 robo Exp $

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * a class to handle configs for different things. reads file on the format
 * 
 * <pre>
 *  [sectionName1]
 *  key=value
 *  key2=value2
 *  [sectionName2]
 *  key=value
 *  key2=value2
 *  key_with_equals\=still_key=value3
 * </pre>
 * 
 * everything after the first '#' is considered a comment. blank lines are
 * ignored. If you want keys with '=' in them escape it to '\=' and you should
 * be fine.
 */
public class Config {
	private Map<String, SProperties> configs;

	/**
	 * create an empty Config (has only section "" with no data in it)
	 */
	public Config() {
		configs = new HashMap<String, SProperties>();
		SProperties current = new SProperties(); // the main thing.
		configs.put("", current);
	}

	/**
	 * Create a Config for the specified file
	 * 
	 * @param filename
	 *            the File we read the config from
	 * @throws IOException
	 *             if the given file can not be read
	 */
	public Config(String filename) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		readConfig(br);
	}

	/**
	 * Create a Config for the specified file
	 * 
	 * @param file
	 *            the File we read the config from
	 * @throws IOException
	 *             if the given file can not be read
	 */
	public Config(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		readConfig(br);
	}

	/**
	 * Create a Config by reading it from a stream.
	 * 
	 * @param is
	 *            the stream to read the config from.
	 * @throws IOException
	 *             if the given input stream can not be read
	 */
	public Config(InputStream is) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		readConfig(br);
	}

	/**
	 * Create a Config by reading it from a reader.
	 * 
	 * @param reader
	 *            the Reader to read the config from.
	 * @throws IOException
	 *             if the given Reader can not be read
	 */
	public Config(Reader reader) throws IOException {
		BufferedReader br = new BufferedReader(reader);
		readConfig(br);
	}

	/**
	 * read in a Config from a reader.
	 * 
	 * @param br
	 *            the reader that has the Config.
	 * @throws IOException
	 *             if the given Reader can not be read
	 */
	private void readConfig(BufferedReader br) throws IOException {
		String line;
		configs = new HashMap<String, SProperties>();
		SProperties current = new SProperties(); // the main thing.
		configs.put("", current);
		while ((line = br.readLine()) != null) {
			int index = line.indexOf('#');
			if (index >= 0)
				line = line.substring(0, index);
			if (line.equals(""))
				continue;
			if (line.startsWith("[")) {
				int endindex = line.indexOf(']');
				if (endindex >= 0) {
					String newSection = line.substring(1, endindex);
					// collapse configs named equal.
					if (configs.get(newSection) != null)
						current = configs.get(newSection);
					else
						current = new SProperties();
					configs.put(newSection, current);
				}
			} else {
				boolean escaped;
				int start = 0;
				int eqindex;
				do {
					escaped = false;
					eqindex = line.indexOf('=', start);
					if (eqindex > 0 && line.charAt(eqindex - 1) == '\\') {
						escaped = true;
						start = eqindex;
						line = line.substring(0, eqindex - 1)
								+ line.substring(eqindex);
					}
				} while (escaped);
				String key, value = "";
				if (eqindex >= 0) {
					key = line.substring(0, eqindex);
					value = line.substring(eqindex + 1);
				} else
					key = line;
				current.put(key, value);
			}
		}
		br.close();
	}

	/**
	 * get the available sections
	 * 
	 * @return an Enumeration of the available sections (including the empty
	 *         section).
	 */
	public Collection<String> getSections() {
		return configs.keySet();
	}

	/**
	 * get the properties for a given section
	 * 
	 * @param sectionName
	 *            the section we want properties for.
	 * @return a SProperties if section exist or null.
	 */
	public SProperties getProperties(String sectionName) {
		SProperties sp = configs.get(sectionName);
		if (sp == null)
			// logging might not be set up at this point so just write
			System.err.println("'" + sectionName
					+ "' section missing from conf");
		return sp;
	}

	/**
	 * set the properties for a given section
	 * 
	 * @param sectionName
	 *            the section we want to set the properties for.
	 * @param prop
	 *            the SProperties for the sections
	 */
	public void setProperties(String sectionName, SProperties prop) {
		configs.put(sectionName, prop);
	}

	/**
	 * get a property for given key in specified section
	 * 
	 * @param section
	 *            the section we should look in.
	 * @param key
	 *            the key we want a value for.
	 * @return a string if section + key is set, null otherwise
	 */
	public String getProperty(String section, String key) {
		return getProperty(section, key, null);
	}

	/**
	 * get a property for given key in specified section
	 * 
	 * @param section
	 *            the section we should look in.
	 * @param key
	 *            the key we want a value for.
	 * @param defaultstring
	 *            the string to use if no value is found.
	 * @return a string if section + key is set, null otherwise
	 */
	public String getProperty(String section, String key, String defaultstring) {
		SProperties p = getProperties(section);
		if (p != null) {
			String s = p.get(key);
			if (s != null)
				return s;
		}
		return defaultstring;
	}

	/**
	 * set a property for given section.
	 * 
	 * @param section
	 *            the section we should look in.
	 * @param key
	 *            the key.
	 * @param value
	 *            the value.
	 */
	public void setProperty(String section, String key, String value) {
		SProperties p = getProperties(section);
		if (p == null) {
			p = new SProperties();
			configs.put(section, p);
		}
		p.put(key, value);
	}

	/**
	 * save the config to a OutputStream
	 * 
	 * @param os
	 *            the OutputStream to write to
	 */
	public void save(OutputStream os) {
		PrintWriter dos = new PrintWriter(os);
		for (String section : configs.keySet()) {
			dos.println("[" + section + "]");
			SProperties pr = configs.get(section);
			for (Map.Entry<String, String> me : pr.entrySet()) {
				String key = me.getKey();
				String value = me.getValue();
				if (value == null)
					value = "";
				StringTokenizer st = new StringTokenizer(key, "=", true);
				while (st.hasMoreTokens()) {
					String token = st.nextToken();
					if (token.equals("="))
						dos.print('\\');
					dos.print(token);
				}
				dos.println("=" + value);
			}
			dos.println("");
		}
		dos.flush();
	}

	/**
	 * Get a string describing this Config
	 */
	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		for (String section : configs.keySet()) {
			res.append('[');
			res.append(section);
			res.append(']');
			res.append('\n');
			SProperties pr = configs.get(section);
			for (Map.Entry<String, String> me : pr.entrySet()) {
				String key = me.getKey();
				String value = me.getValue();
				if (value == null)
					value = "";
				StringTokenizer st = new StringTokenizer(key, "=", true);
				while (st.hasMoreTokens()) {
					String token = st.nextToken();
					if (token.equals("="))
						res.append('\\');
					res.append(token);
				}
				res.append('=');
				res.append(value);
				res.append('\n');
			}
			res.append('\n');
		}
		return res.toString();
	}

	/**
	 * Merge this config with another one. that for every section/key in either
	 * of the two configs do: if this Config has the value use it otherwise use
	 * others value.
	 * 
	 * @param other
	 *            the Config to merge with.
	 */
	public void merge(Config other) {
		for (String section : other.getSections()) {
			SProperties p = other.getProperties(section);
			if (p == null)
				continue;
			for (Map.Entry<String, String> me : p.entrySet()) {
				String key = me.getKey();
				String value = me.getValue();
				String merged = getProperty(section, key, value);
				setProperty(section, key, merged);
			}
		}
	}
}

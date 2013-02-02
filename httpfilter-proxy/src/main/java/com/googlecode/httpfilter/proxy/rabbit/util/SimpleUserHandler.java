package com.googlecode.httpfilter.proxy.rabbit.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl.Closer;

/**
 * This is a class that handles users authentication using a simple text file.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class SimpleUserHandler {
	private String userFile = null;
	private Map<String, String> users = new HashMap<String, String>();
	private final Logger logger = Logger.getLogger(getClass().getName());

	/**
	 * Set the file to use for users, will read the files. Will discard any
	 * previous loaded users.
	 * 
	 * @param userFile
	 *            the filename to read the users from.
	 */
	public void setFile(String userFile) {
		this.userFile = userFile;

		FileReader fr = null;
		try {
			fr = new FileReader(userFile);
			users = loadUsers(fr);
		} catch (FileNotFoundException e) {
			logger.log(Level.WARNING, "could not load the users file: '"
					+ userFile, e);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Error while loading the users file: '"
					+ userFile, e);
		} finally {
			Closer.close(fr, logger);
		}
	}

	/**
	 * Load the users from the given Reader.
	 * 
	 * @param r
	 *            the Reader with the users.
	 * @return a Map with usernames and passwords
	 * @throws IOException
	 *             if reading the users fail
	 */
	public Map<String, String> loadUsers(Reader r) throws IOException {
		BufferedReader br = new BufferedReader(r);
		String line;
		Map<String, String> u = new HashMap<String, String>();
		while ((line = br.readLine()) != null) {
			String[] creds = line.split("[: \n\t]");
			if (creds.length != 2)
				continue;
			String name = creds[0];
			String pass = creds[1];
			u.put(name, pass);
		}
		return u;
	}

	/**
	 * Saves the users from the given Reader.
	 * 
	 * @param r
	 *            the Reader with the users.
	 * @throws IOException
	 *             if reading the users fail if writing users fails
	 */
	public void saveUsers(Reader r) throws IOException {
		if (userFile == null)
			return;
		BufferedReader br = new BufferedReader(r);
		PrintWriter fw = null;
		try {
			fw = new PrintWriter(new FileWriter(userFile));
			String line;
			while ((line = br.readLine()) != null)
				fw.println(line);
			fw.flush();
		} finally {
			if (fw != null) {
				fw.close();
			}
		}
	}

	/**
	 * Return the hash of users.
	 * 
	 * @return the Map of usernames to passwords
	 */
	public Map<String, String> getUsers() {
		return users;
	}

	/**
	 * Set the usernames and passwords to use for authentication.
	 * 
	 * @param users
	 *            the new set of usernames and passwords
	 */
	public void setUsers(Map<String, String> users) {
		this.users = users;
	}

	/**
	 * Check if a user/password combination is valid.
	 * 
	 * @param username
	 *            the username.
	 * @param password
	 *            the decrypted password.
	 * @return true if both username and password match a valid user.
	 */
	public boolean isValidUser(String username, String password) {
		if (username == null)
			return false;
		String pass = users.get(username);
		return (pass != null && password != null && pass.equals(password));
	}
}

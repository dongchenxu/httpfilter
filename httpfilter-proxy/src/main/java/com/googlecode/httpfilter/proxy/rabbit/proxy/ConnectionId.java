package com.googlecode.httpfilter.proxy.rabbit.proxy;

/**
 * The id for a connection.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class ConnectionId {
	private final int group;
	private final long id;

	/**
	 * Create a new identifier for a Connection
	 * 
	 * @param group
	 *            the grup id
	 * @param id
	 *            the id in the group
	 */
	public ConnectionId(int group, long id) {
		this.group = group;
		this.id = id;
	}

	@Override
	public String toString() {
		return "[" + group + ", " + id + "]";
	}
}

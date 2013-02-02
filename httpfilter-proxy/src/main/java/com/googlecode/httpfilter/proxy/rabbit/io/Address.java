package com.googlecode.httpfilter.proxy.rabbit.io;

import java.net.InetAddress;

/**
 * A class to handle the addresses of the connections. Basically just a pair of
 * InetAddress and port number.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class Address {
	/** The internet address of this Address. */
	private final InetAddress ia;
	/** The port number were connected to. */
	private final int port;
	/** The hash code. */
	private final int hash;

	/**
	 * Create a new Address with given InetAddress and port
	 * 
	 * @param ia
	 *            the InetAddress this Address is connected to.
	 * @param port
	 *            the port number this Address is connected to.
	 */
	public Address(InetAddress ia, int port) {
		this.ia = ia;
		this.port = port;
		String s = ia.getHostAddress() + ":" + port;
		hash = s.hashCode();
	}

	/**
	 * Get the hash code for this object.
	 * 
	 * @return the hash code.
	 */
	@Override
	public int hashCode() {
		return hash;
	}

	/**
	 * Compare this object against another object.
	 * 
	 * @param o
	 *            the Object to compare against.
	 * @return true if the other Object is an Address connected to the same
	 *         InetAddress and port, false otherwise.
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Address) {
			Address a = (Address) o;
			return (port == a.port && ia.equals(a.ia));
		}
		return false;
	}

	/** Get a String representation of this Address */
	@Override
	public String toString() {
		return ia + ":" + port;
	}

	/**
	 * Get the internet address.
	 * 
	 * @return the InetAddress for this Address
	 */
	public InetAddress getInetAddress() {
		return ia;
	}

	/**
	 * Get the port number.
	 * 
	 * @return the port number for this Address
	 */
	public int getPort() {
		return port;
	}
}

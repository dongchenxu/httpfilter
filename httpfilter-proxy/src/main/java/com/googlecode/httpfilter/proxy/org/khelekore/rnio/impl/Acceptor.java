package com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import com.googlecode.httpfilter.proxy.org.khelekore.rnio.AcceptHandler;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.NioHandler;

/**
 * A standard acceptor.
 * <p>
 * This AcceptHandler will never timeout, will never use a separate thread and
 * will keep accepting connections until you remove it.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class Acceptor extends SocketHandlerBase<ServerSocketChannel> implements
		AcceptHandler {
	private AcceptorListener listener;

	/**
	 * Create a new Acceptor that will wait for accepts on the given channel.
	 * 
	 * @param ssc
	 *            the channel to accept connections from
	 * @param nioHandler
	 *            the NioHandler to use for waiting
	 * @param listener
	 *            the listener waiting for connections
	 */
	public Acceptor(ServerSocketChannel ssc, NioHandler nioHandler,
			AcceptorListener listener) {
		super(ssc, nioHandler, null);
		this.listener = listener;
	}

	/**
	 * Returns the class name and the channel we are using.
	 */
	@Override
	public String getDescription() {
		return getClass().getSimpleName() + ": channel: " + sc;
	}

	/**
	 * Accept a SocketChannel.
	 */
	public void accept() {
		try {
			SocketChannel s = sc.accept();
			s.configureBlocking(false);
			listener.connectionAccepted(s);
			register();
		} catch (IOException e) {
			throw new RuntimeException("Got some IOException", e);
		}
	}

	/**
	 * Register OP_ACCEPT with the selector.
	 */
	public void register() {
		nioHandler.waitForAccept(sc, this);
	}
}

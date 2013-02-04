package com.googlecode.httpfilter.proxy.rabbit.proxy;

/**
 * an interface for listening on tunnel closedowns.
 */
interface TunnelDoneListener {
	void tunnelClosed();
}

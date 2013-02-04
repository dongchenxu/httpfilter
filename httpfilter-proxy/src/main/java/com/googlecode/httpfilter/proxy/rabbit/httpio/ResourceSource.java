package com.googlecode.httpfilter.proxy.rabbit.httpio;

/**
 * A resource source.
 * 
 * Use supportsTransfer to check if this resource supports transfer, if it does
 * then use the transferTo method. A resource that does not support transfer
 * will listen for blocks that are read, using a BlockListener.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface ResourceSource extends Transferable {

	/**
	 * Return true if FileChannel.transferTo can be used. Will generally only be
	 * true if the resource is served from a FileChannel.
	 * 
	 * @return true if transferTo can be used, false otherwise
	 */
	boolean supportsTransfer();

	/**
	 * Add a ByteBuffer listener.
	 * 
	 * @param bl
	 *            the listener that will get notified when data is available
	 */
	void addBlockListener(BlockListener bl);

	/**
	 * Release any held resources.
	 */
	void release();
}

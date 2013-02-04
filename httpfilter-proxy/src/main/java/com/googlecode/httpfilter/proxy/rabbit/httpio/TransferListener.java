package com.googlecode.httpfilter.proxy.rabbit.httpio;

/**
 * A listener for transfers.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface TransferListener {
	/**
	 * The transfer completed successfully.
	 */
	void transferOk();

	/**
	 * Reading failed
	 * 
	 * @param cause
	 *            the real reason the operation failed.
	 */
	void failed(Exception cause);
}

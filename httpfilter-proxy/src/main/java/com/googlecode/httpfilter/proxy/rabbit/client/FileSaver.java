package com.googlecode.httpfilter.proxy.rabbit.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.TaskIdentifier;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl.Closer;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl.DefaultTaskIdentifier;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.httpio.BlockListener;
import com.googlecode.httpfilter.proxy.rabbit.httpio.ResourceSource;
import com.googlecode.httpfilter.proxy.rabbit.io.BufferHandle;

/**
 * A class to save a ResourceSource into a file. This is mostly an example of
 * how to use the rabbit client classes.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class FileSaver implements BlockListener {
	private final HttpHeader request;
	private final ClientBase clientBase;
	private final ClientListener listener;
	private final ResourceSource rs;
	private final FileChannel fc;

	/**
	 * Create a new FileSaver that will write a resource to the given file.
	 * 
	 * @param request
	 *            the actual request
	 * @param clientBase
	 *            the client
	 * @param listener
	 *            the ClientListener to tell when the resource has been fully
	 *            handled
	 * @param rs
	 *            the resource to save
	 * @param f
	 *            where to store the resource
	 * @throws IOException
	 *             if the file can not be written
	 */
	public FileSaver(HttpHeader request, ClientBase clientBase,
			ClientListener listener, ResourceSource rs, File f)
			throws IOException {
		this.request = request;
		this.clientBase = clientBase;
		this.listener = listener;
		FileOutputStream fos = new FileOutputStream(f);
		fc = fos.getChannel();
		this.rs = rs;
	}

	public void bufferRead(final BufferHandle bufHandle) {
		TaskIdentifier ti = new DefaultTaskIdentifier(getClass()
				.getSimpleName(), request.getRequestURI());
		clientBase.getNioHandler().runThreadTask(new Runnable() {
			public void run() {
				try {
					ByteBuffer buf = bufHandle.getBuffer();
					fc.write(buf);
					bufHandle.possiblyFlush();
					readMore();
				} catch (IOException e) {
					failed(e);
				}
			}
		}, ti);
	}

	private void readMore() {
		rs.addBlockListener(FileSaver.this);
	}

	public void finishedRead() {
		rs.release();
		try {
			fc.close();
			listener.requestDone(request);
		} catch (IOException e) {
			listener.handleFailure(request, e);
		}
	}

	public void failed(Exception cause) {
		downloadFailed();
		listener.handleFailure(request, cause);
	}

	public void timeout() {
		downloadFailed();
		listener.handleTimeout(request);
	}

	private void downloadFailed() {
		rs.release();
		Closer.close(fc, clientBase.getLogger());
	}
}

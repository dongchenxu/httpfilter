package com.googlecode.httpfilter.proxy.rabbit.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.ReadHandler;
import com.googlecode.httpfilter.proxy.rabbit.io.BufferHandle;
import com.googlecode.httpfilter.proxy.rabbit.io.WebConnection;

/**
 * A base for client resource transfer classes.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
abstract class ResourceHandlerBase implements ClientResourceHandler {
	protected final Connection con;
	protected final BufferHandle bufHandle;
	protected final TrafficLoggerHandler tlh;
	protected WebConnection wc;
	protected ClientResourceTransferredListener listener;
	private List<ClientResourceListener> resourceListeners;

	public ResourceHandlerBase(Connection con, BufferHandle bufHandle,
			TrafficLoggerHandler tlh) {
		this.con = con;
		this.bufHandle = bufHandle;
		this.tlh = tlh;
	}

	/**
	 * Will store the variables and call doTransfer ()
	 */
	public void transfer(WebConnection wc,
			ClientResourceTransferredListener crtl) {
		this.wc = wc;
		this.listener = crtl;
		doTransfer();
	}

	protected void doTransfer() {
		if (!bufHandle.isEmpty())
			sendBuffer();
		else
			waitForRead();
	}

	public void addContentListener(ClientResourceListener crl) {
		if (resourceListeners == null)
			resourceListeners = new ArrayList<ClientResourceListener>();
		resourceListeners.add(crl);
	}

	public void fireResouceDataRead(BufferHandle bufHandle) {
		if (resourceListeners == null)
			return;
		for (ClientResourceListener crl : resourceListeners) {
			crl.resourceDataRead(bufHandle);
		}
	}

	abstract void sendBuffer();

	protected void waitForRead() {
		bufHandle.possiblyFlush();
		ReadHandler sh = new Reader();
		con.getNioHandler().waitForRead(con.getChannel(), sh);
	}

	private class Reader implements ReadHandler {
		private final Long timeout = con.getNioHandler().getDefaultTimeout();

		public void read() {
			try {
				ByteBuffer buffer = bufHandle.getBuffer();
				int read = con.getChannel().read(buffer);
				if (read == 0) {
					waitForRead();
				} else if (read == -1) {
					failed(new IOException("Failed to read request"));
				} else {
					tlh.getClient().read(read);
					buffer.flip();
					sendBuffer();
				}
			} catch (IOException e) {
				listener.failed(e);
			}
		}

		public void closed() {
			bufHandle.possiblyFlush();
			listener.failed(new IOException("Connection closed"));
		}

		public void timeout() {
			bufHandle.possiblyFlush();
			listener.timeout();
		}

		public boolean useSeparateThread() {
			return false;
		}

		public String getDescription() {
			return toString();
		}

		public Long getTimeout() {
			return timeout;
		}
	}

	public void timeout() {
		bufHandle.possiblyFlush();
		listener.timeout();
	}

	public void failed(Exception e) {
		bufHandle.possiblyFlush();
		listener.failed(e);
	}
}

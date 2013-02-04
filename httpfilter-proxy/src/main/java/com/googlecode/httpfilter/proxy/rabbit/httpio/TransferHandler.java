package com.googlecode.httpfilter.proxy.rabbit.httpio;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.NioHandler;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.WriteHandler;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl.DefaultTaskIdentifier;
import com.googlecode.httpfilter.proxy.rabbit.io.Address;
import com.googlecode.httpfilter.proxy.rabbit.util.TrafficLogger;

/**
 * A handler that transfers data from a Transferable to a socket channel. Since
 * file transfers may take time we run in a separate thread.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class TransferHandler implements Runnable {
	private final NioHandler nioHandler;
	private final Transferable t;
	private final SocketChannel channel;
	private final TrafficLogger tlFrom;
	private final TrafficLogger tlTo;
	private final TransferListener listener;
	private long pos = 0;
	private long count;

	/**
	 * Create a new TransferHandler
	 * 
	 * @param nioHandler
	 *            the NioHandler to use for network and background tasks
	 * @param t
	 *            the resource to transfer
	 * @param channel
	 *            the Channel to transfer the resource to
	 * @param tlFrom
	 *            the network statistics for the source
	 * @param tlTo
	 *            the network statistics for the sink
	 * @param listener
	 *            the listener that will be notified when the transfer has
	 *            completed
	 */
	public TransferHandler(NioHandler nioHandler, Transferable t,
			SocketChannel channel, TrafficLogger tlFrom, TrafficLogger tlTo,
			TransferListener listener) {
		this.nioHandler = nioHandler;
		this.t = t;
		this.channel = channel;
		this.tlFrom = tlFrom;
		this.tlTo = tlTo;
		this.listener = listener;
		count = t.length();
	}

	/**
	 * Start the data transfer.
	 */
	public void transfer() {
		String groupId = getClass().getSimpleName();
		String desc = "Transferable: " + t + ", chanel: " + channel
				+ ", listener: " + listener;
		nioHandler
				.runThreadTask(this, new DefaultTaskIdentifier(groupId, desc));
	}

	public void run() {
		try {
			while (count > 0) {
				long written = t.transferTo(pos, count, channel);
				pos += written;
				count -= written;
				tlFrom.transferFrom(written);
				tlTo.transferTo(written);
				if (count > 0 && written == 0) {
					setupWaitForWrite();
					return;
				}
			}
			listener.transferOk();
		} catch (IOException e) {
			listener.failed(e);
		}
	}

	private void setupWaitForWrite() {
		nioHandler.waitForWrite(channel, new WriteWaiter());
	}

	private class WriteWaiter implements WriteHandler {
		private final Long timeout = nioHandler.getDefaultTimeout();

		public void closed() {
			listener.failed(new IOException("channel closed"));
		}

		public void timeout() {
			listener.failed(new IOException("write timed out"));
		}

		public boolean useSeparateThread() {
			return true;
		}

		public String getDescription() {
			Socket s = channel.socket();
			Address a = new Address(s.getInetAddress(), s.getPort());
			return "TransferHandler$WriteWaiter: address: " + a;
		}

		public Long getTimeout() {
			return timeout;
		}

		public void write() {
			TransferHandler.this.run();
		}
	}
}

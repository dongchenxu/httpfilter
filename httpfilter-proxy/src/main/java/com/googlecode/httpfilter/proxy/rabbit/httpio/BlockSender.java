package com.googlecode.httpfilter.proxy.rabbit.httpio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import com.googlecode.httpfilter.proxy.rabbit.io.BufferHandle;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.NioHandler;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.WriteHandler;
import com.googlecode.httpfilter.proxy.rabbit.util.TrafficLogger;

/**
 * A handler that writes data blocks.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class BlockSender extends BaseSocketHandler implements WriteHandler {
	private ByteBuffer chunkBuffer;
	private final ByteBuffer end;
	private final ByteBuffer[] buffers;
	private final TrafficLogger tl;
	private final BlockSentListener sender;

	/**
	 * Create a new BlockSender that will write data to the given channel
	 * 
	 * @param channel
	 *            the SocketChannel to write the data to
	 * @param nioHandler
	 *            the NioHandler to use to wait for write ready
	 * @param tl
	 *            the traffic statistics gatherer
	 * @param bufHandle
	 *            the data to write
	 * @param chunking
	 *            if true chunk the data out
	 * @param sender
	 *            the listener that will be notified when the data has been
	 *            handled.
	 */
	public BlockSender(SocketChannel channel, NioHandler nioHandler,
			TrafficLogger tl, BufferHandle bufHandle, boolean chunking,
			BlockSentListener sender) {
		super(channel, bufHandle, nioHandler);
		this.tl = tl;
		ByteBuffer buffer = bufHandle.getBuffer();
		if (chunking) {
			int len = buffer.remaining();
			String s = Long.toHexString(len) + "\r\n";
			try {
				chunkBuffer = ByteBuffer.wrap(s.getBytes("ASCII"));
			} catch (UnsupportedEncodingException e) {
				getLogger().log(Level.WARNING, "BlockSender: ASCII not found!",
						e);
			}
			end = ByteBuffer.wrap(new byte[] { '\r', '\n' });
			buffers = new ByteBuffer[] { chunkBuffer, buffer, end };
		} else {
			buffers = new ByteBuffer[] { buffer };
			end = buffer;
		}
		this.sender = sender;
	}

	@Override
	public String getDescription() {
		StringBuilder sb = new StringBuilder("BlockSender: buffers: "
				+ buffers.length);
		for (int i = 0; i < buffers.length; i++) {
			if (i > 0)
				sb.append(", ");
			sb.append("i: ").append(buffers[i].remaining());
		}
		return sb.toString();
	}

	@Override
	public void timeout() {
		releaseBuffer();
		sender.timeout();
	}

	@Override
	public void closed() {
		releaseBuffer();
		sender.failed(new IOException("channel was closed"));
	}

	public void write() {
		try {
			writeBuffer();
		} catch (IOException e) {
			releaseBuffer();
			sender.failed(e);
		}
	}

	private void writeBuffer() throws IOException {
		long written;
		do {
			written = getChannel().write(buffers);
			tl.write(written);
		} while (written > 0 && end.remaining() > 0);

		if (end.remaining() == 0) {
			releaseBuffer();
			sender.blockSent();
		} else {
			waitForWrite(this);
		}
	}
}

package com.googlecode.httpfilter.proxy.rabbit.httpio;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.NioHandler;
import com.googlecode.httpfilter.proxy.rabbit.io.BufferHandle;
import com.googlecode.httpfilter.proxy.rabbit.io.SimpleBufferHandle;
import com.googlecode.httpfilter.proxy.rabbit.util.TrafficLogger;

/**
 * A class that sends the chunk ending (with an empty footer).
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class ChunkEnder {
	private static final byte[] CHUNK_ENDING = new byte[] { '0', '\r', '\n',
			'\r', '\n' };

	/**
	 * Send the chunk ending block.
	 * 
	 * @param channel
	 *            the Channel to send the chunk ender to
	 * @param nioHandler
	 *            the NioHandler to use for network operations
	 * @param tl
	 *            the TrafficLogger to update with network statistics
	 * @param bsl
	 *            the listener that will be notified when the sending is
	 *            complete
	 */
	public void sendChunkEnding(SocketChannel channel, NioHandler nioHandler,
			TrafficLogger tl, BlockSentListener bsl) {
		ByteBuffer bb = ByteBuffer.wrap(CHUNK_ENDING);
		BufferHandle bh = new SimpleBufferHandle(bb);
		BlockSender bs = new BlockSender(channel, nioHandler, tl, bh, false,
				bsl);
		bs.write();
	}
}

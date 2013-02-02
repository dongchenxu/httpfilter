package com.googlecode.httpfilter.proxy.rabbit.proxy;

import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.httpio.BlockListener;
import com.googlecode.httpfilter.proxy.rabbit.httpio.BlockSender;
import com.googlecode.httpfilter.proxy.rabbit.httpio.BlockSentListener;
import com.googlecode.httpfilter.proxy.rabbit.httpio.ChunkDataFeeder;
import com.googlecode.httpfilter.proxy.rabbit.httpio.ChunkEnder;
import com.googlecode.httpfilter.proxy.rabbit.httpio.ChunkHandler;
import com.googlecode.httpfilter.proxy.rabbit.io.BufferHandle;

/**
 * A handler that transfers chunked request resources. Will chunk data to the
 * real server or fail. Note that we can only do this if we know that the
 * upstream server is HTTP/1.1 compatible.
 * 
 * How do we determine if upstream is HTTP/1.1 compatible? If we can not then we
 * have to add a Content-Length header and not chunk, That means we have to
 * buffer the full resource.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class ChunkedContentTransferHandler extends ResourceHandlerBase implements
		ChunkDataFeeder, BlockListener, BlockSentListener {

	private boolean sentEndChunk = false;
	private final ChunkHandler chunkHandler;

	public ChunkedContentTransferHandler(Connection con,
			BufferHandle bufHandle, TrafficLoggerHandler tlh) {
		super(con, bufHandle, tlh);
		chunkHandler = new ChunkHandler(this, con.getProxy().getStrictHttp());
		chunkHandler.setBlockListener(this);
	}

	public void modifyRequest(HttpHeader header) {
		header.setHeader("Transfer-Encoding", "chunked");
	}

	@Override
	void sendBuffer() {
		chunkHandler.handleData(bufHandle);
	}

	public void bufferRead(BufferHandle bufHandle) {
		fireResouceDataRead(bufHandle);
		BlockSender bs = new BlockSender(wc.getChannel(), con.getNioHandler(),
				tlh.getNetwork(), bufHandle, true, this);
		bs.write();
	}

	public void finishedRead() {
		ChunkEnder ce = new ChunkEnder();
		sentEndChunk = true;
		ce.sendChunkEnding(wc.getChannel(), con.getNioHandler(),
				tlh.getNetwork(), this);
	}

	public void register() {
		waitForRead();
	}

	public void readMore() {
		if (!bufHandle.isEmpty())
			bufHandle.getBuffer().compact();
		register();
	}

	public void blockSent() {
		if (sentEndChunk)
			listener.clientResourceTransferred();
		else
			doTransfer();
	}
}

package com.googlecode.httpfilter.proxy.rabbit.handler;

import java.nio.ByteBuffer;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.httpio.ResourceSource;
import com.googlecode.httpfilter.proxy.rabbit.io.BufferHandle;
import com.googlecode.httpfilter.proxy.rabbit.io.SimpleBufferHandle;
import com.googlecode.httpfilter.proxy.rabbit.proxy.Connection;
import com.googlecode.httpfilter.proxy.rabbit.proxy.HttpProxy;
import com.googlecode.httpfilter.proxy.rabbit.proxy.TrafficLoggerHandler;
import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;
import com.googlecode.httpfilter.proxy.rabbit.zip.GZipPackListener;
import com.googlecode.httpfilter.proxy.rabbit.zip.GZipPacker;

/**
 * This handler compresses the data passing through it.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class GZipHandler extends BaseHandler {
	protected boolean compress = true;
	private boolean isCompressing = false;
	private boolean compressionFinished = false;
	private boolean compressedDataFinished = false;
	private GZipPacker packer = null;

	/**
	 * For creating the factory.
	 */
	public GZipHandler() {
		// empty
	}

	/**
	 * Create a new GZipHandler for the given request.
	 * 
	 * @param con
	 *            the Connection handling the request.
	 * @param tlh
	 *            the TrafficLoggerHandler to update with traffic information
	 * @param request
	 *            the actual request made.
	 * @param response
	 *            the actual response.
	 * @param content
	 *            the resource.
	 * @param mayCache
	 *            May we cache this request?
	 * @param mayFilter
	 *            May we filter this request?
	 * @param size
	 *            the size of the data beeing handled.
	 * @param compress
	 *            if we want this handler to compress or not.
	 */
	public GZipHandler(Connection con, TrafficLoggerHandler tlh,
			HttpHeader request, HttpHeader response, ResourceSource content,
			boolean mayCache, boolean mayFilter, long size, boolean compress) {
		super(con, tlh, request, response, content, mayCache, mayFilter, size);
		this.compress = compress;
	}

	protected void setupHandler() {
		if (compress) {
			isCompressing = willCompress();
			if (isCompressing) {
				response.removeHeader("Content-Length");
				response.setHeader("Content-Encoding", "gzip");
				if (!con.getChunking())
					con.setKeepalive(false);
			} else {
				mayFilter = false;
			}
		}
	}

	protected boolean willCompress() {
		String ce = response.getHeader("Content-Encoding");
		if (ce == null)
			return true;
		ce = ce.toLowerCase();
		return !(ce.equals("gzip") || ce.equals("deflate"));
	}

	@Override
	public Handler getNewInstance(Connection con, TrafficLoggerHandler tlh,
			HttpHeader header, HttpHeader webHeader, ResourceSource content,
			boolean mayCache, boolean mayFilter, long size) {
		GZipHandler h = new GZipHandler(con, tlh, header, webHeader, content,
				mayCache, mayFilter, size, compress && mayFilter);
		h.setupHandler();
		return h;
	}

	/**
	 * ®return true this handler modifies the content.
	 */
	@Override
	public boolean changesContentSize() {
		return true;
	}

	@Override
	protected void prepare() {
		if (isCompressing) {
			GZipPackListener pl = new PListener();
			packer = new GZipPacker(pl);
			if (!packer.needsInput())
				packer.handleCurrentData();
			else
				super.prepare();
		} else {
			super.prepare();
		}
	}

	private class PListener implements GZipPackListener {
		private byte[] buffer;

		public byte[] getBuffer() {
			if (buffer == null)
				buffer = new byte[4096];
			return buffer;
		}

		public void packed(byte[] buf, int off, int len) {
			if (len > 0) {
				ByteBuffer bb = ByteBuffer.wrap(buf, off, len);
				BufferHandle bufHandle = new SimpleBufferHandle(bb);
				GZipHandler.super.bufferRead(bufHandle);
			} else {
				blockSent();
			}
		}

		public void dataPacked() {
			// do not really care...
		}

		public void finished() {
			compressedDataFinished = true;
		}

		public void failed(Exception e) {
			GZipHandler.this.failed(e);
		}
	}

	@Override
	protected void finishData() {
		if (isCompressing) {
			packer.finish();
			compressionFinished = true;
			sendEndBuffers();
		} else {
			super.finishData();
		}
	}

	private void sendEndBuffers() {
		if (packer.finished()) {
			super.finishData();
		} else {
			packer.handleCurrentData();
		}
	}

	/**
	 * Check if this handler supports direct transfers.
	 * 
	 * @return this handler always return false.
	 */
	@Override
	protected boolean mayTransfer() {
		return false;
	}

	@Override
	public void blockSent() {
		if (packer == null)
			super.blockSent();
		else if (compressedDataFinished)
			super.finishData();
		else if (compressionFinished)
			sendEndBuffers();
		else if (packer.needsInput())
			waitForData();
		else
			packer.handleCurrentData();
	}

	protected void waitForData() {
		content.addBlockListener(this);
	}

	/**
	 * Write the current block of data to the gzipper. If you override this
	 * method you probably want to override the modifyBuffer(ByteBuffer) as
	 * well.
	 * 
	 * @param arr
	 *            the data to write to the gzip stream.
	 */
	protected void writeDataToGZipper(byte[] arr) {
		packer.setInput(arr, 0, arr.length);
		if (packer.needsInput())
			waitForData();
		else
			packer.handleCurrentData();
	}

	/**
	 * This method is used when we are not compressing data. This method will
	 * just call "super.bufferRead (buf);"
	 * 
	 * @param bufHandle
	 *            the handle to the buffer that just was read.
	 */
	protected void modifyBuffer(BufferHandle bufHandle) {
		super.bufferRead(bufHandle);
	}

	protected void send(BufferHandle bufHandle) {
		if (isCompressing) {
			ByteBuffer buf = bufHandle.getBuffer();
			byte[] arr = buf.array();
			int pos = buf.position();
			int len = buf.remaining();
			packer.setInput(arr, pos, len);
			if (!packer.needsInput())
				packer.handleCurrentData();
			else
				blockSent();
		} else {
			super.bufferRead(bufHandle);
		}
	}

	@Override
	public void bufferRead(BufferHandle bufHandle) {
		if (con == null) {
			// not sure why this can happen, client has closed connection?
			return;
		}
		if (isCompressing) {
			// we normally have direct buffers and we can not use
			// array() on them. Create a new byte[] and copy data into it.
			byte[] arr;
			ByteBuffer buf = bufHandle.getBuffer();
			totalRead += buf.remaining();
			if (buf.isDirect()) {
				arr = new byte[buf.remaining()];
				buf.get(arr);
			} else {
				arr = buf.array();
				buf.position(buf.limit());
			}
			bufHandle.possiblyFlush();
			writeDataToGZipper(arr);
		} else {
			modifyBuffer(bufHandle);
		}
	}

	@Override
	public void setup(SProperties prop, HttpProxy proxy) {
		super.setup(prop, proxy);
		if (prop != null) {
			String comp = prop.getProperty("compress", "true");
			compress = !comp.equalsIgnoreCase("false");
		}
	}
}

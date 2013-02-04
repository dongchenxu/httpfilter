package com.googlecode.httpfilter.proxy.rabbit.meta;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.httpio.BlockSender;
import com.googlecode.httpfilter.proxy.rabbit.httpio.BlockSentListener;
import com.googlecode.httpfilter.proxy.rabbit.httpio.ChunkEnder;
import com.googlecode.httpfilter.proxy.rabbit.io.BufferHandle;
import com.googlecode.httpfilter.proxy.rabbit.io.SimpleBufferHandle;
import com.googlecode.httpfilter.proxy.rabbit.proxy.Connection;
import com.googlecode.httpfilter.proxy.rabbit.proxy.HtmlPage;
import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;
import com.googlecode.httpfilter.proxy.rabbit.util.TrafficLogger;

/**
 * A base class for meta handlers.
 * 
 * This meta handler will send a http header that say that the content is
 * chunked. Then
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public abstract class BaseMetaHandler implements MetaHandler, BlockSentListener {
	protected HttpHeader request;
	protected SProperties htab;
	protected Connection con;
	protected TrafficLogger tlProxy;
	protected TrafficLogger tlClient;
	private boolean first = true;
	protected final Logger logger = Logger.getLogger(getClass().getName());

	private static enum Mode {
		SEND_HEADER, SEND_DATA, CLEANUP
	}

	private Mode mode = Mode.SEND_HEADER;

	/** The states of the generated page */
	public static enum PageCompletion {
		/**
		 * Used to signal that the page is not yet finished and that more
		 * content will be added.
		 */
		PAGE_NOT_DONE,
		/** Used to signal that the page is finished */
		PAGE_DONE;
	}

	public void handle(HttpHeader request, SProperties htab, Connection con,
			TrafficLogger tlProxy, TrafficLogger tlClient) throws IOException {
		this.request = request;
		this.htab = htab;
		this.con = con;
		this.tlProxy = tlProxy;
		this.tlClient = tlClient;
		HttpHeader response = con.getHttpGenerator().getHeader();
		response.setHeader("Transfer-Encoding", "Chunked");
		byte[] b2 = response.toString().getBytes("ASCII");
		ByteBuffer buffer = ByteBuffer.wrap(b2);
		BufferHandle bh = new SimpleBufferHandle(buffer);
		BlockSender bs = new BlockSender(con.getChannel(), con.getNioHandler(),
				tlClient, bh, false, this);
		bs.write();
	}

	public void blockSent() {
		try {
			switch (mode) {
			case CLEANUP:
				cleanup();
				break;
			case SEND_DATA:
				endChunking();
				break;
			case SEND_HEADER:
				buildAndSendData();
				break;
			default:
				failed(new RuntimeException("Odd mode: " + mode));
			}
		} catch (IOException e) {
			failed(e);
		}
	}

	protected void cleanup() {
		con.logAndTryRestart();
	}

	protected void endChunking() {
		mode = Mode.CLEANUP;
		ChunkEnder ce = new ChunkEnder();
		ce.sendChunkEnding(con.getChannel(), con.getNioHandler(), tlClient,
				this);
	}

	protected void buildAndSendData() throws IOException {
		StringBuilder sb = new StringBuilder(2048);
		if (first) {
			sb.append(HtmlPage.getPageHeader(con, getPageHeader()));
			first = false;
		}
		if (addPageInformation(sb) == PageCompletion.PAGE_DONE) {
			sb.append("\n</body></html>");
			mode = Mode.SEND_DATA;
		}
		byte[] b1 = sb.toString().getBytes("ASCII");
		ByteBuffer data = ByteBuffer.wrap(b1);
		BufferHandle bh = new SimpleBufferHandle(data);
		BlockSender bs = new BlockSender(con.getChannel(), con.getNioHandler(),
				tlClient, bh, true, this);
		bs.write();
	}

	/**
	 * Get the page header name
	 * 
	 * @return the html for the page header
	 */
	protected abstract String getPageHeader();

	/**
	 * Add the page information
	 * 
	 * @param sb
	 *            The page being build.
	 * @return the current status of the page.
	 */
	protected abstract PageCompletion addPageInformation(StringBuilder sb);

	public void failed(Exception e) {
		logger.log(Level.WARNING, "Exception when handling meta", e);
		con.logAndClose();
	}

	public void timeout() {
		logger.warning("Timeout when handling meta.");
		con.logAndClose();
	}
}

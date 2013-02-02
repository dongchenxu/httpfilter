package com.googlecode.httpfilter.proxy.rabbit.meta;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl.Closer;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpDateParser;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.httpio.HttpHeaderSender;
import com.googlecode.httpfilter.proxy.rabbit.httpio.HttpHeaderSentListener;
import com.googlecode.httpfilter.proxy.rabbit.httpio.TransferHandler;
import com.googlecode.httpfilter.proxy.rabbit.httpio.TransferListener;
import com.googlecode.httpfilter.proxy.rabbit.httpio.Transferable;
import com.googlecode.httpfilter.proxy.rabbit.proxy.Connection;
import com.googlecode.httpfilter.proxy.rabbit.util.MimeTypeMapper;
import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;
import com.googlecode.httpfilter.proxy.rabbit.util.TrafficLogger;

/**
 * A file resource handler.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class FileSender implements MetaHandler, HttpHeaderSentListener {
	private Connection con;
	private TrafficLogger tlClient;
	private TrafficLogger tlProxy;
	private FileInputStream fis;
	private FileChannel fc;
	private long length;
	private final Logger logger = Logger.getLogger(getClass().getName());

	public void handle(HttpHeader request, SProperties htab, Connection con,
			TrafficLogger tlProxy, TrafficLogger tlClient) throws IOException {
		this.con = con;
		this.tlProxy = tlProxy;
		this.tlClient = tlClient;

		String file = htab.getProperty("argstring");
		if (file == null)
			throw (new IllegalArgumentException("no file given."));
		if (file.indexOf("..") >= 0) // file is un-url-escaped
			throw (new IllegalArgumentException("Bad filename given"));

		String filename = "htdocs/" + file;
		if (filename.endsWith("/"))
			filename = filename + "index.html";
		filename = filename.replace('/', File.separatorChar);

		File fle = new File(filename);
		if (!fle.exists()) {
			// remove htdocs
			do404(filename.substring(7));
			return;
		}

		// TODO: check etag/if-modified-since and handle it.
		HttpHeader response = con.getHttpGenerator().getHeader();
		setMime(filename, response);

		length = fle.length();
		response.setHeader("Content-Length", Long.toString(length));
		con.setContentLength(response.getHeader("Content-Length"));
		Date lm = new Date(fle.lastModified() - con.getProxy().getOffset());
		response.setHeader("Last-Modified", HttpDateParser.getDateString(lm));
		try {
			fis = new FileInputStream(filename);
		} catch (IOException e) {
			throw (new IllegalArgumentException("Could not open file: '" + file
					+ "'."));
		}
		sendHeader(response);
	}

	private void setMime(String filename, HttpHeader response) {
		// TODO: better filename mapping.
		String type = MimeTypeMapper.getMimeType(filename);
		if (type != null)
			response.setHeader("Content-type", type);
	}

	private void do404(String filename) throws IOException {
		HttpHeader response = con.getHttpGenerator().get404(filename);
		sendHeader(response);
	}

	private void sendHeader(HttpHeader header) throws IOException {
		HttpHeaderSender hhs = new HttpHeaderSender(con.getChannel(),
				con.getNioHandler(), tlClient, header, true, this);
		hhs.sendHeader();
	}

	/**
	 * Write the header and the file to the output.
	 */
	private void channelTransfer(long length) {
		TransferListener ftl = new FileTransferListener();
		TransferHandler th = new TransferHandler(con.getNioHandler(),
				new FCTransferable(length), con.getChannel(), tlProxy,
				tlClient, ftl);
		th.transfer();
	}

	private class FCTransferable implements Transferable {
		private final long length;

		public FCTransferable(long length) {
			this.length = length;
		}

		public long transferTo(long position, long count,
				WritableByteChannel target) throws IOException {
			return fc.transferTo(position, count, target);
		}

		public long length() {
			return length;
		}
	}

	private class FileTransferListener implements TransferListener {
		public void transferOk() {
			closeFile();
			con.logAndTryRestart();
		}

		public void failed(Exception cause) {
			closeFile();
			FileSender.this.failed(cause);
		}
	}

	private void closeFile() {
		Closer.close(fc, logger);
		Closer.close(fis, logger);
	}

	public void httpHeaderSent() {
		if (fis != null) {
			fc = fis.getChannel();
			channelTransfer(length);
		} else {
			con.logAndTryRestart();
		}
	}

	public void failed(Exception e) {
		closeFile();
		logger.log(Level.WARNING, "Exception when handling meta", e);
		con.logAndClose();
	}

	public void timeout() {
		closeFile();
		logger.warning("Timeout when handling meta.");
		con.logAndClose();
	}
}

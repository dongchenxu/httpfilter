package com.googlecode.httpfilter.proxy.rabbit.handler;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import com.googlecode.httpfilter.proxy.rabbit.filter.HtmlFilter;
import com.googlecode.httpfilter.proxy.rabbit.filter.HtmlFilterFactory;
import com.googlecode.httpfilter.proxy.rabbit.html.HtmlBlock;
import com.googlecode.httpfilter.proxy.rabbit.html.HtmlParseException;
import com.googlecode.httpfilter.proxy.rabbit.html.HtmlParser;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.httpio.ResourceSource;
import com.googlecode.httpfilter.proxy.rabbit.io.BufferHandle;
import com.googlecode.httpfilter.proxy.rabbit.io.SimpleBufferHandle;
import com.googlecode.httpfilter.proxy.rabbit.proxy.Connection;
import com.googlecode.httpfilter.proxy.rabbit.proxy.HttpProxy;
import com.googlecode.httpfilter.proxy.rabbit.proxy.TrafficLoggerHandler;
import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;
import com.googlecode.httpfilter.proxy.rabbit.zip.GZipUnpackListener;
import com.googlecode.httpfilter.proxy.rabbit.zip.GZipUnpacker;

/**
 * This handler filters out unwanted html features.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class FilterHandler extends GZipHandler {
	private List<HtmlFilterFactory> filterClasses = new ArrayList<HtmlFilterFactory>();
	private boolean repack = false;
	private String defaultCharSet = null;
	private String overrideCharSet = null;

	private List<HtmlFilter> filters;
	private HtmlParser parser;
	private byte[] restBlock = null;
	private boolean sendingRest = false;
	private Iterator<ByteBuffer> sendBlocks = null;

	private GZipUnpacker gzu = null;
	private GZListener gzListener = null;

	/**
	 * Create a new FilterHandler that is uninitialized. Normally this should
	 * only be used for the factory creation.
	 */
	public FilterHandler() {
		// empty
	}

	/**
	 * Create a new FilterHandler for the given request.
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
	 * @param repack
	 *            if true unpack, filter and possibly repack compressed
	 *            resources.
	 * @param filterClasses
	 *            the filters to use
	 */
	public FilterHandler(Connection con, TrafficLoggerHandler tlh,
			HttpHeader request, HttpHeader response, ResourceSource content,
			boolean mayCache, boolean mayFilter, long size, boolean compress,
			boolean repack, List<HtmlFilterFactory> filterClasses) {
		super(con, tlh, request, response, content, mayCache, mayFilter, size,
				compress);
		this.repack = repack;
		this.filterClasses = filterClasses;
	}

	@Override
	protected void setupHandler() {
		String ce = response.getHeader("Content-Encoding");
		if (repack && ce != null)
			setupRepacking(ce);

		super.setupHandler();
		if (mayFilter) {
			response.removeHeader("Content-Length");

			String cs;
			if (overrideCharSet != null) {
				cs = overrideCharSet;
			} else {
				cs = tryToGetCharset();
			}
			// There are lots of other charsets, and it could be specified by a
			// HTML Meta tag.
			// And it might be specified incorrectly for the actual page.
			// http://www.w3.org/International/O-HTTP-charset

			// default fron conf file
			// then look for HTTP charset
			// then look for HTML Meta charset, maybe re-decode
			// <META content="text/html; charset=gb2312"
			// http-equiv=Content-Type>
			// <meta http-equiv="content-type"
			// content="text/html;charset=Shift_JIS" />

			Charset charSet;
			try {
				charSet = Charset.forName(cs);
			} catch (UnsupportedCharsetException e) {
				getLogger().warning("Bad CharSet: " + cs);
				charSet = Charset.forName("ISO-8859-1");
			}
			parser = new HtmlParser(charSet);
			filters = initFilters();
		}
	}

	private void setupRepacking(String ce) {
		ce = ce.toLowerCase();
		if (ce.equals("gzip")) {
			gzListener = new GZListener();
			gzu = new GZipUnpacker(gzListener, false);
		} else if (ce.equals("deflate")) {
			gzListener = new GZListener();
			gzu = new GZipUnpacker(gzListener, true);
		} else {
			getLogger().warning("Do not know how to handle encoding: " + ce);
		}
		if (gzu != null && !compress) {
			response.removeHeader("Content-Encoding");
		}
	}

	private String tryToGetCharset() {
		String cs = defaultCharSet;
		// Content-Type: text/html; charset=iso-8859-1
		String ct = response.getHeader("Content-Type");
		if (ct != null) {
			String look = "charset=";
			int beginIndex = ct.indexOf(look);
			if (beginIndex > 0) {
				beginIndex += look.length();
				String charSet = ct.substring(beginIndex).trim();
				charSet = charSet.replace("_", "").replace("-", "");
				if (charSet.equalsIgnoreCase("iso88591")) {
					cs = "ISO8859_1";
				} else {
					cs = charSet;
				}
					
			}
		}
		return cs;
	}

	@Override
	protected boolean willCompress() {
		return gzu != null || super.willCompress();
	}

	private class GZListener implements GZipUnpackListener {
		private boolean gotData = false;
		private final byte[] buffer = new byte[4096];

		public void unpacked(byte[] buf, int off, int len) {
			gotData = true;
			handleArray(buf, off, len);
		}

		public void clearDataFlag() {
			gotData = false;
		}

		public boolean gotData() {
			return gotData;
		}

		public void finished() {
			gzu = null;
			gzListener = null;
			finishData();
		}

		public byte[] getBuffer() {
			return buffer;
		}

		public void failed(Exception e) {
			FilterHandler.this.failed(e);
		}
	}

	@Override
	public Handler getNewInstance(Connection con, TrafficLoggerHandler tlh,
			HttpHeader header, HttpHeader webHeader, ResourceSource content,
			boolean mayCache, boolean mayFilter, long size) {
		FilterHandler h = new FilterHandler(con, tlh, header, webHeader,
				content, mayCache, mayFilter, size, compress, repack,
				filterClasses);
		h.defaultCharSet = defaultCharSet;
		h.overrideCharSet = overrideCharSet;
		h.setupHandler();
		return h;
	}

	@Override
	protected void writeDataToGZipper(byte[] arr) {
		forwardArrayToHandler(arr, 0, arr.length);
	}

	@Override
	protected void modifyBuffer(BufferHandle bufHandle) {
		if (!mayFilter) {
			super.modifyBuffer(bufHandle);
			return;
		}
		ByteBuffer buf = bufHandle.getBuffer();
		byte[] arr;
		int off = 0;
		int len = buf.remaining();
		if (buf.hasArray()) {
			arr = buf.array();
			off = buf.position();
		} else {
			arr = new byte[len];
			buf.get(arr);
		}
		bufHandle.possiblyFlush();
		forwardArrayToHandler(arr, off, len);
	}

	private void forwardArrayToHandler(byte[] arr, int off, int len) {
		if (gzu != null) {
			gzListener.clearDataFlag();
			gzu.setInput(arr, off, len);
			// gzu may be null if we get into finished mode
			if (gzu != null && gzu.needsInput() && !gzListener.gotData())
				waitForData();
		} else {
			handleArray(arr, off, len);
		}
	}

	private void handleArray(byte[] arr, int off, int len) {
		if (restBlock != null) {
			int rs = restBlock.length;
			int newLen = len + rs;
			byte[] buf = new byte[newLen];
			System.arraycopy(restBlock, 0, buf, 0, rs);
			System.arraycopy(arr, off, buf, rs, len);
			arr = buf;
			off = 0;
			len = newLen;
			restBlock = null;
		}
		parser.setText(arr, off, len);
		HtmlBlock currentBlock;
		try {
			currentBlock = parser.parse();
			for (HtmlFilter hf : filters) {
				hf.filterHtml(currentBlock);
				if (!hf.isCacheable()) {
					mayCache = false;
					removeCache();
				}
			}

			List<ByteBuffer> ls = currentBlock.getBlocks();
			if (currentBlock.hasRests()) {
				// since the unpacking buffer is re used we need to store the
				// rest in a separate buffer.
				restBlock = currentBlock.getRestBlock();
			}
			sendBlocks = ls.iterator();
		} catch (HtmlParseException e) {
			getLogger().info("Bad HTML: " + e.toString());
			// out.write (arr);
			ByteBuffer buf = ByteBuffer.wrap(arr, off, len);
			sendBlocks = Arrays.asList(buf).iterator();
		}
		if (sendBlocks.hasNext()) {
			sendBlockBuffers();
		} else {
			// no more blocks so wait for more data, either from
			// gzip or the net
			blockSent();
		}
	}

	@Override
	public void blockSent() {
		if (sendingRest) {
			super.finishData();
		} else if (sendBlocks != null && sendBlocks.hasNext()) {
			sendBlockBuffers();
		} else if (gzu != null && !gzu.needsInput()) {
			gzu.handleCurrentData();
		} else {
			super.blockSent();
		}
	}

	private void sendBlockBuffers() {
		ByteBuffer buf = sendBlocks.next();
		SimpleBufferHandle bh = new SimpleBufferHandle(buf);
		send(bh);
	}

	@Override
	protected void finishData() {
		if (restBlock != null && restBlock.length > 0) {
			ByteBuffer buf = ByteBuffer.wrap(restBlock);
			SimpleBufferHandle bh = new SimpleBufferHandle(buf);
			restBlock = null;
			sendingRest = true;
			send(bh);
		} else {
			super.finishData();
		}
	}

	/**
	 * Initialize the filter we are using.
	 * 
	 * @return a List of HtmlFilters.
	 */
	private List<HtmlFilter> initFilters() {
		int fsize = filterClasses.size();
		List<HtmlFilter> fl = new ArrayList<HtmlFilter>(fsize);

		for (int i = 0; i < fsize; i++) {
			HtmlFilterFactory hff = filterClasses.get(i);
			fl.add(hff.newFilter(con, request, response));
		}
		return fl;
	}

	/**
	 * Setup this class.
	 * 
	 * @param prop
	 *            the properties of this class.
	 */
	@Override
	public void setup(SProperties prop, HttpProxy proxy) {
		super.setup(prop, proxy);
		defaultCharSet = prop.getProperty("defaultCharSet", "ISO-8859-1");
		overrideCharSet = prop.getProperty("overrideCharSet");
		String rp = prop.getProperty("repack", "false");
		repack = Boolean.parseBoolean(rp);
		String fs = prop.getProperty("filters", "");
		if ("".equals(fs))
			return;
		String[] names = fs.split(",");
		for (String classname : names) {
			try {
				Class<? extends HtmlFilterFactory> cls = proxy
						.load3rdPartyClass(classname, HtmlFilterFactory.class);
				filterClasses.add(cls.newInstance());
			} catch (ClassNotFoundException e) {
				getLogger().warning("Could not find filter: '" + classname + "'");
			} catch (InstantiationException e) {
				getLogger().log(Level.WARNING,"Could not instanciate class: '" + classname + "'", e);
			} catch (IllegalAccessException e) {
				getLogger().log(Level.WARNING,"Could not get constructor for: '" + classname + "'", e);
			}
		}
	}
}

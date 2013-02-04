package com.googlecode.httpfilter.proxy.rabbit.webserver;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.BufferHandler;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.NioHandler;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.StatisticsHolder;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl.Acceptor;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl.AcceptorListener;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl.BasicStatisticsHolder;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl.CachingBufferHandler;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl.MultiSelectorNioHandler;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl.SimpleThreadFactory;
import com.googlecode.httpfilter.proxy.rabbit.util.SimpleTrafficLogger;
import com.googlecode.httpfilter.proxy.rabbit.util.TrafficLogger;

/**
 * A simple web server that serves static resources.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class SimpleWebServer {
	private File dir;
	private final int port;
	private final NioHandler nioHandler;
	private final TrafficLogger trafficLogger = new SimpleTrafficLogger();
	private final BufferHandler bufferHandler = new CachingBufferHandler();

	/**
	 * Start a web server using the port and base dir given as arguments.
	 * 
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
			usage();
			return;
		}
		try {
			int port = Integer.parseInt(args[0]);
			SimpleWebServer sws = new SimpleWebServer(port, args[1]);
			sws.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void usage() {
		System.err.println("java " + SimpleWebServer.class.getName()
				+ " <port> <dir>");
	}

	/**
	 * Start a web server listening on the given port and serving files from the
	 * given path. The web server will not serve requests until
	 * <code>start ()</code> is called.
	 * 
	 * @param port
	 *            the port to listen on
	 * @param path
	 *            the directory to serve resources from
	 * @throws IOException
	 *             if server setup fails
	 */
	public SimpleWebServer(int port, String path) throws IOException {
		this.port = port;
		dir = new File(path);
		if (!(dir.exists() && dir.isDirectory()))
			throw new IOException(dir + " is not an existing directory");
		dir = dir.getCanonicalFile();
		ExecutorService es = Executors.newCachedThreadPool();
		StatisticsHolder sh = new BasicStatisticsHolder();
		nioHandler = new MultiSelectorNioHandler(es, sh, 4,
				Long.valueOf(15000L));
	}

	/**
	 * Start serving requests.
	 */
	public void start() {
		nioHandler.start(new SimpleThreadFactory());
		setupServerSocket();
	}

	private void setupServerSocket() {
		try {
			ServerSocketChannel ssc = ServerSocketChannel.open();
			ssc.configureBlocking(false);
			ssc.socket().bind(new InetSocketAddress(port));
			AcceptorListener acceptListener = new AcceptListener();
			Acceptor acceptor = new Acceptor(ssc, nioHandler, acceptListener);
			acceptor.register();
		} catch (IOException e) {
			throw new RuntimeException("Failed to setup server socket", e);
		}
	}

	private class AcceptListener implements AcceptorListener {
		public void connectionAccepted(SocketChannel sc) throws IOException {
			new Connection(SimpleWebServer.this, sc).readRequest();
		}
	}

	/**
	 * Get the directory files are served from.
	 * 
	 * @return the root directory for resources
	 */
	public File getBaseDir() {
		return dir;
	}

	/**
	 * Get the BufferHandler used by this web server.
	 * 
	 * @return the BufferHandler
	 */
	public BufferHandler getBufferHandler() {
		return bufferHandler;
	}

	/**
	 * Get the SelectorRunner used by this web server.
	 * 
	 * @return the NioHandler in use
	 */
	public NioHandler getNioHandler() {
		return nioHandler;
	}

	/**
	 * Get the TrafficLogger used by this web server.
	 * 
	 * @return the TrafficLogger in use
	 */
	public TrafficLogger getTrafficLogger() {
		return trafficLogger;
	}
}

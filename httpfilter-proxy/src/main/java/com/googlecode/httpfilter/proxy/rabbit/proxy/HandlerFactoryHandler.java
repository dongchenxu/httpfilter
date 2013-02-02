package com.googlecode.httpfilter.proxy.rabbit.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.googlecode.httpfilter.proxy.rabbit.handler.HandlerFactory;
import com.googlecode.httpfilter.proxy.rabbit.util.Config;
import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;

/**
 * A class to handle mime type handler factories.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class HandlerFactoryHandler {
	private final List<HandlerInfo> handlers;
	private final List<HandlerInfo> cacheHandlers;
	private final Logger logger = Logger.getLogger(getClass().getName());

	public HandlerFactoryHandler(SProperties handlersProps,
			SProperties cacheHandlersProps, Config config, HttpProxy proxy) {
		handlers = loadHandlers(handlersProps, config, proxy);
		cacheHandlers = loadHandlers(cacheHandlersProps, config, proxy);
	}

	private static class HandlerInfo {
		public final String mime;
		public final Pattern pattern;
		public final HandlerFactory factory;

		public HandlerInfo(String mime, HandlerFactory factory) {
			this.mime = mime;
			this.pattern = Pattern.compile(mime, Pattern.CASE_INSENSITIVE);
			this.factory = factory;
		}

		public boolean accept(String mime) {
			Matcher m = pattern.matcher(mime);
			return m.matches();
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + "{" + mime + ", " + factory
					+ "}";
		}
	}

	/**
	 * Load a set of handlers.
	 * 
	 * @param handlersProps
	 *            the properties for the handlers
	 * @param config
	 *            the Config to get handler properties from
	 * @param proxy
	 *            the HttpProxy loading the Handler
	 * @return a Map with mimetypes as keys and Handlers as values.
	 */
	protected List<HandlerInfo> loadHandlers(SProperties handlersProps,
			Config config, HttpProxy proxy) {
		List<HandlerInfo> hhandlers = new ArrayList<HandlerInfo>();
		if (handlersProps == null)
			return hhandlers;
		for (String handler : handlersProps.keySet()) {
			HandlerFactory hf;
			String id = handlersProps.getProperty(handler).trim();
			hf = setupHandler(id, config, handler, proxy);
			hhandlers.add(new HandlerInfo(handler, hf));
		}
		return hhandlers;
	}

	private HandlerFactory setupHandler(String id, Config config,
			String handler, HttpProxy proxy) {
		String className = id;
		HandlerFactory hf = null;
		try {
			int i = id.indexOf('*');
			if (i >= 0)
				className = id.substring(0, i);
			Class<? extends HandlerFactory> cls = proxy.load3rdPartyClass(
					className, HandlerFactory.class);
			hf = cls.newInstance();
			hf.setup(config.getProperties(id), proxy);
		} catch (ClassNotFoundException ex) {
			logger.log(Level.WARNING, "Could not load class: '" + className
					+ "' for handlerfactory '" + handler + "'", ex);
		} catch (InstantiationException ie) {
			logger.log(Level.WARNING, "Could not instanciate factory class: '"
					+ className + "' for handler '" + handler + "'", ie);
		} catch (IllegalAccessException iae) {
			logger.log(Level.WARNING, "Could not instanciate factory class: '"
					+ className + "' for handler '" + handler + "'", iae);
		}
		return hf;
	}

	HandlerFactory getHandlerFactory(String mime) {
		for (HandlerInfo hi : handlers) {
			if (hi.accept(mime))
				return hi.factory;
		}
		return null;
	}

	HandlerFactory getCacheHandlerFactory(String mime) {
		for (HandlerInfo hi : cacheHandlers) {
			if (hi.accept(mime))
				return hi.factory;
		}
		return null;
	}
}

package com.googlecode.httpfilter.proxy.rabbit.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.util.Config;
import com.googlecode.httpfilter.proxy.rabbit.util.TrafficLogger;

/**
 * A class to handle the client traffic loggers.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class ClientTrafficLoggerHandler {
	private final List<ClientTrafficLogger> loggers;

	public ClientTrafficLoggerHandler(Config config, HttpProxy proxy) {
		Logger log = Logger.getLogger(getClass().getName());
		String filters = config.getProperty("logging", "traffic_loggers", "");
		String[] classNames = filters.split(",");
		loggers = new ArrayList<ClientTrafficLogger>(classNames.length);
		for (String clz : classNames) {
			clz = clz.trim();
			if (clz.equals(""))
				continue;
			try {
				Class<? extends ClientTrafficLogger> cls = proxy
						.load3rdPartyClass(clz, ClientTrafficLogger.class);
				ClientTrafficLogger ctl = cls.newInstance();
				ctl.setup(config.getProperties(clz), proxy);
				loggers.add(ctl);
			} catch (ClassNotFoundException ex) {
				log.log(Level.WARNING, "Could not load traffic logger class: '"
						+ clz + "'", ex);
			} catch (InstantiationException ex) {
				log.log(Level.WARNING,
						"Could not instansiate traffic logger: '" + clz + "'",
						ex);
			} catch (IllegalAccessException ex) {
				log.log(Level.WARNING, "Could not access traffic logger: '"
						+ clz + "'", ex);
			}
		}
	}

	public void logTraffic(String user, HttpHeader request,
			TrafficLogger client, TrafficLogger network, TrafficLogger cache,
			TrafficLogger proxy) {
		for (ClientTrafficLogger ctl : loggers) {
			ctl.logTraffic(user, request, client, network, cache, proxy);
		}
	}
}

package com.googlecode.httpfilter.proxy.rabbit.proxy;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.googlecode.httpfilter.proxy.rabbit.filter.IPAccessFilter;
import com.googlecode.httpfilter.proxy.rabbit.util.Config;

/**
 * An access controller based on socket channels.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class SocketAccessController {
	/** the filters, a List of classes (in given order) */
	private List<IPAccessFilter> accessfilters = new ArrayList<IPAccessFilter>();
	private final Logger logger = Logger.getLogger(getClass().getName());

	/**
	 * Create a new SocketAccessController that will use a list of internal
	 * filters.
	 * 
	 * @param filters
	 *            a comma separated list of filters to use
	 * @param config
	 *            the Config to get the internal filters properties from
	 * @param proxy
	 *            the HttpProxy using this access controller
	 */
	public SocketAccessController(String filters, Config config, HttpProxy proxy) {
		accessfilters = new ArrayList<IPAccessFilter>();
		loadAccessFilters(filters, accessfilters, config, proxy);
	}

	private void loadAccessFilters(String filters,
			List<IPAccessFilter> accessfilters, Config config, HttpProxy proxy) {
		StringTokenizer st = new StringTokenizer(filters, ",");
		String classname = "";
		while (st.hasMoreElements()) {
			try {
				classname = st.nextToken().trim();
				Class<? extends IPAccessFilter> cls = proxy.load3rdPartyClass(
						classname, IPAccessFilter.class);
				IPAccessFilter ipf = cls.newInstance();
				ipf.setup(config.getProperties(classname));
				accessfilters.add(ipf);
			} catch (ClassNotFoundException ex) {
				logger.log(Level.WARNING, "Could not load class: '" + classname
						+ "'", ex);
			} catch (InstantiationException ex) {
				logger.log(Level.WARNING, "Could not instansiate: '"
						+ classname + "'", ex);
			} catch (IllegalAccessException ex) {
				logger.log(Level.WARNING, "Could not instansiate: '"
						+ classname + "'", ex);
			}
		}
	}

	private List<IPAccessFilter> getAccessFilters() {
		return Collections.unmodifiableList(accessfilters);
	}

	/**
	 * Check if the given channel is allowed access.
	 * 
	 * @param sc
	 *            the channel to check
	 * @return true if the channel is allowed access, false otherwise
	 */
	public boolean checkAccess(SocketChannel sc) {
		for (IPAccessFilter filter : getAccessFilters()) {
			if (filter.doIPFiltering(sc))
				return true;
		}
		return false;
	}
}

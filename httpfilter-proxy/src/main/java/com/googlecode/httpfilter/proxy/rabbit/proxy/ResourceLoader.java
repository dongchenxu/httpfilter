package com.googlecode.httpfilter.proxy.rabbit.proxy;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import com.googlecode.httpfilter.proxy.rabbit.jndi.InitCtxFactory;
import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;

/**
 * A class that can load and bind resources for jndi.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class ResourceLoader {
	private final Logger logger = Logger.getLogger(getClass().getName());
	private final Context context;

	/**
	 * Create a new ResourceLoader
	 * 
	 * @throws NamingException
	 *             if the initial context can not be created
	 */
	public ResourceLoader() throws NamingException {
		System.setProperty("java.naming.factory.initial",
				InitCtxFactory.class.getName());
		context = new InitialContext();
	}

	/**
	 * Load and bind the resouce given by the given section name and properties.
	 * 
	 * @param name
	 *            the name of the resource
	 * @param props
	 *            the properties for the resource
	 * @param proxy
	 *            the HttpProxy loading the classes
	 */
	public void setupResource(String name, SProperties props, HttpProxy proxy) {
		if (props == null) {
			logger.warning("No properties for: " + name
					+ ", not registering resource");
			return;
		}
		String clz = props.get("class");
		try {
			Class<?> c = proxy.load3rdPartyClass(clz, Object.class);
			Object dataSource = c.newInstance();
			Set<String> ignore = new HashSet<String>(Arrays.asList("class",
					"bind_name"));
			for (Map.Entry<String, String> me : props.entrySet()) {
				String k = me.getKey();
				if (ignore.contains(k))
					continue;
				Method m = c.getMethod(k, String.class);
				m.invoke(dataSource, me.getValue());
			}
			String bindName = props.get("bind_name");
			context.bind(bindName, dataSource);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Failed to setup resource: " + name, e);
		}
	}
}
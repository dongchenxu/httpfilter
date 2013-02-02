package com.googlecode.httpfilter.proxy.rabbit.jndi;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.spi.InitialContextFactory;

/**
 * Factory for jndi contexts.
 * 
 * Originally from the jndi tutorial
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class InitCtxFactory implements InitialContextFactory {
	private static HierContext global;

	public Context getInitialContext(Hashtable<?, ?> env) {
		synchronized (InitCtxFactory.class) {
			if (global == null)
				global = new HierContext(env);
			return global;
		}
	}
}

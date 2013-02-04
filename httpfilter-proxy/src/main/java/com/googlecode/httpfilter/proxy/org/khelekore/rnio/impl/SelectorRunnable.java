package com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl;

import java.io.IOException;

/**
 * A task to be run on a selector thread.
 */
interface SelectorRunnable {
	void run(SingleSelectorRunner sc) throws IOException;
}

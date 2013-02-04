package com.googlecode.httpfilter.proxy.rabbit.meta;

/**
 * Kills the proxy instance. This may or may not cause the jvm to end.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class Kill extends BaseMetaHandler {
	private boolean timeToKill = false;

	protected String getPageHeader() {
		return "Killing the proxy";
	}

	/** Add the page information */
	protected PageCompletion addPageInformation(StringBuilder sb) {
		if (!timeToKill) {
			sb.append("At the time you read this, I am probably dead.\n");
			timeToKill = true;
			return PageCompletion.PAGE_NOT_DONE;
		} else {
			con.getProxy().stop();
			return PageCompletion.PAGE_DONE;
		}
	}
}

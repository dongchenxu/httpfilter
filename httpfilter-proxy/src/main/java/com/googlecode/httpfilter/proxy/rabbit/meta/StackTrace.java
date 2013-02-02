package com.googlecode.httpfilter.proxy.rabbit.meta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Clears the cache completely
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class StackTrace extends BaseMetaHandler {
	protected String getPageHeader() {
		return "Current thread stack";
	}

	/** Add the page information */
	protected PageCompletion addPageInformation(StringBuilder sb) {
		sb.append("<xmp>\n");
		Map<Thread, StackTraceElement[]> m = Thread.getAllStackTraces();
		List<Thread> ls = new ArrayList<Thread>(m.keySet());
		Collections.sort(ls, new ThreadComparator());
		for (Thread t : ls) {
			sb.append(t.toString());
			sb.append("\n");
			for (StackTraceElement se : m.get(t))
				sb.append(("\t" + se + "\n"));
			sb.append("\n");
		}
		sb.append("</xmp>");
		sb.append("</body></html>");
		return PageCompletion.PAGE_DONE;
	}

	private static class ThreadComparator implements Comparator<Thread>,
			Serializable {
		private static final long serialVersionUID = 20060606;

		public int compare(Thread t1, Thread t2) {
			return t1.getName().compareTo(t2.getName());
		}
	}
}

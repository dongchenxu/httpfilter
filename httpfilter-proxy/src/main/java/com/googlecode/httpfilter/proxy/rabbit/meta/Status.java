package com.googlecode.httpfilter.proxy.rabbit.meta;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import com.googlecode.httpfilter.proxy.rabbit.proxy.Connection;
import com.googlecode.httpfilter.proxy.rabbit.proxy.HtmlPage;
import com.googlecode.httpfilter.proxy.rabbit.proxy.HttpProxy;
import com.googlecode.httpfilter.proxy.rabbit.proxy.TrafficLoggerHandler;
import com.googlecode.httpfilter.proxy.rabbit.util.TrafficLogger;

/**
 * A status page for the proxy.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class Status extends BaseMetaHandler {

	@Override
	protected String getPageHeader() {
		return "Status";
	}

	/** Add the page information */
	@Override
	protected PageCompletion addPageInformation(StringBuilder sb) {
		addStatus(sb);
		return PageCompletion.PAGE_DONE;
	}

	private void addStatus(StringBuilder sb) {
		HttpProxy proxy = con.getProxy();
		List<Connection> connections = proxy.getCurrentConnections();
		sb.append("Version: " + proxy.getVersion() + "<br>\n");
		sb.append("Running on: " + proxy.getHost() + " <B>:</B> "
				+ proxy.getPort() + "<br>\n");
		sb.append("Started at: " + new Date(proxy.getStartTime()) + "<br>\n");
		sb.append("Current time: " + new Date() + "<br>\n");
		sb.append("Alive and kicking with " + connections.size()
				+ " current connections.<br>\n");

		sb.append(HtmlPage.getTableHeader(100, 1));
		sb.append(HtmlPage.getTableTopicRow());
		sb.append("<th width=\"20%\">Type</th>" + "<th width=\"20%\">Read</th>"
				+ "<th width=\"20%\">Write</th>"
				+ "<th width=\"20%\">TransferTo</th>"
				+ "<th width=\"20%\">TransferFrom</th></tr>\n");
		TrafficLoggerHandler tlh = proxy.getTrafficLoggerHandler();
		synchronized (tlh) {
			appendTL(sb, "Client", tlh.getClient());
			appendTL(sb, "Network", tlh.getNetwork());
			appendTL(sb, "Cache", tlh.getCache());
			appendTL(sb, "Proxy", tlh.getProxy());
		}
		sb.append("</table>\n<br>\n");

		sb.append(HtmlPage.getTableHeader(100, 1));
		sb.append(HtmlPage.getTableTopicRow());
		sb.append("<th width=\"20%\">InetAddress</th><th>Id</th>"
				+ "<th width=\"50%\">Connection</th>"
				+ "<th width=\"20%\">Status</th><th>Time(s)</th></tr>\n");

		long now = System.currentTimeMillis();
		for (Connection hth : connections) {
			sb.append("<tr><td>");
			InetAddress ia = hth.getChannel().socket().getInetAddress();
			if (ia != null)
				sb.append(ia.getHostAddress());
			else
				sb.append("?");
			sb.append("</td><td><nobr>").append(hth.getId());
			sb.append("</nobr></td><td>").append(hth.getRequestLine());
			sb.append("</td><td>").append(hth.getStatus());
			sb.append("</td><td>").append((now - hth.getStarted()) / 1000);
			sb.append("</td></tr>\n");
		}

		sb.append("</table>\n<br>\n");
		sb.append(HtmlPage.getTableHeader(100, 1));
		sb.append(HtmlPage.getTableTopicRow());
		sb.append("<th>thingy</th><th width=\"10%\">times</th></tr>\n");
		Set<String> e = proxy.getCounter().keys();
		List<String> ls = new ArrayList<String>(e);
		Collections.sort(ls);
		for (String type : ls) {
			int val = proxy.getCounter().get(type);
			sb.append("\t<tr><td>").append(type);
			sb.append("</td><td>").append(val).append("</td></tr>\n");
		}
		sb.append("</table>\n");
	}

	private void appendTL(StringBuilder sb, String type, TrafficLogger tl) {
		sb.append("<tr><td>" + type + "</td>");
		sb.append("<td align=\"right\">").append(tl.read()).append("</td>");
		sb.append("<td align=\"right\">").append(tl.write()).append("</td>");
		sb.append("<td align=\"right\">").append(tl.transferTo())
				.append("</td>");
		sb.append("<td align=\"right\">").append(tl.transferFrom())
				.append("</td>");
		sb.append("</tr>\n");
	}
}

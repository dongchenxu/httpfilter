package com.googlecode.httpfilter.proxy.rabbit.meta;

import java.util.List;
import java.util.Map;
import com.googlecode.httpfilter.proxy.rabbit.io.Address;
import com.googlecode.httpfilter.proxy.rabbit.io.ConnectionHandler;
import com.googlecode.httpfilter.proxy.rabbit.io.WebConnection;
import com.googlecode.httpfilter.proxy.rabbit.proxy.HtmlPage;
import com.googlecode.httpfilter.proxy.rabbit.proxy.HttpProxy;

/**
 * A page that shows the currently open web connections.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class Connections extends BaseMetaHandler {
	@Override
	protected String getPageHeader() {
		return "Current connections";
	}

	/** Add the page information */
	@Override
	protected PageCompletion addPageInformation(StringBuilder sb) {
		addStatus(sb);
		return PageCompletion.PAGE_DONE;
	}

	private void addStatus(StringBuilder sb) {
		HttpProxy proxy = con.getProxy();
		ConnectionHandler ch = proxy.getConnectionHandler();
		sb.append("<br>\n");
		sb.append("Keepalive is set to: ");
		sb.append(ch.getKeepaliveTime() / 1000);
		sb.append(" s.<br>\n");
		sb.append(HtmlPage.getTableHeader(100, 1));
		sb.append(HtmlPage.getTableTopicRow());
		sb.append("<P><H1>keepalive connections</H1></P>\n");
		sb.append("<th width=\"30%\">InetAddress</th>");
		sb.append("<th width=\"20%\">Port</th>");
		sb.append("<th width=\"50%\">#Connection</th>\n");

		Map<Address, List<WebConnection>> m = ch.getActiveConnections();
		for (Map.Entry<Address, List<WebConnection>> me : m.entrySet()) {
			Address a = me.getKey();
			List<WebConnection> ls = me.getValue();
			sb.append("<tr><td>").append(a.getInetAddress());
			sb.append("</td><td>").append(a.getPort());
			sb.append("</td><td>").append(ls.size());
			sb.append("</td></tr>\n");
		}

		sb.append("</table><br>\n");
		sb.append("<P><H1>Pipelined connections</H1></P>\n");
		sb.append(HtmlPage.getTableHeader(100, 1));
		sb.append(HtmlPage.getTableTopicRow());
		sb.append("<th width=\"30%\">InetAddress</th>");
		sb.append("<th width=\"20%\">Port</th>");
		sb.append("<th width=\"50%\">#Connection</th>\n");

		// TODO: fill in lots of stuff...
		// TODO: not implemented yet

		sb.append("</table>\n");
	}
}

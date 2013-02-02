package com.googlecode.httpfilter.proxy.rabbit.meta;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Date;
import java.util.Set;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.SelectorVisitor;
import com.googlecode.httpfilter.proxy.rabbit.proxy.HtmlPage;

/**
 * A status page for the proxy.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class SelectorStatus extends BaseMetaHandler {

	@Override
	protected String getPageHeader() {
		return "Selector status";
	}

	/** Add the page information */
	@Override
	protected PageCompletion addPageInformation(StringBuilder sb) {
		addStatus(sb);
		return PageCompletion.PAGE_DONE;
	}

	private void addStatus(final StringBuilder sb) {
		sb.append("Status of selector at: ");
		sb.append(new Date());
		sb.append("<p>\n");

		con.getNioHandler().visitSelectors(new SelectorVisitor() {
			int count = 0;

			public void selector(Selector selector) {
				boolean odd = (count & 1) == 1;
				String trColor = odd ? "#EE8888" : "#DD6666";
				String tdColor = odd ? "#EEFFFF" : "#DDDDFF";
				appendKeys(sb, selector.selectedKeys(), "Selected key",
						trColor, tdColor);
				appendKeys(sb, selector.keys(), "Registered key", trColor,
						tdColor);
				count++;
			}

			public void end() {
			}
		});
	}

	private void appendKeys(StringBuilder sb, Set<SelectionKey> sks,
			String header, String thColor, String trColor) {
		sb.append(HtmlPage.getTableHeader(100, 1));
		sb.append("<tr bgcolor=\"").append(thColor).append("\">");
		sb.append("<th width=\"20%\">").append(header).append("</th>");
		sb.append("<th>channel</th>" + "<th width=\"50%\">Attachment</th>"
				+ "<th>Interest</th>" + "<th>Ready</th>" + "</tr>\n");
		for (SelectionKey sk : sks) {
			sb.append("<tr bgcolor=\"").append(trColor).append("\"><td>");
			sb.append(sk.toString());
			sb.append("</td><td>");
			sb.append(sk.channel());
			sb.append("</td><td>");
			sb.append(sk.attachment());
			sb.append("</td><td>");
			boolean valid = sk.isValid();
			appendOpString(sb, valid ? sk.interestOps() : 0);
			sb.append("</td><td>");
			appendOpString(sb, valid ? sk.readyOps() : 0);
			sb.append("</td></tr>\n");
		}
		sb.append("</table>\n<br>\n");
	}

	private void appendOpString(StringBuilder sb, int op) {
		sb.append((op & SelectionKey.OP_READ) != 0 ? "R" : "_");
		sb.append((op & SelectionKey.OP_WRITE) != 0 ? "W" : "_");
		sb.append((op & SelectionKey.OP_CONNECT) != 0 ? "C" : "_");
		sb.append((op & SelectionKey.OP_ACCEPT) != 0 ? "A" : "_");
	}
}

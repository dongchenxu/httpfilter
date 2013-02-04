package com.googlecode.httpfilter.proxy.rabbit.proxy;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpDateParser;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;

/**
 * A class that handles warning headers.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class WarningsHandler {
	private int nextNonBlank(String s, int start) {
		char c;
		int len = s.length();
		while (start < len
				&& ((c = s.charAt(start)) == ' ' || c == '\n' || c == '\r' || c == '\t'))
			start++;
		return start;
	}

	private int nextBlank(String s, int start) {
		char c;
		int len = s.length();
		while (start < len
				&& !((c = s.charAt(start)) == ' ' || c == '\n' || c == '\r' || c == '\t'))
			start++;
		return start;
	}

	public void removeWarnings(HttpHeader header, boolean remove1xx) {
		String rdate = header.getHeader("Date");
		List<String> ws = header.getHeaders("Warning");
		int wl = ws.size();
		for (int wi = 0; wi < wl; wi++) {
			String val = ws.get(wi);
			try {
				StringBuilder sb = new StringBuilder();
				boolean first = true;
				int start = 0;
				while (start < val.length()) {
					int i = nextNonBlank(val, start);
					i = nextBlank(val, i);
					String code = val.substring(start, i);
					int j = nextNonBlank(val, i + 1);
					j = nextBlank(val, j);
					String agent = val.substring(i + 1, j);
					int k = val.indexOf('"', j);
					int l = val.indexOf('"', k + 1);
					// StringIndexOutOfBoundsException: -1
					String text = val.substring(k + 1, l);
					int c = val.indexOf(',', l);
					int m = val.indexOf('"', l + 1);
					String date = null;
					if (((c == -1 && m == -1) || (c < m))) {
						start = l + 1;
					} else {
						int n = val.indexOf('"', m + 1);
						date = val.substring(m + 1, n);
						int c2 = val.indexOf(',', n + 1);
						if (c2 != -1)
							start = c2;
						else
							start = n + 1;
					}
					char s;
					while (start < val.length()
							&& ((s = val.charAt(start)) == ' ' || s == ','))
						start++;

					Date d1 = null, d2 = null;
					if (date != null)
						d1 = HttpDateParser.getDate(date);
					if (rdate != null)
						d2 = HttpDateParser.getDate(rdate);
					if (!((d1 != null && !d1.equals(d2)) || (remove1xx && code
							.charAt(0) == '1') && !"RabbIT".equals(agent))) {
						if (!first)
							sb.append(", ");
						sb.append(code + " " + agent + " \"" + text);
						sb.append(date != null ? "\" \"" + date + "\"" : "\"");
						first = false;
					}
				}
				if (sb.length() != 0)
					header.setExistingValue(val, sb.toString());
				else
					header.removeValue(val);
			} catch (StringIndexOutOfBoundsException e) {
				Logger logger = Logger.getLogger(getClass().getName());
				logger.warning("bad warning header: '" + val + "'");
			}
		}
	}

	public void updateWarnings(HttpHeader header, HttpHeader webheader) {
		for (String warn : webheader.getHeaders("Warning"))
			header.addHeader("Warning", warn);
	}
}

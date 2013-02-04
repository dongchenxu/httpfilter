package com.googlecode.httpfilter.proxy.rabbit.proxy;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl.Closer;
import com.googlecode.httpfilter.proxy.rabbit.html.HtmlBlock;
import com.googlecode.httpfilter.proxy.rabbit.html.HtmlEscapeUtils;
import com.googlecode.httpfilter.proxy.rabbit.html.HtmlParseException;
import com.googlecode.httpfilter.proxy.rabbit.html.HtmlParser;
import com.googlecode.httpfilter.proxy.rabbit.html.Tag;
import com.googlecode.httpfilter.proxy.rabbit.html.TagType;
import com.googlecode.httpfilter.proxy.rabbit.html.Token;
import com.googlecode.httpfilter.proxy.rabbit.html.TokenType;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeaderWithContent;
import com.googlecode.httpfilter.proxy.rabbit.http.StatusCode;
import com.googlecode.httpfilter.proxy.rabbit.util.StackTraceUtil;

import static com.googlecode.httpfilter.proxy.rabbit.http.StatusCode.*;

/**
 * A HttpGenerator that creates error pages from file templates.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class FileTemplateHttpGenerator extends StandardResponseHeaders {

	private final File templateDir;
	private final Logger logger = Logger.getLogger(getClass().getName());

	public FileTemplateHttpGenerator(String identity, Connection con,
			File templateDir) {
		super(identity, con);
		this.templateDir = templateDir;
	}

	private File getFile(StatusCode sc) {
		return new File(templateDir, Integer.toString(sc.getCode()));
	}

	private boolean hasFile(StatusCode sc) {
		return getFile(sc).exists();
	}

	private boolean match(Token t, TagType type) {
		if (t.getType() == TokenType.TAG)
			return t.getTag().getTagType() == type;
		return false;
	}

	private void replaceValue(Tag tag, String attribute, String match,
			String replacer) {
		String attr = tag.getAttribute(attribute);
		if (attr != null) {
			boolean found = false;
			int idx;
			// only expect to find zero or one
			while ((idx = attr.indexOf(match)) > -1) {
				found = true;
				attr = attr.substring(0, idx) + replacer
						+ attr.substring(idx + match.length());
			}
			if (found)
				tag.setAttribute(attribute, attr);
		}
	}

	private void replaceLinks(HtmlBlock block, String match, String replacer) {
		for (Token t : block.getTokens()) {
			if (match(t, TagType.A))
				replaceValue(t.getTag(), "href", match, replacer);
			else if (match(t, TagType.IMG))
				replaceValue(t.getTag(), "src", match, replacer);
		}
	}

	private boolean isTagOfType(Token token, String type) {
		if (token.getType() == TokenType.TAG) {
			Tag tag = token.getTag();
			if (tag.getLowerCaseType().equals(type))
				return true;
		}
		return false;
	}

	private void replaceTemplate(HtmlBlock block, String tagType, String text) {
		for (Token t : block.getTokens())
			if (isTagOfType(t, tagType))
				t.setText(text);
	}

	private void replacePlaces(HtmlBlock block, String tag, URL url) {
		for (Token t : block.getTokens())
			if (isTagOfType(t, tag))
				t.setText(getPlaces(url).toString());
	}

	private void replaceStackTrace(HtmlBlock block, String tag, Throwable thrown) {
		for (Token t : block.getTokens())
			if (isTagOfType(t, tag))
				t.setText(StackTraceUtil.getStackTrace(thrown));
	}

	private void replace(HtmlBlock block, String tag, String value) {
		if (value != null)
			replaceTemplate(block, tag, HtmlEscapeUtils.escapeHtml(value));
	}

	private void replaceTemplates(HtmlBlock block, TemplateData td)
			throws IOException {
		replace(block, "%url%", td.url);
		if (td.thrown != null) {
			replace(block, "%exception%", td.thrown.toString());
			replaceStackTrace(block, "%stacktrace%", td.thrown);
		}
		replace(block, "%filename%", td.file);
		replace(block, "%expectation%", td.expectation);
		replace(block, "%realm%", td.realm);
		if (td.url != null)
			replacePlaces(block, "%places%", new URL(td.url));

		HttpProxy proxy = getProxy();
		String sproxy = proxy.getHost().getHostName() + ":" + proxy.getPort();
		replaceLinks(block, "$proxy", sproxy);
	}

	private static class TemplateData {
		private final String url;
		private final Throwable thrown;
		private final String file;
		private final String expectation;
		private final String realm;
		private final String realmType;

		public TemplateData(String url, Throwable thrown, String file,
				String expectation, String realm, String realmType) {
			this.url = url;
			this.thrown = thrown;
			this.file = file;
			this.expectation = expectation;
			this.realm = realm;
			this.realmType = realmType;
		}
	}

	public TemplateData getTemplateData() {
		return new TemplateData(getConnection().getRequestURI(), null, null,
				null, null, null);
	}

	public TemplateData getTemplateData(Throwable thrown) {
		return new TemplateData(getConnection().getRequestURI(), thrown, null,
				null, null, null);
	}

	public TemplateData getTemplateData(URL url) {
		return new TemplateData(url.toString(), null, null, null, null, null);
	}

	public TemplateData getExpectionationData(String expectation) {
		return new TemplateData(getConnection().getRequestURI(), null, null,
				expectation, null, null);
	}

	public TemplateData getURLExceptionData(String url, Throwable thrown) {
		return new TemplateData(url, thrown, null, null, null, null);
	}

	public TemplateData getURLRealmData(URL url, String realm, String realmType) {
		return new TemplateData(url.toString(), null, null, null, realm,
				realmType);
	}

	private HttpHeader getTemplated(StatusCode sc, TemplateData td) {
		HttpHeaderWithContent ret = getHeader(sc);
		if (td.realm != null)
			ret.setHeader(td.realmType + "-Authenticate", "Basic realm=\""
					+ td.realm + "\"");
		File f = getFile(sc);
		try {
			FileInputStream fis = new FileInputStream(f);
			try {
				byte[] buf = new byte[(int) f.length()];
				DataInputStream dis = new DataInputStream(fis);
				try {
					dis.readFully(buf);
					Charset utf8 = Charset.forName("UTF-8");
					HtmlParser parser = new HtmlParser(utf8);
					parser.setText(buf);
					HtmlBlock block = parser.parse();
					replaceTemplates(block, td);
					ret.setContent(block.toString(), "UTF-8");
				} finally {
					Closer.close(dis, logger);
				}
			} finally {
				Closer.close(fis, logger);
			}
		} catch (HtmlParseException e) {
			logger.log(Level.WARNING, "Failed to read template", e);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to read template", e);
		}
		return ret;
	}

	@Override
	public HttpHeader get400(Exception exception) {
		if (hasFile(_400))
			return getTemplated(_400, getTemplateData(exception));
		return super.get400(exception);
	}

	@Override
	public HttpHeader get401(URL url, String realm) {
		if (hasFile(_401))
			return getTemplated(_401, getURLRealmData(url, realm, "WWW"));
		return super.get401(url, realm);
	}

	@Override
	public HttpHeader get403() {
		if (hasFile(_403))
			return getTemplated(_403, getTemplateData());
		return super.get403();
	}

	@Override
	public HttpHeader get404(String file) {
		if (hasFile(_404))
			return getTemplated(_404, getTemplateData());
		return super.get404(file);
	}

	@Override
	public HttpHeader get407(URL url, String realm) {
		if (hasFile(_407))
			return getTemplated(_407, getURLRealmData(url, realm, "Proxy"));
		return super.get407(url, realm);
	}

	@Override
	public HttpHeader get412() {
		if (hasFile(_412))
			return getTemplated(_412, getTemplateData());
		return super.get412();
	}

	@Override
	public HttpHeader get414() {
		if (hasFile(_414))
			return getTemplated(_414, getTemplateData());
		return super.get414();
	}

	@Override
	public HttpHeader get416(Throwable exception) {
		if (hasFile(_416))
			return getTemplated(_416, getTemplateData(exception));
		return super.get416(exception);
	}

	@Override
	public HttpHeader get417(String expectation) {
		if (hasFile(_417))
			return getTemplated(_417, getExpectionationData(expectation));
		return super.get417(expectation);
	}

	@Override
	public HttpHeader get500(String url, Throwable exception) {
		if (hasFile(_500))
			return getTemplated(_500, getURLExceptionData(url, exception));
		return super.get500(url, exception);
	}

	@Override
	public HttpHeader get504(String url, Throwable exception) {
		if (hasFile(_504))
			return getTemplated(_504, getURLExceptionData(url, exception));
		return super.get504(url, exception);
	}
}

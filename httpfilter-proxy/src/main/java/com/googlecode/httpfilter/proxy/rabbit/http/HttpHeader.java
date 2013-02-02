package com.googlecode.httpfilter.proxy.rabbit.http;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import com.googlecode.httpfilter.proxy.rabbit.util.StringCache;

/**
 * A class to handle http headers.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class HttpHeader extends GeneralHeader {

	private String method = "";
	private String requestURI = "";
	private String httpVersion = null;
	private int hashCodeValue;

	/**
	 * Create a new HTTPHeader from scratch
	 */
	public HttpHeader() {
		// empty
	}

	/** The string cache we are using. */
	private static final StringCache stringCache = StringCache
			.getSharedInstance();

	private static String getCachedString(String s) {
		return stringCache.getCachedString(s);
	}

	@Override
	protected void fillBuffer(StringBuilder sb) {
		sb.append(getRequestLine());
		sb.append(Header.CRLF);
		super.fillBuffer(sb);
	}

	/**
	 * Convert this header to a byte[]. The header lines are converted to
	 * US-ASCII and any content is appended.
	 * 
	 * @return the content of this header
	 */
	public byte[] getBytes() {
		StringBuilder sb = new StringBuilder();
		sb.append(getRequestLine());
		sb.append(Header.CRLF);
		super.fillBuffer(sb);
		try {
			return sb.toString().getBytes("US-ASCII");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Failed to find ascii", e);
		}
	}

	/**
	 * Get the statusline of this header (only valid for responses).
	 * 
	 * @return the status of the request.
	 */
	public String getStatusLine() {
		return getRequestLine();
	}

	/**
	 * Set the statusline of this header.
	 * 
	 * @param line
	 *            a Status-Line )RFC 2068: 6.1)
	 */
	public void setStatusLine(String line) {
		setRequestLine(line);
	}

	/**
	 * Get the requestline of this header (only valid for requests).
	 * 
	 * @return the request.
	 */
	public String getRequestLine() {
		StringBuilder sb = new StringBuilder(method.length()
				+ requestURI.length() + 10);
		sb.append(method).append(' ').append(requestURI);
		if (httpVersion != null) {
			sb.append(' ').append(httpVersion);
		}
		return sb.toString();
	}

	/**
	 * Set the requestline of this header
	 * 
	 * @param line
	 *            a Request-Line (RFC 2068: 5.1)
	 */
	public void setRequestLine(String line) {
		int s1 = line.indexOf(' ');
		if (s1 < 0) {
			method = getCachedString(line);
			return;
		}
		int s2 = line.indexOf(' ', s1 + 1);
		method = getCachedString(line.substring(0, s1));
		if (s2 > 0) {
			requestURI = getCachedString(line.substring(s1 + 1, s2));
			httpVersion = getCachedString(line.substring(s2 + 1).trim());
		} else {
			requestURI = getCachedString(line.substring(s1 + 1));
			httpVersion = null;
		}
		hashCodeValue = getRequestURI().toLowerCase().hashCode();
	}

	/**
	 * Is this request for the head only?
	 * 
	 * @return true if this request is for HEAD, false otherwise
	 */
	public boolean isHeadOnlyRequest() {
		return method.equals("HEAD"); // method is casesensitive.
	}

	/**
	 * Get the request method of this header (only valid for requests).
	 * 
	 * @return the request method.
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * Sets the request method of this header
	 * 
	 * @param method
	 *            the new requestmethod
	 */
	public void setMehtod(String method) {
		this.method = method;
	}

	/**
	 * Check to see if this header is an SSL header.
	 * 
	 * @return true if this header is an CONNECT request.
	 */
	public boolean isSSLRequest() {
		return getMethod().equals("CONNECT");
	}

	/**
	 * Get the requestURI of this request (only valid for requests).
	 * 
	 * @return the requestURI.
	 */
	public String getRequestURI() {
		return requestURI;
	}

	/**
	 * Sets the request URI of this header
	 * 
	 * @param requestURI
	 *            the new URI
	 */
	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
		hashCodeValue = getRequestURI().toLowerCase().hashCode();
	}

	/**
	 * Get the HTTP Version of this request (only valid for requests).
	 * 
	 * @return the http version.
	 */
	public String getHTTPVersion() {
		return httpVersion;
	}

	/**
	 * Set the HTTP Version to use for request.
	 * 
	 * @param version
	 *            the version to use.
	 */
	public void setHTTPVersion(String version) {
		httpVersion = version;
	}

	/**
	 * Get the HTTP version of the response (only valid for responses).
	 * 
	 * @return the HTTP version.
	 */
	public String getResponseHTTPVersion() {
		return method;
	}

	/**
	 * Set the HTTP version for this response.
	 * 
	 * @param httpVersion
	 *            the version to use.
	 */
	public void setResponseHTTPVersion(String httpVersion) {
		method = httpVersion;
	}

	/**
	 * Get the Status code of the response (only valid for responses).
	 * 
	 * @return the status code.
	 */
	public String getStatusCode() {
		return requestURI;
	}

	/**
	 * Set the Status code for this response.
	 * 
	 * @param status
	 *            the new status code.
	 */
	public void setStatusCode(String status) {
		requestURI = status;
		hashCodeValue = getRequestURI().toLowerCase().hashCode();
	}

	/**
	 * Get the Reason phrase of the response (only valid for responses).
	 * 
	 * @return the reason phrase.
	 */
	public String getReasonPhrase() {
		return httpVersion;
	}

	/**
	 * Set the reason phrase for this reqponse.
	 * 
	 * @param reason
	 *            the new reasonphrase
	 */
	public void setReasonPhrase(String reason) {
		httpVersion = reason;
	}

	/**
	 * Is this request a HTTP/0.9 type request? A 0.9 request doesnt have a full
	 * HTTPheader, only a requestline so we need to treat it differently.
	 * 
	 * @return true if the request did not have any http version
	 */
	public boolean isDot9Request() {
		return isRequest() && httpVersion == null;
	}

	/**
	 * Get the hashCode for this header.
	 * 
	 * @return the hash code for this object.
	 */
	@Override
	public int hashCode() {
		return hashCodeValue;
	}

	/**
	 * Is this Header equal to the other object? Two HTTPHeaders are assumed
	 * equal if the requesURI's are equal.
	 * 
	 * @param o
	 *            the Object to compare to.
	 * @return true if o and this object are equal, false otherwise.
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof HttpHeader) {
			String lcuri = getRequestURI().toLowerCase();
			String olcuri = ((HttpHeader) o).getRequestURI().toLowerCase();
			return lcuri.equals(olcuri);
		}
		return false;
	}

	/**
	 * Try to guess if this header is a request.
	 * 
	 * @return true if this (probably) is a request, false otherwise.
	 */
	public boolean isRequest() {
		return !isResponse();
	}

	/**
	 * Try to guess if this header is a response.
	 * 
	 * @return true if this (probably) is a response, false otherwise.
	 */
	public boolean isResponse() {
		return (getResponseHTTPVersion() != null && getResponseHTTPVersion()
				.toLowerCase().startsWith("http/"));
	}

	/**
	 * Try to guess if this header is a secure thing.
	 * 
	 * @return true if this (probably) is a secure connection.
	 */
	public boolean isSecure() {
		return (getMethod() != null && getMethod().equals("CONNECT"));
	}

	/**
	 * Get the current content for this request/response.
	 * 
	 * @return the resource associated with this header, may be null
	 */
	public byte[] getContent() {
		return null;
	}

	@Override
	public void read(DataInput in) throws IOException {
		method = in.readUTF();
		requestURI = in.readUTF();
		httpVersion = in.readUTF();
		if ("".equals(httpVersion))
			httpVersion = null;
		hashCodeValue = getRequestURI().toLowerCase().hashCode();
		super.read(in);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(method);
		out.writeUTF(requestURI);
		out.writeUTF(httpVersion != null ? httpVersion : "");
		super.write(out);
	}
}

package com.googlecode.httpfilter.proxy.rabbit.http;

/**
 * The http response codes.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public enum StatusCode {
	// 10.1 Informational 1xx
	/** 100 Continue */
	_100(100, "Continue"),
	/** 101 Switching Protocols */
	_101(101, "Switching Protocols"),

	// 10.2 Successful 2xx
	/** 200 Ok */
	_200(200, "OK"),
	/** 201 Created */
	_201(201, "Created"),
	/** 202 Accepted */
	_202(202, "Accepted"),
	/** 203 Non-Authoritative Information */
	_203(203, "Non-Authoritative Information"),
	/** 204 No Content */
	_204(204, "No Content"),
	/** 205 Reset Content */
	_205(205, "Reset Content"),
	/** 206 Partial Content */
	_206(206, "Partial Content"),

	// 10.3 Redirection 3xx
	/** 300 Multiple Choices */
	_300(300, "Multiple Choices"),
	/** 301 Moved Permanently */
	_301(301, "Moved Permanently"),
	/** 302 Found */
	_302(302, "Found"),
	/** 303 See Other */
	_303(303, "See Other"),
	/** 304 Not Modified */
	_304(304, "Not Modified"),
	/** 305 Use Proxy */
	_305(305, "Use Proxy"),
	/** 306 (Unused) */
	_306(306, "(Unused)"),
	/** 307 Temporary Redirect */
	_307(307, "Temporary Redirect"),

	// 10.4 Client Error 4xx
	/** 400 Bad Request */
	_400(400, "Bad Request"),
	/** 401 Unauthorized */
	_401(401, "Unauthorized"),
	/** 402 Payment Required */
	_402(402, "Payment Required"),
	/** 403 Forbidden */
	_403(403, "Forbidden"),
	/** 404 Not Found */
	_404(404, "Not Found"),
	/** 405 Method Not Allowed */
	_405(405, "Method Not Allowed"),
	/** 406 Not Acceptable */
	_406(406, "Not Acceptable"),
	/** 407 Proxy Authentication Required */
	_407(407, "Proxy Authentication Required"),
	/** 408 Request Timeout */
	_408(408, "Request Timeout"),
	/** 409 Conflict */
	_409(409, "Conflict"),
	/** 410 Gone */
	_410(410, "Gone"),
	/** 411 Length Required */
	_411(411, "Length Required"),
	/** 412 Precondition Failed */
	_412(412, "Precondition Failed"),
	/** 413 Request Entity Too Large */
	_413(413, "Request Entity Too Large"),
	/** 414 Request-URI Too Long */
	_414(414, "Request-URI Too Long"),
	/** 415 Unsupported Media Type */
	_415(415, "Unsupported Media Type"),
	/** 416 Requested Range Not Satisfiable */
	_416(416, "Requested Range Not Satisfiable"),
	/** 417 Expectation Failed */
	_417(417, "Expectation Failed"),

	// 10.5 Server Error 5xx
	/** 500 Internal Server Error */
	_500(500, "Internal Server Error"),
	/** 501 Not Implemented */
	_501(501, "Not Implemented"),
	/** 502 Bad Gateway */
	_502(502, "Bad Gateway"),
	/** 503 Service Unavailable */
	_503(503, "Service Unavailable"),
	/** 504 Gateway Timeout */
	_504(504, "Gateway Timeout"),
	/** 505 HTTP Version Not Supported */
	_505(505, "HTTP Version Not Supported");

	private final int code;
	private final String description;

	private StatusCode(int code, String description) {
		this.code = code;
		this.description = description;
	}

	/**
	 * Get the numeric value of the status code
	 * 
	 * @return the status code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Get the human readable description of this status code.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Get a http response line using this status code
	 * 
	 * @param httpVersion
	 *            the HTTP version to use
	 * @return the formatted status line
	 */
	public String getStatusLine(String httpVersion) {
		return httpVersion + " " + getCode() + " " + getDescription();
	}
}
package com.googlecode.httpfilter.proxy.rabbit.html;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * This is a class that is used to parse a block of HTML code into separate
 * tokens. This parser uses a recursive descent approach.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class HtmlParser {
	/** The actual data to parse. */
	private char[] pagepart;
	/** The size of the data to parse. */
	private int length;
	/** The char set */
	private final Charset cs;
	/** The string decoder. */
	private final CharsetDecoder csDecoder;
	/** The current decode rest. */
	private byte[] decodeRest;

	/** The type of the next token. */
	private int nextToken = START;
	/** Index of the parse. */
	private int index = 0;
	/** The current tag started here. */
	private int tagStart = 0;
	/** The current value as a String. */
	private String stringValue = null;
	/** the current start of string. */
	private int stringLength = -1;

	/** True if were in a Tag, false otherwise. */
	private boolean tagmode = false;
	/** The last tag started here. */
	private int lastTagStart = 0;
	/** The block we have. */
	private HtmlBlock block;
	/** A pending comment (script or style data). */
	private Token pendingComment = null;

	/** This indicates the start of a block. */
	public final static int START = 0;
	/** This indicate a String value was found. */
	public final static int STRING = 1;
	/** This is a Single Quoted String a 'string' */
	public final static int SQSTRING = 2;
	/** This is a Double Quoted String a &quot;string&quot; */
	public final static int DQSTRING = 3;
	/** This is the character ''' */
	public final static int SINGELQUOTE = 4;
	/** This is the character '"' */
	public final static int DOUBLEQUOTE = 5;
/** Less Than '<' */
	public final static int LT = 6;
	/** More Than '>' */
	public final static int MT = 7;
	/** Equals '=' */
	public final static int EQUALS = 8;
	/** A HTML comment &quot;&lt;&#33-- some text --&gt;&quot; */
	public final static int COMMENT = 9;
	/** A HTML script */
	public final static int SCRIPT = 10;

	/** This indicates the end of a block. */
	public final static int END = 100;
	/** Unknown token. */
	public final static int UNKNOWN = 1000;

	/**
	 * Create a new HTMLParser
	 * 
	 * @param cs
	 *            the Charset to use when converting bytes to text
	 */
	public HtmlParser(Charset cs) {
		this.cs = cs;
		pagepart = null;
		csDecoder = cs.newDecoder();
	}

	/**
	 * Restores all internal variables to start positions
	 */
	private void init() {
		nextToken = START;
		index = 0;
		tagStart = 0;
		stringValue = null;
		stringLength = -1;
		tagmode = false;
		lastTagStart = 0;
		block = null;
		pendingComment = null;
	}

	/**
	 * Set the data block to parse.
	 * 
	 * @param page
	 *            the block to parse.
	 */
	public void setText(byte[] page) {
		setText(page, 0, page.length);
	}

	/**
	 * Set the data block to parse.
	 * 
	 * @param page
	 *            the block to parse.
	 * @param startIndex
	 *            where to start in the page
	 * @param length
	 *            the length of the data.
	 */
	public void setText(byte[] page, int startIndex, int length) {
		init();
		csDecoder.reset();
		ByteBuffer bb = ByteBuffer.wrap(page, startIndex, length);
		CharBuffer cb = CharBuffer.allocate(length);
		csDecoder.decode(bb, cb, false);
		cb.flip();
		this.pagepart = cb.array();
		this.length = cb.remaining();
		if (bb.remaining() > 0) {
			decodeRest = new byte[bb.remaining()];
			bb.get(decodeRest);
		}
		index = startIndex;
	}

	/**
	 * Get a String describing the token.
	 * 
	 * @param token
	 *            the token type (like STRING).
	 * @return a String describing the token (like &quot;STRING&quot;)
	 */
	private String getTokenString(int token) {
		switch (token) {
		case START:
			return "START";
		case STRING:
			return "STRING";
		case SQSTRING:
			return "SQSTRING";
		case DQSTRING:
			return "DQSTRING";
		case SINGELQUOTE:
			return "SINGELQUOTE";
		case DOUBLEQUOTE:
			return "DOUBLEQUOTE";
		case LT:
			return "LT";
		case MT:
			return "MT";
		case EQUALS:
			return "EQUALS";
		case COMMENT:
			return "COMMENT";
		case END:
			return "END";
		case UNKNOWN:
			return "UNKNOWN";
		default:
			return "unknown";
		}
	}

	/**
	 * Scan a String from the block.
	 * 
	 * @throws HtmlParseException
	 *             if an error occurs.
	 * @return STRING
	 */
	private int scanString() {
		int endindex = length;
		int startindex = index - 1;

		if (tagmode) {
			loop: while (index < length) {
				switch (pagepart[index]) {
				case ' ':
				case '\t':
				case '\n':
				case '\r':
				case '\"':
				case '\'':
				case '<':
				case '>':
				case '=':
					endindex = index;
					break loop;
				default:
					index++;
				}
			}
		} else {
			while (index < length) {
				if (pagepart[index] == '<') {
					endindex = index;
					break;
				}
				index++;
			}
		}
		if (tagmode) {
			stringValue = new String(pagepart, startindex,
					(endindex - startindex));
		} else {
			stringLength = (endindex - startindex);
		}
		return STRING;
	}

	/**
	 * Scan a quoted tring from the block. The first character is treated as the
	 * quotation character.
	 * 
	 * @throws HtmlParseException
	 *             if an error occurs.
	 * @return SQSTRING, DQSTRING or UNKNOWN (for strange quotes).
	 */
	private int scanQuotedString() {
		int endindex = -1;
		int startindex = index - 1;
		char start = pagepart[startindex];
		while (index < length) {
			if (pagepart[index++] == start) {
				endindex = index;
				break;
			}
		}
		if (endindex == -1) {
			block.setRest(lastTagStart);
			return END;
		}
		int l = (endindex < length ? endindex : length) - startindex;
		stringValue = new String(pagepart, startindex, l);
		switch (start) {
		case '\'':
			return SQSTRING;
		case '"':
			return DQSTRING;
		default:
			return UNKNOWN;
		}
	}

	/**
	 * Is this tag a comment?
	 * 
	 * @return true if the block(at current index) starts with !--, false
	 *         otherwise.
	 */
	private boolean isComment() {
		if (index + 3 >= length)
			return false;
		return (pagepart[index] == '!' && pagepart[index + 1] == '-' && pagepart[index + 2] == '-');
	}

	/**
	 * Scan a comment from the block, that is the string up to and including
	 * &quot;-->&quot;.
	 * 
	 * @return COMMENT or END.
	 * @throws HtmlParseException
	 *             if the html can not be parsed
	 */
	private int scanComment() throws HtmlParseException {
		int startvalue = index - 1;
		int i = -1;
		int j = index;
		while (j + 2 < length) {
			if (pagepart[j] == '-' && pagepart[j + 1] == '-'
					&& pagepart[j + 2] == '>') {
				i = j;
				break;
			}
			j++;
		}
		if (i > -1) {
			index = i + 2;
			nextToken = MT;
			match(MT);
			stringLength = index - startvalue;
			return COMMENT;
		}
		block.setRest(startvalue);
		return END;
	}

	/**
	 * Match the token with next token and scan the (new)next token.
	 * 
	 * @param token
	 *            the token to match.
	 * @return the next token.
	 * @throws HtmlParseException
	 *             if the match fails
	 */
	private int match(int token) throws HtmlParseException {
		int ts;

		if (nextToken != token)
			throw new HtmlParseException("Token: " + getTokenString(token)
					+ " != " + getTokenString(nextToken));

		if (pendingComment != null) {
			nextToken = LT;
			pendingComment = null;
			return SCRIPT;
		}
		while (index < length) {
			tagStart = index;
			stringValue = null;
			switch (pagepart[index++]) {
			case ' ':
			case '\t':
			case '\n':
			case '\r':
				// A continue here may result in RabbIT cutting out
				// whitespaces from the html-page, but this seems to have
				// worked well for quite a few years.
				continue;
			case '<':
				ts = tagStart;
				if (isComment()) {
					nextToken = scanComment();
					tagStart = ts;
					return nextToken;
				}
				return nextToken = LT;
			case '>':
				return nextToken = MT;
			case '=':
				stringValue = "=";
				return nextToken = EQUALS;
			case '"':
				if (tagmode)
					return nextToken = scanQuotedString();
				// else fallthrough...
			case '\'':
				if (tagmode)
					return nextToken = scanQuotedString();
				// else fallthrough...
			default:
				return nextToken = scanString();
			}
		}

		return nextToken = END;
	}

	/**
	 * Scan a value from the block.
	 * 
	 * @return the value or null.
	 * @throws HtmlParseException
	 *             if the parsing fails
	 */
	private String value() throws HtmlParseException {
		if (nextToken == EQUALS) {
			match(EQUALS);
			if (nextToken == STRING || nextToken == SQSTRING
					|| nextToken == DQSTRING) {
				String val = stringValue;
				match(nextToken);
				return val;
			}
			return "";
		}
		return null;
	}

	private void setPendingComment(Token comment) {
		nextToken = SCRIPT;
		this.pendingComment = comment;
		tagStart = pendingComment.getStartIndex();
		stringLength = pendingComment.getLength();
	}

	/**
	 * Scan an argument list from the block.
	 * 
	 * @param tag
	 *            the Tag that have the arguments.
	 * @throws HtmlParseException
	 *             if the argument list can not be parsed
	 */
	private void arglist(Tag tag) throws HtmlParseException {
		String key;

		// System.err.println ("parsing arglist for tag: '" + tag + "'");
		while (true) {
			// System.err.println ("nextToken: " + nextToken + " => " +
			// getTokenString (nextToken));
			switch (nextToken) {
			case MT:
				tagmode = false;
				// ok, this is kinda ugly but safer this way
				if (tag.getLowerCaseType() != null
						&& (tag.getLowerCaseType().equals("script") || tag
								.getLowerCaseType().equals("style"))) {
					Token text = scanCommentUntilEnd(tag.getLowerCaseType());
					if (text != null) {
						setPendingComment(text);
					} else {
						tagmode = false;
						return;
					}
				} else {
					match(MT);
				}
				return;
			case STRING:
				key = stringValue;
				match(STRING);
				String value = value();
				tag.addArg(key, value, false);
				break;
			case END:
				return;
			case DQSTRING:
				String ttype = tag.getType();
				if (ttype != null && ttype.charAt(0) == '!') {
					// Handle <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01
					// Transitional//EN">
					// and similar cases.
					tag.addArg(stringValue, null, false);
					match(nextToken);
				} else {
					throw new HtmlParseException("strange arglist: " + tag);
					/* usually from javascript being sent as html. */
					/*
					 * This is what we used to do, keep for a while.
					 * //System.err.println ("hmmm, strange arglist..: " + tag);
					 * // this is probably due to something like: // ...
					 * framespacing="0""> // we backstep and change the second
					 * '"' to a blank and // restart from that point... index -=
					 * stringValue.length (); pagepart[index] = ' '; match
					 * (nextToken); tag.getToken ().setChanged (true);
					 */
				}
				break;
			case LT:
				// I consider this an error (in the html of the page)
				// but handle it anyway.
				String type = tag.getLowerCaseType();
				if (type == null || // <<</font.... etc
						stringValue == null) { // <table.. width=100% <tr>
					tagmode = false;
					return;
				}
				// fall through.
			default:
				// System.err.println ("hmmm, default arglist..: " + tag);
				// this is probably due to something like:
				// <img src=someimagead;ad=40;valu=560>
				// we will break at '=' and split the tag to something like:
				// <img src=someimagead;ad = 40;valu=560> if we change it.
				// the html is already broken so should we fix it?
				// we ignore for now..
				if (stringValue != null)
					tag.addArg(stringValue, null, false);
				match(nextToken);
			}
		}
	}

	/**
	 * Is this tag at the scan position?
	 * 
	 * @param tag
	 *            the tag to check for
	 * @param j
	 *            the position in the page
	 * @return true if the pagepart at j holds the given tag
	 */
	private boolean sameTag(String tag, int j) {
		int i;
		for (i = 0; i < tag.length() && i < length; i++) {
			char lb = Character.toLowerCase(tag.charAt(i));
			char pb = Character.toLowerCase(pagepart[j + i]);
			if (lb != pb)
				return false;
		}
		return true;
	}

	private Token scanCommentUntilEnd(String tag) {
		int len = tag.length();
		int startvalue = index;
		int i = -1;
		int j = index;
		while (j + 1 + len < length) {
			if (pagepart[j] == '<' && pagepart[j + 1] == '/'
					&& sameTag(tag, j + 2)) {
				i = j;
				break;
			}
			j++;
		}
		if (i > -1) {
			stringLength = j - startvalue;
			index = j;
			return new Token(pagepart, TokenType.COMMENT, startvalue,
					stringLength);
		}
		block.setRest(lastTagStart);
		return null;
	}

	/**
	 * Scan a tag from the block.
	 * 
	 * @param ltagStart
	 *            the index of the last tag started.
	 * @throws HtmlParseException
	 *             if the tag can not be parsed
	 */
	private void tag(int ltagStart) throws HtmlParseException {
		Tag tag = new Tag();
		Token token = new Token(tag, false);
		switch (nextToken) {
		case STRING:
			tag.setType(stringValue);
			match(STRING);
			arglist(tag);
			if (tagmode) {
				block.setRest(lastTagStart);
			} else {
				if (!block.hasRests()) {
					token.setStartIndex(ltagStart);
					block.addToken(token);
				}
			}
			break;
		case MT:
			tagmode = false;
			match(MT);
			break;
		case END:
			block.setRest(lastTagStart);
			tagmode = false;
			return;
		default:
			arglist(tag);
		}
	}

	private Token getToken(TokenType type) {
		return new Token(pagepart, type, tagStart, stringLength);
	}

	/**
	 * Scan a page from the block.
	 * 
	 * @throws HtmlParseException
	 *             if the page can not be parsed
	 */
	private void page() throws HtmlParseException {
		while (!block.hasRests()) {
			switch (nextToken) {
			case END:
				return;
			case LT:
				lastTagStart = tagStart;
				tagmode = true;
				match(LT);
				tag(lastTagStart);
				break;
			case COMMENT:
				block.addToken(getToken(TokenType.COMMENT));
				match(COMMENT);
				break;
			case SCRIPT:
				block.addToken(getToken(TokenType.SCRIPT));
				match(SCRIPT);
				break;
			case STRING:
			default:
				block.addToken(getToken(TokenType.TEXT));
				match(nextToken);
			}
		}
	}

	/**
	 * Get a HtmlBlock from the pagepart given.
	 * 
	 * @return the parsed block
	 * @throws HtmlParseException
	 *             if a block can not be parsed
	 */
	public HtmlBlock parse() throws HtmlParseException {
		block = new HtmlBlock(pagepart, length, cs, decodeRest);
		nextToken = START;
		match(START);
		page();

		return block;
	}
}

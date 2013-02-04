package com.googlecode.httpfilter.proxy.rabbit.html;

/**
 * This class is used to describe a small part of a html page. A small part is
 * the text between the tags, a tag, a comment or a script.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class Token {
	private String text;
	private char[] page;
	private Tag tag;
	private TokenType type;
	private boolean changed = false;
	private int startindex = 0;
	private int len = 0;

	/**
	 * Create a new Token of type TEXT with given text.
	 * 
	 * @param text
	 *            the String of this Token.
	 */
	public Token(String text) {
		this(text, TokenType.TEXT, -1);
		setChanged(true);
	}

	/**
	 * Create a new Token of type Tag with given Tag.
	 * 
	 * @param tag
	 *            the Tag of this Token.
	 */
	public Token(Tag tag) {
		this(tag, -1);
		setChanged(true);
	}

	/**
	 * Create a new Token of type Tag with given Tag.
	 * 
	 * @param tag
	 *            the Tag of this Token.
	 * @param changed
	 *            if true this tag is considered changed, if false this tag is
	 *            unmodified.
	 */
	public Token(Tag tag, boolean changed) {
		this(tag, -1);
		setChanged(changed);
	}

	/**
	 * Create a new Token with given arguments.
	 * 
	 * @param text
	 *            the text of this Token.
	 * @param type
	 *            the type of this token.
	 * @param startindex
	 *            the start index of this token.
	 */
	public Token(String text, TokenType type, int startindex) {
		this.text = text;
		this.type = type;
		this.startindex = startindex;
	}

	/**
	 * Create a new Token with given arguments.
	 * 
	 * @param page
	 *            the text of this Token.
	 * @param type
	 *            the type of this token.
	 * @param startindex
	 *            the start index of this token.
	 * @param len
	 *            the length of this token.
	 */
	public Token(char[] page, TokenType type, int startindex, int len) {
		this.page = page;
		this.type = type;
		this.startindex = startindex;
		this.len = len;
	}

	/**
	 * Create a new Token with given arguments and of type TAG.
	 * 
	 * @param tag
	 *            the Tag of this Token.
	 * @param startindex
	 *            the start index of this token.
	 */
	public Token(Tag tag, int startindex) {
		this.tag = tag;
		this.type = TokenType.TAG;
		tag.setToken(this);
		this.startindex = startindex;
	}

	/**
	 * Get the tag of this token.
	 * 
	 * @return the Tag or null if type is other than TAG.
	 */
	public Tag getTag() {
		return tag;
	}

	/**
	 * Set the tag of this token, also set the type to TAG.
	 * 
	 * @param tag
	 *            the Tag to hold.
	 */
	public void setTag(Tag tag) {
		this.tag = tag;
		this.type = TokenType.TAG;
		tag.setToken(this);
		this.changed = true;
	}

	/**
	 * Get the text of this token.
	 * 
	 * @return the text or null if type is other than TEXT.
	 */
	public String getText() {
		if ((type == TokenType.TEXT || type == TokenType.COMMENT)
				&& text == null)
			text = new String(page, startindex, len);
		return text;
	}

	/**
	 * Set the text of this Token, also sets the type to TEXT.
	 * 
	 * @param text
	 *            the text of this token.
	 */
	public void setText(String text) {
		this.text = text;
		this.type = TokenType.TEXT;
		this.changed = true;
	}

	/**
	 * Get the type of this token.
	 * 
	 * @return the type of this token.
	 */
	public TokenType getType() {
		return type;
	}

	/**
	 * Has this Token changed since it was created?
	 * 
	 * @return true if this Token has changed, false otherwise.
	 */
	public boolean getChanged() {
		return changed;
	}

	/**
	 * Set the change value of this Token.
	 * 
	 * @param changed
	 *            the new change value of this Token.
	 */
	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	/**
	 * Get the start index of this Token.
	 * 
	 * @return the start index.
	 */
	public int getStartIndex() {
		return startindex;
	}

	/**
	 * Set the start index of this Token.
	 * 
	 * @param startindex
	 *            the new startindex.
	 */
	public void setStartIndex(int startindex) {
		this.startindex = startindex;
	}

	/**
	 * Get the length of this token.
	 * 
	 * @return the length in chars
	 */
	public int getLength() {
		return len;
	}

	/**
	 * Empty this token, That is set its type to EMPTY and set the text and tag
	 * to null.
	 */
	public void empty() {
		type = TokenType.EMPTY;
		setChanged(false);
		text = null;
		tag = null;
	}

	/**
	 * Get the String representation of this object.
	 * 
	 * @return a String representation of this object.
	 */
	@Override
	public String toString() {
		if (text == null && page != null && type != TokenType.TAG)
			text = new String(page, startindex, len);
		switch (type) {
		case EMPTY:
			return "";
		case TEXT:
			return text;
		case TAG:
			return tag.toString();
		case COMMENT:
			return text;
		case SCRIPT:
			return text;
		default:
			return text;
		}
	}
}

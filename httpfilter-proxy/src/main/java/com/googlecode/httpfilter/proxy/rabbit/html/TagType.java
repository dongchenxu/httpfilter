package com.googlecode.httpfilter.proxy.rabbit.html;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a class that holds common tagtypes.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class TagType {
	private static final Map<String, TagType> types;
	private final String type;

	/** Start of an A-tag */
	public static final TagType A = new TagType("a");
	/** End of an A-tag */
	public static final TagType SA = new TagType("/a");
	/** Start of an APPLET-tag */
	public static final TagType APPLET = new TagType("applet");
	/** End of an APPLET-tag */
	public static final TagType SAPPLET = new TagType("/applet");
	/** Start of an IMG-tag */
	public static final TagType IMG = new TagType("img");
	/** End of an IMG-tag */
	public static final TagType SIMG = new TagType("/img");
	/** Start of an LAYER-tag */
	public static final TagType LAYER = new TagType("layer");
	/** End of an LAYER-tag */
	public static final TagType SLAYER = new TagType("/layer");
	/** Start of an OBJECT-tag */
	public static final TagType OBJECT = new TagType("object");
	/** End of an OBJECT-tag */
	public static final TagType SOBJECT = new TagType("/object");
	/** Start of an SCRIPT-tag */
	public static final TagType SCRIPT = new TagType("script");
	/** End of an SCRIPT-tag */
	public static final TagType SSCRIPT = new TagType("/script");
	/** Start of an STYLE-tag */
	public static final TagType STYLE = new TagType("style");
	/** End of an STYLE-tag */
	public static final TagType SSTYLE = new TagType("/style");
	/** Start of an BODY-tag */
	public static final TagType BODY = new TagType("body");
	/** End of an BODY-tag */
	public static final TagType SBODY = new TagType("/body");
	/** Start of an TABLE-tag */
	public static final TagType TABLE = new TagType("table");
	/** End of an TABLE-tag */
	public static final TagType STABLE = new TagType("/table");
	/** Start of an TR-tag */
	public static final TagType TR = new TagType("tr");
	/** End of an TR-tag */
	public static final TagType STR = new TagType("/tr");
	/** Start of an TD-tag */
	public static final TagType TD = new TagType("td");
	/** End of an TD-tag */
	public static final TagType STD = new TagType("/td");
	/** Start of an BLINK-tag */
	public static final TagType BLINK = new TagType("blink");
	/** End of an BLINK-tag */
	public static final TagType SBLINK = new TagType("/blink");
	/** Start of an DOCTYPE-tag */
	public static final TagType DOCTYPE = new TagType("!doctype");
	/** Start of an HTML-tag */
	public static final TagType HTML = new TagType("html");
	/** End of an HTML-tag */
	public static final TagType SHTML = new TagType("/html");
	/** Start of an HEAD-tag */
	public static final TagType HEAD = new TagType("head");
	/** End of an HEAD-tag */
	public static final TagType SHEAD = new TagType("/head");
	/** A BR-tag */
	public static final TagType BR = new TagType("br");
	/** Start of an FONT-tag */
	public static final TagType FONT = new TagType("font");
	/** End of an FONT-tag */
	public static final TagType SFONT = new TagType("/font");
	/** Start of an LI-tag */
	public static final TagType LI = new TagType("li");
	/** End of an LI-tag */
	public static final TagType SLI = new TagType("/li");
	/** Start of an B-tag */
	public static final TagType B = new TagType("b");
	/** End of an B-tag */
	public static final TagType SB = new TagType("/b");
	/** Start of an P-tag */
	public static final TagType P = new TagType("p");
	/** End of an P-tag */
	public static final TagType SP = new TagType("/p");
	/** Start of an TT-tag */
	public static final TagType TT = new TagType("tt");
	/** End of an TT-tag */
	public static final TagType STT = new TagType("/tt");
	/** Start of an SPAN-tag */
	public static final TagType SPAN = new TagType("span");
	/** End of an SPAN-tag */
	public static final TagType SSPAN = new TagType("/span");
	/** Start of an DIV-tag */
	public static final TagType DIV = new TagType("div");
	/** End of an DIV-tag */
	public static final TagType SDIV = new TagType("/div");
	/** Start of an FORM-tag */
	public static final TagType FORM = new TagType("form");
	/** End of an FORM-tag */
	public static final TagType SFORM = new TagType("/form");
	/** Start of an INPUT-tag */
	public static final TagType INPUT = new TagType("input");
	/** Start of an META-tag */
	public static final TagType META = new TagType("meta");
	/** End of an META-tag */
	public static final TagType SMETA = new TagType("/meta");
	/** Start of an TITLE-tag */
	public static final TagType TITLE = new TagType("title");
	/** End of an TITLE-tag */
	public static final TagType STITLE = new TagType("/title");
	/** Start of an FRAMESET-tag */
	public static final TagType FRAMESET = new TagType("frameset");
	/** End of an FRAMESET-tag */
	public static final TagType SFRAMESET = new TagType("/frameset");

	static {
		types = new HashMap<String, TagType>();
		types.put(A.toString(), A);
		types.put(SA.toString(), SA);
		types.put(APPLET.toString(), APPLET);
		types.put(SAPPLET.toString(), SAPPLET);
		types.put(IMG.toString(), IMG);
		types.put(SIMG.toString(), SIMG);
		types.put(LAYER.toString(), LAYER);
		types.put(SLAYER.toString(), SLAYER);
		types.put(OBJECT.toString(), OBJECT);
		types.put(SOBJECT.toString(), SOBJECT);
		types.put(SCRIPT.toString(), SCRIPT);
		types.put(SSCRIPT.toString(), SSCRIPT);
		types.put(STYLE.toString(), STYLE);
		types.put(SSTYLE.toString(), SSTYLE);
		types.put(BODY.toString(), BODY);
		types.put(SBODY.toString(), SBODY);
		types.put(TABLE.toString(), TABLE);
		types.put(STABLE.toString(), STABLE);
		types.put(TR.toString(), TR);
		types.put(STR.toString(), STR);
		types.put(TD.toString(), TD);
		types.put(STD.toString(), STD);
		types.put(BLINK.toString(), BLINK);
		types.put(SBLINK.toString(), SBLINK);
		types.put(DOCTYPE.toString(), DOCTYPE);
		types.put(HTML.toString(), HTML);
		types.put(SHTML.toString(), SHTML);
		types.put(HEAD.toString(), HEAD);
		types.put(SHEAD.toString(), SHEAD);
		types.put(BR.toString(), BR);
		types.put(FONT.toString(), FONT);
		types.put(SFONT.toString(), SFONT);
		types.put(LI.toString(), LI);
		types.put(SLI.toString(), SLI);
		types.put(B.toString(), B);
		types.put(SB.toString(), SB);
		types.put(P.toString(), P);
		types.put(SP.toString(), SP);
		types.put(TT.toString(), TT);
		types.put(STT.toString(), STT);
		types.put(SPAN.toString(), SPAN);
		types.put(SSPAN.toString(), SSPAN);
		types.put(DIV.toString(), DIV);
		types.put(SDIV.toString(), SDIV);
		types.put(FORM.toString(), FORM);
		types.put(SFORM.toString(), SFORM);
		types.put(INPUT.toString(), INPUT);
		types.put(META.toString(), META);
		types.put(SMETA.toString(), SMETA);
		types.put(TITLE.toString(), TITLE);
		types.put(STITLE.toString(), STITLE);
		types.put(FRAMESET.toString(), FRAMESET);
		types.put(SFRAMESET.toString(), SFRAMESET);
	}

	private TagType(String type) {
		this.type = type;
	}

	/**
	 * Get the TagType matching the given String.
	 * 
	 * @param type
	 *            the String to get the TagType for
	 * @return a TagType or null if no matching TagType exists
	 */
	public static TagType getTagType(String type) {
		return types.get(type);
	}

	@Override
	public String toString() {
		return type;
	}
}

/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2026 Actian Corporation, a division of HCL Software. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Actian,
 * the following license terms apply:
 *
 * This program is part of JasperReports.
 *
 * JasperReports is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JasperReports is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JasperReports. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.jasperreports.engine.export;

import java.util.Locale;

import net.sf.jasperreports.engine.fonts.FontFace;
import net.sf.jasperreports.engine.util.JRDataUtils;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class HtmlFont
{
	private static final String LOCALE_SEPARATOR = "_-";
	private static final int IE_FONT_NAME_MAX_LENGTH = 31;
	
	private HtmlFontFamily family;
	private Locale locale;
	private String fontName;
	private String ttf;
	private String eot;
	private String svg;
	private String woff;
	private boolean isBold;
	private boolean isItalic;
	
	private String id;
	private String shortId;
	
	/**
	 * 
	 */
	private HtmlFont(
		HtmlFontFamily family, 
		Locale locale, 
		FontFace fontFace, 
		boolean isBold, 
		boolean isItalic
		)
	{
		this.family = family;
		this.locale = locale;
		this.fontName = fontFace.getName();
		this.ttf = fontFace.getTtf();
		this.eot = fontFace.getEot();
		this.svg = fontFace.getSvg();
		this.woff = fontFace.getWoff();
		this.isBold = isBold;
		this.isItalic = isItalic;
		
		createIds();
	}
	
	/**
	 * 
	 */
	public static HtmlFont getInstance(HtmlFontFamily family, Locale locale, FontFace fontFace, boolean isBold, boolean isItalic)
	{
		HtmlFont htmlFont = null;

		if (
			fontFace.getTtf() != null
			|| fontFace.getEot() != null
			|| fontFace.getSvg() != null
			|| fontFace.getWoff() != null
			)
		{
			htmlFont = new HtmlFont(family, locale, fontFace, isBold, isItalic);
		}

		return htmlFont;
	}
	
	/**
	 * 
	 */
	private void createIds()
	{
		String prefix = fontName;
		String suffix =
			((isBold || isItalic) ? "-" : "")
			+ (isBold ? "Bold" : "")
			+ (isItalic ? "Italic" : "")
			+ (locale == null ? "" : (LOCALE_SEPARATOR + JRDataUtils.getLocaleCode(locale)));
		
		id = prefix + suffix;
		
		if (prefix.length() + suffix.length() > IE_FONT_NAME_MAX_LENGTH)
		{
			prefix = prefix.replaceAll("\\s", "");
		}
		if (prefix.length() + suffix.length() > IE_FONT_NAME_MAX_LENGTH)
		{
			suffix =
				((isBold || isItalic) ? "-" : "")
				+ (isBold ? "B" : "")
				+ (isItalic ? "I" : "")
				+ (locale == null ? "" : (LOCALE_SEPARATOR + JRDataUtils.getLocaleCode(locale)));
		}
		if (prefix.length() + suffix.length() > IE_FONT_NAME_MAX_LENGTH)
		{
			prefix = prefix.substring(0, 1) + prefix.substring(1).replaceAll("[AaEeIiOoUu]", "");
		}
		if (prefix.length() + suffix.length() > IE_FONT_NAME_MAX_LENGTH)
		{
			prefix = prefix.substring(0, IE_FONT_NAME_MAX_LENGTH - suffix.length());
		}
		
		shortId = prefix + suffix;
	}
	
	/**
	 * 
	 */
	public String getId()
	{
		return id;
	}
	
	/**
	 * 
	 */
	public String getShortId()
	{
		return shortId;
	}
	
	public HtmlFontFamily getFamily()
	{
		return family;
	}
	
	public Locale getLocale()
	{
		return locale;
	}
	
	public String getFontName()
	{
		return fontName;
	}
	
	public String getTtf()
	{
		return ttf;
	}
	
	public String getEot()
	{
		return eot;
	}
	
	public String getSvg()
	{
		return svg;
	}
	
	public String getWoff()
	{
		return woff;
	}
	
	public boolean isBold()
	{
		return isBold;
	}
	
	public boolean isItalic()
	{
		return isItalic;
	}
}

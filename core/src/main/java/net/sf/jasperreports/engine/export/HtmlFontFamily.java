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

import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.fonts.FontFamily;
import net.sf.jasperreports.engine.fonts.FontInfo;
import net.sf.jasperreports.engine.fonts.FontUtil;
import net.sf.jasperreports.engine.util.JRDataUtils;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class HtmlFontFamily
{
	public static final String LOCALE_SEPARATOR = "_-";
	private static final int IE_FONT_NAME_MAX_LENGTH = 31;
	
	private Locale locale;
	private FontFamily fontFamily;
	
	private String id;
	private String shortId;
	
	/**
	 * 
	 */
	private HtmlFontFamily(Locale locale, FontFamily fontFamily)
	{
		this.locale = locale;
		this.fontFamily = fontFamily;
		
		createIds();
	}
	
	/**
	 * 
	 */
	public static HtmlFontFamily getInstance(Locale locale, FontInfo fontInfo)
	{
		HtmlFontFamily htmlFont = null;
		
		if (fontInfo != null)
		{
			FontFamily family = fontInfo.getFontFamily();
			
			htmlFont = new HtmlFontFamily(locale, family);
		}
		
		return htmlFont;
	}
	
	/**
	 * 
	 */
	public static HtmlFontFamily getInstance(JasperReportsContext jasperReportsContext, String htmlFontFamilyId)
	{
		int localeSeparatorPos = htmlFontFamilyId.lastIndexOf(LOCALE_SEPARATOR);
		String familyName = htmlFontFamilyId.substring(0, localeSeparatorPos);
		String localeCode = htmlFontFamilyId.substring(localeSeparatorPos + LOCALE_SEPARATOR.length());
		Locale locale = JRDataUtils.getLocale(localeCode);
		
		FontInfo fontInfo = FontUtil.getInstance(jasperReportsContext).getFontInfo(familyName, locale);

		return getInstance(locale, fontInfo);
	}
	
	/**
	 * 
	 */
	private void createIds()
	{
		String prefix = fontFamily.getName();
		String suffix =
			(locale == null ? "" : (LOCALE_SEPARATOR + JRDataUtils.getLocaleCode(locale)));
		
		id = prefix + suffix;
		
		if (prefix.length() + suffix.length() > IE_FONT_NAME_MAX_LENGTH)
		{
			prefix = prefix.replaceAll("\\s", "");
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
	
	public Locale getLocale()
	{
		return locale;
	}
	
	public FontFamily getFontFamily()
	{
		return fontFamily;
	}
}

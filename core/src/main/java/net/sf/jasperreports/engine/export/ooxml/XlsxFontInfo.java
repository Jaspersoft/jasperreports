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
package net.sf.jasperreports.engine.export.ooxml;

import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.util.JRColorUtil;
import net.sf.jasperreports.engine.util.ObjectUtils;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class XlsxFontInfo
{
	/**
	 *
	 */
	protected String fontName;
	protected float fontSize;
	protected boolean isBold;
	protected boolean isItalic;
	protected boolean isUnderline;
	protected boolean isStrikeThrough;
	protected String color;

	/**
	 *
	 */
	public XlsxFontInfo(JRPrintElement element, String fontName, boolean isFontSizeFixEnabled)
	{
		if (element != null)
		{
			this.color = JRColorUtil.getColorHexa(element.getForecolor());
		}
		
		JRFont font = element instanceof JRFont ? (JRFont)element : null;
		if (font != null)
		{
			this.fontName = fontName;
			this.fontSize = font.getFontSize() + (isFontSizeFixEnabled ? -1 : 0);
			this.isBold = font.isBold();
			this.isItalic = font.isItalic();
			this.isUnderline = font.isUnderline();
			this.isStrikeThrough = font.isStrikeThrough();
		}
	}
	
	@Override
	public int hashCode()
	{
		int hash = 47 + ObjectUtils.hashCode(fontName);
		hash = 29 * hash + Float.hashCode(fontSize);
		hash = 29 * hash + Boolean.hashCode(isBold);
		hash = 29 * hash + Boolean.hashCode(isItalic);
		hash = 29 * hash + Boolean.hashCode(isUnderline);
		hash = 29 * hash + Boolean.hashCode(isStrikeThrough);
		hash = 29 * hash + ObjectUtils.hashCode(color);
		return hash;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof XlsxFontInfo))
		{
			return false;
		}
		
		XlsxFontInfo info = (XlsxFontInfo) obj;
		return ObjectUtils.equals(fontName, info.fontName)
				&& ObjectUtils.equals(fontSize, info.fontSize)
				&& isBold == info.isBold
				&& isItalic == info.isItalic
				&& isUnderline == info.isUnderline
				&& isStrikeThrough == info.isStrikeThrough
				&& ObjectUtils.equals(color, info.color);
	}
}

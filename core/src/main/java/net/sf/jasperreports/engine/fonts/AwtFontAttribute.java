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
package net.sf.jasperreports.engine.fonts;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.Map;

import net.sf.jasperreports.engine.util.JRTextAttribute;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class AwtFontAttribute
{

	public static AwtFontAttribute fromAttributes(Map<Attribute,Object> attributes)
	{
		String family = (String) attributes.get(TextAttribute.FAMILY);
		FontInfo fontInfo = (FontInfo) attributes.get(JRTextAttribute.FONT_INFO);
		return new AwtFontAttribute(family, fontInfo);
	}
	
	private final String family;
	private final FontInfo fontInfo;
	
	public AwtFontAttribute(String family, FontInfo fontInfo)
	{
		this.family = family;
		this.fontInfo = fontInfo;
	}

	public boolean hasAttribute()
	{
		return family != null || fontInfo != null;
	}
	
	public String getFamily()
	{
		return family;
	}

	public FontInfo getFontInfo()
	{
		return fontInfo;
	}
	
	public void putAttributes(Map<Attribute,Object> attributes)
	{
		attributes.put(TextAttribute.FAMILY, family);
		if (fontInfo != null)
		{
			attributes.put(JRTextAttribute.FONT_INFO, fontInfo);
		}
	}

	@Override
	public int hashCode()
	{
		int hash = 43;
		hash = hash*29 + family.hashCode();
		if (fontInfo != null)
		{
			hash = hash*29 + fontInfo.getStyle();
			if (fontInfo.getFontFace() != null && fontInfo.getFontFace().getFont() != null)
			{
				hash = hash*29 + fontInfo.getFontFace().getFont().hashCode();
			}
		}
		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof AwtFontAttribute))
		{
			return false;
		}
		
		AwtFontAttribute other = (AwtFontAttribute) obj;
		if (!family.equals(other.family))
		{
			return false;
		}
		
		if (fontInfo == null)
		{
			return other.fontInfo == null;
		}
		
		if (other.fontInfo == null)
		{
			return false;
		}
		
		if (fontInfo.getStyle() != other.fontInfo.getStyle())
		{
			return false;
		}
		
		Font font = fontInfo.getFontFace() == null ? null : fontInfo.getFontFace().getFont();
		Font otherFont = other.fontInfo.getFontFace() == null ? null : other.fontInfo.getFontFace().getFont();
		return font == null ? otherFont == null : (otherFont != null && font.equals(otherFont));
	}

	@Override
	public String toString()
	{
		StringBuilder string = new StringBuilder();
		string.append("{family: ");
		string.append(family);
		if (fontInfo != null)
		{
			string.append(", style: ");
			string.append(fontInfo.getStyle());
			
			if (fontInfo.getFontFace() != null && fontInfo.getFontFace().getFont() != null)
			{
				string.append(", font: ");
				string.append(fontInfo.getFontFace().getFont());
			}
		}
		string.append("}");
		return string.toString();
	}
	
}

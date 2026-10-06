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
package net.sf.jasperreports.renderers;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.Map;

import org.apache.batik.gvt.font.AWTFontFamily;
import org.apache.batik.gvt.font.AWTGVTFont;
import org.apache.batik.gvt.font.GVTFont;

import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.export.HtmlFontFamily;
import net.sf.jasperreports.engine.fonts.FontUtil;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class BatikAWTFontFamily extends AWTFontFamily
{
	private final JasperReportsContext jasperReportsContext;
	
	public BatikAWTFontFamily(
		JasperReportsContext jasperReportsContext,
		String familyName
		) 
	{
		super(familyName);
		
		this.jasperReportsContext = jasperReportsContext;
	}

	@Override
	public GVTFont deriveFont(float size, Map attrs) 
	{
		if (font != null)
			return new AWTGVTFont(font, size);

		String fontFamilyName = fontFace.getFamilyName();
		if (
			fontFamilyName.startsWith("'")
			&& fontFamilyName.endsWith("'")
			)
		{
			fontFamilyName = fontFamilyName.substring(1, fontFamilyName.length() - 1);
		}

		// svg font-family could have locale suffix because it is needed in svg measured by phantomjs;
		int localeSeparatorPos = fontFamilyName.lastIndexOf(HtmlFontFamily.LOCALE_SEPARATOR);
		if (localeSeparatorPos > 0)
		{
			fontFamilyName = fontFamilyName.substring(0, localeSeparatorPos);
		}

		Font awtFont = 
			FontUtil.getInstance(jasperReportsContext).getAwtFontFromBundles(
				true,
				fontFamilyName,
				(TextAttribute.WEIGHT_BOLD.equals(attrs.get(TextAttribute.WEIGHT)) ? Font.BOLD : Font.PLAIN)
				| (TextAttribute.POSTURE_OBLIQUE.equals(attrs.get(TextAttribute.POSTURE)) ? Font.ITALIC : Font.PLAIN),
				size, 
				null,//FIXMEBATIK locale 
				true
				);
		
		if (awtFont != null)
		{
			return new AWTGVTFont(awtFont);
		}

		return super.deriveFont(size, attrs);
	}
}

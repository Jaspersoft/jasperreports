/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2025 Cloud Software Group, Inc. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
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
package net.sf.jasperreports.pdf.classic.producer;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.io.IOException;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.BaseFont;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.pdf.common.FontRecipient;
import net.sf.jasperreports.pdf.common.PdfFontStyle;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ClassicFontRecipient implements FontRecipient
{

	private ColorSpace cmykColorSpace;
	private Font font;

	public ClassicFontRecipient(ColorSpace cmykColorSpace)
	{
		this.cmykColorSpace = cmykColorSpace;
	}
	
	@Override
	public boolean hasFont()
	{
		return font != null;
	}
	
	public Font getFont()
	{
		return font;
	}

	@Override
	public void setFont(String pdfFontName, String pdfEncoding, boolean isPdfEmbedded, 
			float size, PdfFontStyle pdfFontStyle, Color forecolor)
	{
		Font font = FontFactory.getFont(pdfFontName, pdfEncoding, isPdfEmbedded, 
				size, toPdfFontStyle(pdfFontStyle), ClassicPdfUtils.convertColor(cmykColorSpace, forecolor));
		// check if FontFactory didn't find the font
		if (font != null && font.getBaseFont() == null && font.getFamily() == Font.UNDEFINED)
		{
			font = null;
		}
		this.font = font;
	}

	@Override
	public void setFont(String pdfFontName, String pdfEncoding, boolean isPdfEmbedded,
			float size, PdfFontStyle pdfFontStyle, Color forecolor,
			byte[] fontData)
	{
		BaseFont baseFont;
		try
		{
			baseFont = BaseFont.createFont(pdfFontName, pdfEncoding, isPdfEmbedded,
					true, fontData, null);
		}
		catch (DocumentException | IOException e)
		{
			throw new JRRuntimeException(e);
		}

		font = new Font(baseFont, size, toPdfFontStyle(pdfFontStyle), ClassicPdfUtils.convertColor(cmykColorSpace, forecolor));
	}
	
	protected static int toPdfFontStyle(PdfFontStyle pdfFontStyle)
	{
		return (pdfFontStyle.isBold() ? Font.BOLD : 0)
				| (pdfFontStyle.isItalic() ? Font.ITALIC : 0)
				| (pdfFontStyle.isUnderline() ? Font.UNDERLINE : 0)
				| (pdfFontStyle.isStrikethrough() ? Font.STRIKETHRU : 0);
	}
	
}

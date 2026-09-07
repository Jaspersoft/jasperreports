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
package net.sf.jasperreports.openpdf.producer;

import java.text.AttributedCharacterIterator.Attribute;
import java.util.HashMap;
import java.util.Map;

import org.openpdf.text.Font;
import org.openpdf.text.pdf.BaseFont;
import org.openpdf.text.pdf.FontMapper;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class StandardPdfFontMapper implements FontMapper
{

	private StandardPdfProducer pdfProducer;

	public StandardPdfFontMapper(StandardPdfProducer pdfProducer)
	{
		this.pdfProducer = pdfProducer;
	}

	@Override
	public BaseFont awtToPdf(java.awt.Font font)
	{
		// not setting underline and strikethrough as we only need the base font.
		// underline and strikethrough will not work here because PdfGraphics2D
		// doesn't check the font attributes.
		Map<Attribute,Object> atts = new HashMap<>();
		atts.putAll(font.getAttributes());
		Font pdfFont = pdfProducer.getFont(atts, null);
		return pdfFont.getBaseFont();
	}

	@Override
	public java.awt.Font pdfToAwt(BaseFont font, int size)
	{
		return null;
	}

}

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

import com.lowagie.text.Chunk;
import com.lowagie.text.Font;

import net.sf.jasperreports.pdf.common.PdfTextChunk;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ClassicTextChunk extends ClassicChunk implements PdfTextChunk
{
	
	private Font font;

	public ClassicTextChunk(ClassicPdfProducer pdfProducer, Chunk chunk, Font font)
	{
		super(pdfProducer, chunk);
		
		this.font = font;
	}

	@Override
	public void setUnderline()
	{
		// using the same values as sun.font.Fond2D
		chunk.setUnderline(null, 0, 1f / 18, 0, -1f / 12, 0);
	}

	@Override
	public void setStrikethrough()
	{
		// using the same thickness as sun.font.Fond2D.
		// the position is calculated in Fond2D based on the ascent, defaulting 
		// to OpenPDF default position which depends on the font size
		chunk.setUnderline(null, 0, 1f / 18, 0, 1f / 3, 0);
	}

	@Override
	public void setSuperscript()
	{
		chunk.setTextRise(font.getCalculatedLeading(1f)/2);
	}

	@Override
	public void setSubscript()
	{
		chunk.setTextRise(-font.getCalculatedLeading(1f)/2);
	}

	@Override
	public void setBackground(Color backcolor)
	{
		chunk.setBackground(backcolor);
	}

}

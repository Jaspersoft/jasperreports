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

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfWriter;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.pdf.common.PdfChunk;
import net.sf.jasperreports.pdf.common.PdfPhrase;
import net.sf.jasperreports.pdf.common.PdfTextAlignment;
import net.sf.jasperreports.pdf.common.TextDirection;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class StandardPhrase implements PdfPhrase
{

	private StandardPdfProducer pdfProducer;
	private Phrase phrase;
	private boolean markedContentChunks;

	public StandardPhrase(StandardPdfProducer pdfProducer, Phrase phrase)
	{
		this.pdfProducer = pdfProducer;
		this.phrase = phrase;
	}

	@Override
	public void add(PdfChunk chunk)
	{
		StandardChunk standardChunk = (StandardChunk) chunk;
		markedContentChunks |= standardChunk.getMarkedContentTag() != null;
		phrase.add(standardChunk.getChunk());
	}

	@Override
	public float go(float llx, float lly, float urx, float ury, 
			float fixedLeading, float multipliedLeading, 
			PdfTextAlignment alignment, TextDirection runDirection)
	{
		// chunks that carry a structure element need the marked content operators to be written in
		// between the text showing operators, which requires a content byte of our own
		StandardPdfProducer.MarkedContentCanvas markedContentCanvas =
			markedContentChunks ? pdfProducer.createMarkedContentCanvas() : null;

		ColumnText colText =
			new ColumnText(markedContentCanvas == null ? pdfProducer.getPdfContentByte() : markedContentCanvas);
		colText.setSimpleColumn(phrase, 
				llx, lly, urx, ury, 
				fixedLeading, 
				StandardPdfUtils.toPdfAlignment(alignment));
		if (multipliedLeading != 0f)
		{
			colText.setLeading(fixedLeading, multipliedLeading);
		}
		colText.setRunDirection(toPdfRunDirection(runDirection));
		try
		{
			colText.go();
		}
		catch (DocumentException e)
		{
			throw new JRRuntimeException(e);
		}

		if (markedContentCanvas != null)
		{
			// the text of the column has been appended to it by the layout
			pdfProducer.getPdfContentByte().add(markedContentCanvas);
		}

		return colText.getYLine();
	}
	
	protected static int toPdfRunDirection(TextDirection direction)
	{
		int pdfDirection;
		switch (direction)
		{
		case DEFAULT:
			pdfDirection = PdfWriter.RUN_DIRECTION_DEFAULT;
			break;
		case LTR:
			pdfDirection = PdfWriter.RUN_DIRECTION_LTR;
			break;
		case RTL:
			pdfDirection = PdfWriter.RUN_DIRECTION_RTL;
			break;
		default:
			throw new JRRuntimeException("Unknown text direction " + direction);
		}
		return pdfDirection;
	}

}

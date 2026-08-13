/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2023 Cloud Software Group, Inc. All rights reserved.
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
package net.sf.jasperreports.pdf;

import java.text.AttributedString;

import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.pdf.common.PdfTagger;
import net.sf.jasperreports.pdf.common.PdfProducer;
import net.sf.jasperreports.pdf.common.PdfTextRendererContext;


/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class LineBreaksPdfTextRenderer extends SimpleAbstractPdfTextRenderer
{

	private static final char LINE_BREAK_CHAR = '\u0085';

	private static final String LINE_BREAK_STRING = Character.toString(LINE_BREAK_CHAR);
	
	public LineBreaksPdfTextRenderer(JasperReportsContext jasperReportsContext, 
		PdfTextRendererContext context)
	{
		super(jasperReportsContext, context);
	}

	@Override
	public void initialize(JRPdfExporter pdfExporter, PdfProducer pdfProducer, 
			PdfTagger pdfTagger,
			JRPrintText text, JRStyledText styledText, 
			int offsetX, int offsetY)
	{
		if (text.getLineBreakOffsets() != null)
		{
			styledText.insert(LINE_BREAK_STRING, text.getLineBreakOffsets());
		}
		
		super.initialize(pdfExporter, pdfProducer, pdfTagger,
			text, styledText, offsetX, offsetY);
	}

	@Override
	protected void createParagraphPhrase(AttributedString paragraph, String paragraphText)
	{
		int lineBreakIndex = paragraphText.indexOf(LINE_BREAK_CHAR);
		int lineStart = 0;
		if (lineBreakIndex >= 0)
		{
			do
			{
				createPhrase(paragraph, lineStart, lineBreakIndex, paragraphText, true);
				
				lineStart = lineBreakIndex + 1;
				lineBreakIndex = paragraphText.indexOf('\u0085', lineBreakIndex + 1);
			}
			while(lineBreakIndex >= 0);
		}
		
		//last line
		createPhrase(paragraph, lineStart, paragraphText.length(), paragraphText, isLastParagraph && justifyLastLine);
	}
}

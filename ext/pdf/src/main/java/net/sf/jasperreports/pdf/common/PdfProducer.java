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
package net.sf.jasperreports.pdf.common;

import java.io.IOException;
import java.io.OutputStream;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.Locale;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.PrintPageFormat;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.pdf.AbstractPdfTextRenderer;
import net.sf.jasperreports.renderers.Graphics2DRenderable;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public interface PdfProducer
{

	PdfProducerContext getContext();
	
	PdfDocument createDocument(PrintPageFormat pageFormat);

	PdfDocumentWriter createWriter(OutputStream os) throws JRException;

	PdfStructure getPdfStructure();

	PdfContent createPdfContent();

	PdfContent getPdfContent();

	void initReport();

	void setForceLineBreakPolicy(boolean forceLineBreakPolicy);

	void newPage();

	void setPageSize(PrintPageFormat pageFormat, int pageWidth, int pageHeight);

	void endPage();

	default AbstractPdfTextRenderer getTextRenderer(PdfTextRendererContext context)
	{
		return 
			getTextRenderer(
				context.getPrintText(), context.getStyledText(), context.getTextLocale(), 
				context.getAwtIgnoreMissingFont(), context.getIndentFirstLine(), context.getJustifyLastLine()
				);
	}
	
	/**
	 * @deprecated Replaced by {@link #getTextRenderer(PdfTextRendererContext)}.
	 */
	AbstractPdfTextRenderer getTextRenderer(
			JRPrintText text, JRStyledText styledText, Locale textLocale, 
			boolean awtIgnoreMissingFont, boolean defaultIndentFirstLine, boolean defaultJustifyLastLine);
	
	PdfImage createImage(byte[] loadBytesFromResource, boolean verify) throws IOException, JRException;

	PdfImage clipImage(
			PdfImage image, 
			int clipWidth, int clipHeight, 
			int translateX, int translateY
			) throws JRException;

	PdfImage drawImage(
			JRPrintImage image, Graphics2DRenderable renderer, boolean forceSvgShapes, 
			double renderWidth, double renderHeight
			) throws JRException, IOException;
	
	PdfTextChunk createChunk(String text, Map<Attribute, Object> attributes, Locale locale);

	PdfChunk createChunk(PdfImage imageContainer);

	PdfPhrase createPhrase();

	PdfPhrase createPhrase(PdfChunk chunk);

	PdfTextField createTextField(float llx, float lly, float urx, float ury, String fieldName);

	PdfTextField createComboField(float llx, float lly, float urx, float ury, String fieldName,
			String value, String[] choices);

	PdfTextField createListField(float llx, float lly, float urx, float ury, String fieldName,
			String value, String[] choices);

	PdfRadioCheck createCheckField(float llx, float lly, float urx, float ury, String fieldName, 
			String onValue);

	PdfRadioCheck getRadioField(float llx, float lly, float urx, float ury, String fieldName, 
			String onValue);
	
	PdfOutlineEntry getRootOutline();
	
	void addPdfPage(byte[] pdfData, int x, int y, int width, int height);
	
	void close();
	
}

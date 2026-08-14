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
package net.sf.jasperreports.pdf.classic;

import java.awt.font.GlyphVector;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfString;

import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.pdf.AbstractPdfTextRenderer;
import net.sf.jasperreports.pdf.JRPdfExporter;
import net.sf.jasperreports.pdf.classic.producer.StandardPdfProducer;
import net.sf.jasperreports.pdf.common.PdfProducer;
import net.sf.jasperreports.pdf.common.PdfTagger;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class PdfGlyphRenderer extends AbstractPdfTextRenderer
{
	private static final Log log = LogFactory.getLog(PdfGlyphRenderer.class);
	
	private static final boolean PATCHED_LIBRARY;
	static
	{
		//TODO not actually needed with OpenPDF
		PATCHED_LIBRARY = determinePatchedLibrary();
	}

	private static boolean determinePatchedLibrary()
	{
		try
		{
			PdfContentByte.class.getMethod("showText", GlyphVector.class);
			return true;
		} 
		catch (NoSuchMethodException e) 
		{
			log.warn("Unpatched PDF library found, cannot use glyph rendering");
			return false;
		} 
		catch (SecurityException e) 
		{
			throw new JRRuntimeException(e);
		}
	}
	
	public static boolean supported()
	{
		return PATCHED_LIBRARY;
	}

	private StandardPdfProducer classicPdfProducer;
	private PdfContentByte pdfContentByte;
	
	private boolean addActualText;	
	private PdfGlyphGraphics2D pdfGraphics2D;
	
	public PdfGlyphRenderer(
		JasperReportsContext jasperReportsContext, 
		boolean ignoreMissingFont, 
		boolean defaultIndentFirstLine,
		boolean defaultJustifyLastLine,
		boolean addActualText
		)
	{
		super(
			jasperReportsContext, 
			ignoreMissingFont,
			defaultIndentFirstLine,
			defaultJustifyLastLine
			);
		this.addActualText = addActualText;
	}

	@Override
	public void initialize(
		JRPdfExporter pdfExporter, 
		PdfProducer pdfProducer,
		PdfTagger pdfTagger,
		JRPrintText text,
		JRStyledText styledText, 
		int offsetX, 
		int offsetY
		)
	{
		if (!(pdfProducer instanceof StandardPdfProducer))
		{
			throw new IllegalArgumentException("Only ClassicPdfProducer is supported");
		}
		super.initialize(pdfExporter, pdfProducer, pdfTagger, text, styledText, offsetX, offsetY);
		
		classicPdfProducer = (StandardPdfProducer) pdfProducer;
		pdfContentByte = classicPdfProducer.getPdfContentByte();
	}

	@Override
	public void render()
	{
		Locale locale = pdfExporter.getTextLocale(text);
		pdfGraphics2D = new PdfGlyphGraphics2D(pdfContentByte, pdfExporter, classicPdfProducer, locale);

		boolean addText = addActualText && !classicPdfProducer.getContext().isTagged();
		if (addText)
		{
			PdfDictionary markedContentProps = new PdfDictionary();
			markedContentProps.put(PdfName.ACTUALTEXT, new PdfString(allText, PdfObject.TEXT_UNICODE));
			pdfContentByte.beginMarkedContentSequence(PdfName.SPAN, markedContentProps, true);
		}

		super.render();

		if (addText)
		{
			pdfContentByte.endMarkedContentSequence();
		}
		
		pdfGraphics2D.dispose();
	}
	
	
	@Override
	public void draw()
	{
		if (bulletChunk != null)
		{
			AttributedCharacterIterator bulletIterator = bulletChunk.getIterator();
			LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(bulletIterator, getFontRenderContext());//grx.getFontRenderContext()

			TextLayout bulletLayout =
				lineMeasurer.nextLayout(
					1000,
					bulletIterator.getEndIndex(),
					true
					);

			bulletLayout.draw(
				pdfGraphics2D,
				pdfExporter.toPoints(x + drawPosX) - bulletLayout.getVisibleAdvance() - 10,
				pdfExporter.toPoints(y + topPadding + verticalAlignOffset + drawPosY)
				);
		}

		TabSegment segment = segments.get(segmentIndex);
		segment.layout.draw(
				pdfGraphics2D,
				pdfExporter.toPoints(x + drawPosX),// + leftPadding,
				pdfExporter.toPoints(y + topPadding + verticalAlignOffset + drawPosY)
				);
		
		return;
	}

	@Override
	public boolean addActualText()
	{
		return addActualText;
	}
	

}

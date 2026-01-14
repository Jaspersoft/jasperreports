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

import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.type.RunDirectionEnum;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.pdf.common.PdfPhrase;
import net.sf.jasperreports.pdf.common.PdfProducer;
import net.sf.jasperreports.pdf.common.PdfTextAlignment;
import net.sf.jasperreports.pdf.common.PdfTextRendererContext;
import net.sf.jasperreports.pdf.common.TextDirection;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class SimpleAbstractPdfTextRenderer extends AbstractPdfTextRenderer
{

	private float yLine = 0;
	/**
	 * @deprecated To be removed.
	 */
	protected boolean legacyTextMeasuringFix;
	
	/**
	 * 
	 */
	public SimpleAbstractPdfTextRenderer(
		JasperReportsContext jasperReportsContext, 
		PdfTextRendererContext context
		)
	{
		super(
			jasperReportsContext, 
			context.getAwtIgnoreMissingFont(),
			context.getIndentFirstLine(),
			context.getJustifyLastLine()
			);
		
		this.legacyTextMeasuringFix = context.getLegacyTextMeasuringFix();
	}

	
	/**
	 * @deprecated Replaced by {@link #SimpleAbstractPdfTextRenderer(JasperReportsContext, PdfTextRendererContext)}.
	 */
	public SimpleAbstractPdfTextRenderer(
		JasperReportsContext jasperReportsContext, 
		boolean ignoreMissingFont,
		boolean defaultIndentFirstLine,
		boolean defaultJustifyLastLine
		)
	{
		super(
			jasperReportsContext, 
			ignoreMissingFont,
			defaultIndentFirstLine,
			defaultJustifyLastLine
			);
	}

	
	@Override
	public void initialize(
		JRPdfExporter pdfExporter, 
		PdfProducer pdfProducer,
		JRPdfExporterTagHelper tagHelper,
		JRPrintText text, 
		JRStyledText styledText, 
		int offsetX,
		int offsetY
		)
	{
		super.initialize(
			pdfExporter, 
			pdfProducer,
			tagHelper,
			text, 
			styledText, 
			offsetX,
			offsetY
			);
		
		yLine = 
			pdfExporter.getCurrentPageFormat().getPageHeight()
				- y
				- topPadding
				- verticalAlignOffset
				- text.getLeadingOffset();
	}

	
	@Override
	public void render()
	{
		super.render();
	}

	
	@Override
	protected void renderParagraph(
		AttributedCharacterIterator allParagraphs,
		int paragraphStart,
		String paragraphText
		)
	{
		tagHelper.startText(text);

		if (bulletChunk != null)
		{
			PdfPhrase phrase = pdfProducer.createPhrase();
			pdfExporter.getPhrase(bulletChunk, bulletText, text, phrase);

			phrase.go(
				x + leftPadding,
				yLine,
				htmlListIndent + x + leftPadding - 10,
				pdfExporter.getCurrentPageFormat().getPageHeight()
					- y
					- height
					+ bottomPadding,
				0,//text.getLineSpacingFactor(),// * text.getFont().getSize(),
				text.getLineSpacingFactor(),
				PdfTextAlignment.RIGHT,
				TextDirection.LTR
				);
		}

		bulletText = null;
		bulletChunk = null;
		
		if (paragraphText == null)
		{
			String emptyText = " ";
			AttributedString paragraph = 
				new AttributedString(
					emptyText,
					new AttributedString(
						allParagraphs, 
						paragraphStart, 
						paragraphStart + emptyText.length()
						).getIterator().getAttributes()
					);
			createPhrase(paragraph, 0, emptyText.length(), emptyText, false);
		}
		else
		{
			AttributedString paragraph = 
					new AttributedString(
						allParagraphs, 
						paragraphStart, 
						paragraphStart + paragraphText.length()
						);
			createParagraphPhrase(paragraph, paragraphText);
		}
		
		tagHelper.endText();
	}
	
	protected abstract void createParagraphPhrase(AttributedString paragraph, String paragraphText);

	protected void createPhrase(AttributedString paragraph, int startIndex, int endIndex, 
			String paragraphText, boolean justifyAll)
	{
		PdfPhrase phrase = pdfProducer.createPhrase();
		pdfExporter.getPhrase(paragraph, startIndex, endIndex, paragraphText, text, phrase);
		yLine = phrase.go(
			htmlListIndent + x + leftPadding,
			yLine,
			x + width - rightPadding,
			pdfExporter.getCurrentPageFormat().getPageHeight()
				- y
				- height
				+ bottomPadding
				- (legacyTextMeasuringFix ? 1 : 0),
			0,//text.getLineSpacingFactor(),// * text.getFont().getSize(),
			text.getLineSpacingFactor(),
			horizontalAlignment == PdfTextAlignment.JUSTIFIED && justifyAll 
				? PdfTextAlignment.JUSTIFIED_ALL : horizontalAlignment,
			text.getRunDirection() == RunDirectionEnum.LTR
				? TextDirection.LTR : TextDirection.RTL
			);
	}


	@Override
	protected AttributedString getAttributedString()
	{
		return styledText.getAttributedString();
	}


	@Override
	public void draw()
	{
		//nothing to do
	}


	@Override
	public boolean addActualText()
	{
		return false;
	}
}

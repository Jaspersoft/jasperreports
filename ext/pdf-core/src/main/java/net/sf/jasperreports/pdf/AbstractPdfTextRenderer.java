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
package net.sf.jasperreports.pdf;

import java.text.AttributedCharacterIterator;

import net.sf.jasperreports.engine.JRCommonText;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.export.AbstractTextRenderer;
import net.sf.jasperreports.engine.type.RunDirectionEnum;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.engine.util.JRStyledText.Run;
import net.sf.jasperreports.engine.util.JRTextAttribute;
import net.sf.jasperreports.engine.util.StyledTextListWriter;
import net.sf.jasperreports.pdf.common.PdfProducer;
import net.sf.jasperreports.pdf.common.PdfTagger;
import net.sf.jasperreports.pdf.common.PdfTextAlignment;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class AbstractPdfTextRenderer extends AbstractTextRenderer
{
	/**
	 * 
	 */
	protected JRPdfExporter pdfExporter;
	protected PdfProducer pdfProducer;
	protected PdfTagger pdfTagger;
	protected PdfTextAlignment horizontalAlignment;
	protected float leftOffsetFactor;
	protected float rightOffsetFactor;
	protected boolean styledTextChunkTags;

	
	/**
	 * 
	 */
	public AbstractPdfTextRenderer(
		JasperReportsContext jasperReportsContext, 
		boolean ignoreMissingFont,
		boolean defaultIndentFirstLine,
		boolean defaultJustifyLastLine
		)
	{
		super(
			jasperReportsContext, 
			false, 
			ignoreMissingFont, 
			defaultIndentFirstLine, 
			defaultJustifyLastLine
			);
	}
	
	
	/**
	 * 
	 */
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
		this.pdfExporter = pdfExporter;
		this.pdfProducer = pdfProducer;
		this.pdfTagger = pdfTagger;
		
		// accessibility tags and hyperlinks that only cover parts of the text require the marked
		// content of the paragraph to be created by its chunks, each of them going into a
		// structure element of its own; hyperlinks set on the element itself are tagged as a Link
		// element that wraps the whole text, so they do not need structure elements per run; the
		// styled text can only contain tags or hyperlinks if the markup of the text was parsed
		this.styledTextChunkTags =
			pdfProducer.getContext().isTagged()
			&& supportsStyledTextChunkTags()
			&& !JRCommonText.MARKUP_NONE.equals(text.getMarkup())
			&& hasStyledTextChunkTags(styledText, text.getLinkType() == null);
		
		horizontalAlignment = PdfTextAlignment.LEFT;
		leftOffsetFactor = 0f;
		rightOffsetFactor = 0f;
		
		//FIXMETAB 0.2f was a fair approximation
		switch (text.getHorizontalTextAlign())
		{
			case JUSTIFIED :
			{
				horizontalAlignment = PdfTextAlignment.JUSTIFIED;
				leftOffsetFactor = 0f;
				rightOffsetFactor = 0f;
				break;
			}
			case RIGHT :
			{
				if (text.getRunDirection() == RunDirectionEnum.RTL)
				{
					horizontalAlignment = PdfTextAlignment.LEFT;
				}
				else
				{
					horizontalAlignment = PdfTextAlignment.RIGHT;
				}
				leftOffsetFactor = -0.2f;
				rightOffsetFactor = 0f;
				break;
			}
			case CENTER :
			{
				horizontalAlignment = PdfTextAlignment.CENTER;
				leftOffsetFactor = -0.1f;
				rightOffsetFactor = 0.1f;
				break;
			}
			case LEFT :
			default :
			{
				if (text.getRunDirection() == RunDirectionEnum.RTL)
				{
					horizontalAlignment = PdfTextAlignment.RIGHT;
				}
				else
				{
					horizontalAlignment = PdfTextAlignment.LEFT;
				}
				leftOffsetFactor = 0f;
				rightOffsetFactor = 0.2f;
				break;
			}
		}

		super.initialize(text, styledText, offsetX, offsetY);
	}
	
	/**
	 * Determines whether the styled text has runs that require structure elements of their own,
	 * that is, runs carrying accessibility tags (<code>&lt;reference&gt;</code> or
	 * <code>&lt;note&gt;</code>) or, when the text element as a whole has no hyperlink, runs
	 * carrying hyperlinks of their own.
	 */
	protected static boolean hasStyledTextChunkTags(JRStyledText styledText, boolean includeHyperlinks)
	{
		for (Run run : styledText.getRuns())
		{
			if (
				run.attributes != null
				&& (run.attributes.containsKey(JRTextAttribute.REFERENCE)
					|| run.attributes.containsKey(JRTextAttribute.NOTE)
					|| (includeHyperlinks && run.attributes.containsKey(JRTextAttribute.HYPERLINK)))
				)
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected StyledTextListWriter getListWriter()
	{
		return pdfTagger.getListWriter();
	}
	
	public abstract boolean addActualText();
	
	/**
	 * Determines whether this renderer writes the text of a paragraph as chunks, each of which can
	 * carry the structure element that it belongs to.
	 *
	 * <p>
	 * This is what the accessibility tags and the hyperlinks in the styled text require, because
	 * the marked content sequence of a tagged portion of the text can only be created while the
	 * text is laid out, and because the position of a hyperlink annotation is only known then.
	 * Renderers that draw the text themselves, without going through chunks, do not support them
	 * and leave the marked content of the whole paragraph in the tag of the paragraph.
	 * </p>
	 */
	protected boolean supportsStyledTextChunkTags()
	{
		return true;
	}
	
	 @Override
	protected void renderParagraph(
		AttributedCharacterIterator allParagraphs, 
		int paragraphStart,
		String paragraphText
		) 
	 {
		pdfTagger.startText(text, addActualText() ? paragraphText : null, styledTextChunkTags);
		
		super.renderParagraph(allParagraphs, paragraphStart, paragraphText);
		
		pdfTagger.endText();
	}
}

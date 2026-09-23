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
	protected boolean styledTextHyperlinks;

	
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
		
		// hyperlinks set on the element itself are tagged as a Link element that wraps the whole
		// text, so only the hyperlinks coming from the styled text need Link tags of their own;
		// the styled text can only contain hyperlinks if the markup of the text was parsed
		this.styledTextHyperlinks =
			pdfProducer.getContext().isTagged()
			&& text.getLinkType() == null
			&& !JRCommonText.MARKUP_NONE.equals(text.getMarkup())
			&& hasStyledTextHyperlinks(styledText);
		
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
	 * Determines whether the styled text contains hyperlinks set on some of its runs, as opposed
	 * to a hyperlink set on the text element as a whole.
	 */
	protected static boolean hasStyledTextHyperlinks(JRStyledText styledText)
	{
		for (Run run : styledText.getRuns())
		{
			if (run.attributes != null && run.attributes.containsKey(JRTextAttribute.HYPERLINK))
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
	
	 @Override
	protected void renderParagraph(
		AttributedCharacterIterator allParagraphs, 
		int paragraphStart,
		String paragraphText
		) 
	 {
		pdfTagger.startText(text, addActualText() ? paragraphText : null, styledTextHyperlinks);
		
		super.renderParagraph(allParagraphs, paragraphStart, paragraphText);
		
		pdfTagger.endText();
	}
}

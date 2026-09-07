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

import java.text.AttributedString;

import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.pdf.common.PdfTextRendererContext;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class SimplePdfTextRenderer extends SimpleAbstractPdfTextRenderer
{
	
	/**
	 * 
	 */
	public SimplePdfTextRenderer(
		JasperReportsContext jasperReportsContext, 
		PdfTextRendererContext context
		)
	{
		super(
			jasperReportsContext, 
			context
			);
	}

	
	/**
	 * @deprecated Replaced by {@link #SimplePdfTextRenderer(JasperReportsContext, PdfTextRendererContext)}.
	 */
	public SimplePdfTextRenderer(
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
	protected void createParagraphPhrase(AttributedString paragraph, String paragraphText)
	{
		createPhrase(paragraph, 0, paragraphText.length(), paragraphText, isLastParagraph && justifyLastLine);
	}
}

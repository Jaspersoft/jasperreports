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

import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.util.StyledTextListWriter;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class DefaultPdfTagger implements PdfTagger
{
	public static final String EXCEPTION_MESSAGE_KEY_PDF_TAGS_NOT_SUPPORTED = "export.pdf.tags.not.supported";

	private boolean silent;
	
	public DefaultPdfTagger()
	{
	}

	public DefaultPdfTagger(boolean silent)
	{
		this.silent = silent;
	}

	@Override
	public void setLanguage(String language)
	{
	}

	@Override
	public void setPdfProducer(PdfProducer pdfProducer)
	{
	}

	@Override
	public void init()
	{
		if (!silent)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_PDF_TAGS_NOT_SUPPORTED,  
					(Object[])null 
					);
		}
	}

	@Override
	public void startPage()
	{
	}

	@Override
	public void endPage()
	{
	}

	@Override
	public void startElement(JRPrintElement element)
	{
	}

	@Override
	public void endElement(JRPrintElement element)
	{
	}

	@Override
	public void beginArtifact()
	{
	}

	@Override
	public void endArtifact()
	{
	}

	@Override
	public void startImage(JRPrintImage printImage, float llx, float lly, float urx, float ury)
	{
	}

	@Override
	public void endImage()
	{
	}

	@Override
	public void startText(JRPrintText textElement)
	{
	}

	@Override
	public void startText(JRPrintText textElement, String actualText)
	{
	}

	@Override
	public void endText()
	{
	}

	@Override
	public boolean isFirstLinkParagraph()
	{
		return false;
	}

	@Override
	public PdfStructureEntry getCurrentLinkTag()
	{
		return null;
	}

	@Override
	public StyledTextListWriter getListWriter()
	{
		return null;
	}
}

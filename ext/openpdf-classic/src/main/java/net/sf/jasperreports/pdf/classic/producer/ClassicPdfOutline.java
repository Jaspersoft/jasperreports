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

import com.lowagie.text.pdf.PdfDestination;
import com.lowagie.text.pdf.PdfOutline;

import net.sf.jasperreports.pdf.common.PdfOutlineEntry;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ClassicPdfOutline implements PdfOutlineEntry
{

	private PdfOutline pdfOutline;

	public ClassicPdfOutline(PdfOutline pdfOutline)
	{
		this.pdfOutline = pdfOutline;
	}

	@Override
	public PdfOutlineEntry createChild(String title)
	{
		PdfOutline childOutline = new PdfOutline(pdfOutline, pdfOutline.getPdfDestination(), title, false);
		return new ClassicPdfOutline(childOutline);
	}

	@Override
	public PdfOutlineEntry createChild(String title, float left, float top)
	{
		PdfDestination destination = new PdfDestination(PdfDestination.XYZ, left, top, 0);
		PdfOutline childOutline = new PdfOutline(pdfOutline, destination, title, false);
		return new ClassicPdfOutline(childOutline);
	}

}

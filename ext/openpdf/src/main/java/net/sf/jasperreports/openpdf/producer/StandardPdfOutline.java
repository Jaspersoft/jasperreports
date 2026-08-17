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
package net.sf.jasperreports.openpdf.producer;

import org.openpdf.text.pdf.PdfAction;
import org.openpdf.text.pdf.PdfArray;
import org.openpdf.text.pdf.PdfDestination;
import org.openpdf.text.pdf.PdfName;
import org.openpdf.text.pdf.PdfNumber;
import org.openpdf.text.pdf.PdfOutline;
import org.openpdf.text.pdf.PdfWriter;

import net.sf.jasperreports.pdf.common.PdfOutlineEntry;
import net.sf.jasperreports.pdf.common.PdfStructureEntry;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class StandardPdfOutline implements PdfOutlineEntry
{

	private static final PdfName PDF_NAME_SD = new PdfName("SD");
	
	private PdfOutline pdfOutline;
	private PdfWriter pdfWriter;
	private boolean pdf2;

	public StandardPdfOutline(PdfOutline pdfOutline, PdfWriter pdfWriter, boolean pdf2)
	{
		this.pdfOutline = pdfOutline;
		this.pdfWriter = pdfWriter;
		this.pdf2 = pdf2;
	}

	@Override
	public PdfOutlineEntry createChild(String title)
	{
		PdfOutline childOutline = new PdfOutline(pdfOutline, pdfOutline.getPdfDestination(), title, false);
		return new StandardPdfOutline(childOutline, pdfWriter, pdf2);
	}

	@Override
	public PdfOutlineEntry createChild(String title, float left, float top, PdfStructureEntry structureEntry)
	{
		PdfDestination destination = new PdfDestination(PdfDestination.XYZ, left, top, 0);
		PdfOutline childOutline;
		if (structureEntry == null || !pdf2)
		{
			childOutline = new PdfOutline(pdfOutline, destination, title, false);
		}
		else
		{
			int currentPageNumber = pdfWriter.getCurrentPageNumber();
			PdfAction action = PdfAction.gotoLocalPage(currentPageNumber, destination, pdfWriter);

			PdfArray structDest = new PdfArray();
			structDest.add(((StandardStructureEntry) structureEntry).getElement().getReference());
			structDest.add(PdfName.XYZ);
			structDest.add(new PdfNumber(left));
			structDest.add(new PdfNumber(top));
			structDest.add(new PdfNumber(0));
			action.put(PDF_NAME_SD, structDest);

			childOutline = new PdfOutline(pdfOutline, action, title, false);
		}
		return new StandardPdfOutline(childOutline, pdfWriter, pdf2);
	}

}

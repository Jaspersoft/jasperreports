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
package net.sf.jasperreports.engine.export.ooxml;

import java.io.Writer;

import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.PrintPageFormat;
import net.sf.jasperreports.engine.export.CutsInfo;
import net.sf.jasperreports.engine.export.JRGridLayout;
import net.sf.jasperreports.engine.export.LengthUtil;
import net.sf.jasperreports.engine.export.ooxml.type.PaperSizeEnum;
import net.sf.jasperreports.engine.type.OrientationEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class DocxDocumentHelper extends BaseHelper
{
	protected static int DEFAULT_LINE_PITCH = 360;

	/**
	 * 
	 */
	public DocxDocumentHelper(JasperReportsContext jasperReportsContext, Writer writer)
	{
		super(jasperReportsContext, writer);
	}
	
	/**
	 *
	 */
	public void exportHeader(PrintPageFormat pageFormat)
	{
		write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		write("<w:document\n");
		write(" xmlns:ve=\"http://schemas.openxmlformats.org/markup-compatibility/2006\"\n");
		write(" xmlns:o=\"urn:schemas-microsoft-com:office:office\"\n");
		write(" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"\n");
		write(" xmlns:m=\"http://schemas.openxmlformats.org/officeDocument/2006/math\"\n");
		write(" xmlns:v=\"urn:schemas-microsoft-com:vml\"\n");
		write(" xmlns:wp=\"http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing\"\n");
		write(" xmlns:w10=\"urn:schemas-microsoft-com:office:word\"\n");
		write(" xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\"\n");
		write(" xmlns:wne=\"http://schemas.microsoft.com/office/word/2006/wordml\"\n");
		write(" xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\"\n");
		write(" xmlns:pic=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">\n"); 
		write(" <w:body>\n");
	}

	/**
	 *
	 */
	public void exportSection(PrintPageFormat pageFormat, JRGridLayout pageGridLayout, boolean isSizePageToContent, int headerIndex, boolean lastPage)
	{
		CutsInfo xCuts = pageGridLayout.getXCuts();
		CutsInfo yCuts = pageGridLayout.getYCuts();
		
		int leftMargin = 0;
		int rightMargin = 0;
		int topMargin = 0;
		int bottomMargin = 0;
		int pageWidth = 0;
		int pageHeight = 0;

		if (isSizePageToContent)
		{
			leftMargin = pageFormat.getLeftMargin();
			rightMargin = pageFormat.getRightMargin();
			pageWidth =
				Math.max(
					pageFormat.getPageWidth(), 
					Math.max(leftMargin, xCuts.getCutOffset(0)) + xCuts.getTotalLength() + rightMargin
					);

			topMargin = pageFormat.getTopMargin();
			bottomMargin = pageFormat.getBottomMargin();
			pageHeight =
				Math.max(
					pageFormat.getPageHeight(), 
					Math.max(topMargin, yCuts.getCutOffset(0)) + yCuts.getTotalLength() + bottomMargin
					);
		}
		else
		{
			leftMargin = Math.min(xCuts.getCutOffset(0), pageFormat.getLeftMargin());
			leftMargin = leftMargin < 0 ? 0 : leftMargin;
			rightMargin = Math.min(pageFormat.getPageWidth() - xCuts.getCutOffset(xCuts.size() - 1), pageFormat.getRightMargin());
			rightMargin = rightMargin < 0 ? 0 : rightMargin;
			pageWidth = pageFormat.getPageWidth();

			topMargin = Math.min(yCuts.getCutOffset(0), pageFormat.getTopMargin());
			topMargin = topMargin < 0 ? 0 : topMargin;
			bottomMargin = Math.min(pageFormat.getPageHeight() - yCuts.getCutOffset(yCuts.size() - 1), pageFormat.getBottomMargin());
			bottomMargin = bottomMargin < 0 ? 0 : bottomMargin;
			pageHeight = pageFormat.getPageHeight();
		}
		
		if (!lastPage)
		{
			write("    <w:p>\n");
			write("    <w:pPr>\n");
		}
		write("  <w:sectPr>\n");
		if (headerIndex > 0) // headers are being created
		{
			write("   <w:headerReference w:type=\"default\" r:id=\"header" + headerIndex + "\"/>\n");
			write("   <w:type w:val=\"continuous\"/>\n");
		}
		write("   <w:pgSz w:w=\"" + LengthUtil.twip(pageWidth) + "\" w:h=\"" + LengthUtil.twip(pageHeight) + "\"");
		write(" w:orient=\"" + (pageFormat.getOrientation() == OrientationEnum.LANDSCAPE ? "landscape" : "portrait") + "\"");
		
		if (OoxmlUtils.getSuitablePaperSize(pageWidth, pageHeight) == PaperSizeEnum.UNDEFINED)
		{
			// unique identifier for the paper size
			write(" w:code=\""+ (1000 + pageWidth + pageHeight) +"\"");
		}
		write("/>\n");

		write("   <w:pgMar w:top=\""
				+ LengthUtil.twip(topMargin)
				+ "\" w:right=\""
				+ LengthUtil.twip(rightMargin)
				+ "\" w:bottom=\""
				+ bottomMargin
				+ "\" w:left=\""
				+ LengthUtil.twip(leftMargin)
				+ "\" w:header=\"0\" w:footer=\"0\" w:gutter=\"0\" />\n");
//		write("   <w:cols w:space=\"720\" />\n");
		write("   <w:docGrid w:linePitch=\"" + DEFAULT_LINE_PITCH + "\" />\n");
		write("  </w:sectPr>\n");
		if (!lastPage)
		{
			write("    </w:pPr>\n");
			write("    </w:p>\n");
		}
	}

	/**
	 *
	 */
	public void exportFooter()
	{
		write(" </w:body>\n");
		write("</w:document>\n");
	}

}

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

import net.sf.jasperreports.engine.JRPrintElementIndex;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.PrintPageFormat;
import net.sf.jasperreports.engine.export.CutsInfo;
import net.sf.jasperreports.engine.export.JRExporterGridCell;
import net.sf.jasperreports.engine.export.LengthUtil;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class DocxTableHelper extends BaseHelper
{
	/**
	 * 
	 */
	private CutsInfo xCuts;
	private DocxCellHelper cellHelper;
	private DocxParagraphHelper paragraphHelper;
	private PrintPageFormat pageFormat;
	private JRPrintElementIndex frameIndex;
	private int rowMaxTopPadding;

	/**
	 * 
	 */
	protected DocxTableHelper(
		JasperReportsContext jasperReportsContext,
		Writer writer,
		CutsInfo xCuts,
		boolean pageBreak,
		PrintPageFormat pageFormat,
		JRPrintElementIndex frameIndex
		) 
	{
		super(jasperReportsContext, writer);

		this.xCuts = xCuts;
		this.cellHelper = new DocxCellHelper(jasperReportsContext, writer);
		this.paragraphHelper = new DocxParagraphHelper(jasperReportsContext, writer, pageBreak);
		this.pageFormat = pageFormat;
		this.frameIndex = frameIndex;
	}


	/**
	 * 
	 */
	public DocxCellHelper getCellHelper() 
	{
		return cellHelper;
	}
	

	/**
	 * 
	 */
	public DocxParagraphHelper getParagraphHelper() 
	{
		return paragraphHelper;
	}
	

	/**
	 * 
	 */
	public void exportHeader() 
	{
		write("  <w:tbl>\n");
		write("   <w:tblPr>\n");
		write("    <w:tblLayout w:type=\"fixed\"/>\n");
		write("   </w:tblPr>\n");
		write("   <w:tblGrid>\n");
		int firstCutOffset = xCuts.getCutOffset(0);
		if (frameIndex == null && firstCutOffset > pageFormat.getLeftMargin())
		{
			write("    <w:gridCol w:w=\"" + LengthUtil.twip(firstCutOffset - pageFormat.getLeftMargin()) + "\"/>\n");
		}
		for (int col = 1; col < xCuts.size(); col++)
		{
			write("    <w:gridCol w:w=\"" + LengthUtil.twip(xCuts.getCutOffset(col) - xCuts.getCutOffset(col - 1)) + "\"/>\n");
		}
		int lastColumnOffset = xCuts.getCutOffset(xCuts.size() - 1);
		if (frameIndex == null && lastColumnOffset < pageFormat.getPageWidth() - pageFormat.getRightMargin())
		{
			write("    <w:gridCol w:w=\"" + LengthUtil.twip(pageFormat.getPageWidth() - pageFormat.getRightMargin() - lastColumnOffset) + "\"/>\n");
		}
		write("   </w:tblGrid>\n");
	}
	
	public void exportFooter() 
	{
		write("  </w:tbl>\n");
	}
	
	public void exportRowHeader(int rowHeight, boolean allowRowResize) 
	{
		write("   <w:tr>\n");
		write("    <w:trPr>\n");
		write("     <w:trHeight w:hRule=\"" + (allowRowResize ? "atLeast" : "exact")  + "\" w:val=\"" +  + LengthUtil.twip(rowHeight) + "\" />\n");
		write("    </w:trPr>\n");
	}
	
	public void exportRowFooter() 
	{
		write("   </w:tr>\n");
	}
	
	public void exportEmptyCell(JRExporterGridCell gridCell, int emptyCellColSpan, boolean startPage, long bookmarkIndex, String pageAnchor)
	{
		write("    <w:tc>\n");
		write("     <w:tcPr>\n");
		if (frameIndex != null)
		{
			// starting with MS Office 2409, column width needed to be specified, but only for nested tables;
			// this also avoids the complicated case of the first column of the top level table which has its width
			// take into account the page margin
			write("      <w:tcW w:w=\"" + LengthUtil.twip(gridCell.getWidth()) +"\" w:type=\"dxa\"/>\n");
		}
		if (emptyCellColSpan > 1)
		{
			write("      <w:gridSpan w:val=\"" + emptyCellColSpan +"\" />\n");
		}
		
		if (gridCell != null)
		{
			cellHelper.exportProps(gridCell);
		}
		
		write("     </w:tcPr>\n");
		
		paragraphHelper.exportEmptyParagraph(startPage, bookmarkIndex, pageAnchor);

		write("    </w:tc>\n");
	}

	public void exportOccupiedCells(JRExporterGridCell gridCell, boolean startPage, long bookmarkIndex, String pageAnchor)
	{
		write("    <w:tc>\n");
		write("     <w:tcPr>\n");
		if (frameIndex != null)
		{
			write("      <w:tcW w:w=\"" + LengthUtil.twip(gridCell.getWidth()) +"\" w:type=\"dxa\"/>\n");
		}
		if (gridCell.getColSpan() > 1)
		{
			write("      <w:gridSpan w:val=\"" + gridCell.getColSpan() +"\" />\n");
		}
		write("      <w:vMerge w:val=\"continue\" />\n");
		
		cellHelper.exportProps(gridCell.getElement(), gridCell);
		
		write("     </w:tcPr>\n");
		
		paragraphHelper.exportEmptyParagraph(startPage, bookmarkIndex, pageAnchor);

		cellHelper.exportFooter();
	}
	
	public int getRowMaxTopPadding()
	{
		return rowMaxTopPadding;
	}
	
	public void setRowMaxTopPadding(int rowMaxTopPadding)
	{
		this.rowMaxTopPadding = rowMaxTopPadding;
	}
}
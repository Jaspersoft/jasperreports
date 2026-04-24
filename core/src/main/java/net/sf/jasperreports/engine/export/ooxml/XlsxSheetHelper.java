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

import java.awt.Color;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.PrintPageFormat;
import net.sf.jasperreports.engine.export.Cut;
import net.sf.jasperreports.engine.export.ExcelAbstractExporter;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporter;
import net.sf.jasperreports.engine.export.LengthUtil;
import net.sf.jasperreports.engine.export.XlsRowLevelInfo;
import net.sf.jasperreports.engine.export.ooxml.type.PaperSizeEnum;
import net.sf.jasperreports.engine.type.OrientationEnum;
import net.sf.jasperreports.engine.util.FileBufferedWriter;
import net.sf.jasperreports.engine.util.JRColorUtil;
import net.sf.jasperreports.engine.util.JRStringUtil;
import net.sf.jasperreports.export.XlsReportConfiguration;
import net.sf.jasperreports.util.Base64Util;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class XlsxSheetHelper extends BaseHelper
{
	private int rowIndex;
	
	private FileBufferedWriter colsWriter = new FileBufferedWriter();
	private FileBufferedWriter mergedCellsWriter = new FileBufferedWriter();
	private FileBufferedWriter hyperlinksWriter = new FileBufferedWriter();
	
	/**
	 *
	 */
	private XlsxSheetRelsHelper sheetRelsHelper;//FIXMEXLSX truly embed the rels helper here and no longer have it available from outside; check drawing rels too
	
	private List<Integer> rowBreaks = new ArrayList<>();

	private final boolean collapseRowSpan;
	private final Integer fitWidth;
	private final Integer fitHeight;
	private final Boolean autoFitPageHeight;
	private final boolean defaultAutoFitRow;

	/**
	 * 
	 */
	public XlsxSheetHelper(
		JasperReportsContext jasperReportsContext,
		Writer writer, 
		XlsxSheetRelsHelper sheetRelsHelper,
		XlsReportConfiguration configuration
		)
	{
		super(jasperReportsContext, writer);
		
		this.sheetRelsHelper = sheetRelsHelper;
		collapseRowSpan = configuration.isCollapseRowSpan();
		fitWidth = configuration.getFitWidth();
		fitHeight = configuration.getFitHeight();
		autoFitPageHeight = configuration.isAutoFitPageHeight();
		defaultAutoFitRow = configuration.isAutoFitRow();
	}

	/**
	 *
	 */
	public void exportHeader(
			boolean showGridlines, 
			int scale, 
			int rowFreezeIndex, 
			int columnFreezeIndex, 
			int maxColumnFreezeIndex, 
			JasperPrint jasperPrint, 
			Color tabColor)
	{
		write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		write("<worksheet\n");
		write(" xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"\n");
		write(" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">\n");
		
		write("<sheetPr>\n");
		if(tabColor != null)
		{
			write("<tabColor rgb=\"FF" + JRColorUtil.getColorHexa(tabColor) + "\"/>\n");
		}
		write("<outlinePr summaryBelow=\"0\"/>\n");
		
		/* the scale factor takes precedence over fitWidth and fitHeight properties */
		if (
			(scale < 10 || scale > 400)
			&& (
				fitWidth != null 
				|| fitHeight != null
				|| Boolean.TRUE == autoFitPageHeight //FIXME equals?
				)
			)
		{
			write("<pageSetUpPr fitToPage=\"1\"/>");
		}
		
		write("</sheetPr><dimension ref=\"A1\"/><sheetViews><sheetView workbookViewId=\"0\"");
		
		if(!showGridlines)
		{
			write(" showGridLines=\"0\"");
		}
		
		if(rowFreezeIndex > 0 || columnFreezeIndex > 0)
		{
			write(">\n<pane" + (columnFreezeIndex > 0 ? (" xSplit=\"" + columnFreezeIndex + "\"") : "") + (rowFreezeIndex > 0 ? (" ySplit=\"" + rowFreezeIndex + "\"") : ""));
			String columnFreezeName = columnFreezeIndex < 0 
					? "A" 
					: JRXlsAbstractExporter.getColumIndexName(columnFreezeIndex, maxColumnFreezeIndex);
			write(" topLeftCell=\"" + columnFreezeName + (rowFreezeIndex + 1) + "\"");
			String activePane = (rowFreezeIndex > 0 ? "bottom" : "top") + (columnFreezeIndex > 0 ? "Right" : "Left");
			write(" activePane=\"" + activePane + "\" state=\"frozen\"/>\n");
			write("<selection pane=\"" + activePane + "\"");
			write(" activeCell=\"" + columnFreezeName + (rowFreezeIndex + 1) + "\"");
			write(" sqref=\"" + columnFreezeName + (rowFreezeIndex + 1) + "\"");
			write("/>\n");
			write("</sheetView>\n</sheetViews>\n");
		}
		else
		{
			write("/></sheetViews>\n");
		}
		write("<sheetFormatPr defaultRowHeight=\"15\"/>\n");
	}
	

	/**
	 *
	 */
	public void exportFooter(
		int index, 
		PrintPageFormat jasperPrint, 
		boolean isIgnorePageMargins, 
		String autoFilter,
		Integer scale,
		Integer firstPageNumber,
		boolean firstPageNotSet, 
		Integer sheetPageCount,
		JRXlsAbstractExporter.SheetInfo.SheetPrintSettings printSettings,
		String password
		)
	{
		if (rowIndex > 0)
		{
			write("</row>\n");
		}
		else
		{
			if (!colsWriter.isEmpty())
			{
				write("<cols>\n");
				colsWriter.writeData(writer);
				write("</cols>\n");
			}
			write("<sheetData>\n");
		}
		write("</sheetData>\n");
		
		if (password != null && password.trim().length() > 0)
		{
			byte[] salt = OoxmlUtils.getSalt();
			int spinCount = 100;
			byte[] passwordHash = OoxmlUtils.sha512(password, salt, spinCount);
			
			write("<sheetProtection algorithmName=\"SHA-512\" hashValue=\"");
			write(Base64Util.encode(passwordHash, false));
			write("\" saltValue=\"");
			write(Base64Util.encode(salt, false));
			write("\" spinCount=\"" + spinCount + "\" sheet=\"1\" objects=\"1\" scenarios=\"1\"/>\n");
		}

		if(autoFilter != null)
		{
			write("<autoFilter ref=\"" + autoFilter + "\"/>\n");
		}
		
		if (!mergedCellsWriter.isEmpty())
		{
			write("<mergeCells>\n");
			mergedCellsWriter.writeData(writer);
			write("</mergeCells>\n");
		}
		if (!hyperlinksWriter.isEmpty())
		{
			write("<hyperlinks>\n");
			hyperlinksWriter.writeData(writer);
			write("</hyperlinks>\n");
		}

		write("<pageMargins left=\"");
		write(String.valueOf(LengthUtil.inchFloor4Dec(printSettings.getLeftMargin()))); 
		write("\" right=\"");
		write(String.valueOf(LengthUtil.inchFloor4Dec(printSettings.getRightMargin()))); 
		write("\" top=\"");
		write(String.valueOf(LengthUtil.inchFloor4Dec(printSettings.getTopMargin()))); 
		write("\" bottom=\"");
		write(String.valueOf(LengthUtil.inchFloor4Dec(printSettings.getBottomMargin()))); 
		write("0");
		write("\" header=\"");
		write(String.valueOf(LengthUtil.inchFloor4Dec(printSettings.getHeaderMargin()))); 
		write("\" footer=\"");
		write(String.valueOf(LengthUtil.inchFloor4Dec(printSettings.getFooterMargin()))); 
		write("\"/>\n");
		write("<pageSetup");	
		
		write(" orientation=\"" + OrientationEnum.getValueOrDefault(jasperPrint.getOrientation()).getName().toLowerCase() + "\"");	
		
		/* the scale factor takes precedence over fitWidth and fitHeight properties */
		if(scale != null && scale > 9 && scale < 401)
		{
			write(" scale=\"" + scale + "\"");	
		}
		else
		{
			if (fitWidth != null && fitWidth !=  1)
			{
				write(" fitToWidth=\"" + fitWidth + "\"");
			}
			Integer sheetFitHeight = fitHeight == null
				? (Boolean.TRUE == autoFitPageHeight //FIXME equals?
					? sheetPageCount
					: null)
				: fitHeight;
			if (sheetFitHeight != null && sheetFitHeight != 1)
			{
				write(" fitToHeight=\"" + sheetFitHeight + "\"");
			}
		}
		
		PaperSizeEnum pSize = OoxmlUtils.getSuitablePaperSize(printSettings);
		String paperSize = pSize == PaperSizeEnum.UNDEFINED ? "" : " paperSize=\"" + pSize.getOoxmlValue() + "\"";
		write(paperSize);	
		
		if(firstPageNumber!= null && firstPageNumber > 0)
		{
			write(" firstPageNumber=\"" + firstPageNumber + "\"");
			write(" useFirstPageNumber=\"1\"/>\n");
		}
		else
		{
			write("/>\n");	
		}
		
		if (hasHeaderOrFooter(printSettings)) 
		{
			write("<headerFooter>");
			if (hasHeader(printSettings))
			{
				write("<oddHeader>");
				if (printSettings.getHeaderLeft() != null && !printSettings.getHeaderLeft().trim().isEmpty())
				{
					write("&amp;L");
					write(JRStringUtil.xmlEncode(printSettings.getHeaderLeft()));
				}
				if (printSettings.getHeaderCenter() != null && !printSettings.getHeaderCenter().trim().isEmpty())
				{
					write("&amp;C");
					write(JRStringUtil.xmlEncode(printSettings.getHeaderCenter()));
				}
				if (printSettings.getHeaderRight() != null && !printSettings.getHeaderRight().trim().isEmpty())
				{
					write("&amp;R");
					write(JRStringUtil.xmlEncode(printSettings.getHeaderRight()));
				}
				write("</oddHeader>");
			}
			if (hasFooter(printSettings))
			{
				write("<oddFooter>");
				if (printSettings.getFooterLeft() != null && !printSettings.getFooterLeft().trim().isEmpty())
				{
					write("&amp;L");
					write(JRStringUtil.xmlEncode(printSettings.getFooterLeft()));
				}
				if (printSettings.getFooterCenter() != null && !printSettings.getFooterCenter().trim().isEmpty())
				{
					write("&amp;C");
					write(JRStringUtil.xmlEncode(printSettings.getFooterCenter()));
				}
				if (printSettings.getFooterRight() != null && !printSettings.getFooterRight().trim().isEmpty())
				{
					write("&amp;R");
					write(JRStringUtil.xmlEncode(printSettings.getFooterRight()));
				}
				write("</oddFooter>");
			}
			write("</headerFooter>\n");
		}
		else if(!firstPageNotSet)
		{
			write("<headerFooter><oddFooter>&amp;CPage &amp;P</oddFooter></headerFooter>\n");
		}
		
		if (rowBreaks.size() > 0)
		{
			write("<rowBreaks count=\"" + rowBreaks.size() + "\" manualBreakCount=\"" + rowBreaks.size() + "\">");
			for (Integer rowBreakIndex : rowBreaks)
			{
				write("<brk id=\"" + (rowBreakIndex + 1) + "\" man=\"1\"/>");
			}
			write("</rowBreaks>\n");
		}


		write("<drawing r:id=\"rIdDr" + index + "\"/>");
		write("</worksheet>");		
	}
	
	private boolean hasHeaderOrFooter(JRXlsAbstractExporter.SheetInfo.SheetPrintSettings printSettings)
	{
		return hasHeader(printSettings) || hasFooter(printSettings);
	}

	private boolean hasHeader(JRXlsAbstractExporter.SheetInfo.SheetPrintSettings printSettings)
	{
		return (printSettings.getHeaderLeft() != null && !printSettings.getHeaderLeft().trim().isEmpty())
				|| (printSettings.getHeaderCenter() != null && !printSettings.getHeaderCenter().trim().isEmpty()) 
				|| (printSettings.getHeaderRight() != null && !printSettings.getHeaderRight().trim().isEmpty()); 
	}

	private boolean hasFooter(JRXlsAbstractExporter.SheetInfo.SheetPrintSettings printSettings)
	{
		return (printSettings.getFooterLeft() != null && !printSettings.getFooterLeft().trim().isEmpty()) 
				|| (printSettings.getFooterCenter() != null && !printSettings.getFooterCenter().trim().isEmpty()) 
				|| (printSettings.getFooterRight() != null && !printSettings.getFooterRight().trim().isEmpty()); 
	}

	/**
	 *
	 */
	public void exportColumn(int colIndex, int colWidth, boolean autoFit) 
	{
		try
		{
			colsWriter.write("<col min=\"" + (colIndex + 1) + "\" max=\"" + (colIndex + 1) + "\" customWidth=\"1\" width=\"" + (3f * colWidth / 18f) + "\"/>\n");
		}
		catch (IOException e)
		{
			throw new JRRuntimeException(e);
		}
	}
	
	/**
	 *
	 */
	public void exportRow(int rowHeight, Cut yCut, XlsRowLevelInfo levelInfo) 
	{
		boolean isAutoFit = yCut.hasProperty(ExcelAbstractExporter.PROPERTY_AUTO_FIT_ROW) 
				? (Boolean)yCut.getProperty(ExcelAbstractExporter.PROPERTY_AUTO_FIT_ROW)
				: defaultAutoFitRow;
		exportRow(rowHeight, isAutoFit, levelInfo);
	}
	
	
	/**
	 *
	 */
	public void exportRow(int rowHeight, boolean isAutoFit, XlsRowLevelInfo levelInfo) 
	{
		if (rowIndex > 0)
		{
			write("</row>\n");
		}
		else
		{
			if (!colsWriter.isEmpty())
			{
				write("<cols>\n");
				colsWriter.writeData(writer);
				write("</cols>\n");
			}
			write("<sheetData>\n");
		}
		rowIndex++;
		write("<row r=\"" + rowIndex + "\""  + (isAutoFit ? " customHeight=\"0\" bestFit=\"1\"" : " customHeight=\"1\"") + " ht=\"" + (isAutoFit ? 0 : rowHeight) + "\"");
		if (levelInfo != null && levelInfo.getLevelMap().size() > 0)
		{
			write(" outlineLevel=\"" + levelInfo.getLevelMap().size() + "\"");
		}
		write(">\n");
	}
			
	/**
	 *
	 */
	public void exportMergedCells(int row, int col, int maxColumnIndex, int rowSpan, int colSpan) 
	{
		rowSpan = collapseRowSpan ? 1 : rowSpan;
		
		if (rowSpan > 1	|| colSpan > 1)
		{
			String ref = 
				JRXlsAbstractExporter.getColumIndexName(col, maxColumnIndex) + (row + 1)
				+ ":" + JRXlsAbstractExporter.getColumIndexName(col + colSpan - 1, maxColumnIndex) + (row + rowSpan); //FIXMEXLSX reuse this utility method
			
			try
			{
				mergedCellsWriter.write("<mergeCell ref=\"" + ref + "\"/>\n");
			}
			catch (IOException e)
			{
				throw new JRRuntimeException(e);
			}
		}
	}
	
	/**
	 *
	 */
	public void exportHyperlink(int row, int col, int maxColumnIndex, String href, boolean isLocal) 
	{
		String ref = 
				JRXlsAbstractExporter.getColumIndexName(col, maxColumnIndex) + (row + 1);
		
		try
		{
			if(isLocal){
				hyperlinksWriter.write("<hyperlink ref=\"" + ref + "\" location=\"" + getDefinedName(href) + "\"/>\n");
			} else {
				hyperlinksWriter.write("<hyperlink ref=\"" + ref + "\" r:id=\"rIdLnk" + sheetRelsHelper.getHyperlink(href) + "\"/>\n");
			}
		}
		catch (IOException e)
		{
			throw new JRRuntimeException(e);
		}
	}
	
	public void addRowBreak(int rowIndex)
	{
		rowBreaks.add(rowIndex);
	}
	
	public String getDefinedName(String name)
	{
		if (name != null)
		{
			String definedName = name.replaceAll("\\W", "");
			if (!definedName.isEmpty() && Character.isDigit(definedName.charAt(0)))
			{
				definedName = "_" + definedName;
			}
			return definedName;
		}
		return null;
	}
}

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

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.export.JRExporterGridCell;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporter;
import net.sf.jasperreports.engine.type.LineDirectionEnum;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.util.FileBufferedWriter;
import net.sf.jasperreports.export.XlsReportConfiguration;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class XlsxStyleHelper extends BaseHelper
{
	/**
	 * 
	 */
	private FileBufferedWriter formatsWriter = new FileBufferedWriter();
	private FileBufferedWriter fontsWriter = new FileBufferedWriter();
	private FileBufferedWriter fillsWriter = new FileBufferedWriter();
	private FileBufferedWriter bordersWriter = new FileBufferedWriter();
	private FileBufferedWriter cellXfsWriter = new FileBufferedWriter();
	
	private Map<XlsxStyleInfo, Integer> styleCache = new HashMap<>();//FIXMEXLSX use soft cache? check other exporter caches as well
	
	private XlsxFormatHelper formatHelper;
	private XlsxFontHelper fontHelper;
	private XlsxBorderHelper borderHelper;
	
	/**
	 * 
	 */
	public XlsxStyleHelper(
		JasperReportsContext jasperReportsContext,
		Writer writer, 
		String exporterKey
		)
	{
		super(jasperReportsContext, writer);
		
		formatHelper = new XlsxFormatHelper(jasperReportsContext, formatsWriter);
		fontHelper = new XlsxFontHelper(jasperReportsContext, fontsWriter, exporterKey);
		borderHelper = new XlsxBorderHelper(jasperReportsContext ,bordersWriter);
	}
	
	
	/**
	 * 
	 */
	public void setConfiguration(XlsReportConfiguration configuration)
	{
		fontHelper.setConfiguration(configuration);
	}
	

	/**
	 * 
	 */
	public int getCellStyle(
		JRExporterGridCell gridCell,
		JRPrintElement element,
		String pattern, 
		Locale locale,
		boolean isWrapText, 
		boolean isHidden, 
		boolean isLocked,
		boolean  isShrinkToFit,
		boolean isIgnoreTextFormatting,
		RotationEnum rotation,
		JRXlsAbstractExporter.SheetInfo sheetInfo,
		LineDirectionEnum direction
		)
	{
		XlsxStyleInfo styleInfo = 
			new XlsxStyleInfo(
				formatHelper.getFormat(pattern) + 1,
				fontHelper.getFont(element, locale) + 1,
				borderHelper.getBorder(gridCell, sheetInfo, direction) + 1,
				gridCell,
				element,
				isWrapText,
				isHidden,
				isLocked,
				isShrinkToFit,
				isIgnoreTextFormatting, 
				getRotation(rotation),
				sheetInfo,
				direction
				);
		Integer styleIndex = styleCache.get(styleInfo);
		if (styleIndex == null)
		{
			styleIndex = styleCache.size() + 1;
			exportCellStyle(styleInfo, styleIndex, sheetInfo);
			styleCache.put(styleInfo, styleIndex);
		}
		return styleIndex;
	}

	public int getCellStyle(
			JRPrintElement element, 
			String pattern, 
			Locale locale,
			boolean isWrapText, 
			boolean isHidden, 
			boolean isLocked,
			boolean  isShrinkToFit,
			boolean isIgnoreTextFormatting,
			RotationEnum rotation,
			JRXlsAbstractExporter.SheetInfo sheetInfo,
			LineDirectionEnum direction,
			JRStyle parentStyle
			)
	{
		XlsxStyleInfo styleInfo = 
				new XlsxStyleInfo(
						formatHelper.getFormat(pattern) + 1,
						fontHelper.getFont(element, locale) + 1,
						borderHelper.getBorder(element, sheetInfo, direction, parentStyle) + 1,
						element,
						isWrapText,
						isHidden,
						isLocked,
						isShrinkToFit,
						isIgnoreTextFormatting, 
						getRotation(rotation),
						sheetInfo,
						direction,
						parentStyle
						);
		Integer styleIndex = styleCache.get(styleInfo);
		if (styleIndex == null)
		{
			styleIndex = styleCache.size() + 1;
			exportCellStyle(styleInfo, styleIndex, sheetInfo);
			styleCache.put(styleInfo, styleIndex);
		}
		return styleIndex;
	}
	
	public int getCellStyle(
			JRLineBox box, 
			String pattern, 
			Locale locale,
			boolean isWrapText, 
			boolean isHidden, 
			boolean isLocked,
			boolean  isShrinkToFit,
			boolean isIgnoreTextFormatting,
			RotationEnum rotation,
			JRXlsAbstractExporter.SheetInfo sheetInfo,
			LineDirectionEnum direction,
			JRStyle parentStyle
			)
	{
		XlsxStyleInfo styleInfo = 
				new XlsxStyleInfo(
						formatHelper.getFormat(pattern) + 1,
						fontHelper.getFont(box, locale) + 1,
						borderHelper.getBorder(box, sheetInfo, direction, parentStyle) + 1,
						isWrapText,
						isHidden,
						isLocked,
						isShrinkToFit,
						isIgnoreTextFormatting, 
						getRotation(rotation),
						sheetInfo,
						direction
						);
		Integer styleIndex = styleCache.get(styleInfo);
		if (styleIndex == null)
		{
			styleIndex = styleCache.size() + 1;
			exportCellStyle(styleInfo, styleIndex, sheetInfo);
			styleCache.put(styleInfo, styleIndex);
		}
		return styleIndex;
	}
	
	/**
	 * 
	 */
	private void exportCellStyle(
			XlsxStyleInfo styleInfo, 
			Integer styleIndex, 
			JRXlsAbstractExporter.SheetInfo sheetInfo)
	{
		try
		{
			if (sheetInfo != null && (Boolean.TRUE.equals(sheetInfo.ignoreCellBackground) || styleInfo.backcolor == null))
			{
				if (Boolean.TRUE.equals(sheetInfo.whitePageBackground))
				{
					fillsWriter.write("<fill><patternFill patternType=\"solid\"><fgColor rgb=\"FFFFFF\"/></patternFill></fill>\n");
				}
				else
				{
					fillsWriter.write("<fill><patternFill patternType=\"none\"/></fill>\n");
				}
			}
			else
			{
				fillsWriter.write("<fill><patternFill patternType=\"solid\"><fgColor rgb=\"" + styleInfo.backcolor + "\"/></patternFill></fill>\n");
			}
			
			cellXfsWriter.write(
				"<xf numFmtId=\"" + styleInfo.formatIndex
				+ "\" fontId=\"" + styleInfo.fontIndex
				+ "\" fillId=\"" + (styleIndex + 1)
				+ "\" borderId=\"" + styleInfo.borderIndex
				+ "\" xfId=\"0\""
				+ " applyAlignment=\"1\" applyProtection=\"1\" applyNumberFormat=\"1\" applyFont=\"1\" applyFill=\"1\" applyBorder=\"1\">"
				+ "<alignment wrapText=\"" + (styleInfo.isWrapText && !styleInfo.isShrinkToFit) + "\""
				+ (styleInfo.horizontalAlign == null ? "" : " horizontal=\"" + styleInfo.horizontalAlign + "\"")
				+ (styleInfo.verticalAlign == null ? "" : " vertical=\"" + styleInfo.verticalAlign + "\"")
				+ (styleInfo.isShrinkToFit ? " shrinkToFit=\"" + styleInfo.isShrinkToFit + "\"" : "")
				+ (styleInfo.rotation != 0 ? " textRotation=\"" + styleInfo.rotation + "\"" : "")
				+ "/>"
				);
			cellXfsWriter.write("<protection hidden=\"" + styleInfo.isHidden + "\" locked=\"" + styleInfo.isLocked + "\"/>");
			cellXfsWriter.write("</xf>\n");
		}
		catch (IOException e)
		{
			throw new JRRuntimeException(e);
		}
	}

	/**
	 * 
	 */
	public void export()
	{
		write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		write("<styleSheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">\n");
		
		write("<numFmts>\n");// count=\"1\">\n");
		write("<numFmt numFmtId=\"0\" formatCode=\"General\"/>\n");
		formatsWriter.writeData(writer);
		write("</numFmts>\n");

		write("<fonts>\n");// count=\"1\">\n");
		write("<font><sz val=\"11\"/><color theme=\"1\"/><name val=\"Calibri\"/><family val=\"2\"/></font>\n");
		fontsWriter.writeData(writer);
		write("</fonts>\n");

		write("<fills>\n");// count=\"2\">\n");
		write("<fill><patternFill patternType=\"none\"/></fill>\n");
		write("<fill><patternFill patternType=\"solid\"><fgColor rgb=\"FFFFFF\"/></patternFill></fill>\n");
		fillsWriter.writeData(writer);
		write("</fills>\n");
		write("<borders>\n");// count=\"1\">\n");
		write("<border><left/><right/><top/><bottom/><diagonal/></border>\n");
		bordersWriter.writeData(writer);
		write("</borders>\n");
		
		write("<cellStyleXfs count=\"1\"><xf fontId=\"0\" applyFont=\"1\"/></cellStyleXfs>\n");

		write("<cellXfs>\n");// count=\"1\">\n");
		write("<xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\" xfId=\"0\" applyAlignment=\"1\" applyProtection=\"1\" applyNumberFormat=\"1\" applyFont=\"1\" applyFill=\"1\" applyBorder=\"1\"/>\n");
		cellXfsWriter.writeData(writer);
		write("</cellXfs>\n");
		
		write("<cellStyles count=\"1\"><cellStyle name=\"Normal\" xfId=\"0\" builtinId=\"0\" customBuiltin=\"1\"/></cellStyles>\n");
		write("<dxfs count=\"0\"/><tableStyles count=\"0\" defaultTableStyle=\"TableStyleMedium9\" defaultPivotStyle=\"PivotStyleLight16\"/>\n");

		write("</styleSheet>\n");
	}
	
	/**
	 *
	 */
	protected int getRotation(RotationEnum rotation)
	{
		int result = 0;
		
		if (rotation != null)
		{
			switch(rotation)
			{
				case LEFT:
				{
					result = 90;
					break;
				}
				case RIGHT:
				{
					result = 180;
					break;
				}
				case UPSIDE_DOWN:
				case NONE:
				default:
				{
				}
			}
		}

		return result;
	}
	
}

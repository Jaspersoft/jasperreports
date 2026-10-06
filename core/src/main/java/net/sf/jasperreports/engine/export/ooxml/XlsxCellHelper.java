/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2026 Actian Corporation, a division of HCL Software. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Actian,
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
import java.util.Locale;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.export.ElementGridCell;
import net.sf.jasperreports.engine.export.JRExporterGridCell;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporter;
import net.sf.jasperreports.engine.export.OccupiedGridCell;
import net.sf.jasperreports.engine.export.data.BooleanTextValue;
import net.sf.jasperreports.engine.export.data.DateTextValue;
import net.sf.jasperreports.engine.export.data.NumberTextValue;
import net.sf.jasperreports.engine.export.data.StringTextValue;
import net.sf.jasperreports.engine.export.data.TextValue;
import net.sf.jasperreports.engine.export.data.TextValueHandler;
import net.sf.jasperreports.engine.type.LineDirectionEnum;
import net.sf.jasperreports.engine.type.RotationEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class XlsxCellHelper extends BaseHelper
{

	/**
	 *
	 */
	private XlsxStyleHelper styleHelper;
//	private XlsxBorderHelper borderHelper;
	
	private final TypeTextValueHandler textValueHandler = new TypeTextValueHandler();
	
	/**
	 *
	 */
	public XlsxCellHelper(
		JasperReportsContext jasperReportsContext,
		Writer writer,
		XlsxStyleHelper styleHelper
		)
	{
		super(jasperReportsContext, writer);
		
		this.styleHelper = styleHelper;
//		borderHelper = new XlsxBorderHelper(writer);
	}

	/**
	 * 
	 *
	public XlsxBorderHelper getBorderHelper() 
	{
		return borderHelper;
	}

	/**
	 *
	 */
	public void exportHeader(
			JRExporterGridCell gridCell,
			JRPrintElement element,
			int rowIndex,
			int colIndex, 
			int maxColIndex,
			JRXlsAbstractExporter.SheetInfo sheetInfo
			) 
	{
		exportHeader(
				gridCell,
				element,
				rowIndex, 
				colIndex, 
				maxColIndex, 
				null, 
				null, 
				null, 
				true, 
				false, 
				false, 
				false, 
				false, 
				RotationEnum.NONE, 
				sheetInfo);
	}
	
	/**
	 *
	 */
	public void exportHeader(
			JRExporterGridCell gridCell,
			JRPrintElement element,
			int rowIndex,
			int colIndex, 
			int maxColIndex, 
			TextValue textValue,
			String pattern,
			Locale locale,
			boolean isWrapText,
			boolean isHidden,
			boolean isLocked,
			boolean isShrinkToFit,
			boolean isIgnoreTextFormatting, 
			RotationEnum rotation,
			JRXlsAbstractExporter.SheetInfo sheetInfo
			) 
	{
		exportHeader(
				gridCell,
				element,
				rowIndex,
				colIndex, 
				maxColIndex, 
				textValue,
				pattern,
				locale,
				isWrapText,
				isHidden,
				isLocked,
				isShrinkToFit,
				isIgnoreTextFormatting, 
				rotation,
				sheetInfo,
				null
				);
	}
	
	/**
	 *
	 */
	public void exportHeader(
		JRExporterGridCell gridCell,
		JRPrintElement element,
		int rowIndex,
		int colIndex, 
		int maxColIndex,
		JRXlsAbstractExporter.SheetInfo sheetInfo,
		LineDirectionEnum direction
		) 
	{
		exportHeader(
				gridCell,
				element,
				rowIndex, 
				colIndex, 
				maxColIndex, 
				null, 
				null, 
				null, 
				true, 
				false, 
				false, 
				false, 
				false, 
				RotationEnum.NONE, 
				sheetInfo,
				direction);
	}

	/**
	 *
	 */
	public void exportHeader(
		JRExporterGridCell gridCell,
		JRPrintElement element,
		int rowIndex,
		int colIndex, 
		int maxColIndex, 
		TextValue textValue,
		String pattern,
		Locale locale,
		boolean isWrapText,
		boolean isHidden,
		boolean isLocked,
		boolean isShrinkToFit,
		boolean isIgnoreTextFormatting, 
		RotationEnum rotation,
		JRXlsAbstractExporter.SheetInfo sheetInfo,
		LineDirectionEnum direction
		) 
	{
		try
		{
			if (textValue != null)
			{
				textValue.handle(textValueHandler);
			}
			else
			{
				textValueHandler.handle((StringTextValue)null);
			}
		}
		catch (JRException e)
		{
			throw new JRRuntimeException(e);
		}

		Integer styleIndex = null;
		
		if (gridCell != null && gridCell.getType() == JRExporterGridCell.TYPE_OCCUPIED_CELL)
		{
			styleIndex = ((ElementGridCell)((OccupiedGridCell)gridCell).getOccupier()).getStyleIndex();
		}
		
		if (styleIndex == null)
		{
			styleIndex = 
				styleHelper.getCellStyle(
					gridCell, 
					element,
					pattern, 
					locale, 
					isWrapText, 
					isHidden, 
					isLocked, 
					isShrinkToFit, 
					isIgnoreTextFormatting,
					rotation,
					sheetInfo,
					direction
					);
			if (
				gridCell != null && gridCell.getType() == JRExporterGridCell.TYPE_ELEMENT_CELL
				&& gridCell instanceof ElementGridCell
				)
			{
				((ElementGridCell)gridCell).setStyleIndex(styleIndex);
			}
		}
		
		write("  <c r=\"" 
			+ JRXlsAbstractExporter.getColumIndexName(colIndex, maxColIndex) 
			+ (rowIndex + 1) 
			+ "\" s=\"" + styleIndex + "\""
			);
		String type = textValueHandler.getType();
		if (type != null)
		{
			write(" t=\"" + type + "\"");
		}
		write(">");
	}

	public void exportHeader(
			JRPrintElement element,
			int rowIndex,
			int colIndex, 
			int maxColIndex, 
			TextValue textValue,
			String pattern,
			Locale locale,
			boolean isWrapText,
			boolean isHidden,
			boolean isLocked,
			boolean isShrinkToFit,
			boolean isIgnoreTextFormatting, 
			RotationEnum rotation,
			JRXlsAbstractExporter.SheetInfo sheetInfo,
			LineDirectionEnum direction,
			JRStyle parentStyle
			) 
	{
		try
		{
			if (textValue != null)
			{
				textValue.handle(textValueHandler);
			}
			else
			{
				textValueHandler.handle((StringTextValue)null);
			}
		}
		catch (JRException e)
		{
			throw new JRRuntimeException(e);
		}
		
		Integer styleIndex = styleHelper.getCellStyle(
							element, 
							pattern, 
							locale, 
							isWrapText, 
							isHidden, 
							isLocked, 
							isShrinkToFit, 
							isIgnoreTextFormatting,
							rotation,
							sheetInfo,
							direction,
							parentStyle
							);
		
		write("  <c r=\"" 
				+ JRXlsAbstractExporter.getColumIndexName(colIndex, maxColIndex) 
				+ (rowIndex + 1) 
				+ "\" s=\"" + styleIndex + "\""
				);
		String type = textValueHandler.getType();
		if (type != null)
		{
			write(" t=\"" + type + "\"");
		}
		write(">");
	}
	
	public void exportHeader(
			JRLineBox box,
			int rowIndex,
			int colIndex, 
			int maxColIndex, 
			TextValue textValue,
			String pattern,
			Locale locale,
			boolean isWrapText,
			boolean isHidden,
			boolean isLocked,
			boolean isShrinkToFit,
			boolean isIgnoreTextFormatting, 
			RotationEnum rotation,
			JRXlsAbstractExporter.SheetInfo sheetInfo,
			LineDirectionEnum direction,
			JRStyle parentStyle
			) 
	{
		try
		{
			if (textValue != null)
			{
				textValue.handle(textValueHandler);
			}
			else
			{
				textValueHandler.handle((StringTextValue)null);
			}
		}
		catch (JRException e)
		{
			throw new JRRuntimeException(e);
		}
		
		Integer styleIndex = styleHelper.getCellStyle(
				box, 
				pattern, 
				locale, 
				isWrapText, 
				isHidden, 
				isLocked, 
				isShrinkToFit, 
				isIgnoreTextFormatting,
				rotation,
				sheetInfo,
				direction,
				parentStyle
				);
		
		write("  <c r=\"" 
				+ JRXlsAbstractExporter.getColumIndexName(colIndex, maxColIndex) 
				+ (rowIndex + 1) 
				+ "\" s=\"" + styleIndex + "\""
				);
		String type = textValueHandler.getType();
		if (type != null)
		{
			write(" t=\"" + type + "\"");
		}
		write(">");
	}
	
	/**
	 *
	 */
	public void exportFooter() 
	{
		write("</c>");
	}

}

class TypeTextValueHandler implements TextValueHandler 
{
	private String type;
	
	TypeTextValueHandler(){
	}
	
	@Override
	public void handle(BooleanTextValue textValue) throws JRException {
		type = "b";
	}
	
	@Override
	public void handle(DateTextValue textValue) throws JRException {
		type = null;//"d"; //mantis #5192 : invalid file in ms office 2010
	}
	
	@Override
	public void handle(NumberTextValue textValue) throws JRException {
		type = "n";
	}
	
	@Override
	public void handle(StringTextValue textValue) throws JRException {
		type = "s";
	}
	
	public String getType()
	{
		return type;
	}
}

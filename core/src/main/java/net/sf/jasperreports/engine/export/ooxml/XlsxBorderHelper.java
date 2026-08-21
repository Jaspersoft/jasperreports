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
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRBoxContainer;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.base.JRBaseLineBox;
import net.sf.jasperreports.engine.export.JRExporterGridCell;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporter;
import net.sf.jasperreports.engine.type.LineDirectionEnum;
import net.sf.jasperreports.engine.util.JRColorUtil;
import net.sf.jasperreports.engine.util.StyleUtil;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class XlsxBorderHelper extends BaseHelper
{
	private Map<XlsxBorderInfo, Integer> borderCache = new HashMap<>();//FIXMEXLSX use soft cache? check other exporter caches as well
	
	/**
	 *
	 */
	public XlsxBorderHelper(JasperReportsContext jasperReportsContext, Writer writer)
	{
		super(jasperReportsContext, writer);
	}
	
	/**
	 *
	 */
	public int getBorder(JRExporterGridCell gridCell, JRXlsAbstractExporter.SheetInfo sheetInfo, LineDirectionEnum direction)
	{
		if (gridCell == null)
		{
			return -1;			
		}
		return getBorder(gridCell.getBox(), sheetInfo, direction);
	}

	public int getBorder(JRPrintElement element, JRXlsAbstractExporter.SheetInfo sheetInfo, LineDirectionEnum direction, JRStyle parentStyle)
	{	
		if (Boolean.TRUE.equals(sheetInfo.ignoreCellBorder))
		{
			return -1;			
		}
		JRLineBox box = null;
		if (element instanceof JRBoxContainer && ((JRBoxContainer)element).getLineBox() != null)
		{
			box = new JRBaseLineBox(null);
			if (parentStyle != null && parentStyle.getLineBox() != null)
			{
				StyleUtil.appendBox(box, parentStyle.getLineBox());
			}
			if (element.getStyle() != null && element.getStyle().getLineBox() != null)
			{
				StyleUtil.appendBox(box, element.getStyle().getLineBox());
			}
			StyleUtil.appendBox(box, ((JRBoxContainer)element).getLineBox());
		}
		else
		{
			box = element == null || element.getStyle() == null 
				? (parentStyle == null ? null : parentStyle.getLineBox()) 
				: element.getStyle().getLineBox();
		}
		
		return getBorder(box, sheetInfo, direction);
	}
	
	public int getBorder(JRLineBox box, JRXlsAbstractExporter.SheetInfo sheetInfo, LineDirectionEnum direction, JRStyle parentStyle)
	{		
		if (Boolean.TRUE.equals(sheetInfo.ignoreCellBorder))
		{
			return -1;			
		}
		if (box == null && parentStyle != null)
		{
			box = parentStyle.getLineBox();
		}
		return getBorder(box, sheetInfo, direction);
	}
	
	private int getBorder(JRLineBox box, JRXlsAbstractExporter.SheetInfo sheetInfo, LineDirectionEnum direction)
	{		
		if (box == null)
		{
			return -1;			
		}
		
		XlsxBorderInfo borderInfo = new XlsxBorderInfo(box, direction);
		Integer borderIndex = borderCache.get(borderInfo);
		if (borderIndex == null)
		{
			borderIndex = borderCache.size();
			export(borderInfo);
			borderCache.put(borderInfo, borderIndex);
		}
		return borderIndex;
	}

	/**
	 *
	 */
	public void export(JRLineBox box)
	{
		if (box != null)
		{
			export(new XlsxBorderInfo(box));
		}
	}

	/**
	 *
	 */
	public void export(JRPen pen)
	{
		if (pen != null)
		{
			export(new XlsxBorderInfo(pen));
		}
	}

	/**
	 *
	 */
	private void export(XlsxBorderInfo info)
	{
//		if (info.hasBorder())
		{
			write("<border");
			if (info.getDirection() != null)
			{
				write(info.getDirection().equals(LineDirectionEnum.TOP_DOWN) ? " diagonalDown=\"1\"" : " diagonalUp=\"1\"");
			}
			write(">");
			exportBorder(XlsxBorderInfo.LEFT_BORDER, info.leftBorderStyle, info.leftBorderColor);
			exportBorder(XlsxBorderInfo.RIGHT_BORDER, info.rightBorderStyle, info.rightBorderColor);
			exportBorder(XlsxBorderInfo.TOP_BORDER, info.topBorderStyle, info.topBorderColor);
			exportBorder(XlsxBorderInfo.BOTTOM_BORDER, info.bottomBorderStyle, info.bottomBorderColor);
			exportBorder(XlsxBorderInfo.DIAGONAL_BORDER, info.diagonalBorderStyle, info.diagonalBorderColor);
			write("</border>\n");
		}
//		
//		write("      <w:tcMar>\n");
//		exportPadding(info, BorderInfo.TOP_BORDER);
//		exportPadding(info, BorderInfo.LEFT_BORDER);
//		exportPadding(info, BorderInfo.BOTTOM_BORDER);
//		exportPadding(info, BorderInfo.RIGHT_BORDER);
//		write("      </w:tcMar>\n");
	}

	/**
	 *
	 */
	private void exportBorder(String borderName, XlsxBorderStyle style, Color color)
	{
		write("<" + borderName);
  		if (style != null)
		{
  			write(" style=\"" + style.value() + "\"");
		}
		write(">");
		if (color != null)	//FIXMEDOCX check this; use default color?
		{
			write("<color rgb=\"" + JRColorUtil.getColorHexa(color) + "\"/>");
		}
		write("</" + borderName + ">");
	}
	
//	/**
//	 *
//	 */
//	private void exportPadding(XlsxBorderInfo info, int side)//FIXMEXLSX padding
//	{
//		if (info.borderPadding[side] != null)
//		{
//			write("       <w:" + XlsxBorderInfo.BORDER[side] +" w:w=\"" + info.borderPadding[side] + "\" w:type=\"dxa\" />\n");
//		}
//	}

}

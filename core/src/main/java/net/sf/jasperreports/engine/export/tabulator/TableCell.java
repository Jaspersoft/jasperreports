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
package net.sf.jasperreports.engine.export.tabulator;

import java.awt.Color;

import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.export.PrintElementIndex;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class TableCell
{
	public static enum CellType
	{
		COLUMN_HEADER,
		ROW_HEADER
	}
	
	private final Tabulator tabulator;
	private final TablePosition position;
	private final Cell cell;
	private final JRPrintElement element;
	
	private int colSpan;
	private int rowSpan;
	private CellType cellType;
	private Color backcolor;
	private JRLineBox box;
	
	public TableCell(Tabulator tabulator, 
			TablePosition position, Cell cell, JRPrintElement element,
			CellType cellType, int colSpan, int rowSpan, Color backcolor, JRLineBox box)
	{
		this.tabulator = tabulator;
		this.position = position;
		this.cell = cell;
		this.element = element;
		
		this.cellType = cellType;
		this.colSpan = colSpan;
		this.rowSpan = rowSpan;
		this.backcolor = backcolor;
		this.box = box;
	}

	public Tabulator getTabulator()
	{
		return tabulator;
	}

	public Cell getCell()
	{
		return cell;
	}

	public TablePosition getPosition()
	{
		return position;
	}

	public JRPrintElement getElement()
	{
		return element;
	}

	public CellType getCellType()
	{
		return cellType;
	}

	public void setCellType(CellType cellType)
	{
		this.cellType = cellType;
	}

	public int getColumnSpan()
	{
		return colSpan;
	}

	public void setColumnSpan(int colSpan)
	{
		this.colSpan = colSpan;
	}

	public int getRowSpan()
	{
		return rowSpan;
	}

	public void setRowSpan(int rowSpan)
	{
		this.rowSpan = rowSpan;
	}

	public JRLineBox getBox()
	{
		return box;
	}

	public void setBox(JRLineBox box)
	{
		this.box = box;
	}

	public Color getBackcolor()
	{
		return backcolor;
	}

	public void setBackcolor(Color backcolor)
	{
		this.backcolor = backcolor;
	}

	public String getElementAddress()
	{
		BaseElementCell elementCell = (BaseElementCell) cell;
		return PrintElementIndex.asAddress(elementCell.getParentIndex(), elementCell.getElementIndex());
	}

}

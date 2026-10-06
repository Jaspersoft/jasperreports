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
package net.sf.jasperreports.engine.export;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class Grid
{

	private final int rowCount;
	private final int columnCount;
	private JRExporterGridCell[] cells;
	
	public Grid(int rowCount, int columnCount)
	{
		this.rowCount = rowCount;
		this.columnCount = columnCount;
		this.cells = new JRExporterGridCell[rowCount * columnCount];
	}

	public int getRowCount()
	{
		return rowCount;
	}

	public int getColumnCount()
	{
		return columnCount;
	}
	
	public GridRow getRow(int row)
	{
		rowBoundsCheck(row);
		return new GridRow(cells, row * columnCount, columnCount);
	}

	private void rowBoundsCheck(int row)
	{
		if (row < 0 || row >= rowCount)
		{
			throw new IndexOutOfBoundsException("row index " + row + " out of bounds, size " + rowCount);
		}
	}

	private void columnBoundsCheck(int column)
	{
		if (column < 0 || column >= columnCount)
		{
			throw new IndexOutOfBoundsException("column index " + column + " out of bounds, size " + columnCount);
		}
	}
	
	public void set(int row, int column, JRExporterGridCell cell)
	{
		rowBoundsCheck(row);
		columnBoundsCheck(column);
		cells[row * columnCount + column] = cell;
	}
	
	public JRExporterGridCell get(int row, int column)
	{
		rowBoundsCheck(row);
		columnBoundsCheck(column);
		return cells[row * columnCount + column];
	}
}

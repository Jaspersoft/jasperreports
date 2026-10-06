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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class Table
{
	private static final Log log = LogFactory.getLog(Tabulator.class);
	
	protected final Tabulator tabulator;
	protected DimensionEntries<Column> columns;
	protected DimensionEntries<Row> rows;
	
	private String role;
	
	public Table(Tabulator tabulator)
	{
		this.tabulator = tabulator;
		
		DimensionControl<Column> columnsControl = new ColumnsControl();
		columns = new DimensionEntries<>(columnsControl);
		
		DimensionControl<Row> rowsControl = new RowsControl();
		rows = new DimensionEntries<>(rowsControl);
		
		if (log.isTraceEnabled())
		{
			log.trace("created columns " + columns + " and rows " + rows);
		}
	}

	public void removeColumn(Column column, Column prevColumn)
	{
		columns.removeEntry(column, prevColumn);
		
		for (Row row : rows.getEntries())
		{
			row.setCell(column, null);
		}
		
		// recycle column index
		((ColumnsControl) columns.getControl()).indexes.recycle(column.index);
	}

	public void removeRow(Row row, Row prevRow)
	{
		rows.removeEntry(row, prevRow);
	}
	
	public DimensionEntries<Column> getColumns()
	{
		return columns;
	}

	public DimensionEntries<Row> getRows()
	{
		return rows;
	}

	public String getRole()
	{
		return role;
	}

	public void setRole(String role)
	{
		this.role = role;
	}

	protected class ColumnsControl implements DimensionControl<Column>
	{
		protected EntryIndexes indexes;
		
		public ColumnsControl()
		{
			this.indexes = new EntryIndexes();
		}
		
		@Override
		public Column entryKey(int coord)
		{
			return tabulator.columnKey(coord);
		}

		@Override
		public Column createEntry(int startCoord, int endCoord)
		{
			Column column = new Column(startCoord);
			column.endCoord = endCoord;
			column.index = indexes.next();
			return column;
		}
		
		@Override
		public void entrySplit(Column splitEntry, Column newEntry)
		{
			tabulator.columnSplit(Table.this, splitEntry, newEntry);
		}
	}

	protected class RowsControl implements DimensionControl<Row>
	{
		@Override
		public Row entryKey(int coord)
		{
			return tabulator.rowKey(coord);
		}

		@Override
		public Row createEntry(int startCoord, int endCoord)
		{
			Row row = new Row (startCoord);
			row.endCoord = endCoord;
			return row;
		}
		
		@Override
		public void entrySplit(Row splitEntry, Row newEntry)
		{
			tabulator.rowSplit(Table.this, splitEntry, newEntry); 
		}
	}
}
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
package net.sf.jasperreports.components.table.util;

import java.util.List;

import net.sf.jasperreports.components.table.BaseColumn;
import net.sf.jasperreports.components.table.Cell;
import net.sf.jasperreports.components.table.Column;
import net.sf.jasperreports.components.table.ColumnGroup;
import net.sf.jasperreports.components.table.ColumnVisitor;
import net.sf.jasperreports.components.table.GroupCell;
import net.sf.jasperreports.engine.JRVisitor;
import net.sf.jasperreports.engine.util.ElementsVisitorUtils;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ColumnElementsVisitor implements ColumnVisitor<Void>
{

	private final JRVisitor visitor;
	
	public ColumnElementsVisitor(JRVisitor visitor)
	{
		this.visitor = visitor;
	}

	@Override
	public Void visitColumn(Column column)
	{
		visitBaseColumn(column);
		visitCell(column.getDetailCell());
		
		return null;
	}

	@Override
	public Void visitColumnGroup(ColumnGroup columnGroup)
	{
		visitBaseColumn(columnGroup);
		
		List<BaseColumn> subColumns = columnGroup.getColumns();
		if (subColumns != null)
		{
			for (BaseColumn subColumn : subColumns)
			{
				subColumn.visitColumn(this);
			}
		}
		
		return null;
	}

	protected void visitBaseColumn(BaseColumn column)
	{
		visitCell(column.getTableHeader());
		visitCell(column.getTableFooter());
		
		visitCell(column.getColumnHeader());
		visitCell(column.getColumnFooter());
		
		List<GroupCell> groupHeaders = column.getGroupHeaders();
		if (groupHeaders != null)
		{
			for (GroupCell groupCell : groupHeaders)
			{
				visitCell(groupCell.getCell());
			}
		}
		
		List<GroupCell> groupFooters = column.getGroupFooters();
		if (groupFooters != null)
		{
			for (GroupCell groupCell : groupFooters)
			{
				visitCell(groupCell.getCell());
			}
		}
	}
	
	protected void visitCell(Cell cell)
	{
		if (cell != null)
		{
			ElementsVisitorUtils.visitElements(visitor, cell.getChildren());
		}
	}
	

}

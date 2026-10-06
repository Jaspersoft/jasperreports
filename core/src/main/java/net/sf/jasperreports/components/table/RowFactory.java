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
package net.sf.jasperreports.components.table;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.base.JRBaseObjectFactory;

/**
 * 
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class RowFactory
{
	private final JRBaseObjectFactory factory;
	
	public RowFactory(JRBaseObjectFactory factory)
	{
		this.factory = factory;
	}

	public BaseCell createBaseCell(BaseCell cell)
	{
		BaseCell newCell;
		if (cell == null)
		{
			newCell = null;
		}
		else
		{
			newCell = new CompiledBaseCell(cell, factory);
		}
		return newCell;
	}
	
	public Row createRow(Row row)
	{
		Row newRow;
		if (row == null)
		{
			newRow = null;
		}
		else
		{
			newRow = new CompiledRow(row, factory);
		}
		return newRow;
	}
	
	public List<GroupRow> createGroupRows(List<GroupRow> rows)
	{
		List<GroupRow> newRows;
		if (rows == null)
		{
			newRows = null;
		}
		else
		{
			newRows = new ArrayList<>(rows.size());
			for (GroupRow groupRow : rows)
			{
				GroupRow newRow = new StandardGroupRow(groupRow, this);
				newRows.add(newRow);
			}
		}
		return newRows;
	}

	public JRBaseObjectFactory getBaseObjectFactory()
	{
		return factory;
	}
}

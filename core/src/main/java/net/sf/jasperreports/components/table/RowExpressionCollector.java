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
package net.sf.jasperreports.components.table;

import java.util.List;

import net.sf.jasperreports.engine.JRExpressionCollector;

/**
 * 
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class RowExpressionCollector
{

	private final JRExpressionCollector datasetCollector;
	
	public RowExpressionCollector(JRExpressionCollector datasetCollector)
	{
		this.datasetCollector = datasetCollector;
	}

	public void collectGroupRows(List<GroupRow> groupRows)
	{
		if (groupRows != null)
		{
			for (GroupRow groupRow : groupRows)
			{
				collectRow(groupRow.getRow());
			}
		}
	}
	
	public void collectRow(Row row)
	{
		if (row != null)
		{
			datasetCollector.addExpression(row.getPrintWhenExpression());
		}
	}
	
}

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
package net.sf.jasperreports.crosstabs;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.jasperreports.crosstabs.design.JRDesignCrosstab;
import net.sf.jasperreports.engine.ElementsVisitor;
import net.sf.jasperreports.engine.JRVisitor;
import net.sf.jasperreports.engine.util.ElementsVisitorUtils;
import net.sf.jasperreports.engine.util.JRDelegationVisitor;

/**
 * Deep crosstab visitor.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class CrosstabDeepVisitor extends JRDelegationVisitor implements ElementsVisitor
{

	/**
	 * Creates a deep crosstab visitor.
	 * 
	 * @param visitor the elements visitor which is to visit crosstab nested elements
	 */
	public CrosstabDeepVisitor(JRVisitor visitor)
	{
		super(visitor);
	}

	@Override
	public boolean visitDeepElements()
	{
		return true;
	}
	
	/**
	 * Visits all the elements nested into a crosstab.
	 * 
	 * @param crosstab the crosstab whose elements to visit
	 */
	public void deepVisitCrosstab(JRCrosstab crosstab)
	{
		visitCrosstabCell(crosstab.getWhenNoDataCell());
		if (crosstab.getTitleCell() != null)
		{
			visitCrosstabCell(crosstab.getTitleCell().getCellContents());
		}
		visitCrosstabCell(crosstab.getHeaderCell());
		
		JRCrosstabRowGroup[] rowGroups = crosstab.getRowGroups();
		for (JRCrosstabRowGroup rowGroup : rowGroups)
		{
			visitCrosstabCell(rowGroup.getHeader());
			visitCrosstabCell(rowGroup.getTotalHeader());
		}
		
		JRCrosstabColumnGroup[] columnGroups = crosstab.getColumnGroups();
		for (JRCrosstabColumnGroup columnGroup : columnGroups)
		{
			visitCrosstabCell(columnGroup.getCrosstabHeader());
			visitCrosstabCell(columnGroup.getHeader());
			visitCrosstabCell(columnGroup.getTotalHeader());
		}
		
		if (crosstab instanceof JRDesignCrosstab)
		{
			List<JRCrosstabCell> cells = ((JRDesignCrosstab) crosstab).getCellsList();
			for (JRCrosstabCell cell : cells)
			{
				visitCrosstabCell(cell.getContents());
			}
		}
		else
		{
			JRCrosstabCell[][] cells = crosstab.getCells();
			if (cells != null)
			{
				Set<JRCellContents> cellContents = new HashSet<>();
				for (int i = 0; i < cells.length; i++)
				{
					for (int j = 0; j < cells[i].length; j++)
					{
						JRCrosstabCell cell = cells[i][j];
						if (cell != null && cell.getContents() != null
								&& cellContents.add(cell.getContents()))
						{
							visitCrosstabCell(cell.getContents());
						}
					}
				}
			}
		}
	}
	
	protected void visitCrosstabCell(JRCellContents cell)
	{
		if (cell != null)
		{
			ElementsVisitorUtils.visitElements(this, cell.getChildren());
		}
	}

}

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

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class NestedTableCell implements Cell
{
	private FrameCell parent;
	private final Table table;
	
	private SplitCell splitCell;

	public NestedTableCell(FrameCell parent, Table table)
	{
		this.parent = parent;
		this.table = table;
	}
	
	@Override
	public FrameCell getParent()
	{
		return parent;
	}

	public void setParent(FrameCell parent)
	{
		this.parent = parent;
	}

	@Override
	public Cell split()
	{
		if (splitCell == null)
		{
			splitCell = new SplitCell(this);
		}
		
		return splitCell;
	}

	@Override
	public <T, R, E extends Exception> R accept(CellVisitor<T, R, E> visitor, T arg) throws E
	{
		return visitor.visit(this, arg);
	}

	public Table getTable()
	{
		return table;
	}

}

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
package net.sf.jasperreports.engine.fill;

import net.sf.jasperreports.engine.JRPrintPage;

/**
 * 
 */
public class SimpleElementRange implements ElementRange
{
	private final JRPrintPage page;
	private final int columnIndex;
	private final int topY;
	private int bottomY;
	private final int firstElementIndex;
	private int lastElementIndex;
	
	public SimpleElementRange(
		JRPrintPage page,
		int columnIndex,
		int topY
		)
	{
		this.page = page;
		this.columnIndex = columnIndex;

		this.firstElementIndex = page.getElements().size(); // this points to the next element added to the page, if any
		this.lastElementIndex = firstElementIndex - 1; // if endElementIndex does not change, it means there is no content to move
		
		this.topY = topY;
		this.bottomY = topY;
	}
	
	@Override
	public JRPrintPage getPage()
	{
		return page;
	}
	
	@Override
	public int getColumnIndex()
	{
		return columnIndex;
	}
	
	@Override
	public int getTopY()
	{
		return topY;
	}
	
	@Override
	public int getBottomY()
	{
		return bottomY;
	}
	
	@Override
	public int getFirstElementIndex()
	{
		return firstElementIndex;
	}
	
	@Override
	public int getLastElementIndex()
	{
		return lastElementIndex;
	}
	
	@Override
	public void expand(int bottomY)
	{
		this.bottomY = bottomY;
		this.lastElementIndex = page.getElements().size() - 1;
	}
}
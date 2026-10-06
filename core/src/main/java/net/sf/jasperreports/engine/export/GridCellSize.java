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
public class GridCellSize
{
	private final int width;
	private final int height;
	private final int colSpan;
	private final int rowSpan;
	
	public GridCellSize(int width, int height, int colSpan, int rowSpan)
	{
		this.width = width;
		this.height = height;
		this.colSpan = colSpan;
		this.rowSpan = rowSpan;
	}

	@Override
	public int hashCode()
	{
		return (17 * width) ^ (19 * height) ^ (37 * colSpan) ^ (67 * rowSpan);
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof GridCellSize))
		{
			return false;
		}
		
		GridCellSize s = (GridCellSize) o;
		return width == s.width && height == s.height
				&& colSpan == s.colSpan && rowSpan == s.rowSpan;
	}
	
	@Override
	public String toString()
	{
		return "{" + width + ", " + height + ", " + colSpan + ", " + rowSpan + "}";
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public int getColSpan()
	{
		return colSpan;
	}

	public int getRowSpan()
	{
		return rowSpan;
	}
}

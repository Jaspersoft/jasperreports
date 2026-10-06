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
public abstract class DimensionEntry implements Comparable<DimensionEntry>
{
	// keeping this smaller so that PLUS_INF - MINUS_INF does not overflow
	protected static final int MINUS_INF = 0xfe000000;
	
	// we need this to be bigger than JRBaseFiller.PAGE_HEIGHT_PAGINATION_IGNORED to cover some cases with page breaks in unpaginated reports
	protected static final int PLUS_INF = 0x7d100000;
	
	protected int startCoord;
	protected int endCoord;
	
	public DimensionEntry(int startCoord)
	{
		this.startCoord = startCoord;
	}

	public int getStartCoord()
	{
		return startCoord;
	}

	public int getEndCoord()
	{
		return endCoord;
	}
	
	public int getExtent()
	{
		return endCoord - startCoord;
	}

	@Override
	public int compareTo(DimensionEntry o)
	{
		return (startCoord < o.startCoord) ? -1 : ((startCoord == o.startCoord) ? 0 : 1);
	}
	
	@Override
	public String toString()
	{
		boolean minusInf = startCoord == MINUS_INF;
		boolean plusInf = endCoord == PLUS_INF;
		return (minusInf ? "-inf" : startCoord)
				+ " - " 
				+ (plusInf ? "+inf" : endCoord) 
				+ ((minusInf || plusInf) ? "" : (" (" + (endCoord - startCoord) + ")"));
	}
}
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
package net.sf.jasperreports.engine.fill;

import java.util.Collection;

import net.sf.jasperreports.engine.JRPrintElement;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class OffsetElements
{
	
	private final Collection<? extends JRPrintElement> elements;
	private final int offsetX;
	private final int offsetY;
	private final double dpiScale;
	
	public OffsetElements(Collection<? extends JRPrintElement> elements,
			int offsetX, int offsetY)
	{
		this(elements, offsetX, offsetY, 1d);
	}

	public OffsetElements(Collection<? extends JRPrintElement> elements,
			int offsetX, int offsetY, double dpiScale)
	{
		this.elements = elements;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.dpiScale = dpiScale;
	}

	public Collection<? extends JRPrintElement> getElements()
	{
		return elements;
	}

	public int getOffsetX()
	{
		return offsetX;
	}

	public int getOffsetY()
	{
		return offsetY;
	}

	public double getDpiScale()
	{
		return dpiScale;
	}

}

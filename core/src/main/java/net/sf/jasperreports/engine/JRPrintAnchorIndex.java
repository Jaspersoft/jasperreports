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
package net.sf.jasperreports.engine;



/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRPrintAnchorIndex
{


	/**
	 *
	 */
	private int pageIndex;
	private JRPrintElement element;
	private final int offsetX;
	private final int offsetY;

	
	/**
	 * Creates an element anchor.
	 * 
	 * @param page the index of the page in the report
	 * @param elem the element
	 */
	public JRPrintAnchorIndex(
			int page,
			JRPrintElement elem)
	{
		this (page, elem, 0, 0);
	}
	
	
	/**
	 * Creates an element anchor.
	 * 
	 * @param page the index of the page in the report
	 * @param elem the element
	 * @param offsetX the X offset of the element coordinates system
	 * @param offsetY the Y offset of the element coordinates system
	 * @see #getElementAbsoluteX()
	 * @see #getElementAbsoluteY()
	 */
	public JRPrintAnchorIndex(int page, JRPrintElement elem, int offsetX, int offsetY)
	{
		this.pageIndex = page;
		this.element = elem;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}


	/**
	 * 
	 */
	public int getPageIndex()
	{
		return this.pageIndex;
	}
		

	/**
	 *
	 */
	public JRPrintElement getElement()
	{
		return this.element;
	}
	
	
	/**
	 * Returns the absolute (relative to the report page) X coordinate of the anchor element.
	 * <p>
	 * This can be different than {@link JRPrintElement#getX() getElement().getX()} for elements
	 * placed inside a {@link JRPrintFrame frame}.
	 * </p>
	 * 
	 * @return the absolute X coordinate of the anchor element
	 */
	public int getElementAbsoluteX()
	{
		return element.getX() + offsetX;
	}
	
	
	/**
	 * Returns the absolute (relative to the report page) Y coordinate of the anchor element.
	 * <p>
	 * This can be different than {@link JRPrintElement#getY() getElement().getY()} for elements
	 * placed inside a {@link JRPrintFrame frame}.
	 * </p>
	 * 
	 * @return the absolute Y coordinate of the anchor element
	 */
	public int getElementAbsoluteY()
	{
		return element.getY() + offsetY;
	}


}

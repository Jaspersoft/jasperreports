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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintElementContainer;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRPrintBand implements JRPrintElementContainer, OffsetElementsContainer
{
	

	/**
	 *
	 */
	private int height;
	// adding both elements and OffsetElements to this list
	private List<Object> elements = new ArrayList<>();
	private int contentsWidth;
	private boolean iterated = false;

	
	@Override
	public int getHeight()
	{
		return this.height;
	}
		
	@Override
	public void setHeight(int height)
	{
		this.height = height;
	}

	@Override
	public List<JRPrintElement> getElements()
	{
		// should not be called
		throw new UnsupportedOperationException();
	}
	
	public void consumeElement(Consumer<JRPrintElement> consumer)
	{
		// we only allow one iteration of the elements because we apply 
		// the offsets during iteration
		if (iterated)
		{
			// this should not happen
			throw new IllegalStateException("Elements already consumed");
		}
		
		iterated = true;
		OffsetElementsUtil.transfer(elements, consumer);
	}
	
	@Override
	public void addElement(JRPrintElement element)
	{
		this.elements.add(element);
	}
	
	@Override
	public void addOffsetElements(Collection<? extends JRPrintElement> elements, int offsetX, int offsetY)
	{
		addOffsetElements(elements, offsetX, offsetY, 1d);
	}

	@Override
	public void addOffsetElements(Collection<? extends JRPrintElement> elements, int offsetX, int offsetY, double dpiScale)
	{
		if (elements == null || elements.isEmpty())
		{
			return;
		}
		
		OffsetElements offsetElements = new OffsetElements(elements, offsetX, offsetY, dpiScale);
		this.elements.add(offsetElements);
	}
	
	@Override
	public void setContentsWidth(int width)
	{
		this.contentsWidth = width;
	}
	
	public int getContentsWidth()
	{
		return contentsWidth;
	}
}

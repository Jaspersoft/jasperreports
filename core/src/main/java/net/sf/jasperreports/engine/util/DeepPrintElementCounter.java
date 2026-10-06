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
package net.sf.jasperreports.engine.util;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;

/**
 * Print element visitor that counts deep elements by recursively visiting
 * {@link JRPrintFrame} containers.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
//we need a mutable integer argument, using AtomicInteger
public class DeepPrintElementCounter extends UniformPrintElementVisitor<AtomicInteger>
{

	private static final DeepPrintElementCounter INSTANCE = new DeepPrintElementCounter();
	
	/**
	 * Calculates the deep element count of an element.
	 * 
	 * @param element
	 * @return the deep element count of the element
	 */
	public static int count(JRPrintElement element)
	{
		return count(element, INSTANCE);
	}
	
	protected static int count(JRPrintElement element, UniformPrintElementVisitor<AtomicInteger> counter)
	{
		if (element == null)
		{
			return 0;
		}
		
		AtomicInteger count = new AtomicInteger(0);
		element.accept(counter, count);
		return count.get();
	}
	
	public static int count(Collection<? extends JRPrintElement> elements)
	{
		return count(elements, INSTANCE);
	}
	
	protected static int count(Collection<? extends JRPrintElement> elements, 
			UniformPrintElementVisitor<AtomicInteger> counter)
	{
		if (elements == null || elements.isEmpty())
		{
			return 0;
		}
		
		AtomicInteger count = new AtomicInteger(0);
		for (JRPrintElement element : elements)
		{
			element.accept(counter, count);
		}
		return count.get();
	}
	
	protected DeepPrintElementCounter()
	{
		super(true);
	}

	@Override
	protected void visitElement(JRPrintElement element, AtomicInteger count)
	{
		count.incrementAndGet();		
	}
	
}

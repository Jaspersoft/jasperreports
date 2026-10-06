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

import java.util.LinkedList;


/**
 * Thread local stack utility class.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ThreadLocalStack
{
	private final ThreadLocal<LinkedList<Object>> threadStack;
	
	public ThreadLocalStack()
	{
		threadStack = new ThreadLocal<>();
	}
	
	public void push(Object o)
	{
		LinkedList<Object> stack = threadStack.get();
		if (stack == null)
		{
			stack = new LinkedList<>();
			threadStack.set(stack);
		}
		stack.addFirst(o);
	}
	
	public Object top()
	{
		Object o = null;
		LinkedList<Object> stack = threadStack.get();
		if (stack != null && !stack.isEmpty())
		{
			o = stack.getFirst();
		}
		return o;
	}
	
	public Object pop()
	{
		Object o = null;
		LinkedList<Object> stack = threadStack.get();
		if (stack != null)
		{
			o = stack.removeFirst();
		}
		return o;
	}
	
	public boolean empty()
	{
		LinkedList<Object> stack = threadStack.get();
		return stack == null || stack.isEmpty();
	}
}

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

import java.util.Arrays;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class SortedIntList
{
	private int size;
	//FIXME implement a list composed of blocks so that we don't need to grow/shift large arrays
	private int[] values;

	public SortedIntList()
	{
		this.size = 0;
		this.values = new int[64];
	}

	public int size()
	{
		return size;
	}
	
	public boolean isEmpty()
	{
		return size == 0;
	}
	
	public int get(int index)
	{
		if (index < 0 || index >= size)
		{
			throw new IndexOutOfBoundsException("index " + index + " out of bounds, size " + size);
		}
		
		return values[index];
	}
	
	public boolean add(int value)
	{
		int idx = Arrays.binarySearch(values, 0, size, value);
		
		if (idx >= 0)
		{
			return false;
		}
		
		if (size >= values.length)
		{
			int newLength = values.length + (values.length >> 1);
			values = Arrays.copyOf(values, newLength);
		}
		
		int index = -idx - 1;
		if (index < size)
		{
			System.arraycopy(values, index, values, index + 1, size - index);
		}
		
		values[index] = value;
		++size;
		return true;
	}

	public boolean remove(int value)
	{
		int index = Arrays.binarySearch(values, 0, size, value);
		if (index < 0)
		{
			return false;
		}
		
		if (index < size - 1)
		{
			System.arraycopy(values, index + 1, values, index, size - index - 1);
		}
		--size;
		return true;
	}
	
	public int indexOf(int value)
	{
		int idx = Arrays.binarySearch(values, 0, size, value);
		
		if (idx < 0)
		{
			idx = -1;
		}
		
		return idx;
	}
}

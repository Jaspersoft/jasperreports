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
package net.sf.jasperreports.engine.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.jasperreports.engine.JRCloneable;

/**
 * Cloning related utility methods.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public final class JRCloneUtils
{

	/**
	 * Clones an object after checking whether the argument is null.
	 * 
	 * @param original the object to be cloned
	 * @return a clone of the argument, or <code>null</code> if the argument was
	 * <code>null</code>
	 */
	public static <T extends JRCloneable> T nullSafeClone(T original)
	{
		return original == null ? null : (T)original.clone();
	}
	
	/**
	 * Clones a list of objects.
	 * 
	 * The list elements are assumed to implement {@link JRCloneable}.
	 * 
	 * @param items the list to clone
	 * @return a new list which contains clones of the elements in the original list
	 */
	public static <T extends JRCloneable> List<T> cloneList(List<T> items)
	{
		List<T> clone;
		if (items == null)
		{
			clone = null;
		}
		else
		{
			clone = new ArrayList<>(items.size());
			for (T item : items)
			{
				clone.add(JRCloneUtils.nullSafeClone(item));
			}
		}
		return clone;
	}
	

	/**
	 * Clones a list of objects.
	 * 
	 * The list elements are assumed to implement {@link JRCloneable}.
	 * 
	 * @param items the list to clone
	 * @return a new list which contains clones of the elements in the original list
	 */
	public static <T extends JRCloneable> T[] cloneArray(T[] items)
	{
		if (items == null)
		{
			return null;
		}
		
		T[] clone = items.clone();
		Arrays.setAll(clone, i -> JRCloneUtils.nullSafeClone(items[i]));
		return clone;
	}
	

	private JRCloneUtils()
	{
	}
}

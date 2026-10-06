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

import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRCloneable;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class CloneStore
{

	private Map<JRCloneable, JRCloneable> store;
	
	public CloneStore()
	{
		this.store = new HashMap<>();
	}
	
	public <T extends StoreCloneable<T>> void store(T original, T clone)
	{
		store.put(original, clone);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends JRCloneable> T clone(T original)
	{
		if (original == null)
		{
			return null;
		}
		
		T clone = (T) store.get(original);
		if (clone == null)
		{
			if (original instanceof StoreCloneable<?>)
			{
				clone = (T) ((StoreCloneable<?>) original).clone(this);
			}
			else
			{
				clone = (T) original.clone();
			}
		}
		return clone;
	}
	
}

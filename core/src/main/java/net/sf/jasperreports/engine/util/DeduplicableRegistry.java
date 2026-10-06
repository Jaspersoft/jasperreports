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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.Deduplicable;

/**
 * A registry of deduplicable objects.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class DeduplicableRegistry
{

	private static final Log log = LogFactory.getLog(DeduplicableRegistry.class);
	
	protected static class DeduplicableWrapper<T extends Deduplicable>
	{
		private final T object;
		private final int hash;
		
		public DeduplicableWrapper(T object)
		{
			this.object = object;
			this.hash = object.getHashCode();
		}
		
		@Override
		public int hashCode()
		{
			return hash;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object other)
		{
			return object.isIdentical(((DeduplicableWrapper<T>) other).object);
		}
	}
	
	protected static class DeduplicableMap<T extends Deduplicable>
	{
		private final Map<DeduplicableWrapper<T>, T> objects = new HashMap<>();
		
		public T deduplicate(T object)
		{
			DeduplicableWrapper<T> key = new DeduplicableWrapper<>(object);
			T existing = objects.get(key);
			if (existing == null)
			{
				objects.put(key, object);
				existing = object;
				
				if (log.isDebugEnabled())
				{
					log.debug("Added object " + object);
				}
			}
			else
			{
				if (log.isDebugEnabled())
				{
					log.debug("Found existing instance " + existing + " for object " + object);
				}
			}
			return existing;
		}
	}
	
	private final Map<Class<? extends Deduplicable>, DeduplicableMap<?>> typesMap = new HashMap<>();
	
	/**
	 * Search for a duplicate of a given object in the registry, and add the object
	 * to the registry if no duplicate found.
	 * 
	 * @param object the object to be searched or added
	 * @return a duplicate of the object if found, or the passed object if not
	 */
	public <T extends Deduplicable> T deduplicate(T object)
	{
		DeduplicableMap<T> typeMap = typeMap(object);
		return typeMap.deduplicate(object);
	}
	
	protected <T extends Deduplicable> DeduplicableMap<T> typeMap(T object)
	{
		@SuppressWarnings("unchecked")
		DeduplicableMap<T> typeMap = (DeduplicableMap<T>) typesMap.get(object.getClass());
		if (typeMap == null)
		{
			typeMap = new DeduplicableMap<>();
			typesMap.put(object.getClass(), typeMap);
		}
		return typeMap;
	}
}

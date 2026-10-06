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
package net.sf.jasperreports.data.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class TypedColumnStore implements ColumnStore
{

	private static final Log log = LogFactory.getLog(TypedColumnStore.class);
	
	private final ColumnDataCacheHandler cacheHandler;
	private final Class<?> baseType;
	private ColumnStore valueStore;
	private Class<?> valueStoreType;
	private int count;
	private ColumnStore nullStore;
	
	public TypedColumnStore(ColumnDataCacheHandler cacheHandler, Class<?> baseType)
	{
		this.cacheHandler = cacheHandler;
		this.baseType = baseType;
		
		this.count = 0;
	}

	@Override
	public Class<?> getBaseValuesType()
	{
		return baseType;
	}
	
	@Override
	public void addValue(Object value)
	{
		if (value == null)
		{
			ensureNullStore();
			nullStore.addValue(true);
		}
		else
		{
			if (!ensureValueStore(value))
			{
				return;
			}

			if (!valueStoreType.isInstance(value))
			{
				// this shouldn't normally happen
				if (log.isDebugEnabled())
				{
					log.debug(this + ": value not instance of type " + baseType);
				}
				
				cacheHandler.disableCaching();
				return;
			}
			
			valueStore.addValue(value);
			
			// we need all values in the null store
			if (nullStore != null)
			{
				nullStore.addValue(false);
			}
		}

		++count;
	}

	protected void ensureNullStore()
	{
		if (nullStore == null)
		{
			nullStore = cacheHandler.createColumnStore(Boolean.class);
			
			if (log.isDebugEnabled())
			{
				log.debug(this + ": created null store " + nullStore);
			}
			
			// we need all values in the null store
			for (int i = 0; i < count; ++i)
			{
				nullStore.addValue(false);
			}
		}
	}

	protected boolean ensureValueStore(Object value)
	{
		if (valueStore == null)
		{
			// using the actual type of the first value to create the store
			Class<?> valueType = value.getClass();
			valueStore = cacheHandler.createColumnStore(valueType);
			
			if (log.isDebugEnabled())
			{
				log.debug(this + ": created value store " + valueStore + " for type " + valueType);
			}
			
			if (valueStore == null)
			{
				// the value type is not supported
				cacheHandler.disableCaching();
				return false;
			}
			
			valueStoreType = valueStore.getBaseValuesType();
			// check that the store values type is a subclass of the base type
			if (!baseType.isAssignableFrom(valueStoreType))
			{
				if (log.isDebugEnabled())
				{
					log.debug(this + ": store type " + valueStoreType 
							+ " is not compatible with base type " + baseType);
				}
				
				cacheHandler.disableCaching();
				return false;
			}
		}
		return true;
	}

	@Override
	public ColumnValues createValues()
	{
		ColumnValues values = valueStore == null ? EmptyColumnValues.instance() : valueStore.createValues();
		ColumnValues finalValues;
		if (nullStore == null)
		{
			// no nulls
			finalValues = values;
			
			if (log.isDebugEnabled())
			{
				log.debug(this + ": created not null values of size " + values.size());
			}
		}
		else
		{
			ColumnValues nullValues = nullStore.createValues();
			finalValues = new NullableValues(nullValues, values);
			
			if (log.isDebugEnabled())
			{
				log.debug(this + ": created not nulluable values of size " + nullValues.size()
						+ ", non null size " + values.size());
			}
		}
		return finalValues;
	}

	@Override
	public String toString()
	{
		return "TypedColumnStore@" + hashCode();
	}
	
}

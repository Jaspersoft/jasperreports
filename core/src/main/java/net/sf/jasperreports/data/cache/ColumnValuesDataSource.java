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
package net.sf.jasperreports.data.cache;

import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;
import net.sf.jasperreports.engine.data.IndexedDataSource;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ColumnValuesDataSource implements JRRewindableDataSource, IndexedDataSource
{
	private static final String EXCEPTION_MESSAGE_KEY_INVALID_SNAPSHOT_FIELD_TYPE = "data.cache.invalid.snapshot.field.type";
	private static final String EXCEPTION_MESSAGE_KEY_NO_SUCH_SNAPSHOT_FIELD = "data.cache.no.such.snapshot.field";

	private int size;
	private int iteratorIndex;
	private Map<String, ColumnValuesIterator> iterators;
	
	public ColumnValuesDataSource(String[] fieldNames, int size, ColumnValues[] values)
	{
		if (fieldNames == null || values == null || fieldNames.length != values.length)
		{
			throw new IllegalArgumentException();
		}
		
		iterators = new LinkedHashMap<>();
		
		this.size = size;
		for (int i = 0; i < fieldNames.length; i++)
		{
			if (size != values[i].size())
			{
				throw new IllegalArgumentException();
			}
			
			iterators.put(fieldNames[i], values[i].iterator());
		}
		
		iteratorIndex = 0;
	}
	
	@Override
	public boolean next() throws JRException
	{
		if (iteratorIndex >= size)
		{
			return false;
		}
		
		++iteratorIndex;
		for (ColumnValuesIterator iterator : iterators.values())
		{
			iterator.next();
		}
		
		return true;
	}

	@Override
	public Object getFieldValue(JRField field) throws DataSnapshotException
	{
		ColumnValuesIterator iterator = iterators.get(field.getName());
		if (iterator == null)
		{
			throw 
				new DataSnapshotException(
					EXCEPTION_MESSAGE_KEY_NO_SUCH_SNAPSHOT_FIELD,
					new Object[]{field.getName()});
		}
		
		Object value = iterator.get();
		
		if (value != null && !field.getValueClass().isInstance(value))
		{
			throw 
				new DataSnapshotException(
					EXCEPTION_MESSAGE_KEY_INVALID_SNAPSHOT_FIELD_TYPE,
					new Object[]{field.getName(), field.getValueClassName(), value.getClass().getName()});
		}
		
		return value;
	}

	@Override
	public void moveFirst()
	{
		iteratorIndex = 0;
		for (ColumnValuesIterator iterator : iterators.values())
		{
			iterator.moveFirst();
		}
	}

	@Override
	public int getRecordIndex()
	{
		return iteratorIndex - 1;
	}

}

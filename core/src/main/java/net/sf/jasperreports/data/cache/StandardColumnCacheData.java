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

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.data.IndexedDataSource;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class StandardColumnCacheData implements Serializable, ColumnCacheData
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private static final Log log = LogFactory.getLog(StandardColumnCacheData.class);
	
	private String[] fieldNames;
	private int size;
	private ColumnValues[] values;
	private LinkedHashMap<String, Object> parameters;
	
	public StandardColumnCacheData(String[] fieldNames, int size, ColumnValues[] values,
			LinkedHashMap<String, Object> parameters)
	{
		if (fieldNames == null || values == null || fieldNames.length != values.length)
		{
			throw new IllegalArgumentException();
		}
		
		this.fieldNames = fieldNames;
		this.size = size;
		this.values = values;
		this.parameters = parameters;
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		int paramsCount = parameters == null ? 0 : parameters.size();
		out.writeInt(paramsCount);
		if (parameters != null)
		{
			for (Map.Entry<String, Object> entry : parameters.entrySet())
			{
				out.writeObject(entry.getKey());
				out.writeObject(entry.getValue());
			}
		}
		
		out.writeInt(size);
		out.writeInt(fieldNames.length);
		for (int i = 0; i < fieldNames.length; i++)
		{
			out.writeObject(fieldNames[i]);
			out.writeObject(values[i]);
		}
	}
	
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		int paramsCount = in.readInt();
		if (paramsCount > 0)
		{
			parameters = new LinkedHashMap<>(paramsCount * 4 / 3);
			for (int i = 0; i < paramsCount; i++)
			{
				String key = (String) in.readObject();
				Object value = in.readObject();
				parameters.put(key, value);
			}
		}
		
		size = in.readInt();
		int fieldCount = in.readInt();
		fieldNames = new String[fieldCount];
		values = new ColumnValues[fieldCount];
		for (int i = 0; i < fieldCount; i++)
		{
			fieldNames[i] = (String) in.readObject();
			values[i] = (ColumnValues) in.readObject();
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jasperreports.data.cache.ColumnCacheData#isSerializable()
	 */
	@Override
	public boolean isSerializable()
	{
		// only checking parameters because we know that field values are serializable
		boolean serializable = true;
		if (parameters != null)
		{
			for (Map.Entry<String, Object> parameter : parameters.entrySet())
			{
				Object value = parameter.getValue();
				if (value != null && !(value instanceof Serializable))
				{
					if (log.isDebugEnabled())
					{
						log.debug("parameter " + parameter.getKey() 
								+ " has unserializable value of type " + value.getClass().getName());
					}
					
					serializable = false;
					break;
				}
			}
		}
		return serializable;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jasperreports.data.cache.ColumnCacheData#createDataSource()
	 */
	@Override
	public IndexedDataSource createDataSource()
	{
		return new ColumnValuesDataSource(fieldNames, size, values);
	}

	/* (non-Javadoc)
	 * @see net.sf.jasperreports.data.cache.ColumnCacheData#size()
	 */
	@Override
	public int size()
	{
		return size;
	}

	/* (non-Javadoc)
	 * @see net.sf.jasperreports.data.cache.ColumnCacheData#getParameters()
	 */
	@Override
	public Map<String, Object> getParameters()
	{
		return parameters;
	}
}
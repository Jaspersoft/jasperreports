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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.properties.PropertyConstants;

/**
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class FilteredObjectInputStream extends ObjectInputStream
{
	@Property(
		category = PropertyConstants.CATEGORY_OTHER,
		scopes = {PropertyScope.CONTEXT},
		sinceVersion = PropertyConstants.VERSION_7_0_4,
		valueType = Long.class
		)
	public static final String PROPERTY_BYTE_COUNT_LIMIT = 
		JRPropertiesUtil.PROPERTY_PREFIX + "deserialization.byte.count.limit";
	
	protected final JasperReportsContext jasperReportsContext;

	private ClassFilter deserializationFilter;

	/**
	 * Creates an object input stream that reads data from the specified
	 * {@link InputStream}.
	 * 
	 * @param in the input stream to read data from
	 * @throws IOException
	 * @see ObjectInputStream#ObjectInputStream(InputStream)
	 */
	public FilteredObjectInputStream(JasperReportsContext jasperReportsContext, InputStream in,
			ClassFilter deserializationFilter) throws IOException
	{
		super(wrapInputStream(jasperReportsContext, in));
		
		this.jasperReportsContext = jasperReportsContext;		
		this.deserializationFilter = deserializationFilter;
	}
	
	private static InputStream wrapInputStream(JasperReportsContext jasperReportsContext, InputStream is)
	{
		long byteCountLimit = JRPropertiesUtil.getInstance(jasperReportsContext).getLongProperty(PROPERTY_BYTE_COUNT_LIMIT, 0);
		return byteCountLimit == 0 ? is : new CountInputStream(is, byteCountLimit);
	}

	public JasperReportsContext getJasperReportsContext()
	{
		return jasperReportsContext;
	}

	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException,
			ClassNotFoundException
	{
		if (deserializationFilter.isFilteringEnabled())
		{
			String className = desc.getName();
			if (className.startsWith("["))
			{
				if (className.endsWith(";"))
				{
					className = className.substring(className.lastIndexOf("[L") + 2, className.length() - 1);
				}
				else
				{
					className = className.substring(className.lastIndexOf("[") + 1);
				}
			}
			deserializationFilter.checkClassVisibility(className);
		}

		return super.resolveClass(desc);
	}
}

class CountInputStream extends FilterInputStream
{
	public static final String EXCEPTION_MESSAGE_KEY_DESERIALIZATION_BYTE_COUNT_LIMIT_EXCEEDED = "deserialization.byte.count.limit.exceeded";

	private long byteCount = 0;
	private final long byteCountLimit; 

	public CountInputStream(InputStream is, long byteCountLimit)
	{
		super(is);
		
		this.byteCountLimit = byteCountLimit;
	}
	
	
	@Override
	public int read() throws IOException 
	{
		int r = super.read();
		if (r >= 0)
		{
			byteCount++;
			if (byteCountLimit > 0 && byteCount > byteCountLimit)
			{
				throw new JRRuntimeException(EXCEPTION_MESSAGE_KEY_DESERIALIZATION_BYTE_COUNT_LIMIT_EXCEEDED, new Object[] {byteCountLimit});
			}
		}
		return r;
	}

	@Override
	public int read(byte[] buf) throws IOException 
	{
		int r = super.read(buf);
		if (r >= 0)
		{
			byteCount += r;
			if (byteCountLimit > 0 && byteCount > byteCountLimit)
			{
				throw new JRRuntimeException(EXCEPTION_MESSAGE_KEY_DESERIALIZATION_BYTE_COUNT_LIMIT_EXCEEDED, new Object[] {byteCountLimit});
			}
		}
		return r;
	}

	@Override
	public int read(byte[] buf, int off, int len) throws IOException 
	{
		int r = super.read(buf, off, len);
		if (r >= 0)
		{
			byteCount += r;
			if (byteCountLimit > 0 && byteCount > byteCountLimit)
			{
				throw new JRRuntimeException(EXCEPTION_MESSAGE_KEY_DESERIALIZATION_BYTE_COUNT_LIMIT_EXCEEDED, new Object[] {byteCountLimit});
			}
		}
		return r;
	}
	
	@Override
	public long skip(long n) throws IOException 
	{
		long r = super.skip(n);
		byteCount += r;
		if (byteCountLimit > 0 && byteCount > byteCountLimit)
		{
			throw new JRRuntimeException(EXCEPTION_MESSAGE_KEY_DESERIALIZATION_BYTE_COUNT_LIMIT_EXCEEDED, new Object[] {byteCountLimit});
		}
		return r;
	}

}

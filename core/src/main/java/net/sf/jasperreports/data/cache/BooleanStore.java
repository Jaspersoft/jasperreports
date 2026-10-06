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
public class BooleanStore implements BufferColumnStore
{

	private static final Log log = LogFactory.getLog(BooleanStore.class);

	private final int size;
	private final LongArrayStore longArrayStore;

	private long currentLong;
	private int count;
	private boolean min;
	private boolean max;
	
	public BooleanStore(int size)
	{
		this.size = size;
		
		int longSize = 1 + ((size - 1) >>> 6);
		this.longArrayStore = new LongArrayStore(longSize);
		
		reset();
		
		if (log.isDebugEnabled())
		{
			log.debug(this + ": created long store " + longArrayStore);
		}
	}
	
	private void reset()
	{
		count = 0;
		min = true;
		max = false;
		currentLong = 0;
	}

	@Override
	public Class<?> getBaseValuesType()
	{
		return Boolean.class;
	}
	
	@Override
	public void addValue(Object object)
	{
		if (!(object instanceof Boolean))
		{
			throw new IllegalArgumentException();
		}
		
		boolean value = (Boolean) object;
		
		if (value)
		{
			currentLong |= 1L << count;
		}
		
		if ((count & 0x3F) == 0x3F)
		{
			longArrayStore.add(currentLong);
			currentLong = 0;
		}

		++count;
		min &= value;
		max |= value;
	}

	@Override
	public boolean full()
	{
		return count >= size;
	}

	@Override
	public void resetValues()
	{
		reset();
		longArrayStore.resetValues();
	}

	@Override
	public ColumnValues createValues()
	{
		if (count == 0)
		{
			if (log.isDebugEnabled())
			{
				log.debug(this + ": empty values");
			}
			
			return EmptyColumnValues.instance();
		}
		
		if (count == 1)
		{
			if (log.isDebugEnabled())
			{
				log.debug(this + ": single value");
			}
			
			return new SingleObjectValue(min);
		}
		
		if (min == max)
		{
			if (log.isDebugEnabled())
			{
				log.debug(this + ": constant value of size " + count);
			}
			
			return new ConstantColumnValue(count, min);
		}
		
		// add the last value
		if ((count & 0x3F) != 0)
		{
			longArrayStore.add(currentLong);
		}
		
		if (log.isDebugEnabled())
		{
			log.debug(this + ": creating long store of boolean size " +count);
		}
		
		ColumnValues longValues = longArrayStore.createValues();
		return new BooleanValues(count, longValues);
	}


	@Override
	public String toString()
	{
		return "BooleanStore@" + hashCode();
	}
}

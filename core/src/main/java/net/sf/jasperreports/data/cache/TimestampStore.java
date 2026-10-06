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

import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class TimestampStore implements BufferColumnStore
{
	
	private static final Log log = LogFactory.getLog(TimestampStore.class);

	private final LongArrayStore timeStore;
	private final LongArrayStore nanoStore;

	public TimestampStore(int size)
	{
		timeStore = new LongArrayStore(size, true);
		nanoStore = new LongArrayStore(size);
		
		if (log.isDebugEnabled())
		{
			log.debug(this + ": created time store " + timeStore);
			log.debug(this + ": created nano store " + nanoStore);
		}
	}

	@Override
	public Class<?> getBaseValuesType()
	{
		return Timestamp.class;
	}
	
	@Override
	public void addValue(Object object)
	{
		if (!(object instanceof Timestamp))
		{
			throw new IllegalArgumentException();
		}

		Timestamp value = (Timestamp) object;
		long time = value.getTime();
		int nanos = value.getNanos();
		timeStore.add(time);
		nanoStore.add(nanos);
	}

	@Override
	public boolean full()
	{
		return timeStore.full() || nanoStore.full();
	}

	@Override
	public void resetValues()
	{
		timeStore.resetValues();
		nanoStore.resetValues();
	}

	@Override
	public ColumnValues createValues()
	{
		ColumnValues timeValues = timeStore.createValues();
		ColumnValues nanoValues = nanoStore.createValues();
		// TODO lucianc check if nano values are all zero and skip
		return new TimestampValues(timeValues, nanoValues);
	}

	@Override
	public String toString()
	{
		return "TimestampStore@" + hashCode();
	}

}

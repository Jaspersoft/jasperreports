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
import java.sql.Timestamp;

import net.sf.jasperreports.engine.JRConstants;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class TimestampValues implements ColumnValues, Serializable
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private ColumnValues timeValues;
	private ColumnValues nanoValues;

	public TimestampValues(ColumnValues timeValues, ColumnValues nanoValues)
	{
		if (timeValues.size() != nanoValues.size())
		{
			throw new IllegalArgumentException();
		}
		
		this.timeValues = timeValues;
		this.nanoValues = nanoValues;
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		out.writeUnshared(timeValues);
		out.writeUnshared(nanoValues);
	}
	
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		this.timeValues = (ColumnValues) in.readUnshared();
		this.nanoValues = (ColumnValues) in.readUnshared();
	}

	@Override
	public int size()
	{
		return timeValues.size();
	}

	@Override
	public ColumnValuesIterator iterator()
	{
		ColumnValuesIterator timeIterator = timeValues.iterator();
		ColumnValuesIterator nanoIterator = nanoValues.iterator();
		return new TimestampValuesIterator(timeIterator, nanoIterator);
	}

}

class TimestampValuesIterator implements ColumnValuesIterator
{

	private final ColumnValuesIterator timeIterator;
	private final ColumnValuesIterator nanoIterator;

	public TimestampValuesIterator(ColumnValuesIterator timeIterator,
			ColumnValuesIterator nanoIterator)
	{
		this.timeIterator = timeIterator;
		this.nanoIterator = nanoIterator;
	}

	@Override
	public void moveFirst()
	{
		timeIterator.moveFirst();
		nanoIterator.moveFirst();
	}

	@Override
	public boolean next()
	{
		return timeIterator.next() && nanoIterator.next();
	}

	@Override
	public Object get()
	{
		long time = ((Number) timeIterator.get()).longValue();
		int nano = ((Number) nanoIterator.get()).intValue();
		
		Timestamp timestamp = new Timestamp(time);
		timestamp.setNanos(nano);
		return timestamp;
	}

}
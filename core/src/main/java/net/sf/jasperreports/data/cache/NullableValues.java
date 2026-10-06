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

import net.sf.jasperreports.engine.JRConstants;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class NullableValues implements ColumnValues, Serializable
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private ColumnValues nullValues;
	private ColumnValues values;
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		out.writeUnshared(nullValues);
		out.writeUnshared(values);
	}
	
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		this.nullValues = (ColumnValues) in.readUnshared();
		this.values = (ColumnValues) in.readUnshared();
	}

	public NullableValues(ColumnValues nullValues, ColumnValues values)
	{
		this.nullValues = nullValues;
		this.values = values;
	}

	@Override
	public int size()
	{
		return nullValues.size();
	}

	@Override
	public ColumnValuesIterator iterator()
	{
		ColumnValuesIterator nullsIterator = nullValues.iterator();
		ColumnValuesIterator valuesIterator = values.iterator();
		return new NullableIterator(nullsIterator, valuesIterator);
	}

}

class NullableIterator implements ColumnValuesIterator
{

	private final ColumnValuesIterator nulls;
	private final ColumnValuesIterator values;
	private Object value;

	public NullableIterator(ColumnValuesIterator nulls,
			ColumnValuesIterator values)
	{
		this.nulls = nulls;
		this.values = values;
	}

	@Override
	public void moveFirst()
	{
		nulls.moveFirst();
		values.moveFirst();
		value = null;
	}

	@Override
	public boolean next()
	{
		if (!nulls.next())
		{
			return false;
		}
		
		boolean isNull = (Boolean) nulls.get();
		if (isNull)
		{
			value = null;
		}
		else
		{
			if (!values.next())
			{
				throw new IllegalStateException();
			}
			
			value = values.get();
		}
		return true;
	}

	@Override
	public Object get()
	{
		return value;
	}
	
}
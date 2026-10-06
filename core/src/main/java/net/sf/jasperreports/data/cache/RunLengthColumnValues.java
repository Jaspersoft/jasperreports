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
public class RunLengthColumnValues implements ColumnValues, Serializable
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private int size;
	private ColumnValues values;
	private ColumnValues runLengths;

	public RunLengthColumnValues(int size, ColumnValues values,
			ColumnValues runLengths)
	{
		if (values.size() != runLengths.size())
		{
			throw new IllegalArgumentException();
		}
		
		this.size = size;
		this.values = values;
		this.runLengths = runLengths;
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		out.writeInt(size);
		out.writeUnshared(values);
		out.writeUnshared(runLengths);
	}
	
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		
		this.size = in.readInt();
		this.values = (ColumnValues) in.readUnshared();
		this.runLengths = (ColumnValues) in.readUnshared();
	}

	@Override
	public int size()
	{
		return size;
	}

	@Override
	public ColumnValuesIterator iterator()
	{
		ColumnValuesIterator valuesIterator = values.iterator();
		ColumnValuesIterator lengthsIterator = runLengths.iterator();
		return new RunLengthIterator(valuesIterator, lengthsIterator);
	}

}

class RunLengthIterator implements ColumnValuesIterator
{

	private final ColumnValuesIterator values;
	private final ColumnValuesIterator lengths;
	
	private int currentCount;
	private Object currentValue;

	public RunLengthIterator(ColumnValuesIterator values,
			ColumnValuesIterator lengths)
	{
		this.values = values;
		this.lengths = lengths;
		
		currentCount = 0;
		currentValue = null;
	}

	@Override
	public void moveFirst()
	{
		values.moveFirst();
		lengths.moveFirst();
		
		currentCount = 0;
		currentValue = null;
	}

	@Override
	public boolean next()
	{
		if (currentCount > 1)
		{
			--currentCount;
			return true;
		}
		
		if (!lengths.next())
		{
			return false;
		}
		
		if (!values.next())
		{
			throw new IllegalStateException();
		}
		
		currentCount = ((Number) lengths.get()).intValue();
		currentValue = values.get();
		return true;
	}

	@Override
	public Object get()
	{
		return currentValue;
	}

}
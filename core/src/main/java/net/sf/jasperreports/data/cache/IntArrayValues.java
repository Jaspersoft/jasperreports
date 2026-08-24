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

import java.io.IOException;
import java.io.Serializable;

import net.sf.jasperreports.engine.JRConstants;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class IntArrayValues implements ColumnValues, Serializable
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private int[] values;
	private long linearFactor;
	private long linearOffset;
	
	public IntArrayValues(int[] values, long linearFactor, long linearOffset)
	{
		this.values = values;
		this.linearFactor = linearFactor;
		this.linearOffset = linearOffset;
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		out.writeLong(linearFactor);
		out.writeLong(linearOffset);
		
		out.writeInt(values.length);
		for (int value : values)
		{
			out.writeInt(value);
		}
	}
	
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		linearFactor = in.readLong();
		linearOffset = in.readLong();
		
		int size = in.readInt();
		values = new int[size];
		for (int i = 0; i < size; i++)
		{
			values[i] = in.readInt();
		}
	}
	
	@Override
	public int size()
	{
		return values.length;
	}

	@Override
	public ColumnValuesIterator iterator()
	{
		return new ValuesIterator();
	}

	protected class ValuesIterator extends IndexColumnValueIterator
	{

		public ValuesIterator()
		{
			super(values.length);
		}

		@Override
		public Object get()
		{
			return (values[currentIndex] & 0xFFFFFFFFL) * linearFactor + linearOffset;
		}
		
	}
}

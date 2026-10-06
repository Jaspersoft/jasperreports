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
public class BooleanValues implements ColumnValues, Serializable
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private int size;
	private ColumnValues longValues;

	public BooleanValues(int size, ColumnValues longValues)
	{
		this.size = size;
		this.longValues = longValues;
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		out.writeInt(size);
		out.writeUnshared(longValues);
	}
	
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		size = in.readInt();
		longValues = (ColumnValues) in.readUnshared();
	}

	@Override
	public int size()
	{
		return size;
	}

	@Override
	public ColumnValuesIterator iterator()
	{
		return new BooleanIterator();
	}

	protected class BooleanIterator implements ColumnValuesIterator
	{
		private final ColumnValuesIterator longIterator;
		private int index;
		private long currentLong;

		public BooleanIterator()
		{
			longIterator = longValues.iterator();
			index = -1;
			currentLong = 0;
		}

		@Override
		public void moveFirst()
		{
			longIterator.moveFirst();
			index = -1;
			currentLong = 0;
		}

		@Override
		public boolean next()
		{
			if (index + 1 >= size)
			{
				return false;
			}
			
			++index;
			if ((index & 0x3F) == 0)
			{
				if (!longIterator.next())
				{
					throw new IllegalStateException();
				}
				
				currentLong = ((Number) longIterator.get()).longValue();
			}
			
			return true;
		}

		@Override
		public Object get()
		{
			return (currentLong & (1L << index)) != 0;
		}
	
	}
}

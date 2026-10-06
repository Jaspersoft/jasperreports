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

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class NumberValuesUtils
{
	
	private static final NumberValuesUtils INSTANCE = new NumberValuesUtils();
	
	public static NumberValuesUtils instance()
	{
		return INSTANCE;
	}
	
	// allow subclasses
	protected NumberValuesUtils()
	{		
	}
	
	public ColumnValues toValues(int count, long[] values, ValueLength valueLength, 
			long linearFactor, long linearOffset)
	{
		ColumnValues colValues;
		switch (valueLength) {
		case BYTE:
			// byte values
			colValues = toByteValues(count, values, linearFactor, linearOffset);
			break;
		case SHORT:
			// short values
			colValues = toShortValues(count, values, linearFactor, linearOffset);
			break;
		case INT:
			// int values
			colValues = toIntValues(count, values, linearFactor, linearOffset);
			break;
		default:
			colValues = toLongValues(count, values, linearFactor, linearOffset);
			break;
		}
		return colValues;
	}

	protected ColumnValues toByteValues(int count, long[] values, long linearFactor, long linearOffset)
	{
		byte[] byteValues = new byte[count];
		for (int i = 0; i < count; i++)
		{
			byteValues[i] = (byte) (values[i] & 0xFF);
		}
		return new ByteArrayValues(byteValues, linearFactor, linearOffset);
	}

	protected ColumnValues toShortValues(int count, long[] values, long linearFactor, long linearOffset)
	{
		short[] shortValues = new short[count];
		for (int i = 0; i < count; i++)
		{
			shortValues[i] = (short) (values[i] & 0xFFFF);
		}
		return new ShortArrayValues(shortValues, linearFactor, linearOffset);
	}

	protected ColumnValues toIntValues(int count, long[] values, long linearFactor, long linearOffset)
	{
		int[] intValues = new int[count];
		for (int i = 0; i < count; i++)
		{
			intValues[i] = (int) (values[i] & 0xFFFFFFFFL);
		}
		return new IntArrayValues(intValues, linearFactor, linearOffset);
	}

	protected ColumnValues toLongValues(int count, long[] values, long linearFactor, long linearOffset)
	{
		// always create a copy
		long[] longValues = new long[count];
		System.arraycopy(values, 0, longValues, 0, count);
		return new LongArrayValues(longValues, linearFactor, linearOffset);
	}

}

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
public class LongArrayStore implements BufferColumnStore, ArrayStore
{

	private static final Log log = LogFactory.getLog(LongArrayStore.class);

	private final boolean useGCD;
	private final ValueTransformer valueTransformer;
	
	private final long[] values;
	
	private int count;
	
	private long min;
	private long max;
	
	private RunLengthStore runLengthStore;
	
	public LongArrayStore(int size)
	{
		this(size, null, false);
	}
	
	public LongArrayStore(int size, ValueTransformer valueTransformer)
	{
		this(size, valueTransformer, false);
	}
	
	public LongArrayStore(int size, boolean useGCD)
	{
		this(size, null, useGCD);
	}

	@Override
	public Class<?> getBaseValuesType()
	{
		return valueTransformer == null ? Number.class : valueTransformer.getResultType();
	}
	
	public LongArrayStore(int size, ValueTransformer valueTransformer, boolean useGCD)
	{
		this.useGCD = useGCD;
		this.valueTransformer = valueTransformer;
		
		this.values = new long[size];
		this.runLengthStore = new RunLengthStore(this);
		reset();
	}
	
	private void reset()
	{
		this.count = 0;
		
		this.min = Long.MAX_VALUE;
		this.max = Long.MIN_VALUE;
		
		this.runLengthStore.reset();
	}

	@Override
	public int count()
	{
		return count;
	}

	@Override
	public boolean valuesEqual(int idx1, int idx2)
	{
		return values[idx1] == values[idx2];
	}

	@Override
	public void copyValue(int destIdx, int sourceIdx)
	{
		values[destIdx] = values[sourceIdx];
	}

	@Override
	public void updateCount(int count)
	{
		this.count = count;
	}
	
	public void add(long value)
	{
		values[count] = value;
		++count;
		
		if (value < min)
		{
			min = value;
		}
		if (value > max)
		{
			max = value;
		}
		
		runLengthStore.valueAdded();
	}

	@Override
	public void addValue(Object value)
	{
		if (value instanceof Integer
				|| value instanceof Long
				|| value instanceof Byte
				|| value instanceof Short)
		{
			add(((Number) value).longValue());
		}
		else if (value instanceof Character)
		{
			add(((Character) value).charValue());
		}
		else
		{
			throw new IllegalArgumentException();
		}
	}

	@Override
	public boolean full()
	{
		return count >= values.length;
	}
	
	@Override
	public void resetValues()
	{
		reset();
	}
	
	@Override
	public ColumnValues createValues()
	{
		if (count == 0)
		{
			// no values
			if (log.isDebugEnabled())
			{
				log.debug(this + ": no values");
			}
			
			return EmptyColumnValues.instance();
		}
		
		if (min == max)
		{
			// constant value
			if (log.isDebugEnabled())
			{
				log.debug(this + ": constant value of size " + count);
			}
			
			//FIXME keep value as primitive?
			// transform the value so that we don't need to store the transformer
			Object value = valueTransformer == null ? min : valueTransformer.get(min);
			return new ConstantColumnValue(count, value);
		}
		
		long linearOffset = 0;
		long linearFactor = 1;
		
		if (min != 0)
		{
			linearOffset = min;
			
			if (log.isDebugEnabled())
			{
				log.debug(this + ": using offset " + linearOffset);
			}
			
			// apply min
			for (int i = 0; i < count; i++)
			{
				values[i] -= linearOffset;
			}
			
			min = 0;
			max -= linearOffset;
		}
		
		if (useGCD)
		{
			long gcd = computeGCD();
			if (gcd > 1)
			{
				if (log.isDebugEnabled())
				{
					log.debug(this + ": using factor " + gcd);
				}
				
				for (int i = 0; i < count; i++)
				{
					values[i] /= gcd;
				}

				max /= gcd;
				linearFactor = gcd;
			}
		}
		
		int originalCount = count;
		ValueLength valueLength = ValueLength.getNumberLength(max);
		ColumnValues runLengthValues = runLengthStore.applyRunLengths(valueLength);
		
		if (log.isDebugEnabled())
		{
			log.debug(this + ": creating values of count " + count + ", value length " + valueLength);
		}
		
		ColumnValues colValues = NumberValuesUtils.instance().toValues(count, values, valueLength, linearFactor, linearOffset);
		if (valueTransformer != null)
		{
			colValues = new TransformedColumnValues(colValues, valueTransformer);
		}
		
		ColumnValues finalValues;
		if (runLengthValues == null)
		{
			finalValues = colValues;
		}
		else
		{
			finalValues = new RunLengthColumnValues(originalCount, colValues, runLengthValues);
		}
		return finalValues;
	}

	protected long computeGCD()
	{
		long gcd = values[0] - min;
		for (int i = 1; i < count; i++)
		{
			gcd = gcd(gcd, values[i] - min);
			if (gcd == 1)
			{
				return gcd;
			}
		}
		return gcd;
	}

	private static long gcd(long a, long b)
	{
		if (b == 0)
		{
			return a;
		}
		
		while (a > 0)
		{
			long t = a;
			a = b % a;
			b = t;
		}
		return b;
	}

	@Override
	public String toString()
	{
		return "LongArrayStore@" + hashCode();
	}

}

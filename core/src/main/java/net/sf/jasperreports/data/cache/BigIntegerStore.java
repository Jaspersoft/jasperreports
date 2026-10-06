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

import java.math.BigInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class BigIntegerStore implements BufferColumnStore
{

	private static final Log log = LogFactory.getLog(BigIntegerStore.class);
	
	private final ObjectArrayStore<BigInteger> rawStore;
	
	private BigInteger min;
	private BigInteger max;
	
	private LongArrayStore primitiveStore;
	
	public BigIntegerStore(int size)
	{
		this.rawStore = new ObjectArrayStore<>(BigInteger.class, size);
		reset();
		
		if (log.isDebugEnabled())
		{
			log.debug("created object store " + rawStore + " for " + this);
		}
	}
	
	private void reset()
	{
		this.rawStore.resetValues();
		
		this.min = BigInteger.valueOf(Long.MAX_VALUE);
		this.max = BigInteger.valueOf(Long.MIN_VALUE);
	}

	@Override
	public Class<?> getBaseValuesType()
	{
		return BigInteger.class;
	}

	@Override
	public void addValue(Object object)
	{
		if (!(object instanceof BigInteger))
		{
			throw new IllegalArgumentException();
		}
		
		BigInteger value = (BigInteger) object;
		rawStore.addValue(value);

		min = min.min(value);
		max = max.max(value);
	}

	@Override
	public boolean full()
	{
		return rawStore.full();
	}
	
	@Override
	public void resetValues()
	{
		reset();
	}
	
	@Override
	public ColumnValues createValues()
	{
		int count = rawStore.count();
		if (count == 0)
		{
			// no values
			if (log.isDebugEnabled())
			{
				log.debug(this + ": empty values");
			}
			
			return EmptyColumnValues.instance();
		}
		
		if (min.equals(max))
		{
			// constant value
			if (log.isDebugEnabled())
			{
				log.debug(this + ": constant values of size " + count);
			}
			
			return new ConstantColumnValue(count, min);
		}
		
		BigInteger delta = max.subtract(min);
		int deltaLength = delta.bitLength();
		boolean useOffset = false;
		ColumnValues primitiveValues = null;
		if (deltaLength < 64)// bitLength() excludes the sign
		{
			// if all values can be stored as long, do not use a BigInteger offset
			useOffset = !(min.signum() >= 0 && min.bitLength() < 64 
					&& max.signum() >=0 && max.bitLength() < 64);
			
			if (log.isDebugEnabled())
			{
				log.debug(this + ": creating privitive store of size " + count
						+ ", with offset " + useOffset);
			}
			
			primitiveValues = createPrimitiveValues(useOffset);
		}
		
		ColumnValues columnValues;
		if (primitiveValues == null)
		{
			if (log.isDebugEnabled())
			{
				log.debug(this + ": creating raw array store of size " + count);
			}
			
			return rawStore.createValues();
		}
		else
		{
			ValueTransformer transformer = useOffset 
					? new NumberToBigIntegerOffsetTransformer(min)
					: NumberToBigIntegerTransformer.instance();
			columnValues = new TransformedColumnValues(primitiveValues, transformer);
		}
		return columnValues;
	}

	protected ColumnValues createPrimitiveValues(boolean useOffset)
	{
		if (primitiveStore == null)
		{
			primitiveStore = new LongArrayStore(rawStore.size(), true);
		}
		else
		{
			primitiveStore.resetValues();
		}
		
		int count = rawStore.count();
		Object[] values = rawStore.valuesBuffer();
		for (int i = 0; i < count; i++)
		{
			BigInteger value = (BigInteger) values[i];
			if (useOffset)
			{
				value = value.subtract(min);
			}
			primitiveStore.add(value.longValue());
		}
		
		ColumnValues primitiveValues = primitiveStore.createValues();
		return primitiveValues;
	}

	@Override
	public String toString()
	{
		return "BigIntegerStore@" + hashCode();
	}
}

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
public class DoubleArrayStore implements BufferColumnStore, ArrayStore
{

	private static final Log log = LogFactory.getLog(DoubleArrayStore.class);

	private final double[] values;
	private int count;

	private RunLengthStore runLengthStore;
	
	public DoubleArrayStore(int size)
	{
		this.values = new double[size];
		this.runLengthStore = new RunLengthStore(this);
		reset();
	}
	
	private void reset()
	{
		this.count = 0;
		this.runLengthStore.reset();
	}

	@Override
	public Class<?> getBaseValuesType()
	{
		return Double.class;
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

	@Override
	public void addValue(Object object)
	{
		if (!(object instanceof Double))
		{
			throw new IllegalArgumentException();
		}
		
		double value = (Double) object;
		values[count] = value;
		++count;
		
		runLengthStore.valueAdded();
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
			
			return new SingleObjectValue(values[0]);
		}
		
		int originalCount = count;
		ColumnValues runLengthValues = runLengthStore.applyRunLengths(ValueLength.FLOAT);
		
		if (log.isDebugEnabled())
		{
			log.debug(this + ": creating values of size " + count);
		}
		
		double[] doubleValues = new double[count];
		System.arraycopy(values, 0, doubleValues, 0, count);
		DoubleArrayValues colValues = new DoubleArrayValues(doubleValues);
		
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

	@Override
	public String toString()
	{
		return "DoubleArrayStore@" + hashCode();
	}

}

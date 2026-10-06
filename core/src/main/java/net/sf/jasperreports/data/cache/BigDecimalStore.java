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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class BigDecimalStore implements BufferColumnStore
{

	private static final Log log = LogFactory.getLog(BigDecimalStore.class);
	
	private final BigIntegerStore valueStore;
	private final LongArrayStore scaleStore;

	public BigDecimalStore(int size)
	{
		valueStore = new BigIntegerStore(size);
		scaleStore = new LongArrayStore(size);
		
		if (log.isDebugEnabled())
		{
			log.debug(this + ": created value store " + valueStore);
			log.debug(this + ": created scale store " + scaleStore);
		}
	}

	@Override
	public Class<?> getBaseValuesType()
	{
		return BigDecimal.class;
	}
	
	@Override
	public void addValue(Object object)
	{
		if (!(object instanceof BigDecimal))
		{
			throw new IllegalArgumentException();
		}

		BigDecimal value = (BigDecimal) object;
		BigInteger unscaledValue = value.unscaledValue();
		int scale = value.scale();
		
		valueStore.addValue(unscaledValue);
		scaleStore.addValue(scale);
	}

	@Override
	public boolean full()
	{
		return valueStore.full() || scaleStore.full();
	}

	@Override
	public void resetValues()
	{
		valueStore.resetValues();
		scaleStore.resetValues();
	}

	@Override
	public ColumnValues createValues()
	{
		// TODO lucianc check empty
		ColumnValues unscaledValues = valueStore.createValues();
		ColumnValues scaleValues = scaleStore.createValues();
		return new BigDecimalValues(unscaledValues, scaleValues);
	}

	@Override
	public String toString()
	{
		return "BigDecimalStore@" + hashCode();
	}

}

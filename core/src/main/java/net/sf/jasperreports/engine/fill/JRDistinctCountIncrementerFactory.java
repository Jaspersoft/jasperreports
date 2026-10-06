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
package net.sf.jasperreports.engine.fill;

import java.util.HashSet;
import java.util.Set;

import net.sf.jasperreports.engine.type.CalculationEnum;
import net.sf.jasperreports.engine.type.ResetTypeEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRDistinctCountIncrementerFactory implements JRIncrementerFactory
{


	/**
	 *
	 */
	private static JRDistinctCountIncrementerFactory mainInstance = new JRDistinctCountIncrementerFactory();


	/**
	 *
	 */
	public JRDistinctCountIncrementerFactory()
	{
	}


	/**
	 *
	 */
	public static JRDistinctCountIncrementerFactory getInstance()
	{
		return mainInstance;
	}


	@Override
	public JRIncrementer getIncrementer(CalculationEnum calculation)
	{
		return new JRDistinctCountIncrementer();
	}
}


/**
 *
 */
class JRDistinctCountIncrementer implements JRIncrementer
{

	private DistinctCountHolder lastHolder = new DistinctCountHolder();
	
	
	/**
	 *
	 */
	public JRDistinctCountIncrementer()
	{
	}


	@Override
	public Object increment(
		JRFillVariable variable, 
		Object expressionValue,
		AbstractValueProvider valueProvider
		)
	{
		DistinctCountHolder holder  = (DistinctCountHolder)variable.getIncrementedValue();

		if (holder == null)
		{
			holder = lastHolder;
		}
		else
		{
			lastHolder = holder;
		}
		
		if (variable.getResetType() == ResetTypeEnum.REPORT || variable.isInitialized())
		{
			holder.addLastValue();
		}

		return new DistinctCountHolder(holder, expressionValue);
	}


}


/**
 *
 */
class DistinctCountHolder
{
	private Set<Object> distinctValues;
	private Object lastValue;

	public DistinctCountHolder()
	{
		distinctValues = new HashSet<>();
	}

	public DistinctCountHolder(Set<Object> distinctValues)
	{
		this.distinctValues = distinctValues;
	}

	public DistinctCountHolder(DistinctCountHolder holder, Object lastValue)
	{
		this.distinctValues = holder.getDistinctValues();
		this.lastValue = lastValue;
	}

	public void init()
	{
		distinctValues = new HashSet<>();
	}

	public Set<Object> getDistinctValues()
	{
		return distinctValues;
	}

	public Object getLastValue()
	{
		return lastValue;
	}

	public void addLastValue()
	{
		if (lastValue != null)
		{
			distinctValues.add(lastValue);
		}
		lastValue = null;
	}

	public long getCount()
	{
		return distinctValues.size() + (lastValue == null || distinctValues.contains(lastValue) ? 0 : 1);
	}
}

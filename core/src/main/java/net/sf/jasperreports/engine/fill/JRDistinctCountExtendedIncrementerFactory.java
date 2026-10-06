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


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRDistinctCountExtendedIncrementerFactory extends JRAbstractExtendedIncrementerFactory
{


	/**
	 *
	 */
	private static JRDistinctCountExtendedIncrementerFactory mainInstance = new JRDistinctCountExtendedIncrementerFactory();


	/**
	 *
	 */
	public JRDistinctCountExtendedIncrementerFactory()
	{
	}


	/**
	 *
	 */
	public static JRDistinctCountExtendedIncrementerFactory getInstance()
	{
		return mainInstance;
	}


	@Override
	public JRExtendedIncrementer getExtendedIncrementer(CalculationEnum calculation)
	{
		return new JRDistinctCountExtendedIncrementer();
	}


	public static JRExtendedIncrementerFactory getFactory (Class<?> valueClass)
	{
		return JRDistinctCountExtendedIncrementerFactory.getInstance();
	}
}


/**
 *
 */
class JRDistinctCountExtendedIncrementer extends JRAbstractExtendedIncrementer
{

	private DistinctCountHolder lastHolder = new DistinctCountHolder();

	
	/**
	 *
	 */
	public JRDistinctCountExtendedIncrementer()
	{
	}


	@Override
	public Object increment(
		JRCalculable variable, 
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
		
		holder.addLastValue();

		return new DistinctCountHolder(holder, expressionValue);
	}


	@Override
	public Object combine(JRCalculable calculable1, JRCalculable calculable2, AbstractValueProvider valueProvider)
	{
		Set<Object> distinctValues = new HashSet<>();
		
		DistinctCountHolder holder1  = (DistinctCountHolder)calculable1.getValue();
		if (holder1 != null)
		{
			distinctValues.addAll(holder1.getDistinctValues());
			if (holder1.getLastValue() != null)
			{
				distinctValues.add(holder1.getLastValue());
			}
		}
		
		DistinctCountHolder holder2  = (DistinctCountHolder)calculable2.getValue();
		if (holder2 != null)
		{
			distinctValues.addAll(holder2.getDistinctValues());
			if (holder2.getLastValue() != null)
			{
				distinctValues.add(holder2.getLastValue());
			}
		}
		
		return new DistinctCountHolder(distinctValues);
	}


	@Override
	public Object initialValue()
	{
		return null;
	}


}

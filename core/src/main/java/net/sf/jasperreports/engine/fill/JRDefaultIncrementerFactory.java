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

import net.sf.jasperreports.engine.type.CalculationEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class JRDefaultIncrementerFactory extends JRAbstractExtendedIncrementerFactory
{


	/**
	 *
	 */
	private static JRDefaultIncrementerFactory mainInstance = new JRDefaultIncrementerFactory();


	/**
	 *
	 */
	private JRDefaultIncrementerFactory()
	{
	}


	/**
	 *
	 */
	public static JRDefaultIncrementerFactory getInstance()
	{
		return mainInstance;
	}


	@Override
	public JRExtendedIncrementer getExtendedIncrementer(CalculationEnum calculation)
	{
		JRExtendedIncrementer incrementer = null;

		switch (calculation)
		{
			case SYSTEM :
			{
				incrementer = JRDefaultSystemIncrementer.getInstance();
				break;
			}
			case FIRST :
			{
				incrementer = JRDefaultFirstIncrementer.getInstance();
				break;
			}
			case NOTHING :
			case COUNT :
			case SUM :
			case AVERAGE :
			case LOWEST :
			case HIGHEST :
			case STANDARD_DEVIATION :
			case VARIANCE :
			case DISTINCT_COUNT :
			default :
			{
				incrementer = JRDefaultNothingIncrementer.getInstance();
				break;
			}
		}
		
		return incrementer;
	}


	public static JRExtendedIncrementerFactory getFactory (Class<?> valueClass)
	{
		JRExtendedIncrementerFactory factory;
		
		if (java.math.BigDecimal.class.equals(valueClass))
		{
			factory = JRBigDecimalIncrementerFactory.getInstance();
		}
		else if (
			java.lang.Number.class.equals(valueClass)
			|| java.lang.Double.class.equals(valueClass)
			)
		{
			factory = JRDoubleIncrementerFactory.getInstance();
		}
		else if (java.lang.Float.class.equals(valueClass))
		{
			factory = JRFloatIncrementerFactory.getInstance();
		}
		else if (java.lang.Long.class.equals(valueClass))
		{
			factory = JRLongIncrementerFactory.getInstance();
		}
		else if (java.lang.Integer.class.equals(valueClass))
		{
			factory = JRIntegerIncrementerFactory.getInstance();
		}
		else if (java.lang.Short.class.equals(valueClass))
		{
			factory = JRShortIncrementerFactory.getInstance();
		}
		else if (java.lang.Byte.class.equals(valueClass))
		{
			factory = JRByteIncrementerFactory.getInstance();
		}
		else if (java.lang.Comparable.class.isAssignableFrom(valueClass))
		{
			factory = JRComparableIncrementerFactory.getInstance();
		}
		else
		{
			factory = JRDefaultIncrementerFactory.getInstance();
		}
		
		return factory;
	}
}


/**
 *
 */
final class JRDefaultNothingIncrementer extends JRAbstractExtendedIncrementer
{


	/**
	 *
	 */
	private static JRDefaultNothingIncrementer mainInstance = new JRDefaultNothingIncrementer();


	/**
	 *
	 */
	private JRDefaultNothingIncrementer()
	{
	}


	/**
	 *
	 */
	public static JRDefaultNothingIncrementer getInstance()
	{
		return mainInstance;
	}


	@Override
	public Object increment(
		JRCalculable variable, 
		Object expressionValue,
		AbstractValueProvider valueProvider
		)
	{
		return expressionValue;
	}


	@Override
	public Object combine(JRCalculable calculable, JRCalculable calculableValue, AbstractValueProvider valueProvider)
	{
		if (!calculableValue.isInitialized())
		{
			return calculableValue.getValue();
		}
		
		if (!calculable.isInitialized())
		{
			return calculable.getValue();
		}
		
		return null;
	}

	@Override
	public Object initialValue()
	{
		return null;
	}

	@Override
	public boolean ignoresNullValues()
	{
		return false;
	}
	
}


/**
 *
 */
final class JRDefaultSystemIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRDefaultSystemIncrementer mainInstance = new JRDefaultSystemIncrementer();

	/**
	 *
	 */
	private JRDefaultSystemIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRDefaultSystemIncrementer getInstance()
	{
		return mainInstance;
	}

	@Override
	public Object increment(
		JRCalculable variable, 
		Object expressionValue,
		AbstractValueProvider valueProvider
		)
	{
		return variable.getValue();
	}

	@Override
	public Object combine(JRCalculable calculable, JRCalculable calculableValue, AbstractValueProvider valueProvider)
	{
		return calculable.getValue();
	}
	
	@Override
	public Object initialValue()
	{
		return null;
	}
}

final class JRDefaultFirstIncrementer extends JRAbstractExtendedIncrementer
{
	private static final JRDefaultFirstIncrementer instance = new JRDefaultFirstIncrementer();

	private JRDefaultFirstIncrementer()
	{
	}
	
	public static JRDefaultFirstIncrementer getInstance()
	{
		return instance;
	}
	
	@Override
	public Object initialValue()
	{
		return null;
	}

	@Override
	public Object combine(JRCalculable calculable, JRCalculable calculableValue, AbstractValueProvider valueProvider)
	{
		if (!calculable.isInitialized())
		{
			return calculable.getValue();
		}
		
		if (!calculableValue.isInitialized())
		{
			return calculableValue.getValue();
		}
		
		return null;
	}

	@Override
	public Object increment(JRCalculable calculable, Object expressionValue, AbstractValueProvider valueProvider)
	{
		if (calculable.isInitialized())
		{
			return expressionValue;
		}

		return calculable.getIncrementedValue();
	}

	@Override
	public boolean ignoresNullValues()
	{
		return false;
	}
}

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
public final class JRShortIncrementerFactory extends JRAbstractExtendedIncrementerFactory
{


	/**
	 *
	 */
	protected static final Short ZERO = 0;


	/**
	 *
	 */
	private static JRShortIncrementerFactory mainInstance = new JRShortIncrementerFactory();


	/**
	 *
	 */
	private JRShortIncrementerFactory()
	{
	}


	/**
	 *
	 */
	public static JRShortIncrementerFactory getInstance()
	{
		return mainInstance;
	}


	@Override
	public JRExtendedIncrementer getExtendedIncrementer(CalculationEnum calculation)
	{
		JRExtendedIncrementer incrementer = null;

		switch (calculation)
		{
			case COUNT :
			{
				incrementer = JRShortCountIncrementer.getInstance();
				break;
			}
			case SUM :
			{
				incrementer = JRShortSumIncrementer.getInstance();
				break;
			}
			case AVERAGE :
			{
				incrementer = JRShortAverageIncrementer.getInstance();
				break;
			}
			case LOWEST :
			case HIGHEST :
			{
				incrementer = JRComparableIncrementerFactory.getInstance().getExtendedIncrementer(calculation);
				break;
			}
			case STANDARD_DEVIATION :
			{
				incrementer = JRShortStandardDeviationIncrementer.getInstance();
				break;
			}
			case VARIANCE :
			{
				incrementer = JRShortVarianceIncrementer.getInstance();
				break;
			}
			case DISTINCT_COUNT :
			{
				incrementer = JRShortDistinctCountIncrementer.getInstance();
				break;
			}
			case SYSTEM :
			case NOTHING :
			case FIRST :
			default :
			{
				incrementer = JRDefaultIncrementerFactory.getInstance().getExtendedIncrementer(calculation);
				break;
			}
		}
		
		return incrementer;
	}


}


/**
 *
 */
final class JRShortCountIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRShortCountIncrementer mainInstance = new JRShortCountIncrementer();

	/**
	 *
	 */
	private JRShortCountIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRShortCountIncrementer getInstance()
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
		Number value = (Number)variable.getIncrementedValue();

		if (value == null || variable.isInitialized())
		{
			value = JRShortIncrementerFactory.ZERO;
		}

		if (expressionValue == null)
		{
			return value;
		}

		return (short)(value.shortValue() + 1);
	}

	
	@Override
	public Object combine(JRCalculable calculable, JRCalculable calculableValue, AbstractValueProvider valueProvider)
	{
		Number value = (Number)calculable.getIncrementedValue();
		Number combineValue = (Number) calculableValue.getValue();

		if (value == null || calculable.isInitialized())
		{
			value = JRShortIncrementerFactory.ZERO;
		}

		if (combineValue == null)
		{
			return value;
		}

		return (short) (value.shortValue() + combineValue.shortValue());
	}

	
	@Override
	public Object initialValue()
	{
		return JRShortIncrementerFactory.ZERO;
	}
}


/**
 *
 */
final class JRShortDistinctCountIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRShortDistinctCountIncrementer mainInstance = new JRShortDistinctCountIncrementer();

	/**
	 *
	 */
	private JRShortDistinctCountIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRShortDistinctCountIncrementer getInstance()
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
		DistinctCountHolder holder = 
			(DistinctCountHolder)valueProvider.getValue(variable.getHelperVariable(JRCalculable.HELPER_COUNT));
		
		if (variable.isInitialized())
		{
			holder.init();
		}

		return (short)holder.getCount();
	}

	@Override
	public Object combine(JRCalculable calculable, JRCalculable calculableValue, AbstractValueProvider valueProvider)
	{
		DistinctCountHolder holder = 
			(DistinctCountHolder)valueProvider.getValue(calculable.getHelperVariable(JRCalculable.HELPER_COUNT));
		
		return (short)holder.getCount();
	}
	
	@Override
	public Object initialValue()
	{
		return JRShortIncrementerFactory.ZERO;
	}
}


/**
 *
 */
final class JRShortSumIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRShortSumIncrementer mainInstance = new JRShortSumIncrementer();

	/**
	 *
	 */
	private JRShortSumIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRShortSumIncrementer getInstance()
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
		Number value = (Number)variable.getIncrementedValue();
		Number newValue = (Number)expressionValue;

		if (newValue == null)
		{
			if (variable.isInitialized())
			{
				return null;
			}

			return value;
		}

		if (value == null || variable.isInitialized())
		{
			value = JRShortIncrementerFactory.ZERO;
		}

		return (short)(value.shortValue() + newValue.shortValue());
	}

	
	@Override
	public Object initialValue()
	{
		return JRShortIncrementerFactory.ZERO;
	}
}


/**
 *
 */
final class JRShortAverageIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRShortAverageIncrementer mainInstance = new JRShortAverageIncrementer();

	/**
	 *
	 */
	private JRShortAverageIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRShortAverageIncrementer getInstance()
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
		if (expressionValue == null)
		{
			if (variable.isInitialized())
			{
				return null;
			}
			return variable.getValue();
		}
		Number countValue = (Number)valueProvider.getValue(variable.getHelperVariable(JRCalculable.HELPER_COUNT));
		Number sumValue = (Number)valueProvider.getValue(variable.getHelperVariable(JRCalculable.HELPER_SUM));
		return (short)(sumValue.shortValue() / countValue.shortValue());
	}

	
	@Override
	public Object initialValue()
	{
		return JRShortIncrementerFactory.ZERO;
	}
}


/**
 *
 */
final class JRShortStandardDeviationIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRShortStandardDeviationIncrementer mainInstance = new JRShortStandardDeviationIncrementer();

	/**
	 *
	 */
	private JRShortStandardDeviationIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRShortStandardDeviationIncrementer getInstance()
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
		if (expressionValue == null)
		{
			if (variable.isInitialized())
			{
				return null;
			}
			return variable.getValue(); 
		}
		Number varianceValue = (Number)valueProvider.getValue(variable.getHelperVariable(JRCalculable.HELPER_VARIANCE));
		return (short)Math.sqrt(varianceValue.doubleValue());
	}

	
	@Override
	public Object initialValue()
	{
		return JRShortIncrementerFactory.ZERO;
	}
}


/**
 *
 */
final class JRShortVarianceIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRShortVarianceIncrementer mainInstance = new JRShortVarianceIncrementer();

	/**
	 *
	 */
	private JRShortVarianceIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRShortVarianceIncrementer getInstance()
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
		Number value = (Number)variable.getIncrementedValue();
		Number newValue = (Number)expressionValue;
		
		if (newValue == null)
		{
			if (variable.isInitialized())
			{
				return null;
			}
			return value;
		}
		else if (value == null || variable.isInitialized())
		{
			return JRShortIncrementerFactory.ZERO;
		}
		else
		{
			Number countValue = (Number)valueProvider.getValue(variable.getHelperVariable(JRCalculable.HELPER_COUNT));
			Number sumValue = (Number)valueProvider.getValue(variable.getHelperVariable(JRCalculable.HELPER_SUM));
			return
				(short)(
					(countValue.shortValue() - 1) * value.shortValue() / countValue.shortValue() +
					( sumValue.shortValue() / countValue.shortValue() - newValue.shortValue() ) *
					( sumValue.shortValue() / countValue.shortValue() - newValue.shortValue() ) /
					(countValue.shortValue() - 1)
					);
		}
	}

	@Override
	public Object combine(JRCalculable calculable, JRCalculable calculableValue, AbstractValueProvider valueProvider)
	{
		Number value = (Number)calculable.getIncrementedValue();
		
		if (calculableValue.getValue() == null)
		{
			if (calculable.isInitialized())
			{
				return null;
			}

			return value;
		}
		else if (value == null || calculable.isInitialized())
		{
			return ((Number) calculableValue.getIncrementedValue()).shortValue();
		}

		float v1 = value.floatValue();
		float c1 = ((Number) valueProvider.getValue(calculable.getHelperVariable(JRCalculable.HELPER_COUNT))).floatValue();
		float s1 = ((Number) valueProvider.getValue(calculable.getHelperVariable(JRCalculable.HELPER_SUM))).floatValue();

		float v2 = ((Number) calculableValue.getIncrementedValue()).floatValue();
		float c2 = ((Number) valueProvider.getValue(calculableValue.getHelperVariable(JRCalculable.HELPER_COUNT))).floatValue();
		float s2 = ((Number) valueProvider.getValue(calculableValue.getHelperVariable(JRCalculable.HELPER_SUM))).floatValue();

		c1 -= c2;
		s1 -= s2;
		
		float c = c1 + c2;

		return
			(short) (
				c1 / c * v1 +
				c2 / c * v2 +
				c2 / c1 * s1 / c * s1 / c +
				c1 / c2 * s2 / c * s2 / c -
				2 * s1 / c * s2 /c
				);
	}

	
	@Override
	public Object initialValue()
	{
		return JRShortIncrementerFactory.ZERO;
	}
}

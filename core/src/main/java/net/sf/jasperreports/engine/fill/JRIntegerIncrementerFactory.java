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
public final class JRIntegerIncrementerFactory extends JRAbstractExtendedIncrementerFactory
{


	/**
	 *
	 */
	protected static final Integer ZERO = 0;


	/**
	 *
	 */
	private static JRIntegerIncrementerFactory mainInstance = new JRIntegerIncrementerFactory();


	/**
	 *
	 */
	private JRIntegerIncrementerFactory()
	{
	}


	/**
	 *
	 */
	public static JRIntegerIncrementerFactory getInstance()
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
				incrementer = JRIntegerCountIncrementer.getInstance();
				break;
			}
			case SUM :
			{
				incrementer = JRIntegerSumIncrementer.getInstance();
				break;
			}
			case AVERAGE :
			{
				incrementer = JRIntegerAverageIncrementer.getInstance();
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
				incrementer = JRIntegerStandardDeviationIncrementer.getInstance();
				break;
			}
			case VARIANCE :
			{
				incrementer = JRIntegerVarianceIncrementer.getInstance();
				break;
			}
			case DISTINCT_COUNT :
			{
				incrementer = JRIntegerDistinctCountIncrementer.getInstance();
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
final class JRIntegerCountIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRIntegerCountIncrementer mainInstance = new JRIntegerCountIncrementer();

	/**
	 *
	 */
	private JRIntegerCountIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRIntegerCountIncrementer getInstance()
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
			value = JRIntegerIncrementerFactory.ZERO;
		}

		if (expressionValue == null)
		{
			return value;
		}

		return value.intValue() + 1;
	}

	
	@Override
	public Object combine(JRCalculable calculable, JRCalculable calculableValue, AbstractValueProvider valueProvider)
	{
		Number value = (Number)calculable.getIncrementedValue();
		Number combineValue = (Number) calculableValue.getValue();

		if (value == null || calculable.isInitialized())
		{
			value = JRIntegerIncrementerFactory.ZERO;
		}

		if (combineValue == null)
		{
			return value;
		}

		return value.intValue() + combineValue.intValue();
	}

	
	@Override
	public Object initialValue()
	{
		return JRIntegerIncrementerFactory.ZERO;
	}
}


/**
 *
 */
final class JRIntegerDistinctCountIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRIntegerDistinctCountIncrementer mainInstance = new JRIntegerDistinctCountIncrementer();

	/**
	 *
	 */
	private JRIntegerDistinctCountIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRIntegerDistinctCountIncrementer getInstance()
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

		return (int)holder.getCount();
	}

	@Override
	public Object combine(JRCalculable calculable, JRCalculable calculableValue, AbstractValueProvider valueProvider)
	{
		DistinctCountHolder holder = 
			(DistinctCountHolder)valueProvider.getValue(calculable.getHelperVariable(JRCalculable.HELPER_COUNT));
		
		return (int)holder.getCount();
	}
	
	@Override
	public Object initialValue()
	{
		return JRIntegerIncrementerFactory.ZERO;
	}
}


/**
 *
 */
final class JRIntegerSumIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRIntegerSumIncrementer mainInstance = new JRIntegerSumIncrementer();

	/**
	 *
	 */
	private JRIntegerSumIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRIntegerSumIncrementer getInstance()
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
			value = JRIntegerIncrementerFactory.ZERO;
		}

		return value.intValue() + newValue.intValue();
	}

	
	@Override
	public Object initialValue()
	{
		return JRIntegerIncrementerFactory.ZERO;
	}
}


/**
 *
 */
final class JRIntegerAverageIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRIntegerAverageIncrementer mainInstance = new JRIntegerAverageIncrementer();

	/**
	 *
	 */
	private JRIntegerAverageIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRIntegerAverageIncrementer getInstance()
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
		return sumValue.intValue() / countValue.intValue();
	}

	
	@Override
	public Object initialValue()
	{
		return JRIntegerIncrementerFactory.ZERO;
	}
}


/**
 *
 */
final class JRIntegerStandardDeviationIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRIntegerStandardDeviationIncrementer mainInstance = new JRIntegerStandardDeviationIncrementer();

	/**
	 *
	 */
	private JRIntegerStandardDeviationIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRIntegerStandardDeviationIncrementer getInstance()
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
		return (int)Math.sqrt(varianceValue.doubleValue());
	}

	
	@Override
	public Object initialValue()
	{
		return JRIntegerIncrementerFactory.ZERO;
	}
}


/**
 *
 */
final class JRIntegerVarianceIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRIntegerVarianceIncrementer mainInstance = new JRIntegerVarianceIncrementer();

	/**
	 *
	 */
	private JRIntegerVarianceIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRIntegerVarianceIncrementer getInstance()
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
			return JRIntegerIncrementerFactory.ZERO;
		}
		else
		{
			Number countValue = (Number)valueProvider.getValue(variable.getHelperVariable(JRCalculable.HELPER_COUNT));
			Number sumValue = (Number)valueProvider.getValue(variable.getHelperVariable(JRCalculable.HELPER_SUM));
			return
					(countValue.intValue() - 1) * value.intValue() / countValue.intValue() +
					( sumValue.intValue() / countValue.intValue() - newValue.intValue() ) *
					( sumValue.intValue() / countValue.intValue() - newValue.intValue() ) /
					(countValue.intValue() - 1);
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
			return ((Number) calculableValue.getIncrementedValue()).intValue();
		}

		double v1 = value.doubleValue();
		double c1 = ((Number) valueProvider.getValue(calculable.getHelperVariable(JRCalculable.HELPER_COUNT))).doubleValue();
		double s1 = ((Number) valueProvider.getValue(calculable.getHelperVariable(JRCalculable.HELPER_SUM))).doubleValue();

		double v2 = ((Number) calculableValue.getIncrementedValue()).doubleValue();
		double c2 = ((Number) valueProvider.getValue(calculableValue.getHelperVariable(JRCalculable.HELPER_COUNT))).doubleValue();
		double s2 = ((Number) valueProvider.getValue(calculableValue.getHelperVariable(JRCalculable.HELPER_SUM))).doubleValue();

		c1 -= c2;
		s1 -= s2;
		
		double c = c1 + c2;

		return
			(int) (
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
		return JRIntegerIncrementerFactory.ZERO;
	}
}

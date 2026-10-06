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
public final class JRDoubleIncrementerFactory extends JRAbstractExtendedIncrementerFactory
{


	/**
	 *
	 */
	protected static final Double ZERO = 0d;


	/**
	 *
	 */
	private static JRDoubleIncrementerFactory mainInstance = new JRDoubleIncrementerFactory();


	/**
	 *
	 */
	private JRDoubleIncrementerFactory()
	{
	}


	/**
	 *
	 */
	public static JRDoubleIncrementerFactory getInstance()
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
				incrementer = JRDoubleCountIncrementer.getInstance();
				break;
			}
			case SUM :
			{
				incrementer = JRDoubleSumIncrementer.getInstance();
				break;
			}
			case AVERAGE :
			{
				incrementer = JRDoubleAverageIncrementer.getInstance();
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
				incrementer = JRDoubleStandardDeviationIncrementer.getInstance();
				break;
			}
			case VARIANCE :
			{
				incrementer = JRDoubleVarianceIncrementer.getInstance();
				break;
			}
			case DISTINCT_COUNT :
			{
				incrementer = JRDoubleDistinctCountIncrementer.getInstance();
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
final class JRDoubleCountIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRDoubleCountIncrementer mainInstance = new JRDoubleCountIncrementer();

	/**
	 *
	 */
	private JRDoubleCountIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRDoubleCountIncrementer getInstance()
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
			value = JRDoubleIncrementerFactory.ZERO;
		}

		if (expressionValue == null)
		{
			return value;
		}

		return value.doubleValue() + 1;
	}

	
	@Override
	public Object combine(JRCalculable calculable, JRCalculable calculableValue, AbstractValueProvider valueProvider)
	{
		Number value = (Number)calculable.getIncrementedValue();
		Number combineValue = (Number) calculableValue.getValue();

		if (value == null || calculable.isInitialized())
		{
			value = JRDoubleIncrementerFactory.ZERO;
		}

		if (combineValue == null)
		{
			return value;
		}

		return value.doubleValue() + combineValue.doubleValue();
	}

	
	@Override
	public Object initialValue()
	{
		return JRDoubleIncrementerFactory.ZERO;
	}
}


/**
 *
 */
final class JRDoubleDistinctCountIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRDoubleDistinctCountIncrementer mainInstance = new JRDoubleDistinctCountIncrementer();

	/**
	 *
	 */
	private JRDoubleDistinctCountIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRDoubleDistinctCountIncrementer getInstance()
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

		return (double)holder.getCount();
	}

	@Override
	public Object combine(JRCalculable calculable, JRCalculable calculableValue, AbstractValueProvider valueProvider)
	{
		DistinctCountHolder holder = 
			(DistinctCountHolder)valueProvider.getValue(calculable.getHelperVariable(JRCalculable.HELPER_COUNT));
		
		return (double)holder.getCount();
	}
	
	@Override
	public Object initialValue()
	{
		return JRDoubleIncrementerFactory.ZERO;
	}
}


/**
 *
 */
final class JRDoubleSumIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRDoubleSumIncrementer mainInstance = new JRDoubleSumIncrementer();

	/**
	 *
	 */
	private JRDoubleSumIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRDoubleSumIncrementer getInstance()
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
			value = JRDoubleIncrementerFactory.ZERO;
		}

		return value.doubleValue() + newValue.doubleValue();
	}

	
	@Override
	public Object initialValue()
	{
		return JRDoubleIncrementerFactory.ZERO;
	}
}


/**
 *
 */
final class JRDoubleAverageIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRDoubleAverageIncrementer mainInstance = new JRDoubleAverageIncrementer();

	/**
	 *
	 */
	private JRDoubleAverageIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRDoubleAverageIncrementer getInstance()
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
		return sumValue.doubleValue() / countValue.doubleValue();
	}

	
	@Override
	public Object initialValue()
	{
		return JRDoubleIncrementerFactory.ZERO;
	}
}


/**
 *
 */
final class JRDoubleStandardDeviationIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRDoubleStandardDeviationIncrementer mainInstance = new JRDoubleStandardDeviationIncrementer();

	/**
	 *
	 */
	private JRDoubleStandardDeviationIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRDoubleStandardDeviationIncrementer getInstance()
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
		return Math.sqrt(varianceValue.doubleValue());
	}

	
	@Override
	public Object initialValue()
	{
		return JRDoubleIncrementerFactory.ZERO;
	}
}


/**
 *
 */
final class JRDoubleVarianceIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRDoubleVarianceIncrementer mainInstance = new JRDoubleVarianceIncrementer();

	/**
	 *
	 */
	private JRDoubleVarianceIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRDoubleVarianceIncrementer getInstance()
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
			return JRDoubleIncrementerFactory.ZERO;
		}
		else
		{
			Number countValue = (Number)valueProvider.getValue(variable.getHelperVariable(JRCalculable.HELPER_COUNT));
			Number sumValue = (Number)valueProvider.getValue(variable.getHelperVariable(JRCalculable.HELPER_SUM));
			return
					(countValue.doubleValue() - 1) * value.doubleValue() / countValue.doubleValue() +
					( sumValue.doubleValue() / countValue.doubleValue() - newValue.doubleValue() ) *
					( sumValue.doubleValue() / countValue.doubleValue() - newValue.doubleValue() ) /
					(countValue.doubleValue() - 1);
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
			return ((Number) calculableValue.getIncrementedValue()).doubleValue();
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
				c1 / c * v1 +
				c2 / c * v2 +
				c2 / c1 * s1 / c * s1 / c +
				c1 / c2 * s2 / c * s2 / c -
				2 * s1 / c * s2 /c;
	}

	
	@Override
	public Object initialValue()
	{
		return JRDoubleIncrementerFactory.ZERO;
	}
}

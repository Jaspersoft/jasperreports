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
public final class JRLongIncrementerFactory extends JRAbstractExtendedIncrementerFactory
{


	/**
	 *
	 */
	protected static final Long ZERO = 0L;


	/**
	 *
	 */
	private static JRLongIncrementerFactory mainInstance = new JRLongIncrementerFactory();


	/**
	 *
	 */
	private JRLongIncrementerFactory()
	{
	}


	/**
	 *
	 */
	public static JRLongIncrementerFactory getInstance()
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
				incrementer = JRLongCountIncrementer.getInstance();
				break;
			}
			case SUM :
			{
				incrementer = JRLongSumIncrementer.getInstance();
				break;
			}
			case AVERAGE :
			{
				incrementer = JRLongAverageIncrementer.getInstance();
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
				incrementer = JRLongStandardDeviationIncrementer.getInstance();
				break;
			}
			case VARIANCE :
			{
				incrementer = JRLongVarianceIncrementer.getInstance();
				break;
			}
			case DISTINCT_COUNT :
			{
				incrementer = JRLongDistinctCountIncrementer.getInstance();
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
final class JRLongCountIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRLongCountIncrementer mainInstance = new JRLongCountIncrementer();

	/**
	 *
	 */
	private JRLongCountIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRLongCountIncrementer getInstance()
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
			value = JRLongIncrementerFactory.ZERO;
		}

		if (expressionValue == null)
		{
			return value;
		}

		return value.longValue() + 1;
	}

	
	@Override
	public Object combine(JRCalculable calculable, JRCalculable calculableValue, AbstractValueProvider valueProvider)
	{
		Number value = (Number)calculable.getIncrementedValue();
		Number combineValue = (Number) calculableValue.getValue();

		if (value == null || calculable.isInitialized())
		{
			value = JRLongIncrementerFactory.ZERO;
		}

		if (combineValue == null)
		{
			return value;
		}

		return value.longValue() + combineValue.longValue();
	}

	
	@Override
	public Object initialValue()
	{
		return JRLongIncrementerFactory.ZERO;
	}
}


/**
 *
 */
final class JRLongDistinctCountIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRLongDistinctCountIncrementer mainInstance = new JRLongDistinctCountIncrementer();

	/**
	 *
	 */
	private JRLongDistinctCountIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRLongDistinctCountIncrementer getInstance()
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

		return holder.getCount();
	}

	@Override
	public Object combine(JRCalculable calculable, JRCalculable calculableValue, AbstractValueProvider valueProvider)
	{
		DistinctCountHolder holder = 
			(DistinctCountHolder)valueProvider.getValue(calculable.getHelperVariable(JRCalculable.HELPER_COUNT));
		
		return holder.getCount();
	}
	
	@Override
	public Object initialValue()
	{
		return JRLongIncrementerFactory.ZERO;
	}
}


/**
 *
 */
final class JRLongSumIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRLongSumIncrementer mainInstance = new JRLongSumIncrementer();

	/**
	 *
	 */
	private JRLongSumIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRLongSumIncrementer getInstance()
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
			value = JRLongIncrementerFactory.ZERO;
		}

		return value.longValue() + newValue.longValue();
	}

	
	@Override
	public Object initialValue()
	{
		return JRLongIncrementerFactory.ZERO;
	}
}


/**
 *
 */
final class JRLongAverageIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRLongAverageIncrementer mainInstance = new JRLongAverageIncrementer();

	/**
	 *
	 */
	private JRLongAverageIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRLongAverageIncrementer getInstance()
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
		return sumValue.longValue() / countValue.longValue();
	}

	
	@Override
	public Object initialValue()
	{
		return JRLongIncrementerFactory.ZERO;
	}
}


/**
 *
 */
final class JRLongStandardDeviationIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRLongStandardDeviationIncrementer mainInstance = new JRLongStandardDeviationIncrementer();

	/**
	 *
	 */
	private JRLongStandardDeviationIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRLongStandardDeviationIncrementer getInstance()
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
		return (long)Math.sqrt(varianceValue.doubleValue());
	}

	
	@Override
	public Object initialValue()
	{
		return JRLongIncrementerFactory.ZERO;
	}
}


/**
 *
 */
final class JRLongVarianceIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRLongVarianceIncrementer mainInstance = new JRLongVarianceIncrementer();

	/**
	 *
	 */
	private JRLongVarianceIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRLongVarianceIncrementer getInstance()
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
			return JRLongIncrementerFactory.ZERO;
		}
		else
		{
			Number countValue = (Number)valueProvider.getValue(variable.getHelperVariable(JRCalculable.HELPER_COUNT));
			Number sumValue = (Number)valueProvider.getValue(variable.getHelperVariable(JRCalculable.HELPER_SUM));
			return
					(countValue.longValue() - 1) * value.longValue() / countValue.longValue() +
					( sumValue.longValue() / countValue.longValue() - newValue.longValue() ) *
					( sumValue.longValue() / countValue.longValue() - newValue.longValue() ) /
					(countValue.longValue() - 1);
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
			return ((Number) calculableValue.getIncrementedValue()).longValue();
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
			(long) (
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
		return JRLongIncrementerFactory.ZERO;
	}
}

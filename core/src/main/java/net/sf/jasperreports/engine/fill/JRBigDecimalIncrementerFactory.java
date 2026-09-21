/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2025 Cloud Software Group, Inc. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
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

import java.math.BigDecimal;
import java.math.BigInteger;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.type.CalculationEnum;
import net.sf.jasperreports.engine.util.BigDecimalUtils;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class JRBigDecimalIncrementerFactory extends JRAbstractExtendedIncrementerFactory
{
	private static final String EXCEPTION_MESSAGE_KEY_VALUE_NOT_SUPPORTED = "fill.big.decimal.value.not.supported";

	/**
	 *
	 */
	protected static final BigDecimal ZERO = BigDecimal.ZERO;
	protected static final BigDecimal ONE = BigDecimal.ONE;


	/**
	 *
	 */
	private static JRBigDecimalIncrementerFactory mainInstance = new JRBigDecimalIncrementerFactory();


	/**
	 *
	 */
	private JRBigDecimalIncrementerFactory()
	{
	}


	/**
	 *
	 */
	public static JRBigDecimalIncrementerFactory getInstance()
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
				incrementer = JRBigDecimalCountIncrementer.getInstance();
				break;
			}
			case SUM :
			{
				incrementer = JRBigDecimalSumIncrementer.getInstance();
				break;
			}
			case AVERAGE :
			{
				incrementer = JRBigDecimalAverageIncrementer.getInstance();
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
				incrementer = JRBigDecimalStandardDeviationIncrementer.getInstance();
				break;
			}
			case VARIANCE :
			{
				incrementer = JRBigDecimalVarianceIncrementer.getInstance();
				break;
			}
			case DISTINCT_COUNT :
			{
				incrementer = JRBigDecimalDistinctCountIncrementer.getInstance();
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

	protected static BigDecimal toBigDecimal(Object value)
	{
		if (value == null)
		{
			return null;
		}
		
		BigDecimal bigDecimal;
		if (value instanceof BigDecimal)
		{
			bigDecimal = (BigDecimal) value;
		}
		else if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long)
		{
			bigDecimal = BigDecimal.valueOf(((Number) value).longValue());
		}
		else if (value instanceof BigInteger)
		{
			bigDecimal = new BigDecimal((BigInteger) value);
		}
		else if (value instanceof Number)//this includes Double and Float
		{
			bigDecimal = BigDecimal.valueOf(((Number) value).doubleValue());
		}
		else
		{
			// assuming a number for now, not converting strings
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_VALUE_NOT_SUPPORTED,
					new Object[]{value, value.getClass().getName()});
		}
		return bigDecimal;
	}
}


/**
 *
 */
final class JRBigDecimalCountIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRBigDecimalCountIncrementer mainInstance = new JRBigDecimalCountIncrementer();

	/**
	 *
	 */
	private JRBigDecimalCountIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRBigDecimalCountIncrementer getInstance()
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
		BigDecimal value = (BigDecimal)variable.getIncrementedValue();

		if (value == null || variable.isInitialized())
		{
			value = JRBigDecimalIncrementerFactory.ZERO;
		}

		if (expressionValue == null)
		{
			return value;
		}

		return value.add(JRBigDecimalIncrementerFactory.ONE);
	}

	
	@Override
	public Object combine(JRCalculable calculable, JRCalculable calculableValue, AbstractValueProvider valueProvider)
	{
		BigDecimal value = (BigDecimal)calculable.getIncrementedValue();
		BigDecimal combineValue = (BigDecimal) calculableValue.getValue();

		if (value == null || calculable.isInitialized())
		{
			value = JRBigDecimalIncrementerFactory.ZERO;
		}

		if (combineValue == null)
		{
			return value;
		}

		return value.add(combineValue);
	}

	
	@Override
	public Object initialValue()
	{
		return JRBigDecimalIncrementerFactory.ZERO;
	}
}


/**
*
*/
final class JRBigDecimalDistinctCountIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRBigDecimalDistinctCountIncrementer mainInstance = new JRBigDecimalDistinctCountIncrementer();

	/**
	 *
	 */
	private JRBigDecimalDistinctCountIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRBigDecimalDistinctCountIncrementer getInstance()
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

		return new BigDecimal(holder.getCount());
	}

	@Override
	public Object combine(JRCalculable calculable, JRCalculable calculableValue, AbstractValueProvider valueProvider)
	{
		DistinctCountHolder holder = 
			(DistinctCountHolder)valueProvider.getValue(calculable.getHelperVariable(JRCalculable.HELPER_COUNT));
		
		return new BigDecimal(holder.getCount());
	}
	
	@Override
	public Object initialValue()
	{
		return JRBigDecimalIncrementerFactory.ZERO;
	}
}


/**
 *
 */
final class JRBigDecimalSumIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRBigDecimalSumIncrementer mainInstance = new JRBigDecimalSumIncrementer();

	/**
	 *
	 */
	private JRBigDecimalSumIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRBigDecimalSumIncrementer getInstance()
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
		BigDecimal value = (BigDecimal)variable.getIncrementedValue();
		BigDecimal newValue = JRBigDecimalIncrementerFactory.toBigDecimal(expressionValue);

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
			value = JRBigDecimalIncrementerFactory.ZERO;
		}

		return value.add(newValue);
	}

	
	@Override
	public Object initialValue()
	{
		return JRBigDecimalIncrementerFactory.ZERO;
	}
}


/**
 *
 */
final class JRBigDecimalAverageIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRBigDecimalAverageIncrementer mainInstance = new JRBigDecimalAverageIncrementer();

	/**
	 *
	 */
	private JRBigDecimalAverageIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRBigDecimalAverageIncrementer getInstance()
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
		BigDecimal countValue = (BigDecimal)valueProvider.getValue(variable.getHelperVariable(JRCalculable.HELPER_COUNT));
		BigDecimal sumValue = (BigDecimal)valueProvider.getValue(variable.getHelperVariable(JRCalculable.HELPER_SUM));
		return BigDecimalUtils.divide(sumValue, countValue);
	}

	
	@Override
	public Object initialValue()
	{
		return JRBigDecimalIncrementerFactory.ZERO;
	}
}


/**
 *
 */
final class JRBigDecimalStandardDeviationIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRBigDecimalStandardDeviationIncrementer mainInstance = new JRBigDecimalStandardDeviationIncrementer();

	/**
	 *
	 */
	private JRBigDecimalStandardDeviationIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRBigDecimalStandardDeviationIncrementer getInstance()
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
		return new BigDecimal( Math.sqrt(varianceValue.doubleValue()) );
	}

	
	@Override
	public Object initialValue()
	{
		return JRBigDecimalIncrementerFactory.ZERO;
	}
}


/**
 *
 */
final class JRBigDecimalVarianceIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRBigDecimalVarianceIncrementer mainInstance = new JRBigDecimalVarianceIncrementer();

	/**
	 *
	 */
	private JRBigDecimalVarianceIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRBigDecimalVarianceIncrementer getInstance()
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
		BigDecimal value = (BigDecimal)variable.getIncrementedValue();
		BigDecimal newValue = JRBigDecimalIncrementerFactory.toBigDecimal(expressionValue);
		
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
			return JRBigDecimalIncrementerFactory.ZERO;
		}
		else
		{
			BigDecimal countValue = (BigDecimal)valueProvider.getValue(variable.getHelperVariable(JRCalculable.HELPER_COUNT));
			BigDecimal sumValue = (BigDecimal)valueProvider.getValue(variable.getHelperVariable(JRCalculable.HELPER_SUM));

			BigDecimal x1 = BigDecimalUtils.divide(
					countValue.subtract(JRBigDecimalIncrementerFactory.ONE).multiply(value), 
					countValue);
			BigDecimal avg = BigDecimalUtils.divide(sumValue, countValue);
			BigDecimal avg2 = avg.subtract(newValue);
			return x1.add(
					BigDecimalUtils.divide(avg2.multiply(avg2), 
							countValue.subtract(JRBigDecimalIncrementerFactory.ONE)));
		}
	}

	
	@Override
	public Object combine(JRCalculable calculable, JRCalculable calculableValue, AbstractValueProvider valueProvider)
	{
		BigDecimal value = (BigDecimal)calculable.getIncrementedValue();
		
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
			return calculableValue.getIncrementedValue();
		}

		BigDecimal v1 = value;
		BigDecimal c1 = (BigDecimal) valueProvider.getValue(calculable.getHelperVariable(JRCalculable.HELPER_COUNT));
		BigDecimal s1 = (BigDecimal) valueProvider.getValue(calculable.getHelperVariable(JRCalculable.HELPER_SUM));

		BigDecimal v2 = (BigDecimal) calculableValue.getIncrementedValue();
		BigDecimal c2 = (BigDecimal) valueProvider.getValue(calculableValue.getHelperVariable(JRCalculable.HELPER_COUNT));
		BigDecimal s2 = (BigDecimal) valueProvider.getValue(calculableValue.getHelperVariable(JRCalculable.HELPER_SUM));

		c1 = c1.subtract(c2);
		s1 = s1.subtract(s2);
		
		BigDecimal c = c1.add(c2);
		BigDecimal t1 = c1.multiply(c2).multiply(c).multiply(c1.multiply(v1).add(c2.multiply(v2)));
		BigDecimal t2 = c1.multiply(s2).subtract(c2.multiply(s1));
		BigDecimal t3 = c1.multiply(c2).multiply(c).multiply(c);
		return BigDecimalUtils.divide(t1.add(t2.multiply(t2)), t3);
	}

	
	@Override
	public Object initialValue()
	{
		return JRBigDecimalIncrementerFactory.ZERO;
	}
}

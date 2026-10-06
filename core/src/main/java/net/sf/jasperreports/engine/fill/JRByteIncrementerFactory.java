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
public final class JRByteIncrementerFactory extends JRAbstractExtendedIncrementerFactory
{


	/**
	 *
	 */
	protected static final Byte ZERO = 0;


	/**
	 *
	 */
	private static JRByteIncrementerFactory mainInstance = new JRByteIncrementerFactory();


	/**
	 *
	 */
	private JRByteIncrementerFactory()
	{
	}


	/**
	 *
	 */
	public static JRByteIncrementerFactory getInstance()
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
				incrementer = JRByteCountIncrementer.getInstance();
				break;
			}
			case SUM :
			{
				incrementer = JRByteSumIncrementer.getInstance();
				break;
			}
			case AVERAGE :
			{
				incrementer = JRByteAverageIncrementer.getInstance();
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
				incrementer = JRByteStandardDeviationIncrementer.getInstance();
				break;
			}
			case VARIANCE :
			{
				incrementer = JRByteVarianceIncrementer.getInstance();
				break;
			}
			case DISTINCT_COUNT :
			{
				incrementer = JRByteDistinctCountIncrementer.getInstance();
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
final class JRByteCountIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRByteCountIncrementer mainInstance = new JRByteCountIncrementer();

	/**
	 *
	 */
	private JRByteCountIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRByteCountIncrementer getInstance()
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
			value = JRByteIncrementerFactory.ZERO;
		}

		if (expressionValue == null)
		{
			return value;
		}

		return (byte)(value.byteValue() + 1);
	}

	
	@Override
	public Object combine(JRCalculable calculable, JRCalculable calculableValue, AbstractValueProvider valueProvider)
	{
		Number value = (Number)calculable.getIncrementedValue();
		Number combineValue = (Number) calculableValue.getValue();

		if (value == null || calculable.isInitialized())
		{
			value = JRByteIncrementerFactory.ZERO;
		}

		if (combineValue == null)
		{
			return value;
		}

		return (byte) (value.byteValue() + combineValue.byteValue());
	}

	
	@Override
	public Object initialValue()
	{
		return JRByteIncrementerFactory.ZERO;
	}
}


/**
 *
 */
final class JRByteDistinctCountIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRByteDistinctCountIncrementer mainInstance = new JRByteDistinctCountIncrementer();

	/**
	 *
	 */
	private JRByteDistinctCountIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRByteDistinctCountIncrementer getInstance()
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

		return (byte)holder.getCount();
	}

	@Override
	public Object combine(JRCalculable calculable, JRCalculable calculableValue, AbstractValueProvider valueProvider)
	{
		DistinctCountHolder holder = 
			(DistinctCountHolder)valueProvider.getValue(calculable.getHelperVariable(JRCalculable.HELPER_COUNT));
		
		return (byte)holder.getCount();
	}
	
	@Override
	public Object initialValue()
	{
		return JRByteIncrementerFactory.ZERO;
	}
}


/**
 *
 */
final class JRByteSumIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRByteSumIncrementer mainInstance = new JRByteSumIncrementer();

	/**
	 *
	 */
	private JRByteSumIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRByteSumIncrementer getInstance()
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
			value = JRByteIncrementerFactory.ZERO;
		}

		return (byte)(value.byteValue() + newValue.byteValue());
	}

	
	@Override
	public Object initialValue()
	{
		return JRByteIncrementerFactory.ZERO;
	}
}


/**
 *
 */
final class JRByteAverageIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRByteAverageIncrementer mainInstance = new JRByteAverageIncrementer();

	/**
	 *
	 */
	private JRByteAverageIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRByteAverageIncrementer getInstance()
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
		return (byte)(sumValue.byteValue() / countValue.byteValue());
	}

	
	@Override
	public Object initialValue()
	{
		return JRByteIncrementerFactory.ZERO;
	}
}


/**
 *
 */
final class JRByteStandardDeviationIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRByteStandardDeviationIncrementer mainInstance = new JRByteStandardDeviationIncrementer();

	/**
	 *
	 */
	private JRByteStandardDeviationIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRByteStandardDeviationIncrementer getInstance()
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
		return (byte)Math.sqrt(varianceValue.doubleValue());
	}

	
	@Override
	public Object initialValue()
	{
		return JRByteIncrementerFactory.ZERO;
	}
}


/**
 *
 */
final class JRByteVarianceIncrementer extends JRAbstractExtendedIncrementer
{
	/**
	 *
	 */
	private static JRByteVarianceIncrementer mainInstance = new JRByteVarianceIncrementer();

	/**
	 *
	 */
	private JRByteVarianceIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRByteVarianceIncrementer getInstance()
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
			return JRByteIncrementerFactory.ZERO;
		}
		else
		{
			Number countValue = (Number)valueProvider.getValue(variable.getHelperVariable(JRCalculable.HELPER_COUNT));
			Number sumValue = (Number)valueProvider.getValue(variable.getHelperVariable(JRCalculable.HELPER_SUM));
			return
				(byte)(
					(countValue.byteValue() - 1) * value.byteValue() / countValue.byteValue() +
					( sumValue.byteValue() / countValue.byteValue() - newValue.byteValue() ) *
					( sumValue.byteValue() / countValue.byteValue() - newValue.byteValue() ) /
					(countValue.byteValue() - 1)
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
			return ((Number) calculableValue.getIncrementedValue()).byteValue();
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
			(byte) (
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
		return JRByteIncrementerFactory.ZERO;
	}
}

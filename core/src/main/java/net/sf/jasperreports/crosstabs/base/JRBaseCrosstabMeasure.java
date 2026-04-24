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
package net.sf.jasperreports.crosstabs.base;

import java.io.Serializable;

import net.sf.jasperreports.crosstabs.JRCrosstabMeasure;
import net.sf.jasperreports.crosstabs.type.CrosstabPercentageEnum;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.type.CalculationEnum;
import net.sf.jasperreports.engine.util.JRClassLoader;
import net.sf.jasperreports.engine.util.JRCloneUtils;


/**
 * Base read-only crosstab measure implementation.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRBaseCrosstabMeasure implements JRCrosstabMeasure, Serializable, CrosstabBaseCloneable
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public static final String EXCEPTION_MESSAGE_KEY_MEASURE_INCREMENTER_CLASS_LOAD_ERROR = "crosstabs.measure.incrementer.class.load.error";
	public static final String EXCEPTION_MESSAGE_KEY_MEASURE_PERCENTAGE_CALCULATOR_CLASS_LOAD_ERROR = "crosstabs.measure.percentage.calculator.class.load.error";
	public static final String EXCEPTION_MESSAGE_KEY_MEASURE_VALUE_CLASS_LOAD_ERROR = "crosstabs.measure.value.class.load.error";
	
	protected String name;
	protected String valueClassName;
	protected String valueClassRealName;
	protected transient Class<?> valueClass;
	protected JRExpression expression;
	protected CalculationEnum calculation;
	protected String incrementerFactoryClassName;
	protected String incrementerFactoryClassRealName;
	protected Class<?> incrementerFactoryClass;
	protected CrosstabPercentageEnum percentageType;
	protected String percentageCalculatorClassName;
	protected String percentageCalculatorClassRealName;
	protected Class<?> percentageCalculatorClass;
	protected JRVariable variable;

	protected JRBaseCrosstabMeasure()
	{
	}
	
	public JRBaseCrosstabMeasure(JRCrosstabMeasure measure, JRBaseObjectFactory factory)
	{
		factory.put(measure, this);
		
		this.name = measure.getName();
		this.valueClassName = measure.getValueClassName();
		this.expression = factory.getExpression(measure.getValueExpression());
		this.calculation = measure.getCalculation();
		this.incrementerFactoryClassName = measure.getIncrementerFactoryClassName();
		this.percentageType = measure.getPercentageType();		
		this.percentageCalculatorClassName = measure.getPercentageCalculatorClassName();
		this.variable = factory.getVariable(measure.getVariable());
	}
	
	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getValueClassName()
	{
		return valueClassName;
	}

	@Override
	public JRExpression getValueExpression()
	{
		return expression;
	}

	@Override
	public CalculationEnum getCalculation()
	{
		return calculation;
	}

	@Override
	public String getIncrementerFactoryClassName()
	{
		return incrementerFactoryClassName;
	}

	@Override
	public CrosstabPercentageEnum getPercentageType()
	{
		return percentageType;
	}

	@Override
	public Class<?> getIncrementerFactoryClass()
	{
		if (incrementerFactoryClass == null)
		{
			String className = getIncrementerFactoryClassRealName();
			if (className != null)
			{
				try
				{
					incrementerFactoryClass = JRClassLoader.loadClassForName(className);
				}
				catch (ClassNotFoundException e)
				{
					throw 
						new JRRuntimeException(
							EXCEPTION_MESSAGE_KEY_MEASURE_INCREMENTER_CLASS_LOAD_ERROR,
							(Object[])null,
							e);
				}
			}
		}
		
		return incrementerFactoryClass;
	}

	/**
	 *
	 */
	private String getIncrementerFactoryClassRealName()
	{
		if (incrementerFactoryClassRealName == null)
		{
			incrementerFactoryClassRealName = JRClassLoader.getClassRealName(incrementerFactoryClassName);
		}
		
		return incrementerFactoryClassRealName;
	}

	@Override
	public Class<?> getValueClass()
	{
		if (valueClass == null)
		{
			String className = getValueClassRealName();
			if (className != null)
			{
				try
				{
					valueClass = JRClassLoader.loadClassForName(className);
				}
				catch (ClassNotFoundException e)
				{
					throw 
						new JRRuntimeException(
							EXCEPTION_MESSAGE_KEY_MEASURE_VALUE_CLASS_LOAD_ERROR,
							(Object[])null,
							e);
				}
			}
		}
		
		return valueClass;
	}

	/**
	 *
	 */
	private String getValueClassRealName()
	{
		if (valueClassRealName == null)
		{
			valueClassRealName = JRClassLoader.getClassRealName(valueClassName);
		}
		
		return valueClassRealName;
	}

	@Override
	public JRVariable getVariable()
	{
		return variable;
	}

	@Override
	public String getPercentageCalculatorClassName()
	{
		return percentageCalculatorClassName;
	}

	@Override
	public Class<?> getPercentageCalculatorClass()
	{
		if (percentageCalculatorClass == null)
		{
			String className = getPercentageCalculatorClassRealName();
			if (className != null)
			{
				try
				{
					percentageCalculatorClass = JRClassLoader.loadClassForName(className);
				}
				catch (ClassNotFoundException e)
				{
					throw 
						new JRRuntimeException(
							EXCEPTION_MESSAGE_KEY_MEASURE_PERCENTAGE_CALCULATOR_CLASS_LOAD_ERROR,
							(Object[])null,
							e);
				}
			}
		}
		
		return percentageCalculatorClass;
	}

	/**
	 *
	 */
	private String getPercentageCalculatorClassRealName()
	{
		if (percentageCalculatorClassRealName == null)
		{
			percentageCalculatorClassRealName = JRClassLoader.getClassRealName(percentageCalculatorClassName);
		}
		
		return percentageCalculatorClassRealName;
	}

	@Override
	public Object clone() 
	{
		JRBaseCrosstabMeasure clone = null;
		try
		{
			clone = (JRBaseCrosstabMeasure) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			// never
			throw new JRRuntimeException(e);
		}
		clone.expression = JRCloneUtils.nullSafeClone(expression);
		clone.variable = JRCloneUtils.nullSafeClone(variable);
		return clone;
	}

	@Override
	public Object clone(CrosstabBaseCloneFactory cloneFactory)
	{
		JRBaseCrosstabMeasure clone = (JRBaseCrosstabMeasure) clone();
		clone.variable = cloneFactory.clone(variable);
		return clone;
	}
}

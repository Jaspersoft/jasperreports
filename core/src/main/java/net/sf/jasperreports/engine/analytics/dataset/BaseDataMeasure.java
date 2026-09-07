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
package net.sf.jasperreports.engine.analytics.dataset;

import java.io.Serializable;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.type.CalculationEnum;
import net.sf.jasperreports.engine.util.JRClassLoader;
import net.sf.jasperreports.engine.util.JRCloneUtils;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class BaseDataMeasure implements DataMeasure, Serializable
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String EXCEPTION_MESSAGE_KEY_MEASURE_INCREMENTER_LOAD_ERROR = "engine.analytics.dataset.measure.incrementer.load.error";
	public static final String EXCEPTION_MESSAGE_KEY_MEASURE_VALUE_LOAD_ERROR = "engine.analytics.dataset.measure.value.load.error";
	
	protected String name;
	protected JRExpression labelExpression;
	protected String valueClassName;
	private transient Class<?> valueClass;
	protected JRExpression valueExpression;
	protected CalculationEnum calculation;
	protected String incrementerFactoryClassName;
	private transient Class<?> incrementerFactoryClass;
	
	public BaseDataMeasure()
	{
	}
	
	public BaseDataMeasure(DataMeasure measure, JRBaseObjectFactory factory)
	{
		factory.put(measure, this);
		
		this.name = measure.getName();
		this.labelExpression = factory.getExpression(measure.getLabelExpression());
		this.valueClassName = measure.getValueClassName();
		this.valueExpression = factory.getExpression(measure.getValueExpression());
		this.calculation = measure.getCalculation();
		this.incrementerFactoryClassName = measure.getIncrementerFactoryClassName();
	}
	
	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public JRExpression getLabelExpression()
	{
		return labelExpression;
	}

	@Override
	public String getValueClassName()
	{
		return valueClassName;
	}
	
	protected void setValueClassName(String valueClassName)
	{
		this.valueClassName = valueClassName;
		this.valueClass = null;
	}

	@Override
	public Class<?> getValueClass()
	{
		if (valueClass == null)
		{
			String className = JRClassLoader.getClassRealName(valueClassName);
			if (className != null)
			{
				try
				{
					valueClass = JRClassLoader.resolveClassForName(className);
				}
				catch (ClassNotFoundException e)
				{
					throw 
						new JRRuntimeException(
							EXCEPTION_MESSAGE_KEY_MEASURE_VALUE_LOAD_ERROR,
							(Object[])null,
							e);
				}
			}
		}
		
		return valueClass;
	}

	@Override
	public JRExpression getValueExpression()
	{
		return valueExpression;
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
	public Class<?> getIncrementerFactoryClass()
	{
		if (incrementerFactoryClass == null)
		{
			String className = JRClassLoader.getClassRealName(incrementerFactoryClassName);
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
							EXCEPTION_MESSAGE_KEY_MEASURE_INCREMENTER_LOAD_ERROR,
							(Object[])null,
							e);
				}
			}
		}
		
		return incrementerFactoryClass;
	}
	
	protected void setIncrementerFactoryClassName(String incrementerFactoryClassName)
	{
		this.incrementerFactoryClassName = incrementerFactoryClassName;
		this.incrementerFactoryClass = null;
	}
	
	@Override
	public Object clone() 
	{
		BaseDataMeasure clone = null;
		try
		{
			clone = (BaseDataMeasure) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			// never
			throw new JRRuntimeException(e);
		}
		
		clone.labelExpression = JRCloneUtils.nullSafeClone(labelExpression);
		clone.valueExpression = JRCloneUtils.nullSafeClone(valueExpression);
		return clone;
	}

}

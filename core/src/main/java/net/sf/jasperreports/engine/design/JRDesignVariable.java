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
package net.sf.jasperreports.engine.design;

import com.fasterxml.jackson.annotation.JsonSetter;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.base.JRBaseVariable;
import net.sf.jasperreports.engine.type.CalculationEnum;
import net.sf.jasperreports.engine.type.IncrementTypeEnum;
import net.sf.jasperreports.engine.type.ResetTypeEnum;
import net.sf.jasperreports.engine.xml.JRXmlConstants;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRDesignVariable extends JRBaseVariable
{


	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_CALCULATION = "calculation";
	
	public static final String PROPERTY_EXPRESSION = "expression";
	
	public static final String PROPERTY_INCREMENTER_FACTORY_CLASS_NAME = "incrementerFactoryClass";
	
	public static final String PROPERTY_INCREMENT_GROUP = "incrementGroup";
	
	public static final String PROPERTY_INCREMENT_TYPE = "incrementType";
	
	public static final String PROPERTY_INITIAL_VALUE_EXPRESSION = "initialValueExpression";
	
	public static final String PROPERTY_NAME = "name";
	
	public static final String PROPERTY_RESET_GROUP = "resetGroup";
	
	public static final String PROPERTY_RESET_TYPE = "resetType";
	
	public static final String PROPERTY_SYSTEM_DEFINED = "systemDefined";
	
	public static final String PROPERTY_VALUE_CLASS_NAME = "valueClassName";

	/**
	 *
	 */
	public void setName(String name)
	{
		String old = this.name;
		this.name = name;
		getEventSupport().firePropertyChange(PROPERTY_NAME, old, this.name);
	}
		
	/**
	 *
	 */
	public void setValueClass(Class<?> clazz)
	{
		setValueClassName(clazz.getName());
	}
		
	/**
	 *
	 */
	@JsonSetter(JRXmlConstants.ATTRIBUTE_class)
	public void setValueClassName(String className)
	{
		Object old = this.valueClassName;
		valueClassName = className;
		valueClass = null;
		valueClassRealName = null;
		getEventSupport().firePropertyChange(PROPERTY_VALUE_CLASS_NAME, old, this.valueClassName);
	}
		
	/**
	 *
	 */
	public void setIncrementerFactoryClass(Class<?> clazz)
	{
		setIncrementerFactoryClassName(clazz.getName());
	}
		
	/**
	 *
	 */
	@JsonSetter(JRXmlConstants.ATTRIBUTE_incrementerFactoryClass)
	public void setIncrementerFactoryClassName(String className)
	{
		Object old = this.incrementerFactoryClassName;
		incrementerFactoryClassName = className;
		incrementerFactoryClass = null;
		incrementerFactoryClassRealName = null;
		getEventSupport().firePropertyChange(PROPERTY_INCREMENTER_FACTORY_CLASS_NAME, old, this.incrementerFactoryClassName);
	}
		
	/**
	 *
	 */
	public void setResetType(ResetTypeEnum resetType)
	{
		Object old = this.resetType;
		this.resetType = resetType;
		getEventSupport().firePropertyChange(PROPERTY_RESET_TYPE, old, this.resetType);
	}
		
	/**
	 *
	 */
	public void setIncrementType(IncrementTypeEnum incrementType)
	{
		Object old = this.incrementType;
		this.incrementType = incrementType;
		getEventSupport().firePropertyChange(PROPERTY_INCREMENT_TYPE, old, this.incrementType);
	}
		
	/**
	 *
	 */
	public void setCalculation(CalculationEnum calculation)
	{
		CalculationEnum old = this.calculation;
		this.calculation = calculation;
		getEventSupport().firePropertyChange(PROPERTY_CALCULATION, old, this.calculation);
	}

	/**
	 *
	 */
	public void setSystemDefined(boolean isSystemDefined)
	{
		boolean old = this.isSystemDefined;
		this.isSystemDefined = isSystemDefined;
		getEventSupport().firePropertyChange(PROPERTY_SYSTEM_DEFINED, old, this.isSystemDefined);
	}

	/**
	 *
	 */
	public void setExpression(JRExpression expression)
	{
		Object old = this.expression;
		this.expression = expression;
		getEventSupport().firePropertyChange(PROPERTY_EXPRESSION, old, this.expression);
	}
		
	/**
	 *
	 */
	public void setInitialValueExpression(JRExpression expression)
	{
		Object old = this.initialValueExpression;
		this.initialValueExpression = expression;
		getEventSupport().firePropertyChange(PROPERTY_INITIAL_VALUE_EXPRESSION, old, this.initialValueExpression);
	}
	
	/**
	 *
	 */
	public void setResetGroup(String group)
	{
		Object old = this.resetGroup;
		this.resetGroup = group;
		getEventSupport().firePropertyChange(PROPERTY_RESET_GROUP, old, this.resetGroup);
	}
		
	/**
	 *
	 */
	public void setIncrementGroup(String group)
	{
		Object old = this.incrementGroup;
		this.incrementGroup = group;
		getEventSupport().firePropertyChange(PROPERTY_INCREMENT_GROUP, old, this.incrementGroup);
	}
	
	@Override
	public Object clone()
	{
		JRDesignVariable clone = (JRDesignVariable)super.clone();
		return clone;
	}

}

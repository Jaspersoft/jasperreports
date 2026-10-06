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
import net.sf.jasperreports.engine.base.JRBaseParameter;
import net.sf.jasperreports.engine.type.ParameterEvaluationTimeEnum;
import net.sf.jasperreports.engine.xml.JRXmlConstants;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRDesignParameter extends JRBaseParameter
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_DEFAULT_VALUE_EXPRESSION = "defaultValueExpression";
	
	public static final String PROPERTY_FOR_PROMPTING = "isForPrompting";
	
	public static final String PROPERTY_NAME = "name";
	
	public static final String PROPERTY_SYSTEM_DEFINED = "systemDefined";
	
	public static final String PROPERTY_VALUE_CLASS_NAME = "valueClassName";
	
	public static final String PROPERTY_NESTED_TYPE_NAME = "nestedType";
	
	public static final String PROPERTY_EVALUATION_TIME = "evaluationTime";


	/**
	 *
	 */
	public void setName(String name)
	{
		Object old = this.name;
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
	 * Sets the parameter nested value type.
	 * 
	 * @param type the nested value type
	 * @see #getNestedType()
	 */
	public void setNestedType(Class<?> type)
	{
		setNestedTypeName(type == null ? null : type.getName());
	}
	
	/**
	 * Sets the parameter nested value type.
	 * 
	 * @param typeName the name of the nested value type
	 * @see #getNestedType()
	 */
	@JsonSetter(JRXmlConstants.ATTRIBUTE_nestedType)
	public void setNestedTypeName(String typeName)
	{
		Object old = this.nestedTypeName;
		nestedTypeName = typeName;
		nestedType = null;
		getEventSupport().firePropertyChange(PROPERTY_NESTED_TYPE_NAME, old, this.nestedTypeName);
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
	public void setForPrompting(boolean isForPrompting)
	{
		boolean old = this.isForPrompting;
		this.isForPrompting = isForPrompting;
		getEventSupport().firePropertyChange(PROPERTY_FOR_PROMPTING, old, this.isForPrompting);
	}

	/**
	 *
	 */
	public void setEvaluationTime(ParameterEvaluationTimeEnum evaluationTime)
	{
		ParameterEvaluationTimeEnum old = this.evaluationTime;
		this.evaluationTime = evaluationTime;
		getEventSupport().firePropertyChange(PROPERTY_EVALUATION_TIME, old, this.evaluationTime);
	}

	/**
	 *
	 */
	public void setDefaultValueExpression(JRExpression expression)
	{
		Object old = this.defaultValueExpression;
		this.defaultValueExpression = expression;
		getEventSupport().firePropertyChange(PROPERTY_DEFAULT_VALUE_EXPRESSION, old, this.defaultValueExpression);
	}

}

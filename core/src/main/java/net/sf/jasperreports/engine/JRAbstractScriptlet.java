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
package net.sf.jasperreports.engine;

import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.fill.JRFillDataset;
import net.sf.jasperreports.engine.fill.JRFillField;
import net.sf.jasperreports.engine.fill.JRFillGroup;
import net.sf.jasperreports.engine.fill.JRFillParameter;
import net.sf.jasperreports.engine.fill.JRFillVariable;


/**
 * Defines an abstract representation of a report scriptlet. Scriptlets are useful when a specific behavior is needed
 * in certain moments of the report filling process, such as report, column or group initialization. Scriptlets must implement
 * the abstract methods that define the behavior at the specified moments.
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class JRAbstractScriptlet
{
	public static final String EXCEPTION_MESSAGE_KEY_FIELD_NOT_FOUND = "scriptlets.field.not.found";
	public static final String EXCEPTION_MESSAGE_KEY_PARAMETER_NOT_FOUND = "scriptlets.parameter.not.found";
	public static final String EXCEPTION_MESSAGE_KEY_VARIABLE_NOT_FOUND = "scriptlets.variable.not.found";
	public static final String EXCEPTION_MESSAGE_KEY_VARIABLE_VALUE_INCOMPATIBLE = "scriptlets.variable.value.incompatible";

	/**
	 *
	 */
	protected JRScriptlet scriptletDefinition;
	protected JRFillDataset dataset;
	protected Map<String,JRFillParameter> parametersMap;
	protected Map<String,JRFillField> fieldsMap;
	protected Map<String,JRFillVariable> variablesMap;
	protected JRFillGroup[] groups;


	/**
	 *
	 */
	public JRAbstractScriptlet()
	{
	}

	public void setScriptletDefinition(JRScriptlet scriptletDefinition)
	{
		this.scriptletDefinition = scriptletDefinition;
	}

	/**
	 *
	 */
	public void setData(JRFillDataset dataset)
	{
		this.dataset = dataset;
		
		parametersMap = dataset.getParametersMap();
		fieldsMap = dataset.getFieldsMap();
		variablesMap = dataset.getVariablesMap();
		groups = (JRFillGroup[])dataset.getGroups();
	}


	/**
	 *
	 */
	public Object getParameterValue(String parameterName) throws JRScriptletException
	{
		return getParameterValue(parameterName, true);
	}


	/**
	 *
	 */
	public Object getParameterValue(String parameterName, boolean mustBeDeclared) throws JRScriptletException
	{
		JRFillParameter parameter = this.parametersMap.get(parameterName);
		if (parameter == null)
		{
			if (mustBeDeclared)
			{
				throw 
					new JRScriptletException(
						EXCEPTION_MESSAGE_KEY_PARAMETER_NOT_FOUND,
						new Object[]{parameterName});
			}
			return ((Map<String,?>)this.parametersMap.get(JRParameter.REPORT_PARAMETERS_MAP).getValue()).get(parameterName);
		}
		return parameter.getValue();
	}


	/**
	 *
	 */
	public Object getFieldValue(String fieldName) throws JRScriptletException
	{
		JRFillField field = fieldsMap == null ? null : fieldsMap.get(fieldName);
		if (field == null)
		{
			throw 
				new JRScriptletException(
					EXCEPTION_MESSAGE_KEY_FIELD_NOT_FOUND,
					new Object[]{fieldName});
		}
		return field.getValue();
	}


	/**
	 *
	 */
	public Object getVariableValue(String variableName) throws JRScriptletException
	{
		JRFillVariable variable = this.variablesMap.get(variableName);
		if (variable == null)
		{
			throw 
				new JRScriptletException(
					EXCEPTION_MESSAGE_KEY_VARIABLE_NOT_FOUND,
					new Object[]{variableName});
		}
		return variable.getValue();
	}


	/**
	 *
	 */
	public void setVariableValue(String variableName, Object value) throws JRScriptletException
	{
		JRFillVariable variable = this.variablesMap.get(variableName);
		if (variable == null)
		{
			throw 
				new JRScriptletException(
					EXCEPTION_MESSAGE_KEY_VARIABLE_NOT_FOUND,
					new Object[]{variableName});
		}
		
		if (value != null && !variable.getValueClass().isInstance(value) )
		{
			throw 
				new JRScriptletException(
					EXCEPTION_MESSAGE_KEY_VARIABLE_VALUE_INCOMPATIBLE,
					new Object[]{variableName, variable.getValueClassName()});
		}
		
		variable.setValue(value);
	}

	protected Map<String, Object> evaluateProperties(byte evaluation) throws JRException
	{
		Map<String, Object> properties = new HashMap<>();
		if (scriptletDefinition != null)
		{
			if (scriptletDefinition.hasProperties())
			{
				JRPropertiesMap propertiesMap = scriptletDefinition.getPropertiesMap();
				String[] names = propertiesMap.getPropertyNames();
				for (int i = 0; i < names.length; i++)
				{
					properties.put(names[i], propertiesMap.getProperty(names[i]));
				}
			}
			
			JRPropertyExpression[] propertyExpressions = scriptletDefinition.getPropertyExpressions();
			if (propertyExpressions != null && propertyExpressions.length > 0)
			{
				for (int i = 0; i < propertyExpressions.length; i++)
				{
					JRPropertyExpression propertyExpression = propertyExpressions[i];
					Object value = dataset.evaluateExpression(propertyExpression.getValueExpression(), evaluation);
					properties.put(propertyExpression.getName(), value);
				}
			}
		}
		return properties;
	}

	/**
	 *
	 */
	public void callBeforeReportInit() throws JRScriptletException
	{
		this.beforeReportInit();
		this.beforePageInit();
		this.beforeColumnInit();

		if (groups != null && groups.length > 0)
		{
			for (int i = 0; i < groups.length; i++)
			{
				this.beforeGroupInit( groups[i].getName() );
			}
		}
	}


	/**
	 *
	 */
	public void callAfterReportInit() throws JRScriptletException
	{
		if (groups != null && groups.length > 0)
		{
			for (int i = groups.length - 1; i >= 0; i--)
			{
				this.afterGroupInit( groups[i].getName() );
			}
		}

		this.afterColumnInit();
		this.afterPageInit();
		this.afterReportInit();
	}


	/**
	 *
	 */
	public void callBeforePageInit() throws JRScriptletException
	{
		this.beforePageInit();
		this.beforeColumnInit();
	}


	/**
	 *
	 */
	public void callAfterPageInit() throws JRScriptletException
	{
		this.afterColumnInit();
		this.afterPageInit();
	}


	/**
	 *
	 */
	public void callBeforeColumnInit() throws JRScriptletException
	{
		this.beforeColumnInit();
	}


	/**
	 *
	 */
	public void callAfterColumnInit() throws JRScriptletException
	{
		this.afterColumnInit();
	}


	/**
	 *
	 */
	public void callBeforeGroupInit() throws JRScriptletException
	{
		if (groups != null && groups.length > 0)
		{
			JRFillGroup group = null;
			for (int i = 0; i < groups.length; i++)
			{
				group = groups[i];
				if (group.hasChanged())
				{
					this.beforeGroupInit(group.getName());
				}
			}
		}
	}


	/**
	 *
	 */
	public void callAfterGroupInit() throws JRScriptletException
	{
		if (groups != null && groups.length > 0)
		{
			JRFillGroup group = null;
			for (int i = groups.length - 1; i >= 0; i--)
			{
				group = groups[i];
				if (group.hasChanged())
				{
					this.afterGroupInit(group.getName());
				}
			}
		}
	}


	/**
	 *
	 */
	public void callBeforeDetailEval() throws JRScriptletException
	{
		this.beforeDetailEval();
	}


	/**
	 *
	 */
	public void callAfterDetailEval() throws JRScriptletException
	{
		this.afterDetailEval();
	}


	/**
	 * Called before the report is initialized.
	 */
	public abstract void beforeReportInit() throws JRScriptletException;


	/**
	 * Called after the report is initialized.
	 */
	public abstract void afterReportInit() throws JRScriptletException;


	/**
	 * Called before each page is initialized.
	 */
	public abstract void beforePageInit() throws JRScriptletException;


	/**
	 * Called after each page is initialized.
	 */
	public abstract void afterPageInit() throws JRScriptletException;


	/**
	 * Called before each column is initialized.
	 */
	public abstract void beforeColumnInit() throws JRScriptletException;


	/**
	 * Called after each column is initialized.
	 */
	public abstract void afterColumnInit() throws JRScriptletException;


	/**
	 * Called before a group is initialized.
	 * @param groupName the group name
	 */
	public abstract void beforeGroupInit(String groupName) throws JRScriptletException;


	/**
	 * Called after a group is initialized.
	 * @param groupName the group name
	 */
	public abstract void afterGroupInit(String groupName) throws JRScriptletException;


	/**
	 * Called before evaluating each detail.
	 */
	public abstract void beforeDetailEval() throws JRScriptletException;


	/**
	 * Called after evaluating each detail.
	 */
	public abstract void afterDetailEval() throws JRScriptletException;


}

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
package net.sf.jasperreports.engine.design;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDatasetParameter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.ReturnValue;
import net.sf.jasperreports.engine.base.JRBaseDatasetRun;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;
import net.sf.jasperreports.engine.util.JRCloneUtils;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * Implementation of {@link net.sf.jasperreports.engine.JRDatasetRun JRDatasetRun} to be used for report design.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRDesignDatasetRun extends JRBaseDatasetRun implements JRChangeEventsSupport
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	public static final String EXCEPTION_MESSAGE_KEY_DUPLICATE_PARAMETER = "design.dataset.run.duplicate.parameter";

	private Map<String, JRDatasetParameter> parametersMap;
	private List<JRDatasetParameter> parametersList;
	
	public static final String PROPERTY_CONNECTION_EXPRESSION = "connectionExpression";
	
	public static final String PROPERTY_DATASET_NAME = "datasetName";
	
	public static final String PROPERTY_DATA_SOURCE_EXPRESSION = "dataSourceExpression";
	
	public static final String PROPERTY_PARAMETERS_MAP_EXPRESSION = "parametersMapExpression";
	
	public static final String PROPERTY_PARAMETERS = "parameters";
	
	public static final String PROPERTY_RETURN_VALUES = "returnValues";
	
	
	/**
	 * Creates an empty dataset instantiation.
	 */
	public JRDesignDatasetRun()
	{
		parametersMap = new HashMap<>();
		parametersList = new ArrayList<>();
		
		returnValues = new ArrayList<ReturnValue>(2);
	}
	
	
	/**
	 * Adds a parameter value.
	 * 
	 * @param parameter the parameter value
	 * @throws JRException
	 * @see net.sf.jasperreports.engine.JRDatasetRun#getParameters()
	 */
	public void addParameter(JRDatasetParameter parameter) throws JRException
	{
		if (parametersMap.containsKey(parameter.getName()))
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_DUPLICATE_PARAMETER,
					new Object[]{parameter.getName()});
		}
		
		parametersMap.put(parameter.getName(), parameter);
		parametersList.add(parameter);
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_PARAMETERS, 
				parameter, parametersList.size() - 1);
	}
	
	
	/**
	 * Removes a parameter value.
	 * 
	 * @param parameterName the parameter name
	 * @return the removed parameter value
	 */
	public JRDatasetParameter removeParameter(String parameterName)
	{
		JRDatasetParameter param = parametersMap.remove(parameterName);
		if (param != null)
		{
			int idx = parametersList.indexOf(param);
			if (idx >= 0)
			{
				parametersList.remove(idx);
			}
			getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_PARAMETERS, param, idx);
		}
		
		return param;
	}
	
	
	/**
	 * Removes a parameter value.
	 * 
	 * @param parameter the parameter value
	 * @return the parameter value
	 */
	public JRDatasetParameter removeParameter(JRDatasetParameter parameter)
	{
		return removeParameter(parameter.getName());
	}

	
	/**
	 * Sets the connection expression.
	 * 
	 * @param connectionExpression the connection expression
	 * @see net.sf.jasperreports.engine.JRDatasetRun#getConnectionExpression()
	 */
	public void setConnectionExpression(JRExpression connectionExpression)
	{
		Object old = this.connectionExpression;
		this.connectionExpression = connectionExpression;
		getEventSupport().firePropertyChange(PROPERTY_CONNECTION_EXPRESSION, old, this.connectionExpression);
	}

	
	/**
	 * Sets the sub dataset name.
	 * 
	 * @param datasetName the sub dataset name
	 * @see net.sf.jasperreports.engine.JRDatasetRun#getDatasetName()
	 */
	@JsonSetter(JRXmlConstants.ATTRIBUTE_subDataset)
	public void setDatasetName(String datasetName)
	{
		Object old = this.datasetName;
		this.datasetName = datasetName;
		getEventSupport().firePropertyChange(PROPERTY_DATASET_NAME, old, this.datasetName);
	}

	
	/**
	 * Sets the data source expression.
	 * 
	 * @param dataSourceExpression the data source expression
	 * @see net.sf.jasperreports.engine.JRDatasetRun#getDataSourceExpression()
	 */
	public void setDataSourceExpression(JRExpression dataSourceExpression)
	{
		Object old = this.dataSourceExpression;
		this.dataSourceExpression = dataSourceExpression;
		getEventSupport().firePropertyChange(PROPERTY_DATA_SOURCE_EXPRESSION, old, this.dataSourceExpression);
	}

	
	/**
	 * Sets the parameters map expression.
	 * 
	 * @param parametersMapExpression the parameters map expression
	 * @see net.sf.jasperreports.engine.JRDatasetRun#getParametersMapExpression()
	 */
	public void setParametersMapExpression(JRExpression parametersMapExpression)
	{
		Object old = this.parametersMapExpression;
		this.parametersMapExpression = parametersMapExpression;
		getEventSupport().firePropertyChange(PROPERTY_PARAMETERS_MAP_EXPRESSION, old, this.parametersMapExpression);
	}

	@Override
	public JRDatasetParameter[] getParameters()
	{
		JRDatasetParameter[] params = new JRDatasetParameter[parametersList.size()];
		parametersList.toArray(params);
		return params;
	}

	@JsonSetter
	private void setParameters(List<JRDatasetParameter> parameters) throws JRException
	{
		if (parameters != null)
		{
			for (JRDatasetParameter parameter : parameters)
			{
				addParameter(parameter);
			}
		}
	}

	public void setUUID(UUID uuid)
	{
		this.uuid = uuid;
	}
	
	@JsonSetter
	private void setReturnValues(List<ReturnValue> returnValues) throws JRException
	{
		if (returnValues != null)
		{
			for (ReturnValue returnValue : returnValues)
			{
				addReturnValue(returnValue);
			}
		}
	}

	/**
	 * Adds a return value to the subdataset run.
	 * 
	 * @param returnValue the return value to be added.
	 */
	public void addReturnValue(ReturnValue returnValue)
	{
		this.returnValues.add(returnValue);
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_RETURN_VALUES, 
				returnValue, returnValues.size() - 1);
	}

	
	/**
	 * Returns the list of values to be copied from the subdataset run into the main dataset.
	 * 
	 * @return list of {@link ReturnValue ReturnValue} objects
	 */
	@JsonIgnore
	public List<ReturnValue> getReturnValuesList()
	{
		return returnValues;
	}

	
	/**
	 * Removes a return value from the subdataset run.
	 * 
	 * @param returnValue the return value to be removed
	 * @return <code>true</code> if the return value was found and removed 
	 */
	public boolean removeReturnValue(ReturnValue returnValue)
	{
		int idx = this.returnValues.indexOf(returnValue);
		if (idx >= 0)
		{
			this.returnValues.remove(idx);
			getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_RETURN_VALUES, returnValue, idx);
			return true;
		}
		return false;
	}

	@Override
	public Object clone() 
	{
		JRDesignDatasetRun clone = (JRDesignDatasetRun)super.clone();
		
		if (parametersList != null)
		{
			clone.parametersList = new ArrayList<>(parametersList.size());
			clone.parametersMap = new HashMap<>(parametersList.size());
			for (int i = 0; i < parametersList.size(); i++)
			{
				JRDatasetParameter parameter = JRCloneUtils.nullSafeClone(parametersList.get(i));
				clone.parametersList.add(parameter);
				clone.parametersMap.put(parameter.getName(), parameter);
			}
		}
		
		clone.eventSupport = null;
		
		return clone;
	}
	
	private transient JRPropertyChangeSupport eventSupport;
	
	@Override
	public JRPropertyChangeSupport getEventSupport()
	{
		synchronized (this)
		{
			if (eventSupport == null)
			{
				eventSupport = new JRPropertyChangeSupport(this);
			}
		}
		
		return eventSupport;
	}

}

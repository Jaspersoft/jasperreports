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
package net.sf.jasperreports.parts.subreport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRSubreportParameter;
import net.sf.jasperreports.engine.JRSubreportReturnValue;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.base.JRBaseSubreport;
import net.sf.jasperreports.engine.design.JRDesignSubreport;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;
import net.sf.jasperreports.engine.util.JRCloneUtils;

/**
 * Standard {@link SubreportPartComponent} implementation.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class StandardSubreportPartComponent implements Serializable, SubreportPartComponent, JRChangeEventsSupport
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	protected Boolean usingCache;


	/**
	 *
	 */
	protected Map<String, JRSubreportParameter> parametersMap = new LinkedHashMap<>();
	
	/**
	 * Values to be copied from the subreport into the master report.
	 */
	protected List<JRSubreportReturnValue> returnValues = new ArrayList<>();

	/**
	 *
	 */
	protected JRExpression parametersMapExpression;
	protected JRExpression expression;


	/**
	 *
	 */
	public StandardSubreportPartComponent()
	{
	}

	public StandardSubreportPartComponent(SubreportPartComponent subreport, JRBaseObjectFactory factory)
	{
		this.usingCache = subreport.getUsingCache();
		this.parametersMapExpression = factory.getExpression(subreport.getParametersMapExpression());
		this.expression = factory.getExpression(subreport.getExpression());

		/*   */
		JRSubreportParameter[] jrSubreportParameters = subreport.getParameters();
		if (jrSubreportParameters != null && jrSubreportParameters.length > 0)
		{
			parametersMap = new HashMap<>(jrSubreportParameters.length);
			for (JRSubreportParameter jrSubreportParameter : jrSubreportParameters)
			{
				parametersMap.put(jrSubreportParameter.getName(), factory.getSubreportParameter(jrSubreportParameter));
			}
		}

		JRSubreportReturnValue[] subrepReturnValues = subreport.getReturnValues();
		if (subrepReturnValues != null && subrepReturnValues.length > 0)
		{
			returnValues = new ArrayList<>(subrepReturnValues.length);
			for (JRSubreportReturnValue subrepReturnValue : subrepReturnValues)
			{
				returnValues.add(factory.getSubreportReturnValue(subrepReturnValue));
			}
		}
	}
	
	@Override
	public Boolean getUsingCache()
	{
		return usingCache;
	}


	@Override
	public void setUsingCache(Boolean usingCache)
	{
		Object old = this.usingCache;
		this.usingCache = usingCache;
		getEventSupport().firePropertyChange(JRBaseSubreport.PROPERTY_USING_CACHE, old, this.usingCache);
	}


	@Override
	public JRExpression getParametersMapExpression()
	{
		return this.parametersMapExpression;
	}

	/**
	 *
	 */
	public void setParametersMapExpression(JRExpression parametersMapExpression)
	{
		Object old = this.parametersMapExpression;
		this.parametersMapExpression = parametersMapExpression;
		getEventSupport().firePropertyChange(JRDesignSubreport.PROPERTY_PARAMETERS_MAP_EXPRESSION, old, this.parametersMapExpression);
	}

	@Override
	public JRSubreportParameter[] getParameters()
	{
		JRSubreportParameter[] parametersArray = new JRSubreportParameter[parametersMap.size()];
		
		parametersMap.values().toArray(parametersArray);

		return parametersArray;
	}
	
	/**
	 *
	 */
	@JsonIgnore
	public Map<String, JRSubreportParameter> getParametersMap()
	{
		return this.parametersMap;
	}
	
	@JsonSetter
	private void setParameters(List<JRSubreportParameter> parameters) throws JRException
	{
		if (parameters != null)
		{
			for (JRSubreportParameter parameter : parameters)
			{
				addParameter(parameter);
			}
		}
	}

	/**
	 *
	 */
	public void addParameter(JRSubreportParameter subreportParameter) throws JRException
	{
		if (this.parametersMap.containsKey(subreportParameter.getName()))
		{
			throw 
			new JRException(
				JRDesignSubreport.EXCEPTION_MESSAGE_KEY_DUPLICATE_PARAMETER,
				new Object[]{subreportParameter.getName()});
		}

		this.parametersMap.put(subreportParameter.getName(), subreportParameter);
		getEventSupport().fireCollectionElementAddedEvent(JRDesignSubreport.PROPERTY_PARAMETERS, 
				subreportParameter, parametersMap.size() - 1);
	}
	
	/**
	 *
	 */
	public JRSubreportParameter removeParameter(String name)
	{
		JRSubreportParameter removed = this.parametersMap.remove(name);
		if (removed != null)
		{
			getEventSupport().fireCollectionElementRemovedEvent(JRDesignSubreport.PROPERTY_PARAMETERS, removed, -1);
		}
		return removed;
	}
	
	
	/**
	 * Adds a return value to the subreport.
	 * 
	 * @param returnValue the return value to be added.
	 */
	public void addReturnValue(JRSubreportReturnValue returnValue)
	{
		this.returnValues.add(returnValue);
		getEventSupport().fireCollectionElementAddedEvent(JRDesignSubreport.PROPERTY_RETURN_VALUES, 
				returnValue, returnValues.size() - 1);
	}

	/**
	 * Returns the list of values to be copied from the subreport into the master.
	 * 
	 * @return the list of values to be copied from the subreport into the master.
	 */
	@Override
	public JRSubreportReturnValue[] getReturnValues()
	{
		JRSubreportReturnValue[] returnValuesArray = new JRSubreportReturnValue[returnValues.size()];

		returnValues.toArray(returnValuesArray);

		return returnValuesArray;
	}

	/**
	 * Returns the list of values to be copied from the subreport into the master.
	 * 
	 * @return list of {@link JRSubreportReturnValue JRSubreportReturnValue} objects
	 */
	@JsonIgnore
	public List<JRSubreportReturnValue> getReturnValuesList()
	{
		return returnValues;
	}

	@JsonSetter
	private void seReturnValues(List<JRSubreportReturnValue> returnValues)
	{
		if (returnValues != null)
		{
			for (JRSubreportReturnValue returnValue : returnValues)
			{
				addReturnValue(returnValue);
			}
		}
	}

	/**
	 * Removes a return value from the subreport.
	 * 
	 * @param returnValue the return value to be removed
	 * @return <code>true</code> if the return value was found and removed 
	 */
	public boolean removeReturnValue(JRSubreportReturnValue returnValue)
	{
		int idx = this.returnValues.indexOf(returnValue);
		if (idx >= 0)
		{
			this.returnValues.remove(idx);
			getEventSupport().fireCollectionElementRemovedEvent(JRDesignSubreport.PROPERTY_RETURN_VALUES, returnValue, idx);
			return true;
		}
		return false;
	}

	@Override
	public JRExpression getExpression()
	{
		return this.expression;
	}

	/**
	 *
	 */
	public void setExpression(JRExpression expression)
	{
		Object old = this.expression;
		this.expression = expression;
		getEventSupport().firePropertyChange(JRDesignSubreport.PROPERTY_EXPRESSION, old, this.expression);
	}
	
	@Override
	public Object clone()
	{
		StandardSubreportPartComponent clone = null;
		try
		{
			clone = (StandardSubreportPartComponent) super.clone();
		} 
		catch (CloneNotSupportedException e)
		{
			// never
			throw new JRRuntimeException(e);
		}

		if (parametersMap != null)
		{
			clone.parametersMap = new LinkedHashMap<>();
			for (String name : parametersMap.keySet())
			{
				clone.parametersMap.put(name, JRCloneUtils.nullSafeClone(parametersMap.get(name)));
			}
		}

		clone.returnValues = JRCloneUtils.cloneList(returnValues);
		clone.parametersMapExpression = JRCloneUtils.nullSafeClone(parametersMapExpression);
		clone.expression = JRCloneUtils.nullSafeClone(expression);

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

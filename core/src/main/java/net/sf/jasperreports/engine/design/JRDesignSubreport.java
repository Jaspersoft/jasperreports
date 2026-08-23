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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRSubreport;
import net.sf.jasperreports.engine.JRSubreportParameter;
import net.sf.jasperreports.engine.JRSubreportReturnValue;
import net.sf.jasperreports.engine.JRVisitor;
import net.sf.jasperreports.engine.base.JRBaseSubreport;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.OverflowType;
import net.sf.jasperreports.engine.util.JRCloneUtils;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRDesignSubreport extends JRDesignElement implements JRSubreport
{


	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String EXCEPTION_MESSAGE_KEY_DUPLICATE_PARAMETER = "design.subreport.duplicate.parameter";
	
	public static final String PROPERTY_CONNECTION_EXPRESSION = "connectionExpression";
	
	public static final String PROPERTY_DATASOURCE_EXPRESSION = "dataSourceExpression";
	
	public static final String PROPERTY_EXPRESSION = "expression";
	
	public static final String PROPERTY_PARAMETERS_MAP_EXPRESSION = "parametersMapExpression";
	
	public static final String PROPERTY_PARAMETERS = "parameters";
	
	public static final String PROPERTY_RETURN_VALUES = "returnValues";

	/**
	 *
	 */
	protected Boolean isUsingCache;

	private Boolean runToBottom;
	private OverflowType overflowType;

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
	protected JRExpression connectionExpression;
	protected JRExpression dataSourceExpression;
	protected JRExpression expression;


	@JsonCreator
	private JRDesignSubreport()
	{
		this(JasperDesign.getThreadInstance());
	}
		

	/**
	 *
	 */
	public JRDesignSubreport(JRDefaultStyleProvider defaultStyleProvider)
	{
		super(defaultStyleProvider);
	}
		

	@Override
	public ModeEnum getMode()
	{
		return getStyleResolver().getMode(this, ModeEnum.TRANSPARENT);
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
		getEventSupport().firePropertyChange(PROPERTY_PARAMETERS_MAP_EXPRESSION, old, this.parametersMapExpression);
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
					EXCEPTION_MESSAGE_KEY_DUPLICATE_PARAMETER,
					new Object[]{subreportParameter.getName()});
		}

		this.parametersMap.put(subreportParameter.getName(), subreportParameter);
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_PARAMETERS, 
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
			getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_PARAMETERS, removed, -1);
		}
		return removed;
	}

	@Override
	public JRExpression getConnectionExpression()
	{
		return this.connectionExpression;
	}

	/**
	 *
	 */
	public void setConnectionExpression(JRExpression connectionExpression)
	{
		Object old = this.connectionExpression;
		this.connectionExpression = connectionExpression;
		if (this.connectionExpression != null)
		{
			setDataSourceExpression(null);
		}
		getEventSupport().firePropertyChange(PROPERTY_CONNECTION_EXPRESSION, old, this.connectionExpression);
	}

	@Override
	public JRExpression getDataSourceExpression()
	{
		return this.dataSourceExpression;
	}

	/**
	 *
	 */
	public void setDataSourceExpression(JRExpression dataSourceExpression)
	{
		Object old = this.dataSourceExpression;
		this.dataSourceExpression = dataSourceExpression;
		if (this.dataSourceExpression != null)
		{
			setConnectionExpression(null);
		}
		getEventSupport().firePropertyChange(PROPERTY_DATASOURCE_EXPRESSION, old, this.dataSourceExpression);
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
		getEventSupport().firePropertyChange(PROPERTY_EXPRESSION, old, this.expression);
	}
	
	@Override
	public void collectExpressions(JRExpressionCollector collector)
	{
		collector.collect(this);
	}

	@Override
	public void visit(JRVisitor visitor)
	{
		visitor.visitSubreport(this);
	}
	
	
	/**
	 * Adds a return value to the subreport.
	 * 
	 * @param returnValue the return value to be added.
	 */
	public void addReturnValue(JRSubreportReturnValue returnValue)
	{
		this.returnValues.add(returnValue);
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_RETURN_VALUES, 
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
	private void setReturnValues(List<JRSubreportReturnValue> returnValues)
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
			getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_RETURN_VALUES, returnValue, idx);
			return true;
		}
		return false;
	}


	@Override
	public Boolean getUsingCache()
	{
		return isUsingCache;
	}


	@Override
	public void setUsingCache(Boolean isUsingCache)
	{
		Object old = this.isUsingCache;
		this.isUsingCache = isUsingCache;
		getEventSupport().firePropertyChange(JRBaseSubreport.PROPERTY_USING_CACHE, old, this.isUsingCache);
	}

	
	@Override
	public Boolean isRunToBottom()
	{
		return runToBottom;
	}

	
	@Override
	public void setRunToBottom(Boolean runToBottom)
	{
		Object old = this.runToBottom;
		this.runToBottom = runToBottom;
		getEventSupport().firePropertyChange(JRBaseSubreport.PROPERTY_RUN_TO_BOTTOM, old, this.runToBottom);
	}

	@Override
	public OverflowType getOverflowType()
	{
		return overflowType;
	}

	@Override
	public void setOverflowType(OverflowType overflowType)
	{
		Object old = this.overflowType;
		this.overflowType = overflowType;
		getEventSupport().firePropertyChange(JRBaseSubreport.PROPERTY_OVERFLOW_TYPE, old, this.overflowType);
	}

	
	@Override
	public Object clone() 
	{
		JRDesignSubreport clone = (JRDesignSubreport)super.clone();
		
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
		clone.connectionExpression = JRCloneUtils.nullSafeClone(connectionExpression);
		clone.dataSourceExpression = JRCloneUtils.nullSafeClone(dataSourceExpression);
		clone.expression = JRCloneUtils.nullSafeClone(expression);

		return clone;
	}
}

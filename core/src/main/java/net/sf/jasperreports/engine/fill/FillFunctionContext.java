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

import java.util.Map;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.functions.FunctionContext;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class FillFunctionContext implements FunctionContext
{
	private static final String EXCEPTION_MESSAGE_KEY_PARAMETER_NOT_FOUND = "fill.function.context.parameter.not.found";
	
	private final Map<String, JRFillParameter> parametersMap; 
	
	public FillFunctionContext(
		Map<String, JRFillParameter> parametersMap 
		)
	{
		this.parametersMap = parametersMap;
	}
	
	/**
	 * Returns the value of a parameter.
	 * 
	 * @param parameterName the parameter name
	 * @return the parameter value
	 */
	@Override
	public Object getParameterValue(String parameterName)
	{
		return getParameterValue(parameterName, false);
	}
	
	/**
	 * Returns the value of a parameter.
	 * 
	 * @param parameterName the parameter name
	 * @param ignoreMissing if set, <code>null</code> will be returned for inexisting parameters
	 * @return the parameter value
	 */
	@Override
	public Object getParameterValue(String parameterName, boolean ignoreMissing)
	{
		JRFillParameter param = parametersMap.get(parameterName);
		Object value;
		if (param == null)
		{
			if (!ignoreMissing)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_PARAMETER_NOT_FOUND,
						new Object[]{parameterName});
			}
			
			// look into REPORT_PARAMETERS_MAP
			Map<String, Object> valuesMap = getParameterValuesMap();
			value = valuesMap == null ? null : valuesMap.get(parameterName);
		}
		else
		{
			value = param.getValue();
		}
		return value;
	}
	
	/**
	 * Returns the map of parameter values.
	 * 
	 * @return the map of parameter values
	 */
	protected Map<String,Object> getParameterValuesMap()
	{
		JRFillParameter paramValuesParameter = parametersMap.get(JRParameter.REPORT_PARAMETERS_MAP);
		return (Map<String,Object>) paramValuesParameter.getValue();
	}
	
}

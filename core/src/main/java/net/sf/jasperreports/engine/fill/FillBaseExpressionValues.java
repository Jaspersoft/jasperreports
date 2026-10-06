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

import java.util.Map;

import net.sf.jasperreports.engine.JRRuntimeException;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public abstract class FillBaseExpressionValues implements ExpressionValues
{

	private final JREvaluator evaluator;
	private final Map<String, JRFillParameter> parametersMap;
	private final Map<String, JRFillField> fieldsMap;
	private final Map<String, JRFillVariable> variablesMap;

	protected FillBaseExpressionValues(JREvaluator evaluator,
			Map<String, JRFillParameter> parametersMap, 
			Map<String, JRFillField> fieldsMap, 
			Map<String, JRFillVariable> variablesMap)
	{
		this.evaluator = evaluator;
		this.parametersMap = parametersMap;
		this.fieldsMap = fieldsMap;
		this.variablesMap = variablesMap;
	}
	
	@Override
	public Object getParameterValue(String parameterName)
	{
		JRFillParameter parameter = parametersMap.get(parameterName);
		if (parameter == null)
		{
			throw new JRRuntimeException("Parameter " + parameterName + " not found");
		}
		return parameter.getValue();
	}

	@Override
	public String getMessage(String messageKey)
	{
		return evaluator.str(messageKey);
	}
	
	protected JRFillField field(String fieldName)
	{
		JRFillField field = fieldsMap.get(fieldName);
		if (field == null)
		{
			throw new JRRuntimeException("Field " + fieldName + " not found");
		}
		return field;
	}
	
	protected JRFillVariable variable(String variableName)
	{
		JRFillVariable variable = variablesMap.get(variableName);
		if (variable == null)
		{
			throw new JRRuntimeException("Variable " + variableName + " not found");
		}
		return variable;
	}

}

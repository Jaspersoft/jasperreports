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

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.type.WhenResourceMissingTypeEnum;

/**
 * 
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class BuiltinExpressionEvaluatorDecorator implements DatasetExpressionEvaluator
{

	private final DatasetExpressionEvaluator decorated;
	private final Map<JRExpression, BuiltinExpressionEvaluator> builtinEvaluators;

	public BuiltinExpressionEvaluatorDecorator(DatasetExpressionEvaluator decorated,
			Map<JRExpression, BuiltinExpressionEvaluator> builtinEvaluators)
	{
		this.decorated = decorated;
		this.builtinEvaluators = builtinEvaluators;
	}

	@Override
	public void init(Map<String, JRFillParameter> parametersMap, 
			Map<String, JRFillField> fieldsMap, 
			Map<String, JRFillVariable> variablesMap, 
			WhenResourceMissingTypeEnum resourceMissingType,
			boolean ignoreNPE) throws JRException
	{
		decorated.init(parametersMap, fieldsMap, variablesMap, resourceMissingType, ignoreNPE);
		
		for (BuiltinExpressionEvaluator builtinEvaluator : builtinEvaluators.values())
		{
			builtinEvaluator.init(parametersMap, fieldsMap, variablesMap, 
					resourceMissingType);
		}
	}
	
	@Override
	public Object evaluate(JRExpression expression)
			throws JRExpressionEvalException
	{
		BuiltinExpressionEvaluator builtinEvaluator = builtinEvaluators.get(expression);
		if (builtinEvaluator != null)
		{
			return builtinEvaluator.evaluate(decorated);
		}
		
		return decorated.evaluate(expression);
	}

	@Override
	public Object evaluateEstimated(JRExpression expression)
			throws JRExpressionEvalException
	{
		BuiltinExpressionEvaluator builtinEvaluator = builtinEvaluators.get(expression);
		if (builtinEvaluator != null)
		{
			return builtinEvaluator.evaluateEstimated(decorated);
		}
		
		return decorated.evaluateEstimated(expression);
	}

	@Override
	public Object evaluateOld(JRExpression expression)
			throws JRExpressionEvalException
	{
		BuiltinExpressionEvaluator builtinEvaluator = builtinEvaluators.get(expression);
		if (builtinEvaluator != null)
		{
			return builtinEvaluator.evaluateOld(decorated);
		}
		
		return decorated.evaluateOld(expression);
	}

}

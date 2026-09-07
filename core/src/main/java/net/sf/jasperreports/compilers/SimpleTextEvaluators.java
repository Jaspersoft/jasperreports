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
package net.sf.jasperreports.compilers;

import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.fill.ExpressionValues;
import net.sf.jasperreports.engine.fill.FillExpressionDefaultValues;
import net.sf.jasperreports.engine.fill.FillExpressionEstimatedValues;
import net.sf.jasperreports.engine.fill.FillExpressionOldValues;
import net.sf.jasperreports.engine.fill.JREvaluator;
import net.sf.jasperreports.engine.fill.JRFillField;
import net.sf.jasperreports.engine.fill.JRFillParameter;
import net.sf.jasperreports.engine.fill.JRFillVariable;
import net.sf.jasperreports.engine.fill.SimpleTextExpressionEvaluator;
import net.sf.jasperreports.engine.type.ExpressionTypeEnum;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class SimpleTextEvaluators implements DirectExpressionEvaluators
{

	private ExpressionValues defaultValues;
	private ExpressionValues oldValues;
	private ExpressionValues estimatedValues;
	
	private Map<JRExpression, Evaluator> evaluators;
	
	public SimpleTextEvaluators()
	{
		this.evaluators = new HashMap<>();
	}

	@Override
	public void init(JREvaluator evaluator,
			Map<String, JRFillParameter> parametersMap, 
			Map<String, JRFillField> fieldsMap, 
			Map<String, JRFillVariable> variablesMap)
	{		
		defaultValues = new FillExpressionDefaultValues(evaluator, parametersMap, fieldsMap, variablesMap);
		oldValues =  new FillExpressionOldValues(evaluator, parametersMap, fieldsMap, variablesMap);
		estimatedValues = new FillExpressionEstimatedValues(evaluator, parametersMap, fieldsMap, variablesMap);
		
		this.evaluators.clear();
	}

	@Override
	public DirectExpressionEvaluator getEvaluator(JRExpression expression)
	{
		if (expression.getType() == ExpressionTypeEnum.SIMPLE_TEXT)
		{
			return evaluators.computeIfAbsent(expression, key -> new Evaluator(key));
		}
		
		return null;
	}
	
	protected class Evaluator implements DirectExpressionEvaluator
	{
		private final JRExpression expression;
		
		public Evaluator(JRExpression expression)
		{
			this.expression = expression;
		}
		
		@Override
		public Object evaluate()
		{
			return SimpleTextExpressionEvaluator.evaluateExpression(expression, defaultValues);
		}

		@Override
		public Object evaluateOld()
		{
			return SimpleTextExpressionEvaluator.evaluateExpression(expression, oldValues);
		}

		@Override
		public Object evaluateEstimated()
		{
			return SimpleTextExpressionEvaluator.evaluateExpression(expression, estimatedValues);
		}
	}

}

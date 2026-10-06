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
package net.sf.jasperreports.compilers;

import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionChunk;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.fill.ExpressionValues;
import net.sf.jasperreports.engine.fill.FillExpressionDefaultValues;
import net.sf.jasperreports.engine.fill.FillExpressionEstimatedValues;
import net.sf.jasperreports.engine.fill.FillExpressionOldValues;
import net.sf.jasperreports.engine.fill.JREvaluator;
import net.sf.jasperreports.engine.fill.JRFillField;
import net.sf.jasperreports.engine.fill.JRFillParameter;
import net.sf.jasperreports.engine.fill.JRFillVariable;
import net.sf.jasperreports.engine.fill.SimpleTextExpressionEvaluator;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class StandardExpressionEvaluators implements DirectExpressionEvaluators
{
	
	private static DirectExpressionEvaluator NULL_PLACEHOLDER = new UniformExpressionEvaluator()
	{
		@Override
		protected Object defaultEvaluate()
		{
			throw new UnsupportedOperationException();
		}
	};
	
	private Map<Integer, DirectExpressionEvaluation> directEvaluations;
	private DirectExpressionValueFilter valueFilter;
	
	private Map<Integer, DirectExpressionEvaluator> evaluators;
	
	private JREvaluator evaluator;
	private Map<String, JRFillParameter> parametersMap;
	private Map<String, JRFillField> fieldsMap;
	private Map<String, JRFillVariable> variablesMap;
	
	private ExpressionValues defaultValues;
	private ExpressionValues oldValues;
	private ExpressionValues estimatedValues;

	public StandardExpressionEvaluators(Map<Integer, DirectExpressionEvaluation> directEvaluations,
			DirectExpressionValueFilter valueFilter)
	{
		this.directEvaluations = directEvaluations;
		this.valueFilter = valueFilter;
		
		this.evaluators = new HashMap<>();
	}
	
	@Override
	public void init(JREvaluator evaluator, Map<String, JRFillParameter> parametersMap,
			Map<String, JRFillField> fieldsMap, Map<String, JRFillVariable> variablesMap)
	{
		this.evaluator = evaluator;
		this.parametersMap = parametersMap;
		this.fieldsMap = fieldsMap;
		this.variablesMap = variablesMap;
		
		this.defaultValues = new FillExpressionDefaultValues(evaluator, parametersMap, fieldsMap, variablesMap);
		this.oldValues =  new FillExpressionOldValues(evaluator, parametersMap, fieldsMap, variablesMap);
		this.estimatedValues = new FillExpressionEstimatedValues(evaluator, parametersMap, fieldsMap, variablesMap);
		
		this.evaluators.clear();
	}

	@Override
	public DirectExpressionEvaluator getEvaluator(JRExpression expression)
	{
		int expressionId = expression.getId();
		DirectExpressionEvaluator evaluator = evaluators.get(expressionId);
		if (evaluator == null)
		{
			DirectExpressionEvaluation directEvaluation = directEvaluation(expression.getId());
			if (directEvaluation == null)
			{
				evaluator = NULL_PLACEHOLDER;
			}
			else
			{
				evaluator = createDirectEvaluator(directEvaluation);
			}
			evaluators.put(expressionId, evaluator);
		}
		return evaluator == NULL_PLACEHOLDER ? null : evaluator;
	}
	
	protected DirectExpressionEvaluation directEvaluation(int expressionId)
	{
		DirectExpressionEvaluation directEvaluation = directEvaluations == null ? null 
				: directEvaluations.get(expressionId);
		return directEvaluation;
	}

	protected DirectExpressionEvaluator createDirectEvaluator(DirectExpressionEvaluation evaluation)
	{
		DirectExpressionEvaluator evaluator;
		switch (evaluation.getType())
		{
		case CONSTANT:
			Object expressionValue = ((ConstantExpressionEvaluation) evaluation).getValue();
			Object value = valueFilter.filterValue(expressionValue, null);
			evaluator = new DirectConstantEvaluator(value);
			break;
		case SIMPLE_TEXT:
			evaluator = new SimpleTextEvaluator(((SimpleTextEvaluation) evaluation).getChunks());
			break;
		case PARAMETER:
			String parameterName = ((ParameterEvaluation) evaluation).getName();
			JRFillParameter parameter = parametersMap.get(parameterName);
			if (parameter == null)
			{
				//shout not happen
				throw new JRRuntimeException("Did not find parameter " + parameterName);
			}
			evaluator = new DirectParameterEvaluator(parameter, valueFilter);
			break;
		case FIELD:
			String fieldName = ((FieldEvaluation) evaluation).getName();
			JRFillField field = fieldsMap.get(fieldName);
			if (field == null)
			{
				//shout not happen
				throw new JRRuntimeException("Did not find field " + fieldName);
			}
			evaluator = new DirectFieldEvaluator(field, valueFilter);
			break;
		case VARIABLE:
			String variableName = ((VariableEvaluation) evaluation).getName();
			JRFillVariable variable = variablesMap.get(variableName);
			if (variable == null)
			{
				//shout not happen
				throw new JRRuntimeException("Did not find variable " + variableName);
			}
			evaluator = new DirectVariableEvaluator(variable, valueFilter);
			break;
		case RESOURCE:
			String messageKey = ((ResourceEvaluation) evaluation).getMessageKey();
			Object message = valueFilter.filterValue(this.evaluator.str(messageKey), null);
			evaluator = new DirectConstantEvaluator(message);
			break;
		default:
			//should not happen
			throw new JRRuntimeException("Unknown direct expression evaluation type " + evaluation.getType());
		}
		return evaluator;
	}
	
	protected class SimpleTextEvaluator implements DirectExpressionEvaluator
	{
		private JRExpressionChunk[] chunks;
		
		public SimpleTextEvaluator(JRExpressionChunk[] chunks)
		{
			this.chunks = chunks;
		}

		@Override
		public Object evaluate()
		{
			return SimpleTextExpressionEvaluator.evaluate(chunks, defaultValues);
		}

		@Override
		public Object evaluateOld()
		{
			return SimpleTextExpressionEvaluator.evaluate(chunks, oldValues);
		}

		@Override
		public Object evaluateEstimated()
		{
			return SimpleTextExpressionEvaluator.evaluate(chunks, estimatedValues);
		}	
	}

}

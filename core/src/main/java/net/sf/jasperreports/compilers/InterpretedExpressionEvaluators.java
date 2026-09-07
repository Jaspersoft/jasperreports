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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionChunk;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.fill.JREvaluator;
import net.sf.jasperreports.engine.fill.JRFillField;
import net.sf.jasperreports.engine.fill.JRFillParameter;
import net.sf.jasperreports.engine.fill.JRFillVariable;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class InterpretedExpressionEvaluators implements DirectExpressionEvaluators
{

	private static final Log log = LogFactory.getLog(InterpretedExpressionEvaluators.class);

	private DirectExpressionValueFilter valueFilter;
	private DirectConstantEvaluator nullEvaluator;

	private JREvaluator evaluator;
	private Map<String, JRFillParameter> parametersMap;
	private Map<String, JRFillField> fieldsMap;
	private Map<String, JRFillVariable> variablesMap;
	
	private Map<JRExpression, DirectExpressionEvaluator> evaluators;

	public InterpretedExpressionEvaluators(DirectExpressionValueFilter valueFilter)
	{
		this.valueFilter = valueFilter;
		this.nullEvaluator = new DirectConstantEvaluator(valueFilter.filterValue(null, null));
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
		
		this.evaluators.clear();
	}

	@Override
	public DirectExpressionEvaluator getEvaluator(JRExpression expression)
	{
		if (expression.isInterpreted())
		{
			return evaluators.computeIfAbsent(expression, key -> createEvaluator(key));
		}
		return null;
	}

	protected DirectExpressionEvaluator createEvaluator(JRExpression expression)
	{
		DirectExpressionEvaluator evaluator = null;
		JRExpressionChunk[] chunks = expression.getChunks();
		if (chunks == null || chunks.length == 0)
		{
			evaluator = nullEvaluator;
		}
		else if (chunks.length == 1)
		{
			JRExpressionChunk chunk = chunks[0];
			String chunkText = chunk.getText();
			switch (chunk.getType())
			{
			case JRExpressionChunk.TYPE_PARAMETER:
				JRFillParameter parameter = parametersMap.get(chunkText);
				if (parameter != null)
				{
					evaluator = new DirectParameterEvaluator(parameter, valueFilter);
				}
				else
				{
					if (log.isDebugEnabled())
					{
						log.debug("Parameter " + chunkText + " not found for interpreted expression");
					}
					evaluator = nullEvaluator;
				}
				break;
			case JRExpressionChunk.TYPE_FIELD:
				JRFillField field = fieldsMap.get(chunkText);
				if (field != null)
				{
					evaluator = new DirectFieldEvaluator(field, valueFilter);
				}
				else
				{
					if (log.isDebugEnabled())
					{
						log.debug("Field " + chunkText + " not found for interpreted expression");
					}
					evaluator = nullEvaluator;
				}
				break;
			case JRExpressionChunk.TYPE_VARIABLE:
				JRFillVariable variable = variablesMap.get(chunkText);
				if (variable != null)
				{
					evaluator = new DirectVariableEvaluator(variable, valueFilter);
				}
				else
				{
					if (log.isDebugEnabled())
					{
						log.debug("Variable " + chunkText + " not found for interpreted expression");
					}
					evaluator = nullEvaluator;
				}
				break;
			case JRExpressionChunk.TYPE_RESOURCE:
				Object message = valueFilter.filterValue(this.evaluator.str(chunkText), null);
				evaluator = new DirectConstantEvaluator(message);
				break;
			default:
				break;
			}
		}

		if (evaluator == null)
		{
			//TODO lucianc throw JRExpressionEvalException?
			throw new JRRuntimeException("Interpreted expression " + expression.getText() 
					+ " is not a simple parameter/field/variable/resource message reference.");
		}

		return evaluator;
	}

}

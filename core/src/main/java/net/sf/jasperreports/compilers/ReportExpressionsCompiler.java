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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionChunk;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.type.ExpressionTypeEnum;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ReportExpressionsCompiler
{
	
	private static final ReportExpressionsCompiler INSTANCE = new ReportExpressionsCompiler();
	
	public static ReportExpressionsCompiler instance()
	{
		return INSTANCE;
	}

	private Map<String, DirectExpressionEvaluation> constantExpressionEvaluations;
	
	public ReportExpressionsCompiler()
	{
		constantExpressionEvaluations = constantEvaluations();
	}
	
	protected Map<String, DirectExpressionEvaluation> constantEvaluations()
	{
		Map<String, DirectExpressionEvaluation> constantEvaluations = new HashMap<>();
		//used by builtin variables
		constantEvaluations.put("new java.lang.Integer(0)", new ConstantExpressionEvaluation(Integer.valueOf(0)));
		constantEvaluations.put("new java.lang.Integer(1)", new ConstantExpressionEvaluation(Integer.valueOf(1)));
		//general constants
		constantEvaluations.put("false", new ConstantExpressionEvaluation(Boolean.FALSE));
		constantEvaluations.put("true", new ConstantExpressionEvaluation(Boolean.TRUE));
		constantEvaluations.put("Boolean.FALSE", new ConstantExpressionEvaluation(Boolean.FALSE));
		constantEvaluations.put("Boolean.TRUE", new ConstantExpressionEvaluation(Boolean.TRUE));
		constantEvaluations.put("\"\"", new ConstantExpressionEvaluation(""));
		return constantEvaluations;
	}
	
	public ReportExpressionsCompilation getExpressionsCompilation(JRExpressionCollector expressionCollector)
	{
		List<JRExpression> sourceExpressions = new ArrayList<>();
		
		Map<Integer, DirectExpressionEvaluation> directEvaluations = new HashMap<>();
		List<JRExpression> expressions = expressionCollector.getExpressions();
		for (JRExpression expression : expressions)
		{
			DirectExpressionEvaluation directEvaluation = directEvaluation(expression);
			if (directEvaluation == null)
			{
				sourceExpressions.add(expression);
			}
			else
			{
				Integer expressionId = expressionCollector.getExpressionId(expression);
				directEvaluations.put(expressionId, directEvaluation);
			}
		}
		return new ReportExpressionsCompilation(sourceExpressions,
				directEvaluations);
	}

	protected DirectExpressionEvaluation directEvaluation(JRExpression expression)
	{
		DirectExpressionEvaluation directEvaluation = null;
		if (expression.getType() == ExpressionTypeEnum.SIMPLE_TEXT)
		{
			directEvaluation = new SimpleTextEvaluation(expression.getChunks());
		}
		else
		{
			JRExpressionChunk[] chunks = expression.getChunks();
			if (chunks == null || chunks.length == 0)
			{
				directEvaluation = ConstantExpressionEvaluation.nullEvaluation();
			}
			else if (chunks.length == 1)
			{
				JRExpressionChunk chunk = chunks[0];
				switch (chunk.getType())
				{
				case JRExpressionChunk.TYPE_PARAMETER:
					directEvaluation = new ParameterEvaluation(chunk.getText());
					break;
				case JRExpressionChunk.TYPE_FIELD:
					directEvaluation = new FieldEvaluation(chunk.getText());
					break;
				case JRExpressionChunk.TYPE_VARIABLE:
					directEvaluation = new VariableEvaluation(chunk.getText());
					break;
				case JRExpressionChunk.TYPE_RESOURCE:
					directEvaluation = new ResourceEvaluation(chunk.getText());
					break;
				case JRExpressionChunk.TYPE_TEXT:
					directEvaluation = constantExpressionEvaluations.get(chunk.getText());
					break;
				default:
					break;
				}
			}
		}
		return directEvaluation;
	}

}

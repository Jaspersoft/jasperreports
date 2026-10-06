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

import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.design.JRDesignExpression;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class BuiltinExpressionEvaluatorFactory
{
	
	private final Map<JRExpression, BuiltinExpressionEvaluator> builtinEvaluators;

	public BuiltinExpressionEvaluatorFactory()
	{
		builtinEvaluators = new HashMap<>();
	}

	public JRDesignExpression createExpression(BuiltinExpressionEvaluator evaluator)
	{
		// we only need an empty expression object here
		// the evaluation logic is separate
		JRDesignExpression expression = new JRDesignExpression();
		builtinEvaluators.put(expression, evaluator);
		return expression;
	}

	public JRDesignExpression createConstantExpression(Object value)
	{
		ConstantBuiltinExpression evaluator = new ConstantBuiltinExpression(value);
		return createExpression(evaluator);
	}

	public DatasetExpressionEvaluator decorate(DatasetExpressionEvaluator evaluator)
	{
		if (builtinEvaluators.isEmpty())
		{
			return evaluator;
		}
		
		// use the builtin expression evaluators
		return new BuiltinExpressionEvaluatorDecorator(evaluator, builtinEvaluators);
	}

	public JRFillExpressionEvaluator decorate(JRFillExpressionEvaluator evaluator)
	{
		// always use the builtin expression evaluators, bultin expression can be added after decoration
		return new BuiltinFillExpressionEvaluatorDecorator(evaluator, builtinEvaluators);
	}
}

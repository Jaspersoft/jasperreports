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
package net.sf.jasperreports.json.expression.filter.evaluation;

import net.sf.jasperreports.json.JRJsonNode;
import net.sf.jasperreports.json.expression.EvaluationContext;
import net.sf.jasperreports.json.expression.filter.CompoundFilterExpression;

/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class CompoundFilterExpressionEvaluator implements FilterExpressionEvaluator {
	private EvaluationContext evaluationContext;
	private CompoundFilterExpression expression;


	public CompoundFilterExpressionEvaluator(EvaluationContext evaluationContext, CompoundFilterExpression expression) {
		this.evaluationContext = evaluationContext;
		this.expression = expression;
	}

	@Override
	public boolean evaluate(JRJsonNode contextNode) {
		FilterExpressionEvaluatorVisitor evaluatorVisitor = evaluationContext.getFilterExpressionEvaluatorVisitor();

		switch (expression.getLogicalOperator()) {
			case AND:
				return expression.getLeft().evaluate(contextNode, evaluatorVisitor)
						&& expression.getRight().evaluate(contextNode, evaluatorVisitor);
			case OR:
				return expression.getLeft().evaluate(contextNode, evaluatorVisitor)
						|| expression.getRight().evaluate(contextNode, evaluatorVisitor);
		}

		return false;
	}
}

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
package net.sf.jasperreports.json.expression.member.evaluation;

import net.sf.jasperreports.json.JsonNodeContainer;
import net.sf.jasperreports.json.expression.EvaluationContext;
import net.sf.jasperreports.json.expression.member.ArrayConstructionExpression;
import net.sf.jasperreports.json.expression.member.ArrayIndexExpression;
import net.sf.jasperreports.json.expression.member.ArraySliceExpression;
import net.sf.jasperreports.json.expression.member.MultiLevelUpExpression;
import net.sf.jasperreports.json.expression.member.ObjectConstructionExpression;
import net.sf.jasperreports.json.expression.member.ObjectKeyExpression;

/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class DefaultMemberExpressionEvaluatorVisitor implements MemberExpressionEvaluatorVisitor {
	private EvaluationContext evaluationContext;


	public DefaultMemberExpressionEvaluatorVisitor(EvaluationContext evaluationContext) {
		this.evaluationContext = evaluationContext;
	}

	@Override
	public JsonNodeContainer evaluateObjectKey(ObjectKeyExpression expression, JsonNodeContainer contextNode) {
		MemberExpressionEvaluator evaluator = new ObjectKeyExpressionEvaluator(evaluationContext, expression);
		return evaluator.evaluate(contextNode);
	}

	@Override
	public JsonNodeContainer evaluateMultiLevelUp(MultiLevelUpExpression expression, JsonNodeContainer contextNode) {
		MemberExpressionEvaluator evaluator = new MultiLevelUpExpressionEvaluator(evaluationContext, expression);
		return evaluator.evaluate(contextNode);
	}

	@Override
	public JsonNodeContainer evaluateArrayIndex(ArrayIndexExpression expression, JsonNodeContainer contextNode) {
		MemberExpressionEvaluator evaluator = new ArrayIndexExpressionEvaluator(evaluationContext, expression);
		return evaluator.evaluate(contextNode);
	}

	@Override
	public JsonNodeContainer evaluateArraySlice(ArraySliceExpression expression, JsonNodeContainer contextNode) {
		MemberExpressionEvaluator evaluator = new ArraySliceExpressionEvaluator(evaluationContext, expression);
		return evaluator.evaluate(contextNode);
	}

	@Override
	public JsonNodeContainer evaluateObjectConstruction(ObjectConstructionExpression expression, JsonNodeContainer contextNode) {
		MemberExpressionEvaluator evaluator = new ObjectConstructionExpressionEvaluator(evaluationContext, expression);
		return evaluator.evaluate(contextNode);
	}

	@Override
	public JsonNodeContainer evaluateArrayConstruction(ArrayConstructionExpression expression, JsonNodeContainer contextNode) {
		MemberExpressionEvaluator evaluator = new ArrayConstructionExpressionEvaluator(evaluationContext, expression);
		return evaluator.evaluate(contextNode);
	}

	public EvaluationContext getEvaluationContext() {
		return evaluationContext;
	}
}

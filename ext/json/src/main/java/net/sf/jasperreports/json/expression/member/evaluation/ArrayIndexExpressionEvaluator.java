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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import net.sf.jasperreports.json.JRJsonNode;
import net.sf.jasperreports.json.JsonNodeContainer;
import net.sf.jasperreports.json.expression.EvaluationContext;
import net.sf.jasperreports.json.expression.member.ArrayIndexExpression;
import net.sf.jasperreports.json.expression.member.MemberExpression;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class ArrayIndexExpressionEvaluator extends AbstractMemberExpressionEvaluator {
	private static final Log log = LogFactory.getLog(ArrayIndexExpressionEvaluator.class);

	private ArrayIndexExpression expression;

	public ArrayIndexExpressionEvaluator(EvaluationContext evaluationContext, ArrayIndexExpression expression) {
		super(evaluationContext);
		this.expression = expression;
	}

	@Override
	public JsonNodeContainer evaluate(JsonNodeContainer contextNode) {
		if (log.isDebugEnabled()) {
			log.debug("---> evaluating arrayIndex expression [" + expression +
					"] on a node with (size: " + contextNode.getSize() +
					", cSize: " + contextNode.getContainerSize() + ")");
		}

		JsonNodeContainer result = new JsonNodeContainer();

		switch (expression.getDirection()) {
			case DOWN:
				// this only make sense for containers with appropriate size
				if (expression.getIndex() >= 0 && expression.getIndex() < contextNode.getContainerSize()) {
					List<JRJsonNode> containerNodes = contextNode.getContainerNodes();
					JRJsonNode nodeAtIndex = containerNodes.get(expression.getIndex());

					if (applyFilter(nodeAtIndex)) {
						result.add(nodeAtIndex);
					}
				}
				break;
			case ANYWHERE_DOWN:
				List<JRJsonNode> nodes = contextNode.getContainerNodes();

				for (JRJsonNode node: nodes) {
					result.addNodes(goAnywhereDown(node));
				}

				break;
		}

		if (result.getSize() > 0) {
			return result;
		}

		return null;

	}

	@Override
	public MemberExpression getMemberExpression() {
		return expression;
	}

	private List<JRJsonNode> goAnywhereDown(JRJsonNode jrJsonNode) {
		List<JRJsonNode> result = new ArrayList<>();
		Deque<JRJsonNode> stack = new ArrayDeque<>();
		JsonNode initialDataNode = jrJsonNode.getDataNode();

		if (log.isDebugEnabled()) {
			log.debug("initial stack population with: " + initialDataNode);
		}

		// populate the stack initially
		stack.push(jrJsonNode);

		while (!stack.isEmpty()) {
			JRJsonNode stackNode = stack.pop();
			JsonNode stackDataNode = stackNode.getDataNode();

			addChildrenToStack(stackNode, stack);

			// process the current stack item
			if (stackDataNode.isArray()) {
				if (log.isDebugEnabled()) {
					log.debug("processing stack element: " + stackDataNode);
				}

				if (expression.getIndex() >= 0 && expression.getIndex() < stackDataNode.size()) {
					JsonNode nodeAtIndex = stackDataNode.get(expression.getIndex());
					JRJsonNode child = stackNode.createChild(nodeAtIndex);

					if (applyFilter(child)) {
						result.add(child);
					}
				}
			}
		}

		return result;
	}

}

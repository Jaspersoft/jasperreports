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
import net.sf.jasperreports.json.expression.member.ArraySliceExpression;
import net.sf.jasperreports.json.expression.member.MemberExpression;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class ArraySliceExpressionEvaluator extends AbstractMemberExpressionEvaluator {
	private static final Log log = LogFactory.getLog(ArraySliceExpressionEvaluator.class);

	private ArraySliceExpression expression;

	public ArraySliceExpressionEvaluator(EvaluationContext evaluationContext, ArraySliceExpression expression) {
		super(evaluationContext);
		this.expression = expression;
	}

	@Override
	public JsonNodeContainer evaluate(JsonNodeContainer contextNode) {
		if (log.isDebugEnabled()) {
			log.debug("---> evaluating expression [" + expression +
					"] on a node with (size: " + contextNode.getSize() +
					", cSize: " + contextNode.getContainerSize() + ")");
		}

		JsonNodeContainer result = new JsonNodeContainer();

		switch (expression.getDirection()) {
			case DOWN:
				Integer start = getSliceStart(contextNode.getContainerSize());
				if (start >= contextNode.getContainerSize()) {
					return null;
				}

				Integer end = getSliceEnd(contextNode.getContainerSize());
				if (end < 0) {
					return null;
				}

				if (log.isDebugEnabled()) {
					log.debug("start: " + start + ", end: " + end);
				}

				List<JRJsonNode> containerNodes = contextNode.getContainerNodes();

				for (int i = start; i < end; i++) {
					JRJsonNode nodeAtIndex = containerNodes.get(i);

					if (applyFilter(nodeAtIndex)) {
						result.add(nodeAtIndex);
					}
				}

				break;
			case ANYWHERE_DOWN:
				for (JRJsonNode node: contextNode.getContainerNodes()) {
					result.addNodes(goAnywhereDown(node));
				}

				break;
		}

		if (result.getSize() > 0) {
			return result;
		}

		return null;

	}

	private Integer getSliceStart(int containerSize) {
		Integer start = expression.getStart();

		if (start == null) {
			start = 0;
		} else if (start < 0) {
			start = containerSize + start;

			if (start < 0 ) {
				start = 0;
			}
		}

		return start;
	}

	private Integer getSliceEnd(int containerSize) {
		Integer end = expression.getEnd();

		if (end == null) {
			end = containerSize;
		} else if (end < 0) {
			end = containerSize + end;
		} else if (end > containerSize) {
			end = containerSize;
		}

		return end;
	}

	private List<JRJsonNode> goAnywhereDown(JRJsonNode jrJsonNode) {
		List<JRJsonNode> result = new ArrayList<>();
		Deque<JRJsonNode> stack = new ArrayDeque<>();

		if (log.isDebugEnabled()) {
			log.debug("initial stack population with: " + jrJsonNode.getDataNode());
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

				ArrayNode newNode = getEvaluationContext().getObjectMapper().createArrayNode();

				Integer start = getSliceStart(stackDataNode.size());
				if (start >= stackDataNode.size()) {
					continue;
				}

				Integer end = getSliceEnd(stackDataNode.size());
				if (end < 0) {
					continue;
				}

				for (int i = start; i < end; i++) {
					JRJsonNode nodeAtIndex = stackNode.createChild(stackDataNode.get(i));

					if (applyFilter(nodeAtIndex)) {
						newNode.add(nodeAtIndex.getDataNode());
					}
				}

				if (newNode.size() > 0) {
					result.add(stackNode.createChild(newNode));
				}
			}
		}

		return result;
	}

	@Override
	public MemberExpression getMemberExpression() {
		return expression;
	}

}

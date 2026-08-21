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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import net.sf.jasperreports.json.JRJsonNode;
import net.sf.jasperreports.json.JsonNodeContainer;
import net.sf.jasperreports.json.expression.EvaluationContext;
import net.sf.jasperreports.json.expression.member.MemberExpression;
import net.sf.jasperreports.json.expression.member.ObjectConstructionExpression;

/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class ObjectConstructionExpressionEvaluator extends AbstractMemberExpressionEvaluator {
	private static final Log log = LogFactory.getLog(ObjectConstructionExpressionEvaluator.class);

	private ObjectConstructionExpression expression;

	public ObjectConstructionExpressionEvaluator(EvaluationContext evaluationContext, ObjectConstructionExpression expression) {
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
				for (JRJsonNode node: contextNode.getNodes()) {
					result.addNodes(goDown(node));
				}

				break;
			case ANYWHERE_DOWN:
				for (JRJsonNode node: contextNode.getNodes()) {
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

	private List<JRJsonNode> goDown(JRJsonNode jrJsonNode) {
		List<JRJsonNode> result = new ArrayList<>();
		JsonNode dataNode = jrJsonNode.getDataNode();

		// advance into object
		if (dataNode.isObject()) {
			JRJsonNode deeperNode = constructNewObjectNodeWithKeys(jrJsonNode);
			if (deeperNode != null) {
				result.add(deeperNode);
			}
		}
		// advance into array
		else if (dataNode.isArray()) {
			for (JsonNode node : dataNode) {
				JRJsonNode childWithKeys = constructNewObjectNodeWithKeys(jrJsonNode.createChild(node));

				if (childWithKeys != null) {
					result.add(childWithKeys);
				}
			}
		}

		return result;
	}

	private JRJsonNode constructNewObjectNodeWithKeys(JRJsonNode from) {
		ObjectNode newNode = getEvaluationContext().getObjectMapper().createObjectNode();

		for (String objectKey: expression.getObjectKeys()) {
			JsonNode deeperNode = from.getDataNode().get(objectKey);

			if (deeperNode != null && (deeperNode.isObject() || deeperNode.isValueNode() || deeperNode.isArray())) {
				JRJsonNode deeperChild = from.createChild(deeperNode);

				if (applyFilter(deeperChild)) {
					newNode.set(objectKey, deeperNode);
				}
			}
		}

		if (newNode.size() > 0) {
			return from.createChild(newNode);
		}

		return null;
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

			if (log.isDebugEnabled()) {
				log.debug("processing stack element: " + stackDataNode);
			}

			// process the current stack item
			if (stackDataNode.isObject()) {
				JRJsonNode childWithKeys = constructNewObjectNodeWithKeys(stackNode);

				if (childWithKeys != null) {
					result.add(childWithKeys);
				}
			}
		}

		return result;
	}

}

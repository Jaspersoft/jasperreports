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

import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.json.JRJsonNode;
import net.sf.jasperreports.json.expression.EvaluationContext;
import net.sf.jasperreports.json.expression.member.MemberExpression;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public abstract class AbstractMemberExpressionEvaluator implements MemberExpressionEvaluator {
	private static final Log log = LogFactory.getLog(AbstractMemberExpressionEvaluator.class);

	private EvaluationContext evaluationContext;


	public AbstractMemberExpressionEvaluator(EvaluationContext evaluationContext) {
		this.evaluationContext = evaluationContext;
	}

	public abstract MemberExpression getMemberExpression();

	public EvaluationContext getEvaluationContext() {
		return evaluationContext;
	}

	protected boolean applyFilter(JRJsonNode node) {
		if (log.isDebugEnabled()) {
			log.debug("apply filter - " + getMemberExpression().getFilterExpression() + " - to: " + node);
		}

		if (getMemberExpression().getFilterExpression() != null) {
			boolean evalResult = getMemberExpression().getFilterExpression().evaluate(node, evaluationContext.getFilterExpressionEvaluatorVisitor());

			if (log.isDebugEnabled()) {
				log.debug("filter returns " + evalResult);
			}

			return evalResult;
		}

		if (log.isDebugEnabled()) {
			log.debug("no filter expression, returns true");
		}

		return true;
	}

	protected List<JRJsonNode> filterArrayNode(JRJsonNode parent, ArrayNode childArray) {
		return filterArrayNode(parent, childArray, null);
	}

	protected List<JRJsonNode> filterArrayNode(JRJsonNode parent, ArrayNode childArray, String deeperKey) {
		return filterArrayNode(parent, childArray, deeperKey, false);
	}

	protected List<JRJsonNode> filterArrayNode(JRJsonNode parent, ArrayNode childArray, String deeperKey, boolean keepArrayContainment) {
		if (log.isDebugEnabled()) {
			log.debug("filtering array: " + childArray + "; deeperKey: " + deeperKey + "; keepArrayContainment: " + keepArrayContainment);
		}

		List<JRJsonNode> result = new ArrayList<>();
		ArrayNode container = null;

		if (keepArrayContainment) {
			container = evaluationContext.getObjectMapper().createArrayNode();
		}

		for (JsonNode current : childArray) {
			if (deeperKey != null) {
				JsonNode deeperNode = current.get(deeperKey);

				if (deeperNode != null) {
					JRJsonNode currentParent = parent.createChild(current);
					JRJsonNode currentChild = currentParent.createChild(deeperNode);

					if (applyFilter(currentChild)) {
						if (keepArrayContainment) {
							container.add(deeperNode);
						} else {
							result.add(currentChild);
						}
					}
				}
			} else {
				JRJsonNode currentChild = parent.createChild(current);

				if (applyFilter(currentChild)) {
					if (keepArrayContainment) {
						container.add(current);
					} else {
						result.add(currentChild);
					}
				}
			}
		}

		if (keepArrayContainment && container.size() > 0) {
			result.add(parent.createChild(container));
		}

		return result;
	}

	protected void addChildrenToStack(JRJsonNode stackNode, Deque<JRJsonNode> stack) {
		JsonNode stackDataNode = stackNode.getDataNode();

		if (stackDataNode.isObject()) {
			Iterator<Map.Entry<String, JsonNode>> it = stackDataNode.fields();

			while (it.hasNext()) {
				JsonNode current = it.next().getValue();
				stack.addLast(stackNode.createChild(current));
			}
		}
		// if array => push all children
		else if (stackDataNode.isArray()) {
			for (JsonNode deeper: stackDataNode) {
				stack.addLast(stackNode.createChild(deeper));
			}
		}
	}

}

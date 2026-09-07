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

import java.math.BigDecimal;

import net.sf.jasperreports.engine.type.JsonOperatorEnum;
import net.sf.jasperreports.json.JRJsonNode;
import net.sf.jasperreports.json.JsonNodeContainer;
import net.sf.jasperreports.json.expression.EvaluationContext;
import net.sf.jasperreports.json.expression.filter.BasicFilterExpression;
import net.sf.jasperreports.json.expression.filter.FilterExpression;
import net.sf.jasperreports.json.expression.filter.ValueDescriptor;
import net.sf.jasperreports.json.expression.member.MemberExpression;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class BasicFilterExpressionEvaluator implements FilterExpressionEvaluator {
	private static final Log log = LogFactory.getLog(BasicFilterExpressionEvaluator.class);

	private EvaluationContext evaluationContext;
	private BasicFilterExpression expression;


	public BasicFilterExpressionEvaluator(EvaluationContext evaluationContext, BasicFilterExpression expression) {
		this.evaluationContext = evaluationContext;
		this.expression = expression;
	}

	@Override
	public boolean evaluate(JRJsonNode jsonNode) {
		JsonNodeContainer memberEval = new JsonNodeContainer(jsonNode);
		boolean result = false;

		if (log.isDebugEnabled()) {
			log.debug("filtering (" + this.expression + ") to: " + jsonNode);
		}

		// traverse the members
		outer: for (MemberExpression me: expression.getMemberExpressionList()) {
			memberEval = me.evaluate(memberEval, evaluationContext.getMemberExpressionEvaluatorVisitorForFilter());

			// exit on first null
			if (memberEval == null) {
				if (log.isDebugEnabled()) {
					log.debug("result is null");
				}
				return false;
			}

			// break when hitting a missing node; this will allow filtering for missing keys
			if (memberEval.getSize() == 1 && memberEval.getFirst().getDataNode().isMissingNode()) {
				if (log.isDebugEnabled()) {
					log.debug("hit missing node");
				}
				break outer;
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("done filter members' eval => node with (size: " + memberEval.getSize() +
					", cSize: " + memberEval.getContainerSize() + ")");
		}

		// check for null first
		if (expression.isNullFunction()) {
			if (log.isDebugEnabled()) {
				log.debug("expression isNullFunction");
			}

			if (memberEval.getSize() == 1 &&
					(memberEval.getFirst().getDataNode().isNull() ||
							memberEval.getFirst().getDataNode().isMissingNode())) {
				result = true;
			}
		}
		// check for not null on everything except null and missing nodes
		else if (expression.isNotNullFunction()) {
			if (log.isDebugEnabled()) {
				log.debug("expression isNotNullFunction");
			}

			if (memberEval.getSize() > 1 ||
					memberEval.getSize() == 1 &&
							!(memberEval.getFirst().getDataNode().isNull() ||
									memberEval.getFirst().getDataNode().isMissingNode())) {
				result = true;
			}
		}
		else if (expression.isArrayFunction()) {
			if (log.isDebugEnabled()) {
				log.debug("expression isArrayFunction");
			}

			if (memberEval.getSize() > 1 ||
					memberEval.getSize() == 1 && memberEval.getFirst().getDataNode().isArray()) {
				return true;
			}
		}
		else if (expression.isObjectFunction()) {
			if (log.isDebugEnabled()) {
				log.debug("expression isObjectFunction");
			}

			if (memberEval.getSize() == 1 && memberEval.getFirst().getDataNode().isObject()) {
				return true;
			}
		}
		else if (expression.isValueFunction()) {
			if (log.isDebugEnabled()) {
				log.debug("expression isValueFunction");
			}

			if (memberEval.getSize() == 1 && memberEval.getFirst().getDataNode().isValueNode()) {
				return true;
			}
		}
		// the size is only checked on an array object
		else if (expression.isSizeFunction()) {
			if (log.isDebugEnabled()) {
				log.debug("expression isSizeFunction");
			}

			if (memberEval.getSize() > 1 ||
					memberEval.getSize() == 1 && memberEval.getFirst().getDataNode().isArray()) {
				result = applySizeOperator(memberEval.getContainerSize());
			}
		}
		// else perform the filtering only for value/missing nodes
		else if (memberEval.getSize() == 1 &&
				(memberEval.getFirst().getDataNode().isValueNode() || memberEval.getFirst().getDataNode().isMissingNode())) {

			result = applyOperator(memberEval.getFirst().getDataNode());
		}

		if (log.isDebugEnabled()) {
			log.debug("filter result is: " + result);
		}

		return result;
	}

	protected boolean applySizeOperator(int size) {
		if (expression.getValueDescriptor().getType() == FilterExpression.VALUE_TYPE.INTEGER) {
			int operand = Integer.parseInt(expression.getValueDescriptor().getValue());

			switch (expression.getOperator()) {
				case EQ:
					return size == operand;
				case NE:
					return size != operand;
				case GT:
					return size > operand;
				case GE:
					return size >= operand;
				case LT:
					return size < operand;
				case LE:
					return size <= operand;
				default:
			}
		}

		return false;
	}

	protected boolean applyOperator(JsonNode valueNode) {
		ValueDescriptor valueDescriptor = expression.getValueDescriptor();
		JsonOperatorEnum operator = expression.getOperator();
		FilterExpression.VALUE_TYPE type = valueDescriptor.getType();

		// do null comparison first
		if (FilterExpression.VALUE_TYPE.NULL.equals(type)) {
			switch (operator) {
				case EQ:
					return valueNode.isNull() || valueNode.isMissingNode();
				case NE:
					return !(valueNode.isNull() || valueNode.isMissingNode());
				default:
			}
		} else {
			// compare numbers with numbers
			if (valueNode.isNumber() &&
					(FilterExpression.VALUE_TYPE.INTEGER.equals(type) || FilterExpression.VALUE_TYPE.DOUBLE.equals(type))) {

				BigDecimal opRight = new BigDecimal(valueDescriptor.getValue());
				BigDecimal opLeft;

				if (valueNode.isBigDecimal()) {
					opLeft = valueNode.decimalValue();
				} else {
					opLeft = new BigDecimal(valueNode.asText());
				}

				switch (operator) {
					case EQ:
						return opLeft.compareTo(opRight) == 0;
					case NE:
						return opLeft.compareTo(opRight) != 0;
					case GT:
						return opLeft.compareTo(opRight) > 0;
					case GE:
						return opLeft.compareTo(opRight) >= 0;
					case LT:
						return opLeft.compareTo(opRight) < 0;
					case LE:
						return opLeft.compareTo(opRight) <= 0;
					default:
				}
			}
			// compare strings with strings
			else if (valueNode.isTextual() && FilterExpression.VALUE_TYPE.STRING.equals(type)) {
				switch (operator) {
					case EQ:
						return valueNode.textValue().equals(valueDescriptor.getValue());
					case NE:
						return !valueNode.textValue().equals(valueDescriptor.getValue());
					case CONTAINS:
						return valueNode.textValue().contains(valueDescriptor.getValue());
					default:
				}
			}
			// compare booleans with booleans
			else if (valueNode.isBoolean() && FilterExpression.VALUE_TYPE.BOOLEAN.equals(type)) {
				switch (operator) {
					case EQ:
						return valueNode.booleanValue() == Boolean.parseBoolean(valueDescriptor.getValue());
					case NE:
						return valueNode.booleanValue() != Boolean.parseBoolean(valueDescriptor.getValue());
					default:
				}
			}
		}

		return false;
	}
}

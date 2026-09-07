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
package net.sf.jasperreports.json.expression.filter;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.type.JsonOperatorEnum;
import net.sf.jasperreports.json.JRJsonNode;
import net.sf.jasperreports.json.expression.filter.evaluation.FilterExpressionEvaluatorVisitor;
import net.sf.jasperreports.json.expression.member.MemberExpression;

/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class BasicFilterExpression implements FilterExpression {
	private List<MemberExpression> memberExpressionList = new ArrayList<>();
	private JsonOperatorEnum operator;
	private ValueDescriptor valueDescriptor;
	private boolean isSizeFunction;
	private boolean isNullFunction;
	private boolean isNotNullFunction;
	private boolean isArrayFunction;
	private boolean isObjectFunction;
	private boolean isValueFunction;


	public BasicFilterExpression() {
	}

	@Override
	public boolean evaluate(JRJsonNode jsonNode, FilterExpressionEvaluatorVisitor evaluator) {
		return evaluator.evaluateBasicFilter(this, jsonNode);
	}

	public List<MemberExpression> getMemberExpressionList() {
		return memberExpressionList;
	}

	public void addMemberExpression(MemberExpression memberExpression) {
		memberExpressionList.add(memberExpression);
	}

	public JsonOperatorEnum getOperator() {
		return operator;
	}

	public void setOperator(JsonOperatorEnum operator) {
		this.operator = operator;
	}

	public ValueDescriptor getValueDescriptor() {
		return valueDescriptor;
	}

	public void setValueDescriptor(ValueDescriptor valueDescriptor) {
		this.valueDescriptor = valueDescriptor;
	}

	public boolean isSizeFunction() {
		return isSizeFunction;
	}

	public void setIsSizeFunction(boolean isSizeFunction) {
		this.isSizeFunction = isSizeFunction;
	}

	public boolean isNullFunction() {
		return isNullFunction;
	}

	public void setIsNullFunction(boolean isNullFunction) {
		this.isNullFunction = isNullFunction;
	}

	public boolean isNotNullFunction() {
		return isNotNullFunction;
	}

	public void setIsNotNullFunction(boolean isNotNullFunction) {
		this.isNotNullFunction = isNotNullFunction;
	}

	public boolean isArrayFunction() {
		return isArrayFunction;
	}

	public void setIsArrayFunction(boolean isArrayFunction) {
		this.isArrayFunction = isArrayFunction;
	}

	public boolean isObjectFunction() {
		return isObjectFunction;
	}

	public void setIsObjectFunction(boolean isObjectFunction) {
		this.isObjectFunction = isObjectFunction;
	}

	public boolean isValueFunction() {
		return isValueFunction;
	}

	public void setIsValueFunction(boolean isValueFunction) {
		this.isValueFunction = isValueFunction;
	}

	@Override
	public String toString() {
		String result = "";

		for (MemberExpression me: memberExpressionList) {
			result += me.toString() + " ";
		}

		if (isSizeFunction) {
			result += "isSizeFn";
		} else if (isValueFunction) {
			result += "isValueFn";
		} else if (isArrayFunction) {
			result += "isArrayFn";
		} else if (isNotNullFunction) {
			result += "isNotNullFn";
		} else if (isObjectFunction) {
			result += "isObjectFn";
		}

		return result + " " + operator + " " + valueDescriptor;
	}
}

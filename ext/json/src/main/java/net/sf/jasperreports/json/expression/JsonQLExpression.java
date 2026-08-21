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
package net.sf.jasperreports.json.expression;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.json.expression.member.MemberExpression;

/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class JsonQLExpression {
	private List<MemberExpression> memberExpressionList = new ArrayList<>();
	private boolean isAbsolute;

	public void addMemberExpression(MemberExpression memberExpression) {
		memberExpressionList.add(memberExpression);
	}

	public List<MemberExpression> getMemberExpressionList() {
		return memberExpressionList;
	}

	public boolean isAbsolute() {
		return isAbsolute;
	}

	public void setIsAbsolute(boolean isAbsolute) {
		this.isAbsolute = isAbsolute;
	}

	@Override
	public String toString() {
		String result = (isAbsolute ? "" : "NON ") + "absolute pathExpression: \n";

		for (MemberExpression me: memberExpressionList) {
			result += me + "\n";
		}

		return result;
	}
}

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
package net.sf.jasperreports.types.date;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.query.JRClauseFunction;
import net.sf.jasperreports.engine.query.JRClauseTokens;
import net.sf.jasperreports.engine.query.JRQueryClauseContext;
import net.sf.jasperreports.engine.query.JRSqlAbstractEqualClause;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public abstract class DateRangeBaseSQLEqualityClause implements JRClauseFunction
{
	private static final String EXCEPTION_MESSAGE_KEY_EQUAL_CLAUSE_DB_COLUMN_TOKEN_MISSING = "date.range.equal.clause.db.column.token.missing";
	private static final String EXCEPTION_MESSAGE_KEY_EQUAL_CLAUSE_PARAMETER_TOKEN_MISSING = "date.range.equal.clause.parameter.token.missing";
	private static final String EXCEPTION_MESSAGE_KEY_UNEXPECTED_PARAMETER_TYPE = "date.range.unexpected.parameter.type";
	
	protected DateRangeBaseSQLEqualityClause()
	{
	}
	
	@Override
	public void apply(JRClauseTokens clauseTokens, JRQueryClauseContext queryContext)
	{
		String clauseId = clauseTokens.getClauseId();
		String col = clauseTokens.getToken(JRSqlAbstractEqualClause.POSITION_DB_COLUMN);
		String param = clauseTokens.getToken(JRSqlAbstractEqualClause.POSITION_PARAMETER);
		
		if (col == null)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_EQUAL_CLAUSE_DB_COLUMN_TOKEN_MISSING,
					(Object[])null);
		}
		
		if (param == null)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_EQUAL_CLAUSE_PARAMETER_TOKEN_MISSING,
					(Object[])null);
		}
		
		Object paramValue = queryContext.getValueParameter(param).getValue();
		if (paramValue != null && !(paramValue instanceof DateRange))
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_UNEXPECTED_PARAMETER_TYPE,
					new Object[]{param, clauseId});
		}
		
		DateRange dateRange = (DateRange) paramValue;
		StringBuffer queryBuffer = queryContext.queryBuffer();
		queryBuffer.append('(');
		applyDateRange(queryContext, col, dateRange);
		queryBuffer.append(')');
	}

	protected abstract void applyDateRange(JRQueryClauseContext queryContext,
			String column, DateRange dateRange);

}

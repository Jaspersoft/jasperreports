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
package net.sf.jasperreports.engine.query;

import net.sf.jasperreports.engine.JRRuntimeException;


/**
 * Base (NOT) EQUAL clause function for SQL queries.
 * <p>
 * The first token in the $X{...} syntax is the function ID token. Possible values for 
 * the (NOT) EQUAL clause function ID token are:
 * <ul>
 * 	<li>EQUAL</li>
 * 	<li>NOTEQUAL</li>
 * </ul>
 * </p>
 * 
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public abstract class JRSqlAbstractEqualClause implements JRClauseFunction
{
	private static final String EXCEPTION_MESSAGE_KEY_QUERY_EQUAL_CLAUSE_DB_COLUMN_TOKEN_MISSING = "query.equal.clause.db.column.token.missing";
	private static final String EXCEPTION_MESSAGE_KEY_QUERY_EQUAL_CLAUSE_PARAMETER_TOKEN_MISSING = "query.equal.clause.parameter.token.missing";
	
	public static final int POSITION_DB_COLUMN = 1;
	public static final int POSITION_PARAMETER = 2;

	protected JRSqlAbstractEqualClause()
	{
	}

	/**
	 * Creates a (NOT) EQUAL SQL clause.
	 * 
	 * <p>
	 * The method expects two clause tokens (after the ID token):
	 * <ul>
	 * 	<li>The first token is the SQL column (or column combination) to be used in the clause.</li>
	 * 	<li>The second token is the name of the report parameter that contains the value to compare to.</li>
	 * </ul>
	 * </p>
	 * <p>
	 * The EQUAL function constructs either a <code>column = ?</code> or an 
	 * <code>column IS NULL</code> clause, depending on the parameter's value.
	 * </p>
	 * <p>
	 * The NOTEQUAL function constructs either a <code>column &lt;&gt; ?</code> or an 
	 * <code>column IS NOT NULL</code> clause, depending on the parameter's value.
	 * </p>
	 */
	@Override
	public void apply(JRClauseTokens clauseTokens, JRQueryClauseContext queryContext)
	{
		String col = clauseTokens.getToken(POSITION_DB_COLUMN);
		String param = clauseTokens.getToken(POSITION_PARAMETER);
		
		if (col == null)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_QUERY_EQUAL_CLAUSE_DB_COLUMN_TOKEN_MISSING,
					(Object[])null);
		}
		
		if (param == null)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_QUERY_EQUAL_CLAUSE_PARAMETER_TOKEN_MISSING,
					(Object[])null);
		}
		
		StringBuffer sbuffer = queryContext.queryBuffer();
		sbuffer.append(col);
		sbuffer.append(' ');
		handleEqualOperator(sbuffer, param, queryContext);
	}
	
	/**
	 * Finalizes the query string 
	 * 
	 * @param sbuffer
	 * @param param
	 * @param queryContext
	 */
	protected void finalizeClause(StringBuffer sbuffer, String param, JRQueryClauseContext queryContext)
	{
		sbuffer.append(' ');
		sbuffer.append('?');
		queryContext.addQueryParameter(param);
	}

	protected abstract void handleEqualOperator(StringBuffer sBuffer, String param, JRQueryClauseContext queryContext);
}

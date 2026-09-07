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
 * Base BETWEEN clause function for SQL queries. 
 * <p>
 * The purpose of this clause function is to find that a given 
 * value can be found in a given A...B interval.
 * </p>
 * <p>
 * The first token in the $X{...} syntax is the function ID token. Possible values for 
 * the BETWEEN clause function ID token are:
 * <ul>
 * <li><code>BETWEEN</code> - in this case the A...B interval will be considered open: (A,B)</li>
 * <li><code>[BETWEEN</code> - in this case the A...B interval will be considered right-open: [A,B)</li>
 * <li><code>BETWEEN]</code> - in this case the A...B interval will be considered left-open: (A,B]</li>
 * <li><code>[BETWEEN]</code> - in this case the A...B interval will be considered closed: [A,B]</li>
 * </ul> 
 * </p>
 * 
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public abstract class SQLBetweenBaseClause implements JRClauseFunction
{
	public static final String EXCEPTION_MESSAGE_KEY_QUERY_BETWEEN_CLAUSE_DB_COLUMN_TOKEN_MISSING = "query.between.clause.db.column.token.missing";
	public static final String EXCEPTION_MESSAGE_KEY_QUERY_BETWEEN_CLAUSE_LEFT_PARAMETER_TOKEN_MISSING = "query.between.clause.left.parameter.token.missing";
	public static final String EXCEPTION_MESSAGE_KEY_QUERY_BETWEEN_CLAUSE_NAME_TOKEN_MISSING = "query.between.clause.name.token.missing";
	public static final String EXCEPTION_MESSAGE_KEY_QUERY_BETWEEN_CLAUSE_RIGHT_PARAMETER_TOKEN_MISSING = "query.between.clause.right.parameter.token.missing";
	
	protected static final int POSITION_CLAUSE_ID = 0;
	protected static final int POSITION_DB_COLUMN = 1;
	protected static final int POSITION_LEFT_PARAMETER = 2;
	protected static final int POSITION_RIGHT_PARAMETER = 3;


	/**
	 * Creates a BETWEEN-like SQL clause, depending on the clause ID.
	 * 
	 * <p>
	 * The method expects three clause tokens (after the ID token):
	 * <ul>
	 * 	<li>The first token is the SQL column (or column combination) to be used in the clause.</li>
	 * 	<li>The second token is the name of the parameter that contains the left member value.</li>
	 * 	<li>The second token is the name of the parameter that contains the right member value.</li>
	 * </ul>
	 * </p>
	 * <p>
	 * The method constructs one of the following clauses:
	 * <ul>
	 * <li><code>(column &gt; ? AND column &lt; ?)</code> if the clause ID is <code>BETWEEN</code></li>
	 * <li><code>(column &gt;= ? AND column &lt; ?)</code> if the clause ID is <code>[BETWEEN</code></li>
	 * <li><code>(column &gt; ? AND column &lt;= ?)</code> if the clause ID is <code>BETWEEN]</code></li>
	 * <li><code>(column &gt;= ? AND column &lt;= ?)</code> if the clause ID is <code>[BETWEEN]</code></li>
	 * </ul> 
	 * If the left member value is null, one of the following clauses will be generated:
	 * <ul>
	 * <li><code>column &lt; ?</code> if the clause ID is <code>BETWEEN</code> or <code>BETWEEN</code></li>
	 * <li><code>column &lt;= ?</code> if the clause ID is <code>BETWEEN]</code> or <code>[BETWEEN]</code></li>
	 * </ul> 
	 * If the right member value is null, one of the following clauses will be generated:
	 * <ul>
	 * <li><code>column &gt; ?</code> if the clause ID is <code>BETWEEN</code> or <code>BETWEEN]</code></li>
	 * <li><code>column &gt;= ?</code> if the clause ID is <code>[BETWEEN</code> or <code>[BETWEEN]</code></li>
	 * </ul> 
	 * If the both left and right member values are null, the method generates a SQL clause that 
	 * will always evaluate to true (e.g. <code>0 = 0</code>).
	 * </p>
	 * @param clauseTokens
	 * @param queryContext
	 */
	@Override
	public void apply(JRClauseTokens clauseTokens, JRQueryClauseContext queryContext)
	{
		String clauseId = clauseTokens.getToken(POSITION_CLAUSE_ID);
		String col = clauseTokens.getToken(POSITION_DB_COLUMN);
		String leftParam = clauseTokens.getToken(POSITION_LEFT_PARAMETER);
		String rightParam = clauseTokens.getToken(POSITION_RIGHT_PARAMETER);

		if (clauseId == null)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_QUERY_BETWEEN_CLAUSE_NAME_TOKEN_MISSING,
					(Object[])null);
		}
		
		if (col == null)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_QUERY_BETWEEN_CLAUSE_DB_COLUMN_TOKEN_MISSING,
					(Object[])null);
		}
		
		if (leftParam == null)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_QUERY_BETWEEN_CLAUSE_LEFT_PARAMETER_TOKEN_MISSING,
					(Object[])null);
		}
		
		if (rightParam == null)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_QUERY_BETWEEN_CLAUSE_RIGHT_PARAMETER_TOKEN_MISSING,
					(Object[])null);
		}
		
		ClauseFunctionParameterHandler leftParamHandler = createParameterHandler(queryContext, clauseId, leftParam, true);
		ClauseFunctionParameterHandler rightParamHandler = createParameterHandler(queryContext, clauseId, rightParam, false);
		StringBuffer sbuffer = queryContext.queryBuffer();
		
		if (leftParamHandler.hasValue() && rightParamHandler.hasValue())
		{
			sbuffer.append("( ");
			handleGreaterClause(sbuffer, clauseId, col, leftParamHandler, queryContext);
			sbuffer.append(" AND ");
			handleLessClause(sbuffer, clauseId, col, rightParamHandler, queryContext);
			sbuffer.append(" )");
		}
		else if (!leftParamHandler.hasValue())
		{
			if (!rightParamHandler.hasValue())
			{
				sbuffer.append(CLAUSE_TRUISM);
			}
			else
			{
				handleLessClause(sbuffer, clauseId, col, rightParamHandler, queryContext);
			}
		}
		else 
		{
			handleGreaterClause(sbuffer, clauseId, col, leftParamHandler, queryContext);
		}
	}
	
	protected abstract ClauseFunctionParameterHandler createParameterHandler(JRQueryClauseContext queryContext, 
			String clauseId, String parameterName, boolean left);

	
	/**
	 * 
	 * @param clauseId the clause ID
	 * @return the '<code>&gt;</code>' or '<code>&gt;=</code>' sign
	 */
	protected String getGreaterOperator(String clauseId)
	{
		return isLeftClosed(clauseId) ? ">=" : ">";
	}

	protected boolean isLeftClosed(String clauseId)
	{
		return clauseId.startsWith("[");
	}
	
	/**
	 * 
	 * @param clauseId the clause ID
	 * @return the '<code>&lt;</code>' or '<code>&lt;=</code>' sign
	 */
	protected String getLessOperator(String clauseId)
	{
		return isRightClosed(clauseId) ? "<=" : "<";
	}

	protected boolean isRightClosed(String clauseId)
	{
		return clauseId.endsWith("]");
	}
	
	/**
	 * Generates either a '<code>column &gt; ?</code>' or a '<code>column &gt;= ?</code>' clause
	 * 
	 * @param sbuffer the StringBuffer that contains the generated query
	 * @param clauseId the clause ID
	 * @param col the name of the column, or a column names combination  
	 * @param leftParamHandler the parameter handler that contains the left member value
	 * @param queryContext the query context
	 */
	protected void handleGreaterClause(
			StringBuffer sbuffer, 
			String clauseId, 
			String col, 
			ClauseFunctionParameterHandler leftParamHandler, 
			JRQueryClauseContext queryContext
			)
	{
		sbuffer.append(col);
		sbuffer.append(' ');
		sbuffer.append(getGreaterOperator(clauseId));
		sbuffer.append(' ');
		sbuffer.append('?');
		
		leftParamHandler.addQueryParameter();
	}
	
	/**
	 * Generates either a '<code>column &lt; ?</code>' or a '<code>column &lt;= ?</code>' clause
	 * 
	 * @param sbuffer the StringBuffer that contains the generated query
	 * @param clauseId the clause ID
	 * @param col the name of the column, or a column names combination  
	 * @param rightParamHandler
	 * @param queryContext the query context
	 */
	protected void handleLessClause(
			StringBuffer sbuffer, 
			String clauseId, 
			String col, 
			ClauseFunctionParameterHandler rightParamHandler, 
			JRQueryClauseContext queryContext
			)
	{
		sbuffer.append(col);
		sbuffer.append(' ');
		sbuffer.append(getLessOperator(clauseId));
		sbuffer.append(' ');
		sbuffer.append('?');
		
		rightParamHandler.addQueryParameter();
	}
}

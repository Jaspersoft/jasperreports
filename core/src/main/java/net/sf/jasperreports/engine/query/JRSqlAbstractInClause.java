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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;


/**
 * Base (NOT) IN clause function for SQL queries.
 * 
 * <p>
 * The first token in the $X{...} syntax is the function ID token. Possible values for 
 * the (NOT) IN clause function ID token are:
 * <ul>
 * <li><code>IN</code></li>
 * <li><code>NOTIN</code></li>
 * </ul>
 * </p> 
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public abstract class JRSqlAbstractInClause implements JRClauseFunction
{
	public static final String EXCEPTION_MESSAGE_KEY_QUERY_IN_CLAUSE_DB_COLUMN_TOKEN_MISSING = "query.in.clause.db.column.token.missing";
	public static final String EXCEPTION_MESSAGE_KEY_QUERY_IN_CLAUSE_INVALID_PARAMETER_TYPE = "query.in.clause.invalid.parameter.type";
	public static final String EXCEPTION_MESSAGE_KEY_QUERY_IN_CLAUSE_PARAMETER_TOKEN_MISSING = "query.in.clause.parameter.token.missing";
	
	protected static final int POSITION_DB_COLUMN = 1;
	protected static final int POSITION_PARAMETER = 2;
	protected static final int POSITION_NO_VALUES_RESULT = 3;
	
	protected JRSqlAbstractInClause()
	{
	}

	/**
	 * Creates a (NOT) IN SQL clause.
	 * 
	 * <p>
	 * The function expects two clause tokens (after the ID token), the third one being optional:
	 * <ul>
	 * 	<li>The first token is the SQL column to be used in the clause.</li>
	 * 	<li>The second token is the name of the report parameter that contains the value list.
	 * 		<br/>
	 * 		The value of this parameter has to be an array, a <code>java.util.Collection</code>
	 * 		or <code>null</code>.
	 * 	</li>
	 * 	<li>The third token is an optional boolean representing the logical value (true/false) 
	 * that the clause should return in case the list of values is null or empty.</li>
	 * </ul>
	 * </p>
	 * 
	 * <p>
	 * The function constructs one of the following clauses:
	 * <ol>
	 * <li>When the function ID token is IN:
	 * <ul>
	 * <li>If the parameter's value is a collection of not null values, the function constructs 
	 * a <code>&lt;column_name&gt; IN (?, ?, .., ?)</code> clause</li>
	 * <li>If the parameter's value is a collection containing both null and not null values, the 
	 * function constructs a <code>(&lt;column_name&gt; IS NULL OR &lt;column_name&gt; IN (?, ?, .., ?))</code> clause</li>
	 * <li>If the parameter's value is a collection containing only null values, the function 
	 * constructs a <code>&lt;column_name&gt; IS NULL</code> clause</li>
	 * </ul>
	 * </li>
	 * <li>When the function ID token is NOTIN:
	 * <ul>
	 * <li>If the parameter's value is a collection of not null values, the function constructs 
	 * a <code>&lt;column_name&gt; NOT IN (?, ?, .., ?)</code> clause</li>
	 * <li>If the parameter's value is a collection containing both null and not null values, the 
	 * function constructs a <code>(&lt;column_name&gt; IS NOT NULL AND &lt;column_name&gt; NOT IN (?, ?, .., ?))</code> clause</li>
	 * <li>If the parameter's value is a collection containing only null values, the function 
	 * constructs a <code>&lt;column_name&gt; IS NOT NULL</code> clause</li>
	 * </ul>
	 * </li>
	 * <li>If the values list is null or empty, both IN and NOTIN functions generate a SQL clause that
	 * will evaluate to true (e.g. <code>0 = 0</code>) or false (e.g. <code>1 = 0</code>) according to the value
	 * of the optional third parameter of the clause. This third optional parameter has itself a default value
	 * that is controlled by the net.sf.jasperreports.sql.clause.in.novalues.result and the net.sf.jasperreports.sql.clause.notin.novalues.result
	 * configuration properties, respectively.</li>
	 * </ol>
	 * </p>
	 * 
	 * @param clauseTokens
	 * @param queryContext
	 * 
	 */
	@Override
	public void apply(JRClauseTokens clauseTokens, JRQueryClauseContext queryContext)
	{
		String col = clauseTokens.getToken(POSITION_DB_COLUMN);
		if (col == null)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_QUERY_IN_CLAUSE_DB_COLUMN_TOKEN_MISSING,
					(Object[])null);
		}
		
		String param = clauseTokens.getToken(POSITION_PARAMETER);
		if (param == null)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_QUERY_IN_CLAUSE_PARAMETER_TOKEN_MISSING,
					(Object[])null);
		}
		
		StringBuffer sbuffer = queryContext.queryBuffer();
		
		Object paramValue = queryContext.getValueParameter(param).getValue();
		if (paramValue == null)
		{
			handleNoValues(clauseTokens, queryContext);
		}
		else
		{
			Collection<?> paramCollection = convert(param, paramValue);
			int count = paramCollection.size();
			Iterator<?> it = paramCollection.iterator();

			if (count == 0)
			{
				handleNoValues(clauseTokens, queryContext);
			}
			else
			{
				StringBuffer nullSbuffer = new StringBuffer();
				StringBuffer notNullSbuffer = new StringBuffer();
				boolean nullFound = false;
				boolean notNullFound = false;
				int idx = 0;
				List<Object> notNullQueryParameters = new ArrayList<>();
				
				while (it.hasNext())
				{
					Object element = it.next();
					if (element == null)
					{
						if (!nullFound)
						{
							nullFound = true;
							nullSbuffer.append(col);
							nullSbuffer.append(' ');
							appendNullOperator(nullSbuffer);
						}
					}
					else
					{
						if (!notNullFound)
						{
							notNullFound = true;
							notNullSbuffer.append(col);
							notNullSbuffer.append(' ');
							appendInOperator(notNullSbuffer);
							notNullSbuffer.append(' ');
							notNullSbuffer.append('(');
						}
					
						if (idx > 0)
						{
							notNullSbuffer.append(", ");
						}
						notNullSbuffer.append('?');
						notNullQueryParameters.add(element);
						idx++;
					}
				}
				if (nullFound)
				{
					
					if (notNullFound)
					{
						sbuffer.append("( ");
						sbuffer.append(nullSbuffer);
						appendAndOrOperator(sbuffer);
					}
					else
					{
						sbuffer.append(nullSbuffer);
					}
				}
				if (notNullFound)
				{
					notNullSbuffer.append(')');
					
					if (nullFound)
					{
						notNullSbuffer.append(" )");
					}
					sbuffer.append(notNullSbuffer);
					queryContext.addQueryMultiParameters(param, count, nullFound);
				}
			}
		}
	}
	
	/**
	 * Generate a SQL clause that will always evaluate to true (e.g. '<code>0 = 0</code>').
	 * 
	 * @param queryContext the query context
	 */
	protected void handleNoValues(JRClauseTokens clauseTokens, JRQueryClauseContext queryContext)
	{
		String noValuesResult = clauseTokens.getToken(POSITION_NO_VALUES_RESULT);
		
		if (noValuesResult == null)
		{
			noValuesResult = 
				JRPropertiesUtil.getInstance(queryContext.getJasperReportsContext()).getProperty(
					getNoValuesResultProperty(), 
					queryContext.getDataset()
					);
		}
		
		if (noValuesResult == null || Boolean.valueOf(noValuesResult))
		{
			queryContext.queryBuffer().append(CLAUSE_TRUISM);
		}
		else
		{
			queryContext.queryBuffer().append(CLAUSE_FALSISM);
		}
	}

	protected abstract String getNoValuesResultProperty();

	/**
	 * 
	 * @param paramName the parameter name
	 * @param paramValue the parameter value
	 * @return a <code>java.util.Collection</code> type object obtained either by converting an array to 
	 * a list or by a cast to <code>java.util.Collection</code> type.
	 */
	protected Collection<?> convert(String paramName, Object paramValue)
	{
		Collection<?> paramCollection;
		if (paramValue.getClass().isArray())
		{
			int size = Array.getLength(paramValue);
			ArrayList<Object> list = new ArrayList<>(size);
			for (int i = 0; i < size; i++)
			{
				list.add(Array.get(paramValue, i));
			}
			paramCollection = list;
		}
		else if (paramValue instanceof Collection<?>)
		{
			paramCollection = (Collection<?>) paramValue;
		}
		else
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_QUERY_IN_CLAUSE_INVALID_PARAMETER_TYPE,
					new Object[]{paramValue.getClass().getName(), paramName});
		}
		return paramCollection;
	}

	protected abstract void appendInOperator(StringBuffer sBuffer);
	protected abstract void appendNullOperator(StringBuffer sBuffer);
	protected abstract void appendAndOrOperator(StringBuffer sBuffer);
}

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
import net.sf.jasperreports.engine.query.ClauseFunctionParameterHandler;
import net.sf.jasperreports.engine.query.JRJdbcQueryExecuter;
import net.sf.jasperreports.engine.query.JRQueryClauseContext;
import net.sf.jasperreports.engine.query.SQLLessOrGreaterBaseClause;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class DateRangeSQLLessOrGreaterClause extends SQLLessOrGreaterBaseClause
{
	private static final String EXCEPTION_MESSAGE_KEY_UNEXPECTED_PARAMETER_TYPE = "date.range.unexpected.parameter.type";
	private static final String EXCEPTION_MESSAGE_KEY_UNKNOWN_CLAUSE_ID = "date.range.unknown.clause.id";

	protected static final DateRangeSQLLessOrGreaterClause singleton = new DateRangeSQLLessOrGreaterClause();
	
	/**
	 * Returns the singleton function instance.
	 * 
	 * @return the singleton function instance
	 */
	public static DateRangeSQLLessOrGreaterClause instance()
	{
		return singleton;
	}

	@Override
	protected ClauseFunctionParameterHandler createParameterHandler(JRQueryClauseContext queryContext, 
			String clauseId, String parameterName)
	{
		Object paramValue = queryContext.getValueParameter(parameterName).getValue();
		if (paramValue != null && !(paramValue instanceof DateRange))
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_UNEXPECTED_PARAMETER_TYPE,
					new Object[]{parameterName, clauseId});
		}
		
		boolean useRangeStart;
		if (JRJdbcQueryExecuter.CLAUSE_ID_LESS.equals(clauseId) 
				|| JRJdbcQueryExecuter.CLAUSE_ID_GREATER_OR_EQUAL.equals(clauseId))
		{
			useRangeStart = true;
		}
		else if (JRJdbcQueryExecuter.CLAUSE_ID_GREATER.equals(clauseId) 
				|| JRJdbcQueryExecuter.CLAUSE_ID_LESS_OR_EQUAL.equals(clauseId))
		{
			useRangeStart = false;
		}
		else
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_UNKNOWN_CLAUSE_ID,
					new Object[]{clauseId});
		}
		
		return new DateRangeParameterHandler(queryContext, parameterName, 
				(DateRange) paramValue, useRangeStart);
	}
}

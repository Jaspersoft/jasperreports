/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2026 Actian Corporation, a division of HCL Software. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Actian,
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



/**
 * Base LESS/GREATER clause function for SQL queries.
 * 
 * <p>
 * The first token in the $X{...} syntax is the function ID token. Possible values for 
 * the LESS | GREATER clause function ID token are:
 * <ul>
 * <li><code>LESS</code> - in this case the <code>LESS THAN</code> operator <code>&lt;</code> will be applied.</li>
 * <li><code>LESS]</code> - in this case the <code>LESS OR EQUAL</code> operator <code>&lt;=</code> will be applied.</li>
 * <li><code>GREATER</code> - in this case the <code>GREATER THAN</code> operator <code>&gt;</code> will be applied.</li>
 * <li><code>[GREATER</code> - in this case the <code>GREATER OR EQUAL</code> operator <code>&gt;=</code> will be applied.</li>
 * </ul>
 * </p> 
 * 
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class JRSqlLessOrGreaterClause extends SQLLessOrGreaterBaseClause
{

	protected static final JRSqlLessOrGreaterClause singleton = new JRSqlLessOrGreaterClause();
	
	/**
	 * Returns the singleton function instance.
	 * 
	 * @return the singleton function instance
	 */
	public static JRSqlLessOrGreaterClause instance()
	{
		return singleton;
	}

	@Override
	protected ClauseFunctionParameterHandler createParameterHandler(JRQueryClauseContext queryContext, 
			String clauseId, String parameterName)
	{
		Object paramValue = queryContext.getValueParameter(parameterName).getValue();
		return new DefaultClauseFunctionParameterHandler(queryContext, parameterName, paramValue);
	}
}

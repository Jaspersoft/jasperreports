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
public class JRSqlBetweenClause extends SQLBetweenBaseClause
{

	protected static final JRSqlBetweenClause singleton = new JRSqlBetweenClause();
	
	/**
	 * Returns the singleton function instance.
	 * 
	 * @return the singleton function instance
	 */
	public static JRSqlBetweenClause instance()
	{
		return singleton;
	}

	@Override
	protected ClauseFunctionParameterHandler createParameterHandler(JRQueryClauseContext queryContext, 
			String clauseId, String parameterName, boolean left)
	{
		Object paramValue = queryContext.getValueParameter(parameterName).getValue();
		return new DefaultClauseFunctionParameterHandler(queryContext, parameterName, paramValue);
	}
}

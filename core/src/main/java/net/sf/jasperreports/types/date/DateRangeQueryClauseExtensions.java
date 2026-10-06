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
package net.sf.jasperreports.types.date;

import java.util.Date;

import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.query.JRJdbcQueryExecuter;
import net.sf.jasperreports.engine.query.ParameterTypesClauseFunctionBundle;
import net.sf.jasperreports.engine.query.StandardParameterTypesClauseFunction;
import net.sf.jasperreports.engine.query.StandardSingleQueryParameterTypesClauseFunctionBundle;
import net.sf.jasperreports.extensions.ExtensionsRegistry;
import net.sf.jasperreports.extensions.ExtensionsRegistryFactory;
import net.sf.jasperreports.extensions.SingletonExtensionRegistry;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class DateRangeQueryClauseExtensions implements ExtensionsRegistryFactory
{

	private static ExtensionsRegistry registry;

	static
	{
		StandardSingleQueryParameterTypesClauseFunctionBundle typesFunctions = 
				new StandardSingleQueryParameterTypesClauseFunctionBundle(JRJdbcQueryExecuter.CANONICAL_LANGUAGE);
		
		typesFunctions.setFunctions(JRJdbcQueryExecuter.CLAUSE_ID_EQUAL, 
				new StandardParameterTypesClauseFunction(DateRangeSQLEqualClause.instance(), DateRange.class));
		typesFunctions.setFunctions(JRJdbcQueryExecuter.CLAUSE_ID_NOTEQUAL, 
				new StandardParameterTypesClauseFunction(DateRangeSQLNotEqualClause.instance(), DateRange.class));
		
		typesFunctions.setFunctions(JRJdbcQueryExecuter.CLAUSE_ID_LESS, 
				new StandardParameterTypesClauseFunction(DateRangeSQLLessOrGreaterClause.instance(), DateRange.class));
		typesFunctions.setFunctions(JRJdbcQueryExecuter.CLAUSE_ID_GREATER, 
				new StandardParameterTypesClauseFunction(DateRangeSQLLessOrGreaterClause.instance(), DateRange.class));
		typesFunctions.setFunctions(JRJdbcQueryExecuter.CLAUSE_ID_LESS_OR_EQUAL, 
				new StandardParameterTypesClauseFunction(DateRangeSQLLessOrGreaterClause.instance(), DateRange.class));
		typesFunctions.setFunctions(JRJdbcQueryExecuter.CLAUSE_ID_GREATER_OR_EQUAL, 
				new StandardParameterTypesClauseFunction(DateRangeSQLLessOrGreaterClause.instance(), DateRange.class));
		
		typesFunctions.setFunctions(JRJdbcQueryExecuter.CLAUSE_ID_BETWEEN, 
				new StandardParameterTypesClauseFunction(DateRangeSQLBetweenClause.instance(), DateRange.class, Date.class));
		typesFunctions.setFunctions(JRJdbcQueryExecuter.CLAUSE_ID_BETWEEN_CLOSED, 
				new StandardParameterTypesClauseFunction(DateRangeSQLBetweenClause.instance(), DateRange.class, Date.class));
		typesFunctions.setFunctions(JRJdbcQueryExecuter.CLAUSE_ID_BETWEEN_LEFT_CLOSED, 
				new StandardParameterTypesClauseFunction(DateRangeSQLBetweenClause.instance(), DateRange.class, Date.class));
		typesFunctions.setFunctions(JRJdbcQueryExecuter.CLAUSE_ID_BETWEEN_RIGHT_CLOSED, 
				new StandardParameterTypesClauseFunction(DateRangeSQLBetweenClause.instance(), DateRange.class, Date.class));
		
		registry = new SingletonExtensionRegistry<ParameterTypesClauseFunctionBundle>(
				ParameterTypesClauseFunctionBundle.class, typesFunctions);
	}
	
	@Override
	public ExtensionsRegistry createRegistry(String registryId,
			JRPropertiesMap properties)
	{
		return registry;
	}

}

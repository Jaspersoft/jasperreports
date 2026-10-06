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

import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.extensions.ExtensionsRegistry;
import net.sf.jasperreports.extensions.ExtensionsRegistryFactory;
import net.sf.jasperreports.extensions.ListExtensionsRegistry;

/**
 * Extensions factory that registers built-in query clause functions for SQL queries.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class SQLQueryClauseFunctionsExtensions implements ExtensionsRegistryFactory
{

	private static final ExtensionsRegistry REGISTRY;

	static
	{
		StandardSingleQueryClauseFunctionBundle functions = new StandardSingleQueryClauseFunctionBundle(
				JRJdbcQueryExecuter.CANONICAL_LANGUAGE);
		
		StandardSingleQueryParameterTypesClauseFunctionBundle typesFunctions = 
				new StandardSingleQueryParameterTypesClauseFunctionBundle(JRJdbcQueryExecuter.CANONICAL_LANGUAGE);
		
		functions.addFunction(JRJdbcQueryExecuter.CLAUSE_ID_IN, JRSqlInClause.instance());
		functions.addFunction(JRJdbcQueryExecuter.CLAUSE_ID_NOTIN, JRSqlNotInClause.instance());	
		
		functions.addFunction(JRJdbcQueryExecuter.CLAUSE_ID_EQUAL, 
				new ParameterTypeSelectorClauseFunction(JRSqlAbstractEqualClause.POSITION_PARAMETER));
		functions.addFunction(JRJdbcQueryExecuter.CLAUSE_ID_NOTEQUAL, 
				new ParameterTypeSelectorClauseFunction(JRSqlAbstractEqualClause.POSITION_PARAMETER));
		typesFunctions.setFunctions(JRJdbcQueryExecuter.CLAUSE_ID_EQUAL, 
				new StandardParameterTypesClauseFunction(JRSqlEqualClause.instance(), Object.class));
		typesFunctions.setFunctions(JRJdbcQueryExecuter.CLAUSE_ID_NOTEQUAL, 
				new StandardParameterTypesClauseFunction(JRSqlNotEqualClause.instance(), Object.class));
		
		functions.addFunction(JRJdbcQueryExecuter.CLAUSE_ID_LESS, 
				new ParameterTypeSelectorClauseFunction(JRSqlLessOrGreaterClause.POSITION_PARAMETER));
		functions.addFunction(JRJdbcQueryExecuter.CLAUSE_ID_GREATER, 
				new ParameterTypeSelectorClauseFunction(JRSqlLessOrGreaterClause.POSITION_PARAMETER));
		functions.addFunction(JRJdbcQueryExecuter.CLAUSE_ID_LESS_OR_EQUAL, 
				new ParameterTypeSelectorClauseFunction(JRSqlLessOrGreaterClause.POSITION_PARAMETER));
		functions.addFunction(JRJdbcQueryExecuter.CLAUSE_ID_GREATER_OR_EQUAL, 
				new ParameterTypeSelectorClauseFunction(JRSqlLessOrGreaterClause.POSITION_PARAMETER));
		typesFunctions.setFunctions(JRJdbcQueryExecuter.CLAUSE_ID_LESS, 
				new StandardParameterTypesClauseFunction(JRSqlLessOrGreaterClause.instance(), Object.class));
		typesFunctions.setFunctions(JRJdbcQueryExecuter.CLAUSE_ID_GREATER, 
				new StandardParameterTypesClauseFunction(JRSqlLessOrGreaterClause.instance(), Object.class));
		typesFunctions.setFunctions(JRJdbcQueryExecuter.CLAUSE_ID_LESS_OR_EQUAL, 
				new StandardParameterTypesClauseFunction(JRSqlLessOrGreaterClause.instance(), Object.class));
		typesFunctions.setFunctions(JRJdbcQueryExecuter.CLAUSE_ID_GREATER_OR_EQUAL, 
				new StandardParameterTypesClauseFunction(JRSqlLessOrGreaterClause.instance(), Object.class));
		
		functions.addFunction(JRJdbcQueryExecuter.CLAUSE_ID_BETWEEN, 
				new ParameterTypeSelectorClauseFunction(JRSqlBetweenClause.POSITION_LEFT_PARAMETER, JRSqlBetweenClause.POSITION_RIGHT_PARAMETER));	
		functions.addFunction(JRJdbcQueryExecuter.CLAUSE_ID_BETWEEN_CLOSED, 
				new ParameterTypeSelectorClauseFunction(JRSqlBetweenClause.POSITION_LEFT_PARAMETER, JRSqlBetweenClause.POSITION_RIGHT_PARAMETER));	
		functions.addFunction(JRJdbcQueryExecuter.CLAUSE_ID_BETWEEN_LEFT_CLOSED, 
				new ParameterTypeSelectorClauseFunction(JRSqlBetweenClause.POSITION_LEFT_PARAMETER, JRSqlBetweenClause.POSITION_RIGHT_PARAMETER));	
		functions.addFunction(JRJdbcQueryExecuter.CLAUSE_ID_BETWEEN_RIGHT_CLOSED, 
				new ParameterTypeSelectorClauseFunction(JRSqlBetweenClause.POSITION_LEFT_PARAMETER, JRSqlBetweenClause.POSITION_RIGHT_PARAMETER));	
		typesFunctions.setFunctions(JRJdbcQueryExecuter.CLAUSE_ID_BETWEEN, 
				new StandardParameterTypesClauseFunction(JRSqlBetweenClause.instance(), Object.class));
		typesFunctions.setFunctions(JRJdbcQueryExecuter.CLAUSE_ID_BETWEEN_CLOSED, 
				new StandardParameterTypesClauseFunction(JRSqlBetweenClause.instance(), Object.class));
		typesFunctions.setFunctions(JRJdbcQueryExecuter.CLAUSE_ID_BETWEEN_LEFT_CLOSED, 
				new StandardParameterTypesClauseFunction(JRSqlBetweenClause.instance(), Object.class));
		typesFunctions.setFunctions(JRJdbcQueryExecuter.CLAUSE_ID_BETWEEN_RIGHT_CLOSED, 
				new StandardParameterTypesClauseFunction(JRSqlBetweenClause.instance(), Object.class));
		
		ListExtensionsRegistry registry = new ListExtensionsRegistry();
		registry.add(QueryClauseFunctionBundle.class, functions);
		registry.add(ParameterTypesClauseFunctionBundle.class, typesFunctions);
		
		REGISTRY = registry;
	}
	
	@Override
	public ExtensionsRegistry createRegistry(String registryId,
			JRPropertiesMap properties)
	{
		return REGISTRY;
	}

}

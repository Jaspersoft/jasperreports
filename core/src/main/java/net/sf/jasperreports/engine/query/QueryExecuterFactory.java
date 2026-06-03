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

import java.util.Map;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * Factory classes used to create query executers.
 * <p/>
 * For each query language, a query executer factory must be created and registered as a JR property.
 * <p/>
 * Query executer factory instances must be thread-safe as they are cached and used as singletons.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see net.sf.jasperreports.engine.query.JRQueryExecuter
 */
public interface QueryExecuterFactory
{

	
	/**
	 * Prefix for query executer factory properties.
	 * <p/>
	 * To obtain query executer factories, a property having the query language appended to this prefix is used 
	 * to get the query executer factory name.
	 */
	@Property(
			name = "net.sf.jasperreports.query.executer.factory.{language}",
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_1_2_0
			)
	public static final String QUERY_EXECUTER_FACTORY_PREFIX = JRPropertiesUtil.PROPERTY_PREFIX + "query.executer.factory.";

	/**
	 * Flag property that determines whether a query executer factory for a given language is enabled.
	 * <p/>
	 * When set to <code>false</code>, any attempt to execute a query in the specified language will result
	 * in an error. This can be used to restrict the query languages available in a deployment.
	 * <p/>
	 * The property is enabled by default for all query languages.
	 */
	@Property(
			name = "net.sf.jasperreports.query.executer.factory.{language}.enabled",
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			defaultValue = PropertyConstants.BOOLEAN_TRUE,
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_7_0_7,
			valueType = Boolean.class
			)
	public static final String PROPERTY_QUERY_EXECUTER_FACTORY_ENABLED_SUFFIX = ".enabled";


	/**
	 * Returns the built-in parameters associated with this query type.
	 * <p/>
	 * These parameters will be created as system-defined parameters for each 
	 * report/dataset having a query of this type.
	 * <p/>
	 * The returned array should contain consecutive pairs of parameter names and parameter classes
	 * (e.g. <code>{"Param1", String.class, "Param2", "List.class"}</code>).
	 * 
	 * @return array of built-in parameter names and types associated with this query type
	 */
	public Object[] getBuiltinParameters();
	
	
	/**
	 * Creates a query executer.
	 * <p/>
	 * This method is called at fill time for reports/datasets having a query supported by
	 * this factory.
	 * 
	 * @param jasperReportsContext the JasperReportsContext
	 * @param dataset the dataset containing the query, fields, etc
	 * @param parameters map of value parameters (instances of {@link JRValueParameter JRValueParameter})
	 * 	indexed by name
	 * 
	 * @return a query executer
	 * @throws JRException
	 */
	public JRQueryExecuter createQueryExecuter(
		JasperReportsContext jasperReportsContext, 
		JRDataset dataset, 
		Map<String,? extends JRValueParameter> parameters
		) throws JRException;

	default public JRQueryExecuter createQueryExecuter(
			QueryExecutionContext context, 
			JRDataset dataset, 
			Map<String,? extends JRValueParameter> parameters
			) throws JRException
	{
		return createQueryExecuter(context.getJasperReportsContext(), dataset, parameters);
	}
	
	/**
	 * Decides whether the query executers created by this factory support a query parameter type.
	 * <p/>
	 * This check is performed for all $P{..} parameters in the query.
	 * 
	 * @param className the value class name of the parameter
	 * @return whether the parameter value type is supported
	 */
	public boolean supportsQueryParameterType(String className);


}

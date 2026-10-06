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
package net.sf.jasperreports.json.query;

import java.util.Map;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.query.AbstractQueryExecuterFactory;
import net.sf.jasperreports.engine.query.JRQueryExecuter;
import net.sf.jasperreports.engine.query.QueryExecutionContext;
import net.sf.jasperreports.engine.query.SimpleQueryExecutionContext;
import net.sf.jasperreports.engine.util.Designated;
import net.sf.jasperreports.properties.PropertyConstants;

/**
 * JSON query executer factory.
 * <p/>
 * The factory creates {@link net.sf.jasperreports.json.query.JsonQueryExecuter JsonQueryExecuter}
 * query executers.
 * 
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class JsonQueryExecuterFactory extends AbstractQueryExecuterFactory implements Designated
{
	
	public static final String JSON_QUERY_EXECUTER_NAME = "net.sf.jasperreports.query.executer:JSON";
	
	/**
	 * Built-in parameter holding the value of the <code>java.io.InputStream</code> to be used for obtaining the JSON data.
	 */
	public static final String JSON_INPUT_STREAM = "JSON_INPUT_STREAM";
	
	/**
	 * Built-in parameter/property holding the value of the source for the JSON file. 
	 * <p/>
	 * It can be:
	 * <ul>
	 * 	<li>a resource on the classpath</li>
	 * 	<li>a file from the filesystem, with an absolute or relative path</li>
	 * 	<li>a url</li>
	 * </ul>
	 */
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			scopeQualifications = {JsonQueryExecuterFactory.JSON_QUERY_EXECUTER_NAME},
			sinceVersion = PropertyConstants.VERSION_4_6_0
			)
	public static final String JSON_SOURCE = JRPropertiesUtil.PROPERTY_PREFIX + "json.source";
	
	//FIXME javadoc
	public static final String JSON_SOURCES = JRPropertiesUtil.PROPERTY_PREFIX + "json.sources";
	
	/**
	 * Parameter/property holding the format pattern used to instantiate java.util.Date instances.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			scopeQualifications = {JsonQueryExecuterFactory.JSON_QUERY_EXECUTER_NAME},
			sinceVersion = PropertyConstants.VERSION_4_6_0
			)
	public final static String JSON_DATE_PATTERN = JRPropertiesUtil.PROPERTY_PREFIX + "json.date.pattern";
	
	/**
	 * Parameter/property holding the format pattern used to instantiate java.lang.Number instances.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			scopeQualifications = {JsonQueryExecuterFactory.JSON_QUERY_EXECUTER_NAME},
			sinceVersion = PropertyConstants.VERSION_4_6_0
			)
	public final static String JSON_NUMBER_PATTERN = JRPropertiesUtil.PROPERTY_PREFIX + "json.number.pattern";

	/**
	 * Parameter holding the value of the datasource Locale
	 */
	public final static String JSON_LOCALE = "JSON_LOCALE";
	
	/**
	 * Built-in parameter/property holding the <code>java.lang.String</code> code of the locale to be used when parsing the JSON data.
	 * <p/>
	 * The allowed format is: language[_country[_variant]] 
	 */
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			scopeQualifications = {JsonQueryExecuterFactory.JSON_QUERY_EXECUTER_NAME},
			sinceVersion = PropertyConstants.VERSION_4_6_0
			)
	public static final String JSON_LOCALE_CODE = JRPropertiesUtil.PROPERTY_PREFIX + "json.locale.code";
	
	/**
	 * Parameter holding the value of the datasource Timezone
	 */
	public final static String JSON_TIME_ZONE = "JSON_TIME_ZONE";
	
	/**
	 * Built-in parameter/property holding the <code>java.lang.String</code> value of the time zone id to be used when parsing the JSON data.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			scopeQualifications = {JsonQueryExecuterFactory.JSON_QUERY_EXECUTER_NAME},
			sinceVersion = PropertyConstants.VERSION_4_6_0
			)
	public static final String JSON_TIMEZONE_ID = JRPropertiesUtil.PROPERTY_PREFIX + "json.timezone.id";
	
	private final static Object[] JSON_BUILTIN_PARAMETERS = {
		JSON_INPUT_STREAM, "java.io.InputStream",
		JSON_SOURCE, "java.lang.String",
		JSON_SOURCES, "java.util.List",
		JSON_DATE_PATTERN, "java.lang.String",
		JSON_NUMBER_PATTERN, "java.lang.String",
		JSON_LOCALE, "java.util.Locale",
		JSON_LOCALE_CODE, "java.lang.String",
		JSON_TIME_ZONE, "java.util.TimeZone",
		JSON_TIMEZONE_ID, "java.lang.String"
		};

	@Override
	public Object[] getBuiltinParameters()
	{
		return JSON_BUILTIN_PARAMETERS;
	}

	@Override
	public JRQueryExecuter createQueryExecuter(
		JasperReportsContext jasperReportsContext,
		JRDataset dataset, 
		Map<String, ? extends JRValueParameter> parameters
		) throws JRException
	{
		return createQueryExecuter(SimpleQueryExecutionContext.of(jasperReportsContext), 
				dataset, parameters);
	}

	@Override
	public JRQueryExecuter createQueryExecuter(
		QueryExecutionContext context,
		JRDataset dataset, 
		Map<String, ? extends JRValueParameter> parameters
		) throws JRException
	{
		return new JsonQueryExecuter(context, dataset, parameters);
	}

	@Override
	public boolean supportsQueryParameterType(String className)
	{
		return true;
	}

	@Override
	public String getDesignation()
	{
		return JSON_QUERY_EXECUTER_NAME;
	}
}

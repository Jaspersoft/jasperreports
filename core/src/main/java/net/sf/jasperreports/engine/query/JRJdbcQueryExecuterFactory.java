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

import java.util.Arrays;
import java.util.Map;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.util.Designated;
import net.sf.jasperreports.properties.PropertyConstants;

/**
 * Query executer factory for SQL queries.
 * <p/>
 * This factory creates JDBC query executers for SQL queries.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see net.sf.jasperreports.engine.query.JRJdbcQueryExecuter
 */
public class JRJdbcQueryExecuterFactory extends AbstractQueryExecuterFactory implements Designated
{	
	
	public static final String QUERY_EXECUTER_NAME = "net.sf.jasperreports.query.executer:SQL";
	
	/**
	 * Property specifying the ResultSet fetch size.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			defaultValue = "0",
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			scopeQualifications = {QUERY_EXECUTER_NAME, HibernateConstants.QUERY_EXECUTER_NAME_HQL},
			sinceVersion = PropertyConstants.VERSION_1_2_0,
			valueType = Integer.class
			)
	public static final String PROPERTY_JDBC_FETCH_SIZE = JRPropertiesUtil.PROPERTY_PREFIX + "jdbc.fetch.size";

	/**
	 * Property specifying the ResultSet type.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			defaultValue = "forwardOnly",
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			scopeQualifications = {QUERY_EXECUTER_NAME},
			sinceVersion = PropertyConstants.VERSION_3_5_3
			)
	public static final String PROPERTY_JDBC_RESULT_SET_TYPE = JRPropertiesUtil.PROPERTY_PREFIX + "jdbc.result.set.type";

	/**
	 * Property specifying the ResultSet concurrency.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			defaultValue = "readOnly",
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			scopeQualifications = {QUERY_EXECUTER_NAME},
			sinceVersion = PropertyConstants.VERSION_3_5_3
			)
	public static final String PROPERTY_JDBC_CONCURRENCY = JRPropertiesUtil.PROPERTY_PREFIX + "jdbc.concurrency";

	/**
	 * Property specifying the ResultSet holdability.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			defaultValue = "hold",
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			scopeQualifications = {QUERY_EXECUTER_NAME},
			sinceVersion = PropertyConstants.VERSION_3_5_3
			)
	public static final String PROPERTY_JDBC_HOLDABILITY = JRPropertiesUtil.PROPERTY_PREFIX + "jdbc.holdability";

	/**
	 * Property specifying the statement max field size.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			scopeQualifications = {QUERY_EXECUTER_NAME},
			sinceVersion = PropertyConstants.VERSION_3_5_3,
			valueType = Integer.class
			)
	public static final String PROPERTY_JDBC_MAX_FIELD_SIZE = JRPropertiesUtil.PROPERTY_PREFIX + "jdbc.max.field.size";

	/**
	 * Property specifying the statement query timeout value (in seconds).
	 */
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			scopeQualifications = {QUERY_EXECUTER_NAME},
			sinceVersion = PropertyConstants.VERSION_6_8_0,
			valueType = Integer.class
	)
	public static final String PROPERTY_JDBC_QUERY_TIMEOUT = JRPropertiesUtil.PROPERTY_PREFIX + "jdbc.query.timeout";

	/**
	 * Flag property specifying if data will be stored in a cached rowset.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			scopeQualifications = {QUERY_EXECUTER_NAME},
			sinceVersion = PropertyConstants.VERSION_4_1_2,
			valueType = Boolean.class
			)
	public static final String PROPERTY_CACHED_ROWSET = JRPropertiesUtil.PROPERTY_PREFIX + "jdbc.cached.rowset";

	/**
	 * Property specifying the default time zone to be used for sending and retrieving 
	 * date/time values to and from the database.
	 * 
	 * <p>
	 * The property can be set globally, at dataset level, at parameter and field levels,
	 * and as a report/dataset parameter.  Note that sending a value as parameter will 
	 * override all properties, and the time zone will be used for all date/time parameters
	 * and fields in the report. 
	 * </p>
	 * 
	 * <p>
	 * The property value can be a time zone ID or REPORT_TIME_ZONE.
	 * In the latter case the report time zone (as in {@link JRParameter#REPORT_TIME_ZONE}) will be used.
	 * </p>
	 * 
	 * @see JRResultSetDataSource#setTimeZone(java.util.TimeZone, boolean)
	 */
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET, PropertyScope.PARAMETER, PropertyScope.FIELD},
			scopeQualifications = {QUERY_EXECUTER_NAME},
			sinceVersion = PropertyConstants.VERSION_4_6_0
			)
	public static final String PROPERTY_TIME_ZONE = JRPropertiesUtil.PROPERTY_PREFIX + "jdbc.time.zone";
	
	//FIXME to be documented soon
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			scopeQualifications = {QUERY_EXECUTER_NAME},
			sinceVersion = PropertyConstants.VERSION_6_1_0
			)
	public static final String PROPERTY_PARAMETERS_TIME_ZONE = JRPropertiesUtil.PROPERTY_PREFIX + "jdbc.parameters.time.zone";
	
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			scopeQualifications = {QUERY_EXECUTER_NAME},
			sinceVersion = PropertyConstants.VERSION_6_1_0
			)
	public static final String PROPERTY_FIELDS_TIME_ZONE = JRPropertiesUtil.PROPERTY_PREFIX + "jdbc.fields.time.zone";

	/**
	 * SQL query language.
	 */
	public static final String QUERY_LANGUAGE_SQL = "sql";
	
	
	private static final String[] queryParameterClassNames;
	
	static
	{
		queryParameterClassNames = new String[] {
				java.lang.Object.class.getName(), 
				java.lang.Boolean.class.getName(), 
				java.lang.Byte.class.getName(), 
				java.lang.Double.class.getName(),
				java.lang.Float.class.getName(), 
				java.lang.Integer.class.getName(), 
				java.lang.Long.class.getName(), 
				java.lang.Short.class.getName(), 
				java.math.BigDecimal.class.getName(),
				java.lang.String.class.getName(), 
				java.util.Date.class.getName(), 
				java.sql.Date.class.getName(), 
				java.sql.Timestamp.class.getName(), 
				java.sql.Time.class.getName(),
				java.time.LocalDate.class.getName(),
				java.time.LocalDateTime.class.getName(),
				java.time.LocalTime.class.getName(),
				java.time.OffsetTime.class.getName(),
				java.time.OffsetDateTime.class.getName()
				};

		Arrays.sort(queryParameterClassNames);
	}
	
	@Override
	public JRQueryExecuter createQueryExecuter(
		JasperReportsContext jasperReportsContext,
		JRDataset dataset, 
		Map<String,? extends JRValueParameter> parameters
		) throws JRException
	{
		return new JRJdbcQueryExecuter(jasperReportsContext, dataset, parameters);
	}

	@Override
	public Object[] getBuiltinParameters()
	{
		return null;
	}

	@Override
	public boolean supportsQueryParameterType(String className)
	{
		return Arrays.binarySearch(queryParameterClassNames, className) >= 0;
	}

	@Override
	public String getDesignation()
	{
		return QUERY_EXECUTER_NAME;
	}
}

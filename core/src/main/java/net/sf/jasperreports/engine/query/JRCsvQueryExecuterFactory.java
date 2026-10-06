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

import java.util.Locale;
import java.util.Map;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.data.DataAdapterServiceConstants;
import net.sf.jasperreports.engine.util.Designated;
import net.sf.jasperreports.properties.PropertyConstants;

/**
 * Query executer factory for CSV file type.
 * <p/>
 * The factory creates {@link net.sf.jasperreports.engine.query.JRCsvQueryExecuter JRCsvQueryExecuter}
 * query executers.
 * 
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class JRCsvQueryExecuterFactory extends AbstractQueryExecuterFactory implements Designated 
{
	
	public static final String QUERY_EXECUTER_NAME = "net.sf.jasperreports.query.executer:CSV";
	
	/**
	 * Built-in parameter/property holding the value of the source for the CSV file. 
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
			scopeQualifications = {JRCsvQueryExecuterFactory.QUERY_EXECUTER_NAME},
			sinceVersion = PropertyConstants.VERSION_4_0_0
			)
	public static final String CSV_SOURCE = JRPropertiesUtil.PROPERTY_PREFIX + "csv.source";
	
	/**
	 * Built-in parameter holding the value of the <code>java.io.InputStream</code> to be used for obtaining the CSV data.
	 */
	public static final String CSV_INPUT_STREAM = "CSV_INPUT_STREAM";
	
	/**
	 * Built-in parameter holding the value of the <code>java.net.URL</code> to be used for obtaining the CSV data.
	 */
	public static final String CSV_URL = "CSV_URL";

	/**
	 * Built-in parameter holding the value of the <code>java.io.File</code> to be used for obtaining the CSV data.
	 */
	public static final String CSV_FILE = "CSV_FILE";
	
	/**
	 * Built-in parameter holding the value of the <code>java.io.Reader</code> to be used for obtaining the CSV data.
	 */
	public static final String CSV_READER = "CSV_READER";
	
	/**
	 * Built-in parameter/property holding the value of the charset used to encode the CSV stream. 
	 * <p/>
	 * It is meaningful only in combination with 
	 * {@link #CSV_INPUT_STREAM CSV_INPUT_STREAM},  {@link #CSV_URL CSV_URL} or {@link #CSV_FILE CSV_FILE}.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			scopeQualifications = {JRCsvQueryExecuterFactory.QUERY_EXECUTER_NAME, 
					DataAdapterServiceConstants.SERVICE_DESIGNATION_CSV},
			sinceVersion = PropertyConstants.VERSION_4_0_0
			)
	public static final String CSV_ENCODING = JRPropertiesUtil.PROPERTY_PREFIX + "csv.encoding";

	/**
	 * Built-in parameter/property holding the names of the columns (in a comma-separated list) to be extracted from the CSV source.
	 * When used as report parameter, the value has to be a <code>java.lang.String</code> object containing column names separated by commas.
	 * It can also be used as the prefix for custom dataset properties specifying the names of the CSV columns in the format:
	 * <code>net.sf.jasperreports.csv.column.names.{arbitrary_name}=value1[, value2, ...]</code>
	 */
	@Property(
			name = "net.sf.jasperreports.csv.column.names.{arbitrary_name}",
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			scopeQualifications = {JRCsvQueryExecuterFactory.QUERY_EXECUTER_NAME},
			sinceVersion = PropertyConstants.VERSION_4_0_0
			)
	public static final String CSV_COLUMN_NAMES = JRPropertiesUtil.PROPERTY_PREFIX + "csv.column.names";

	/**
	 * Built-in parameter holding the value of the columns to be extracted from the CSV source, as a <code>java.lang.String[]</code> object. 
	 * <p/>
	 * When this parameter is null or missing, its value defaults to the values provided 
	 * by properties prefixed with {@link #CSV_COLUMN_NAMES CSV_COLUMN_NAMES}.
	 */
	public static final String CSV_COLUMN_NAMES_ARRAY = "CSV_COLUMN_NAMES_ARRAY";
	
	/**
	 * Built-in parameter holding the <code>java.util.Locale</code> value of the locale to be used when parsing the CSV data.
	 */
	public static final String CSV_LOCALE = "CSV_LOCALE";
	
	/**
	 * Built-in parameter/property holding the <code>java.lang.String</code> code of the locale to be used when parsing the CSV data.
	 * <p/>
	 * The allowed format is: language[_country[_variant]] 
	 */
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			scopeQualifications = {JRCsvQueryExecuterFactory.QUERY_EXECUTER_NAME},
			sinceVersion = PropertyConstants.VERSION_4_0_0,
			valueType = Locale.class
			)
	public static final String CSV_LOCALE_CODE = JRPropertiesUtil.PROPERTY_PREFIX + "csv.locale.code";
	
	/**
	 * Built-in parameter holding the <code>java.util.TimeZone</code> value of the timezone to be used when parsing the CSV data.
	 */
	public static final String CSV_TIMEZONE = "CSV_TIMEZONE";
	
	/**
	 * Built-in parameter/property holding the <code>java.lang.String</code> value of the time zone id to be used when parsing the CSV data.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			scopeQualifications = {JRCsvQueryExecuterFactory.QUERY_EXECUTER_NAME},
			sinceVersion = PropertyConstants.VERSION_4_0_0
			)
	public static final String CSV_TIMEZONE_ID = JRPropertiesUtil.PROPERTY_PREFIX + "csv.timezone.id";
	
	/**
	 * Built-in parameter holding the value of the <code>java.text.DateFormat</code> used to format date columns from the CSV source.
	 */
	public static final String CSV_DATE_FORMAT = "CSV_DATE_FORMAT";
	
	/**
	 * Built-in parameter/property holding the value of the date format pattern to be used when parsing the CSV data.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			scopeQualifications = {JRCsvQueryExecuterFactory.QUERY_EXECUTER_NAME},
			sinceVersion = PropertyConstants.VERSION_4_0_0
			)
	public static final String CSV_DATE_PATTERN = JRPropertiesUtil.PROPERTY_PREFIX + "csv.date.pattern";
	
	/**
	 * Built-in parameter/property holding the value of the field delimiter from the CSV source.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			defaultValue = ",",
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			scopeQualifications = {JRCsvQueryExecuterFactory.QUERY_EXECUTER_NAME,
					DataAdapterServiceConstants.SERVICE_DESIGNATION_CSV},
			sinceVersion = PropertyConstants.VERSION_4_0_0
			)
	public static final String CSV_FIELD_DELIMITER = JRPropertiesUtil.PROPERTY_PREFIX + "csv.field.delimiter";
	
	/**
	 * Built-in parameter holding the value of the <code>java.text.NumberFormat</code> used to format numeric columns from the CSV source.
	 */
	public static final String CSV_NUMBER_FORMAT = "CSV_NUMBER_FORMAT";
	
	/**
	 * Built-in parameter/property holding the value of the number format pattern to be used when parsing the CSV data.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			scopeQualifications = {JRCsvQueryExecuterFactory.QUERY_EXECUTER_NAME},
			sinceVersion = PropertyConstants.VERSION_4_0_0
			)
	public static final String CSV_NUMBER_PATTERN = JRPropertiesUtil.PROPERTY_PREFIX + "csv.number.pattern";
	
	/**
	 * Build-in parameter/property holding the value of the record delimiter from the CSV source
	 */
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			defaultValue = "\\n",
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			scopeQualifications = {JRCsvQueryExecuterFactory.QUERY_EXECUTER_NAME,
					DataAdapterServiceConstants.SERVICE_DESIGNATION_CSV},
			sinceVersion = PropertyConstants.VERSION_4_0_0
			)
	public static final String CSV_RECORD_DELIMITER = JRPropertiesUtil.PROPERTY_PREFIX + "csv.record.delimiter";
	
	/**
	 * Built-in parameter/property specifying whether or not the column names should be obtained 
	 * from the first row in the CSV source.
	 * As parameter, it should hold a <code>java.lang.Boolean</code> value, while as custom dataset property, it should be true or false. 
	 * 
	 * If this parameter is set to true, then setting the {@link #CSV_COLUMN_NAMES} or {@link #CSV_COLUMN_NAMES_ARRAY}
	 * would have no effect. 
	 */
	public static final String CSV_USE_FIRST_ROW_AS_HEADER = "CSV_USE_FIRST_ROW_AS_HEADER";

	private final static Object[] CSV_BUILTIN_PARAMETERS = {
			CSV_SOURCE, "java.lang.String",
			CSV_INPUT_STREAM, "java.io.InputStream",
			CSV_URL, "java.net.URL",
			CSV_FILE, "java.io.File",
			CSV_ENCODING, "java.lang.String",
			CSV_READER, "java.io.Reader",
			CSV_COLUMN_NAMES, "java.lang.String",
			CSV_COLUMN_NAMES_ARRAY, "java.lang.String[]",
			CSV_DATE_FORMAT, "java.text.DateFormat",
			CSV_DATE_PATTERN, "java.lang.String",
			CSV_FIELD_DELIMITER, "java.lang.String",
			CSV_NUMBER_FORMAT, "java.text.NumberFormat",
			CSV_NUMBER_PATTERN, "java.lang.String",
			CSV_RECORD_DELIMITER, "java.lang.String",
			CSV_USE_FIRST_ROW_AS_HEADER, "java.lang.Boolean",
			CSV_LOCALE, "java.util.Locale",
			CSV_LOCALE_CODE, "java.lang.String",
			CSV_TIMEZONE, "java.util.TimeZone",
			CSV_TIMEZONE_ID, "java.lang.String"
			};
	
	@Override
	public Object[] getBuiltinParameters() {
		return CSV_BUILTIN_PARAMETERS;
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
		return new JRCsvQueryExecuter(context, dataset, parameters);
	}

	@Override
	public boolean supportsQueryParameterType(String className) {
		return true;
	}

	@Override
	public String getDesignation()
	{
		return QUERY_EXECUTER_NAME;
	}

}

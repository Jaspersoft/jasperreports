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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesUtil.PropertySuffix;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.data.JRCsvDataSource;

/**
 * CSV query executer implementation.
 * 
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class JRCsvQueryExecuter extends JRAbstractQueryExecuter 
{
	
	private static final Log log = LogFactory.getLog(JRCsvQueryExecuter.class);
	
	private JRCsvDataSource datasource;
	
	/**
	 * 
	 */
	protected JRCsvQueryExecuter(
		JasperReportsContext jasperReportsContext,
		JRDataset dataset, 
		Map<String, ? extends JRValueParameter> parametersMap
		)
	{
		this(SimpleQueryExecutionContext.of(jasperReportsContext), dataset, parametersMap);
	}
	
	protected JRCsvQueryExecuter(
		QueryExecutionContext context,
		JRDataset dataset, 
		Map<String, ? extends JRValueParameter> parametersMap
		) 
	{
		super(context, dataset, parametersMap);
	}

	@Override
	public JRDataSource createDatasource() throws JRException {
		String csvCharset = getStringParameterOrProperty(JRCsvQueryExecuterFactory.CSV_ENCODING);
		
		try {
			Reader csvReader = (Reader) getParameterValue(JRCsvQueryExecuterFactory.CSV_READER); 
			if (csvReader != null) {
				datasource = new JRCsvDataSource(csvReader);
			} else {
				InputStream csvInputStream = (InputStream) getParameterValue(JRCsvQueryExecuterFactory.CSV_INPUT_STREAM);
				if (csvInputStream != null) {
					if (csvCharset != null) {
						datasource = new JRCsvDataSource(csvInputStream, csvCharset);
					} else {
						datasource = new JRCsvDataSource(csvInputStream);
					}
				} else {
					File csvFile = (File) getParameterValue(JRCsvQueryExecuterFactory.CSV_FILE);
					if (csvFile != null) {
						if (csvCharset != null) {
							datasource = new JRCsvDataSource(csvFile, csvCharset);
						} else {
							datasource = new JRCsvDataSource(csvFile);
						}
					} else {
						URL csvUrl = (URL) getParameterValue(JRCsvQueryExecuterFactory.CSV_URL);
						if (csvUrl != null) {
							if (csvCharset != null) {
								datasource = new JRCsvDataSource(csvUrl, csvCharset);
							} else {
								datasource = new JRCsvDataSource(csvUrl);
							}
						} else {
							String csvSource = getStringParameterOrProperty(JRCsvQueryExecuterFactory.CSV_SOURCE);
							if (csvSource != null) {
								if (csvCharset != null) {
									datasource = new JRCsvDataSource(getRepositoryContext(), csvSource, csvCharset);
								} else {
									datasource = new JRCsvDataSource(getRepositoryContext(), csvSource);
								}
							} else {
								if (log.isWarnEnabled()){
									log.warn("No CSV source was provided.");
								}
							}
						}
					}
				}
			}
		} catch (IOException e) {
			throw new JRException(e);
		}
		
		if (datasource != null) {
			List<String> columnNamesList = null;
			String columnNames = getStringParameterOrProperty(JRCsvQueryExecuterFactory.CSV_COLUMN_NAMES);
			if (columnNames != null) {
				columnNamesList = new ArrayList<>();
				columnNamesList.add(columnNames);
			} else {
				String[] columnNamesArray = (String[]) getParameterValue(JRCsvQueryExecuterFactory.CSV_COLUMN_NAMES_ARRAY, true);
				if (columnNamesArray != null) {
					columnNamesList = Arrays.asList(columnNamesArray);
				} else {
					List<PropertySuffix> properties = getPropertiesUtil().getAllProperties(dataset, JRCsvQueryExecuterFactory.CSV_COLUMN_NAMES);
					if (properties != null && !properties.isEmpty()) {
						columnNamesList = new ArrayList<>();
						for (PropertySuffix property : properties) {
							columnNamesList.add(property.getValue());
						}
					} else {
						JRField[] fields = dataset.getFields();
						if (fields != null && fields.length > 0)
						{
							columnNamesList = new ArrayList<>();
							for (JRField field : fields)
							{
								columnNamesList.add(field.getName());
							}
						}
					}
				}
			}

			if (columnNamesList != null && columnNamesList.size() > 0) {
				List<String> splitColumnNamesList = new ArrayList<>();
				for (String names : columnNamesList) {
					for (String token: names.split(",")){
						splitColumnNamesList.add(token.trim());
					}
				}
				datasource.setColumnNames(splitColumnNamesList.toArray(new String[splitColumnNamesList.size()]));
			} else {
				if (log.isWarnEnabled()){
					log.warn("No column names were specified.");
				}
			}
			
			DateFormat dateFormat = (DateFormat) getParameterValue(JRCsvQueryExecuterFactory.CSV_DATE_FORMAT, true);
			if (dateFormat!=null) {
				datasource.setDateFormat(dateFormat);
			} else {
				String dateFormatPattern = getStringParameterOrProperty(JRCsvQueryExecuterFactory.CSV_DATE_PATTERN);
				if (dateFormatPattern != null){
					datasource.setDatePattern(dateFormatPattern);
				}
			}
			
			NumberFormat numberFormat = (NumberFormat) getParameterValue(JRCsvQueryExecuterFactory.CSV_NUMBER_FORMAT, true);
			if (numberFormat != null) {
				datasource.setNumberFormat(numberFormat);
			} else {
				String numberFormatPattern = getStringParameterOrProperty(JRCsvQueryExecuterFactory.CSV_NUMBER_PATTERN);
				if (numberFormatPattern != null){
					datasource.setNumberPattern(numberFormatPattern);
				}
			}

			String fieldDelimiter = getStringParameterOrProperty(JRCsvQueryExecuterFactory.CSV_FIELD_DELIMITER);
			if (fieldDelimiter != null && fieldDelimiter.length() > 0) {
				datasource.setFieldDelimiter(fieldDelimiter.charAt(0));
			}
			
			String recordDelimiter = getStringParameterOrProperty(JRCsvQueryExecuterFactory.CSV_RECORD_DELIMITER);
			if (recordDelimiter != null && recordDelimiter.length() > 0) {
				datasource.setRecordDelimiter(recordDelimiter);
			}
			
			datasource.setUseFirstRowAsHeader(getBooleanParameterOrProperty(JRCsvQueryExecuterFactory.CSV_USE_FIRST_ROW_AS_HEADER, false));
			
			Locale csvLocale = (Locale) getParameterValue(JRCsvQueryExecuterFactory.CSV_LOCALE, true);
			if (csvLocale != null) {
				datasource.setLocale(csvLocale);
			} else {
				String csvLocaleCode = getStringParameterOrProperty(JRCsvQueryExecuterFactory.CSV_LOCALE_CODE);
				if (csvLocaleCode != null) {
					datasource.setLocale(csvLocaleCode);
				} else {
					csvLocale = (Locale) getParameterValue(JRParameter.REPORT_LOCALE, true);
					if (csvLocale != null) { //this is never null at this point, actually
						datasource.setLocale(csvLocale);
					}
				}
			}
			
			TimeZone csvTimezone = (TimeZone) getParameterValue(JRCsvQueryExecuterFactory.CSV_TIMEZONE, true);
			if (csvTimezone != null) {
				datasource.setTimeZone(csvTimezone);
			} else {
				String csvTimezoneId = getStringParameterOrProperty(JRCsvQueryExecuterFactory.CSV_TIMEZONE_ID);
				if (csvTimezoneId != null) {
					datasource.setTimeZone(csvTimezoneId);
				} else {
					csvTimezone = (TimeZone) getParameterValue(JRParameter.REPORT_TIME_ZONE, true);
					if (csvTimezone != null) { //this is never null at this point, actually
						datasource.setTimeZone(csvTimezone);
					}
				}
			}
		}
		
		return datasource;
	}

	@Override
	public void close() {
		if (datasource != null){
			datasource.close();
		}
	}

	@Override
	public boolean cancelQuery() throws JRException {
		return false;
	}

	@Override
	protected String getParameterReplacement(String parameterName) {
		return String.valueOf(getParameterValue(parameterName));
	}
	
}

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

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesUtil.PropertySuffix;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.data.AbstractXlsDataSource;

/**
 * Excel query executer implementation.
 * 
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public abstract class AbstractXlsQueryExecuter extends JRAbstractQueryExecuter {
	
	private static final Log log = LogFactory.getLog(AbstractXlsQueryExecuter.class);
	
	private AbstractXlsDataSource datasource;
	
	/**
	 * 
	 */
	protected AbstractXlsQueryExecuter(
		JasperReportsContext jasperReportsContext, 
		JRDataset dataset, 
		Map<String,? extends JRValueParameter> parametersMap
		)
	{
		this(SimpleQueryExecutionContext.of(jasperReportsContext),
				dataset, parametersMap);
	}
	
	protected AbstractXlsQueryExecuter(
		QueryExecutionContext context, 
		JRDataset dataset, 
		Map<String,? extends JRValueParameter> parametersMap
		) 
	{
		super(context, dataset, parametersMap);
	}

	protected AbstractXlsQueryExecuter(JRDataset dataset, Map<String,? extends JRValueParameter> parametersMap) 
	{
		this(DefaultJasperReportsContext.getInstance(), dataset, parametersMap);
	}

	protected void initDatasource(AbstractXlsDataSource datasource) throws JRException 
	{
		this.datasource = datasource;
		
		if (datasource != null) {
			// build column names list
			List<String> columnNamesList = null;
			@SuppressWarnings("deprecation")
			String columnNames = getStringParameterOrProperty(AbstractXlsQueryExecuterFactory.XLSX_COLUMN_NAMES);
			if (columnNames == null)
			{
				columnNames = getStringParameterOrProperty(AbstractXlsQueryExecuterFactory.XLS_COLUMN_NAMES);
			}
			
			if (columnNames != null) {
				columnNamesList = new ArrayList<>();
				columnNamesList.add(columnNames);
			} else {
				@SuppressWarnings("deprecation")
				String[] columnNamesArray = (String[]) getParameterValue(AbstractXlsQueryExecuterFactory.XLSX_COLUMN_NAMES_ARRAY, true);
				if (columnNamesArray == null)
				{
					columnNamesArray = (String[]) getParameterValue(AbstractXlsQueryExecuterFactory.XLS_COLUMN_NAMES_ARRAY, true);
				}
				if (columnNamesArray != null) {
					columnNamesList = Arrays.asList(columnNamesArray);
				} else {
					@SuppressWarnings("deprecation")
					String propertiesPrefix = AbstractXlsQueryExecuterFactory.XLSX_COLUMN_NAMES;
					List<PropertySuffix> properties = getPropertiesUtil().getAllProperties(dataset, propertiesPrefix);
					if (properties != null && !properties.isEmpty()) 
					{
						columnNamesList = new ArrayList<>();
						for (int i = 0; i < properties.size(); i++) {
							PropertySuffix property = properties.get(i);
							columnNamesList.add(property.getValue());
						}
					}
					else 
					{
						propertiesPrefix = AbstractXlsQueryExecuterFactory.XLS_COLUMN_NAMES;
						properties = getPropertiesUtil().getAllProperties(dataset, propertiesPrefix);
						if (properties != null && !properties.isEmpty()) 
						{
							columnNamesList = new ArrayList<>();
							for (int i = 0; i < properties.size(); i++) {
								PropertySuffix property = properties.get(i);
								columnNamesList.add(property.getValue());
							}
						}
						else 
						{
							JRField[] fields = dataset.getFields();
							if (fields != null && fields.length > 0)
							{
								columnNamesList = new ArrayList<>();
								for (int i = 0; i < fields.length; i++)
								{
									columnNamesList.add(fields[i].getName());
								}
							}
						}
					}
				}
			}
			List<String> splitColumnNamesList = null;
			if (columnNamesList != null && columnNamesList.size() > 0) {
				splitColumnNamesList = new ArrayList<>();
				for (int i = 0; i < columnNamesList.size(); i++) {
					String names = columnNamesList.get(i);
					for (String token: names.split(",")){
						splitColumnNamesList.add(token.trim());
					}
				}
			} 
			
			// build column indexes list
			List<Integer> columnIndexesList = null;
			@SuppressWarnings("deprecation")
			String columnIndexes = getStringParameterOrProperty(AbstractXlsQueryExecuterFactory.XLSX_COLUMN_INDEXES);
			if (columnIndexes == null)
			{
				columnIndexes = getStringParameterOrProperty(AbstractXlsQueryExecuterFactory.XLS_COLUMN_INDEXES);
			}
			
			if (columnIndexes != null) {
				columnIndexesList = new ArrayList<>();
				for (String colIndex: columnIndexes.split(",")){
					columnIndexesList.add(Integer.valueOf(colIndex.trim()));
				}
			} else {
				@SuppressWarnings("deprecation")
				Integer[] columnIndexesArray = (Integer[]) getParameterValue(AbstractXlsQueryExecuterFactory.XLSX_COLUMN_INDEXES_ARRAY, true);
				if (columnIndexesArray == null)
				{
					columnIndexesArray = (Integer[]) getParameterValue(AbstractXlsQueryExecuterFactory.XLS_COLUMN_INDEXES_ARRAY, true);
				}
				if (columnIndexesArray != null) {
					columnIndexesList = Arrays.asList(columnIndexesArray);
				} else {
					@SuppressWarnings("deprecation")
					String propertiesPrefix = AbstractXlsQueryExecuterFactory.XLSX_COLUMN_INDEXES;
					List<PropertySuffix> properties = getPropertiesUtil().getAllProperties(dataset, propertiesPrefix);
					if (properties != null && !properties.isEmpty()) 
					{
						columnIndexesList = new ArrayList<>();
						for (int i = 0; i < properties.size(); i++) {
							String propertyValue = properties.get(i).getValue();
							for (String colIndex: propertyValue.split(",")){
								columnIndexesList.add(Integer.valueOf(colIndex.trim()));
							}
						}
					}
					else
					{
						propertiesPrefix = AbstractXlsQueryExecuterFactory.XLS_COLUMN_INDEXES;
						properties = getPropertiesUtil().getAllProperties(dataset, propertiesPrefix);
						if (properties != null && !properties.isEmpty()) 
						{
							columnIndexesList = new ArrayList<>();
							for (int i = 0; i < properties.size(); i++) {
								String propertyValue = properties.get(i).getValue();
								for (String colIndex: propertyValue.split(",")){
									columnIndexesList.add(Integer.valueOf(colIndex.trim()));
								}
							}
						}
					}
				}
			}
			
			// set column names or column indexes or both
			if (splitColumnNamesList != null) {
				if (columnIndexesList != null) {
					int[] indexesArray = new int[columnIndexesList.size()];
					for (int i=0; i<columnIndexesList.size(); i++) {
						indexesArray[i] = columnIndexesList.get(i);
					}
					datasource.setColumnNames(splitColumnNamesList.toArray(new String[splitColumnNamesList.size()]), indexesArray);
				} else {
					datasource.setColumnNames(splitColumnNamesList.toArray(new String[splitColumnNamesList.size()]));
				}
			} else if (columnIndexesList != null) {
				datasource.setColumnIndexes(columnIndexesList.toArray(new Integer[columnIndexesList.size()]));
			} else {
				if (log.isWarnEnabled()){
					log.warn("No column names or column indexes were specified.");
				}
			}
			
			
			@SuppressWarnings("deprecation")
			DateFormat dateFormat = (DateFormat) getParameterValue(AbstractXlsQueryExecuterFactory.XLSX_DATE_FORMAT, true);
			if (dateFormat == null)
			{
				dateFormat = (DateFormat) getParameterValue(AbstractXlsQueryExecuterFactory.XLS_DATE_FORMAT, true);
			}
			if (dateFormat!=null) {
				datasource.setDateFormat(dateFormat);
			} else {
				@SuppressWarnings("deprecation")
				String dateFormatPattern = getStringParameterOrProperty(AbstractXlsQueryExecuterFactory.XLSX_DATE_PATTERN);
				if (dateFormatPattern == null)
				{
					dateFormatPattern = getStringParameterOrProperty(AbstractXlsQueryExecuterFactory.XLS_DATE_PATTERN);
				}
				if (dateFormatPattern != null){
					datasource.setDatePattern(dateFormatPattern);
				}
			}
			
			@SuppressWarnings("deprecation")
			NumberFormat numberFormat = (NumberFormat) getParameterValue(AbstractXlsQueryExecuterFactory.XLSX_NUMBER_FORMAT, true);
			if (numberFormat == null)
			{
				numberFormat = (NumberFormat) getParameterValue(AbstractXlsQueryExecuterFactory.XLS_NUMBER_FORMAT, true);
			}
			if (numberFormat != null) {
				datasource.setNumberFormat(numberFormat);
			} else {
				@SuppressWarnings("deprecation")
				String numberFormatPattern = getStringParameterOrProperty(AbstractXlsQueryExecuterFactory.XLSX_NUMBER_PATTERN);
				if (numberFormatPattern == null)
				{
					numberFormatPattern = getStringParameterOrProperty(AbstractXlsQueryExecuterFactory.XLS_NUMBER_PATTERN);
				}
				if (numberFormatPattern != null){
					datasource.setNumberPattern(numberFormatPattern);
				}
			}

			@SuppressWarnings("deprecation")
			Boolean useFirstRowAsHeader = getBooleanParameterOrProperty(AbstractXlsQueryExecuterFactory.XLSX_USE_FIRST_ROW_AS_HEADER);
			if (useFirstRowAsHeader == null)
			{
				useFirstRowAsHeader = getBooleanParameterOrProperty(AbstractXlsQueryExecuterFactory.XLS_USE_FIRST_ROW_AS_HEADER, false);
			}
			datasource.setUseFirstRowAsHeader(useFirstRowAsHeader);
			
			@SuppressWarnings("deprecation")
			Locale xlsLocale = (Locale) getParameterValue(AbstractXlsQueryExecuterFactory.XLSX_LOCALE, true);
			if (xlsLocale == null)
			{
				xlsLocale = (Locale) getParameterValue(AbstractXlsQueryExecuterFactory.XLS_LOCALE, true);
			}
			if (xlsLocale != null) {
				datasource.setLocale(xlsLocale);
			} else {
				@SuppressWarnings("deprecation")
				String xlsLocaleCode = getStringParameterOrProperty(AbstractXlsQueryExecuterFactory.XLSX_LOCALE_CODE);
				if (xlsLocaleCode == null)
				{
					xlsLocaleCode = getStringParameterOrProperty(AbstractXlsQueryExecuterFactory.XLS_LOCALE_CODE);
				}
				if (xlsLocaleCode != null) {
					datasource.setLocale(xlsLocaleCode);
				} else {
					xlsLocale = (Locale) getParameterValue(JRParameter.REPORT_LOCALE, true);
					if (xlsLocale != null) { //this is never null at this point, actually
						datasource.setLocale(xlsLocale);
					}
				}
			}
			
			@SuppressWarnings("deprecation")
			TimeZone xlsTimezone = (TimeZone) getParameterValue(AbstractXlsQueryExecuterFactory.XLSX_TIMEZONE, true);
			if (xlsTimezone == null)
			{
				xlsTimezone = (TimeZone) getParameterValue(AbstractXlsQueryExecuterFactory.XLS_TIMEZONE, true);
			}
			if (xlsTimezone != null) {
				datasource.setTimeZone(xlsTimezone);
			} else {
				@SuppressWarnings("deprecation")
				String xlsTimezoneId = getStringParameterOrProperty(AbstractXlsQueryExecuterFactory.XLSX_TIMEZONE_ID);
				if (xlsTimezoneId == null)
				{
					xlsTimezoneId = getStringParameterOrProperty(AbstractXlsQueryExecuterFactory.XLS_TIMEZONE_ID);
				}
				if (xlsTimezoneId != null) {
					datasource.setTimeZone(xlsTimezoneId);
				} else {
					xlsTimezone = (TimeZone) getParameterValue(JRParameter.REPORT_TIME_ZONE, true);
					if (xlsTimezone != null) { //this is never null at this point, actually
						datasource.setTimeZone(xlsTimezone);
					}
				}
			}
			
			String sheetSelection = getStringParameterOrProperty(AbstractXlsQueryExecuterFactory.XLS_SHEET_SELECTION);
			if (sheetSelection != null && sheetSelection.length() > 0) 
			{
				datasource.setSheetSelection(sheetSelection);
			}
		}
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

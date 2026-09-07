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
package net.sf.jasperreports.json.query;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.data.JRAbstractTextDataSource;
import net.sf.jasperreports.engine.data.JsonData;
import net.sf.jasperreports.engine.data.JsonDataCollection;
import net.sf.jasperreports.engine.data.RewindableDataSourceProvider;
import net.sf.jasperreports.engine.data.TextDataSourceAttributes;
import net.sf.jasperreports.engine.query.JRAbstractQueryExecuter;
import net.sf.jasperreports.engine.query.QueryExecutionContext;
import net.sf.jasperreports.engine.query.SimpleQueryExecutionContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public abstract class AbstractJsonQueryExecuter<T extends JRAbstractTextDataSource & JsonData> extends JRAbstractQueryExecuter
{
	private static final Log log = LogFactory.getLog(AbstractJsonQueryExecuter.class);

	private JsonData datasource;


	/**
	 *
	 */
	public AbstractJsonQueryExecuter(
		JasperReportsContext jasperReportsContext,
		JRDataset dataset, 
		Map<String, ? extends JRValueParameter> parametersMap
		)
	{
		this(SimpleQueryExecutionContext.of(jasperReportsContext), 
				dataset, parametersMap);
	}
	
	public AbstractJsonQueryExecuter(
		QueryExecutionContext context,
		JRDataset dataset, 
		Map<String, ? extends JRValueParameter> parametersMap
		)
	{
		super(context, dataset, parametersMap);
		parseQuery();
	}

	@Override
	public JRDataSource createDatasource() throws JRException
	{
		TextDataSourceAttributes textAttributes = getTextAttributes();
		
		InputStream jsonInputStream = (InputStream) getParameterValue(JsonQueryExecuterFactory.JSON_INPUT_STREAM);
		if (jsonInputStream != null) {
			T jsonDatasource = getJsonDataInstance(jsonInputStream);
			jsonDatasource.setTextAttributes(textAttributes);
			datasource = jsonDatasource;
		} else {
			String jsonSource = getStringParameterOrProperty(JsonQueryExecuterFactory.JSON_SOURCE);
			if (jsonSource != null) {
				T jsonDatasource = getJsonDataInstance(jsonSource);
				jsonDatasource.setTextAttributes(textAttributes);
				datasource = jsonDatasource;
			} else {
				List<String> jsonSources = (List<String>) getParameterValue(JsonQueryExecuterFactory.JSON_SOURCES, true);
				if (jsonSources != null) {
					List<RewindableDataSourceProvider<T>> jsonProviders = new ArrayList<>(jsonSources.size());

					for (String source : jsonSources) {
						RewindableDataSourceProvider<T> jsonProvider = getJsonDataProviderInstance(source, textAttributes);
						jsonProviders.add(jsonProvider);
					}
					datasource = new JsonDataCollection(jsonProviders);
				} else {
					if (log.isWarnEnabled()) {
						log.warn("No JSON source was provided.");
					}
				}
			}
		}
		
		return datasource;
	}

	protected abstract T getJsonDataInstance(InputStream jsonInputStream) throws JRException;

	protected abstract T getJsonDataInstance(String jsonSource) throws JRException;

	protected abstract RewindableDataSourceProvider<T> getJsonDataProviderInstance(
			String source, TextDataSourceAttributes textAttributes);

	protected TextDataSourceAttributes getTextAttributes()
	{
		TextDataSourceAttributes attributes = new TextDataSourceAttributes();
		String dateFormatPattern = getStringParameterOrProperty(JsonQueryExecuterFactory.JSON_DATE_PATTERN);
		if (dateFormatPattern != null){
			attributes.setDatePattern(dateFormatPattern);
		}
		
		String numberFormatPattern = getStringParameterOrProperty(JsonQueryExecuterFactory.JSON_NUMBER_PATTERN);
		if (numberFormatPattern != null){
			attributes.setNumberPattern(numberFormatPattern);
		}
		
		Locale jsonLocale = (Locale) getParameterValue(JsonQueryExecuterFactory.JSON_LOCALE, true);
		if (jsonLocale != null) {
			attributes.setLocale(jsonLocale);
		} else {
			String jsonLocaleCode = getStringParameterOrProperty(JsonQueryExecuterFactory.JSON_LOCALE_CODE);
			if (jsonLocaleCode != null) {
				attributes.setLocale(jsonLocaleCode);
			}
		}
		
		TimeZone jsonTimezone = (TimeZone) getParameterValue(JsonQueryExecuterFactory.JSON_TIME_ZONE, true);
		if (jsonTimezone != null) {
			attributes.setTimeZone(jsonTimezone);
		} else {
			String jsonTimezoneId = getStringParameterOrProperty(JsonQueryExecuterFactory.JSON_TIMEZONE_ID);
			if (jsonTimezoneId != null) {
				attributes.setTimeZone(jsonTimezoneId);
			}
		}
		
		return attributes;
	}

	@Override
	public void close()
	{
		//NOP
	}

	@Override
	public boolean cancelQuery() throws JRException
	{
		//nothing to cancel
		return false;
	}
	
}

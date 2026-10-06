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
package net.sf.jasperreports.data.json;

import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import net.sf.jasperreports.dataadapters.AbstractDataAdapterService;
import net.sf.jasperreports.dataadapters.DataAdapter;
import net.sf.jasperreports.dataadapters.DataFileStream;
import net.sf.jasperreports.dataadapters.DataFileUtils;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.ParameterContributorContext;
import net.sf.jasperreports.engine.data.JRAbstractTextDataSource;
import net.sf.jasperreports.engine.util.Designator;
import net.sf.jasperreports.json.data.JsonDataSource;
import net.sf.jasperreports.json.data.JsonQLDataSource;
import net.sf.jasperreports.json.query.JsonQueryExecuterFactory;

/**
 * @author Veaceslov Chicu (schicu@users.sourceforge.net)
 */
public class JsonDataAdapterService extends AbstractDataAdapterService implements Designator<DataAdapter>
{
	
	public static final String JSON_DESIGNATION = "net.sf.jasperreports.data.adapter:JSON";
	
	public static final String JSONQL_DESIGNATION = "net.sf.jasperreports.data.adapter:JSONQL";
	
	private DataFileStream dataStream;
	
	/**
	 * 
	 */
	public JsonDataAdapterService(ParameterContributorContext paramContribContext, JsonDataAdapter jsonDataAdapter)
	{
		super(paramContribContext, jsonDataAdapter);
	}

	public JsonDataAdapter getJsonDataAdapter() {
		return (JsonDataAdapter) getDataAdapter();
	}

	@Override
	public void contributeParameters(Map<String, Object> parameters)
			throws JRException {
		JsonDataAdapter jsonDataAdapter = getJsonDataAdapter();
		if (jsonDataAdapter != null) {
			dataStream = DataFileUtils.instance(getParameterContributorContext()).getDataStream(
					jsonDataAdapter.getDataFile(), parameters);
			
			if (jsonDataAdapter.isUseConnection()) {
				parameters.put(JsonQueryExecuterFactory.JSON_INPUT_STREAM, dataStream);

				Locale locale = jsonDataAdapter.getLocale();
				if (locale != null) {
					parameters.put(JsonQueryExecuterFactory.JSON_LOCALE,
							locale);
				}

				TimeZone timeZone = jsonDataAdapter.getTimeZone();
				if (timeZone != null) {
					parameters.put(
							JsonQueryExecuterFactory.JSON_TIME_ZONE,
							timeZone);
				}

				String datePattern = jsonDataAdapter.getDatePattern();
				if (datePattern != null && datePattern.trim().length() > 0) {
					parameters.put(
							JsonQueryExecuterFactory.JSON_DATE_PATTERN,
							datePattern);
				}

				String numberPattern = jsonDataAdapter.getNumberPattern();
				if (numberPattern != null
						&& numberPattern.trim().length() > 0) {
					parameters.put(
							JsonQueryExecuterFactory.JSON_NUMBER_PATTERN,
							numberPattern);
				}
			} else {
				JRAbstractTextDataSource ds;

				switch (jsonDataAdapter.getLanguage()) {
					case JSONQL:
						ds = new JsonQLDataSource(dataStream, jsonDataAdapter.getSelectExpression());
						break;
					case JSON:
					default:
						ds = new JsonDataSource(dataStream, jsonDataAdapter.getSelectExpression());
						break;
				}

				Locale locale = jsonDataAdapter.getLocale();
				if (locale != null) {
					ds.setLocale(locale);
				}

				TimeZone timeZone = jsonDataAdapter.getTimeZone();
				if (timeZone != null) {
					ds.setTimeZone(timeZone);
				}

				String datePattern = jsonDataAdapter.getDatePattern();
				if (datePattern != null && datePattern.trim().length() > 0) {
					ds.setDatePattern(datePattern);
				}

				String numberPattern = jsonDataAdapter.getNumberPattern();
				if (numberPattern != null && numberPattern.trim().length() > 0) {
					ds.setNumberPattern(numberPattern);
				}

				parameters.put(JRParameter.REPORT_DATA_SOURCE, ds);
			}
		}
	}

	@Override
	public void dispose()
	{
		if (dataStream != null)
		{
			dataStream.dispose();
		}
		
		super.dispose();
	}

	@Override
	public String getName(DataAdapter dataAdapter)
	{
		if (dataAdapter instanceof JsonDataAdapter)
		{
			switch (((JsonDataAdapter) dataAdapter).getLanguage())
			{
			case JSONQL:
				return JSONQL_DESIGNATION;
			case JSON:
			default:
				return JSON_DESIGNATION;
			}
		}
		return null;
	}
}

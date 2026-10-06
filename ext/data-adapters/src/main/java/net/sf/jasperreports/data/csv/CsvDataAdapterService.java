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
package net.sf.jasperreports.data.csv;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import net.sf.jasperreports.dataadapters.AbstractDataAdapterService;
import net.sf.jasperreports.dataadapters.DataFileStream;
import net.sf.jasperreports.dataadapters.DataFileUtils;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.ParameterContributorContext;
import net.sf.jasperreports.engine.data.JRCsvDataSource;
import net.sf.jasperreports.engine.query.JRCsvQueryExecuterFactory;
import net.sf.jasperreports.engine.util.Designated;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class CsvDataAdapterService extends AbstractDataAdapterService implements Designated
{
	
	public static final String SERVICE_DESIGNATION = "net.sf.jasperreports.data.adapter:CSV";
	
	private DataFileStream dataStream;
	
	/**
	 * 
	 */
	public CsvDataAdapterService(ParameterContributorContext paramContribContext, CsvDataAdapter csvDataAdapter)
	{
		super(paramContribContext, csvDataAdapter);
	}
	
	public CsvDataAdapter getCsvDataAdapter()
	{
		return (CsvDataAdapter)getDataAdapter();
	}
	
	@Override
	public void contributeParameters(Map<String, Object> parameters) throws JRException
	{
		CsvDataAdapter csvDataAdapter = getCsvDataAdapter();
		if (csvDataAdapter != null)
		{
			dataStream = DataFileUtils.instance(getParameterContributorContext()).getDataStream(
					csvDataAdapter.getDataFile(), parameters);
			
			Locale locale = csvDataAdapter.getLocale();
			TimeZone timeZone = csvDataAdapter.getTimeZone();
			String datePattern = csvDataAdapter.getDatePattern();
			String numberPattern = csvDataAdapter.getNumberPattern();

			if (csvDataAdapter.isQueryExecuterMode())
			{	
				parameters.put(JRCsvQueryExecuterFactory.CSV_INPUT_STREAM, dataStream);
				if (csvDataAdapter.getEncoding() != null)
				{
					parameters.put(JRCsvQueryExecuterFactory.CSV_ENCODING, csvDataAdapter.getEncoding());
				}

				if (locale != null) 
				{
					parameters.put(JRCsvQueryExecuterFactory.CSV_LOCALE, locale);
				}

				if (timeZone != null) 
				{
					parameters.put(JRCsvQueryExecuterFactory.CSV_TIMEZONE, timeZone);
				}

				if (datePattern != null && datePattern.length() > 0)
				{
					SimpleDateFormat sdf = null;
					
					if (locale == null)
					{
						sdf = new SimpleDateFormat(datePattern);
					}
					else
					{
						sdf = new SimpleDateFormat(datePattern, locale);
					}
					
					if (timeZone != null)
					{
						sdf.setTimeZone(timeZone);
					}

					parameters.put(JRCsvQueryExecuterFactory.CSV_DATE_FORMAT, sdf);
				}

				if (numberPattern != null && numberPattern.length() > 0)
				{
					DecimalFormat df = null;
					if (locale == null)
					{
						df = new DecimalFormat(numberPattern);
					}
					else
					{
						df = new DecimalFormat(numberPattern, DecimalFormatSymbols.getInstance(locale));
					}
					parameters.put(JRCsvQueryExecuterFactory.CSV_NUMBER_FORMAT, df);
				}
				
				String fieldDelimiter = csvDataAdapter.getFieldDelimiter();
				if (fieldDelimiter != null && !fieldDelimiter.isEmpty()) {
					parameters.put( JRCsvQueryExecuterFactory.CSV_FIELD_DELIMITER, fieldDelimiter);
				}
				
				String recordDelimiter = csvDataAdapter.getRecordDelimiter();
				if (recordDelimiter != null && !recordDelimiter.isEmpty()) {
					parameters.put( JRCsvQueryExecuterFactory.CSV_RECORD_DELIMITER, recordDelimiter);
				}
				
				parameters.put( JRCsvQueryExecuterFactory.CSV_USE_FIRST_ROW_AS_HEADER, csvDataAdapter.isUseFirstRowAsHeader());

				if (!csvDataAdapter.isUseFirstRowAsHeader())
				{ 
					parameters.put( JRCsvQueryExecuterFactory.CSV_COLUMN_NAMES_ARRAY, getColumnNames(csvDataAdapter));
				}
			}
			else
			{
				JRCsvDataSource ds = null;
				if (csvDataAdapter.getEncoding() == null)
				{
					ds = new JRCsvDataSource(dataStream);
				}
				else
				{
					try
					{
						ds = new JRCsvDataSource(dataStream, csvDataAdapter.getEncoding());
					}
					catch (UnsupportedEncodingException e)
					{
						throw new JRException(e);
					}
				}

				if (locale != null) 
				{
					ds.setLocale(locale);
				}

				if (timeZone != null) 
				{
					ds.setTimeZone(timeZone);
				}

				if (datePattern != null && datePattern.length() > 0)
				{
					SimpleDateFormat sdf = null;
					
					if (locale == null)
					{
						sdf = new SimpleDateFormat(datePattern);
					}
					else
					{
						sdf = new SimpleDateFormat(datePattern, locale);
					}
					
					if (timeZone != null)
					{
						sdf.setTimeZone(timeZone);
					}

					ds.setDateFormat(sdf);
				}

				if (numberPattern != null && numberPattern.length() > 0)
				{
					DecimalFormat df = null;
					if (locale == null)
					{
						df = new DecimalFormat(numberPattern);
					}
					else
					{
						df = new DecimalFormat(numberPattern, DecimalFormatSymbols.getInstance(locale));
					}
					ds.setNumberFormat(df);
				}

				ds.setFieldDelimiter( csvDataAdapter.getFieldDelimiter().charAt(0) );
				ds.setRecordDelimiter( csvDataAdapter.getRecordDelimiter() );				
				ds.setUseFirstRowAsHeader( csvDataAdapter.isUseFirstRowAsHeader() );
				
				if (!csvDataAdapter.isUseFirstRowAsHeader())
				{ 
					ds.setColumnNames( getColumnNames(csvDataAdapter) );
				}
				
				parameters.put(JRParameter.REPORT_DATA_SOURCE, ds);
			}
		}
	}

	private String[] getColumnNames(CsvDataAdapter csvDataAdapter) {
		String[] names = new String[csvDataAdapter.getColumnNames().size()];
		for (int i=0; i < names.length; ++i )
		{
			names[i] = "" + csvDataAdapter.getColumnNames().get(i);
		}
		return names;
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
	public String getDesignation()
	{
		return SERVICE_DESIGNATION;
	}
	
}

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
package net.sf.jasperreports.data.xls;

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
import net.sf.jasperreports.engine.data.AbstractXlsDataSource;
import net.sf.jasperreports.engine.query.AbstractXlsQueryExecuterFactory;
import net.sf.jasperreports.engine.util.Designated;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class AbstractXlsDataAdapterService extends AbstractDataAdapterService implements Designated 
{
	
	public static final String SERVICE_DESIGNATION = "net.sf.jasperreports.data.adapter:XLS";
	
	protected DataFileStream dataStream;
	
	/**
	 * 
	 */
	public AbstractXlsDataAdapterService(ParameterContributorContext paramContribContext, XlsDataAdapter xlsDataAdapter)
	{
		super(paramContribContext, xlsDataAdapter);
	}
	
	public XlsDataAdapter getXlsDataAdapter()
	{
		return (XlsDataAdapter)getDataAdapter();
	}
	
	@Override
	public void contributeParameters(Map<String, Object> parameters) throws JRException
	{
		XlsDataAdapter xlsDataAdapter = getXlsDataAdapter();
		if (xlsDataAdapter != null)
		{
			dataStream = DataFileUtils.instance(getParameterContributorContext()).getDataStream(
					xlsDataAdapter.getDataFile(), parameters);
			
			Locale locale = xlsDataAdapter.getLocale();
			TimeZone timeZone = xlsDataAdapter.getTimeZone();
			String datePattern = xlsDataAdapter.getDatePattern();
			String numberPattern = xlsDataAdapter.getNumberPattern();
			String sheetSelection = xlsDataAdapter.getSheetSelection();

			if (xlsDataAdapter.isQueryExecuterMode())
			{	
				parameters.put(AbstractXlsQueryExecuterFactory.XLS_INPUT_STREAM, dataStream);

				if (locale != null) 
				{
					parameters.put(AbstractXlsQueryExecuterFactory.XLS_LOCALE, locale);
				}

				if (timeZone != null) 
				{
					parameters.put(AbstractXlsQueryExecuterFactory.XLS_TIMEZONE, timeZone);
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

					parameters.put(AbstractXlsQueryExecuterFactory.XLS_DATE_FORMAT, sdf);
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
					parameters.put(AbstractXlsQueryExecuterFactory.XLS_NUMBER_FORMAT, df);
				}
				
				parameters.put( AbstractXlsQueryExecuterFactory.XLS_USE_FIRST_ROW_AS_HEADER, xlsDataAdapter.isUseFirstRowAsHeader());

				if (sheetSelection != null && sheetSelection.length() > 0)
				{
					parameters.put( AbstractXlsQueryExecuterFactory.XLS_SHEET_SELECTION, sheetSelection );
				}

				if (!xlsDataAdapter.isUseFirstRowAsHeader())
				{ 
					String[] names = new String[xlsDataAdapter.getColumnNames().size()];
					Integer[] indexes = new Integer[xlsDataAdapter.getColumnNames().size()];
					setupColumns(xlsDataAdapter, names, indexes);

					parameters.put( AbstractXlsQueryExecuterFactory.XLS_COLUMN_NAMES_ARRAY, names);
					parameters.put( AbstractXlsQueryExecuterFactory.XLS_COLUMN_INDEXES_ARRAY, indexes);
				}
			}
			else
			{
				AbstractXlsDataSource ds = getXlsDataSource();

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
	
				ds.setUseFirstRowAsHeader(xlsDataAdapter.isUseFirstRowAsHeader());
	
				if (sheetSelection != null && sheetSelection.length() > 0)
				{
					ds.setSheetSelection(sheetSelection);
				}

				if (!xlsDataAdapter.isUseFirstRowAsHeader())
				{
					String[] names = new String[xlsDataAdapter.getColumnNames().size()];
					int[] indexes = new int[xlsDataAdapter.getColumnNames().size()];
					setupColumns(xlsDataAdapter, names, indexes);
					ds.setColumnNames( names, indexes);
				}
	
				parameters.put(JRParameter.REPORT_DATA_SOURCE, ds);
			}
		}
	}
	
	/**
	 * 
	 */
	protected abstract AbstractXlsDataSource getXlsDataSource() throws JRException;

	protected void setupColumns(XlsDataAdapter xlsDataAdapter, String[] names,
			int[] indexes) {
		for (int i=0; i< names.length; ++i )
		{
			names[i] = "" + xlsDataAdapter.getColumnNames().get(i);
			indexes[i] = (xlsDataAdapter.getColumnIndexes().size() > i) ? xlsDataAdapter.getColumnIndexes().get(i) : i;
		}
	}
	
	protected void setupColumns(XlsDataAdapter xlsDataAdapter, String[] names,
			Integer[] indexes) {
		for (int i=0; i< names.length; ++i )
		{
			names[i] = "" + xlsDataAdapter.getColumnNames().get(i);
			indexes[i] = (xlsDataAdapter.getColumnIndexes().size() > i) ? xlsDataAdapter.getColumnIndexes().get(i) : i;
		}
	}

	@Override
	public void dispose()
	{
		if (dataStream != null)
		{
			dataStream.dispose();
		}
	}
	
	@Override
	public String getDesignation()
	{
		return SERVICE_DESIGNATION;
	}
}

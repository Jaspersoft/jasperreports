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
package net.sf.jasperreports.poi.data;

import java.io.InputStream;
import java.util.Map;

import net.sf.jasperreports.data.excel.ExcelDataAdapter;
import net.sf.jasperreports.data.excel.ExcelFormatEnum;
import net.sf.jasperreports.data.xls.AbstractXlsDataAdapterService;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.ParameterContributorContext;
import net.sf.jasperreports.engine.data.AbstractXlsDataSource;
import net.sf.jasperreports.engine.util.JRClassLoader;
import net.sf.jasperreports.engine.util.Pair;
import net.sf.jasperreports.poi.query.ExcelQueryExecuter;
import net.sf.jasperreports.poi.query.ExcelQueryExecuterFactory;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class ExcelDataAdapterService extends AbstractXlsDataAdapterService 
{
	
	/**
	 * 
	 */
	public ExcelDataAdapterService(ParameterContributorContext paramContribContext, ExcelDataAdapter excelDataAdapter)
	{
		super(paramContribContext, excelDataAdapter);
	}

	public ExcelDataAdapter getExcelDataAdapter()
	{
		return (ExcelDataAdapter)getDataAdapter();
	}

	@Override
	public void contributeParameters(Map<String, Object> parameters) throws JRException
	{
		super.contributeParameters(parameters);

		ExcelDataAdapter xlsDataAdapter = getExcelDataAdapter();
		if (xlsDataAdapter != null)
		{
			ExcelFormatEnum format = xlsDataAdapter.getFormat();

			if (xlsDataAdapter.isQueryExecuterMode())
			{	
				if (format != null) 
				{
					parameters.put(ExcelQueryExecuterFactory.XLS_FORMAT, format);
				}
			}
		}
	}

	@Override
	protected AbstractXlsDataSource getXlsDataSource() throws JRException
	{
		AbstractXlsDataSource dataSource = null;

		ExcelDataAdapter excelDataAdapter = getExcelDataAdapter();
		
		InputStream inputStream = dataStream;
		ExcelFormatEnum format = excelDataAdapter.getFormat();
		if (format == null || format == ExcelFormatEnum.AUTODETECT)
		{
			Pair<InputStream, ExcelFormatEnum> sniffResult = ExcelQueryExecuter.sniffExcelFormat(inputStream);
			inputStream = sniffResult.first();
			format = sniffResult.second();
		}
		
		switch (format)
		{
			case XLS :
			{
				dataSource =
					ExcelQueryExecuter.createDataSource(
						ExcelQueryExecuter.EXCEL_DATA_SOURCE_CLASS,
						new Class<?>[]{InputStream.class, boolean.class, ExcelFormatEnum.class},
						new Object[]{inputStream, false, format}
						);
				break;
			}
			case XLSX :
			{
				String dataSourceFactoryClassName = 
					JRPropertiesUtil.getInstance(getJasperReportsContext()).getProperty(
						ExcelQueryExecuter.PROPERTY_XLSX_DATA_SOURCE_FACTORY, 
						getParameterContributorContext().getDataset()
						);
				if (dataSourceFactoryClassName == null)
				{
					try
					{
						JRClassLoader.loadClassForName(ExcelQueryExecuter.FASTEXCEL_DATA_SOURCE_CLASS);
						dataSource =
							ExcelQueryExecuter.createDataSource(
								ExcelQueryExecuter.FASTEXCEL_DATA_SOURCE_CLASS,
								new Class<?>[]{InputStream.class, boolean.class},
								new Object[]{inputStream, false}
								);
					}
					catch (ClassNotFoundException e)
					{
						dataSource =
							ExcelQueryExecuter.createDataSource(
								ExcelQueryExecuter.EXCEL_DATA_SOURCE_CLASS,
								new Class<?>[]{InputStream.class, boolean.class, ExcelFormatEnum.class},
								new Object[]{inputStream, false, format}
								);
					}
				}
				else
				{
					dataSource =
						ExcelQueryExecuter.createDataSource(
							dataSourceFactoryClassName,
							inputStream,
							false
							);
				}
				
				break;
			}
			case AUTODETECT :
			default:
			{
				// should never get here
			}
		}
		
		return dataSource;
	}

}

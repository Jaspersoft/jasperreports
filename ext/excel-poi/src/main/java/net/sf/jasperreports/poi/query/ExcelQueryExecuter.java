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
package net.sf.jasperreports.poi.query;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.data.excel.ExcelFormatEnum;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.data.AbstractXlsDataSource;
import net.sf.jasperreports.engine.data.XlsxDataSourceFactory;
import net.sf.jasperreports.engine.query.AbstractXlsQueryExecuter;
import net.sf.jasperreports.engine.query.AbstractXlsQueryExecuterFactory;
import net.sf.jasperreports.engine.query.QueryExecutionContext;
import net.sf.jasperreports.engine.query.SimpleQueryExecutionContext;
import net.sf.jasperreports.engine.util.JRClassLoader;
import net.sf.jasperreports.engine.util.Pair;
import net.sf.jasperreports.properties.PropertyConstants;
import net.sf.jasperreports.repo.RepositoryUtil;

/**
 * Excel query executer implementation.
 * 
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class ExcelQueryExecuter extends AbstractXlsQueryExecuter
{
	private static final Log log = LogFactory.getLog(ExcelQueryExecuter.class);

	/**
	 * A property that specifies the name of class implementing the Excel data source factory interface to be used for reading XLSX format files.
	 * See the {@link XlsxDataSourceFactory} interface.
	 */
	@Property(
		category = PropertyConstants.CATEGORY_DATA_SOURCE,
		scopes = {PropertyScope.GLOBAL, PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.DATASET},
		sinceVersion = PropertyConstants.VERSION_6_20_6
		)
	public static final String PROPERTY_XLSX_DATA_SOURCE_FACTORY = JRPropertiesUtil.PROPERTY_PREFIX + "xlsx.data.source.factory";

	public static final String EXCEL_DATA_SOURCE_CLASS = "net.sf.jasperreports.poi.data.ExcelDataSource";
	public static final String FASTEXCEL_DATA_SOURCE_CLASS = "net.sf.jasperreports.fastexcel.FastExcelDataSource";
	private static final String POI_XLS_WORKBOOK_CLASS = "org.apache.poi.hssf.usermodel.HSSFWorkbook";
	private static final String POI_XLSX_WORKBOOK_CLASS = "org.apache.poi.xssf.usermodel.XSSFWorkbook";
	private static final String FASTEXCEL_WORKBOOK_CLASS = "org.dhatim.fastexcel.reader.ReadableWorkbook";
	
	
	/**
	 * 
	 */
	protected ExcelQueryExecuter(
		JasperReportsContext jasperReportsContext, 
		JRDataset dataset, 
		Map<String,? extends JRValueParameter> parametersMap
		)
	{
		this(SimpleQueryExecutionContext.of(jasperReportsContext),
				dataset, parametersMap);
	}
	
	protected ExcelQueryExecuter(
		QueryExecutionContext context, 
		JRDataset dataset, 
		Map<String,? extends JRValueParameter> parametersMap
		) 
	{
		super(context, dataset, parametersMap);
	}

	protected ExcelQueryExecuter(JRDataset dataset, Map<String,? extends JRValueParameter> parametersMap) 
	{
		this(DefaultJasperReportsContext.getInstance(), dataset, parametersMap);
	}

	@Override
	public JRDataSource createDatasource() throws JRException
	{
		AbstractXlsDataSource dataSource = null;

		@SuppressWarnings("deprecation")
		Object workbook = getParameterValue(JRXlsxQueryExecuterFactory.XLSX_WORKBOOK, true);
		if (workbook == null)
		{
			workbook = getParameterValue(AbstractXlsQueryExecuterFactory.XLS_WORKBOOK, true);
		}
		if (workbook != null) 
		{
			String dataSourceClassName = null;
			String workbookClassName = workbook.getClass().getName();
			if (POI_XLS_WORKBOOK_CLASS.equals(workbookClassName))
			{
				dataSourceClassName = EXCEL_DATA_SOURCE_CLASS;
			}
			else if (POI_XLSX_WORKBOOK_CLASS.equals(workbookClassName))
			{
				dataSourceClassName = EXCEL_DATA_SOURCE_CLASS;
			}
			else if (FASTEXCEL_WORKBOOK_CLASS.equals(workbookClassName))
			{
				dataSourceClassName = FASTEXCEL_DATA_SOURCE_CLASS;
			}

			dataSource = 
				createDataSource(
					dataSourceClassName, 
					new Class<?>[]{workbook.getClass()},
					new Object[]{workbook}
					);
		}
		else 
		{
			boolean closeInputStream = false;
			
			@SuppressWarnings("deprecation")
			InputStream xlsInputStream = (InputStream)getParameterValue(JRXlsxQueryExecuterFactory.XLSX_INPUT_STREAM, true);
			if (xlsInputStream == null)
			{
				xlsInputStream = (InputStream)getParameterValue(AbstractXlsQueryExecuterFactory.XLS_INPUT_STREAM, true);
			}
			
			if (xlsInputStream == null) 
			{
				closeInputStream = true;
				
				@SuppressWarnings("deprecation")
				File xlsFile = (File)getParameterValue(JRXlsxQueryExecuterFactory.XLSX_FILE, true);
				if (xlsFile == null)
				{
					xlsFile = (File)getParameterValue(AbstractXlsQueryExecuterFactory.XLS_FILE, true);
				}
				
				if (xlsFile == null) 
				{
					@SuppressWarnings("deprecation")
					String xlsSource = getStringParameterOrProperty(JRXlsxQueryExecuterFactory.XLSX_SOURCE);
					if (xlsSource == null)
					{
						xlsSource = getStringParameterOrProperty(AbstractXlsQueryExecuterFactory.XLS_SOURCE);
					}

					if (xlsSource == null) 
					{
						if (log.isWarnEnabled())
						{
							log.warn("No Excel source was provided.");
						}
					}
					else 
					{
						xlsInputStream = RepositoryUtil.getInstance(getRepositoryContext()).getInputStreamFromLocation(xlsSource);
					}
				}
				else 
				{
					try
					{
						xlsInputStream = new FileInputStream(xlsFile);
					}
					catch (IOException e)
					{
						throw new JRException(e);
					}
				}
			}

			if (xlsInputStream != null)
			{
				ExcelFormatEnum format = null;
				Object objFormat = getParameterValue(ExcelQueryExecuterFactory.XLS_FORMAT, true);
				if (objFormat instanceof ExcelFormatEnum)
				{
					format = (ExcelFormatEnum)objFormat;
				}
				if (format == null)
				{
					format = ExcelFormatEnum.getByName(getStringParameterOrProperty(ExcelQueryExecuterFactory.XLS_FORMAT));
				}
				if (format == null)
				{
					format = ExcelFormatEnum.AUTODETECT;
				}
				
				if (format == ExcelFormatEnum.AUTODETECT)
				{
					Pair<InputStream, ExcelFormatEnum> sniffResult = sniffExcelFormat(xlsInputStream);
					xlsInputStream = sniffResult.first();
					format = sniffResult.second();
				}
				
				switch (format)
				{
					case XLS :
					{
						dataSource =
							createDataSource(
								EXCEL_DATA_SOURCE_CLASS,
								new Class<?>[]{InputStream.class, boolean.class, ExcelFormatEnum.class},
								new Object[]{xlsInputStream, closeInputStream, format}
								);
						break;
					}
					case XLSX :
					{
						String dataSourceFactoryClassName = getPropertiesUtil().getProperty(PROPERTY_XLSX_DATA_SOURCE_FACTORY, dataset);
						if (dataSourceFactoryClassName == null)
						{
							try
							{
								JRClassLoader.loadClassForName(FASTEXCEL_DATA_SOURCE_CLASS);
								dataSource =
									createDataSource(
										FASTEXCEL_DATA_SOURCE_CLASS,
										new Class<?>[]{InputStream.class, boolean.class},
										new Object[]{xlsInputStream, closeInputStream}
										);
							}
							catch (ClassNotFoundException e)
							{
								dataSource =
									createDataSource(
										EXCEL_DATA_SOURCE_CLASS,
										new Class<?>[]{InputStream.class, boolean.class, ExcelFormatEnum.class},
										new Object[]{xlsInputStream, closeInputStream, format}
										);
							}
						}
						else
						{
							dataSource =
								createDataSource(
									dataSourceFactoryClassName,
									xlsInputStream,
									closeInputStream
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
			}
		}
		
		if (dataSource != null) 
		{
			initDatasource(dataSource);
		}
		
		return dataSource;
	}
	

	public static AbstractXlsDataSource createDataSource(
		String dataSourceClassName, 
		Class<?>[] constrParamTypes,
		Object[] constrParamValues
		) throws JRException 
	{
		AbstractXlsDataSource dataSource = null;
		
		try
		{
			@SuppressWarnings("unchecked")
			Class<? extends AbstractXlsDataSource> dataSourceClass = (Class<? extends AbstractXlsDataSource>) JRClassLoader.loadClassForName(dataSourceClassName);
			Constructor<? extends AbstractXlsDataSource> constructor = dataSourceClass.getConstructor(constrParamTypes);
			dataSource = constructor.newInstance(constrParamValues);
		}
		catch (InvocationTargetException  e)
		{
			throw new JRException(e.getTargetException());
		}
		catch (IllegalAccessException | InstantiationException | NoSuchMethodException | ClassNotFoundException e)
		{
			throw new JRException(e);
		}
		
		return dataSource;
	}
	

	public static AbstractXlsDataSource createDataSource(
		String dataSourceFactoryClassName, 
		InputStream inputStream,
		boolean closeInputStream
		) throws JRException 
	{
		AbstractXlsDataSource dataSource = null;
		
		try
		{
			@SuppressWarnings("unchecked")
			Class<? extends XlsxDataSourceFactory> dataSourceFactoryClass = (Class<? extends XlsxDataSourceFactory>) JRClassLoader.loadClassForName(dataSourceFactoryClassName);
			XlsxDataSourceFactory dataSourceFactory = dataSourceFactoryClass.getDeclaredConstructor().newInstance();
			dataSource = dataSourceFactory.getDataSource(inputStream, closeInputStream);
		}
		catch (InvocationTargetException  e)
		{
			throw new JRException(e.getTargetException());
		}
		catch (IOException | IllegalAccessException | InstantiationException | NoSuchMethodException | ClassNotFoundException e)
		{
			throw new JRException(e);
		}
		
		return dataSource;
	}
	

	public static Pair<InputStream, ExcelFormatEnum> sniffExcelFormat(InputStream inputStream)
	{
		ExcelFormatEnum format = null;

		BufferedInputStream bis = new BufferedInputStream(inputStream);
		bis.mark(4);
		try
		{
			int test1 = bis.read();
			int test2 = bis.read();
			int test3 = bis.read();
			int test4 = bis.read();
			bis.reset();

			if (test1 == 'P' && test2 == 'K' && test3 == 0x03 && test4 == 0x04) 
			{
				format = ExcelFormatEnum.XLSX;
			} 
			else 
			{
				format = ExcelFormatEnum.XLS;
			}
		}
		catch (IOException e)
		{
			throw new JRRuntimeException(e);
		}
		
		return new Pair<InputStream, ExcelFormatEnum>(bis, format);
	}
}

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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.repo.RepositoryContext;
import net.sf.jasperreports.repo.SimpleRepositoryContext;


/**
 * This data source implementation reads an XLS stream.
 * <p>
 * The default naming convention is to name report fields COLUMN_x and map each column with the field found at index x 
 * in each row (these indices start with 0). To avoid this situation, users can either specify a collection of column 
 * names or set a flag to read the column names from the first row of the CSV file.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @deprecated Replaced by {@link ExcelDataSource}.
 */
public class XlsDataSource extends AbstractPoiXlsDataSource
{
	/**
	 * Creates a data source instance from a workbook.
	 * @param workbook the workbook
	 */
	public XlsDataSource(Workbook workbook)
	{
		super(workbook);
	}


	/**
	 * Creates a data source instance from an XLS data input stream.
	 * @param inputStream an input stream containing XLS data
	 */
	public XlsDataSource(InputStream inputStream) throws JRException, IOException
	{
		super(inputStream);
	}


	/**
	 * Creates a data source instance from an XLS data input stream.
	 * @param inputStream an input stream containing XLS data
	 */
	public XlsDataSource(InputStream inputStream, boolean closeInputStream) throws JRException, IOException
	{
		super(inputStream, closeInputStream);
	}


	/**
	 * Creates a data source instance from an XLS file.
	 * @param file a file containing XLS data
	 */
	public XlsDataSource(File file) throws JRException, IOException
	{
		super(file);
	}

	
	/**
	 * Creates a data source instance that reads XLS data from a given location.
	 * @param jasperReportsContext the JasperReportsContext
	 * @param location a String representing XLS data source
	 * @throws IOException 
	 */
	public XlsDataSource(JasperReportsContext jasperReportsContext, String location) throws JRException, IOException
	{
		this(SimpleRepositoryContext.of(jasperReportsContext), location);
	}

	public XlsDataSource(RepositoryContext context, String location) throws JRException, IOException
	{
		super(context, location);
	}
	
	/**
	 * @see #XlsDataSource(JasperReportsContext, String)
	 */
	public XlsDataSource(String location) throws JRException, IOException
	{
		super(location);
	}


	@Override
	protected Workbook loadWorkbook(InputStream inputStream) throws IOException
	{
		return new HSSFWorkbook(inputStream);
	}


}



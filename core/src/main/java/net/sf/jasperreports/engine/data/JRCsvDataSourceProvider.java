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
package net.sf.jasperreports.engine.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.NumberFormat;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataSourceProvider;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperReport;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 */
public class JRCsvDataSourceProvider implements JRDataSourceProvider
{
	private static final String EXCEPTION_MESSAGE_KEY_CANNOT_FIND_SOURCE = "data.csv.cannot.find.source";
	
	private Reader reader;

	private DateFormat dateFormat;
	private char fieldDelimiter;
	private String recordDelimiter;
	private String[] columnNames;
	private NumberFormat numberFormat;

	/**
	 * @param stream an input stream containing CSV data
	 */
	public JRCsvDataSourceProvider(InputStream stream)
	{
		this(new InputStreamReader(stream));
	}


	/**
	 * Builds a datasource instance.
	 * @param file a file containing CSV data
	 */
	public JRCsvDataSourceProvider(File file) throws FileNotFoundException
	{
		this(new FileReader(file));
	}


	/**
	 * Builds a datasource instance.
	 * @param reader a <tt>Reader</tt> instance, for reading the stream
	 */
	public JRCsvDataSourceProvider(Reader reader)
	{
		this.reader = reader;
	}

	@Override
	public boolean supportsGetFieldsOperation()
	{
		return false;
	}


	@Override
	public JRField[] getFields(JasperReport report) throws JRException, UnsupportedOperationException
	{
		return null;
	}


	@Override
	public JRDataSource create(JasperReport report) throws JRException
	{
		JRCsvDataSource ds;
		if (reader != null)
		{
			 ds = new JRCsvDataSource(reader);
		}
		else 
		{
			throw 
			new JRException(
				EXCEPTION_MESSAGE_KEY_CANNOT_FIND_SOURCE,
				(Object[])null);
		}

		ds.setDateFormat(dateFormat);
		ds.setNumberFormat(numberFormat);
		ds.setFieldDelimiter(fieldDelimiter);
		ds.setRecordDelimiter(recordDelimiter);
		ds.setColumnNames(columnNames);

		return ds;
	}


	@Override
	public void dispose(JRDataSource dataSource) throws JRException
	{
	}

	public String[] getColumnNames()
	{
		return columnNames;
	}

	public void setColumnNames(String[] colNames)
	{
		if (colNames == null)
		{
			this.columnNames = null;
		}
		else
		{
			this.columnNames = new String[colNames.length];
			System.arraycopy(colNames, 0, this.columnNames, 0, colNames.length);
		}
	}

	public DateFormat getDateFormat()
	{
		return dateFormat;
	}

	public void setDateFormat(DateFormat dateFormat)
	{
		this.dateFormat = dateFormat;
	}

	public char getFieldDelimiter()
	{
		return fieldDelimiter;
	}

	public void setFieldDelimiter(char fieldDelimiter)
	{
		this.fieldDelimiter = fieldDelimiter;
	}

	public String getRecordDelimiter()
	{
		return recordDelimiter;
	}

	public void setRecordDelimiter(String recordDelimiter)
	{
		this.recordDelimiter = recordDelimiter;
	}


	public NumberFormat getNumberFormat() 
	{
		return numberFormat;
	}


	public void setNumberFormat(NumberFormat numberFormat) 
	{
		this.numberFormat = numberFormat;
	}
}

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
package net.sf.jasperreports.fastexcel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.CellType;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.data.AbstractXlsDataSource;
import net.sf.jasperreports.engine.util.FormatUtils;
import net.sf.jasperreports.repo.RepositoryContext;
import net.sf.jasperreports.repo.RepositoryUtil;
import net.sf.jasperreports.repo.SimpleRepositoryContext;


/**
 * This data source implementation reads an XLSX stream.
 * <p>
 * The default naming convention is to name report fields COLUMN_x and map each column with the field found at index x 
 * in each row (these indices start with 0). To avoid this situation, users can either specify a collection of column 
 * names or set a flag to read the column names from the first row of the XLSX file.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class FastExcelDataSource extends AbstractXlsDataSource
{
	private ReadableWorkbook workbook;
	private Iterator<Sheet> sheets;
	private Iterator<Row> rows;

	private Sheet sheet;
	private Row row;
	private int rowNumber = 0;

	private InputStream inputStream;
	private boolean closeWorkbook;
	private boolean closeInputStream;


	/**
	 * Creates a data source instance from a workbook.
	 * @param workbook the workbook
	 */
	public FastExcelDataSource(ReadableWorkbook workbook)
	{
		this.workbook = workbook;
		this.closeWorkbook = false;
	}


	/**
	 * Creates a data source instance from an XLSX or XLS data input stream.
	 * @param is an input stream containing XLSX or XLS data
	 */
	public FastExcelDataSource(InputStream is) throws JRException, IOException
	{
		this.inputStream = is;
		this.closeWorkbook = true;
		this.closeInputStream = false;

		this.workbook = loadWorkbook(inputStream);
	}


	/**
	 * Creates a data source instance from an XLSX or XLS data input stream.
	 * @param is an input stream containing XLSX or XLS data
	 */
	public FastExcelDataSource(InputStream is, boolean closeInputStream) throws JRException, IOException
	{
		this.inputStream = is;
		this.closeWorkbook = true;
		this.closeInputStream = closeInputStream;

		this.workbook = loadWorkbook(inputStream);
	}


	/**
	 * Creates a data source instance from an XLSX or XLS file.
	 * @param file a file containing XLSX or XLS data
	 */
	public FastExcelDataSource(File file) throws JRException, IOException
	{
		this(new FileInputStream(file));
		this.closeInputStream = true;
	}

	
	/**
	 * Creates a data source instance that reads XLSX or XLS data from a given location.
	 * @param jasperReportsContext the JasperReportsContext
	 * @param location a String representing XLSX or XLS data source
	 * @throws IOException 
	 */
	public FastExcelDataSource(JasperReportsContext jasperReportsContext, String location) throws JRException, IOException
	{
		this(SimpleRepositoryContext.of(jasperReportsContext), location);
	}

	public FastExcelDataSource(RepositoryContext context, String location) throws JRException, IOException
	{
		this(RepositoryUtil.getInstance(context).getInputStreamFromLocation(location));
		this.closeInputStream = true;
	}

	
	/**
	 * @see #FastExcelDataSource(JasperReportsContext, String)
	 */
	public FastExcelDataSource(String location) throws JRException, IOException
	{
		this(DefaultJasperReportsContext.getInstance(), location);
	}
	
	
	/**
	 * 
	 */
	protected ReadableWorkbook loadWorkbook(InputStream is) throws IOException
	{
		return new ReadableWorkbook(is);
	}
	

	@Override
	public boolean next() throws JRException
	{
		if (workbook != null)
		{
			boolean isFirstRow = sheets == null;
			
			if (sheets == null)
			{
				if (sheetSelection == null) 
				{
					sheets = workbook.getSheets().iterator();
					if (sheets.hasNext())
					{
						sheet = sheets.next();
					}
					else
					{
						return false;
					}
				}
				else
				{
					Sheet selectedSheet = null;

					try
					{
						int sheetIndex = Integer.parseInt(sheetSelection);
						selectedSheet = workbook.getSheet(sheetIndex).get();
						if (selectedSheet == null)
						{
							throw 
								new JRRuntimeException(
									EXCEPTION_MESSAGE_KEY_XLS_SHEET_NOT_FOUND,
									new Object[]{sheetSelection});
						}
					}
					catch (NumberFormatException e)
					{
					}
					
					if (selectedSheet == null)
					{
						selectedSheet = workbook.findSheet(sheetSelection).get(); 
						if (selectedSheet == null)
						{
							throw 
								new JRRuntimeException(
									EXCEPTION_MESSAGE_KEY_XLS_SHEET_NOT_FOUND,
									new Object[]{sheetSelection});
						}
					}

					sheets = Collections.singletonList(selectedSheet).iterator();
					sheet = sheets.next();
				}
			}

			if (rows == null)
			{
				try
				{
					rows = sheet.openStream().iterator();
				}
				catch (IOException e)
				{
					throw new JRException(e);
				}
			}
			
			if (rows.hasNext())
			{
				row = rows.next();
				rowNumber++;
			}
			else
			{
				if (sheets.hasNext())
				{
					sheet = sheets.next();
					rows = null;
					rowNumber = 0;
					return next();
				}
				else
				{
					return false;
				}
			}

			if (isFirstRow && useFirstRowAsHeader) 
			{
				readHeader();
				return next();
			}
			
			return true;
		}

		return false;
	}


	@Override
	public void moveFirst()
	{
		sheets = null;
		rows = null;
		rowNumber = 0;
	}


	@Override
	public Object getFieldValue(JRField jrField) throws JRException
	{
		Class<?> valueClass = jrField.getValueClass();
		try 
		{
			Integer columnIndex = getColumnIndex(jrField);

			Cell cell = row.getCell(columnIndex);
			if (cell == null)
			{
				return null;
			}
			CellType cellType = cell.getType();
			if (cellType == CellType.FORMULA) 
			{
				return cell.getValue();
			}
			
			if (cellType == CellType.EMPTY) 
			{
				return null;
			}

			if (valueClass.equals(String.class)) 
			{
				if (cellType == CellType.STRING)
				{
					return cell.asString();
				}
				else
				{
					return cell.getText();
				}
			}
			if (valueClass.equals(Boolean.class)) 
			{
				if (cellType == CellType.BOOLEAN)
				{
					return cell.asBoolean();
				}
				else 
				{
					String value = cell.asString();
					if (value == null || value.trim().length() == 0)
					{
						return null;
					}
					else
					{
						return convertStringValue(value, valueClass);
					}					
				}
			}
			else if (Number.class.isAssignableFrom(valueClass))
			{
				if (cellType == CellType.NUMBER)
				{
					return convertNumber(cell.asNumber(), valueClass);
				}
				else
				{
					String value = cell.asString();
					if (value == null || value.trim().length() == 0)
					{
						return null;
					}
					else
					{
						if (numberFormat != null)
						{
							return FormatUtils.getFormattedNumber(numberFormat, value, valueClass);
						}
						else 
						{
							return convertStringValue(value, valueClass);
						}
					}					
				}
			}
			else if (Date.class.isAssignableFrom(valueClass))
			{
				if (cellType == CellType.NUMBER)
				{
					return java.sql.Timestamp.valueOf(cell.asDate());
				}
				else
				{
					String value = cell.asString();
					if (value == null || value.trim().length() == 0)
					{
						return null;
					}
					else
					{
						if (dateFormat != null)
						{
							return FormatUtils.getFormattedDate(dateFormat, value, valueClass);
						}
						else 
						{
							return convertStringValue(value, valueClass);
						}
					}					
				}
			}
			else if (LocalDateTime.class.isAssignableFrom(valueClass))
			{
				if (cellType == CellType.NUMBER)
				{
					return cell.asDate();
				}
				else
				{
					String value = cell.asString();
					if (value == null || value.trim().length() == 0)
					{
						return null;
					}
					else
					{
						if (dateFormat != null)
						{
							return FormatUtils.getFormattedDate(dateFormat, value, valueClass);
						}
						else 
						{
							return convertStringValue(value, valueClass);
						}
					}					
				}
			}
			else
			{
				throw 
					new JRException(
						EXCEPTION_MESSAGE_KEY_CANNOT_CONVERT_FIELD_TYPE,
						new Object[]{jrField.getName(), valueClass.getName(), "[Sheet:" + sheet.getName() + ", Row:" + rowNumber + "]"}
						);
			}
		}
		catch (Exception e) 
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_XLS_FIELD_VALUE_NOT_RETRIEVED,
					new Object[]{jrField.getName(), valueClass.getName(), "[Sheet:" + sheet.getName() + ", Row:" + rowNumber + "]"}, 
					e
					);
		}
	}


	/**
	 *
	 */
	private void readHeader()
	{
		if (columnNames.size() == 0)
		{
			int columnIndex = 0;
			for (Iterator<Cell> cells = row.iterator(); cells.hasNext(); columnIndex++)
			{
				Cell cell = cells.next();
				if (cell != null)
				{
					columnNames.put(cell.asString(), columnIndex);
				}
				else
				{
					columnNames.put("COLUMN_" + columnIndex, columnIndex);
				}
			}
		}
		else
		{
			Map<String, Integer> newColumnNames = new LinkedHashMap<>();
			for (Integer columnIndex : columnNames.values())
			{
				Cell cell = row.getCell(columnIndex);
				if (cell != null)
				{
					if (cell.getType() == CellType.STRING)
					{
						newColumnNames.put(cell.asString(), columnIndex);
					}
					else
					{
						newColumnNames.put(cell.getText(), columnIndex);
					}
				}
			}
			columnNames = newColumnNames;
		}
	}


	/**
	 * Closes the reader. Users of this data source should close it after usage.
	 */
	@Override
	public void close()
	{
		try
		{
			if (closeInputStream)
			{
				inputStream.close();
			}

			if (closeWorkbook && workbook != null)
			{
				workbook.close();
			}
		}
		catch(IOException e)
		{
			//nothing to do
		}
	}


	@Override
	protected void checkReadStarted()
	{
		if (sheet != null)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_CANNOT_MODIFY_PROPERTIES_AFTER_START,
					(Object[])null);
		}
	}
	
	// only used in JSS, to guess field types
	public String getStringFieldValue(JRField jrField) throws JRException
	{
		try
		{
			Integer columnIndex = getColumnIndex(jrField);
			Cell cell = row.getCell(columnIndex);
			if (cell == null)
			{
				return null;
			}
			else
			{
				return cell.asString();
			}
		}
		catch (Exception e)
		{
			throw
				new JRException(
					EXCEPTION_MESSAGE_KEY_XLS_FIELD_VALUE_NOT_RETRIEVED,
					new Object[]{jrField.getName(), String.class.getName(), "[Sheet:" + sheet.getName() + ", Row:" + rowNumber + "]"},
					e
					);
		}
	}

	// only used in JSS, to guess field types
	public String getFieldFormatPattern(JRField jrField) throws JRException
	{
		try
		{
			Integer columnIndex = getColumnIndex(jrField);
			Cell cell = row.getCell(columnIndex);
			if (cell == null)
			{
				return null;
			}
			else
			{
				return cell.getDataFormatString();
			}
		}
		catch (Exception e)
		{
			throw
				new JRException(
					EXCEPTION_MESSAGE_KEY_XLS_FIELD_VALUE_NOT_RETRIEVED,
					new Object[]{jrField.getName(), String.class.getName(), "[Sheet:" + sheet.getName() + ", Row:" + rowNumber + "]"},
					e
					);
		}
	}
	
}



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
package net.sf.jasperreports.poi.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

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
 * This data source implementation reads an XLSX or XLS stream.
 * <p>
 * The default naming convention is to name report fields COLUMN_x and map each column with the field found at index x 
 * in each row (these indices start with 0). To avoid this situation, users can either specify a collection of column 
 * names or set a flag to read the column names from the first row of the XLSX or XLS file.
 *
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public abstract class AbstractPoiXlsDataSource extends AbstractXlsDataSource
{
	private Workbook workbook;

	private int sheetIndex = -1;
	private int recordIndex = -1;

	private InputStream inputStream;
	private boolean closeWorkbook;
	private boolean closeInputStream;


	/**
	 * Creates a data source instance from a workbook.
	 * @param workbook the workbook
	 */
	public AbstractPoiXlsDataSource(Workbook workbook)
	{
		this.workbook = workbook;
		this.closeWorkbook = false;
	}


	/**
	 * Creates a data source instance from an XLSX or XLS data input stream.
	 * @param is an input stream containing XLSX or XLS data
	 */
	public AbstractPoiXlsDataSource(InputStream is) throws JRException, IOException
	{
		this(is, false);
	}


	/**
	 * Creates a data source instance from an XLSX or XLS data input stream.
	 * @param is an input stream containing XLSX or XLS data
	 */
	public AbstractPoiXlsDataSource(InputStream is, boolean closeInputStream) throws JRException, IOException
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
	public AbstractPoiXlsDataSource(File file) throws JRException, IOException
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
	public AbstractPoiXlsDataSource(JasperReportsContext jasperReportsContext, String location) throws JRException, IOException
	{
		this(SimpleRepositoryContext.of(jasperReportsContext), location);
	}

	public AbstractPoiXlsDataSource(RepositoryContext context, String location) throws JRException, IOException
	{
		this(RepositoryUtil.getInstance(context).getInputStreamFromLocation(location));
		this.closeInputStream = true;
	}

	
	/**
	 * @see #AbstractPoiXlsDataSource(JasperReportsContext, String)
	 */
	public AbstractPoiXlsDataSource(String location) throws JRException, IOException
	{
		this(DefaultJasperReportsContext.getInstance(), location);
	}
	
	
	/**
	 * 
	 */
	protected abstract Workbook loadWorkbook(InputStream is) throws IOException;
	

	@Override
	public boolean next() throws JRException
	{
		if (workbook != null)
		{
			//initialize sheetIndex before first record
			if (sheetIndex < 0)
			{
				if (sheetSelection == null) 
				{
					sheetIndex = 0;
				}
				else
				{
					try
					{
						sheetIndex = Integer.parseInt(sheetSelection);
						if (sheetIndex < 0 || sheetIndex > workbook.getNumberOfSheets() - 1)
						{
							throw 
								new JRRuntimeException(
									EXCEPTION_MESSAGE_KEY_XLS_SHEET_INDEX_OUT_OF_RANGE,
									new Object[]{sheetIndex, (workbook.getNumberOfSheets() - 1)});
						}
					}
					catch (NumberFormatException e)
					{
					}
					
					if (sheetIndex < 0)
					{
						sheetIndex = workbook.getSheetIndex(workbook.getSheet(sheetSelection));

						if (sheetIndex < 0)
						{
							throw 
								new JRRuntimeException(
									EXCEPTION_MESSAGE_KEY_XLS_SHEET_NOT_FOUND,
									new Object[]{sheetSelection});
						}
					}
				}
			}

			recordIndex++;
			
			if (sheetSelection == null) 
			{
				if (recordIndex > workbook.getSheetAt(sheetIndex).getLastRowNum())
				{
					if (sheetIndex + 1 < workbook.getNumberOfSheets() 
						&& workbook.getSheetAt(sheetIndex + 1).getLastRowNum() > 0)
					{
						sheetIndex++;
						recordIndex = -1;
						return next();
					}
				}
			}
			
			if ((sheetSelection != null || sheetIndex == 0) && useFirstRowAsHeader && recordIndex == 0) 
			{
				readHeader();
				recordIndex++;
			}
			if (recordIndex <= workbook.getSheetAt(sheetIndex).getLastRowNum())
			{
				return true;
			}
			else
			{
				if (closeWorkbook)
				{
					//FIXME: close workbook
					//workbook.close();
				}
			}
		}

		return false;
	}


	@Override
	public void moveFirst()
	{
		this.recordIndex = -1;
		this.sheetIndex = -1;
	}


	@Override
	public Object getFieldValue(JRField jrField) throws JRException
	{
		Class<?> valueClass = jrField.getValueClass();
		Sheet sheet = workbook.getSheetAt(sheetIndex);
		try 
		{
			Integer columnIndex = getColumnIndex(jrField);

			Cell cell = sheet.getRow(recordIndex).getCell(columnIndex);
			if (cell == null)
			{
				return null;
			}
			CellType cellType = cell.getCellType();
			if (cellType == CellType.FORMULA) 
			{
				FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
				Object value = null;
				CellType evalCellType = evaluator.evaluateFormulaCell(cell);
				switch (evalCellType) 
				{
				    case BOOLEAN:
				    	value = cell.getBooleanCellValue();
				        break;
				    case NUMERIC:
				    	if (Date.class.isAssignableFrom(valueClass)) 
				    	{
				    		value = cell.getDateCellValue();
				    	} 
				    	else 
				    	{
				    		value = cell.getNumericCellValue();
				    	}
				        break;
				    case STRING:
				    	value = cell.getStringCellValue();
				    	if (Date.class.isAssignableFrom(valueClass))
				    	{
							if (value == null || ((String)value).trim().length() == 0)
							{
								value = null;
							}
							else
							{
								if (dateFormat != null)
								{
									value = FormatUtils.getFormattedDate(dateFormat, (String)value, valueClass);
								}
								else 
								{
									value = convertStringValue((String)value, valueClass);
								}
							}					
				    	} 
				    	else if (Number.class.isAssignableFrom(valueClass))
				    	{
							if (value == null || ((String)value).trim().length() == 0)
							{
								value = null;
							}
							else
							{
								if (numberFormat != null)
								{
									value = FormatUtils.getFormattedNumber(numberFormat, (String)value, valueClass);
								}
								else 
								{
									value = convertStringValue((String)value, valueClass);
								}
							}					
				    	}
				        break;
				    case BLANK:
				    case ERROR:
				    case FORMULA: 
				    default:	
				        break;
				}
				return value;
			}
			
			if (valueClass.equals(String.class)) 
			{
				return cell.getStringCellValue();
			}
			if (valueClass.equals(Boolean.class)) 
			{
				if (cellType == CellType.BOOLEAN)
				{
					return cell.getBooleanCellValue();
				}
				else 
				{
					String value = cell.getStringCellValue();
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
				if (cellType == CellType.NUMERIC)
				{
					return convertNumber(cell.getNumericCellValue(), valueClass);
				}
				else
				{
					String value = cell.getStringCellValue();
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
				if (cellType == CellType.NUMERIC)
				{
					return cell.getDateCellValue();
				}
				else
				{
					String value = cell.getStringCellValue();
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
						new Object[]{jrField.getName(), valueClass.getName(), "[Sheet:" + sheet.getSheetName() + ", Row:" + (recordIndex + 1) + "]"}
						);
			}
		}
		catch (Exception e) 
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_XLS_FIELD_VALUE_NOT_RETRIEVED,
					new Object[]{jrField.getName(), valueClass.getName(), "[Sheet:" + sheet.getSheetName() + ", Row:" + (recordIndex + 1) + "]"}, 
					e
					);
		}
	}


	/**
	 *
	 */
	private void readHeader()
	{
		Sheet sheet = workbook.getSheetAt(sheetSelection != null ? sheetIndex : 0);
		if (columnNames.size() == 0)
		{
			Row row = sheet.getRow(recordIndex);
			for (int columnIndex = 0; columnIndex < row.getLastCellNum(); columnIndex++)
			{
				Cell cell = row.getCell(columnIndex);
				if (cell != null)
				{
					columnNames.put(cell.toString(), columnIndex);
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
			for (Iterator<Integer> it = columnNames.values().iterator(); it.hasNext();)
			{
				Integer columnIndex = it.next();
				Row row = sheet.getRow(recordIndex) ;
				Cell cell = row.getCell(columnIndex);
				if (cell != null)
				{
					newColumnNames.put(cell.toString(), columnIndex);
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
		}
		catch(IOException e)
		{
			//nothing to do
		}
	}


	@Override
	protected void checkReadStarted()
	{
		if (sheetIndex >= 0)
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
		Sheet sheet = workbook.getSheetAt(sheetIndex);
		try
		{
			Integer columnIndex = getColumnIndex(jrField);
			Cell cell = sheet.getRow(recordIndex).getCell(columnIndex);
			if (cell == null)
			{
				return null;
			}
			else
			{
				return cell.toString();
			}
		}
		catch (Exception e)
		{
			throw
				new JRException(
					EXCEPTION_MESSAGE_KEY_XLS_FIELD_VALUE_NOT_RETRIEVED,
					new Object[]{jrField.getName(), String.class.getName(), "[Sheet:" + sheet.getSheetName() + ", Row:" + (recordIndex + 1) + "]"},
					e
					);
		}
	}

	// only used in JSS, to guess field types
	public String getFieldFormatPattern(JRField jrField) throws JRException
	{
		Sheet sheet = workbook.getSheetAt(sheetIndex);
		try
		{
			Integer columnIndex = getColumnIndex(jrField);
			Cell cell = sheet.getRow(recordIndex).getCell(columnIndex);
			if (cell == null)
			{
				return null;
			}
			else
			{
				return cell.getCellStyle().getDataFormatString();
			}
		}
		catch (Exception e)
		{
			throw
				new JRException(
					EXCEPTION_MESSAGE_KEY_XLS_FIELD_VALUE_NOT_RETRIEVED,
					new Object[]{jrField.getName(), String.class.getName(), "[Sheet:" + sheet.getSheetName() + ", Row:" + (recordIndex + 1) + "]"},
					e
					);
		}
	}
	
}



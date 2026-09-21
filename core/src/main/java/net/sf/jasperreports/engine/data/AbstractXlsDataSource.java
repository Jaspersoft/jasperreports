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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRewindableDataSource;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.query.AbstractXlsQueryExecuterFactory;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * This data source implementation reads an XLSX or XLS stream.
 * <p>
 * The default naming convention is to name report fields COLUMN_x and map each column with the field found at index x 
 * in each row (these indices start with 0). To avoid this situation, users can either specify a collection of column 
 * names or set a flag to read the column names from the first row of the XLSX or XLS file.
 *
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public abstract class AbstractXlsDataSource extends JRAbstractTextDataSource implements JRRewindableDataSource
{
	private static final String EXCEPTION_MESSAGE_KEY_XLS_COLUMN_NAMES_MISMATCH_COLUMN_INDEXES = "data.xls.column.names.mismatch.column.indexes";
	protected static final String EXCEPTION_MESSAGE_KEY_XLS_FIELD_VALUE_NOT_RETRIEVED = "data.xls.field.value.not.retrieved";
	protected static final String EXCEPTION_MESSAGE_KEY_XLS_SHEET_INDEX_OUT_OF_RANGE = "data.xls.sheet.index.out.of.range";
	protected static final String EXCEPTION_MESSAGE_KEY_XLS_SHEET_NOT_FOUND = "data.xls.sheet.not.found";
	
	/**
	 * Property specifying the XLS column name for the dataset field.
	 */
	@Property (
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.FIELD},
			scopeQualifications = {AbstractXlsQueryExecuterFactory.QUERY_EXECUTER_NAME,
					DataAdapterServiceConstants.SERVICE_DESIGNATION_XLS},
			sinceVersion = PropertyConstants.VERSION_6_3_1
	)
	public static final String PROPERTY_FIELD_COLUMN_NAME = JRPropertiesUtil.PROPERTY_PREFIX + "xls.field.column.name";
	
	/**
	 * Property specifying the XLS column index for the dataset field.
	 */
	@Property (
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.FIELD},
			scopeQualifications = {AbstractXlsQueryExecuterFactory.QUERY_EXECUTER_NAME,
					DataAdapterServiceConstants.SERVICE_DESIGNATION_XLS},
			sinceVersion = PropertyConstants.VERSION_6_3_1,
			valueType = Integer.class
	)
	public static final String PROPERTY_FIELD_COLUMN_INDEX = JRPropertiesUtil.PROPERTY_PREFIX + "xls.field.column.index";
	
	public static final String INDEXED_COLUMN_PREFIX = "COLUMN_";
	private static final int INDEXED_COLUMN_PREFIX_LENGTH = INDEXED_COLUMN_PREFIX.length();

	protected String sheetSelection;
	
	protected DateFormat dateFormat = new SimpleDateFormat();
	protected NumberFormat numberFormat = new DecimalFormat();//these two here prevent commons beanutils conversion from superclass unless explicitly set to null; but it was like this since day one 
	protected Map<String, Integer> columnNames = new LinkedHashMap<>();
	protected boolean useFirstRowAsHeader;

	protected Map<String,Integer> columnIndexMap = new HashMap<>();
	

	/**
	 * Gets the date format that will be used to parse date fields.
	 */
	public DateFormat getDateFormat()
	{
		return dateFormat;
	}


	/**
	 * Sets the desired date format to be used for parsing date fields.
	 */
	public void setDateFormat(DateFormat dateFormat)
	{
		checkReadStarted();
		
		this.dateFormat = dateFormat;
	}


	/**
	 * Gets the number format that will be used to parse numeric fields.
	 */
	public NumberFormat getNumberFormat() 
	{
		return numberFormat;
	}


	/**
	 * Sets the desired number format to be used for parsing numeric fields.
	 */
	public void setNumberFormat(NumberFormat numberFormat) 
	{
		checkReadStarted();
		
		this.numberFormat = numberFormat;
	}

	
	/**
	 * Specifies an array of strings representing column names matching field names in the report template.
	 */
	public void setColumnNames(String[] columnNames)
	{
		checkReadStarted();
		
		for (int i = 0; i < columnNames.length; i++)
		{
			this.columnNames.put(columnNames[i], i);
		}
	}


	/**
	 * Specifies an array of strings representing column names matching field names in the report template 
	 * and an array of integers representing the column indexes in the sheet.
	 * Both array parameters must be not-null and have the same number of values.
	 */
	public void setColumnNames(String[] columnNames, int[] columnIndexes)
	{
		checkReadStarted();
		
		if (columnNames.length != columnIndexes.length)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_XLS_COLUMN_NAMES_MISMATCH_COLUMN_INDEXES,
					(Object[])null);
		}
		
		for (int i = 0; i < columnNames.length; i++)
		{
			this.columnNames.put(columnNames[i], columnIndexes[i]);
		}
	}


	/**
	 * Specifies an array of integers representing the column indexes in the sheet.
	 */
	public void setColumnIndexes(Integer[] columnIndexes)
	{
		checkReadStarted();
		
		for (int i = 0; i < columnIndexes.length; i++)
		{
			this.columnNames.put("COLUMN_" + i, columnIndexes[i]);
		}
	}


	/**
	 * Specifies whether the first row of the XLS file should be considered a table
	 * header, containing column names matching field names in the report template.
	 */
	public void setUseFirstRowAsHeader(boolean useFirstRowAsHeader)
	{
		checkReadStarted();
		
		this.useFirstRowAsHeader = useFirstRowAsHeader;
	}


	/**
	 * Closes the reader. Users of this data source should close it after usage.
	 */
	public abstract void close();

	
	protected abstract void checkReadStarted();

	
	public Map<String, Integer> getColumnNames() 
	{
		return columnNames;
	}
	
	public String getSheetSelection() 
	{
		return sheetSelection;
	}


	public void setSheetSelection(String sheetSelection) 
	{
		checkReadStarted();

		this.sheetSelection = sheetSelection;
	}
	

	protected Integer getColumnIndex(JRField field) throws JRException
	{
		String fieldName = field.getName();
		Integer columnIndex = columnIndexMap.get(fieldName);
		if (columnIndex == null)
		{
			if (field.hasProperties())
			{
				String columnName = field.getPropertiesMap().getProperty(PROPERTY_FIELD_COLUMN_NAME);
				if (columnName != null)
				{
					columnIndex = columnNames.get(columnName);
					if (columnIndex == null)
					{
						throw 
							new JRException(
								EXCEPTION_MESSAGE_KEY_UNKNOWN_COLUMN_NAME,
								new Object[]{columnName});
					}
				}
			}

			if (columnIndex == null)
			{
				if (field.hasProperties())
				{
					String index = field.getPropertiesMap().getProperty(PROPERTY_FIELD_COLUMN_INDEX);
					if (index != null)
					{
						columnIndex = Integer.valueOf(index);
					}
				}
			}
			
			if (columnIndex == null)
			{
				columnIndex = columnNames.get(fieldName);
			}
			
			if (columnIndex == null && fieldName.startsWith(INDEXED_COLUMN_PREFIX))
			{
				columnIndex = Integer.valueOf(fieldName.substring(INDEXED_COLUMN_PREFIX_LENGTH));
			}
			
			if (columnIndex == null)
			{
				throw 
					new JRException(
						EXCEPTION_MESSAGE_KEY_UNKNOWN_COLUMN_NAME,
						new Object[]{fieldName});
			}

			columnIndexMap.put(fieldName, columnIndex);
		}
		
		return columnIndex;
	}
}



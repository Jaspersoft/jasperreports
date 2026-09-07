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
package net.sf.jasperreports.engine.export;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.util.JRStringUtil;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.export.CsvMetadataExporterConfiguration;
import net.sf.jasperreports.export.CsvMetadataReportConfiguration;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * Exports a JasperReports document to CSV format based on the metadata provided.
 * <p/>
 * The exporter allows users to specify which columns should be included in the CSV export, what other value than the default
 * should they contain and whether the values for some columns should be auto filled when they are empty or missing (e.g. value 
 * for group columns)
 * 
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class JRCsvMetadataExporter extends JRAbstractCsvExporter<CsvMetadataReportConfiguration, CsvMetadataExporterConfiguration, JRCsvExporterContext>
{
	/**
	 * Property specifying the name of the column that should appear in the CSV export.
	 * It must be one of the values in {@link CsvMetadataReportConfiguration#getColumnNames()}, if provided. 
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.TEXT_ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_0_0
			)
	public static final String PROPERTY_COLUMN_NAME = JRPropertiesUtil.PROPERTY_PREFIX + "export.csv.column.name";
	
	/**
	 * Property that specifies whether the value associated with {@link #PROPERTY_COLUMN_NAME PROPERTY_COLUMN_NAME} should be repeated or not
	 * when it is missing.
	 * <p>
	 * The property itself defaults to <code>false</code>.
	 * </p>
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.TEXT_ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_0_0,
			valueType = Boolean.class
			)
	public static final String PROPERTY_REPEAT_VALUE = JRPropertiesUtil.PROPERTY_PREFIX + "export.csv.repeat.value";
	
	/**
	 * Property that specifies what value to associate with {@link #PROPERTY_COLUMN_NAME PROPERTY_COLUMN_NAME}.
	 * <p>
	 * The property itself defaults to the text value of the report element that this property is assigned to.
	 * </p>
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.TEXT_ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_0_0
			)
	public static final String PROPERTY_DATA = JRPropertiesUtil.PROPERTY_PREFIX + "export.csv.data";

	/**
	 * 
	 */
	protected List<String> columnNames;
	
	boolean isFirstRow = true;
	

	protected class ExporterContext extends BaseExporterContext implements JRCsvExporterContext
	{
	}

	/**
	 * @see #JRCsvMetadataExporter(JasperReportsContext)
	 */
	public JRCsvMetadataExporter()
	{
		this(DefaultJasperReportsContext.getInstance());
	}

	
	/**
	 *
	 */
	public JRCsvMetadataExporter(JasperReportsContext jasperReportsContext)
	{
		super(jasperReportsContext);

		exporterContext = new ExporterContext();
	}


	@Override
	protected Class<CsvMetadataExporterConfiguration> getConfigurationInterface()
	{
		return CsvMetadataExporterConfiguration.class;
	}
	

	@Override
	protected Class<CsvMetadataReportConfiguration> getItemConfigurationInterface()
	{
		return CsvMetadataReportConfiguration.class;
	}
	

	@Override
	protected void exportPage(JRPrintPage page) throws IOException
	{
		List<JRPrintElement> elements = page.getElements();
		Map<String, String> currentRow = new HashMap<>();
		Map<String, String> repeatedValues = new HashMap<>();
		CsvMetadataReportConfiguration configuration = getCurrentItemConfiguration(); 
		boolean hasDefinedColumns = columnNames != null; // if columns where passed in as property
		
		exportElements(elements, configuration, currentRow, repeatedValues, hasDefinedColumns);
		

		// write last row
		if (columnNames != null && columnNames.size() > 0)
		{
			// write header if it was not yet written
			if (isFirstRow && configuration.isWriteHeader())
			{
				writeReportHeader();
				isFirstRow = false;
			}
			writeCurrentRow(currentRow, repeatedValues);
		}

		JRExportProgressMonitor progressMonitor  = configuration.getProgressMonitor();
		if (progressMonitor != null)
		{
			progressMonitor.afterPageExport();
		}
	}
	
	protected void exportElements(
			List<JRPrintElement> elements, 
			CsvMetadataReportConfiguration configuration,
			Map<String, String> currentRow,
			Map<String, String> repeatedValues,
			boolean hasDefinedColumns)  throws IOException
	{
		
		for (Object element : elements) 
		{
			if (element instanceof JRPrintText) 
			{
				exportText((JRPrintText) element, configuration, currentRow, repeatedValues, hasDefinedColumns);
			}
			else if (element instanceof JRPrintFrame)
			{
				exportElements(((JRPrintFrame) element).getElements(), configuration, currentRow, repeatedValues, hasDefinedColumns);
			}
		}		
	}
	
	protected void exportText(
			JRPrintText textElement,
			CsvMetadataReportConfiguration configuration,
			Map<String, String> currentRow,
			Map<String, String> repeatedValues,
			boolean hasDefinedColumns)  throws IOException
	{
		
		String currentTextValue = null;

		if (textElement.getPropertiesMap().getPropertyNames().length > 0) 
		{
			String currentColumnName = textElement.getPropertiesMap().getProperty(PROPERTY_COLUMN_NAME);
			String currentColumnData = textElement.getPropertiesMap().getProperty(PROPERTY_DATA);
			boolean repeatValue = getPropertiesUtil().getBooleanProperty(textElement, PROPERTY_REPEAT_VALUE, false);
			
			if (textElement.getPropertiesMap().containsProperty(PROPERTY_DATA))
			{
				currentTextValue = currentColumnData;
				
			} else
			{
				JRStyledText styledText = getStyledText(textElement);
				
				if (styledText != null)
				{
					currentTextValue = styledText.getText();
				} else
				{
					currentTextValue = "";
				}
			}
			// when no columns are provided, build the column names list as they are retrieved from the report element property
			if (!hasDefinedColumns)
			{
				if (columnNames == null) 
				{
					columnNames = new ArrayList<>();
				}
				
				if (currentColumnName != null && currentColumnName.length() > 0 && !columnNames.contains(currentColumnName))
				{
					columnNames.add(currentColumnName);
				}
			}
			
			if (columnNames.size() > 0)
			{
				if (columnNames.contains(currentColumnName) && !currentRow.containsKey(currentColumnName) && isColumnReadOnTime(currentRow, currentColumnName)) // the column is for export but was not read yet and comes in the expected order
				{
					currentRow.put(currentColumnName, currentTextValue);
						
				} else if ( (columnNames.contains(currentColumnName) && !currentRow.containsKey(currentColumnName) && !isColumnReadOnTime(currentRow, currentColumnName)) // the column is for export, was not read yet, but it is read after it should be
						|| (columnNames.contains(currentColumnName) && currentRow.containsKey(currentColumnName)) ) // the column is for export and was already read
				{
					// write header 
					if (isFirstRow && configuration.isWriteHeader())
					{
						writeReportHeader();
					}
					
					isFirstRow = false;
					writeCurrentRow(currentRow, repeatedValues);
					currentRow.clear();
					currentRow.put(currentColumnName, currentTextValue);
				}
				// set auto fill columns
				if (repeatValue && currentColumnName != null && currentColumnName.length() > 0 && currentTextValue.length() > 0)
				{
					repeatedValues.put(currentColumnName, currentTextValue);
				}
			}
		}		
	}
	
	
	@Override
	protected void initExport() 
	{
		super.initExport();
	}
	
	
	@Override
	protected void initReport() 
	{
		super.initReport();

		CsvMetadataReportConfiguration configuration = getCurrentItemConfiguration();
		
		columnNames = JRStringUtil.split(configuration.getColumnNames(), ",");

		isFirstRow = true;
	}

	
	/**
	 * Writes the delimiter-separated column names
	 */
	protected void writeReportHeader() throws IOException 
	{
		StringBuilder rowBuilder = new StringBuilder();
		
		for (int i = 0; i < columnNames.size(); i++)
		{
			rowBuilder.append(columnNames.get(i));

			if (i < (columnNames.size()-1))
			{
				rowBuilder.append(fieldDelimiter);
			} else
			{
				rowBuilder.append(recordDelimiter);
			}
		}
		
		if (rowBuilder.length() > 0)
		{
			writer.write(rowBuilder.toString());
		}
	}
	

	/**
	 * Writes the current row.
	 * <p/>
	 * If the row is empty, nothing is written. If the are columns for auto fill (with valid data), they will be set on the current 
	 * row and the row will be written only if it was not originally empty. This prevents the export file from having rows just with auto filled data.
	 * <p/> 
	 * @param currentRow
	 * @param repeatedValues
	 * @throws IOException
	 */
	protected void writeCurrentRow(Map<String, String> currentRow, Map<String, String> repeatedValues) throws IOException
	{
		// FIXME: the rows that are incomplete (e.g. in case of a group, there are rows that contain only the group columns 
		// because the report spanned over a new page and it contains only the values for the group columns, as header, and other information
		// that is not for export, like counts or totals) should not be written

		StringBuilder rowBuilder = new StringBuilder();
		boolean isEmptyRow = true;
		
		for (int i = 0; i < columnNames.size(); i++)
		{
			String currentTextValue = currentRow.get(columnNames.get(i));
			if (currentTextValue != null && currentTextValue.length() > 0)
			{
				isEmptyRow = false;
				rowBuilder.append(prepareText(currentTextValue));
			} else
			{
				String repeatedValue = repeatedValues.get(columnNames.get(i));
				if (repeatedValue != null && repeatedValue.length() > 0)
				{
					rowBuilder.append(prepareText(repeatedValue));
				}
			}
			
			if (i < (columnNames.size()-1))
			{
				rowBuilder.append(fieldDelimiter);
			} else
			{
				rowBuilder.append(recordDelimiter);
			}
		}
		
		if (!isEmptyRow)
		{
			writer.write(rowBuilder.toString());
		}
	}
	
	
	/**
	 * Compares the highest index of the currentRow's columns with the index of the column to be inserted
	 * to determine if the current column is read in the proper order
	 * </p>
	 * @param currentRow
	 * @param currentColumnName
	 */
	private boolean isColumnReadOnTime(Map<String, String> currentRow, String currentColumnName)
	{
		int indexOfLastFilledColumn = -1;
		Set<String> currentlyFilledColumns = currentRow.keySet();
		
		for (String column: currentlyFilledColumns)
		{
			indexOfLastFilledColumn = Math.max(indexOfLastFilledColumn, columnNames.indexOf(column));
		}
		
		return indexOfLastFilledColumn < columnNames.indexOf(currentColumnName);
	}
}

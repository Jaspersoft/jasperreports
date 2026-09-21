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
package net.sf.jasperreports.engine.fill;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.data.IndexedDataSource;
import net.sf.jasperreports.engine.data.RandomAccessDataSource;
import net.sf.jasperreports.engine.fill.DatasetSortInfo.RecordField;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class SortedDataSource implements JRRewindableDataSource, IndexedDataSource
{
	
	private static final Log log = LogFactory.getLog(SortedDataSource.class);
	
	private static final String EXCEPTION_MESSAGE_KEY_FIELD_NOT_FOUND = "data.sorted.field.not.found";
	
	public static class SortRecord
	{
		private final Object[] values;
		private int originalRecordIndex;
		private int recordIndex;
		private boolean filtered;
		
		public SortRecord(Object[] values, int recordIndex)
		{
			this.values = values;
			this.originalRecordIndex = recordIndex;
			this.recordIndex = recordIndex;
			this.filtered = false;
		}
		
		protected void markFiltered()
		{
			filtered = true;
		}

		public Object fieldValue(int fieldIdx)
		{
			return values[fieldIdx];
		}
		
		public Object[] getValues()
		{
			return values;
		}

		protected void setRecordIndex(int recordIndex)
		{
			this.recordIndex = recordIndex;
		}

		public int getRecordIndex()
		{
			return recordIndex;
		}

		public boolean isFiltered()
		{
			return filtered;
		}
	}
	
	private final JRDataSource originalDataSource;
	private final List<SortRecord> records;
	private final Integer[] recordIndexes;
	private final Map<String, Integer> columnNamesMap = new HashMap<>();
	
	private int currentIndex;
	private SortRecord currentRecord;
	
	public SortedDataSource(DatasetSortInfo sortInfo, 
			List<SortRecord> records, Integer[] recordIndexes)
	{
		if (records.size() != recordIndexes.length)
		{
			throw new IllegalArgumentException("Record count " + records.size() 
					+ " doesn't match index count " + recordIndexes.length);
		}

		this.originalDataSource = sortInfo.getOriginalDataSource();
		this.records = records;
		this.recordIndexes = recordIndexes;
		
		for (ListIterator<RecordField> it = sortInfo.getRecordFields().listIterator(); it.hasNext();)
		{
			RecordField recordField = it.next();
			if (!recordField.isVariable())
			{
				columnNamesMap.put(recordField.getName(), it.previousIndex());
			}
		}

		this.currentIndex = 0;
	}

	public JRDataSource getOriginalDataSource() throws JRException
	{
		if (currentRecord != null && originalDataSource instanceof RandomAccessDataSource)
		{
			RandomAccessDataSource dataSource = (RandomAccessDataSource) originalDataSource;
			if (dataSource.currentIndex() != currentRecord.originalRecordIndex)
			{
				if (log.isDebugEnabled())
				{
					log.debug("moving original data source to record " + currentRecord.originalRecordIndex);
				}
				dataSource.moveToRecord(currentRecord.originalRecordIndex);
			}
		}
		
		return originalDataSource;
	}
	
	@Override
	public boolean next()
	{
		if (currentIndex >= recordIndexes.length)
		{
			return false;
		}
		
		int recordIndex = recordIndexes[currentIndex];
		// assuming random access
		currentRecord = records.get(recordIndex);
		++currentIndex;
		return true;
	}

	public void setRecordFilteredIndex(int index)
	{
		currentRecord.markFiltered();
		currentRecord.setRecordIndex(index);
	}

	@Override
	public Object getFieldValue(JRField jrField)
	{
		Integer fieldIndex = columnNamesMap.get(jrField.getName());
		if (fieldIndex == null)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_FIELD_NOT_FOUND,
					new Object[]{jrField.getName()});
		}
		return currentRecord.fieldValue(fieldIndex);
	}

	@Override
	public void moveFirst()
	{
		currentIndex = 0;
	}

	@Override
	public int getRecordIndex()
	{
		return currentRecord.getRecordIndex();
	}

	public List<SortRecord> getRecords()
	{
		return records;
	}
}
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
package net.sf.jasperreports.engine.fill;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRSortField;
import net.sf.jasperreports.engine.type.SortOrderEnum;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class DatasetSortInfo
{
	
	public static class SortFieldInfo
	{
		private final JRSortField sortField;
		private final int recordIndex;
		private final boolean collatorFlag;
		
		protected SortFieldInfo(JRSortField sortField, int recordIndex, boolean collatorFlag)
		{
			this.sortField = sortField;
			this.recordIndex = recordIndex;
			this.collatorFlag = collatorFlag;
		}

		public int getRecordIndex()
		{
			return recordIndex;
		}
		
		public boolean isDescending()
		{
			return SortOrderEnum.DESCENDING == sortField.getOrder();
		}

		public boolean useCollator()
		{
			return collatorFlag;
		}
	}

	public static class RecordField
	{
		private final String name;
		private final boolean isVariable;
		
		protected RecordField(String name, boolean isVariable)
		{
			this.name = name;
			this.isVariable = isVariable;
		}

		public String getName()
		{
			return name;
		}

		public boolean isVariable()
		{
			return isVariable;
		}
	}
	
	private JRDataSource originalDataSource;
	private List<RecordField> recordFields = new ArrayList<>();
	private List<SortFieldInfo> sortFields = new ArrayList<>();

	public void addSortField(JRSortField sortField, int recordIndex, boolean collatorFlag)
	{
		SortFieldInfo info = new SortFieldInfo(sortField, recordIndex, collatorFlag);
		sortFields.add(info);
	}

	public List<SortFieldInfo> getSortFields()
	{
		return sortFields;
	}
	
	public void addRecordField(String name)
	{
		RecordField recordField = new RecordField(name, false);
		recordFields.add(recordField);
	}
	
	public int addRecordVariable(String name)
	{
		int index = recordFields.size();
		RecordField recordField = new RecordField(name, true);
		recordFields.add(recordField);
		return index;
	}
	
	public List<RecordField> getRecordFields()
	{
		return recordFields;
	}

	public JRDataSource getOriginalDataSource()
	{
		return originalDataSource;
	}

	public void setOriginalDataSource(JRDataSource originalDataSource)
	{
		this.originalDataSource = originalDataSource;
	}

}

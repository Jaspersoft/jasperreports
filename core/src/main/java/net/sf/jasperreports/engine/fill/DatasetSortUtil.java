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

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRScriptletException;
import net.sf.jasperreports.engine.JRSortField;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.design.JRDesignDatasetRun;
import net.sf.jasperreports.engine.fill.DatasetSortInfo.RecordField;
import net.sf.jasperreports.engine.fill.DatasetSortInfo.SortFieldInfo;
import net.sf.jasperreports.engine.fill.SortedDataSource.SortRecord;
import net.sf.jasperreports.engine.type.SortFieldTypeEnum;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class DatasetSortUtil
{
	public static final String EXCEPTION_MESSAGE_KEY_SORT_FIELD_NOT_FOUND = "fill.dataset.sort.field.not.found";
	public static final String EXCEPTION_MESSAGE_KEY_SORT_VARIABLE_NOT_FOUND = "fill.dataset.sort.variable.not.found";

	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			defaultValue = "true",
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.FIELD},
			sinceVersion = PropertyConstants.VERSION_6_21_6,
			valueType = Boolean.class
			)
	public static final String PROPERTY_USE_COLLATOR = JRPropertiesUtil.PROPERTY_PREFIX + "sorting.use.collator";

	/**
	 * Returns all current sort field criteria, including the dynamic ones provided as report parameter.
	 */
	public static JRSortField[] getAllSortFields(JRFillDataset dataset)
	{
		List<JRSortField> allSortFields = new ArrayList<>();
		
		JRSortField[] staticSortFields = dataset.getSortFields();
		if (staticSortFields != null)
		{
			allSortFields.addAll(Arrays.asList(staticSortFields));
		}

		@SuppressWarnings("unchecked")
		List<JRSortField> dynamicSortFields = (List<JRSortField>)dataset.getParameterValue(JRParameter.SORT_FIELDS, true);
		if (dynamicSortFields != null)
		{
			allSortFields.addAll(dynamicSortFields);
		}

		return allSortFields.toArray(new JRSortField[allSortFields.size()]);
	}


	/**
	 *
	 */
	public static boolean needSorting(JRFillDataset dataset)
	{
		JRSortField[] staticSortFields = dataset.getSortFields();
		@SuppressWarnings("unchecked")
		List<JRSortField> dynamicSortFields = (List<JRSortField>)dataset.getParameterValue(JRParameter.SORT_FIELDS, true);
		
		return 
			(staticSortFields != null
			&& staticSortFields.length > 0)
			|| (dynamicSortFields != null
			&& dynamicSortFields.size() > 0);
	}


	/**
	 *
	 */
	public static SortedDataSource getSortedDataSource(
		BaseReportFiller filler, 
		JRFillDataset dataset, 
		Locale locale 
		) throws JRException
	{
		DatasetSortInfo sortInfo = createSortInfo(dataset);
		
		SortFillDatasetRun sortDatasetRun = new SortFillDatasetRun(filler, dataset, sortInfo);
		
		List<SortedDataSource.SortRecord> records = sortDatasetRun.sort();
		
		// using indirect sorting in order to also preserve the original record order for data caching
		int recordCount = records.size();
		// we need wrapper objects for Arrays.sort with comparator
		Integer[] indexes = new Integer[recordCount];
		for (int i = 0; i < recordCount; i++) {
			indexes[i] = i;
		}
		
		/*   */
		Arrays.sort(
			indexes, 
			new DataSourceComparator(
				sortInfo, 
				locale,
				records
				)
			);
		
		return new SortedDataSource(sortInfo, records, indexes);
	}


	/**
	 *
	 */
	private static DatasetSortInfo createSortInfo(JRFillDataset dataset) throws JRException
	{
		DatasetSortInfo sortInfo = new DatasetSortInfo();
		sortInfo.setOriginalDataSource(dataset.dataSource);

		Map<String, JRField> fieldsMap = new HashMap<>();
		Map<String, Integer> fieldIndexMap = new HashMap<>();
		JRField[] fields = dataset.getFields();
		if (fields != null)
		{
			for (int i = 0; i < fields.length; i++)
			{
				JRField field = fields[i];
				fieldsMap.put(field.getName(), field);
				fieldIndexMap.put(field.getName(), i);
				sortInfo.addRecordField(field.getName());
			}
		}

		Map<String, JRVariable> variablesMap = new HashMap<>();
		JRVariable[] variables = dataset.getVariables();
		if (variables != null)
		{
			for (int i = 0; i < variables.length; i++)
			{
				variablesMap.put(variables[i].getName(), variables[i]);
			}
		}

		JRSortField[] sortFields = getAllSortFields(dataset);
		if (sortFields != null)
		{
			for (int i = 0; i < sortFields.length; i++)
			{
				JRSortField sortField = sortFields[i];

				String sortFieldName = sortField.getName();
				boolean collatorFlag = false;
				int recordIndex;
				if (sortField.getType() == SortFieldTypeEnum.VARIABLE)
				{
					JRVariable variable = variablesMap.get(sortFieldName);
					if (variable == null)
					{
						throw 
							new JRRuntimeException(
								EXCEPTION_MESSAGE_KEY_SORT_VARIABLE_NOT_FOUND,
								new Object[]{sortFieldName});
					}
					
					recordIndex = sortInfo.addRecordVariable(variable.getName());
					if (String.class.getName().equals(variable.getValueClassName()))
					{
						collatorFlag = dataset.getPropertiesUtil().getBooleanProperty(
								PROPERTY_USE_COLLATOR, true, dataset);
					}
				}
				else
				{
					JRField field = fieldsMap.get(sortFieldName);
					if (field == null)
					{
						throw 
							new JRRuntimeException(
								EXCEPTION_MESSAGE_KEY_SORT_FIELD_NOT_FOUND,
								new Object[]{sortFieldName});
					}
					
					recordIndex = fieldIndexMap.get(sortField.getName());
					if (String.class.getName().equals(field.getValueClassName()))
					{
						collatorFlag = dataset.getPropertiesUtil().getBooleanProperty(
								PROPERTY_USE_COLLATOR, true, field, dataset);
					}
				}

				sortInfo.addSortField(sortField, recordIndex, collatorFlag);
			}
		}
		
		return sortInfo;
	}

	
}


/**
 *
 */
class DataSourceComparator implements Comparator<Integer>
{
	private final Collator collator;
	private final List<SortFieldInfo> sortFields;
	private final List<SortedDataSource.SortRecord> records;

	public DataSourceComparator(DatasetSortInfo sortFieldInfo, Locale locale, 
			List<SortedDataSource.SortRecord> records)
	{
		this.collator = Collator.getInstance(locale);
		this.sortFields = sortFieldInfo.getSortFields();
		this.records = records;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int compare(Integer idx1, Integer idx2)
	{
		// assuming random access records list
		Object[] record1 = records.get(idx1).getValues();
		Object[] record2 = records.get(idx2).getValues();
		
		int ret = 0;

		for (SortFieldInfo info : sortFields)
		{
			Comparable field1 = (Comparable)record1[info.getRecordIndex()];
			Comparable field2 = (Comparable)record2[info.getRecordIndex()];

			if (field1 == null)
			{
				ret = (field2 == null) ? 0 : -1;
			}
			else if (field2 == null)
			{
				ret = 1;
			}
			else
			{
				if (info.useCollator())
				{
					ret = collator.compare(field1, field2);
				}
				else
				{
					ret = field1.compareTo(field2);
				}
			}

			if (ret != 0)
			{
				if (info.isDescending())
				{
					ret = -ret;
				}
				
				return ret;
			}
		}

		return ret;
	}
}


/**
 * Used to iterate on a subdataset and create a sorted data source.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
class SortFillDatasetRun extends JRFillDatasetRun
{

	private DatasetSortInfo sortInfo;
	private int recordIndex;
	private List<SortedDataSource.SortRecord> records;

	
	public SortFillDatasetRun(BaseReportFiller filler, JRFillDataset dataset, DatasetSortInfo sortInfo) throws JRException
	{
		super(
			filler, 
			new JRDesignDatasetRun(), //we don't need anything from a dataset run. just avoid NPEs down the call 
			dataset
			);

		this.sortInfo = sortInfo;
	}

	
	public List<SortedDataSource.SortRecord> sort() throws JRException
	{
		recordIndex = 0;
		records = new ArrayList<>();

		try
		{
			//all parameters are already set onto the dataset by the main fill process

			iterate();
		}
		finally
		{
			dataset.closeQueryExecuter();
			dataset.reset();
		}
		
		return records;
	}

	
	@Override
	protected void detail() throws JRScriptletException, JRException 
	{
		super.detail();
		
		List<RecordField> recordFields = sortInfo.getRecordFields();
		int fieldCount = recordFields.size();
		
		Object[] record = new Object[fieldCount];
		int index = 0;
		for (RecordField recordField : recordFields)
		{
			Object value;
			if (recordField.isVariable())
			{
				value = dataset.getVariableValue(recordField.getName());
			}
			else
			{
				value = dataset.getFieldValue(recordField.getName());
			}
			
			record[index] = value;
			++index;
		}
		
		// also store the original record index
		SortRecord sortRecord = new SortedDataSource.SortRecord(record, recordIndex);
		
		++recordIndex;
		
		records.add(sortRecord);
	}


	@Override
	protected boolean advanceDataset() throws JRException
	{
		// do not filter records before sorting in order to support "Top 5 sorted" cases.
		// FIXME optimize by filtering when the filters are on fields (and other cases).
		return dataset.next(true);
	}


	@Override
	protected boolean toStartWhenNoData()
	{
		//not needed for sorting
		return false;
	}
	
	
}
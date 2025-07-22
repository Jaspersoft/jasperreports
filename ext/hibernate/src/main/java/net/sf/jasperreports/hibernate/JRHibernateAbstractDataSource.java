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
package net.sf.jasperreports.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.data.JRAbstractBeanDataSource;

/**
 * Base abstract Hibernate data source.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public abstract class JRHibernateAbstractDataSource implements JRDataSource
{
	public static final String EXCEPTION_MESSAGE_KEY_FIELD_ALIAS_TYPE_MISMATCH = "data.hibernate.field.alias.type.mismatch";
	public static final String EXCEPTION_MESSAGE_KEY_MANY_FIELDS_DETECTED = "data.hibernate.many.fields.detected";
	public static final String EXCEPTION_MESSAGE_KEY_NO_FIELD_ALIAS = "data.hibernate.no.field.alias";
	public static final String EXCEPTION_MESSAGE_KEY_NO_FIELD_READER = "data.hibernate.no.field.reader";
	public static final String EXCEPTION_MESSAGE_KEY_UNKNOWN_RETURN_ALIAS = "data.hibernate.unknown.return.alias";
	
	public static final char NESTED_DELIM = '.';
	
	private final JRAbstractBeanDataSource.PropertyNameProvider propertyNameProvider;
	private Map<String, FieldReader> fieldReaders;
	protected final JRHibernateQueryExecuter queryExecuter;
	private Tuple currentReturnValue;
	
	
	/**
	 * Creates a Hibernate data source.
	 * 
	 * @param queryExecuter the query executer
	 * @param useFieldDescription whether to use field descriptions for fields to results mapping
	 */
	protected JRHibernateAbstractDataSource(JRHibernateQueryExecuter queryExecuter, boolean useFieldDescription)
	{
		this.propertyNameProvider = new JRAbstractBeanDataSource.DefaultPropertyNameProvider(useFieldDescription);
		
		this.queryExecuter = queryExecuter;
	}
	
	/**
	 * Assigns field readers to report fields.
	 * 
	 * @return a report field name to field reader mapping
	 * @see FieldReader
	 */
	protected Map<String, FieldReader> assignReaders(Tuple result)
	{
		Map<String, FieldReader> readers = new HashMap<>();
		
		JRField[] fields = queryExecuter.getDataset().getFields();
		List<TupleElement<?>> elements = result.getElements();
		
		Map<String, Integer> aliasesMap = new HashMap<>();
		for (ListIterator<TupleElement<?>> iterator = elements.listIterator(); iterator.hasNext();)
		{
			TupleElement<?> tupleElement = (TupleElement<?>) iterator.next();
			aliasesMap.put(tupleElement.getAlias(), iterator.previousIndex());
		}
		
		if (elements.size() == 1)
		{
			TupleElement<?> element = elements.get(0);
			
			boolean managedType = isManagedType(element);
			if (managedType)
			{
				for (int i = 0; i < fields.length; i++)
				{
					JRField field = fields[i];
					readers.put(field.getName(), getFieldReaderSingleReturn(aliasesMap, field));
				}
			}
			else
			{
				if (fields.length > 1)
				{
					throw 
						new JRRuntimeException(
							EXCEPTION_MESSAGE_KEY_MANY_FIELDS_DETECTED,
							(Object[])null);
				}
				
				if (fields.length == 1)
				{
					JRField field = fields[0];
					readers.put(field.getName(), new IndexFieldReader(0));
				}
			}
		}
		else
		{
			for (int i = 0; i < fields.length; i++)
			{
				JRField field = fields[i];
				readers.put(field.getName(), getFieldReader(elements, aliasesMap, field));				
			}
		}

		return readers;
	}

	protected boolean isManagedType(TupleElement<?> element)
	{
		try
		{
			queryExecuter.getSession().getMetamodel().managedType(element.getJavaType());
			return true;
		}
		catch (IllegalArgumentException e)
		{
			return false;
		}
	}

	protected FieldReader getFieldReaderSingleReturn(Map<String,Integer> aliasesMap, JRField field)
	{
		FieldReader reader;
		
		String fieldMapping = getFieldMapping(field);
		if (aliasesMap.containsKey(fieldMapping))
		{
			reader = new IndexFieldReader(0);
		}
		else
		{
			int firstNestedIdx = fieldMapping.indexOf(NESTED_DELIM);

			if (firstNestedIdx >= 0 && aliasesMap.containsKey(fieldMapping.substring(0, firstNestedIdx)))
			{
				fieldMapping = fieldMapping.substring(firstNestedIdx + 1);
			}

			reader = new IndexPropertyFieldReader(0, fieldMapping);
		}
		
		return reader;
	}

	protected FieldReader getFieldReader(List<TupleElement<?>> elements, Map<String,Integer> aliasesMap, JRField field)
	{
		FieldReader reader;
		
		String fieldMapping = getFieldMapping(field);
		Integer fieldIdx = aliasesMap.get(fieldMapping);
		if (fieldIdx == null)
		{
			int firstNestedIdx = fieldMapping.indexOf(NESTED_DELIM);
			
			if (firstNestedIdx < 0)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_UNKNOWN_RETURN_ALIAS,
						new Object[]{fieldMapping});
			}
			
			String fieldAlias = fieldMapping.substring(0, firstNestedIdx);
			String fieldProperty = fieldMapping.substring(firstNestedIdx + 1);
			
			fieldIdx = aliasesMap.get(fieldAlias);
			if (fieldIdx == null)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_NO_FIELD_ALIAS,
						new Object[]{fieldAlias});
			}
			
			if (!isManagedType(elements.get(fieldIdx)))
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_FIELD_ALIAS_TYPE_MISMATCH,
						new Object[]{fieldAlias});
			}
			
			reader = new IndexPropertyFieldReader(fieldIdx, fieldProperty);
		}
		else
		{
			reader = new IndexFieldReader(fieldIdx);
		}
		
		return reader;
	}

	
	/**
	 * Sets the current row of the query result.
	 * 
	 * @param currentReturnValue the current row value
	 */
	protected void setCurrentRowValue(Tuple currentReturnValue)
	{
		this.currentReturnValue = currentReturnValue;
	}

	protected FieldReader fieldReader(JRField jrField)
	{
		if (fieldReaders == null)
		{
			fieldReaders = assignReaders(currentReturnValue);
		}
		
		return fieldReaders.get(jrField.getName());
	}	
	
	@Override
	public Object getFieldValue(JRField jrField) throws JRException
	{
		FieldReader reader = fieldReader(jrField);
		if (reader == null)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_NO_FIELD_READER,
					new Object[]{jrField.getName()});
		}
		return reader.getFieldValue(currentReturnValue);
	}

	
	protected String getFieldMapping(JRField field)
	{
		return propertyNameProvider.getPropertyName(field);
	}
	
	
	/**
	 * Interface used to get the value of a report field from a result row.
	 */
	protected static interface FieldReader
	{
		Object getFieldValue(Tuple resultTuple) throws JRException;
	}
	
	protected static class IndexFieldReader implements FieldReader
	{
		private final int idx;

		protected IndexFieldReader(int idx)
		{
			this.idx = idx;
		}
		
		@Override
		public Object getFieldValue(Tuple resultTuple)
		{
			return resultTuple.get(idx);
		}
	}
	
	protected static class IndexPropertyFieldReader implements FieldReader
	{
		private final int idx;
		private final String property;

		protected IndexPropertyFieldReader(int idx, String property)
		{
			this.idx = idx;
			this.property = property;
		}
		
		@Override
		public Object getFieldValue(Tuple resultTuple) throws JRException
		{
			Object tupleValue = resultTuple.get(idx);
			return JRAbstractBeanDataSource.getBeanProperty(tupleValue, property);
		}
	}
}

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
package net.sf.jasperreports.j2ee.hibernate;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.type.Type;

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
	private final Map<String, FieldReader> fieldReaders;
	protected final JRHibernateQueryExecuter queryExecuter;
	private Object currentReturnValue;
	
	
	/**
	 * Creates a Hibernate data source.
	 * 
	 * @param queryExecuter the query executer
	 * @param useFieldDescription whether to use field descriptions for fields to results mapping
	 * @param useIndexOnSingleReturn whether to use indexed addressing even when the query has only one return column
	 */
	protected JRHibernateAbstractDataSource(JRHibernateQueryExecuter queryExecuter, boolean useFieldDescription, boolean useIndexOnSingleReturn)
	{
		this.propertyNameProvider = new JRAbstractBeanDataSource.DefaultPropertyNameProvider(useFieldDescription);
		
		this.queryExecuter = queryExecuter;

		fieldReaders = assignReaders(useIndexOnSingleReturn);
	}
	
	/**
	 * Assigns field readers to report fields.
	 * 
	 * @param useIndexOnSingleReturn  whether to use indexed addressing even when the query has only one return column
	 * @return a report field name to field reader mapping
	 * @see FieldReader
	 */
	protected Map<String, FieldReader> assignReaders(boolean useIndexOnSingleReturn)
	{
		Map<String, FieldReader> readers = new HashMap<>();
		
		JRField[] fields = queryExecuter.getDataset().getFields();
		Type[] returnTypes = queryExecuter.getReturnTypes();
		String[] aliases = queryExecuter.getReturnAliases();
		
		Map<String, Integer> aliasesMap = new HashMap<>();
		if (aliases != null)
		{
			for (int i = 0; i < aliases.length; i++)
			{
				aliasesMap.put(aliases[i], i);
			}
		}
		
		if (returnTypes.length == 1)
		{
			if (returnTypes[0].isEntityType() || returnTypes[0].isComponentType())
			{
				for (int i = 0; i < fields.length; i++)
				{
					JRField field = fields[i];
					readers.put(field.getName(), getFieldReaderSingleReturn(aliasesMap, field, useIndexOnSingleReturn));
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
					if (useIndexOnSingleReturn)
					{
						readers.put(field.getName(), new IndexFieldReader(0));
					}
					else
					{
						readers.put(field.getName(), new IdentityFieldReader());
					}
				}
			}
		}
		else
		{
			for (int i = 0; i < fields.length; i++)
			{
				JRField field = fields[i];
				readers.put(field.getName(), getFieldReader(returnTypes, aliasesMap, field));				
			}
		}

		return readers;
	}

	protected FieldReader getFieldReaderSingleReturn(Map<String,Integer> aliasesMap, JRField field, boolean useIndex)
	{
		FieldReader reader;
		
		String fieldMapping = getFieldMapping(field);
		if (aliasesMap.containsKey(fieldMapping))
		{
			if (useIndex)
			{
				reader = new IndexFieldReader(0);
			}
			else
			{
				reader = new IdentityFieldReader();
			}
		}
		else
		{
			int firstNestedIdx = fieldMapping.indexOf(NESTED_DELIM);

			if (firstNestedIdx >= 0 && aliasesMap.containsKey(fieldMapping.substring(0, firstNestedIdx)))
			{
				fieldMapping = fieldMapping.substring(firstNestedIdx + 1);
			}

			if (useIndex)
			{
				reader = new IndexPropertyFieldReader(0, fieldMapping);
			}
			else
			{
				reader = new PropertyFieldReader(fieldMapping);
			}
		}
		
		return reader;
	}

	protected FieldReader getFieldReader(Type[] returnTypes, Map<String,Integer> aliasesMap, JRField field)
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
			
			Type type = returnTypes[fieldIdx];
			if (!type.isEntityType() && !type.isComponentType())
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
	protected void setCurrentRowValue(Object currentReturnValue)
	{
		this.currentReturnValue = currentReturnValue;
	}
	
	
	@Override
	public Object getFieldValue(JRField jrField) throws JRException
	{
		FieldReader reader = fieldReaders.get(jrField.getName());
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
		Object getFieldValue(Object resultValue) throws JRException;
	}
	
	protected static class IdentityFieldReader implements FieldReader
	{
		@Override
		public Object getFieldValue(Object resultValue)
		{
			return resultValue;
		}	
	}
	
	protected static class IndexFieldReader implements FieldReader
	{
		private final int idx;

		protected IndexFieldReader(int idx)
		{
			this.idx = idx;
		}
		
		@Override
		public Object getFieldValue(Object resultValue)
		{
			return ((Object[]) resultValue)[idx];
		}
	}
	
	protected static class PropertyFieldReader implements FieldReader
	{
		private final String property;

		protected PropertyFieldReader(String property)
		{
			this.property = property;
		}
		
		@Override
		public Object getFieldValue(Object resultValue) throws JRException
		{
			return JRAbstractBeanDataSource.getBeanProperty(resultValue, property);
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
		public Object getFieldValue(Object resultValue) throws JRException
		{
			return JRAbstractBeanDataSource.getBeanProperty(((Object[]) resultValue)[idx], property);
		}
	}
}

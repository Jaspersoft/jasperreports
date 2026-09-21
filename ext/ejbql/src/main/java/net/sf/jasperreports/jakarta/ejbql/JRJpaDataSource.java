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
package net.sf.jasperreports.jakarta.ejbql;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.data.JRAbstractBeanDataSource;

/**
 * Java Persistence API data source that uses <code>jakarta.persistence.Query.getResultList()</code>.
 * <p/>
 * The query result can be paginated by not retrieving all the rows at once.
 * <p/>
 * Fields are mapped to values in the result following these rules:
 * <ul>
 * <li>if the query returns a single object/bean (e.g. <code>SELECT m FROM Movie m</code> or
 * <code>SELECT NEW MovieDescription(m.title, m.genre) FROM Movie m</code>), then the fields are
 * mapped to bean property names.</li>
 * <li>if the query returns multiple objects per row (e.g. <code>SELECT m.title, m.gender FROM Movie m</code>),
 * the fields are mapped using the following syntax: COLUMN_<em>index</em>[.<em>property</em>], with the
 * indexes starting from 1.  Example mappings: COLUMN_1, COLUMN_2, COLUMN_2.title, COLUMN_2.movie.title.</li>
 * </ul>
 * 
 * @author Marcel Overdijk (marceloverdijk@hotmail.com)
 * @see net.sf.jasperreports.jakarta.ejbql.JRJpaQueryExecuter#PROPERTY_JPA_QUERY_PAGE_SIZE
 */
public class JRJpaDataSource extends JRAbstractBeanDataSource {
	
	protected static final String EXCEPTION_MESSAGE_KEY_INDEX_OUT_OF_BOUNDS = "data.jpa.index.out.of.bounds";

	private static final String MAPPING_INDEX_PREFIX = "COLUMN_";
	private static final int MAPPING_INDEX_PREFIX_LENGTH = MAPPING_INDEX_PREFIX.length();
	private static final String MAPPING_INDEX_PROPERTY_SEP = ".";
	private static final int MAPPING_INDEX_PROPERTY_SEP_LENGTH = MAPPING_INDEX_PROPERTY_SEP.length();
	
	private final JRJpaQueryExecuter queryExecuter;
	private final int pageSize;
	private int pageCount;
	private boolean nextPage;
	private List<?> returnValues;
	private Iterator<?> iterator;
	protected Object currentRow;
	private Map<String,FieldValueReader> fieldValueReaders;

	public JRJpaDataSource(JRJpaQueryExecuter queryExecuter, int pageSize) {
		super(true);

		this.queryExecuter = queryExecuter;
		this.pageSize = pageSize;
		
		fieldValueReaders = new HashMap<>();
		
		pageCount = 0;
		fetchPage();
	}

	protected void fetchPage() {
		if (pageSize <= 0) {
			returnValues = queryExecuter.getResultList();
			nextPage = false;
		}
		else {
			returnValues = queryExecuter.getResultList(pageCount * pageSize, pageSize);
			nextPage = returnValues.size() == pageSize;
		}

		++pageCount;

		initIterator();
	}

	@Override
	public boolean next() {
		if (iterator == null) {
			return false;
		}
		
		boolean hasNext = iterator.hasNext();
		if (!hasNext && nextPage) {
			fetchPage();
			hasNext = iterator != null && iterator.hasNext();
		}
		
		if (hasNext) {
			currentRow = iterator.next();
		}

		return hasNext;
	}

	@Override
	public void moveFirst() {
		if (pageCount == 1) {
			initIterator();
		}
		else {
			pageCount = 0;
			fetchPage();
		}
	}

	private void initIterator() {
		iterator = returnValues == null ? null : returnValues.iterator();
	}
	
	@Override
	public Object getFieldValue(JRField field) throws JRException {
		FieldValueReader reader = getFieldValueReader(field);
		return reader.getValue();
	}
	
	protected FieldValueReader getFieldValueReader(JRField field)
	{
		FieldValueReader reader = fieldValueReaders.get(field.getName());
		if (reader == null)
		{
			String mapping = getPropertyName(field);
			reader = createReader(mapping);
			fieldValueReaders.put(field.getName(), reader);
		}
		return reader;
	}

	private FieldValueReader createReader(String mapping)
	{
		FieldValueReader reader = null;
		if (mapping.startsWith(MAPPING_INDEX_PREFIX))
		{
			int propertySepIdx = mapping.indexOf(MAPPING_INDEX_PROPERTY_SEP, MAPPING_INDEX_PREFIX_LENGTH + 1);
			if (propertySepIdx < 0)
			{
				String indexStr = mapping.substring(MAPPING_INDEX_PREFIX_LENGTH);
				try
				{
					int index = Integer.parseInt(indexStr);
					reader = new IndexReader(index - 1);
				}
				catch (NumberFormatException e)
				{
					//ignore
				}
			}
			else
			{
				String indexStr = mapping.substring(MAPPING_INDEX_PREFIX_LENGTH, propertySepIdx);
				try
				{
					int index = Integer.parseInt(indexStr);
					String property = mapping.substring(propertySepIdx + MAPPING_INDEX_PROPERTY_SEP_LENGTH);
					reader = new IndexPropertyReader(index - 1, property);
				}
				catch (NumberFormatException e)
				{
					//ignore
				}
			}
		}

		//default to a property mapping
		if (reader == null)
		{
			reader = new PropertyReader(mapping);
		}
		
		return reader;
	}

	protected static interface FieldValueReader
	{
		Object getValue() throws JRException;
	}
	
	protected class PropertyReader implements FieldValueReader
	{
		private final String property;
		
		public PropertyReader(String property)
		{
			this.property = property;
		}
		
		@Override
		public Object getValue() throws JRException
		{
			return getBeanProperty(currentRow, property);
		}	
	}
	
	protected class IndexReader implements FieldValueReader
	{
		private final int position;
		
		public IndexReader(int position)
		{
			this.position = position;
		}
		
		@Override
		public Object getValue() throws JRException
		{
			Object[] values = (Object[]) currentRow;
			if (position < 0 || position >= values.length)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_INDEX_OUT_OF_BOUNDS,
						new Object[]{position, values.length});
			}
			return values[position];
		}	
	}
	
	protected class IndexPropertyReader implements FieldValueReader
	{
		private final int position;
		private final String property;
		
		public IndexPropertyReader(int position, String property)
		{
			this.position = position;
			this.property = property;
		}
		
		@Override
		public Object getValue() throws JRException
		{
			Object[] values = (Object[]) currentRow;
			if (position < 0 || position >= values.length)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_INDEX_OUT_OF_BOUNDS,
						new Object[]{position, values.length});
			}
			return getBeanProperty(values[position], property);
		}
	}
}

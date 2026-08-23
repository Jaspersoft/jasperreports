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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.query.BindableType;
import org.hibernate.query.Query;
import org.hibernate.type.StandardBasicTypes;

import jakarta.persistence.Tuple;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.query.HibernateConstants;
import net.sf.jasperreports.engine.query.JRAbstractQueryExecuter;
import net.sf.jasperreports.engine.query.JRJdbcQueryExecuterFactory;
import net.sf.jasperreports.engine.util.JRStringUtil;

/**
 * HQL query executer that uses Hibernate 3.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRHibernateQueryExecuter extends JRAbstractQueryExecuter
{
	private static final Log log = LogFactory.getLog(JRHibernateQueryExecuter.class);
	
	public static final String EXCEPTION_MESSAGE_KEY_UNKNOWN_QUERY_RUN_TYPE = "query.hibernate.unknown.query.run.type";
	public static final String EXCEPTION_MESSAGE_KEY_UNRESOLVED_TYPE_CONSTANT = "query.hibernate.unresolved.type.constant";

	public static final String CANONICAL_LANGUAGE = "HQL";
	
	private static final Map<Class<?>, BindableType<?>> hibernateTypeMap;
	
	static
	{
		hibernateTypeMap = new HashMap<>();
		hibernateTypeMap.put(Boolean.class, StandardBasicTypes.BOOLEAN);
		hibernateTypeMap.put(Byte.class, StandardBasicTypes.BYTE);
		hibernateTypeMap.put(Double.class, StandardBasicTypes.DOUBLE);
		hibernateTypeMap.put(Float.class, StandardBasicTypes.FLOAT);
		hibernateTypeMap.put(Integer.class, StandardBasicTypes.INTEGER);
		hibernateTypeMap.put(Long.class, StandardBasicTypes.LONG);
		hibernateTypeMap.put(Short.class, StandardBasicTypes.SHORT);
		hibernateTypeMap.put(java.math.BigDecimal.class, StandardBasicTypes.BIG_DECIMAL);
		hibernateTypeMap.put(java.math.BigInteger.class, StandardBasicTypes.BIG_INTEGER);
		hibernateTypeMap.put(Character.class, StandardBasicTypes.CHARACTER);
		hibernateTypeMap.put(String.class, StandardBasicTypes.STRING);
		hibernateTypeMap.put(java.util.Date.class, StandardBasicTypes.DATE);
		hibernateTypeMap.put(java.sql.Timestamp.class, StandardBasicTypes.TIMESTAMP);
		hibernateTypeMap.put(java.sql.Time.class, StandardBasicTypes.TIME);
	}

	private final Integer reportMaxCount;
	
	private Session session;
	private Query<Tuple> query;
	private boolean queryRunning;
	private ScrollableResults<Tuple> scrollableResults;
	private Stream<Tuple> resultsStream;
	private boolean isClearCache;

	
	/**
	 * 
	 */
	public JRHibernateQueryExecuter(
		JasperReportsContext jasperReportsContext, 
		JRDataset dataset, Map<String, ? extends JRValueParameter> parameters
		)
	{
		super(jasperReportsContext, dataset, parameters);
		
		session = (Session) getParameterValue(HibernateConstants.PARAMETER_HIBERNATE_SESSION);
		reportMaxCount = (Integer) getParameterValue(JRParameter.REPORT_MAX_COUNT);
		isClearCache = getPropertiesUtil().getBooleanProperty(dataset, 
				HibernateConstants.PROPERTY_HIBERNATE_CLEAR_CACHE,
				false);

		if (session == null)
		{
			log.warn("The supplied org.hibernate.Session object is null.");
		}
		
		parseQuery();
	}
	
	
	@Override
	protected String getCanonicalQueryLanguage()
	{
		return CANONICAL_LANGUAGE;
	}
	
	
	/**
	 * Creates an instance of {@link JRHibernateListDataSource JRHibernateListDataSource},
	 * {@link JRHibernateIterateDataSource JRHibernateIterateDataSource} or
	 * {@link JRHibernateScrollDataSource JRHibernateScrollDataSource}, depending on the 
	 */
	@Override
	public JRDataSource createDatasource() throws JRException
	{
		JRDataSource datasource = null;
		String queryString = getQueryString();
		
		if (session != null && queryString != null && queryString.trim().length() > 0)
		{
			createQuery(queryString);

			datasource = createResultDatasource();
		}

		return datasource;
	}

	/**
	 * Creates a data source out of the query result.
	 * 
	 * @return the data source
	 */
	protected JRDataSource createResultDatasource()
	{
		JRDataSource resDatasource;
		
		String runType = getPropertiesUtil().getProperty(dataset, 
				HibernateConstants.PROPERTY_HIBERNATE_QUERY_RUN_TYPE);
		boolean useFieldDescriptions = getPropertiesUtil().getBooleanProperty(dataset, 
				HibernateConstants.PROPERTY_HIBERNATE_FIELD_MAPPING_DESCRIPTIONS,
				true);
		
		if (runType == null || runType.equals(HibernateConstants.VALUE_HIBERNATE_QUERY_RUN_TYPE_LIST))
		{
			try
			{
				int pageSize = getPropertiesUtil().getIntegerProperty(dataset, 
						HibernateConstants.PROPERTY_HIBERNATE_QUERY_LIST_PAGE_SIZE,
						0);

				resDatasource = new JRHibernateListDataSource(this, useFieldDescriptions, pageSize);
			}
			catch (NumberFormatException e)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_NUMERIC_TYPE_REQUIRED,
						new Object[]{HibernateConstants.PROPERTY_HIBERNATE_QUERY_LIST_PAGE_SIZE},
						e);
			}
		}
		else if (runType.equals(HibernateConstants.VALUE_HIBERNATE_QUERY_RUN_TYPE_ITERATE))
		{
			resDatasource = new JRHibernateIterateDataSource(this, useFieldDescriptions);
		}
		else if (runType.equals(HibernateConstants.VALUE_HIBERNATE_QUERY_RUN_TYPE_SCROLL))
		{
			resDatasource = new JRHibernateScrollDataSource(this, useFieldDescriptions);
		}
		else
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_UNKNOWN_QUERY_RUN_TYPE,
					new Object[]{
						HibernateConstants.PROPERTY_HIBERNATE_QUERY_RUN_TYPE,
						HibernateConstants.VALUE_HIBERNATE_QUERY_RUN_TYPE_LIST,
						HibernateConstants.VALUE_HIBERNATE_QUERY_RUN_TYPE_ITERATE,
						HibernateConstants.VALUE_HIBERNATE_QUERY_RUN_TYPE_SCROLL}
					);
		}
		
		return resDatasource;
	}

	
	/**
	 * Creates the Hibernate query object.
	 * <p/>
	 * @param queryString the query string
	 */
	protected synchronized void createQuery(String queryString)
	{
		if (log.isDebugEnabled())
		{
			log.debug("HQL query: " + queryString);
		}
		
		query = session.createQuery(queryString, Tuple.class);
		query.setReadOnly(true);
		
		int fetchSize = getPropertiesUtil().getIntegerProperty(dataset,
				JRJdbcQueryExecuterFactory.PROPERTY_JDBC_FETCH_SIZE,
				0);
		if (fetchSize != 0)
		{
			query.setFetchSize(fetchSize);
		}
		
		setParameters();
	}

	/**
	 * Binds values for all the query parameters.
	 */
	protected void setParameters()
	{
		List<String> parameterNames = getCollectedParameterNames();
		
		if (!parameterNames.isEmpty())
		{
			Set<String> namesSet = new HashSet<>();
			
			for (String parameterName : parameterNames)
			{
				if (namesSet.add(parameterName))
				{
					JRValueParameter parameter = getValueParameter(parameterName);
					setParameter(parameter);
				}
			}
		}
	}
	
	
	/**
	 * Binds a parameter value to a query parameter.
	 * 
	 * @param parameter the report parameter
	 */
	@SuppressWarnings("unchecked")
	protected void setParameter(JRValueParameter parameter)
	{
		String hqlParamName = getHqlParameterName(parameter.getName());
		Class<?> clazz = parameter.getValueClass();
		Object parameterValue = parameter.getValue();
		
		if (log.isDebugEnabled())
		{
			log.debug("Parameter " + hqlParamName + " of type " + clazz.getName() + ": " + parameterValue);
		}
		
		BindableType<Object> type = (BindableType<Object>) hibernateTypeMap.get(clazz);
		
		if (type != null)
		{
			query.setParameter(hqlParamName, parameterValue, type);
		}
		else if (Collection.class.isAssignableFrom(clazz))
		{
			query.setParameterList(hqlParamName, (Collection<?>) parameterValue);
		}
		else
		{
			//let hibernate guess the type
			//TODO check if the value is an entity via session.getMetamodel()?
			query.setParameter(hqlParamName, parameterValue);
		}
	}

	
	/**
	 * Closes the scrollable result when <em>scroll</em> execution type is used.
	 */
	@Override
	public synchronized void close()
	{
		closeScrollableResults();
		closeResultsStream();

		query = null;
	}


	/**
	 * Closes the scrollable results of the query.
	 */
	public void closeScrollableResults()
	{
		if (scrollableResults != null)
		{
			try
			{
				scrollableResults.close();
			}
			finally
			{
				scrollableResults = null;
			}
		}
	}
	
	protected void closeResultsStream()
	{
		if (resultsStream != null)
		{
			try
			{
				resultsStream.close();
			}
			finally
			{
				resultsStream = null;
			}
		}
	}
	
	@Override
	public synchronized boolean cancelQuery() throws JRException
	{
		if (queryRunning)
		{
			session.cancelQuery();
			return true;
		}
		
		return false;
	}

	@Override
	protected String getParameterReplacement(String parameterName)
	{
		return ':' + getHqlParameterName(parameterName);
	}

	protected String getHqlParameterName(String parameterName)
	{
		return '_' + JRStringUtil.getJavaIdentifier(parameterName);
	}
	
	
	/**
	 * Returns the dataset for which the query executer has been created.
	 * 
	 * @return the dataset for which the query executer has been created
	 */
	public JRDataset getDataset()
	{
		return dataset;
	}
	
	
	/**
	 * Runs the query by calling <code>org.hibernate.Query.list()</code>.
	 * <p/>
	 * All the result rows are returned.
	 * 
	 * @return the result of the query as a list
	 */
	public List<Tuple> list()
	{
		setMaxCount();
		
		setQueryRunning(true);
		try
		{
			return query.list();
		}
		finally
		{
			setQueryRunning(false);
		}
	}

	protected synchronized void setQueryRunning(boolean queryRunning)
	{
		this.queryRunning = queryRunning;
	}

	
	private void setMaxCount()
	{
		if (reportMaxCount != null)
		{
			query.setMaxResults(reportMaxCount);
		}
	}
	
	
	/**
	 * Returns a page of the query results by calling <code>org.hibernate.Query.iterate()</code>.
	 * 
	 * @param firstIndex the index of the first row to return
	 * @param resultCount the number of rows to return
	 * @return result row list
	 */
	public List<Tuple> list(int firstIndex, int resultCount)
	{
		if (reportMaxCount != null && firstIndex + resultCount > reportMaxCount)
		{
			resultCount = reportMaxCount - firstIndex;
		}
		
		query.setFirstResult(firstIndex);
		query.setMaxResults(resultCount);
		if (isClearCache)
		{
			clearCache();
		}
		return query.list();
	}
	
	
	/**
	 * Runs the query by calling <code>org.hibernate.Query.iterate()</code>.
	 * 
	 * @return query iterator
	 */
	public Stream<Tuple> stream()
	{
		closeResultsStream();
		
		setMaxCount();
		
		setQueryRunning(true);
		try
		{
			resultsStream = query.stream();
			return resultsStream;
		}
		finally
		{
			setQueryRunning(false);
		}
	}
	
	
	/**
	 * Runs the query by calling <code>org.hibernate.Query.scroll()</code>.
	 * 
	 * @return scrollable results of the query
	 */
	public ScrollableResults<Tuple> scroll()
	{
		setMaxCount();
		
		setQueryRunning(true);
		try
		{
			scrollableResults = query.scroll(ScrollMode.FORWARD_ONLY);
		}
		finally
		{
			setQueryRunning(false);
		}
		
		return scrollableResults;
	}
	
	protected Session getSession()
	{
		return session;
	}
	
	public void clearCache()
	{
		session.flush();
		session.clear();
	}
}

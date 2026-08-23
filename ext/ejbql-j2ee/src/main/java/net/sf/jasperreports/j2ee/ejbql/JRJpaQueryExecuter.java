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
package net.sf.jasperreports.j2ee.ejbql;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRPropertiesUtil.PropertySuffix;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.query.EjbqlConstants;
import net.sf.jasperreports.engine.query.JRAbstractQueryExecuter;
import net.sf.jasperreports.engine.util.JRStringUtil;
import net.sf.jasperreports.properties.PropertyConstants;

/**
 * EJBQL query executer that uses the Java Persistence API.
 * <p/>
 * To use EJBQL in queries, an <code>javax.persistence.EntityManager</code> is needed.
 * When running or filling reports the <code>em</code> need to be supplied with the named parameter {@link net.sf.jasperreports.engine.query.EjbqlConstants#PARAMETER_JPA_ENTITY_MANAGER}.
 * <p/>
 * Example:
 * <code>
 * <pre>
 * Map parameters = new HashMap();
 * EntityManager em = emf.createEntityManager();
 * parameters.put(EjbqlConstants.PARAMETER_JPA_ENTITY_MANAGER, em);
 * JasperRunManager.runReportToPdfFile(fileName, parameters);
 * </pre>
 * </code>
 * <p/>
 * When dealing with large result sets, pagination can be used by setting the {@link net.sf.jasperreports.j2ee.ejbql.JRJpaQueryExecuter#PROPERTY_JPA_QUERY_PAGE_SIZE} property in the report template.
 * <p/>
 * Example:
 * <code>
 * <pre>
 * &lt;property name="net.sf.jasperreports.ejbql.query.page.size" value="100"/&gt;
 * </pre>
 * </code>
 * <p/>
 * Implementation-specific query hints can be set either using report properties in the report template,
 * or by supplying the named parameter {@link net.sf.jasperreports.engine.query.EjbqlConstants#PARAMETER_JPA_QUERY_HINTS_MAP}
 * containing a <code>java.util.Map</code> with named/value query hints.
 * <p/>
 * Example using report property:
 * <code>
 * <pre>
 * &lt;property name="net.sf.jasperreports.ejbql.query.hint.fetchSize" value="100"/&gt;
 * </pre>
 * </code>
 * The name of the query hint need to be prefixed with {@link net.sf.jasperreports.j2ee.ejbql.JRJpaQueryExecuter#PROPERTY_JPA_QUERY_HINT_PREFIX net.sf.jasperreports.ejbql.query.hint.}.
 * Above example will set a query hint with the name <code>fetchSize</code> and the <code>String</code> value <code>100</code>.
 * <p/>
 * Example using map:
 * <code>
 * <pre>
 * Map hints = new HashMap();
 * hints.put("fetchSize", 100);
 * hints.put("anyName", anyObject());
 * Map parameters = new HashMap();
 * EntityManager em = emf.createEntityManager();
 * parameters.put(EjbqlConstants.PARAMETER_JPA_ENTITY_MANAGER, em);
 * parameters.put(EjbqlConstants.PARAMETER_JPA_QUERY_HINTS_MAP, hints);
 * JasperRunManager.runReportToPdfFile(fileName, parameters);
 * </pre>
 * </code>
 * Note that when using report properties only <code>String</code> values can be set as query hint.
 * When using a query hints map, any <code>Object</code> can be set as value.
 * 
 * @author Marcel Overdijk (marceloverdijk@hotmail.com)
 * @see JRJpaQueryExecuterFactory
 */
public class JRJpaQueryExecuter extends JRAbstractQueryExecuter 
{

	private static final Log log = LogFactory.getLog(JRJpaQueryExecuter.class);

	public static final String CANONICAL_LANGUAGE = "EJBQL";

	/**
	 * Property specifying the number of result rows to be retrieved at once.
	 * <p/>
	 * Result pagination is implemented by <code>javax.persistence.Query.setFirstResult()</code> and <code>javax.persistence.Query.setMaxResults()</code>.
	 * <p/>
	 * By default, all the rows are retrieved (no result pagination is performed).
	 */
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			scopeQualifications = {EjbqlConstants.QUERY_EXECUTER_NAME_EJBQL},
			sinceVersion = PropertyConstants.VERSION_1_2_3,
			valueType = Integer.class
			)
	public static final String PROPERTY_JPA_QUERY_PAGE_SIZE = JRPropertiesUtil.PROPERTY_PREFIX + "ejbql.query.page.size";

	/**
	 * Property specifying the prefix for EJBQL query hints.
	 */
	@Property(
			name = "net.sf.jasperreports.ejbql.query.hint.{hint}",
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.DATASET},
			scopeQualifications = {EjbqlConstants.QUERY_EXECUTER_NAME_EJBQL},
			sinceVersion = PropertyConstants.VERSION_1_2_3
			)
	public static final String PROPERTY_JPA_QUERY_HINT_PREFIX = JRPropertiesUtil.PROPERTY_PREFIX + "ejbql.query.hint.";

		
	private final Integer reportMaxCount;
	
	private EntityManager em;
	private Query query;
	
	/**
	 * 
	 */
	public JRJpaQueryExecuter(
		JasperReportsContext jasperReportsContext, 
		JRDataset dataset, 
		Map<String,? extends JRValueParameter> parameters
		) 
	{
		super(jasperReportsContext, dataset, parameters);
		
		em = (EntityManager)getParameterValue(EjbqlConstants.PARAMETER_JPA_ENTITY_MANAGER);
		reportMaxCount = (Integer)getParameterValue(JRParameter.REPORT_MAX_COUNT);

		if (em == null) {
			log.warn("The supplied javax.persistence.EntityManager object is null.");
		}
	
		parseQuery();
	}
	
	@Override
	protected String getCanonicalQueryLanguage()
	{
		return CANONICAL_LANGUAGE;
	}
	
	@Override
	public JRDataSource createDatasource() throws JRException {
		JRDataSource datasource = null;
		String queryString = getQueryString();
		
		if (em != null && queryString != null && queryString.trim().length() > 0) {
			createQuery(queryString);

			datasource = createResultDatasource();
		}
		
		return datasource;
	}

	/**
	 * Creates the EJBQL query object.
	 *
	 * @param queryString the query string
	 */
	protected synchronized void createQuery(String queryString) {
		
		if (log.isDebugEnabled())
		{
			log.debug("EJBQL query: " + queryString);
		}
		
		query = em.createQuery(queryString);
		
		// Set parameters.
		List<String> parameterNames = getCollectedParameterNames();
		if (!parameterNames.isEmpty()) {
			// Use set to prevent the parameter to be set multiple times.
			Set<String> namesSet = new HashSet<>();
			for (String parameterName : parameterNames) {
				if (namesSet.add(parameterName)) {
					JRValueParameter parameter = getValueParameter(parameterName);
					String ejbParamName = getEjbqlParameterName(parameterName);
					Object paramValue = parameter.getValue();
					
					if (log.isDebugEnabled())
					{
						log.debug("Parameter " + ejbParamName + ": " + paramValue);
					}
					
					query.setParameter(ejbParamName, paramValue);
				}
			}
		}

		// Set query hints.
		// First, set query hints supplied by the JPA_QUERY_HINTS_MAP parameter.
		Map<String,Object> queryHintsMap = (Map<String,Object>)getParameterValue(EjbqlConstants.PARAMETER_JPA_QUERY_HINTS_MAP);
		if (queryHintsMap != null) {
			for (Map.Entry<String,Object> pairs : queryHintsMap.entrySet()) {
				if (log.isDebugEnabled()) {
					log.debug("EJBQL query hint [" + pairs.getKey() + "] set.");
				}
				query.setHint(pairs.getKey(), pairs.getValue());
			}
		}
		// Second, set query hints supplied by report properties which start with JREjbPersistenceQueryExecuterFactory.PROPERTY_JPA_PERSISTENCE_QUERY_HINT_PREFIX
		// Example: net.sf.jasperreports.ejbql.query.hint.fetchSize
		// This property will result in a query hint set with the name: fetchSize
		List<PropertySuffix> properties = JRPropertiesUtil.getProperties(dataset, 
				PROPERTY_JPA_QUERY_HINT_PREFIX);
		for (PropertySuffix property : properties) {
			String queryHint = property.getSuffix();
			if (queryHint.length() > 0) {
				String value = property.getValue();
				if (log.isDebugEnabled()) {
					log.debug("EJBQL query hint [" + queryHint + "] set to: " + value);
				}
				query.setHint(queryHint, value);
			}
		}
	}	

	/**
	 * Creates a data source out of the query result.
	 * 
	 * @return the data source
	 */
	protected JRDataSource createResultDatasource()	{
		JRDataSource resDatasource;
		
		try {
			int pageSize = getPropertiesUtil().getIntegerProperty(dataset, 
					PROPERTY_JPA_QUERY_PAGE_SIZE,
					0);

			resDatasource = new JRJpaDataSource(this, pageSize);
		}
		catch (NumberFormatException e) {
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_NUMERIC_TYPE_REQUIRED,
					new Object[]{PROPERTY_JPA_QUERY_PAGE_SIZE},
					e);
		}
		
		return resDatasource;
	}

	@Override
	public synchronized void close() {
		query = null;
	}

	@Override
	public synchronized boolean cancelQuery() throws JRException {
		return false;
	}
	
	@Override
	protected String getParameterReplacement(String parameterName) {
		return ':' + getEjbqlParameterName(parameterName);
	}
	
	protected String getEjbqlParameterName(String parameterName) {
		return JRStringUtil.getJavaIdentifier(parameterName);
	}
	
	/**
	 * Runs the query by calling <code>javax.persistence.Query.getResultList</code>.
	 * <p/>
	 * All the result rows are returned.
	 * 
	 * @return the result of the query as a list
	 */
	public List<?> getResultList() {
		if (reportMaxCount != null) {
			query.setMaxResults(reportMaxCount);
		}
		
		return query.getResultList();
	}
	
	/**
	 * Returns a page of the query results by calling <code>javax.persistence.Query.getResultList</code>.
	 * 
	 * @param firstIndex the index of the first row to return
	 * @param resultCount the number of rows to return
	 * @return result row list
	 */
	public List<?> getResultList(int firstIndex, int resultCount) {
		if (reportMaxCount != null && firstIndex + resultCount > reportMaxCount) {
			resultCount = reportMaxCount - firstIndex;
		}
		
		query.setFirstResult(firstIndex);
		query.setMaxResults(resultCount);
		
		return query.getResultList();
	}	
}

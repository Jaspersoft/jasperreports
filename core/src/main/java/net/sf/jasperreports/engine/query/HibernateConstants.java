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
package net.sf.jasperreports.engine.query;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.properties.PropertyConstants;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface HibernateConstants
{
	public static final String QUERY_EXECUTER_NAME_HQL = "net.sf.jasperreports.query.executer:HQL";

	/**
	 * HQL query language.
	 */
	public static final String QUERY_LANGUAGE_HQL = "hql";
	
	/**
	 * Built-in parameter holding the value of the Hibernate session to be used for creating the query.
	 */
	public final static String PARAMETER_HIBERNATE_SESSION = "HIBERNATE_SESSION";

	/**
	 * Property specifying the query execution type.
	 * <p/>
	 * Possible values are:
	 * <ul>
	 * 	<li><em>list</em> (default) - the query will be run by calling <code>org.hibernate.Query.list()</code></li>
	 * 	<li><em>iterate</em> - the query will be run by calling <code>org.hibernate.Query.iterate()</code></li>
	 * 	<li><em>scroll</em> - the query will be run by calling <code>org.hibernate.Query.scroll()</code></li>
	 * </ul>
	 */
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			defaultValue = "list",
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			scopeQualifications = {QUERY_EXECUTER_NAME_HQL},
			sinceVersion = PropertyConstants.VERSION_1_2_0
			)
	public static final String PROPERTY_HIBERNATE_QUERY_RUN_TYPE = JRPropertiesUtil.PROPERTY_PREFIX + "hql.query.run.type";
	
	/**
	 * Property specifying the number of result rows to be retrieved at once when the execution type is <em>list</em>.
	 * <p/>
	 * Result pagination is implemented by <code>org.hibernate.Query.setFirstResult()</code> and <code>org.hibernate.Query.setMaxResults()</code>.
	 * <p/>
	 * By default, all the rows are retrieved (no result pagination is performed).
	 */
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			defaultValue = "0",
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			scopeQualifications = {QUERY_EXECUTER_NAME_HQL},
			sinceVersion = PropertyConstants.VERSION_1_2_0,
			valueType = Integer.class
			)
	public static final String PROPERTY_HIBERNATE_QUERY_LIST_PAGE_SIZE = JRPropertiesUtil.PROPERTY_PREFIX + "hql.query.list.page.size";
	
	/**
	 * Property specifying whether hibernate session cache should be cleared between two consecutive fetches when using pagination.
	 * <p/>
	 * By default, the cache cleanup is not performed.
	 * <p/>
	 * @see #PROPERTY_HIBERNATE_QUERY_LIST_PAGE_SIZE
	 */
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			scopeQualifications = {QUERY_EXECUTER_NAME_HQL},
			sinceVersion = PropertyConstants.VERSION_1_3_1,
			valueType = Boolean.class
			)
	public static final String PROPERTY_HIBERNATE_CLEAR_CACHE = JRPropertiesUtil.PROPERTY_PREFIX + "hql.clear.cache";
	
	/**
	 * Property specifying whether field descriptions should be used to determine the mapping between the fields
	 * and the query return values.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			defaultValue = PropertyConstants.BOOLEAN_TRUE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			scopeQualifications = {QUERY_EXECUTER_NAME_HQL},
			sinceVersion = PropertyConstants.VERSION_1_2_0,
			valueType = Boolean.class
			)
	public static final String PROPERTY_HIBERNATE_FIELD_MAPPING_DESCRIPTIONS = JRPropertiesUtil.PROPERTY_PREFIX + "hql.field.mapping.descriptions";
	
	/**
	 * Value of the {@link #PROPERTY_HIBERNATE_QUERY_RUN_TYPE PROPERTY_HIBERNATE_QUERY_RUN_TYPE} property
	 * corresponding to <em>list</em> execution type.
	 */
	public static final String VALUE_HIBERNATE_QUERY_RUN_TYPE_LIST = "list"; 
	
	/**
	 * Value of the {@link #PROPERTY_HIBERNATE_QUERY_RUN_TYPE PROPERTY_HIBERNATE_QUERY_RUN_TYPE} property
	 * corresponding to <em>iterate</em> execution type.
	 */
	public static final String VALUE_HIBERNATE_QUERY_RUN_TYPE_ITERATE = "iterate"; 
	
	/**
	 * Value of the {@link #PROPERTY_HIBERNATE_QUERY_RUN_TYPE PROPERTY_HIBERNATE_QUERY_RUN_TYPE} property
	 * corresponding to <em>scroll</em> execution type.
	 */
	public static final String VALUE_HIBERNATE_QUERY_RUN_TYPE_SCROLL = "scroll";
}

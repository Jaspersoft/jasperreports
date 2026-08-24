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
package net.sf.jasperreports.engine.query;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRPropertiesUtil.PropertySuffix;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.util.JRSingletonCache;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class DefaultQueryExecuterFactoryBundle implements JRQueryExecuterFactoryBundle
{
	private static final JRSingletonCache<QueryExecuterFactory> cache = 
			new JRSingletonCache<>(QueryExecuterFactory.class);
	
	private static final DefaultQueryExecuterFactoryBundle INSTANCE = new DefaultQueryExecuterFactoryBundle();
	

	private JasperReportsContext jasperReportsContext;
	

	private DefaultQueryExecuterFactoryBundle()
	{
		this(DefaultJasperReportsContext.getInstance());
	}
	
	private DefaultQueryExecuterFactoryBundle(JasperReportsContext jasperReportsContext)
	{
		this.jasperReportsContext = jasperReportsContext;
	}
	
	/**
	 * 
	 */
	public static DefaultQueryExecuterFactoryBundle getInstance()
	{
		return INSTANCE;
	}
	
	/**
	 * 
	 */
	public static DefaultQueryExecuterFactoryBundle getInstance(JasperReportsContext jasperReportsContext)
	{
		return new DefaultQueryExecuterFactoryBundle(jasperReportsContext);
	}

	@Override
	public String[] getLanguages()
	{
		return
			JRPropertiesUtil.getInstance(jasperReportsContext).getProperties(QueryExecuterFactory.QUERY_EXECUTER_FACTORY_PREFIX)
				.stream().map(PropertySuffix::getSuffix).toArray(String[]::new);
	}

	@Override
	public QueryExecuterFactory getQueryExecuterFactory(String language) throws JRException
	{
		String factoryClassName = JRPropertiesUtil.getInstance(jasperReportsContext).getProperty(QueryExecuterFactory.QUERY_EXECUTER_FACTORY_PREFIX + language);
		if (factoryClassName == null)
		{
			return null;
		}
		
		return cache.getCachedInstance(factoryClassName);
	}
}

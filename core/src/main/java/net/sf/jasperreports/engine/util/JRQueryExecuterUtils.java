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
package net.sf.jasperreports.engine.util;

import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.query.JRQueryExecuterFactoryBundle;
import net.sf.jasperreports.engine.query.QueryExecuterFactory;

/**
 * Query executer utility class.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public final class JRQueryExecuterUtils
{
	public static final String EXCEPTION_MESSAGE_KEY_QUERY_EXECUTER_FACTORY_NOT_REGISTERED = "util.query.executer.factory.not.registered";
	
	private JasperReportsContext jasperReportsContext;


	/**
	 *
	 */
	private JRQueryExecuterUtils(JasperReportsContext jasperReportsContext)
	{
		this.jasperReportsContext = jasperReportsContext;
	}
	
	
	/**
	 *
	 */
	public static JRQueryExecuterUtils getInstance(JasperReportsContext jasperReportsContext)
	{
		return new JRQueryExecuterUtils(jasperReportsContext);
	}
	
	
	/**
	 * Returns a query executer factory for a query language.
	 * 
	 * @param language the query language
	 * @return a query executer factory
	 * @throws JRException
	 * @see QueryExecuterFactory#QUERY_EXECUTER_FACTORY_PREFIX
	 */
	public QueryExecuterFactory getExecuterFactory(String language) throws JRException
	{
		List<JRQueryExecuterFactoryBundle> bundles = jasperReportsContext.getExtensions(
				JRQueryExecuterFactoryBundle.class);
		for (JRQueryExecuterFactoryBundle bundle : bundles)
		{
			QueryExecuterFactory factory = bundle.getQueryExecuterFactory(language);
			if (factory != null)
			{
				return factory;
			}
		}
		throw 
			new JRRuntimeException(
				EXCEPTION_MESSAGE_KEY_QUERY_EXECUTER_FACTORY_NOT_REGISTERED,
				new Object[]{language});
	}
}


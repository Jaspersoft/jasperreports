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
package net.sf.jasperreports.olap;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.query.JRQueryExecuterFactoryBundle;
import net.sf.jasperreports.engine.query.QueryExecuterFactory;
import net.sf.jasperreports.engine.util.JRSingletonCache;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class OlapQueryExecuterFactoryBundle implements JRQueryExecuterFactoryBundle {
	private static final JRSingletonCache<QueryExecuterFactory> cache = new JRSingletonCache<QueryExecuterFactory>(
			QueryExecuterFactory.class);
	private static final OlapQueryExecuterFactoryBundle INSTANCE = new OlapQueryExecuterFactoryBundle();
	private static final String[] LANGUAGES = new String[] { "mdx", "MDX", "olap4j", "OLAP4J" };

	private OlapQueryExecuterFactoryBundle() {
	}

	/**
	 * 
	 */
	public static OlapQueryExecuterFactoryBundle getInstance() {
		return INSTANCE;
	}

	@Override
	public String[] getLanguages() {
		return LANGUAGES;
	}

	@Override
	public QueryExecuterFactory getQueryExecuterFactory(String language) throws JRException 
	{
		language = language.toUpperCase();
		if (language.equals("MDX"))
		{
			return cache.getCachedInstance(JRMdxQueryExecuterFactory.class.getName());
		}
		if (language.equals("OLAP4J"))
		{
			return cache.getCachedInstance(Olap4jQueryExecuterFactory.class.getName());
		}
		return null;
	}
}

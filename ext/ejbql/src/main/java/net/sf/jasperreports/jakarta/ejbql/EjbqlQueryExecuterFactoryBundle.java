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
package net.sf.jasperreports.jakarta.ejbql;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.query.EjbqlConstants;
import net.sf.jasperreports.engine.query.JRQueryExecuterFactoryBundle;
import net.sf.jasperreports.engine.query.QueryExecuterFactory;
import net.sf.jasperreports.engine.util.JRSingletonCache;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class EjbqlQueryExecuterFactoryBundle implements JRQueryExecuterFactoryBundle {
	private static final JRSingletonCache<QueryExecuterFactory> cache = new JRSingletonCache<QueryExecuterFactory>(
			QueryExecuterFactory.class);
	private static final EjbqlQueryExecuterFactoryBundle INSTANCE = new EjbqlQueryExecuterFactoryBundle();
	private static final String[] LANGUAGES = new String[] { EjbqlConstants.QUERY_LANGUAGE_EJBQL, EjbqlConstants.QUERY_LANGUAGE_EJBQL.toUpperCase() };

	private EjbqlQueryExecuterFactoryBundle() {
	}

	/**
	 * 
	 */
	public static EjbqlQueryExecuterFactoryBundle getInstance() {
		return INSTANCE;
	}

	@Override
	public String[] getLanguages() {
		return LANGUAGES;
	}

	@Override
	public QueryExecuterFactory getQueryExecuterFactory(String language) throws JRException {
		for (String lang : getLanguages()) {
			if (lang.equalsIgnoreCase(language))
				return cache.getCachedInstance(JRJpaQueryExecuterFactory.class.getName());
		}
		return null;
	}

	public static boolean isDomainLanguage(String lang) {
		String[] langs = EjbqlQueryExecuterFactoryBundle.getInstance().getLanguages();
		for (String l : langs)
			if (l.equals(lang))
				return true;
		return false;
	}
}

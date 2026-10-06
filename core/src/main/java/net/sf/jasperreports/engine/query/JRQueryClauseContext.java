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

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;


/**
 * A query clause handling context, as seen from a {@link JRClauseFunction clause function}.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public interface JRQueryClauseContext
{

	/**
	 * Returns the query text buffer.
	 * 
	 * @return the query text buffer
	 */
	StringBuffer queryBuffer();
	
	/**
	 * 
	 */
	JRDataset getDataset();
	
	/**
	 * Return a value parameter from the report parameters map.
	 * 
	 * @param parameterName the parameter name
	 * @return the parameter
	 */
	JRValueParameter getValueParameter(String parameterName);
	
	/**
	 * Records a query parameter.
	 * 
	 * @param parameterName the parameter name
	 */
	void addQueryParameter(String parameterName);
	
	/**
	 * Records a multi-valued query parameter.
	 * 
	 * @param parameterName the parameter name
	 * @param count the value count
	 */
	void addQueryMultiParameters(String parameterName, int count);

	/**
	 * Records a multi-valued query parameter with null values to be ignored.
	 * 
	 * @param parameterName the parameter name
	 * @param count the value count
	 * @param ignoreNulls flag that indicates whether the null values should be ignored
	 */
	void addQueryMultiParameters(String parameterName, int count, boolean ignoreNulls);

	/**
	 * Records a query parameter based on a provided value.
	 * 
	 * @param type the parameter type if specified
	 * @param value the parameter value
	 */
	void addQueryParameter(Class<?> type, Object value);
	
	/**
	 * Returns the JasperReportsContext associated with the current query execution.
	 * 
	 * @return the current JasperReportsContext
	 */
	JasperReportsContext getJasperReportsContext();

	/**
	 * Returns a canonical query language for this query execution.
	 * 
	 * <p>
	 * The canonical language is used to retrieve extensions for the query executer.
	 * </p>
	 * 
	 * @return the canonical query language
	 */
	String getCanonicalQueryLanguage();
}

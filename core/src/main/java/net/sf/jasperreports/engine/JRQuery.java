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
package net.sf.jasperreports.engine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * Represents a query used for generation of report data.
 * <p/>
 * Based on the query language, query executer implementations are used to retrieve the data and
 * create a {@link net.sf.jasperreports.engine.JRDataSource JRDataSource} to be used by the filling process.
 * <p/>
 * When using the default SQL connection data source,
 * an SQL query must also be provided for JasperReports to automatically retrieve the data.
 * For SQL queries, a very important aspect is that column names in the result set obtained from the query must match the field names
 * defined in the report template.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @see net.sf.jasperreports.engine.query.QueryExecuterFactory
 */
@JsonPropertyOrder({
	JRXmlConstants.ATTRIBUTE_language,
	JRXmlConstants.ELEMENT_text
	})
public interface JRQuery extends JRCloneable
{
	
	
	/**
	 *
	 */
	@JsonIgnore
	public JRQueryChunk[] getChunks();

	/**
	 * Returns the query string.
	 */
	@JacksonXmlText
	@JacksonXmlCData
	public String getText();
	

	/**
	 * Returns the query language.
	 * <p/>
	 * Based on the query language, a corresponding
	 * {@link net.sf.jasperreports.engine.query.QueryExecuterFactory QueryExecuterFactory} is used to create
	 * a {@link net.sf.jasperreports.engine.query.JRQueryExecuter JRQueryExecuter} instance.  The query executer
	 * is responsible for executing the query and creating a {@link JRDataSource JRDataSource} from the result.
	 * 
	 * @return the query language
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getLanguage();
}

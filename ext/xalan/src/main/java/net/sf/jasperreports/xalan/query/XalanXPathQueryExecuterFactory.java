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
package net.sf.jasperreports.xalan.query;

import java.util.Map;

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.query.JRQueryExecuter;
import net.sf.jasperreports.engine.query.JRXPathQueryExecuterFactory;
import net.sf.jasperreports.engine.query.QueryExecutionContext;
import net.sf.jasperreports.engine.query.SimpleQueryExecutionContext;

/**
 * Xalan XPath query executer factory.
 * <p/>
 * The factory creates {@link net.sf.jasperreports.xalan.query.XalanXPathQueryExecuter XalanXPathQueryExecuter}
 * query executers.
 * 
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class XalanXPathQueryExecuterFactory extends JRXPathQueryExecuterFactory
{
	private final static Object[] XALAN_XPATH_BUILTIN_PARAMETERS = {
		PARAMETER_XML_DATA_DOCUMENT,  "org.w3c.dom.Document",
		PARAMETER_DOCUMENT_BUILDER_FACTORY, "javax.xml.parsers.DocumentBuilderFactory",
		PARAMETER_XML_NAMESPACE_MAP, "java.util.Map",
		XML_DATE_PATTERN, "java.lang.String",
		XML_NUMBER_PATTERN, "java.lang.String",
		XML_LOCALE, "java.util.Locale",
		XML_TIME_ZONE, "java.util.TimeZone",
		};

	@Override
	public Object[] getBuiltinParameters()
	{
		return XALAN_XPATH_BUILTIN_PARAMETERS;
	}
	
	@Override
	public JRQueryExecuter createQueryExecuter(
		JasperReportsContext jasperReportsContext,
		JRDataset dataset,
		Map<String,? extends JRValueParameter> parameters
		) throws JRException
	{
		return createQueryExecuter(SimpleQueryExecutionContext.of(jasperReportsContext), dataset, parameters);
	}

	@Override
	public JRQueryExecuter createQueryExecuter(
		QueryExecutionContext context,
		JRDataset dataset,
		Map<String, ? extends JRValueParameter> parameters
		) throws JRException
	{
		return new XalanXPathQueryExecuter(context, dataset, parameters);
	}
}

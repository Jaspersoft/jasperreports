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

import java.io.File;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.data.JRXmlDataSource;

/**
 * XPath query executer implementation.
 * <p/>
 * The XPath query of the report is executed against the document specified by the
 * {@link net.sf.jasperreports.engine.query.JRXPathQueryExecuterFactory#PARAMETER_XML_DATA_DOCUMENT PARAMETER_XML_DATA_DOCUMENT}
 * parameter.
 * <p/>
 * All the parameters in the XPath query are replaced by calling <code>String.valueOf(Object)</code>
 * on the parameter value.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRXPathQueryExecuter extends JRAbstractQueryExecuter
{
	private static final Log log = LogFactory.getLog(JRXPathQueryExecuter.class);

	public static final String CANONICAL_LANGUAGE = "XPath";
	
	private JRXmlDataSource datasource;

	/**
	 * 
	 */
	public JRXPathQueryExecuter(
		JasperReportsContext jasperReportsContext,
		JRDataset dataset, 
		Map<String,? extends JRValueParameter> parametersMap
		)
	{
		this(SimpleQueryExecutionContext.of(jasperReportsContext),
				dataset, parametersMap);
	}
	
	public JRXPathQueryExecuter(
		QueryExecutionContext context,
		JRDataset dataset, 
		Map<String,? extends JRValueParameter> parametersMap
		)
	{
		super(context, dataset, parametersMap);
				
		parseQuery();
	}

	@Override
	protected String getCanonicalQueryLanguage()
	{
		return CANONICAL_LANGUAGE;
	}

	@Override
	protected String getParameterReplacement(String parameterName)
	{
		return String.valueOf(getParameterValue(parameterName));
	}

	@Override
	public JRDataSource createDatasource() throws JRException
	{
		JRXmlDataSource datasource = null;
		
		String xPath = getQueryString();
		
		if (log.isDebugEnabled())
		{
			log.debug("XPath query: " + xPath);
		}
		
		if (xPath != null)//FIXME maybe we should create data source with no select expression too
		{
			Document document = (Document) getParameterValue(JRXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT);
			if (document != null) {
				datasource = new JRXmlDataSource(getJasperReportsContext(), document, xPath);
			} else {
				InputStream xmlInputStream = (InputStream) getParameterValue(JRXPathQueryExecuterFactory.XML_INPUT_STREAM);
				if (xmlInputStream != null) {
					datasource = new JRXmlDataSource(getJasperReportsContext(), xmlInputStream, xPath);
				} else {
					File xmlFile = (File) getParameterValue(JRXPathQueryExecuterFactory.XML_FILE);
					if (xmlFile != null) {
						datasource = new JRXmlDataSource(getJasperReportsContext(), xmlFile, xPath);
					} else {
						String xmlSource = getStringParameterOrProperty(JRXPathQueryExecuterFactory.XML_SOURCE);
						if (xmlSource != null) {
							datasource = new JRXmlDataSource(getRepositoryContext(), xmlSource, xPath, false);
						} else {
							if (log.isWarnEnabled()){
								log.warn("No XML source was provided.");
							}
						}
					}
				}
			}

			if (datasource != null)
			{
				datasource.setLocale((Locale)getParameterValue(JRXPathQueryExecuterFactory.XML_LOCALE, true));
				datasource.setDatePattern(getStringParameter(JRXPathQueryExecuterFactory.XML_DATE_PATTERN, JRXPathQueryExecuterFactory.PROPERTY_XML_DATE_PATTERN));
				datasource.setNumberPattern(getStringParameter(JRXPathQueryExecuterFactory.XML_NUMBER_PATTERN, JRXPathQueryExecuterFactory.PROPERTY_XML_NUMBER_PATTERN));
				datasource.setTimeZone((TimeZone)getParameterValue(JRXPathQueryExecuterFactory.XML_TIME_ZONE, true));
			}
		}
		
		return datasource;
	}

	@Override
	public void close()
	{
		if (datasource != null){
			datasource.close();
		}
	}

	@Override
	public boolean cancelQuery() throws JRException
	{
		//nothing to cancel
		return false;
	}
	
}

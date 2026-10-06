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
package net.sf.jasperreports.data.xml;

import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.w3c.dom.Document;

import net.sf.jasperreports.dataadapters.AbstractDataAdapterService;
import net.sf.jasperreports.dataadapters.DataFileStream;
import net.sf.jasperreports.dataadapters.DataFileUtils;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.ParameterContributorContext;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.query.JRXPathQueryExecuterFactory;
import net.sf.jasperreports.engine.util.Designated;
import net.sf.jasperreports.engine.util.JRXmlUtils;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class XmlDataAdapterService extends AbstractDataAdapterService implements Designated
{
	
	public static final String SERVICE_DESIGNATION = "net.sf.jasperreports.data.adapter:XML";

	/**
	 * 
	 */
	public XmlDataAdapterService(
		ParameterContributorContext paramContribContext,
		XmlDataAdapter xmlDataAdapter
		) 
	{
		super(paramContribContext, xmlDataAdapter);
	}
	
	public XmlDataAdapter getXmlDataAdapter()
	{
		return (XmlDataAdapter)getDataAdapter();
	}
	
	@Override
	public void contributeParameters(Map<String, Object> parameters) throws JRException
	{
		XmlDataAdapter xmlDataAdapter = getXmlDataAdapter();
		if (xmlDataAdapter != null)
		{
			Document dataDocument = loadDataDocument(xmlDataAdapter, parameters);
			
			if (xmlDataAdapter.isUseConnection()) {
				
				/*
				if (this.getFilename().toLowerCase().startsWith("https://") ||
					this.getFilename().toLowerCase().startsWith("http://") ||
					this.getFilename().toLowerCase().startsWith("file:"))
				{
					map.put(JRXPathQueryExecuterFactory.XML_URL, this.getFilename());
				}
				else
				{
				*/
				parameters.put(JRXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT, dataDocument);
				//}
				
				
				Locale locale = xmlDataAdapter.getLocale();
				if (locale != null) {
					parameters.put(JRXPathQueryExecuterFactory.XML_LOCALE, locale);
				}

				TimeZone timeZone = xmlDataAdapter.getTimeZone();
				if (timeZone != null) {
					parameters.put(JRXPathQueryExecuterFactory.XML_TIME_ZONE, timeZone);
				}

				String datePattern = xmlDataAdapter.getDatePattern();
				if (datePattern != null && datePattern.trim().length() > 0) {
					parameters.put(JRXPathQueryExecuterFactory.XML_DATE_PATTERN, datePattern);
				}

				String numberPattern = xmlDataAdapter.getNumberPattern();
				if (numberPattern != null && numberPattern.trim().length() > 0) {
					parameters.put(JRXPathQueryExecuterFactory.XML_NUMBER_PATTERN, numberPattern);
				}	
			}
			else
			{
				JRXmlDataSource ds = new JRXmlDataSource(getJasperReportsContext(), dataDocument, xmlDataAdapter.getSelectExpression()); 

				Locale locale = xmlDataAdapter.getLocale();
				if (locale != null) {
					ds.setLocale(locale);
				}

				TimeZone timeZone = xmlDataAdapter.getTimeZone();
				if (timeZone != null) {
					ds.setTimeZone(timeZone);
				}
				
				String datePattern = xmlDataAdapter.getDatePattern();
				if (datePattern != null && datePattern.trim().length() > 0) {
					ds.setDatePattern(datePattern);
				}

				String numberPattern = xmlDataAdapter.getNumberPattern();
				if (numberPattern != null && numberPattern.trim().length() > 0) {
					ds.setNumberPattern(numberPattern);
				}	

				parameters.put(JRParameter.REPORT_DATA_SOURCE, ds);
			}
		}
	}

	protected Document loadDataDocument(XmlDataAdapter xmlDataAdapter, Map<String, Object> parameters) throws JRException
	{
		DataFileUtils dataFileUtils = DataFileUtils.instance(getParameterContributorContext());
		DataFileStream dataStream = dataFileUtils.getDataStream(
				xmlDataAdapter.getDataFile(), parameters);
		try
		{
			Document dataDocument = JRXmlUtils.parse(dataStream, xmlDataAdapter.isNamespaceAware());
			return dataDocument;
		}
		finally
		{
			dataStream.dispose();
		}
	}

	@Override
	public String getDesignation()
	{
		return SERVICE_DESIGNATION;
	}
	
}

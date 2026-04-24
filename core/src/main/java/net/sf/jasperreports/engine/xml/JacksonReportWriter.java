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
package net.sf.jasperreports.engine.xml;

import java.io.IOException;
import java.io.Writer;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JRTemplate;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.util.VersionComparator;
import net.sf.jasperreports.jackson.util.JacksonUtil;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JacksonReportWriter implements ReportWriter
{

	private final JasperReportsContext jasperReportsContext;
	
	public JacksonReportWriter(JasperReportsContext jasperReportsContext)
	{
		this.jasperReportsContext = jasperReportsContext;
	}

	@Override
	public boolean writeReport(JRReport report, String encoding, Writer out)
			throws IOException
	{
		String version = JRPropertiesUtil.getInstance(jasperReportsContext).getProperty(report, JRXmlWriter.PROPERTY_REPORT_VERSION);
		if (version == null || new VersionComparator().compare(version, JRConstants.VERSION_7_0_0) >= 0)
		{
			JacksonUtil.getInstance(jasperReportsContext).writeXml(report, out, version);
			return true;
		}
		return false;
	}

	@Override
	public boolean writeTemplate(JRTemplate template, String encoding,
			Writer out) throws IOException
	{
		String version = JRPropertiesUtil.getInstance(jasperReportsContext).getProperty(JRXmlWriter.PROPERTY_REPORT_VERSION);
		if (version == null || new VersionComparator().compare(version, JRConstants.VERSION_7_0_0) >= 0)
		{
			JacksonUtil.getInstance(jasperReportsContext).writeXml(template, out, version);
			return true;
		}
		return false;
	}

}

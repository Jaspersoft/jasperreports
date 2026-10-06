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
package net.sf.jasperreports.engine.fill;

import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.repo.RepositoryResourceContext;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class SimpleJasperReportSource implements JasperReportSource
{

	public static SimpleJasperReportSource from(JasperReport report)
	{
		SimpleJasperReportSource source = new SimpleJasperReportSource();
		source.setReport(report);
		return source;
	}

	public static SimpleJasperReportSource from(JasperReport report, String reportLocation, RepositoryResourceContext reportContext)
	{
		SimpleJasperReportSource source = new SimpleJasperReportSource();
		source.setReport(report);
		source.setReportLocation(reportLocation);
		source.setRepositoryReportContext(reportContext);
		return source;
	}
	
	private JasperReport report;
	private String reportLocation;
	private RepositoryResourceContext repositoryContext;
	
	public SimpleJasperReportSource()
	{
	}
	
	@Override
	public JasperReport getReport()
	{
		return report;
	}

	public void setReport(JasperReport report)
	{
		this.report = report;
	}

	@Override
	public String getReportLocation()
	{
		return reportLocation;
	}

	public void setReportLocation(String reportLocation)
	{
		this.reportLocation = reportLocation;
	}

	@Override
	public RepositoryResourceContext getRepositoryReportContext()
	{
		return repositoryContext;
	}

	public void setRepositoryReportContext(RepositoryResourceContext repositoryContext)
	{
		this.repositoryContext = repositoryContext;
	}

}

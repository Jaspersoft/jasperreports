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
package net.sf.jasperreports.components;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import net.sf.jasperreports.Report;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;

public class ReturnValuesTest
{
	
	@Test
	public void tableReturn(ITestContext testContext) throws JRException, IOException
	{
		Report report = new Report("net/sf/jasperreports/components/TableReturn.jrxml", 
				"net/sf/jasperreports/components/TableReturn.jrpxml");
		report.init(testContext);
		
		Map<String, Object> params = new HashMap<>();
		report.runReport(params);
	}
	
	@Test
	public void tableWithSubreportReturn(ITestContext testContext) throws JRException, IOException
	{
		Report subreport = new Report("net/sf/jasperreports/components/SubreportForReturn.jrxml", null);
		JasperReport compiledSubreport = subreport.compileReport();
		
		Report report = new Report("net/sf/jasperreports/components/TableWithSubreportReturn.jrxml", 
				"net/sf/jasperreports/components/TableWithSubreportReturn.jrpxml");
		report.init(testContext);
		
		Map<String, Object> params = new HashMap<>();
		params.put("subreport", compiledSubreport);
		report.runReport(params);
	}
	
	@Test
	public void tableWithListReturn(ITestContext testContext) throws JRException, IOException
	{
		Report report = new Report("net/sf/jasperreports/components/TableWithListReturn.jrxml", 
				"net/sf/jasperreports/components/TableWithListReturn.jrpxml");
		report.init(testContext);
		
		Map<String, Object> params = new HashMap<>();
		report.runReport(params);
	}
	
	@Test
	public void tableWithTableReturn(ITestContext testContext) throws JRException, IOException
	{
		Report report = new Report("net/sf/jasperreports/components/TableWithTableReturn.jrxml", 
				"net/sf/jasperreports/components/TableWithTableReturn.jrpxml");
		report.init(testContext);
		
		Map<String, Object> params = new HashMap<>();
		report.runReport(params);
	}
	
	@Test
	public void listWithSubreportReturn(ITestContext testContext) throws JRException, IOException
	{
		Report subreport = new Report("net/sf/jasperreports/components/SubreportForReturn.jrxml", null);
		JasperReport compiledSubreport = subreport.compileReport();
		
		Report report = new Report("net/sf/jasperreports/components/ListWithSubreportReturn.jrxml", 
				"net/sf/jasperreports/components/ListWithSubreportReturn.jrpxml");
		report.init(testContext);
		
		Map<String, Object> params = new HashMap<>();
		params.put("subreport", compiledSubreport);
		report.runReport(params);
	}
	
	@Test
	public void listWithListReturn(ITestContext testContext) throws JRException, IOException
	{
		Report report = new Report("net/sf/jasperreports/components/ListWithListReturn.jrxml", 
				"net/sf/jasperreports/components/ListWithListReturn.jrpxml");
		report.init(testContext);
		
		Map<String, Object> params = new HashMap<>();
		report.runReport(params);
	}
	
	@Test
	public void listWithTableReturn(ITestContext testContext) throws JRException, IOException
	{
		Report report = new Report("net/sf/jasperreports/components/ListWithTableReturn.jrxml", 
				"net/sf/jasperreports/components/ListWithTableReturn.jrpxml");
		report.init(testContext);
		
		Map<String, Object> params = new HashMap<>();
		report.runReport(params);
	}

}

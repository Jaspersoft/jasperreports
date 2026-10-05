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
package net.sf.jasperreports.virtualization;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import net.sf.jasperreports.OwnVirtualizerContainer;
import net.sf.jasperreports.PrintSerializer;
import net.sf.jasperreports.Report;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.fill.JRGzipVirtualizer;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ReportTest
{
	
	private Report report;

	@BeforeClass
	public void initReport(ITestContext testContext) throws JRException, IOException
	{
		report = new Report("net/sf/jasperreports/virtualization/FirstJasper.jrxml", 
				"net/sf/jasperreports/virtualization/FirstJasper.reference.jrpxml");
		report.addPrintConsumer(PrintSerializer.instance());
		report.addPrintConsumer(new PrintSerializer("serialized-virtualized", new OwnVirtualizerContainer(new JRGzipVirtualizer(5))));
		report.init(testContext);
	}
	
	@Test
	public void baseReport() throws JRException, NoSuchAlgorithmException, IOException
	{
		report.runReport(null);
	}
	
	@Test
	public void virtualizedReport() throws JRException, NoSuchAlgorithmException, IOException
	{
		HashMap<String, Object> params = new HashMap<>();
		JRGzipVirtualizer virtualizer = new JRGzipVirtualizer(3);
		params.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		
		report.runReport("virtualized", params);
	}
}

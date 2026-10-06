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
package net.sf.jasperreports.frames;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import net.sf.jasperreports.OwnVirtualizerContainer;
import net.sf.jasperreports.PrintSerializer;
import net.sf.jasperreports.Report;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.fill.JRGzipVirtualizer;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class FrameWhitespaceOverflowTest
{
	
	private Report report;

	@BeforeClass
	public void initReport(ITestContext testContext) throws JRException, IOException
	{
		report = new Report("net/sf/jasperreports/frames/FrameWhitespaceOverflow.jrxml", 
				"net/sf/jasperreports/frames/FrameWhitespaceOverflow.reference.jrpxml");
		report.init(testContext);
	}
	
	@Test
	public void runReport() throws JRException, NoSuchAlgorithmException, IOException
	{
		Map<String, Object> params = new HashMap<>();
		params.put(JRParameter.REPORT_DATA_SOURCE, new JREmptyDataSource(1));
		report.runReport(params);
	}

}

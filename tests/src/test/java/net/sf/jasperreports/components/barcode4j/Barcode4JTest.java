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
package net.sf.jasperreports.components.barcode4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import net.sf.jasperreports.Report;
import net.sf.jasperreports.engine.JRException;

public class Barcode4JTest
{
	
	@Test
	public void nullBarcode(ITestContext testContext) throws JRException, IOException
	{
		Report report = new Report("net/sf/jasperreports/components/barcode4j/Barcode4JNull.jrxml", 
				"net/sf/jasperreports/components/barcode4j/Barcode4JNull.jrpxml");
		report.init(testContext);
		
		Map<String, Object> params = new HashMap<>();
		report.runReport(params);
	}
	
	@Test
	public void nullQRCode(ITestContext testContext) throws JRException, IOException
	{
		Report report = new Report("net/sf/jasperreports/components/barcode4j/QRCodeNull.jrxml", 
				"net/sf/jasperreports/components/barcode4j/QRCodeNull.jrpxml");
		report.init(testContext);
		
		Map<String, Object> params = new HashMap<>();
		report.runReport(params);
	}

}

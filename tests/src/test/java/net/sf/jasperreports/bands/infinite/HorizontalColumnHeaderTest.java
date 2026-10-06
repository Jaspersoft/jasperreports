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
package net.sf.jasperreports.bands.infinite;

import java.util.HashMap;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import net.sf.jasperreports.Report;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRParameter;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class HorizontalColumnHeaderTest
{

	@Test
	public void test(ITestContext testContext)
	{
		Report report = new Report("net/sf/jasperreports/bands/infinite/ColumnOverflowTest");
		report.init(testContext);
		
		HashMap<String, Object> params = new HashMap<>();
		params.put(JRParameter.REPORT_DATA_SOURCE, new JREmptyDataSource());
		report.runReport(params);
	}
	
}

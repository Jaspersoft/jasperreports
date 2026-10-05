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
package net.sf.jasperreports.bands.splittypeprevent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import net.sf.jasperreports.Report;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class InputStreamImageTest
{

	@Test
	public void test(ITestContext testContext)
	{
		Report report = new Report("net/sf/jasperreports/bands/splittypeprevent/InputStreamImage.jrxml", 
				"net/sf/jasperreports/bands/splittypeprevent/InputStreamImage.reference.jrpxml");
		report.init(testContext);
		
		Map<String, Object> params = new HashMap<>();
		
		List<Map<String, ?>> records = new ArrayList<>();
		records.add(Collections.singletonMap("image", 
				InputStreamImageTest.class.getResourceAsStream("/net/sf/jasperreports/images/jaspersoft.png")));
		records.add(Collections.singletonMap("image", 
				InputStreamImageTest.class.getResourceAsStream("/net/sf/jasperreports/images/jasperreports.png")));
		records.add(Collections.singletonMap("image", 
				InputStreamImageTest.class.getResourceAsStream("/net/sf/jasperreports/virtualization/dukesign.jpg")));
		params.put(JRParameter.REPORT_DATA_SOURCE, new JRMapCollectionDataSource(records));
		
		report.runReport(params);
	}
	
}

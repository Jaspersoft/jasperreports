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

import java.util.Collection;
import java.util.Locale;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.AbstractSampleApp;
import net.sf.jasperreports.functions.annotations.FunctionCategoryBean;
import net.sf.jasperreports.functions.annotations.FunctionsInfo;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class FunctionsApp extends AbstractSampleApp
{


	/**
	 *
	 */
	public static void main(String[] args)
	{
		main(new FunctionsApp(), args);
	}
	
	
	@Override
	public void test() throws JRException
	{
		fill();
		html();
	}


	/**
	 *
	 */
	public void fill() throws JRException
	{
		long start = System.currentTimeMillis();
		JRDataSource datasource = createDataSource(DefaultJasperReportsContext.getInstance(), Locale.US);
		JasperFillManager.fillReportToFile("target/reports/FunctionsReport.jasper", null, datasource);
		System.out.println("Filling time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void html() throws JRException
	{
		long start = System.currentTimeMillis();
		JasperExportManager.exportReportToHtmlFile("target/reports/FunctionsReport.jrprint");
		System.out.println("HTML creation time : " + (System.currentTimeMillis() - start));
	}
	
	/**
	 *
	 */
	public JRDataSource createDataSource(
		JasperReportsContext jasperReportsContext, 
		Locale locale 
		) 
	{
		FunctionsInfo functionsInfo = FunctionsInfo.getInstance(jasperReportsContext, locale);
		Collection<FunctionCategoryBean> categories = functionsInfo.getCategories(); 
		return new JRBeanCollectionDataSource(categories);
	}


}

/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2019 TIBCO Software Inc. All rights reserved.
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
package net.sf.jasperreports.engine.fill;

import java.sql.Connection;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.type.SectionTypeEnum;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class JRFiller
{

	public static final String EXCEPTION_MESSAGE_KEY_THREAD_INTERRUPTED = "fill.common.filler.thread.interrupted";
	public static final String EXCEPTION_MESSAGE_KEY_UNKNOWN_REPORT_SECTION_TYPE = "fill.common.filler.unknown.report.section.type";
	
	/**
	 * The default locale used to fill reports.
	 * 
	 * <p>
	 * The property is overridden by the value of the {@link JRParameter#REPORT_LOCALE} parameter.
	 * By default, the system/JVM locale is used.
	 * </p>
	 * 
	 * @see JRParameter#REPORT_LOCALE
	 */
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			valueType = Locale.class,
			defaultValue = "system locale",
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_5_2_0
			)
	public static final String PROPERTY_DEFAULT_LOCALE = JRPropertiesUtil.PROPERTY_PREFIX + "default.locale";
	

	/**
	 * The default time zone used to fill reports.
	 * 
	 * <p>
	 * The property is overridden by the value of the {@link JRParameter#REPORT_TIME_ZONE} parameter.
	 * By default, the system/JVM time zone is used.
	 * </p>
	 * 
	 * @see JRParameter#REPORT_TIME_ZONE
	 */
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			valueType = TimeZone.class,
			defaultValue = "system time zone",
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_5_2_0
			)
	public static final String PROPERTY_DEFAULT_TIMEZONE = JRPropertiesUtil.PROPERTY_PREFIX + "default.timezone";

	/**
	 * Number of rows to fill the report/list/table rows with.
	 * It will stop filling rows if there is a page break. That is, to fill to the end of the report, specify a large value.
	 * 
	 * @see JRParameter#ROWS_TO_FILL
	 */
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			valueType = Locale.class,
			defaultValue = "0",
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_8_0
			)
	public static final String PROPERTY_ROWS_TO_FILL = JRPropertiesUtil.PROPERTY_PREFIX + "rows.to.fill";

	/**
	 *
	 */
	public static JasperPrint fill(
		JasperReportsContext jasperReportsContext,
		JasperReport jasperReport,
		Map<String,Object> parameters,
		Connection conn
		) throws JRException
	{
		return fill(jasperReportsContext, SimpleJasperReportSource.from(jasperReport),
				parameters, conn);
	}

	public static JasperPrint fill(
		JasperReportsContext jasperReportsContext,
		JasperReportSource reportSource,
		Map<String,Object> parameters,
		Connection conn
		) throws JRException
	{
		ReportFiller filler = createReportFiller(jasperReportsContext, reportSource);
		
		JasperPrint jasperPrint = null;
		
		try
		{
			jasperPrint = filler.fill(parameters, conn);
		}
		catch(JRFillInterruptedException e)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_THREAD_INTERRUPTED,
					null,
					e);
		}
		
		return jasperPrint;
	}


	/**
	 *
	 */
	public static JasperPrint fill(
		JasperReportsContext jasperReportsContext,
		JasperReport jasperReport,
		Map<String,Object> parameters,
		JRDataSource dataSource
		) throws JRException
	{
		int rowsToFill = 0;
		try
		{
			rowsToFill = JRPropertiesUtil.getInstance(jasperReportsContext).getIntegerProperty(jasperReport, JRFiller.PROPERTY_ROWS_TO_FILL, 0);
		}
		catch (NumberFormatException e) {
		}
		return fill(jasperReportsContext, SimpleJasperReportSource.from(jasperReport),
				parameters, dataSource, rowsToFill);
	}
	
	public static JasperPrint fill(
		JasperReportsContext jasperReportsContext,
		JasperReportSource reportSource,
		Map<String,Object> parameters,
		JRDataSource dataSource
		) throws JRException
	{
		return fill(jasperReportsContext, reportSource,
				parameters, dataSource, 0);
	}

	public static JasperPrint fill(
		JasperReportsContext jasperReportsContext,
		JasperReportSource reportSource,
		Map<String,Object> parameters,
		JRDataSource dataSource,
		int rowsToFill
		) throws JRException
	{
		ReportFiller filler = createReportFiller(jasperReportsContext, reportSource);
		
		JasperPrint jasperPrint = null;
		
		try
		{
			jasperPrint = filler.fill(parameters, dataSource, rowsToFill);
		}
		catch(JRFillInterruptedException e)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_THREAD_INTERRUPTED,
					null,
					e);
		}
		
		return jasperPrint;
	}
	

	/**
	 * Fills a report.
	 * <p/>
	 * The data source used to fill the report is determined in the following way:
	 * <ul>
	 * 	<li>If a non-null value of the {@link net.sf.jasperreports.engine.JRParameter#REPORT_DATA_SOURCE REPORT_DATA_SOURCE}
	 * has been specified, it will be used as data source.</li>
	 * 	<li>Otherwise, if the report has a query then a data source will be created based on the query and connection
	 * parameter values.</li>
	 * 	<li>Otherwise, the report will be filled without a data source.</li>
	 * </ul>
	 * 
	 * @param jasperReport the report
	 * @param parameters the fill parameters
	 * @return the filled report
	 * @throws JRException
	 */
	public static JasperPrint fill(
		JasperReportsContext jasperReportsContext,
		JasperReport jasperReport, 
		Map<String,Object> parameters
		) throws JRException
	{
		return fill(jasperReportsContext, SimpleJasperReportSource.from(jasperReport),
				parameters);
	}
	
	public static JasperPrint fill(
			JasperReportsContext jasperReportsContext,
			JasperReportSource reportSource, 
			Map<String,Object> parameters
			) throws JRException
	{
		ReportFiller filler = createReportFiller(jasperReportsContext, reportSource);

		try
		{
			JasperPrint jasperPrint = filler.fill(parameters);

			return jasperPrint;
		}
		catch (JRFillInterruptedException e)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_THREAD_INTERRUPTED,
					null,
					e);
		}
	}


	/**
	 *
	 */
	//FIXMEBOOK deprecate?
	public static JRBaseFiller createFiller(JasperReportsContext jasperReportsContext, JasperReport jasperReport) throws JRException
	{
		return createBandReportFiller(jasperReportsContext, SimpleJasperReportSource.from(jasperReport));
	}

	protected static JRBaseFiller createBandReportFiller(JasperReportsContext jasperReportsContext, JasperReportSource reportSource) throws JRException
	{
		JRBaseFiller filler = null;

		switch (reportSource.getReport().getPrintOrderValue())
		{
			case HORIZONTAL :
			{
				filler = new JRHorizontalFiller(jasperReportsContext, reportSource, null);
				break;
			}
			case VERTICAL :
			{
				filler = new JRVerticalFiller(jasperReportsContext, reportSource, null);
				break;
			}
		}
		return filler;
	}
	
	public static ReportFiller createReportFiller(JasperReportsContext jasperReportsContext, JasperReport jasperReport) throws JRException
	{
		return createReportFiller(jasperReportsContext, SimpleJasperReportSource.from(jasperReport));
	}
	
	public static ReportFiller createReportFiller(JasperReportsContext jasperReportsContext, 
			JasperReportSource reportSource) throws JRException
	{
		ReportFiller filler;
		SectionTypeEnum sectionType = reportSource.getReport().getSectionType();
		sectionType = sectionType == null ? SectionTypeEnum.BAND : sectionType;
		switch (sectionType)
		{
		case BAND:
			filler = createBandReportFiller(jasperReportsContext, reportSource);
			break;
		case PART:
		{
			filler = new PartReportFiller(jasperReportsContext, reportSource, null);
			break;
		}
		default:
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_UNKNOWN_REPORT_SECTION_TYPE,  
					new Object[]{reportSource.getReport().getSectionType()} 
					);
		}
		return filler;
	}
	
	
	private JRFiller()
	{
	}
}

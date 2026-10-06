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
package net.sf.jasperreports.export;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.export.ExporterFilter;
import net.sf.jasperreports.engine.export.JRExportProgressMonitor;
import net.sf.jasperreports.engine.export.JRHyperlinkProducerFactory;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * Instances of this class are used for configuring the behavior of exporters.
 * <p>
 * see {@link Exporter#setConfiguration(ReportExportConfiguration)}
 * see {@link ExporterInputItem#getConfiguration()}
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface ReportExportConfiguration extends CommonExportConfiguration
{
	/**
	 * This property serves as default value for the {@link XlsReportConfiguration#isIgnorePageMargins()} 
	 * and {@link HtmlReportConfiguration#isIgnorePageMargins()} exporter configuration settings.
	 * <p>
	 * The property itself defaults to <code>false</code>.
	 * </p>
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_2_0_5,
			valueType = Boolean.class
			)
	public static final String PROPERTY_IGNORE_PAGE_MARGINS = JRPropertiesUtil.PROPERTY_PREFIX + "export.ignore.page.margins";
	

	/**
	 * Returns an integer value representing the start index of the page range to be exported. 
	 * This is useful when only a range of pages is needed for export.
	 */
	public Integer getStartPageIndex();
	

	/**
	 * Returns an integer value representing the end index of the page range to be exported. 
	 * This is useful when only a range of pages is needed for export.
	 */
	public Integer getEndPageIndex();
	

	/**
	 * Returns an integer value representing the index of the page to be exported. 
	 * This is useful when only one page of the entire report is needed for export.
	 */
	public Integer getPageIndex();
	

	/**
	 * Return a {@link net.sf.jasperreports.engine.export.JRExportProgressMonitor JRExportProgressMonitor} instance for monitoring export status. 
	 * This is useful for users who need to be notified after each page is exported (a GUI tool that shows a progress bar might need this feature).
	 */
	public JRExportProgressMonitor getProgressMonitor();
	

	/**
	 * Returns an instance of the {@link net.sf.jasperreports.engine.export.ExporterFilter ExporterFilter} interface to be used by the exporter 
	 * to filter the elements to be exported.
	 */
	public ExporterFilter getExporterFilter();
	

	/**
	 * A setting that allows users to move the entire content of each page horizontally. 
	 * It is mostly useful for printing, when the report data does not fit inside the page margins.
	 */
	public Integer getOffsetX();
	

	/**
	 * A setting that allows users to move the entire content of each page vertically. 
	 * It is mostly useful for printing, when the report data does not fit inside the page margins.
	 */
	public Integer getOffsetY();
	

	/**
	 * Returns a {@link net.sf.jasperreports.engine.export.JRHyperlinkProducerFactory JRHyperlinkProducerFactory} which should be used for custom 
	 * hyperlink types during export.
	 */
	public JRHyperlinkProducerFactory getHyperlinkProducerFactory();
}

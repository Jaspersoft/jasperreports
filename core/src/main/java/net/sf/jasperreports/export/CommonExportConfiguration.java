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
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface CommonExportConfiguration
{
	/**
	 * A global (per context) property that serves as default value for the {@link ReportExportConfiguration#isOverrideHints()} setting 
	 * and establishes the priority of export configuration settings against report hints.
	 * 
	 * If the property is true, export configuration settings override report hints; this is the
	 * default behavior.
	 * 
	 * This property cannot be used as a report export hint.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_TRUE,
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_5_5_1,
			valueType = Boolean.class
			)
	public static final String PROPERTY_EXPORT_CONFIGURATION_OVERRIDE_REPORT_HINTS = 
		JRPropertiesUtil.PROPERTY_PREFIX + "export.configuration.override.report.hints";
	
	/**
	 * Specifies whether the settings provided by this exporter configuration object are supposed to override the equivalent exporter 
	 * hints specified in the reports themselves using configuration properties.
	 * @see #PROPERTY_EXPORT_CONFIGURATION_OVERRIDE_REPORT_HINTS
	 */
	public Boolean isOverrideHints();
}

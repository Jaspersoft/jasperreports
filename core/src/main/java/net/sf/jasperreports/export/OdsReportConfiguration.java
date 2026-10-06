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
import net.sf.jasperreports.engine.export.oasis.JROdsExporter;
import net.sf.jasperreports.export.annotations.ExporterProperty;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * Interface containing settings used by the ODS exporter.
 *
 * @see JROdsExporter
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface OdsReportConfiguration extends XlsReportConfiguration
{

	/**
	 * This property serves as default value for the {@link #isFlexibleRowHeight()} export configuration flag.
	 * <p>
	 * The property itself defaults to <code>false</code>.
	 * </p>
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_5_0_4,
			valueType = Boolean.class
			)
	public static final String PROPERTY_ODS_FLEXIBLE_ROW_HEIGHT = JRPropertiesUtil.PROPERTY_PREFIX + "export.ods.flexible.row.height";

	/**
	 * Indicates whether table rows in ODS documents can grow if more text is added into cells.
	 * <p>
	 * Is set to <code>false</code>, the table rows do not increase in height automatically and the user has to enlarge them manually.
	 * </p>
	 * @see #PROPERTY_ODS_FLEXIBLE_ROW_HEIGHT
	 */
	@ExporterProperty(
		value=PROPERTY_ODS_FLEXIBLE_ROW_HEIGHT,
		booleanDefault=false
		)
	public Boolean isFlexibleRowHeight();

}

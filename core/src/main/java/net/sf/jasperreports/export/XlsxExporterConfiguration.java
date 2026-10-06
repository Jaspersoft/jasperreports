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
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.annotations.ExporterProperty;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * Interface containing settings used by the XLSX exporter.
 *
 * @see JRXlsxExporter
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface XlsxExporterConfiguration extends XlsExporterConfiguration
{

	/**
	 * Property used to provide a default value for the {@link #getMacroTemplate()} export configuration setting.
	 * 
	 * @see JRPropertiesUtil
	 * @since 4.5.1
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_4_5_1
			)
	public static final String PROPERTY_MACRO_TEMPLATE = JRPropertiesUtil.PROPERTY_PREFIX + "export.xlsx.macro.template";

	/**
	 * Specifies the location of an existing workbook template containing a macro object. 
	 * The macro object will be copied into the generated document if the template location is valid. 
	 * Macros can be loaded from Excel macro-enabled template files (*.xltm) as well as from valid 
	 * Excel macro-enabled documents (*.xlsm).
	 * @see #PROPERTY_MACRO_TEMPLATE
	 */
	@ExporterProperty(PROPERTY_MACRO_TEMPLATE)
	public String getMacroTemplate();

}

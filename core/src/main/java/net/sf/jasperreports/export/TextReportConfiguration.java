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
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.export.annotations.ExporterProperty;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * Interface containing settings used by the pure text exporter.
 *
 * @see JRTextExporter
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface TextReportConfiguration extends ReportExportConfiguration
{
	/**
	 * Property whose value is used as default state of the {@link #getCharWidth()} export configuration setting.
	 * <p/>
	 * This property is not set by default.
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_3_6_1,
			valueType = Float.class
			)
	public static final String PROPERTY_CHARACTER_WIDTH = JRPropertiesUtil.PROPERTY_PREFIX + "export.text.character.width";

	/**
	 * Property whose value is used as default state of the {@link #getCharHeight()} export configuration setting.
	 * <p/>
	 * This property is not set by default.
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_3_6_1,
			valueType = Float.class
			)
	public static final String PROPERTY_CHARACTER_HEIGHT = JRPropertiesUtil.PROPERTY_PREFIX + "export.text.character.height";

	/**
	 * Property whose value is used as default state of the {@link #getPageWidthInChars()} export configuration setting.
	 * <p/>
	 * This property is not set by default.
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_3_6_1,
			valueType = Integer.class
			)
	public static final String PROPERTY_PAGE_WIDTH = JRPropertiesUtil.PROPERTY_PREFIX + "export.text.page.width";

	/**
	 * Property whose value is used as default state of the {@link #getPageHeightInChars()} export configuration setting.
	 * <p/>
	 * This property is not set by default.
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_3_6_1,
			valueType = Integer.class
			)
	public static final String PROPERTY_PAGE_HEIGHT = JRPropertiesUtil.PROPERTY_PREFIX + "export.text.page.height";

	/**
	 * Returns a float value representing the pixel/character horizontal ratio.
	 * @see #PROPERTY_CHARACTER_WIDTH
	 */
	@ExporterProperty(
		value=PROPERTY_CHARACTER_WIDTH, 
		intDefault=0
		)
	public Float getCharWidth();

	/**
	 * Returns a float value representing the pixel/character vertical ratio.
	 * @see #PROPERTY_CHARACTER_HEIGHT
	 */
	@ExporterProperty(
		value=PROPERTY_CHARACTER_HEIGHT, 
		intDefault=0
		)
	public Float getCharHeight();

	/**
	 * Returns an integer representing the page width in characters.
	 * @see #PROPERTY_PAGE_WIDTH
	 */
	@ExporterProperty(
		value=PROPERTY_PAGE_WIDTH, 
		intDefault=0
		)
	public Integer getPageWidthInChars();

	/**
	 * Returns an integer representing the page height in characters.
	 * @see #PROPERTY_PAGE_HEIGHT
	 */
	@ExporterProperty(
		value=PROPERTY_PAGE_HEIGHT, 
		intDefault=0
		)
	public Integer getPageHeightInChars();
}

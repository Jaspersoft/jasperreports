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
package net.sf.jasperreports.export;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.export.annotations.ExporterProperty;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * Interface containing settings used by the DOCX exporter.
 *
 * @see JRDocxExporter
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface DocxReportConfiguration extends ReportExportConfiguration
{
	/**
	 * This property serves as default value for the {@link #isFramesAsNestedTables()} export configuration setting.
	 * <p>
	 * The property itself defaults to <code>true</code>.
	 * </p>
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_TRUE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.FRAME},
			sinceVersion = PropertyConstants.VERSION_3_5_3,
			valueType = Boolean.class
			)
	public static final String PROPERTY_FRAMES_AS_NESTED_TABLES = JRDocxExporter.DOCX_EXPORTER_PROPERTIES_PREFIX + "frames.as.nested.tables";

	/**
	 * This property serves as default value for the {@link #isFlexibleRowHeight} export configuration setting.
	 * <p>
	 * The property itself defaults to <code>false</code>.
	 * </p>
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_3_6_1,
			valueType = Boolean.class
			)
	public static final String PROPERTY_FLEXIBLE_ROW_HEIGHT = JRDocxExporter.DOCX_EXPORTER_PROPERTIES_PREFIX + "flexible.row.height";
	
	/**
	 * This property serves as default value for the {@link #isNewLineAsParagraph()} export configuration setting.
	 * <p>
	 * The property itself defaults to <code>false</code>.
	 * </p>
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.TEXT_ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_1_1,
			valueType = Boolean.class
			)
	public static final String PROPERTY_NEW_LINE_AS_PARAGRAPH = JRDocxExporter.DOCX_EXPORTER_PROPERTIES_PREFIX + "new.line.as.paragraph";

	/**
	 * Property that provides a default value for the {@link #isIgnoreHyperlink()} export configuration flag.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.HYPERLINK},
			sinceVersion = PropertyConstants.VERSION_5_1_2,
			valueType = Boolean.class
			)
	public static final String PROPERTY_IGNORE_HYPERLINK = JRDocxExporter.DOCX_EXPORTER_PROPERTIES_PREFIX + JRPrintHyperlink.PROPERTY_IGNORE_HYPERLINK_SUFFIX;

	/**
	 * Property that provides a default value for the {@link #isBackgroundAsHeader()} export configuration flag.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_TRUE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_21_3,
			valueType = Boolean.class
			)
	public static final String PROPERTY_BACKGROUND_AS_HEADER = JRDocxExporter.DOCX_EXPORTER_PROPERTIES_PREFIX + "background.as.header";

	/**
	 * Property which specifies a default for the {@link #isSizePageToContent()} export configuration flag.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_7_0_4,
			valueType = Boolean.class
			)
	public static final String PROPERTY_SIZE_PAGE_TO_CONTENT = JRDocxExporter.DOCX_EXPORTER_PROPERTIES_PREFIX + "size.page.to.content";

	/**
	 * Indicates whether {@link JRPrintFrame frames} are to be exported as nested tables.
	 * <p>
	 * If set to <code>false</code>, the frame contents will be integrated into the master/page table.
	 * </p>
	 * @see #PROPERTY_FRAMES_AS_NESTED_TABLES
	 */
	@ExporterProperty(
		value=PROPERTY_FRAMES_AS_NESTED_TABLES, 
		booleanDefault=true
		)
	public Boolean isFramesAsNestedTables();
	
	/**
	 * Indicates whether table rows can grow if more text is added into cells.
	 * <p>
	 * If set to <code>false</code>, the table rows do not increase in height automatically and the user has to enlarge them manually.
	 * </p>
	 * @see #PROPERTY_FLEXIBLE_ROW_HEIGHT
	 */
	@ExporterProperty(
			value=PROPERTY_FLEXIBLE_ROW_HEIGHT, 
			booleanDefault=false
			)
	public Boolean isFlexibleRowHeight();
	
	/**
	 * Indicates whether the newline element present in a justified paragraph introduces a new justified paragraph. 
	 * <p>
	 * If set to <code>true</code>, the text line before the new paragraph will lose the justified alignment.
	 * </p>
	 * @see #PROPERTY_NEW_LINE_AS_PARAGRAPH
	 */
	@ExporterProperty(
		value=PROPERTY_NEW_LINE_AS_PARAGRAPH, 
		booleanDefault=false
		)
	public Boolean isNewLineAsParagraph();
	
	/**
	 * @see #PROPERTY_IGNORE_HYPERLINK
	 */
	@ExporterProperty(
		value=PROPERTY_IGNORE_HYPERLINK, 
		booleanDefault=false
		)
	public Boolean isIgnoreHyperlink();
	
	/**
	 * @see #PROPERTY_BACKGROUND_AS_HEADER
	 */
	@ExporterProperty(
		value=PROPERTY_BACKGROUND_AS_HEADER, 
		booleanDefault=true
		)
	public Boolean isBackgroundAsHeader();
	
	/**
	 * Flag which specifies if the size of each page should be increased to accommodate its content.
	 * @see #PROPERTY_SIZE_PAGE_TO_CONTENT
	 */
	@ExporterProperty(
		value=PROPERTY_SIZE_PAGE_TO_CONTENT, 
		booleanDefault=false
		)
	public Boolean isSizePageToContent();
}

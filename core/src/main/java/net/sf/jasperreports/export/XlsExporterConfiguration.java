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
import net.sf.jasperreports.engine.export.JRXlsAbstractExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.annotations.ExporterProperty;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * Interface containing settings used by the Excel exporters.
 *
 * @see net.sf.jasperreports.poi.export.JRXlsExporter
 * @see net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter
 * @see net.sf.jasperreports.engine.export.oasis.JROdsExporter
 * @see net.sf.jasperreports.poi.export.JRXlsMetadataExporter
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface XlsExporterConfiguration extends ExporterConfiguration
{
	/**
	 * Property whose value is used as default state of the {@link #isCreateCustomPalette()} export configuration flag.
	 * <p/>
	 * This property is by default not set (<code>false</code>).
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_2_0_0,
			valueType = Boolean.class
			)
	public static final String PROPERTY_CREATE_CUSTOM_PALETTE = JRPropertiesUtil.PROPERTY_PREFIX + "export.xls.create.custom.palette";

	/**
	 * Property that provides a default value for the {@link #getWorkbookTemplate()} export configuration setting.
	 * 
	 * @see JRPropertiesUtil
	 * @since 4.5.1
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_4_5_1
			)
	public static final String PROPERTY_WORKBOOK_TEMPLATE = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "workbook.template";

	/**
	 * Property that provides a default value for the {@link #isKeepWorkbookTemplateSheets()} export configuration flag.
	 * <p>
	 * This property is used in conjunction with {@link #PROPERTY_WORKBOOK_TEMPLATE}.
	 * <p>
	 * Default value is <code>false</code>.
	 * 
	 * @see JRPropertiesUtil
	 * @since 4.5.1
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_4_5_1,
			valueType = Boolean.class
			)
	public static final String PROPERTY_WORKBOOK_TEMPLATE_KEEP_SHEETS = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "workbook.template.keep.sheets";

	/**
	 * Property whose value is used as default for the {@link #getMetadataTitle()} export configuration setting.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_3_1
			)
	public static final String PROPERTY_METADATA_TITLE = JRPropertiesUtil.PROPERTY_PREFIX + "export.xls.metadata.title";

	/**
	 * Property whose value is used as default for the {@link #getMetadataAuthor()} export configuration setting.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_3_1
			)
	public static final String PROPERTY_METADATA_AUTHOR = JRPropertiesUtil.PROPERTY_PREFIX + "export.xls.metadata.author";

	/**
	 * Property whose value is used as default for the {@link #getMetadataSubject()} export configuration setting.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_3_1
			)
	public static final String PROPERTY_METADATA_SUBJECT = JRPropertiesUtil.PROPERTY_PREFIX + "export.xls.metadata.subject";

	/**
	 * Property whose value is used as default for the {@link #getMetadataKeywords()} export configuration setting.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_3_1
			)
	public static final String PROPERTY_METADATA_KEYWORDS = JRPropertiesUtil.PROPERTY_PREFIX + "export.xls.metadata.keywords";

	/**
	 * Property whose value is used as default for the {@link #getMetadataApplication()} export configuration setting.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_3_1
			)
	public static final String PROPERTY_METADATA_APPLICATION = JRPropertiesUtil.PROPERTY_PREFIX + "export.xls.metadata.application";
	
	/**
	 * Property whose value is used as default for the {@link #getEncryptionPassword()} export configuration setting.
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_20_1
			)
	public static final String PROPERTY_ENCRYPTION_PASSWORD = JRPropertiesUtil.PROPERTY_PREFIX + "export.xls.encryption.password";

	/**
	 * Returns a boolean value specifying whether the standard color palette should be customized
	 * so that the XLS result uses the original report colors.
	 * <p/>
	 * The default state of this flag is given by the
	 * {@link #PROPERTY_CREATE_CUSTOM_PALETTE net.sf.jasperreports.export.xls.create.custom.palette} property.
	 * <p/>
	 * The colors used in the result XLS are determined in the following manner:
	 * <ol>
	 * 	<li>
	 * If this flag is not set, the nearest color from the standard XLS palette is chosen
	 * for a report color.
	 * 	</li>
	 * 	<li>
	 * If the flag is set, the nearest not yet modified color from the palette is chosen
	 * and modified to exactly match the report color.  If all the colors from the palette
	 * are modified (the palette has a fixed size), the nearest color from the palette is
	 * chosen for further report colors.
	 * 	</li>
	 * </ol> 
	 * 
	 * @see #PROPERTY_CREATE_CUSTOM_PALETTE
	 */
	@ExporterProperty(
		value=PROPERTY_CREATE_CUSTOM_PALETTE, 
		booleanDefault=false
		)
	public Boolean isCreateCustomPalette();

	/**
	 * Setting that specifies the location of an existing workbook template. The content of an existing workbook document 
	 * or template can be embedded into exported document if the template location is known. In this case the content of 
	 * the template will be exported first and the content of the exported report will be appended to this one. Macros and 
	 * other settings in the existing template will be also preserved in the generated document. Templates can be loaded from 
	 * Excel template files (*.xlt) as well as from valid Excel documents (*.xls).
	 * <p>
	 * This setting is used in the Excel exporter based on Apache POI APIs ({@link net.sf.jasperreports.poi.export.JRXlsExporter}). There's no similar property for the {@link JRXlsxExporter}.
	 * @see #PROPERTY_WORKBOOK_TEMPLATE
	 */
	@ExporterProperty(PROPERTY_WORKBOOK_TEMPLATE)
	public String getWorkbookTemplate();
	
	/**
	 * Flag that specifies whether to keep the sheets of the existing template into generated document. Sometimes 
	 * is important to embed in a generated document only macros and/or other global settings from an existing template, but 
	 * without keeping the own sheets of the template document. If set to false, this property prevent the template sheets 
	 * to be exported.
	 * <p>
	 * This setting is used in conjunction with {@link #getWorkbookTemplate()}.
	 * 
	 * @see #PROPERTY_WORKBOOK_TEMPLATE_KEEP_SHEETS
	 */
	@ExporterProperty(
		value=PROPERTY_WORKBOOK_TEMPLATE_KEEP_SHEETS, 
		booleanDefault=false
		)
	public Boolean isKeepWorkbookTemplateSheets();

	/**
	 * The Title of the XLS document.
	 */
	@ExporterProperty(PROPERTY_METADATA_TITLE)
	public String getMetadataTitle();

	/**
	 * The Author of the XLS document.
	 */
	@ExporterProperty(PROPERTY_METADATA_AUTHOR)
	public String getMetadataAuthor();

	/**
	 * The Subject of the XLS document.
	 */
	@ExporterProperty(PROPERTY_METADATA_SUBJECT)
	public String getMetadataSubject();

	/**
	 * The Keywords of the XLS document.
	 */
	@ExporterProperty(PROPERTY_METADATA_KEYWORDS)
	public String getMetadataKeywords();

	/**
	 * The Application for the XLS document. Defaults to "JasperReports Library version x.x.x".
	 */
	@ExporterProperty(PROPERTY_METADATA_APPLICATION)
	public String getMetadataApplication();

	/**
	 * The encryption password needed to open the document, if it is encrypted.
	 * @see #PROPERTY_ENCRYPTION_PASSWORD
	 */
	@ExporterProperty(PROPERTY_ENCRYPTION_PASSWORD)
	public String getEncryptionPassword();
}

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
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;
import net.sf.jasperreports.export.annotations.ExporterProperty;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * Interface containing settings used by the PPTX exporter.
 *
 * @see JRPptxExporter
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface PptxExporterConfiguration extends ExporterConfiguration
{
	/**
	 * Property whose value is used as default for the {@link #getMetadataTitle()} export configuration setting.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_3_1
			)
	public static final String PROPERTY_METADATA_TITLE = JRPptxExporter.PPTX_EXPORTER_PROPERTIES_PREFIX + "metadata.title";

	/**
	 * Property whose value is used as default for the {@link #getMetadataAuthor()} export configuration setting.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_3_1
			)
	public static final String PROPERTY_METADATA_AUTHOR = JRPptxExporter.PPTX_EXPORTER_PROPERTIES_PREFIX + "metadata.author";

	/**
	 * Property whose value is used as default for the {@link #getMetadataSubject()} export configuration setting.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_3_1
			)
	public static final String PROPERTY_METADATA_SUBJECT = JRPptxExporter.PPTX_EXPORTER_PROPERTIES_PREFIX + "metadata.subject";

	/**
	 * Property whose value is used as default for the {@link #getMetadataKeywords()} export configuration setting.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_3_1
			)
	public static final String PROPERTY_METADATA_KEYWORDS = JRPptxExporter.PPTX_EXPORTER_PROPERTIES_PREFIX + "metadata.keywords";

	/**
	 * Property whose value is used as default for the {@link #getMetadataApplication()} export configuration setting.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_3_1
			)
	public static final String PROPERTY_METADATA_APPLICATION = JRPptxExporter.PPTX_EXPORTER_PROPERTIES_PREFIX + "metadata.application";

	/**
	 * Property that provides a default value for the {@link #getSlideMasterReport()} export configuration setting.
	 * <p>
	 * Default value is <code>1</code>.
	 * 
	 * @see JRPropertiesUtil
	 * @since 6.8.0
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = "1",
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_8_0,
			valueType = Integer.class
			)
	public static final String PROPERTY_SLIDE_MASTER_REPORT = JRPptxExporter.PPTX_EXPORTER_PROPERTIES_PREFIX + "slide.master.report";

	/**
	 * Property that provides a default value for the {@link #getSlideMasterPage()} export configuration setting.
	 * <p>
	 * Default value is <code>1</code>.
	 * 
	 * @see JRPropertiesUtil
	 * @since 6.8.0
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = "1",
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_8_0,
			valueType = Integer.class
			)
	public static final String PROPERTY_SLIDE_MASTER_PAGE = JRPptxExporter.PPTX_EXPORTER_PROPERTIES_PREFIX + "slide.master.page";

	/**
	 * Property that provides a default value for the {@link #isBackgroundAsSlideMaster()} export configuration flag.
	 * <p>
	 * Default value is <code>false</code>.
	 * 
	 * @see JRPropertiesUtil
	 * @since 6.8.0
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_8_0,
			valueType = Boolean.class
			)
	public static final String PROPERTY_BACKGROUND_AS_SLIDE_MASTER = JRPptxExporter.PPTX_EXPORTER_PROPERTIES_PREFIX + "background.as.slide.master";

	/**
	 * Property that indicates if the fonts used in the report should be embedded into the generated PPTX document. 
	 * <p/>
	 * <b>Note: </b>PowerPoint does not embed TTF content type. Only EOT (Embedded OpenType) fonts are considered for embedding. 
	 * Embeddable TTF fonts should be converted into EOT format first, then placed in the classpath using the font extension mechanism.
	 * <p/>
	 * This property serves as default value for the {@link #isEmbedFonts()} export configuration setting.
	 * <p/>
	 * @see JRPropertiesUtil
	 */
	@Property(
			name = "net.sf.jasperreports.export.pptx.embed.fonts",
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_9_0,
			valueType = Boolean.class
			)
	public static final String PROPERTY_EMBED_FONTS = JRPptxExporter.PPTX_EXPORTER_PROPERTIES_PREFIX + "embed.fonts";
	
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
	public static final String PROPERTY_ENCRYPTION_PASSWORD = JRPropertiesUtil.PROPERTY_PREFIX + "export.pptx.encryption.password";

	/**
	 * The Title of the PPTX document.
	 */
	@ExporterProperty(PROPERTY_METADATA_TITLE)
	public String getMetadataTitle();

	/**
	 * The Author of the PPTX document.
	 */
	@ExporterProperty(PROPERTY_METADATA_AUTHOR)
	public String getMetadataAuthor();

	/**
	 * The Subject of the PPTX document.
	 */
	@ExporterProperty(PROPERTY_METADATA_SUBJECT)
	public String getMetadataSubject();

	/**
	 * The Keywords of the PPTX document.
	 */
	@ExporterProperty(PROPERTY_METADATA_KEYWORDS)
	public String getMetadataKeywords();

	/**
	 * The Application for the PPTX document. Defaults to "JasperReports Library version x.x.x".
	 */
	@ExporterProperty(PROPERTY_METADATA_APPLICATION)
	public String getMetadataApplication();

	/**
	 * Flag that specifies whether the elements from the background section on the first page should be exported as the contents of the slide master,
	 * and then ignoring the background section elements for all pages/slides.
	 * 
	 * @see #PROPERTY_BACKGROUND_AS_SLIDE_MASTER
	 */
	@ExporterProperty(
		value=PROPERTY_BACKGROUND_AS_SLIDE_MASTER, 
		booleanDefault=false
		)
	public Boolean isBackgroundAsSlideMaster();

	/**
	 * Specifies the report (export input item) from which the content of the slide master should be extracted. The default value is 1 (first report).
	 * 
	 * @see #PROPERTY_SLIDE_MASTER_REPORT
	 */
	@ExporterProperty(
		value=PROPERTY_SLIDE_MASTER_REPORT, 
		intDefault=1
		)
	public Integer getSlideMasterReport();

	/**
	 * Specifies the page from which the content of the slide master should be extracted. The default value is 1 (first page).
	 * 
	 * @see #PROPERTY_SLIDE_MASTER_PAGE
	 */
	@ExporterProperty(
		value=PROPERTY_SLIDE_MASTER_PAGE, 
		intDefault=1
		)
	public Integer getSlideMasterPage();
	
	/**
	 * Indicates whether the true type fonts used in the report should be embedded into the generated PPTX document. 
	 * @see #PROPERTY_EMBED_FONTS
	 */
	@ExporterProperty(
			value=PROPERTY_EMBED_FONTS,
			booleanDefault=false
			)
	public Boolean isEmbedFonts();

	/**
	 * The encryption password needed to open the document, if it is encrypted.
	 * @see #PROPERTY_ENCRYPTION_PASSWORD
	 */
	@ExporterProperty(PROPERTY_ENCRYPTION_PASSWORD)
	public String getEncryptionPassword();
}

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
package net.sf.jasperreports.pdf;

import com.lowagie.text.pdf.PdfWriter;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.export.ExporterConfiguration;
import net.sf.jasperreports.export.annotations.ExporterProperty;
import net.sf.jasperreports.pdf.type.PdfPrintScalingEnum;
import net.sf.jasperreports.pdf.type.PdfVersionEnum;
import net.sf.jasperreports.pdf.type.PdfaConformanceEnum;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * Interface containing settings used by the PDF exporter.
 *
 * @see JRPdfExporter
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface PdfExporterConfiguration extends ExporterConfiguration
{
	/**
	 * Integer property that contains all permissions for the generated PDF document
	 */
	//TODO lucian
	public static final Integer ALL_PERMISSIONS = 
			PdfWriter.ALLOW_ASSEMBLY 
			| PdfWriter.ALLOW_COPY
			| PdfWriter.ALLOW_DEGRADED_PRINTING
			| PdfWriter.ALLOW_FILL_IN
			| PdfWriter.ALLOW_MODIFY_ANNOTATIONS
			| PdfWriter.ALLOW_MODIFY_CONTENTS
			| PdfWriter.ALLOW_PRINTING
			| PdfWriter.ALLOW_SCREENREADERS;
	
	/**
	 * Property whose value is used as default state of the {@link #isCreatingBatchModeBookmarks()} export configuration flag.
	 * <p/>
	 * This property is by default not set (<code>false</code>).
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_2_0_1,
			valueType = Boolean.class
			)
	public static final String PROPERTY_CREATE_BATCH_MODE_BOOKMARKS = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.create.batch.mode.bookmarks";
	
	/**
	 * Property whose value is used as default state of the {@link #isCompressed()} export configuration flag.
	 * <p/>
	 * This property is by default not set (<code>false</code>).
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_2_0_1,
			valueType = Boolean.class
			)
	public static final String PROPERTY_COMPRESSED = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.compressed";

	/**
	 * Property whose value is used as default state of the {@link #isEncrypted()} export configuration flag.
	 * <p/>
	 * This property is by default not set (<code>false</code>).
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_2_0_1,
			valueType = Boolean.class
			)
	public static final String PROPERTY_ENCRYPTED = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.encrypted";

	/**
	 * Property whose value is used as default state of the {@link #is128BitKey()} export configuration flag.
	 * <p/>
	 * This property is by default not set (<code>false</code>).
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_2_0_1,
			valueType = Boolean.class
			)
	public static final String PROPERTY_128_BIT_KEY = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.128.bit.key";

	/**
	 * Property whose value is used as default for the {@link #getUserPassword()} export configuration setting.
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_2_0_1
			)
	public static final String PROPERTY_USER_PASSWORD = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.user.password";

	/**
	 * Property whose value is used as default for the {@link #getOwnerPassword()} export configuration setting.
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_2_0_1
			)
	public static final String PROPERTY_OWNER_PASSWORD = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.owner.password";
	
	/**
	 * Property whose value is used as default for the {@link #getAllowedPermissions()} export configuration setting.
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_5_6_0
			)
	public static final String PROPERTY_PERMISSIONS_ALLOWED = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.permissions.allowed";
	
	/**
	 * Property whose value is used as default for the {@link #getDeniedPermissions()} export configuration setting.
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_5_6_0
			)
	public static final String PROPERTY_PERMISSIONS_DENIED = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.permissions.denied";

	/**
	 * Property whose value is used as default for the {@link #getPdfVersion()} export configuration setting.
	 * Possible values of the this property are 2, 3, 4, 5, 6 and 7.
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_2_0_1
			)
	public static final String PROPERTY_PDF_VERSION = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.version";

	/**
	 * Property whose value is used as default for the {@link #getPdfJavaScript()} export configuration setting.
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_2_0_1
			)
	public static final String PROPERTY_PDF_JAVASCRIPT = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.javascript";

	/**
	 * Property whose value is used as default state of the {@link #getPrintScaling()} export configuration setting.
	 * <p/>
	 * By default, this property is set to {@link PdfPrintScalingEnum#DEFAULT}.
	 *
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.DEFAULT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_3_6_1,
			valueType = PdfPrintScalingEnum.class
			)
	public static final String PROPERTY_PRINT_SCALING = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.print.scaling";

	/**
	 * Property whose value is used as default for the {@link #isTagged()} export configuration flag.
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_3_1_2,
			valueType = Boolean.class
			)
	public static final String PROPERTY_TAGGED = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.tagged";

	/**
	 * Property whose value is used as default for the {@link #getTagLanguage()} export configuration setting.
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_1_2
			)
	public static final String PROPERTY_TAG_LANGUAGE = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.tag.language";

	/**
	 * Property whose value is used as default for the {@link #getPdfaConformance()} export configuration setting.
	 * <p/>
	 * By default, this property is set to {@link PdfaConformanceEnum#NONE}.
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_3_1_2
			)
	public static final String PROPERTY_PDFA_CONFORMANCE = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdfa.conformance";

	/**
	 * Property whose value is used as default for the {@link #getIccProfilePath()} export configuration setting.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_3_1_2
			)
	public static final String PROPERTY_PDFA_ICC_PROFILE_PATH = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdfa.icc.profile.path";

	/**
	 * Property whose value is used as default for the {@link #isEmbedIccProfile()} export configuration setting.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_20_5,
			valueType = Boolean.class
			)
	public static final String PROPERTY_EMBED_ICC_PROFILE = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdfa.embed.icc.profile";

	/**
	 * Property whose value is used as default for the {@link #isUseCMYKColors()} export configuration setting.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_20_5,
			valueType = Boolean.class
			)
	public static final String PROPERTY_USE_CMYK_COLORS = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.use.cmyk.colors";

	/**
	 * Property whose value is used as default for the {@link #getMetadataTitle()} export configuration setting.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_5_5_2
			)
	public static final String PROPERTY_METADATA_TITLE = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.metadata.title";

	/**
	 * Property whose value is used as default for the {@link #getMetadataAuthor()} export configuration setting.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_5_5_2
			)
	public static final String PROPERTY_METADATA_AUTHOR = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.metadata.author";

	/**
	 * Property whose value is used as default for the {@link #getMetadataSubject()} export configuration setting.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_5_5_2
			)
	public static final String PROPERTY_METADATA_SUBJECT = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.metadata.subject";

	/**
	 * Property whose value is used as default for the {@link #getMetadataKeywords()} export configuration setting.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_5_5_2
			)
	public static final String PROPERTY_METADATA_KEYWORDS = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.metadata.keywords";

	/**
	 * Property whose value is used as default for the {@link #getMetadataCreator()} export configuration setting.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_5_5_2
			)
	public static final String PROPERTY_METADATA_CREATOR = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.metadata.creator";
	
	/**
	 * Property whose value is used as default for the {@link #getMetadataProducer()} export configuration setting.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_21_2
			)
	public static final String PROPERTY_METADATA_PRODUCER = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.metadata.producer";
	
	/**
	 * Property whose value is used as default for the {@link #isDisplayMetadataTitle()} export configuration setting.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_0_0,
			valueType = Boolean.class
			)
	public static final String PROPERTY_DISPLAY_METADATA_TITLE = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.display.metadata.title";

	/**
	 * Property that determines whether justified text alignment can modify letter spacing in words.
	 * 
	 * <p>
	 * By default the property is set to <code>false</code>.
	 * </p>
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_2_0,
			valueType = Boolean.class
			)
	public static final String PROPERTY_JUSTIFIED_LETTER_SPACING = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.justified.letter.spacing";
	
	/**
	 * Returns a boolean value specifying  whether the PDF document should contain an outline section.
	 * @see #PROPERTY_CREATE_BATCH_MODE_BOOKMARKS
	 */
	@ExporterProperty(
		value=PROPERTY_CREATE_BATCH_MODE_BOOKMARKS, 
		booleanDefault=false
		)
	public Boolean isCreatingBatchModeBookmarks();
	
	/**
	 * Returns a boolean value specifying whether the PDF document should be compressed.
	 * @see #PROPERTY_COMPRESSED
	 */
	@ExporterProperty(
		value=PROPERTY_COMPRESSED, 
		booleanDefault=false
		)
	public Boolean isCompressed();
	
	/**
	 * Returns a boolean value specifying whether the final PDF document should be encrypted.
	 * <p/>
	 * When set to <code>Boolean.TRUE</code>, this parameter instructs the exporter to 
	 * encrypt the resulting PDF document. By default PDF files are not encrypted.
	 * @see PdfExporterConfiguration#PROPERTY_ENCRYPTED
	 */
	@ExporterProperty(
		value=PROPERTY_ENCRYPTED, 
		booleanDefault=false
		)
	public Boolean isEncrypted();
	
	/**
	 * Returns a boolean value specifying whether the encryption key is 128 bits.
	 * <p/>
	 * The PDF exporter can encrypt the files using either a 40-bit key or a
	 * 128-bit key. By default, it uses a 40-bit key, but if you set this flag to 
	 * <code>Boolean.TRUE</code>, it
	 * can be configured to use a 128-bit key for stronger encryption.
	 * @see #PROPERTY_128_BIT_KEY
	 */
	@ExporterProperty(
		value=PROPERTY_128_BIT_KEY, 
		booleanDefault=false
		)
	public Boolean is128BitKey();
	
	/**
	 * The user password needed to open the document, if it is encrypted.
	 * @see #PROPERTY_USER_PASSWORD
	 */
	@ExporterProperty(PROPERTY_USER_PASSWORD)
	public String getUserPassword();
	
	/**
	 * The password belonging to the owner of the document, if it is encrypted. If the password is null, it will be replaced
	 * by a random string, so that access is denied to all would-be owners.
	 * @see #PROPERTY_OWNER_PASSWORD
	 */
	@ExporterProperty(PROPERTY_OWNER_PASSWORD)
	public String getOwnerPassword();
	
	/**
	 * Returns a <tt>Character</tt> instance representing the version of the generated PDF. This class contains predefined constants
	 * that can be passed as parameters directly.
	 * @see #PROPERTY_PDF_VERSION
	 */
	@ExporterProperty(PROPERTY_PDF_VERSION)
	public PdfVersionEnum getPdfVersion();
	
	/**
	 * The user defined JavaScript piece of code to be inserted in the generated PDF document.
	 * @see #PROPERTY_PDF_JAVASCRIPT
	 */
	@ExporterProperty(PROPERTY_PDF_JAVASCRIPT)
	public String getPdfJavaScript();
	
	/**
	 * Setting specifying the print scaling preference in the PDF print dialog.
	 */
	@ExporterProperty(PROPERTY_PRINT_SCALING)
	public PdfPrintScalingEnum getPrintScaling();
	
	/**
	 * Specifies whether the exporter should put structure tags in the generated PDF.
	 * @see #PROPERTY_TAGGED
	 */
	@ExporterProperty(
		value=PROPERTY_TAGGED, 
		booleanDefault=false
		)
	public Boolean isTagged();
	
	/**
	 * Specifies the language that the exporter should put in the language tag of the generated PDF.
	 * @see #PROPERTY_TAG_LANGUAGE
	 */
	@ExporterProperty(PROPERTY_TAG_LANGUAGE)
	public String getTagLanguage();
	
	/**
	 * The Conformance level of the PDF/A document.
	 * @see #PROPERTY_PDFA_CONFORMANCE
	 */
	@ExporterProperty(PROPERTY_PDFA_CONFORMANCE)
	public PdfaConformanceEnum getPdfaConformance();
	
	/**
	 * The path to the ICC profile file needed for CMYK color conversion and/or the PDF/A compliance.
	 * @see #PROPERTY_PDFA_ICC_PROFILE_PATH
	 */
	@ExporterProperty(PROPERTY_PDFA_ICC_PROFILE_PATH)
	public String getIccProfilePath();
	
	/**
	 * Specifies whether the ICC profile, which in this case must be provided by {@link #getIccProfilePath()}, 
	 * is embedded into the PDF. PDFA compliance requires embedding the ICC profile, which inhibits this setting. 
	 * @see #PROPERTY_USE_CMYK_COLORS
	 */
	@ExporterProperty(
		value=PROPERTY_EMBED_ICC_PROFILE,
		booleanDefault=false
		)
	public Boolean isEmbedIccProfile();
	
	/**
	 * Specifies whether the ICC profile, which in this case must be provided by {@link #getIccProfilePath()}, 
	 * is used to convert colors from RGB to CMYK color space.
	 * @see #PROPERTY_USE_CMYK_COLORS
	 */
	@ExporterProperty(
		value=PROPERTY_USE_CMYK_COLORS,
		booleanDefault=false
		)
	public Boolean isUseCMYKColors();
	
	/**
	 * An integer value representing the PDF permissions for the generated document. The open permissions for the document
	 * can be ALLOW_PRINTING, ALLOW_MODIFY_CONTENTS, ALLOW_COPY, ALLOW_MODIFY_ANNOTATIONS, ALLOW_FILL_IN, ALLOW_SCREENREADERS,
	 * ALLOW_ASSEMBLY and ALLOW_DEGRADED_PRINTING (these can all be found in the PdfWriter class of OpenPDF library). The
	 * permissions can be combined by applying bitwise OR to them.
	 */
	public Integer getPermissions();
	
	/**
	 * An exporter hint property representing the allowed permissions for the generated PDF document. Allowed permissions for the document
	 * can be PRINTING, MODIFY_CONTENTS, COPY, MODIFY_ANNOTATIONS, FILL_IN,SCREENREADERS,
	 * ASSEMBLY, DEGRADED_PRINTING and ALL. Different permissions are separated by a pipe (|) character.
	 */
	@ExporterProperty(PROPERTY_PERMISSIONS_ALLOWED)
	public String getAllowedPermissions();
	
	/**
	 * An exporter hint property representing the denied permissions for the generated PDF document. Denied permissions for the document
	 * can be PRINTING, MODIFY_CONTENTS, COPY, MODIFY_ANNOTATIONS, FILL_IN,SCREENREADERS,
	 * ASSEMBLY, DEGRADED_PRINTING and ALL. Different permissions are separated by a pipe (|) character.
	 */
	@ExporterProperty(PROPERTY_PERMISSIONS_DENIED)
	public String getDeniedPermissions();

	/**
	 * The Title of the PDF document.
	 */
	@ExporterProperty(PROPERTY_METADATA_TITLE)
	public String getMetadataTitle();

	/**
	 * The Author of the PDF document.
	 */
	@ExporterProperty(PROPERTY_METADATA_AUTHOR)
	public String getMetadataAuthor();

	/**
	 * The Subject of the PDF document.
	 */
	@ExporterProperty(PROPERTY_METADATA_SUBJECT)
	public String getMetadataSubject();

	/**
	 * The Keywords of the PDF document, as comma-separated String.
	 */
	@ExporterProperty(PROPERTY_METADATA_KEYWORDS)
	public String getMetadataKeywords();

	/**
	 * The Creator or Application for the PDF document. Defaults to "JasperReports Library version x.x.x".
	 */
	@ExporterProperty(PROPERTY_METADATA_CREATOR)
	public String getMetadataCreator();
	
	/**
	 * The Producer of the PDF document.
	 */
	@ExporterProperty(PROPERTY_METADATA_PRODUCER)
	public String getMetadataProducer();
	
	/**
	 * Specifies whether the document title should be displayed in the title bar instead of the file name.
	 * @see #PROPERTY_DISPLAY_METADATA_TITLE
	 */
	@ExporterProperty(
		value=PROPERTY_DISPLAY_METADATA_TITLE, 
		booleanDefault=false
		)
	public Boolean isDisplayMetadataTitle();
}

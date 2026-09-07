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

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.export.ReportExportConfiguration;
import net.sf.jasperreports.export.annotations.ExporterProperty;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * Interface containing settings used by the PDF exporter.
 *
 * @see JRPdfExporter
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface PdfReportConfiguration extends ReportExportConfiguration
{
	/**
	 * Property that provides a default value for the {@link #isForceSvgShapes()} PDF exporter configuration flag.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_TRUE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_2_0_1,
			valueType = Boolean.class
			)
	public static final String PROPERTY_FORCE_SVG_SHAPES = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.force.svg.shapes";

	/**
	 * Property that provides a default for the {@link #isBookmarksEnabled()} export configuration flag.
	 * 
	 * <p>
	 * The property can be set globally and at report level.
	 * By default, the property is set to <code>true</code>.
	 * </p>
	 * 
	 * @see net.sf.jasperreports.engine.JRAnchor#getBookmarkLevel()
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_TRUE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_20_6,
			valueType = Boolean.class
			)
	public static final String PROPERTY_BOOKMARKS_ENABLED = 
		JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.bookmarks.enabled";

	/**
	 * Property that provides a default for the {@link #isCollapseMissingBookmarkLevels()} export configuration flag.
	 * 
	 * <p>
	 * The property can be set globally and at report level.
	 * By default, the property is set to <code>false</code>.
	 * </p>
	 * 
	 * @see net.sf.jasperreports.engine.JRAnchor#getBookmarkLevel()
	 * @since 3.7.3
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_3_7_3,
			valueType = Boolean.class
			)
	public static final String PROPERTY_COLLAPSE_MISSING_BOOKMARK_LEVELS = 
		JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.collapse.missing.bookmark.levels";

	/**
	 * Property which specifies a default for the {@link #isSizePageToContent()} export configuration flag.
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_4_7_0,
			valueType = Boolean.class
			)
	public static final String PROPERTY_SIZE_PAGE_TO_CONTENT = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.size.page.to.content";

	/**
	 * Property that provides a default for the {@link #isIgnoreHyperlink()} export configuration flag.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.HYPERLINK},
			sinceVersion = PropertyConstants.VERSION_5_1_2,
			valueType = Boolean.class
			)
	public static final String PROPERTY_IGNORE_HYPERLINK = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + JRPrintHyperlink.PROPERTY_IGNORE_HYPERLINK_SUFFIX;

	/**
	 * Property that provides a default value for the {@link #isForceLineBreakPolicy()} exporter configuration flag.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_2_0_1,
			valueType = Boolean.class
			)
	public static final String PROPERTY_FORCE_LINEBREAK_POLICY = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.force.linebreak.policy";

	/**
	 * Property that provides a default value for the {@link #getOddPageOffsetX()} export configuration setting.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = "0",
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_0_4,
			valueType = Integer.class
			)
	public static final String PROPERTY_ODD_PAGE_OFFSET_X = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.odd.page.offset.x";

	/**
	 * Property that provides a default value for the {@link #getOddPageOffsetY()} export configuration setting.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = "0",
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_0_4,
			valueType = Integer.class
			)
	public static final String PROPERTY_ODD_PAGE_OFFSET_Y = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.odd.page.offset.y";

	/**
	 * Property that provides a default value for the {@link #getEvenPageOffsetX()} export configuration setting.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = "0",
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_0_4,
			valueType = Integer.class
			)
	public static final String PROPERTY_EVEN_PAGE_OFFSET_X = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.even.page.offset.x";

	/**
	 * Property that provides a default value for the {@link #getEvenPageOffsetY()} export configuration setting.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = "0",
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_0_4,
			valueType = Integer.class
			)
	public static final String PROPERTY_EVEN_PAGE_OFFSET_Y = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.even.page.offset.y";

	/**
	 * Property prefix for specifying the Unicode blocks for which the glyph rendering mode should be used
	 * during PDF export. The glyph rendering mode writes text as {@link java.awt.font.GlyphVector GlyphVector}
	 * directly to the PDF content, which allows correct rendering of complex scripts such as Indic languages
	 * (Devanagari, Bengali, Telugu, Tamil, etc.) that require advanced text shaping.
	 *
	 * <p>
	 * Properties with this prefix should have an arbitrary suffix as name and a comma-separated list of
	 * {@link java.lang.Character.UnicodeBlock} names as value. If the text of a report element contains characters
	 * belonging to any of the specified Unicode blocks, the glyph renderer will be used for the entire text element.
	 * </p>
	 *
	 * @see net.sf.jasperreports.pdf.PdfGlyphRenderer
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_2_0,
			name = "net.sf.jasperreports.export.pdf.glyph.renderer.blocks.{arbitrary_name}"
			)
	public static final String PROPERTY_PREFIX_GLYPH_RENDERER_BLOCKS =
			JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.glyph.renderer.blocks.";

	/**
	 * Property that specifies whether the glyph renderer should add actual text to the PDF content
	 * when rendering text via {@link java.awt.font.GlyphVector GlyphVector}. Adding actual text enables
	 * text selection, copy-paste, and searchability in the resulting PDF document for text that is rendered
	 * using the glyph rendering mode.
	 *
	 * <p>
	 * When this property is set to <code>true</code>, the generated PDF will require at least PDF version 1.5.
	 * By default, this property is set to <code>true</code>.
	 * </p>
	 *
	 * @see #PROPERTY_PREFIX_GLYPH_RENDERER_BLOCKS
	 * @see net.sf.jasperreports.pdf.PdfGlyphRenderer
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_TRUE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_2_0,
			valueType = Boolean.class
			)
	public static final String PROPERTY_GLYPH_RENDERER_ADD_ACTUAL_TEXT =
			JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.glyph.renderer.add.actual.text";
	
	/**
	 * Flag to force the rendering of SVG images using shapes, on the PDF Graphics2D context.
	 *
	 * This allows rendering fonts as shapes and avoid font mapping issues that might cause Unicode
	 * text not to show up properly, but has the disadvantage of producing larger PDF files.
	 * By default, the flag is set to true, mainly due to backward compatibility reasons.
	 *
	 * To reduce PDF file size for documents containing SVG images such as charts, the flag should be
	 * turned to false, and the PDF exporter font mappings should be correctly configured using the
	 * font extension support, to ensure proper rendering of text in the SVG.
	 *
	 * This flag can be set system-wide using the {@link #PROPERTY_FORCE_SVG_SHAPES PROPERTY_FORCE_SVG_SHAPES} 
	 * property.
	 *
	 * @see #PROPERTY_FORCE_SVG_SHAPES
	 */
	@ExporterProperty(
		value=PROPERTY_FORCE_SVG_SHAPES, 
		booleanDefault=false
		)
	public Boolean isForceSvgShapes();
	
	/**
	 * Flag that determines if the bookmarks are to be created or not in the resulting PDF document.
	 * 
	 * </p>
	 * @see #PROPERTY_BOOKMARKS_ENABLED
	 */
	@ExporterProperty(
		value=PROPERTY_BOOKMARKS_ENABLED,
		booleanDefault=true
		)
	public Boolean isBookmarksEnabled();
	
	/**
	 * Flag that determines if missing bookmark levels are collapsed, or if 
	 * empty bookmarks are created for the missing levels.
	 * 
	 * <p>
	 * The flag dictates what happens when a filled report contains an anchor
	 * having a bookmark level that is not the immediate successor of its parent
	 * (e.g. an anchor with bookmark level 3 follows immediately after a bookmark
	 * of level 1).
	 * If the flag is not set, an empty bookmark is created for the missing
	 * level(s) in order to preserve the original level of the bookmark.
	 * When the property is set, the level of the bookmark will be collapsed and
	 * the bookmark will be created as a direct descendant of its nearest parent.
	 * </p>
	 * @see #PROPERTY_COLLAPSE_MISSING_BOOKMARK_LEVELS
	 */
	@ExporterProperty(
		value=PROPERTY_COLLAPSE_MISSING_BOOKMARK_LEVELS,
		booleanDefault=false
		)
	public Boolean isCollapseMissingBookmarkLevels();
	
	/**
	 * Flag which specifies if the size of each page should be increased to accommodate its content.
	 * @see #PROPERTY_SIZE_PAGE_TO_CONTENT
	 */
	@ExporterProperty(
		value=PROPERTY_SIZE_PAGE_TO_CONTENT, 
		booleanDefault=false
		)
	public Boolean isSizePageToContent();
	
	/**
	 * @see #PROPERTY_IGNORE_HYPERLINK
	 */
	@ExporterProperty(
		value=PROPERTY_IGNORE_HYPERLINK, 
		booleanDefault=false
		)
	public Boolean isIgnoreHyperlink();
	
	/**
	 * Flag that decides whether the PDF exporter should use a {@link com.lowagie.text.SplitCharacter SplitCharacter}
	 * implementation which ensures that report texts are broken into lines by OpenPDF in the same manner as done by the
	 * fill process.
	 * <p>
	 * The default line-breaking logic differs from AWT (which is used during the report fill) to OpenPDF (used by the PDF
	 * exporter).  By setting this flag, the logic used by AWT is imposed to OpenPDF.  The drawback is that the PDF export
	 * performance would drop.  Because of this, the flag is not set by default.
	 * <p>
	 * This flag can be set system-wide using the
	 * {@link #PROPERTY_FORCE_LINEBREAK_POLICY PROPERTY_FORCE_LINEBREAK_POLICY} property.
	 *
	 * @see #PROPERTY_FORCE_LINEBREAK_POLICY
	 * @see net.sf.jasperreports.openpdf.producer.BreakIteratorSplitCharacter
	 */
	@ExporterProperty(
		value=PROPERTY_FORCE_LINEBREAK_POLICY,
		booleanDefault=false
		)
	public Boolean isForceLineBreakPolicy();

	/**
	 * Specifies the X offset for moving elements in odd page number pages, to simulate gutter margins.
	 */
	@ExporterProperty(
		value=PROPERTY_ODD_PAGE_OFFSET_X,
		intDefault=0
		)
	public Integer getOddPageOffsetX();
	
	/**
	 * Specifies the Y offset for moving elements in odd page number pages, to simulate gutter margins.
	 */
	@ExporterProperty(
		value=PROPERTY_ODD_PAGE_OFFSET_Y,
		intDefault=0
		)
	public Integer getOddPageOffsetY();

	/**
	 * Specifies the X offset for moving elements in even page number pages, to simulate gutter margins.
	 */
	@ExporterProperty(
		value=PROPERTY_EVEN_PAGE_OFFSET_X,
		intDefault=0
		)
	public Integer getEvenPageOffsetX();
	
	/**
	 * Specifies the Y offset for moving elements in even page number pages, to simulate gutter margins.
	 */
	@ExporterProperty(
		value=PROPERTY_EVEN_PAGE_OFFSET_Y,
		intDefault=0
		)
	public Integer getEvenPageOffsetY();
}

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

import java.awt.Color;
import java.util.Map;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.components.BarcodeConstants;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRPropertiesUtil.PropertySuffix;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporter;
import net.sf.jasperreports.engine.export.type.ImageAnchorTypeEnum;
import net.sf.jasperreports.engine.type.RunDirectionEnum;
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
public interface XlsReportConfiguration extends ReportExportConfiguration
{
	/**
	 * Property whose value is used as default state of the {@link #isOnePagePerSheet()} export configuration flag.
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
	public static final String PROPERTY_ONE_PAGE_PER_SHEET = JRPropertiesUtil.PROPERTY_PREFIX + "export.xls.one.page.per.sheet";

	/**
	 * Property whose value is used as default state of the {@link #isRemoveEmptySpaceBetweenRows()} export configuration flag.
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
	public static final String PROPERTY_REMOVE_EMPTY_SPACE_BETWEEN_ROWS = JRPropertiesUtil.PROPERTY_PREFIX + "export.xls.remove.empty.space.between.rows";

	/**
	 * Property whose value is used as default state of the {@link #isRemoveEmptySpaceBetweenColumns()} export configuration flag.
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
	public static final String PROPERTY_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS = JRPropertiesUtil.PROPERTY_PREFIX + "export.xls.remove.empty.space.between.columns";

	/**
	 * Property whose value is used as default state of the {@link #isWhitePageBackground()} export configuration flag.
	 * <p/>
	 * This property is set by default (<code>true</code>).
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_TRUE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_2_0_1,
			valueType = Boolean.class
			)
	public static final String PROPERTY_WHITE_PAGE_BACKGROUND = JRPropertiesUtil.PROPERTY_PREFIX + "export.xls.white.page.background";

	/**
	 * This property serves as default for the {@link #isWrapText()} export configuration flag.
	 * <p>
	 * The property itself defaults to <code>true</code>.
	 * </p>
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_TRUE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_7_3,
			valueType = Boolean.class
			)
	public static final String PROPERTY_WRAP_TEXT = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "wrap.text";

	/**
	 * This property serves as default for the {@link #isCellLocked()} export configuration flag.
	 * </p>
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_TRUE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_7_5,
			valueType = Boolean.class
			)
	public static final String PROPERTY_CELL_LOCKED = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "cell.locked";

	/**
	 * This property serves as default for the {@link #isCellHidden()} export configuration flag.
	 * </p>
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_7_5,
			valueType = Boolean.class
			)
	public static final String PROPERTY_CELL_HIDDEN = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "cell.hidden";

	/**
	 * Property whose value is used as default state of the {@link #isDetectCellType()} export flag.
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
	public static final String PROPERTY_DETECT_CELL_TYPE = JRPropertiesUtil.PROPERTY_PREFIX + "export.xls.detect.cell.type";

	/**
	 * Property whose value is used as default state of the {@link #isFontSizeFixEnabled()} export configuration flag.
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
	public static final String PROPERTY_FONT_SIZE_FIX_ENABLED = JRPropertiesUtil.PROPERTY_PREFIX + "export.xls.font.size.fix.enabled";

	/**
	 * Property whose value is used as default state of the {@link #isImageBorderFixEnabled()} export configuration flag.
	 * <p/>
	 * This property is by default not set (<code>false</code>).
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_3_0_0,
			valueType = Boolean.class
			)
	public static final String PROPERTY_IMAGE_BORDER_FIX_ENABLED = JRPropertiesUtil.PROPERTY_PREFIX + "export.xls.image.border.fix.enabled";

	/**
	 * Property whose value is used as default state of the {@link #isIgnoreGraphics()} export configuration flag.
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
	public static final String PROPERTY_IGNORE_GRAPHICS = JRPropertiesUtil.PROPERTY_PREFIX + "export.xls.ignore.graphics";

	/**
	 * Property whose value is used as default state of the {@link #isCollapseRowSpan()} export configuration flag.
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
	public static final String PROPERTY_COLLAPSE_ROW_SPAN = JRPropertiesUtil.PROPERTY_PREFIX + "export.xls.collapse.row.span";

	/**
	 * Property whose value is used as default state of the {@link #isIgnoreCellBorder()} export configuration flag.
	 * <p/>
	 * This property is by default not set (<code>false</code>).
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_2_0_1,
			valueType = Boolean.class
			)
	public static final String PROPERTY_IGNORE_CELL_BORDER = JRPropertiesUtil.PROPERTY_PREFIX + "export.xls.ignore.cell.border";

	/**
	 * Property whose value is used as default state of the {@link #isIgnoreCellBackground()} export flag.
	 * <p/>
	 * This property is by default not set (<code>false</code>).
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_6_2,
			valueType = Boolean.class
			)
	public static final String PROPERTY_IGNORE_CELL_BACKGROUND = JRPropertiesUtil.PROPERTY_PREFIX + "export.xls.ignore.cell.background";

	/**
	 * Property whose value is used as default of the {@link #getMaxRowsPerSheet()} export configuration setting.
	 * <p/>
	 * This property is by default to zero.
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = "0",
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_2_0_1,
			valueType = Integer.class
			)
	public static final String PROPERTY_MAXIMUM_ROWS_PER_SHEET = JRPropertiesUtil.PROPERTY_PREFIX + "export.xls.max.rows.per.sheet";

	/**
	 * This property provides a default value for the {@link #getSheetHeaderLeft()} export configuration setting.
	 * </p>
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_7_5
			)
	public static final String PROPERTY_SHEET_HEADER_LEFT = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "sheet.header.left";

	/**
	 * This property provides a default value for the {@link #getSheetHeaderCenter()} export configuration setting.
	 * </p>
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_7_5
			)
	public static final String PROPERTY_SHEET_HEADER_CENTER = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "sheet.header.center";

	/**
	 * This property provides a default value for the {@link #getSheetHeaderRight()} export configuration setting.
	 * </p>
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_7_5
			)
	public static final String PROPERTY_SHEET_HEADER_RIGHT = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "sheet.header.right";

	/**
	 * This property provides a default value for the {@link #getSheetFooterLeft()} export configuration setting.
	 * </p>
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_7_5
			)
	public static final String PROPERTY_SHEET_FOOTER_LEFT = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "sheet.footer.left";

	/**
	 * This property provides a default value for the {@link #getSheetFooterCenter()} export configuration setting.
	 * </p>
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_7_5
			)
	public static final String PROPERTY_SHEET_FOOTER_CENTER = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "sheet.footer.center";

	/**
	 * This property provides a default value for the {@link #getSheetFooterRight()} export configuration setting.
	 * </p>
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_7_5
			)
	public static final String PROPERTY_SHEET_FOOTER_RIGHT = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "sheet.footer.right";

	/**
	 * Property whose value is used as default value of the {@link #getPassword()} export configuration setting.
	 * <p/>
	 * This property is by default not set (<code>null</code>).
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_3_0_1
			)
	public static final String PROPERTY_PASSWORD = JRPropertiesUtil.PROPERTY_PREFIX + "export.xls.password";

	/**
	 * Property specifying the value for the #get the sheet name for the single sheet created when the {@link #isOnePagePerSheet()} flag is set to false.
	 * Without this configuration being set, the single sheet name would default to the report name value and this setting is meant 
	 * to allow override this default behavior.
	 * Note that even with the {@link #isOnePagePerSheet()} flag set to false, the workbook could still have more than one sheet
	 * because of sheet break properties attached to elements, in which case the subsequent sheet names would have incremented numbers 
	 * appended to the specified sheet name.
	 * Also, the {@link #getSheetNames()} setting overrides this single sheet name setting.
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			name = "net.sf.jasperreports.export.xls.single.sheet.name",
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_21_5
			)
	public static final String PROPERTY_SINGLE_SHEET_NAME = JRPropertiesUtil.PROPERTY_PREFIX + "export.xls.single.sheet.name";

	/**
	 * Prefix used to identify properties holding sheet names. Such properties are useful when 
	 * defining sheet names in the JRXML template is required. The property values are collected in an 
	 * ordered list, therefore the order in which the properties appear in the report is important. 
	 * If set, the values are used 
	 * as defaults for the {@link #getSheetNames()} export configuration setting.
	 * <p/>
	 * A property starting with this prefix can hold one or several sheet names, separated by a slash character ("/").
	 * <p/>
	 * By default no sheet name properties are set.
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			name = "net.sf.jasperreports.export.xls.sheet.names.{arbitrary_name}",
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_3_5_2
			)
	public static final String PROPERTY_SHEET_NAMES_PREFIX = JRPropertiesUtil.PROPERTY_PREFIX + "export.xls.sheet.names.";
	
	/**
	 * Properties having <code>net.sf.jasperreports.export.xls.pattern.</code> prefix are used to store specific Excel 
	 * format patterns that correspond to their related Java format pattern. The <code>{arbitrary_pattern}</code> suffix represents 
	 * the Java pattern and the property value is the related Excel pattern. When these properties are defined in a JRXML report design, 
	 * special characters should be XML-escaped. When they are defined in a properties file, one should also escape special characters.  
	 * If some properties are defined in the report, they will override the context properties.
	 */
	@Property(
			name = "net.sf.jasperreports.export.xls.pattern.{arbitrary_pattern}",
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_5_0
			)
	public static final String FORMAT_PATTERN_PREFIX = JRPropertiesUtil.PROPERTY_PREFIX + "export.xls.pattern.";
	
	/**
	 * Property that provides a default value for the {@link #isIgnoreHyperlink()} export configuration flag.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.HYPERLINK},
			sinceVersion = PropertyConstants.VERSION_5_1_2,
			valueType = Boolean.class
			)
	public static final String PROPERTY_IGNORE_HYPERLINK = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + JRPrintHyperlink.PROPERTY_IGNORE_HYPERLINK_SUFFIX;

	/**
	 * Property that provides a default for the {@link #isIgnoreAnchors()} export configuration flag.
	 * The default value is <code>false</code>.
	 * <p>
	 * Property scope:
	 * <ul>
	 * <li><code>Global</code></li>
	 * <li><code>Report</code></li>
	 * </ul>
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_4_6_0,
			valueType = Boolean.class
			)
	public static final String PROPERTY_IGNORE_ANCHORS = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "ignore.anchors";

	/**
	 * This property provides a default for the {@link #getFitWidth()} exporter configuration setting.
	 * <p>
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_3_7_5,
			valueType = Integer.class
			)
	public static final String PROPERTY_FIT_WIDTH = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "fit.width";

	/**
	 * This property provides a default for the {@link #getFitHeight()} export configuration setting.
	 * </p>
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_3_7_5,
			valueType = Integer.class
			)
	public static final String PROPERTY_FIT_HEIGHT = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "fit.height";

	/**
	 * This property provides a default value for the {@link #getPageScale()} export configuration setting.
	 * <br/>
	 * Property scope:
	 * <ul>
	 * <li><code>Global</code></li>
	 * <li><code>Report</code></li>
	 * <li><code>Element</code> - this setting can be used to set the page scale per sheet</li>
	 * </ul>
	 * Global settings are overridden by report level settings; report level settings are overridden by element (sheet) level settings.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_6_0,
			valueType = Integer.class
			)
	public static final String PROPERTY_PAGE_SCALE = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "page.scale";

	/**
	 * This property provides a default for the {@link #getSheetDirection()} exporter configuration setting.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.LTR,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_3_7_5,
			valueType = RunDirectionEnum.class
			)
	public static final String PROPERTY_SHEET_DIRECTION = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "sheet.direction";

	/**
	 * This property provides a default for the {@link #getFreezeRow()} exporter configuration setting. 
	 * Allowed values are represented by positive integers in the 1..65536 range. Negative values are not considered. 
	 * The property should be used when all sheets in the document have the same freeze row index.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_4_1_1,
			valueType = Integer.class
			)
	public static final String PROPERTY_FREEZE_ROW = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "freeze.row";
	
	/**
	 * This property provides a default for the {@link #getFreezeColumn()} exporter configuration setting. 
	 * Allowed values are letters or letter combinations representing valid column names in Excel, such as A, B, AB, AC, etc.
	 * The property should be used when all document sheets have the same freeze column name.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_4_1_1
			)
	public static final String PROPERTY_FREEZE_COLUMN = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "freeze.column";

	/**
	 * Property used to provide a default value for the {@link #getColumnWidthRatio()} export configuration setting.
	 * The property can be set:
	 * <ul>
	 * <li>globally - then all the columns in all documents exported to the Excel output format will be adjusted with the same width ratio</li>
	 * <li>at report level - then all the columns in the document will be adjusted with the same width ratio</li>
	 * <li>at element level - then all the columns in the current sheet will be adjusted with the same width ratio</li>
	 * </ul> 
	 * If present, a {@link JRXlsAbstractExporter#PROPERTY_COLUMN_WIDTH PROPERTY_COLUMN_WIDTH} property will override the 
	 * {@link #PROPERTY_COLUMN_WIDTH_RATIO PROPERTY_COLUMN_WIDTH_RATIO} value for that column only.
	 * 
	 * @see JRXlsAbstractExporter#PROPERTY_COLUMN_WIDTH
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_1_3,
			valueType = Float.class
			)
	public static final String PROPERTY_COLUMN_WIDTH_RATIO = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "column.width.ratio";

	/**
	 * Property that provides a default for the {@link #isUseTimeZone()} export configuration flag.
	 * 
	 * The property can be set globally, at report level and at element level.
	 * The default value is <code>false</code>.
	 * 
	 * @since 4.5.0
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.TEXT_ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_5_0,
			valueType = Boolean.class
			)
	public static final String PROPERTY_USE_TIMEZONE = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "use.timezone";

	/**
	 * Property that specifies the default for the {@link #getFirstPageNumber()} exporter configuration setting.
	 * <br/>
	 * Property scope:
	 * <ul>
	 * <li><code>Global</code></li>
	 * <li><code>Report</code></li>
	 * <li><code>Element</code> - this setting can be used to set the first page number per sheet.</li>
	 * </ul>
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_6_0,
			valueType = Integer.class
			)
	public static final String PROPERTY_FIRST_PAGE_NUMBER = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "first.page.number";

	/**
	 * Property that provides a default for the {@link #isShowGridLines()} export configuration flag. 
	 * When set at report element level and multiple elements in a sheet provide this property, 
	 * the last read value will be considered. Default value is <code>true</code>.
	 * <br/>
	 * Property scope:
	 * <ul>
	 * <li><code>Global</code></li>
	 * <li><code>Report</code></li>
	 * <li><code>Element</code> - this setting can be used to set the property value per sheet.</li>
	 * </ul>
	 * Global settings are overridden by report level settings; report level settings are overridden by element (sheet) level settings.
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_TRUE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Boolean.class
			)
	public static final String PROPERTY_SHOW_GRIDLINES = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "show.gridlines";

	/**
	 * Property that provides a default value for the {@link #getImageAnchorType()} export parameter setting.
	 * Possible values are:
	 * <ul>
	 * <li><code>MoveSize</code> - images move and size with cells</li>
	 * <li><code>MoveNoSize</code> - images move but don't size with cells</li>
	 * <li><code>NoMoveNoSize</code> - images don't move or size with cells</li>
	 * </ul>
	 * Default value is <code>MoveNoSize</code>.
	 * <br/>
	 * Property scope:
	 * <ul>
	 * <li><code>Global</code></li>
	 * <li><code>Report</code></li>
	 * <li><code>Element</code></li>
	 * </ul>
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.MOVE_NO_SIZE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, 
					PropertyScope.IMAGE_ELEMENT, PropertyScope.CHART_ELEMENT, PropertyScope.COMPONENT},
			scopeQualifications = {BarcodeConstants.COMPONENT_DESIGNATION_BARCODE4J, BarcodeConstants.COMPONENT_DESIGNATION_QRCODE,
					BarcodeConstants.METADATA_KEY_QUALIFICATION_BARBECUE},
			sinceVersion = PropertyConstants.VERSION_5_0_4,
			valueType = ImageAnchorTypeEnum.class
			)
	public static final String PROPERTY_IMAGE_ANCHOR_TYPE = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "image.anchor.type";
	
	/**
	 * Flag property that provides a default value for the {@link #isAutoFitPageHeight()} export setting.
	 * If set to true, the exporter will automatically fit the print height of a sheet to the number of JasperPrint pages exported in that sheet
	 * <br/>
	 * Property scope:
	 * <ul>
	 * <li><code>Global</code></li>
	 * <li><code>Report</code></li>
	 * </ul>
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_0_2,
			valueType = Boolean.class
			)
	public static final String PROPERTY_AUTO_FIT_PAGE_HEIGHT = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "auto.fit.page.height";
	
	/**
	 * Flag property that provides a default value for the {@link #isForcePageBreaks()} export setting.
	 * If set to true, the exporter will automatically add a page break at the end of each page exported on the current sheet.
	 * <br/>
	 * Property scope:
	 * <ul>
	 * <li><code>Global</code></li>
	 * <li><code>Report</code></li>
	 * </ul>
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_0_2,
			valueType = Boolean.class
			)
	public static final String PROPERTY_FORCE_PAGE_BREAKS = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "force.page.breaks";

	/**
	 * This flag property serves as default for the {@link #isShrinkToFit()} export configuration flag. 
	 * <p/> 
	 * If set to true, this will automatically disable the wrap text setting for the cell (see {@link #isWrapText()}).
	 * <p/>
	 * Usually this property works in conjunction with {@link net.sf.jasperreports.engine.JRTextElement#PROPERTY_PRINT_KEEP_FULL_TEXT net.sf.jasperreports.print.keep.full.text}, 
	 * in order to preserve the entire text content at export time.
	 * <p/>
	 * The property itself defaults to <code>false</code>.
	 * <p/>
	 * Property scope:
	 * <ul>
	 * <li><code>Global</code></li>
	 * <li><code>Report</code></li>
	 * <li><code>Element</code></li>
	 * </ul>
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_0_2,
			valueType = Boolean.class
			)
	public static final String PROPERTY_SHRINK_TO_FIT = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "shrink.to.fit";

	/**
	 * Property whose value is used as default state of the {@link #isIgnoreTextFormatting()} export configuration flag. 
	 * If true, text elements will be exported without formatting features such as bold, underline, backcolor, text color, etc. 
	 * <p/>
	 * Property scope:
	 * <ul>
	 * <li><code>Global</code></li>
	 * <li><code>Report</code></li>
	 * <li><code>Element</code></li>
	 * </ul>
	 * This property is by default not set (<code>false</code>).
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.TEXT_ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_0_4,
			valueType = Boolean.class
			)
	public static final String PROPERTY_IGNORE_TEXT_FORMATTING = JRPropertiesUtil.PROPERTY_PREFIX + "export.xls.ignore.text.formatting";

	/**
	 * Property whose value is used as default state of the {@link #getSheetTabColor()} export configuration setting. 
	 * <p/>
	 * When set at report element level and multiple elements in a sheet provide this property, 
	 * the last read value will be considered. The property works in XLSX and ODS output formats. 
	 * It is not supported by the XLS output format.
	 * <p/>
	 * Property scope:
	 * <ul>
	 * <li><code>Global</code></li>
	 * <li><code>Report</code></li>
	 * <li><code>Element</code> - this setting can be used to set the property value per sheet.</li>
	 * </ul>
	 * Global settings are overridden by report level settings; report level settings are overridden by element (sheet) level settings.
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_2_0,
			valueType = Color.class
			)
	public static final String PROPERTY_SHEET_TAB_COLOR = JRPropertiesUtil.PROPERTY_PREFIX + "export.xls.sheet.tab.color";

		
	/**
	 * Property used to provide a default value for the {@link #getPrintPageTopMargin()} export configuration setting.
	 * The property can be set:
	 * Property scope:
	 * <ul>
	 * <li><code>Global</code></li>
	 * <li><code>Report</code></li>
	 * <li><code>Element</code> - this setting can be used to set the property value per sheet.</li>
	 * </ul>
	 * Global settings are overridden by report level settings; report level settings are overridden by element (sheet) level settings.
	 * 
	 * @see #PROPERTY_PRINT_PAGE_LEFT_MARGIN
	 * @see #PROPERTY_PRINT_PAGE_RIGHT_MARGIN
	 * @see #PROPERTY_PRINT_PAGE_BOTTOM_MARGIN
	 * @see #PROPERTY_PRINT_HEADER_MARGIN
	 * @see #PROPERTY_PRINT_FOOTER_MARGIN
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = "0",
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_4_3,
			valueType = Integer.class
			)
	public static final String PROPERTY_PRINT_PAGE_TOP_MARGIN = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "print.page.top.margin";
	
	/**
	 * Property used to provide a default value for the {@link #getPrintPageLeftMargin()} export configuration setting.
	 * The property can be set:
	 * Property scope:
	 * <ul>
	 * <li><code>Global</code></li>
	 * <li><code>Report</code></li>
	 * <li><code>Element</code> - this setting can be used to set the property value per sheet.</li>
	 * </ul>
	 * Global settings are overridden by report level settings; report level settings are overridden by element (sheet) level settings.
	 * 
	 * @see #PROPERTY_PRINT_PAGE_TOP_MARGIN
	 * @see #PROPERTY_PRINT_PAGE_RIGHT_MARGIN
	 * @see #PROPERTY_PRINT_PAGE_BOTTOM_MARGIN
	 * @see #PROPERTY_PRINT_HEADER_MARGIN
	 * @see #PROPERTY_PRINT_FOOTER_MARGIN
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = "0",
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_4_3,
			valueType = Integer.class
			)
	public static final String PROPERTY_PRINT_PAGE_LEFT_MARGIN = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "print.page.left.margin";
	
	/**
	 * Property used to provide a default value for the {@link #getPrintPageBottomMargin()} export configuration setting.
	 * The property can be set:
	 * Property scope:
	 * <ul>
	 * <li><code>Global</code></li>
	 * <li><code>Report</code></li>
	 * <li><code>Element</code> - this setting can be used to set the property value per sheet.</li>
	 * </ul>
	 * Global settings are overridden by report level settings; report level settings are overridden by element (sheet) level settings.
	 * 
	 * @see #PROPERTY_PRINT_PAGE_TOP_MARGIN
	 * @see #PROPERTY_PRINT_PAGE_LEFT_MARGIN
	 * @see #PROPERTY_PRINT_PAGE_RIGHT_MARGIN
	 * @see #PROPERTY_PRINT_HEADER_MARGIN
	 * @see #PROPERTY_PRINT_FOOTER_MARGIN
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = "0",
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_4_3,
			valueType = Integer.class
			)
	public static final String PROPERTY_PRINT_PAGE_BOTTOM_MARGIN = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "print.page.bottom.margin";
	
	/**
	 * Property used to provide a default value for the {@link #getPrintPageRightMargin()} export configuration setting.
	 * The property can be set:
	 * Property scope:
	 * <ul>
	 * <li><code>Global</code></li>
	 * <li><code>Report</code></li>
	 * <li><code>Element</code> - this setting can be used to set the property value per sheet.</li>
	 * </ul>
	 * Global settings are overridden by report level settings; report level settings are overridden by element (sheet) level settings.
	 * 
	 * @see #PROPERTY_PRINT_PAGE_TOP_MARGIN
	 * @see #PROPERTY_PRINT_PAGE_LEFT_MARGIN
	 * @see #PROPERTY_PRINT_PAGE_BOTTOM_MARGIN
	 * @see #PROPERTY_PRINT_HEADER_MARGIN
	 * @see #PROPERTY_PRINT_FOOTER_MARGIN
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = "0",
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_4_3,
			valueType = Integer.class
			)
	public static final String PROPERTY_PRINT_PAGE_RIGHT_MARGIN = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "print.page.right.margin";
	
	/**
	 * Property used to provide a default value for the {@link #getPrintPageHeight()} export configuration setting.
	 * The property can be set:
	 * Property scope:
	 * <ul>
	 * <li><code>Global</code></li>
	 * <li><code>Report</code></li>
	 * <li><code>Element</code> - this setting can be used to set the property value per sheet.</li>
	 * </ul>
	 * Global settings are overridden by report level settings; report level settings are overridden by element (sheet) level settings.
	 * 
	 * @see #PROPERTY_PRINT_PAGE_TOP_MARGIN
	 * @see #PROPERTY_PRINT_PAGE_LEFT_MARGIN
	 * @see #PROPERTY_PRINT_PAGE_BOTTOM_MARGIN
	 * @see #PROPERTY_PRINT_PAGE_RIGHT_MARGIN
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_6_0,
			valueType = Integer.class
			)
	public static final String PROPERTY_PRINT_PAGE_HEIGHT = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "print.page.height";
	
	/**
	 * Property used to provide a default value for the {@link #getPrintPageWidth()} export configuration setting.
	 * The property can be set:
	 * Property scope:
	 * <ul>
	 * <li><code>Global</code></li>
	 * <li><code>Report</code></li>
	 * <li><code>Element</code> - this setting can be used to set the property value per sheet.</li>
	 * </ul>
	 * Global settings are overridden by report level settings; report level settings are overridden by element (sheet) level settings.
	 * 
	 * @see #PROPERTY_PRINT_PAGE_TOP_MARGIN
	 * @see #PROPERTY_PRINT_PAGE_LEFT_MARGIN
	 * @see #PROPERTY_PRINT_PAGE_BOTTOM_MARGIN
	 * @see #PROPERTY_PRINT_PAGE_RIGHT_MARGIN
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_6_0,
			valueType = Integer.class
			)
	public static final String PROPERTY_PRINT_PAGE_WIDTH = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "print.page.width";
	
	/**
	 * Property used to provide a default value for the {@link #getPrintHeaderMargin()} export configuration setting.
	 * The property can be set:
	 * Property scope:
	 * <ul>
	 * <li><code>Global</code></li>
	 * <li><code>Report</code></li>
	 * <li><code>Element</code> - this setting can be used to set the property value per sheet.</li>
	 * </ul>
	 * Global settings are overridden by report level settings; report level settings are overridden by element (sheet) level settings.
	 * 
	 * @see #PROPERTY_PRINT_PAGE_TOP_MARGIN
	 * @see #PROPERTY_PRINT_PAGE_LEFT_MARGIN
	 * @see #PROPERTY_PRINT_PAGE_RIGHT_MARGIN
	 * @see #PROPERTY_PRINT_PAGE_BOTTOM_MARGIN
	 * @see #PROPERTY_PRINT_FOOTER_MARGIN
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = "0",
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_4_3,
			valueType = Integer.class
			)
	public static final String PROPERTY_PRINT_HEADER_MARGIN = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "print.header.margin";
	
	/**
	 * Property used to provide a default value for the {@link #getPrintFooterMargin()} export configuration setting.
	 * The property can be set:
	 * Property scope:
	 * <ul>
	 * <li><code>Global</code></li>
	 * <li><code>Report</code></li>
	 * <li><code>Element</code> - this setting can be used to set the property value per sheet.</li>
	 * </ul>
	 * Global settings are overridden by report level settings; report level settings are overridden by element (sheet) level settings.
	 * 
	 * @see #PROPERTY_PRINT_PAGE_TOP_MARGIN
	 * @see #PROPERTY_PRINT_PAGE_LEFT_MARGIN
	 * @see #PROPERTY_PRINT_PAGE_RIGHT_MARGIN
	 * @see #PROPERTY_PRINT_PAGE_BOTTOM_MARGIN
	 * @see #PROPERTY_PRINT_HEADER_MARGIN
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = "0",
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_4_3,
			valueType = Integer.class
			)
	public static final String PROPERTY_PRINT_FOOTER_MARGIN = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "print.footer.margin";
	
	/**
	 * Property prefix used to identify a set of properties that store defined names for 
	 * the generated Excel workbook. Each property suffix serves as name identifier and should 
	 * be in accordance with the following rules:
	 * <ul>
	 * <li>the first character in a name should be a letter, an underscore or a backslash</li>
	 * <li>the remaining characters may be letters, digits, periods or underscores; spaces are not allowed</li>
	 * <li>a name cannot represent a cell reference (names like {@code "A10"}, {@code "$A$10"} or {@code "R2C5"} are invalid)</li>
	 * <li>a name cannot exceed 255 characters</li>
	 * <li>names are not case sensitive: {@code "name1"}, {@code "Name1"} and {@code "NAME1"} represent the same name</li>
	 * <li>a name identifier should be unique within a given report, in order to be properly collected at export time</li>
	 * </ul>
	 * The value of such a property contains two distinct parts, separated by a pipe ({@code "|"}) sign:
	 * <ul>
	 * <li>the first part, which is mandatory, may represent:
	 * <ul>
	 * <li>a cell reference in a given sheet (such as {@code "Sheet_1!$A$10"}, {@code "Sheet_1!A10"}, or {@code "'Sheet 1'!$A$10"}); 
	 * the {@code "A10"} cell reference is relative, while {@code "$A$10"} is absolute</li>
	 * <li>a cell range reference in a given sheet (such as {@code "Sheet_1!A10:C20"}, {@code "Sheet_1!$A$10:$C$20"}, {@code "'Sheet 1'!$A$10:$C$20"})</li>
	 * <li>a formula (such as {@code "SUM('Sheet 1'!$A$10:$C$20)"})</li>
	 * </ul>
	 * Note: the R1C1 Excel reference style is not supported in a cell reference, cell range or formula.
	 * </li>
	 * <li>an optional second part represents the scope for the defined name. It can be either a sheet name, 
	 * or the {@code "workbook"} literal constant, representing the entire workbook. In case this second part
	 * is missing, the scope associated by default will be the entire workbook.</li>
	 * </ul>
	 * <p/>
	 * Examples:
	 * <ul>
	 * <li>{@code <property name="net.sf.jasperreports.export.xls.defined.names.test_sum" value="SUM(Sheet_1!$A$10:$C$20)|SecondSheet"/>}</li>
	 * </ul>
	 * <p/>
	 * This name, identified as {@code "test_sum"}, is visible only in a sheet named {@code "SecondSheet"} and is associated 
	 * with a formula that calculates a sum over the cell range {@code [$A$10:$C$20]} belonging to a sheet named {@code "Sheet_1"}.
	 * <ul>
	 * <li>{@code <property name="net.sf.jasperreports.export.xls.defined.names.test_range" value="'Sheet 1'!$A$10:$C$20"/>}</li>
	 * </ul>
	 * <p/>
	 * This name, identified as {@code "test_range"}, is visible in the entire workbook and is associated 
	 * with the cell range {@code [$A$10:$C$20]} in the sheet named {@code "Sheet 1"}.
	 * <p/>
	 * <ul>
	 * <li>{@code <property name="net.sf.jasperreports.export.xls.defined.names.test_sum_1" value="SUM(Sheet_1!$A$10:$C$20)|Sheet_1"/>}</li>
	 * <li>{@code <property name="net.sf.jasperreports.export.xls.defined.names.test_sum_all" value="Sheet_1!test_sum_1"/>}</li>
	 * </ul>
	 * <p/>
	 * In this case the name {@code test_sum_1} is visible only in the sheet named {@code Sheet_1}. 
	 * <p/>
	 * The name {@code test_sum_all} is visible in the entire workbook and 
	 * refers to the name {@code test_sum_1} defined for {@code Sheet_1}. 
	 * <p/>
	 * Note: Names that reference other names with limited visibility/scope (like the above {@code test_sum_all}) are NOT supported for 
	 * the XLS (Excel 2003) export format. They work only in the XLSX (Excel 2007 or newer) exporter.
	 * 
	 * @see JRPropertiesUtil
	 * @see <a href="https://support.office.com/en-us/article/Define-and-use-names-in-formulas-4d0f13ac-53b7-422e-afd2-abd7ff379c64#bmsyntax_rules_for_names">Rules for Excel defined names</a>
	 */
	@Property(
			name = "net.sf.jasperreports.export.xls.defined.names.{arbitrary_name}",
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_4_3
			)
	public static final String PROPERTY_DEFINED_NAMES_PREFIX = JRXlsAbstractExporter.XLS_EXPORTER_PROPERTIES_PREFIX + "defined.names.";
	
	/**
	 * Returns a boolean value specifying whether each report page should be written in a different XLS sheet.
	 * @see #PROPERTY_ONE_PAGE_PER_SHEET
	 */
	@ExporterProperty(
		value=PROPERTY_ONE_PAGE_PER_SHEET, 
		booleanDefault=false
		)
	public Boolean isOnePagePerSheet();

	/**
	 * Returns a boolean value specifying whether the empty spaces that could appear between rows should be removed or not.
	 * @see #PROPERTY_REMOVE_EMPTY_SPACE_BETWEEN_ROWS
	 */
	@ExporterProperty(
		value=PROPERTY_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, 
		booleanDefault=false
		)
	public Boolean isRemoveEmptySpaceBetweenRows();

	/**
	 * Returns a boolean value specifying whether the empty spaces that could appear between columns should be removed or not.
	 * @see #PROPERTY_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS
	 */
	@ExporterProperty(
		value=PROPERTY_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, 
		booleanDefault=false
		)
	public Boolean isRemoveEmptySpaceBetweenColumns();

	/**
	 * Returns a boolean value specifying whether the page background should be white or the default XLS background color. This background
	 * may vary depending on the XLS viewer properties or the operating system color scheme.
	 * @see #PROPERTY_WHITE_PAGE_BACKGROUND
	 */
	@ExporterProperty(
		value=PROPERTY_WHITE_PAGE_BACKGROUND, 
		booleanDefault=false
		)
	public Boolean isWhitePageBackground();
	
	/**
	 * Flag used to indicate whether the exporter should take into consideration the type of the
	 * original text field expressions and set the cell types and values accordingly.
	 * <p>
	 * Text fields having numerical or date expressions save type and formatting (format pattern, locale and time zone)
	 * information in the {@link net.sf.jasperreports.engine.JasperPrint JasperPrint}/{@link net.sf.jasperreports.engine.JRPrintText JRPrintText}
	 * object created by the report fill process.
	 * </p>
	 * <p>
	 * When this flag is set, the exporter will parse back the <code>String</code> value of numerical/date texts.
	 * Numerical/date cells will be created and the original pattern of the text will be included
	 * as part of the cell style.
	 * </p>
	 * <p>
	 * Note that this mechanism would not work when the text field overflows and splits on two pages/columns.
	 * Also, it is required that the text field expression has a numerical or date type set.
	 * </p>
	 * <p>
	 * This flag is off by default to ensure backwards compatibility.
	 * </p>
	 * @see #PROPERTY_DETECT_CELL_TYPE
	 */
	@ExporterProperty(
		value=PROPERTY_DETECT_CELL_TYPE,
		booleanDefault=false
		)
	public Boolean isDetectCellType();

	/**
	 * Flag for decreasing font size so that texts fit into the specified cell height.
	 * @see #PROPERTY_FONT_SIZE_FIX_ENABLED
	 */
	@ExporterProperty(
		value=PROPERTY_FONT_SIZE_FIX_ENABLED,
		booleanDefault=false
		)
	public Boolean isFontSizeFixEnabled();
	
	/**
	 * Flag for forcing the minimum image padding to 1 pixel, to avoid situations where the image hides the cell border.
	 * @see #PROPERTY_IMAGE_BORDER_FIX_ENABLED
	 */
	@ExporterProperty(
		value=PROPERTY_IMAGE_BORDER_FIX_ENABLED,
		booleanDefault=false
		)

	public Boolean isImageBorderFixEnabled();
	
	/**
	 * Flag for ignoring graphic elements and exporting text elements only.
	 * @see #PROPERTY_IGNORE_GRAPHICS
	 */
	@ExporterProperty(
		value=PROPERTY_IGNORE_GRAPHICS,
		booleanDefault=false
		)
	public Boolean isIgnoreGraphics();
	
	/**
	 * Flag for collapsing row span and avoid merging cells across rows.
	 * @see #PROPERTY_COLLAPSE_ROW_SPAN
	 */
	@ExporterProperty(
		value=PROPERTY_COLLAPSE_ROW_SPAN,
		booleanDefault=false
		)
	public Boolean isCollapseRowSpan();
	
	/**
	 * Flag for ignoring the cell border.
	 * @see #PROPERTY_IGNORE_CELL_BORDER
	 */
	@ExporterProperty(
		value=PROPERTY_IGNORE_CELL_BORDER,
		booleanDefault=false
		)
	public Boolean isIgnoreCellBorder();
	
	/**
	 * Flag for ignoring the cell background color.
	 * @see #PROPERTY_IGNORE_CELL_BACKGROUND
	 */
	@ExporterProperty(
		value=PROPERTY_IGNORE_CELL_BACKGROUND,
		booleanDefault=false
		)
	public Boolean isIgnoreCellBackground();
	
	/**
	 * This flag indicates whether text wrapping is allowed in a given cell.
	 * @see #PROPERTY_WRAP_TEXT
	 */
	@ExporterProperty(
		value=PROPERTY_WRAP_TEXT,
		booleanDefault=true
		)
	public Boolean isWrapText();
	
	/**
	 * This flag indicates whether the cell is locked.
	 * @see #PROPERTY_CELL_LOCKED
	 */
	@ExporterProperty(
		value=PROPERTY_CELL_LOCKED,
		booleanDefault=true
		)
	public Boolean isCellLocked();
	
	/**
	 * This flag indicates whether the cell content is hidden.
	 * @see #PROPERTY_CELL_HIDDEN
	 */
	@ExporterProperty(
		value=PROPERTY_CELL_HIDDEN,
		booleanDefault=false
		)
	public Boolean isCellHidden();
	
	/**
	 * Returns an integer value specifying the maximum number of rows allowed to be shown in a sheet.
	 * When set, a new sheet is created for the remaining rows to be displayed. Negative values or zero means that no limit has been set.
	 * @see #PROPERTY_MAXIMUM_ROWS_PER_SHEET
	 */
	@ExporterProperty(
		value=PROPERTY_MAXIMUM_ROWS_PER_SHEET,
		intDefault=0
		)
	public Integer getMaxRowsPerSheet();
	
	/**
	 * Indicates whether page margins should be ignored when the report is exported using a grid-based exporter
	 * <p>
	 * If set to <code>true</code>, any page in the document will be exported without taking into account its margins.
	 * </p>
	 * @see #PROPERTY_IGNORE_PAGE_MARGINS
	 */
	@ExporterProperty(
		value=PROPERTY_IGNORE_PAGE_MARGINS, 
		booleanDefault=false
		)
	public Boolean isIgnorePageMargins();
	
	/**
	 * This property stores the text content of the sheet header's left side.
	 * @see #PROPERTY_SHEET_HEADER_LEFT
	 */
	@ExporterProperty(PROPERTY_SHEET_HEADER_LEFT)
	public String getSheetHeaderLeft();
	
	/**
	 * This property stores the text content of the sheet header's center.
	 * @see #PROPERTY_SHEET_HEADER_CENTER
	 */
	@ExporterProperty(PROPERTY_SHEET_HEADER_CENTER)
	public String getSheetHeaderCenter();
	
	/**
	 * This property stores the text content of the sheet header's right side.
	 * @see #PROPERTY_SHEET_HEADER_RIGHT
	 */
	@ExporterProperty(PROPERTY_SHEET_HEADER_RIGHT)
	public String getSheetHeaderRight();
	
	/**
	 * This property stores the text content of the sheet footer's left side.
	 * @see #PROPERTY_SHEET_FOOTER_LEFT
	 */
	@ExporterProperty(PROPERTY_SHEET_FOOTER_LEFT)
	public String getSheetFooterLeft();
	
	/**
	 * This property stores the text content of the sheet footer's center.
	 * @see #PROPERTY_SHEET_FOOTER_CENTER
	 */
	@ExporterProperty(PROPERTY_SHEET_FOOTER_CENTER)
	public String getSheetFooterCenter();
	
	/**
	 * This property stores the text content of the sheet footer's right side.
	 * @see #PROPERTY_SHEET_FOOTER_RIGHT
	 */
	@ExporterProperty(PROPERTY_SHEET_FOOTER_RIGHT)
	public String getSheetFooterRight();

	/**
	 * Returns a String value representing the password in case of password protected documents.
	 * @see #PROPERTY_PASSWORD 
	 */
	@ExporterProperty(PROPERTY_PASSWORD)
	public String getPassword();

	/**
	 * Returns the value representing the custom sheet name to use instead of the report name,
	 * which would be the default, in case the {@link #isOnePagePerSheet()} is set to false.
	 * @see #PROPERTY_SINGLE_SHEET_NAME
	 */
	@ExporterProperty(PROPERTY_SINGLE_SHEET_NAME)
	public String getSingleSheetName();

	/**
	 * Returns an array of strings representing custom sheet names. 
	 * This is useful when used with the <i>isOnePagePerSheet()</i> setting.
	 * @see #PROPERTY_SHEET_NAMES_PREFIX
	 */
	@ExporterProperty(PROPERTY_SHEET_NAMES_PREFIX)
	public String[] getSheetNames();
	
	/**
	 * This export configuration setting should be used when converting java format patterns to equivalent proprietary
	 * format patterns. It should be constructed as a Map containing java format patterns as keys and the
	 * correspondent proprietary format pattern as correspondent value
	 * <p/>
	 * @see org.apache.poi.hssf.usermodel.HSSFDataFormat
	 */
	@ExporterProperty(FORMAT_PATTERN_PREFIX)
	public Map<String, String> getFormatPatternsMap();
	
	/**
	 * @see #PROPERTY_IGNORE_HYPERLINK
	 */
	@ExporterProperty(
		value=PROPERTY_IGNORE_HYPERLINK, 
		booleanDefault=false
		)
	public Boolean isIgnoreHyperlink();
	
	/**
	 * Flag that indicates whether local anchors should be ignored when elements are exported to Excel.
	 * @see #PROPERTY_IGNORE_ANCHORS
	 */
	@ExporterProperty(
		value=PROPERTY_IGNORE_ANCHORS, 
		booleanDefault=false
		)
	public Boolean isIgnoreAnchors();
	
	/**
	 * This setting indicates the number of pages wide to fit the sheet in.
	 * @see #PROPERTY_FIT_WIDTH
	 */
	@ExporterProperty(
		value=PROPERTY_FIT_WIDTH, 
		nullDefault=true
		)
	public Integer getFitWidth();
	
	/**
	 * This setting indicates the number of pages height to fit the sheet in.
	 * @see #PROPERTY_FIT_WIDTH
	 */
	@ExporterProperty(
		value=PROPERTY_FIT_HEIGHT, 
		nullDefault=true
		)
	public Integer getFitHeight();
	
	/**
	 * This setting is used to adjust the page content to a given percent of the normal size in the print preview pane. Allowed values are 
	 * positive integers from 10 to 400, representing percents of the normal size.
	 * This setting overrides the {@link #getFitWidth()} and {@link #getFitHeight()} settings.
	 * @see #PROPERTY_PAGE_SCALE
	 */
	@ExporterProperty(
		value=PROPERTY_PAGE_SCALE, 
		nullDefault=true
		)
	public Integer getPageScale();

	/**
	 * This setting indicates if the sheet is left-to-right or right-to-left oriented. Possible values are:
	 * <ul>
	 * <li>LTR - meaning left-to-right</li>
	 * <li>RTL - meaning right-to-left</li>
	 * </ul>
	 * The default value is LTR.
	 * @see #PROPERTY_SHEET_DIRECTION
	 */
	@ExporterProperty(PROPERTY_SHEET_DIRECTION)
	public RunDirectionEnum getSheetDirection();
	
	/**
	 * Setting used to adjust all column widths in a document or sheet with the same width ratio, in order to get column width 
	 * values suitable for Excel output. Usually column widths are measured by Excel in Normal style default character width 
	 * units, while the JR engine uses pixels as default size units. When exporting the report to the Excel output format, the 
	 * pixel-to-character width translation depends on the normal style default character width provided by the Excel instance, 
	 * so it cannot be always accurately fitted. In this case, one can alter the generated column widths by setting this property 
	 * with a float value representing the adjustment ratio.
	 * @see #PROPERTY_COLUMN_WIDTH_RATIO
	 */
	@ExporterProperty(
		value=PROPERTY_COLUMN_WIDTH_RATIO,
		nullDefault=true
		)
	public Float getColumnWidthRatio();
	
	/**
	 * Flag that determines whether date values are to be translated to the timezone
	 * that was used to fill the report.
	 * 
	 * <p>
	 * By default, date values are exported to Excel using the default timezone of the system.
	 * Setting this to <code>true</code> instructs the exporter to use he report fill
	 * timezone to export date values.
	 * 
	 * <p>
	 * This only has effect when {@link #isDetectCellType()} is set.
	 * 
	 * @see #PROPERTY_USE_TIMEZONE
	 */
	@ExporterProperty(
		value=PROPERTY_USE_TIMEZONE, 
		booleanDefault=false
		)
	public Boolean isUseTimeZone();
	
	/**
	 * Setting that specifies the first page number in the page setup dialog.
	 * @see #PROPERTY_FIRST_PAGE_NUMBER
	 */
	@ExporterProperty(
		value=PROPERTY_FIRST_PAGE_NUMBER,
		nullDefault=true
		)
	public Integer getFirstPageNumber();
	
	/**
	 * Flag that specifies if the gridlines in a given sheet are shown.
	 * @see #PROPERTY_SHOW_GRIDLINES
	 */
	@ExporterProperty(
		value=PROPERTY_SHOW_GRIDLINES, 
		booleanDefault=true
		)
	public Boolean isShowGridLines();
	
	/**
	 * Specifies the image anchor type.
	 * @see #PROPERTY_IMAGE_ANCHOR_TYPE
	 */
	@ExporterProperty(PROPERTY_IMAGE_ANCHOR_TYPE)
	public ImageAnchorTypeEnum getImageAnchorType();
	
	/**
	 * Flag that specifies whether the fit height should be estimated automatically.
	 * @see #PROPERTY_AUTO_FIT_PAGE_HEIGHT
	 */
	@ExporterProperty(
			value=PROPERTY_AUTO_FIT_PAGE_HEIGHT,
			booleanDefault=false
			)
	public Boolean isAutoFitPageHeight();
	
	/**
	 * Flag that specifies whether the page breaks to be marked automatically on each sheet.
	 * @see #PROPERTY_FORCE_PAGE_BREAKS
	 */
	@ExporterProperty(
			value=PROPERTY_FORCE_PAGE_BREAKS,
			booleanDefault=false
			)
	public Boolean isForcePageBreaks();
	
	/**
	 * Flag that indicates whether the text font size should be decreased in order to 
	 * keep the entire text visible in the cell. If set to true, this will automatically disable the 
	 * wrap text property (see {@link #isWrapText()}).
	 * <p/>
	 * Usually this setting works in conjunction with {@link net.sf.jasperreports.engine.JRTextElement#PROPERTY_PRINT_KEEP_FULL_TEXT net.sf.jasperreports.print.keep.full.text}, 
	 * in order to preserve the entire text content at export time.
	 * @see #PROPERTY_SHRINK_TO_FIT
	 */
	@ExporterProperty(
			value=PROPERTY_SHRINK_TO_FIT,
			booleanDefault=false
			)
	public Boolean isShrinkToFit();
	
	/**
	 * Flag that indicates whether the text elements should be exported without text formatting features, 
	 * such as bold, italic, underline, text color, backcolor, etc. 
	 * <p/>
	 * @see #PROPERTY_IGNORE_TEXT_FORMATTING
	 */
	@ExporterProperty(
		value=PROPERTY_IGNORE_TEXT_FORMATTING,
		booleanDefault=false
		)
	public Boolean isIgnoreTextFormatting();
	
	/**
	 * This setting is used to set the tab color of the sheets. Global and  
	 * report-level settings are overridden by element-level settings for this property.
	 * If several elements in a sheet contain this property, the engine will consider the value of the 
	 * last exported element's property.
	 * <p/>
	 * The setting is neglected in XLS export channel (works with XLSX and ODS only)
	 * @see #PROPERTY_SHEET_TAB_COLOR
	 */
	@ExporterProperty(
		value=PROPERTY_SHEET_TAB_COLOR, 
		nullDefault=true
		)
	public Color getSheetTabColor();
	
	/**
	 * Specifies the index of the first unlocked row in document's sheets. All rows above this will be 'frozen'. 
	 * Allowed values are represented by positive integers in the 1..65536 range. Negative values are not considered. 
	 * @see #PROPERTY_FREEZE_ROW
	 */
	@ExporterProperty(
		value=PROPERTY_FREEZE_ROW, 
		nullDefault=true
		)
	public Integer getFreezeRow();
	
	/**
	 * Indicates the name of the first unlocked column in document's sheets. All columns to the left of this one will be 'frozen'. 
	 * Allowed values are letters or letter combinations representing valid column names in Excel, such as A, B, AB, AC, etc.
	 * @see #PROPERTY_FREEZE_COLUMN
	 */
	@ExporterProperty(
		value=PROPERTY_FREEZE_COLUMN, 
		nullDefault=true
		)
	public String getFreezeColumn();
	
	/**
	 * Specifies the page top margin in print preview pane, measured in pixels. Default value is 0.
	 */
	@ExporterProperty(
			value=PROPERTY_PRINT_PAGE_TOP_MARGIN
			)
	public Integer getPrintPageTopMargin();
	
	/**
	 * Specifies the page left margin in print preview pane, measured in pixels. Default value is 0.
	 */
	@ExporterProperty(
			value=PROPERTY_PRINT_PAGE_LEFT_MARGIN
			)
	public Integer getPrintPageLeftMargin();
	
	/**
	 * Specifies the page bottom margin in print preview pane, measured in pixels. Default value is 0. 
	 */
	@ExporterProperty(
			value=PROPERTY_PRINT_PAGE_BOTTOM_MARGIN
			)
	public Integer getPrintPageBottomMargin();
	
	/**
	 * Specifies the page right margin in print preview pane, measured in pixels. Default value is 0. 
	 */
	@ExporterProperty(
			value=PROPERTY_PRINT_PAGE_RIGHT_MARGIN
			)
	public Integer getPrintPageRightMargin();
	
	/**
	 * Specifies the page height in print preview pane, measured in pixels. 
	 */
	@ExporterProperty(
			value=PROPERTY_PRINT_PAGE_HEIGHT,
			nullDefault=true
			)
	public Integer getPrintPageHeight();
	
	/**
	 * Specifies the page width in print preview pane, measured in pixels. 
	 */
	@ExporterProperty(
			value=PROPERTY_PRINT_PAGE_WIDTH,
			nullDefault=true
			)
	public Integer getPrintPageWidth();
	
	/**
	 * Specifies the sheet header margin in print preview pane, measured in pixels. Default value is 0. 
	 */
	@ExporterProperty(
			value=PROPERTY_PRINT_HEADER_MARGIN
			)
	public Integer getPrintHeaderMargin();
	
	/**
	 * Specifies the sheet footer margin in print preview pane, measured in pixels. Default value is 0. 
	 */
	@ExporterProperty(
		value=PROPERTY_PRINT_FOOTER_MARGIN
		)
	public Integer getPrintFooterMargin();
	
	/**
	 * Returns an array of strings representing defined names in the generated workbook. 
	 * @see #PROPERTY_DEFINED_NAMES_PREFIX
	 */
	@ExporterProperty(
			value=PROPERTY_DEFINED_NAMES_PREFIX
			)
	public PropertySuffix[] getDefinedNames();
	
	/**
	 * 
	 * @see JRXlsAbstractExporter#PROPERTY_AUTO_FIT_ROW
	 */
	@ExporterProperty(
			value=JRXlsAbstractExporter.PROPERTY_AUTO_FIT_ROW,
			booleanDefault=false
			)
	public Boolean isAutoFitRow();
	
}

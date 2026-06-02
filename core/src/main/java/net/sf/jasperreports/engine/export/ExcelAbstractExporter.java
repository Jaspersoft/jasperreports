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

/*
 * Contributors:
 * Greg Hilton
 */

package net.sf.jasperreports.engine.export;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TimeZone;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRPropertiesUtil.PropertySuffix;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRStyledTextAttributeSelector;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.PrintPageFormat;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporter.SheetInfo;
import net.sf.jasperreports.engine.export.type.CellEdgeEnum;
import net.sf.jasperreports.engine.export.type.ImageAnchorTypeEnum;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.type.VerticalTextAlignEnum;
import net.sf.jasperreports.engine.util.JRDataUtils;
import net.sf.jasperreports.engine.util.JRStringUtil;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.engine.util.ObjectUtils;
import net.sf.jasperreports.export.OutputStreamExporterOutput;
import net.sf.jasperreports.export.PrintPartUnrollExporterInput;
import net.sf.jasperreports.export.ReportExportConfiguration;
import net.sf.jasperreports.export.XlsExporterConfiguration;
import net.sf.jasperreports.export.XlsMetadataReportConfiguration;
import net.sf.jasperreports.export.XlsReportConfiguration;
import net.sf.jasperreports.properties.PropertyConstants;
import net.sf.jasperreports.renderers.RenderersCache;

/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public abstract class ExcelAbstractExporter<RC extends XlsReportConfiguration, C extends XlsExporterConfiguration, E extends JRExporterContext>
		extends JRAbstractExporter<RC, C, OutputStreamExporterOutput, E> 
{

	public static final String XLS_EXPORTER_PROPERTIES_PREFIX = JRPropertiesUtil.PROPERTY_PREFIX + "export.xls.";
	public static final String DEFAULT_SHEET_NAME_PREFIX = "Page ";
	public static final String DEFAULT_DEFINED_NAME_SCOPE = "workbook";
	public static final String DEFAULT_DEFINED_NAME_SCOPE_SEPARATOR = "\\u007C";	// the '|' character
	
	public static final String EXCEPTION_MESSAGE_KEY_CANNOT_ADD_CELL = "export.xls.common.cannot.add.cell";
	public static final String EXCEPTION_MESSAGE_KEY_CANNOT_MERGE_CELLS = "export.xls.common.cannot.merge.cells";
	public static final String EXCEPTION_MESSAGE_KEY_CELL_FORMAT_TEMPLATE_ERROR = "export.xls.common.cell.format.template.error";
	public static final String EXCEPTION_MESSAGE_KEY_COLUMN_INDEX_BEYOND_LIMIT = "export.xls.common.column.index.beyond.limit";
	public static final String EXCEPTION_MESSAGE_KEY_LOADED_FONTS_ERROR = "export.xls.common.loaded.fonts.error";
	public static final String EXCEPTION_MESSAGE_KEY_NEGATIVE_COLUMN_INDEX = "export.xls.common.negative.column.index";
	public static final String EXCEPTION_MESSAGE_KEY_REPORT_GENERATION_ERROR = "export.xls.common.report.generation.error";
	public static final String EXCEPTION_MESSAGE_KEY_TEMPLATE_NOT_FOUND = "export.xls.common.template.not.found";

	/**
	 * Property that stores the formula which has to be applied to a given cell in an excel sheet.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.TEXT_ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_0_0
			)
	public static final String PROPERTY_CELL_FORMULA = XLS_EXPORTER_PROPERTIES_PREFIX + "formula";

	/**
	 * Property that stores the pattern which has to be applied to a given cell in an excel sheet.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.TEXT_ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_1_1
			)
	public static final String PROPERTY_CELL_PATTERN = XLS_EXPORTER_PROPERTIES_PREFIX + "pattern";

	/**
	 * Property used to set the name of the sheet containing a given element. Its value overrides the report-level settings for the sheet name. 
	 * If several elements in the sheet contain this property, the engine will consider the value of the last exported element's property.
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_1_3
			)
	public static final String PROPERTY_SHEET_NAME = XLS_EXPORTER_PROPERTIES_PREFIX + "sheet.name";

	/**
	 * This property indicates the horizontal edge of the freeze pane, relative to the current cell. If set, it overrides the 
	 * PROPERTY_FREEZE_ROW value.
	 * Allowed values are:
	 * <ul>
	 * <li><code>Top</code> - The current row is the first unlocked row in the sheet. All rows above are 'frozen'.</li>
	 * <li><code>Bottom</code> - The current row is the last 'frozen' row in the sheet. All rows below are unlocked.</li>
	 * </ul>
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_1_1,
			valueType = CellEdgeEnum.class
			)
	public static final String PROPERTY_FREEZE_ROW_EDGE = XLS_EXPORTER_PROPERTIES_PREFIX + "freeze.row.edge";
	
	/**
	 * This property indicates the vertical edge of the freeze pane, relative to the current cell. If set, it overrides the 
	 * PROPERTY_FREEZE_COLUMN and PROPERTY_FREEZE_SHEET_COLUMNS values.
	 * Allowed values are:
	 * <ul>
	 * <li><code>Left</code> - The current column is the first unlocked column in the sheet. All columns to the left are 'frozen'.</li>
	 * <li><code>Right</code> - The current column is the last 'frozen' column in the sheet. All columns to the right are unlocked.</li>
	 * </ul>
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_1_1,
			valueType = CellEdgeEnum.class
			)
	public static final String PROPERTY_FREEZE_COLUMN_EDGE = XLS_EXPORTER_PROPERTIES_PREFIX + "freeze.column.edge";
	
	/**
	 * Flag property that indicates whether Excel should autofit the current row height.
	 * Allowed values are:
	 * <ul>
	 * <li><code>true</code></li>
	 * <li><code>false</code> - this is the default value.</li>
	 * </ul>
	 * 
	 * @see JRPropertiesUtil
	 * @since 4.5.1
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_5_1,
			valueType = Boolean.class
			)
	public static final String PROPERTY_AUTO_FIT_ROW = XLS_EXPORTER_PROPERTIES_PREFIX + "auto.fit.row";
	
	/**
	 * Flag property that indicates whether the height of the element should contribute to the current row height calculation.
	 * The current row height is given by the tallest element on the row, unless row height auto fit is activated.
	 * But in certain cases, the tallest element might span multiple rows and thus its bigger height should not be considered, especially
	 * in case of metadata based exports.
	 * Allowed values are:
	 * <ul>
	 * <li><code>true</code></li>
	 * <li><code>false</code> - this is the default value.</li>
	 * </ul>
	 * 
	 * @see JRPropertiesUtil
	 * @since 6.20.1
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_20_1,
			valueType = Boolean.class
			)
	public static final String PROPERTY_IGNORE_ROW_HEIGHT = XLS_EXPORTER_PROPERTIES_PREFIX + "ignore.row.height";
	
	/**
	 * Flag property that indicates whether Excel should autofit the current column width.
	 * Allowed values are:
	 * <ul>
	 * <li><code>true</code></li>
	 * <li><code>false</code> - this is the default value.</li>
	 * </ul>
	 * 
	 * @see JRPropertiesUtil
	 * @since 4.5.1
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_5_1,
			valueType = Boolean.class
			)
	public static final String PROPERTY_AUTO_FIT_COLUMN = XLS_EXPORTER_PROPERTIES_PREFIX + "auto.fit.column";
	
	/**
	 * This element-level property is used to indicate the boundaries of the autofilter data range in the current sheet. 
	 * Allowed values are:
	 * <ul>
	 * <li><code>Start</code> - The current cell will be marked as autofilter heading cell, and column data below/to the right of 
	 * this cell can be considered as part of the autofilter data range. The starting point of the autofilter data range will be 
	 * the next cell below the current cell (ie if the current cell reference is B4, the autofilter range will start with the "B5" 
	 * cell reference: "B5:M20").
	 * <br/>
	 * If multiple autofilter <code>Start</code> values are found in the same sheet, only the last one will be considered. If the 
	 * <code>Start</code> value is present but no <code>End</code> value is found in the sheet, then only the current column will 
	 * be considered for the data range.
	 * <br/>
	 * If the autofilter <code>Start</code> value is set on the same row as the autofilter <code>End</code> value, the data range 
	 * will include all data below this heading row, placed between the start column and the end column.</li>
	 * <li><code>End</code> - The current cell will be marked as autofilter ending cell, and column data in this cell and 
	 * above/to the left can be considered as part of the autofilter data range. The ending cell in the data range is the 
	 * current cell reference (ie if the current cell reference is M20, the autofilter range will end in "M20": "B5:M20"). 
	 * The heading cell for the current column will be placed in the same column on the row containing the autofilter <code>Start</code> value. 
	 * <br/>
	 * <b>Caution:</b> If no autofilter <code>Start</code> value is found in the sheet, the autofilter <code>End</code> value will be considered 
	 * as <code>Start</code> value instead. 
	 * <br/>
	 * If multiple autofilter <code>End</code> value are found in the same sheet, only the last one will be considered. 
	 * <br/>
	 * If the autofilter <code>Start</code> value is set on the same row as the autofilter <code>End</code> value, the data range 
	 * will include all data below this heading row, placed between the start column and the end column.</li>
	 * </ul>
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_1_3
			)
	public static final String PROPERTY_AUTO_FILTER = XLS_EXPORTER_PROPERTIES_PREFIX + "auto.filter";
	
	/**
	 * Element-level property used to adjust the column width to values suitable for Excel output, taking into account 
	 * that column widths are measured in Excel in Normal style default character width units. The pixel-to-character width 
	 * translation depends on the default normal style character width, so it cannot be always accurately fitted. In this case, 
	 * one can adjust the current column width by setting this property with an integer value measured in pixels. The JR engine 
	 * will perform the pixel-to-character width mapping using this value instead of the element's <code>width</code> attribute.
	 * <br/>
	 * If defined, this property will override the {@link XlsReportConfiguration#PROPERTY_COLUMN_WIDTH_RATIO PROPERTY_COLUMN_WIDTH_RATIO} value for the current column
	 * 
	 * @see XlsReportConfiguration#PROPERTY_COLUMN_WIDTH_RATIO
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_1_3,
			valueType = Integer.class
			)
	public static final String PROPERTY_COLUMN_WIDTH = XLS_EXPORTER_PROPERTIES_PREFIX + "column.width";
	
	/**
	 * Property specifying the multiplication factor to be used when calculating the column width for auto fit columns
	 * on top of the fill-time calculated average char width.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_6_21_4,
			valueType = Float.class
			)
	public static final String PROPERTY_AVERAGE_CHAR_WIDTH_FACTOR = XLS_EXPORTER_PROPERTIES_PREFIX + "average.char.width.factor";

	/**
	 * Property prefix used to indicate the current outline row level, and when necessary, the ending row of the current outline row 
	 * group with the given level. The suffix 
	 * of these properties is associated with the outline level, while the property value indicates if the current row group should 
	 * continue or should end. The most recommended practice is to use the outline level itself as property suffix, although this is not 
	 * mandatory. The suffix may take any other string value, but one has to keep in mind that suffixes are used as sorted row level 
	 * descriptors. For instance, because "aaa" &lt; "bbb", the outline level associated with the "aaa" suffix will be smaller than 
	 * the level associated with the "bbb" suffix. The most intuitive representation of the row levels uses the row level as property suffix.
	 * <br/>
	 * In such a case, the {@link #PROPERTY_ROW_OUTLINE_LEVEL_PREFIX net.sf.jasperreports.export.xls.outline.level.2} property means that its value is correlated with 
	 * the outline level 2, so the current row belongs to a level 2 row group. Based on Office Open XML specs, allowed values for outline 
	 * levels are positive integers from 1 to 7.
	 * <br/>
	 * The value of this property could be any expression (including <code>null</code>). When such a property occurrence is met, the suffix 
	 * indicates the outline level for that row. If multiple properties with the same prefix are defined for the same row, the deepest 
	 * outline level is considered for that row. To end an outline row group one has to set the related outline level property with the 
	 * <code>End</code> value. This is a special property value instructing the JR engine that the current row group of that level ends 
	 * on the current row. 
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			name = "net.sf.jasperreports.export.xls.row.outline.level.{arbitrary_level}",
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_1_3
			)
	public static final String PROPERTY_ROW_OUTLINE_LEVEL_PREFIX = XLS_EXPORTER_PROPERTIES_PREFIX + "row.outline.level.";
	

	/**
	 * Element level property specifying if a sheet will break before the row displaying that element. 
	 * It is very useful especially when displaying each report's group on a separate sheet is intended. 
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_0_0,
			valueType = Boolean.class
			)
	public static final String PROPERTY_BREAK_BEFORE_ROW = XLS_EXPORTER_PROPERTIES_PREFIX + "break.before.row";
	

	/**
	 * Element level property specifying if a sheet will break after the row displaying that element. 
	 * It is very useful especially when displaying each report's group on a separate sheet is intended. 
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_0_0,
			valueType = Boolean.class
			)
	public static final String PROPERTY_BREAK_AFTER_ROW = XLS_EXPORTER_PROPERTIES_PREFIX + "break.after.row";


	/**
	 * A string that represents the name for the column that should appear in the XLS export.
	 * It must be one of the values in {@link XlsMetadataReportConfiguration#getColumnNames()}, if provided. 
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_0_2
			)
	public static final String PROPERTY_COLUMN_NAME = JRPropertiesUtil.PROPERTY_PREFIX + "export.xls.column.name";
	
	/**
	 * Property that specifies whether the value associated with {@link #PROPERTY_COLUMN_NAME PROPERTY_COLUMN_NAME} should be repeated or not
	 * when it is missing.
	 * <p>
	 * The property itself defaults to <code>false</code>.
	 * </p>
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_0_2,
			valueType = Boolean.class
			)
	public static final String PROPERTY_REPEAT_VALUE = JRPropertiesUtil.PROPERTY_PREFIX + "export.xls.repeat.value";
	
	/**
	 * Property that specifies what value to associate with {@link #PROPERTY_COLUMN_NAME PROPERTY_COLUMN_NAME}.
	 * <p>
	 * The property itself defaults to the text value of the report element that this property is assigned to.
	 * </p>
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.TEXT_ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_0_2
			)
	public static final String PROPERTY_DATA = JRPropertiesUtil.PROPERTY_PREFIX + "export.xls.data";
	
	/**
	 * Element-level property used to adjust the column width to values suitable for Excel metadata output, taking into account 
	 * that when column headers are included in the report, they come with no available width settings.
	 * Also keep in mind that column widths are measured in Excel in Normal style default character width units. The pixel-to-character width 
	 * translation depends on the default normal style character width, so it cannot be always accurately fitted. In this case, 
	 * one can adjust the current column width by setting this property with an integer value measured in pixels. The JR engine 
	 * will perform the pixel-to-character width mapping using this value instead of the element's <code>width</code> attribute.
	 * <br/>
	 * If defined, this property will override both the {@link #PROPERTY_COLUMN_WIDTH PROPERTY_COLUMN_WIDTH} value for 
	 * the current column and the the element's <code>width</code> attribute
	 * 
	 * @see #PROPERTY_COLUMN_WIDTH
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_20_0,
			valueType = Integer.class
			)
	public static final String PROPERTY_COLUMN_WIDTH_METADATA = JRPropertiesUtil.PROPERTY_PREFIX + "export.xls.column.width.metadata";

	public static final String CURRENT_ROW_HEIGHT = "CURRENT_ROW_HEIGHT";

	public static final String CURRENT_ROW_AUTOFIT = "CURRENT_ROW_AUTOFIT";
		

	protected static class TextAlignHolder {
		public final HorizontalTextAlignEnum horizontalAlignment;
		public final VerticalTextAlignEnum verticalAlignment;
		public final RotationEnum rotation;

		public TextAlignHolder(HorizontalTextAlignEnum horizontalAlignment, VerticalTextAlignEnum verticalAlignment,
				RotationEnum rotation) {
			this.horizontalAlignment = horizontalAlignment;
			this.verticalAlignment = verticalAlignment;
			this.rotation = rotation;
		}
	}

	/**
	 *
	 */
	protected List<Object> loadedFonts = new ArrayList<>();

	/**
	 *
	 */
	protected ExporterNature nature;

	/**
	 *
	 */
	protected String[] sheetNames;

	protected int reportIndex;
	protected int pageIndex;
	protected PrintPageFormat pageFormat;
	protected Map<Integer, Boolean> onePagePerSheetMap = new HashMap<>();
	protected int sheetsBeforeCurrentReport;
	protected Map<Integer, Integer> sheetsBeforeCurrentReportMap = new HashMap<>();
	protected RenderersCache renderersCache;

	/**
	 *
	 */
	protected JRFont defaultFont;

	
	protected SheetInfo sheetInfo;
	
	/**
	 * Used for counting the total number of sheets.
	 */
	protected int sheetIndex;

	/**
	 * Used for iterating through sheet names.
	 */
	protected int sheetNamesIndex;

	/**
	 * Used when indexing the identical sheet name. Contains sheet names as keys and
	 * the number of occurrences of each sheet name as values.
	 */
	protected Map<String, Integer> sheetNamesMap;
	protected String autoFilterStart;
	protected String autoFilterEnd;

	protected boolean firstPageNotSet;

	protected Boolean keepTemplateSheets;
	protected String workbookTemplate;

	protected String invalidCharReplacement;
	protected int maxColumnIndex;

	protected Map<NameScope, String> definedNamesMap;

	protected Map<String, String> formatPatternsMap;
	protected boolean onePagePerSheet;
	protected boolean defaultShrinkToFit;
	protected boolean defaultWrapText;
	protected boolean defaultCellLocked;
	protected boolean defaultCellHidden;
	protected boolean defaultIgnoreTextFormatting;
	protected boolean ignoreAnchors;
	protected boolean defaultIgnoreHyperlink;
	protected boolean detectCellType;
	protected ImageAnchorTypeEnum defaultImageAnchorType;
	protected boolean collapseRowSpan;
	protected boolean defaultUseTimeZone;
	protected boolean imageBorderFixEnabled;
	protected int reportDpi;
	
	public class NameScope {
		private String name;
		private String scope;

		public NameScope(String name, String scope) {
			this.name = name;
			this.scope = scope;
		}

		public String getName() {
			return name;
		}

		public String getScope() {
			return scope;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return this == null;
			} else {
				if (this == null || !obj.getClass().equals(NameScope.class)) {
					return false;
				} else {
					@SuppressWarnings("unchecked")
					NameScope obj1 = (NameScope) obj;
					return ObjectUtils.equals(this.name, obj1.name) && ObjectUtils.equals(this.scope, obj1.scope);
				}
			}
		}

		@Override
		public int hashCode() {
			ObjectUtils.HashCode hash = ObjectUtils.hash();
			hash.add(name);
			hash.add(scope);
			return hash.getHashCode();
		}
	}

	/**
	 *
	 */
	protected JRFont getDefaultFont() {
		return defaultFont;
	}

	/**
	 * @see #ExcelAbstractExporter(JasperReportsContext)
	 */
	public ExcelAbstractExporter() {
		this(DefaultJasperReportsContext.getInstance());
	}

	/**
	 *
	 */
	public ExcelAbstractExporter(JasperReportsContext jasperReportsContext) {
		super(jasperReportsContext);
	}

	@Override
	public void exportReport() throws JRException {
		/*   */
		ensureJasperReportsContext();
		ensureInput();

		initExport();

		ensureOutput();

		OutputStream outputStream = getExporterOutput().getOutputStream();

		try {
			exportReportToStream(outputStream);
		} catch (IOException e) {
			throw new JRRuntimeException(e);
		} finally {
			getExporterOutput().close();
			resetExportContext();
		}
	}

	@Override
	protected void ensureInput() {
		super.ensureInput();

		exporterInput = new PrintPartUnrollExporterInput(exporterInput, getItemConfigurationInterface());

		jasperPrint = exporterInput.getItems().get(0).getJasperPrint();// this is just for the sake of
																		// getCurrentConfiguration() calls made prior to
																		// any setCurrentExporterInputItem() call
	}

	@Override
	protected void initExport() {
		super.initExport();

		sheetInfo = null;
		sheetIndex = 0;
		onePagePerSheetMap.clear();
		sheetsBeforeCurrentReport = 0;
		sheetsBeforeCurrentReportMap.clear();
	}

	@Override
	protected void initReport() {
		super.initReport();

		reportDpi = jasperPrint.getDpi();

		setSheetNames();
		if (jasperPrint.hasProperties()
				&& jasperPrint.getPropertiesMap().containsProperty(JRXmlExporter.PROPERTY_REPLACE_INVALID_CHARS)) {
			// allows null values for the property
			invalidCharReplacement = jasperPrint.getProperty(JRXmlExporter.PROPERTY_REPLACE_INVALID_CHARS);
		} else {
			invalidCharReplacement = getPropertiesUtil().getProperty(JRXmlExporter.PROPERTY_REPLACE_INVALID_CHARS,
					jasperPrint);
		}

		renderersCache = new RenderersCache(getJasperReportsContext());
		
		RC configuration = getCurrentItemConfiguration();
		onePagePerSheet = configuration.isOnePagePerSheet();
		formatPatternsMap = configuration.getFormatPatternsMap();
		defaultShrinkToFit = configuration.isShrinkToFit();
		defaultWrapText = configuration.isWrapText();
		defaultCellLocked = configuration.isCellLocked();
		defaultCellHidden = configuration.isCellHidden();
		defaultIgnoreTextFormatting = configuration.isIgnoreTextFormatting();
		ignoreAnchors = configuration.isIgnoreAnchors();
		defaultIgnoreHyperlink = configuration.isIgnoreHyperlink();
		detectCellType = configuration.isDetectCellType();
		defaultImageAnchorType = configuration.getImageAnchorType();
		collapseRowSpan = configuration.isCollapseRowSpan();
		defaultUseTimeZone = configuration.isUseTimeZone();
		imageBorderFixEnabled = configuration.isImageBorderFixEnabled();
	}

	protected void updatePrintSettings(SheetInfo.SheetPrintSettings printSettings,
			XlsReportConfiguration configuration) {
		if (printSettings.getPageHeight() == null) {
			printSettings.setPageHeight(configuration.getPrintPageHeight() == null ? jasperPrint.getPageHeight()
					: configuration.getPrintPageHeight());
		}
		if (printSettings.getPageWidth() == null) {
			printSettings.setPageWidth(configuration.getPrintPageWidth() == null ? jasperPrint.getPageWidth()
					: configuration.getPrintPageWidth());
		}
		if (printSettings.getTopMargin() == null) {
			printSettings.setTopMargin(
					configuration.getPrintPageTopMargin() == null ? 0 : configuration.getPrintPageTopMargin());
		}
		if (printSettings.getLeftMargin() == null) {
			printSettings.setLeftMargin(
					configuration.getPrintPageLeftMargin() == null ? 0 : configuration.getPrintPageLeftMargin());
		}
		if (printSettings.getBottomMargin() == null) {
			printSettings.setBottomMargin(
					configuration.getPrintPageBottomMargin() == null ? 0 : configuration.getPrintPageBottomMargin());
		}
		if (printSettings.getRightMargin() == null) {
			printSettings.setRightMargin(
					configuration.getPrintPageRightMargin() == null ? 0 : configuration.getPrintPageRightMargin());
		}
		if (printSettings.getHeaderMargin() == null) {
			printSettings.setHeaderMargin(
					configuration.getPrintHeaderMargin() == null ? 0 : configuration.getPrintHeaderMargin());
		}
		if (printSettings.getFooterMargin() == null) {
			printSettings.setFooterMargin(
					configuration.getPrintFooterMargin() == null ? 0 : configuration.getPrintFooterMargin());
		}
	}

	protected void updateHeaderFooter(SheetInfo.SheetPrintSettings printSettings,
			XlsReportConfiguration configuration) {
		if (printSettings.getHeaderLeft() == null) {
			printSettings.setHeaderLeft(configuration.getSheetHeaderLeft());
		}
		if (printSettings.getHeaderCenter() == null) {
			printSettings.setHeaderCenter(configuration.getSheetHeaderCenter());
		}
		if (printSettings.getHeaderRight() == null) {
			printSettings.setHeaderRight(configuration.getSheetHeaderRight());
		}
		if (printSettings.getFooterLeft() == null) {
			printSettings.setFooterLeft(configuration.getSheetFooterLeft());
		}
		if (printSettings.getFooterCenter() == null) {
			printSettings.setFooterCenter(configuration.getSheetFooterCenter());
		}
		if (printSettings.getFooterRight() == null) {
			printSettings.setFooterRight(configuration.getSheetFooterRight());
		}
	}

	protected void mergeAndSetRowLevels(XlsRowLevelInfo levelInfo, SortedMap<String, Boolean> rowLevelMap, int rowIndex)
	{
		if (rowLevelMap != null)
		{
			SortedMap<String, Integer> crtLevelMap = levelInfo.getLevelMap();

			for (Entry<String, Boolean> rowLevel : rowLevelMap.entrySet())
			{
				String level = rowLevel.getKey();
				Boolean isEndMarker = rowLevel.getValue();
				
				//check if this level group is already open
				if (crtLevelMap.containsKey(level))
				{
					//the level group is already open
					
					if (isEndMarker)
					{
						//the level group needs to be closed, together with all its child level groups
						setRowLevels(levelInfo, level);

						//clean up current level group and nested level groups as they were closed 
						for (Iterator<String> it = crtLevelMap.keySet().iterator(); it.hasNext();)
						{
							if (level.compareTo(it.next()) <= 0)
							{
								it.remove();
							}
						}
					}
				}
				else // if (!isEndMarker)  // FIXMEXLS we should not add level if it is an end marker
				{
					//the level group is not yet open

					//we check to see if this level is higher than existing levels
					if (crtLevelMap.size() > 0 && level.compareTo(crtLevelMap.firstKey()) < 0)
					{
						//the level is higher than existing levels, so it has to close them all
						setRowLevels(levelInfo, level);
						
						//clean up nested level groups as they were closed; the current one is not yet among them 
						for (Iterator<String> it = crtLevelMap.keySet().iterator(); it.hasNext();)
						{
							if (level.compareTo(it.next()) < 0)
							{
								it.remove();
							}
						}
					}
					
					//create the current level group
//					XlsRowLevelRange range = new XlsRowLevelRange();
//					range.setStartIndex(rowIndex);
					//range.setEndIndex(rowIndex);
					//range.setName(groupName);
					crtLevelMap.put(level, rowIndex);
				}
			}
		}

		levelInfo.setEndIndex(rowIndex);
	}

	@Override
	protected JRStyledText getStyledText(JRPrintText textElement) {
		JRStyledTextAttributeSelector selector = textElement.getMode() == ModeEnum.OPAQUE ? allSelector
				: noBackcolorSelector;
		return textElement.getFullStyledText(selector);
	}

	/**
	 *
	 */
	public static TextAlignHolder getTextAlignHolder(JRPrintText textElement) {
		HorizontalTextAlignEnum horizontalAlignment;
		VerticalTextAlignEnum verticalAlignment;
		RotationEnum rotation = textElement.getRotation();

		switch (textElement.getRotation()) {
		case LEFT: {
			switch (textElement.getHorizontalTextAlign()) {
			case JUSTIFIED: {
				verticalAlignment = VerticalTextAlignEnum.JUSTIFIED;
				break;
			}
			case RIGHT: {
				verticalAlignment = VerticalTextAlignEnum.TOP;
				break;
			}
			case CENTER: {
				verticalAlignment = VerticalTextAlignEnum.MIDDLE;
				break;
			}
			case LEFT:
			default: {
				verticalAlignment = VerticalTextAlignEnum.BOTTOM;
			}
			}

			switch (textElement.getVerticalTextAlign()) {
			case JUSTIFIED: {
				horizontalAlignment = HorizontalTextAlignEnum.JUSTIFIED;
				break;
			}
			case BOTTOM: {
				horizontalAlignment = HorizontalTextAlignEnum.RIGHT;
				break;
			}
			case MIDDLE: {
				horizontalAlignment = HorizontalTextAlignEnum.CENTER;
				break;
			}
			case TOP:
			default: {
				horizontalAlignment = HorizontalTextAlignEnum.LEFT;
			}
			}

			break;
		}
		case RIGHT: {
			switch (textElement.getHorizontalTextAlign()) {
			case JUSTIFIED: {
				verticalAlignment = VerticalTextAlignEnum.JUSTIFIED;
				break;
			}
			case RIGHT: {
				verticalAlignment = VerticalTextAlignEnum.BOTTOM;
				break;
			}
			case CENTER: {
				verticalAlignment = VerticalTextAlignEnum.MIDDLE;
				break;
			}
			case LEFT:
			default: {
				verticalAlignment = VerticalTextAlignEnum.TOP;
			}
			}

			switch (textElement.getVerticalTextAlign()) {
			case JUSTIFIED: {
				horizontalAlignment = HorizontalTextAlignEnum.JUSTIFIED;
				break;
			}
			case BOTTOM: {
				horizontalAlignment = HorizontalTextAlignEnum.LEFT;
				break;
			}
			case MIDDLE: {
				horizontalAlignment = HorizontalTextAlignEnum.CENTER;
				break;
			}
			case TOP:
			default: {
				horizontalAlignment = HorizontalTextAlignEnum.RIGHT;
			}
			}

			break;
		}
		case UPSIDE_DOWN:
		case NONE:
		default: {
			horizontalAlignment = textElement.getHorizontalTextAlign();
			verticalAlignment = textElement.getVerticalTextAlign();
		}
		}

		return new TextAlignHolder(horizontalAlignment, verticalAlignment, rotation);
	}

	/**
	 *
	 */
	protected int getImageBorderCorrection(JRPen pen) {
		float lineWidth = pen.getLineWidth() == null ? 0 : pen.getLineWidth();

		if (lineWidth > 0f) {
			if (lineWidth >= 2f) {
				return 2;
			}

			return 1;
		}

		return imageBorderFixEnabled ? 1 : 0;
	}

	/**
	 *
	 */
	protected String getSheetName(String sheetName, String defaultSheetName) {
		if (sheetName != null) {
			if (sheetNames != null && sheetNamesIndex < sheetNames.length) {
				sheetNames[sheetNamesIndex] = sheetName;
			}
			return getSheetName(sheetName);
		}
		return getSheetName(defaultSheetName);
	}

	protected String getSheetName(String sheetName) {
		if (sheetNames != null && sheetNamesIndex < sheetNames.length) {
			sheetName = sheetNames[sheetNamesIndex];
		}

		if (sheetName == null) {
			// no sheet name was specified or if it was null
			return DEFAULT_SHEET_NAME_PREFIX + (sheetIndex + 1);
		}

		// sheet name specified; assuming it is first occurrence
		int crtIndex = 1;
		String txtIndex = "";
		String validSheetName = sheetName.length() < 32 ? sheetName : sheetName.substring(0, 31);

		if (sheetNamesMap.containsKey(validSheetName)) {
			// sheet names must be unique; altering sheet name using number of occurrences
			crtIndex = sheetNamesMap.get(validSheetName) + 1;
			txtIndex = String.valueOf(crtIndex);
		}

		sheetNamesMap.put(validSheetName, crtIndex);

		String name = sheetName;
		if (txtIndex.length() > 0) {
			name += " " + txtIndex;
		}

		if (name.length() > 31) {
			name = (sheetName + " ").substring(0, 31 - txtIndex.length()) + txtIndex;
		}

		return name;
	}

	// Berechnungsvorschriften für die DIN Formate A, B, und C.
	// Die Angabe der Breite/Höhe erfolgt in [mm].

	protected final int calculateWidthForDinAN(int n) {
		return (int) (Math.pow(2.0, (-0.25 - (n / 2.0))) * 1000.0);
	}

	protected final int calculateHeightForDinAN(int n) {
		return (int) (Math.pow(2.0, (0.25 - (n / 2.0))) * 1000.0);
	}

	protected final int calculateWidthForDinBN(int n) {
		return (int) (Math.pow(2.0, -(n / 2.0)) * 1000.0);
	}

	protected final int calculateHeightForDinBN(int n) {
		return (int) (Math.pow(2.0, (0.5 - (n / 2.0))) * 1000.0);
	}

	protected final int calculateWidthForDinCN(int n) {
		return (int) (Math.pow(2.0, (-0.125 - (n / 2.0))) * 1000.0);
	}

	protected final int calculateHeightForDinCN(int n) {
		return (int) (Math.pow(2.0, (0.375 - (n / 2.0))) * 1000.0);
	}

	/**
	 * 
	 */
	protected boolean isWrapText(JRPrintElement element) {
		if (element.hasProperties()
				&& element.getPropertiesMap().containsProperty(XlsReportConfiguration.PROPERTY_WRAP_TEXT)) {
			// we make this test to avoid reaching the global default value of the property
			// directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getBooleanProperty(element, XlsReportConfiguration.PROPERTY_WRAP_TEXT,
					defaultWrapText);
		}
		return defaultWrapText;
	}

	/**
	 * 
	 */
	protected boolean isCellLocked(JRPrintElement element) {

		if (element.hasProperties()
				&& element.getPropertiesMap().containsProperty(XlsReportConfiguration.PROPERTY_CELL_LOCKED)) {
			// we make this test to avoid reaching the global default value of the property
			// directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getBooleanProperty(element, XlsReportConfiguration.PROPERTY_CELL_LOCKED,
					defaultCellLocked);
		}
		return defaultCellLocked;
	}

	/**
	 * 
	 */
	protected boolean isShrinkToFit(JRPrintElement element) {
		if (element.hasProperties()
				&& element.getPropertiesMap().containsProperty(XlsReportConfiguration.PROPERTY_SHRINK_TO_FIT)) {
			// we make this test to avoid reaching the global default value of the property
			// directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getBooleanProperty(element, XlsReportConfiguration.PROPERTY_SHRINK_TO_FIT,
					defaultShrinkToFit);
		}
		return defaultShrinkToFit;
	}

	/**
	 * 
	 */
	protected boolean isIgnoreTextFormatting(JRPrintElement element) {
		if (element.hasProperties() && element.getPropertiesMap()
				.containsProperty(XlsReportConfiguration.PROPERTY_IGNORE_TEXT_FORMATTING)) {
			// we make this test to avoid reaching the global default value of the property
			// directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getBooleanProperty(element,
					XlsReportConfiguration.PROPERTY_IGNORE_TEXT_FORMATTING,
					defaultIgnoreTextFormatting);
		}
		return defaultIgnoreTextFormatting;
	}

	/**
	 * 
	 */
	protected String getFormula(JRPrintText text) {
		String formula = JRPropertiesUtil.getOwnProperty(text, PROPERTY_CELL_FORMULA);
		if (formula != null) {
			formula = formula.trim();
			if (formula.startsWith("=")) {
				formula = formula.substring(1);
			}
		}
		return formula;
	}

	/**
	 * 
	 */
	protected void setSheetNames() {
		String[] sheetNamesArray = getCurrentItemConfiguration().getSheetNames();

		List<String> sheetNamesList = JRStringUtil.split(sheetNamesArray, "/");
		sheetNames = sheetNamesList == null ? null
				: (String[]) sheetNamesList.toArray(new String[sheetNamesList.size()]);
	}

	/**
	 * 
	 */
	protected boolean hasGlobalSheetNames()// FIXMEEXPORT check sheet names
	{
		Boolean globalSheetNames = null;

		boolean isOverrideHintsDefault = propertiesUtil
				.getBooleanProperty(ReportExportConfiguration.PROPERTY_EXPORT_CONFIGURATION_OVERRIDE_REPORT_HINTS);
		if (itemConfiguration != null && itemConfiguration.getSheetNames() != null) {
			boolean isExporterConfigOverrideHints = itemConfiguration.isOverrideHints() == null ? isOverrideHintsDefault
					: itemConfiguration.isOverrideHints();
			if (isExporterConfigOverrideHints) {
				globalSheetNames = true;
			}
		}

		if (globalSheetNames == null) {
			XlsReportConfiguration lcItemConfiguration = (XlsReportConfiguration) crtItem.getConfiguration();
			if (lcItemConfiguration != null && lcItemConfiguration.getSheetNames() != null) {
				boolean isItemConfigOverrideHints = lcItemConfiguration.isOverrideHints() == null
						? isOverrideHintsDefault
						: lcItemConfiguration.isOverrideHints();
				if (isItemConfigOverrideHints) {
					globalSheetNames = false;
				}
			}
		}

		if (globalSheetNames == null) {
			List<PropertySuffix> properties = JRPropertiesUtil.getProperties(getCurrentJasperPrint(),
					XlsReportConfiguration.PROPERTY_SHEET_NAMES_PREFIX);
			globalSheetNames = properties == null || properties.isEmpty();
		}

		return globalSheetNames;
	}

	/**
	 * 
	 */
	protected boolean isCellHidden(JRPrintElement element) {
		if (element.hasProperties()
				&& element.getPropertiesMap().containsProperty(XlsReportConfiguration.PROPERTY_CELL_HIDDEN)) {
			// we make this test to avoid reaching the global default value of the property
			// directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getBooleanProperty(element, XlsReportConfiguration.PROPERTY_CELL_HIDDEN,
					defaultCellHidden);
		}
		return defaultCellHidden;
	}

	/**
	 * 
	 */
	protected String getConvertedPattern(JRPrintText text, String pattern) {
		String convertedPattern = JRPropertiesUtil.getOwnProperty(text, PROPERTY_CELL_PATTERN);
		if (convertedPattern == null || convertedPattern.trim().length() == 0) {
			if (formatPatternsMap != null && formatPatternsMap.containsKey(pattern)) {
				return formatPatternsMap.get(pattern);
			}
			return pattern == null || pattern.trim().length() == 0 ? null : pattern;
		}
		return convertedPattern;
	}

	/**
	 * 
	 * @return the calculated column index
	 */
	protected int getColumnIndex(String columnName) {
		int index = -1;
		if (columnName != null) {
			String upperColumnName = columnName.toUpperCase();
			if (upperColumnName.matches("[A-Z]*")) {
				for (int i = 0; i < upperColumnName.length(); i++) {
					index += (upperColumnName.charAt(i) - 64) * (int) Math.pow(26, upperColumnName.length() - i - 1);
				}
			}
		}
		return index;
	}

	/**
	 * @deprecated Replaced by {@link JRStringUtil#getLetterNumeral(int, boolean)}.
	 */
	protected String getColumnName(int columnIndex) {
		return JRStringUtil.getLetterNumeral(columnIndex + 1, true);
	}

	protected void resetAutoFilters() {
		autoFilterStart = null;
		autoFilterEnd = null;
	}

	/**
	 * 
	 */
	protected boolean isUseTimeZone(JRPrintElement element) {
		if (element.hasProperties()
				&& element.getPropertiesMap().containsProperty(XlsReportConfiguration.PROPERTY_USE_TIMEZONE)) {
			// we make this test to avoid reaching the global default value of the property
			// directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getBooleanProperty(element, XlsReportConfiguration.PROPERTY_USE_TIMEZONE,
					defaultUseTimeZone);
		}
		return defaultUseTimeZone;
	}

	protected Date translateDateValue(JRPrintText text, Date value) {
		if (isUseTimeZone(text)) {
			// translate the date to the timezone used at fill time
			TimeZone tz = getTextTimeZone(text);
			value = JRDataUtils.translateToTimezone(value, tz);
		}
		return value;
	}

	protected boolean isValidScale(Integer scale) {
		return scale != null && scale > 9 && scale < 401;
	}

	protected Integer getMaxRowsPerSheet() {
		return getCurrentItemConfiguration().getMaxRowsPerSheet();
	}

	protected ExporterNature getNature() {
		return nature;
	}

	/**
	 *
	 * @param colIndex The 0-based integer column index
	 * @return The column name computed from the 0-based column index
	 */
	public static String getColumIndexName(int colIndex, int maxColIndex) {

		if (colIndex < 0) {
			throw new JRRuntimeException(EXCEPTION_MESSAGE_KEY_NEGATIVE_COLUMN_INDEX, new Object[] { colIndex });
		} else if (colIndex > maxColIndex) {
			throw new JRRuntimeException(EXCEPTION_MESSAGE_KEY_COLUMN_INDEX_BEYOND_LIMIT,
					new Object[] { colIndex, maxColIndex });
		} else if (colIndex < 26) {
			// first 26 column names are single letters
			return String.valueOf((char) (colIndex + 65));
		} else if (colIndex < 702) // 702 = 26 + 26^2
		{
			// next 626 (= 26^2) column names are 2-letter names
			return String.valueOf((char) (colIndex / 26 + 64)) + String.valueOf((char) (colIndex % 26 + 65));
		} else {
			// next 17576 (= 26^3) column names are 3-letter names;
			// anyway, the 0-based column index may not exceed maxColIndex value
			return String.valueOf((char) ((colIndex - 26) / 676 + 64))
					+ String.valueOf((char) (((colIndex - 26) % 676) / 26 + 65))
					+ String.valueOf((char) (colIndex % 26 + 65));
		}
	}

	protected void configureDefinedNames(ExporterNature exporterNature, JRPrintElement element) {
		if (exporterNature instanceof JRXlsAbstractExporterNature) {
			configureDefinedNames(((JRXlsAbstractExporterNature) exporterNature).getDefinedNames(element));
		}
	}

	protected void configureDefinedNames(PropertySuffix[] names) {
		if (names != null) {
			for (PropertySuffix definedName : names) {
				configureDefinedNames(definedName);
			}
		}
	}

	protected void configureDefinedNames(PropertySuffix propertySuffix) {
		if (propertySuffix != null) {
			String name = propertySuffix.getSuffix();
			String value = propertySuffix.getValue();
			if (name != null && name.trim().length() > 0 && value != null && value.length() > 0) {
				String[] valueScope = value.split(DEFAULT_DEFINED_NAME_SCOPE_SEPARATOR);
				if (valueScope[0] != null && valueScope[0].length() > 0) {
					String scope = valueScope.length > 1 ? valueScope[1] : DEFAULT_DEFINED_NAME_SCOPE;
					NameScope nameScope = new NameScope(name, scope);
					if (valueScope[0].startsWith("=")) {
						definedNamesMap.put(nameScope, valueScope[0].substring(1));
					} else {
						definedNamesMap.put(nameScope, valueScope[0]);
					}
				}
			}
		}
	}

	protected String toExcelName(String name) {
		if (name.isEmpty()) {
			// this is actually invalid, but let's leave it to POI to throw an exception
			return name;
		}

		char[] chars = name.toCharArray();
		StringBuilder escaped = null;

		// TODO? always prepend "_" to avoid invalid names like R/C/A1
		// valid character taken from the HSSFName.validateName method
		if (!Character.isLetter(chars[0]) && chars[0] != '_' && chars[0] != '\\') {
			escaped = new StringBuilder(chars.length * 4 / 3);
			escaped.append('_');
			escaped.append(Integer.toHexString(chars[0]));
		}

		for (int i = 1; i < chars.length; ++i) {
			if (Character.isLetterOrDigit(chars[i]) || chars[i] == '_' || chars[i] == '\\' || chars[i] == '.') {
				if (escaped != null) {
					escaped.append(chars[i]);
				}
			} else {
				if (escaped == null) {
					escaped = new StringBuilder(chars.length * 4 / 3);
					escaped.append(chars, 0, i);
				}

				escaped.append(Integer.toHexString(chars[i]));
			}
		}

		String excelName = escaped == null ? name : escaped.toString();
		if (excelName.length() > 255) {
			excelName = excelName.substring(0, 255);
		}
		// TODO? check for uniqueness
		return excelName;
	}

	// abstract methods

	protected abstract void openWorkbook(OutputStream os) throws JRException, IOException;

	protected abstract void closeSheet();

	protected abstract void closeWorkbook(OutputStream os) throws JRException, IOException;

	protected abstract void setColumnWidth(int col, int width, boolean autoFit);

	protected abstract void addRowBreak(int rowIndex);

	protected abstract void setFreezePane(int rowIndex, int colIndex);

	protected abstract void setSheetName(String sheetName);

	protected abstract void setAutoFilter(String autoFilterRange);

	protected abstract void setRowLevels(XlsRowLevelInfo levelInfo, String level);

	protected abstract void exportReportToStream(OutputStream os) throws JRException, IOException;

	protected abstract void exportEmptyReport() throws JRException, IOException;

}

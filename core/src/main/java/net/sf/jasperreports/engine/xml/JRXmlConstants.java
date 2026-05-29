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
package net.sf.jasperreports.engine.xml;



/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class JRXmlConstants
{
	
	/**
	 * The namespace used by the JRXML XML schema.
	 */
	public static final String JASPERREPORTS_NAMESPACE = 
		"http://jasperreports.sourceforge.net/jasperreports";
	
	/**
	 * The namespace used by the XML export schema.
	 */
	public static final String JASPERPRINT_NAMESPACE = 
		"http://jasperreports.sourceforge.net/jasperreports/print";
	
	/**
	 * The system location of the XML export schema.
	 */
	public static final String JASPERPRINT_XSD_SYSTEM_ID = 
		"http://jasperreports.sourceforge.net/xsd/jasperprint.xsd";

	/**
	 *	JasperDesignFactory associated constants
	 */
	public static final String ELEMENT_jasperReport = "jasperReport";
	public static final String ATTRIBUTE_name = "name";
	public static final String ATTRIBUTE_language = "language";
	public static final String ATTRIBUTE_columnCount = "columnCount";
	public static final String ATTRIBUTE_printOrder = "printOrder";
	public static final String ATTRIBUTE_columnDirection = "columnDirection";
	public static final String ATTRIBUTE_pageWidth = "pageWidth";
	public static final String ATTRIBUTE_pageHeight = "pageHeight";
	public static final String ATTRIBUTE_dpi = "dpi";
	public static final String ATTRIBUTE_orientation = "orientation";
	public static final String ATTRIBUTE_whenNoDataType = "whenNoDataType";
	public static final String ATTRIBUTE_sectionType = "sectionType";
	public static final String ATTRIBUTE_columnWidth = "columnWidth";
	public static final String ATTRIBUTE_columnSpacing = "columnSpacing";
	public static final String ATTRIBUTE_leftMargin = "leftMargin";
	public static final String ATTRIBUTE_rightMargin = "rightMargin";
	public static final String ATTRIBUTE_topMargin = "topMargin";
	public static final String ATTRIBUTE_bottomMargin = "bottomMargin";
	public static final String ATTRIBUTE_isTitleNewPage = "isTitleNewPage";
	public static final String ATTRIBUTE_isSummaryNewPage = "isSummaryNewPage";
	public static final String ATTRIBUTE_isSummaryWithPageHeaderAndFooter = "isSummaryWithPageHeaderAndFooter";
	public static final String ATTRIBUTE_isFloatColumnFooter = "isFloatColumnFooter";
	public static final String ATTRIBUTE_scriptletClass = "scriptletClass";
	public static final String ATTRIBUTE_formatFactoryClass = "formatFactoryClass";
	public static final String ATTRIBUTE_resourceBundle = "resourceBundle";
	public static final String ATTRIBUTE_whenResourceMissingType = "whenResourceMissingType";
	public static final String ATTRIBUTE_isIgnorePagination = "isIgnorePagination";

	public static final String ATTRIBUTE_value = "value";

	public static final String ELEMENT_import = "import";
	public static final String ELEMENT_background = "background";
	public static final String ELEMENT_title = "title";
	public static final String ELEMENT_pageHeader = "pageHeader";
	public static final String ELEMENT_columnHeader = "columnHeader";
	public static final String ELEMENT_detail = "detail";
	public static final String ELEMENT_columnFooter = "columnFooter";
	public static final String ELEMENT_pageFooter = "pageFooter";
	public static final String ELEMENT_lastPageFooter = "lastPageFooter";
	public static final String ELEMENT_summary = "summary";
	public static final String ELEMENT_noData = "noData";
	public static final String ELEMENT_property = "property";
	public static final String ELEMENT_propertyExpression = "propertyExpression";

	public static final String ELEMENT_bookmark = "bookmark";
	public static final String ATTRIBUTE_pageIndex = "pageIndex";
	public static final String ATTRIBUTE_elementAddress = "elementAddress";

	public static final String ELEMENT_page = "page";

	/**
	 * JasperPrintFactory associated constants
	 */
	public static final String ELEMENT_jasperPrint = "jasperPrint";
	public static final String ATTRIBUTE_locale = "locale";
	public static final String ATTRIBUTE_timezone = "timezone";

	/**
	 * JROriginFactory associated constants
	 */
	public static final String ELEMENT_origin = "origin";
	public static final String ATTRIBUTE_report = "report";
	public static final String ATTRIBUTE_group = "group";
	public static final String ATTRIBUTE_band = "band";

	/**
	 * JRBandFactory associated constants
	 */
	public static final String ELEMENT_band = "band";
	public static final String ELEMENT_printWhenExpression = "printWhenExpression";

	public static final String ATTRIBUTE_height = "height";
	public static final String ATTRIBUTE_isSplitAllowed = "isSplitAllowed";
	public static final String ATTRIBUTE_splitType = "splitType";

	/**
	 * JRPartFactory associated constants
	 */
	public static final String ELEMENT_part = "part";
	public static final String ELEMENT_partNameExpression = "partNameExpression";

	/**
	 * JRPenFactory associated constants
	 */
	public static final String ELEMENT_pen = "pen";

	public static final String ATTRIBUTE_lineWidth = "lineWidth";
	public static final String ATTRIBUTE_lineStyle = "lineStyle";
	public static final String ATTRIBUTE_lineColor = "lineColor";

	/**
	 * JRBoxFactory associated constants
	 */
	public static final String ELEMENT_box = "box";
	public static final String ELEMENT_topPen = "topPen";
	public static final String ELEMENT_leftPen = "leftPen";
	public static final String ELEMENT_bottomPen = "bottomPen";
	public static final String ELEMENT_rightPen = "rightPen";

	public static final String ATTRIBUTE_border = "border";
	public static final String ATTRIBUTE_borderColor = "borderColor";
	public static final String ATTRIBUTE_padding = "padding";
	public static final String ATTRIBUTE_topBorder = "topBorder";
	public static final String ATTRIBUTE_topBorderColor = "topBorderColor";
	public static final String ATTRIBUTE_topPadding = "topPadding";
	public static final String ATTRIBUTE_leftBorder = "leftBorder";
	public static final String ATTRIBUTE_leftBorderColor = "leftBorderColor";
	public static final String ATTRIBUTE_leftPadding = "leftPadding";
	public static final String ATTRIBUTE_bottomBorder = "bottomBorder";
	public static final String ATTRIBUTE_bottomBorderColor = "bottomBorderColor";
	public static final String ATTRIBUTE_bottomPadding = "bottomPadding";
	public static final String ATTRIBUTE_rightBorder = "rightBorder";
	public static final String ATTRIBUTE_rightBorderColor = "rightBorderColor";
	public static final String ATTRIBUTE_rightPadding = "rightPadding";

	/**
	 * JRParagraphFactory associated constants
	 */
	public static final String ELEMENT_paragraph = "paragraph";
	public static final String ATTRIBUTE_lineSpacing = "lineSpacing";
	public static final String ATTRIBUTE_lineSpacingSize = "lineSpacingSize";
	public static final String ATTRIBUTE_firstLineIndent = "firstLineIndent";
	public static final String ATTRIBUTE_leftIndent = "leftIndent";
	public static final String ATTRIBUTE_rightIndent = "rightIndent";
	public static final String ATTRIBUTE_spacingBefore = "spacingBefore";
	public static final String ATTRIBUTE_spacingAfter = "spacingAfter";
	public static final String ATTRIBUTE_tabStopWidth = "tabStopWidth";
	public static final String ELEMENT_tabStop = "tabStop";
	public static final String ATTRIBUTE_alignment = "alignment";

	/**
	 * JRBreakFactory associated constants
	 */
	public static final String ELEMENT_break = "break";

	public static final String ATTRIBUTE_type = "type";

	/**
	 * JRChartFactory associated constants
	 */
	public static final String ELEMENT_chart = "chart";
	public static final String ELEMENT_chartTitle = "chartTitle";
	public static final String ELEMENT_titleExpression = "titleExpression";
	public static final String ELEMENT_chartSubtitle = "chartSubtitle";
	public static final String ELEMENT_subtitleExpression = "subtitleExpression";
	public static final String ELEMENT_chartLegend = "chartLegend";

	public static final String ELEMENT_pieChart = "pieChart";
	public static final String ELEMENT_pie3DChart = "pie3DChart";
	public static final String ELEMENT_barChart = "barChart";
	public static final String ELEMENT_bar3DChart = "bar3DChart";
	public static final String ELEMENT_bubbleChart = "bubbleChart";
	public static final String ELEMENT_stackedBarChart = "stackedBarChart";
	public static final String ELEMENT_stackedBar3DChart = "stackedBar3DChart";
	public static final String ELEMENT_lineChart = "lineChart";
	public static final String ELEMENT_highLowChart = "highLowChart";
	public static final String ELEMENT_candlestickChart = "candlestickChart";
	public static final String ELEMENT_areaChart = "areaChart";
	public static final String ELEMENT_scatterChart = "scatterChart";
	public static final String ELEMENT_timeSeriesChart = "timeSeriesChart";
	public static final String ELEMENT_xyAreaChart = "xyAreaChart";
	public static final String ELEMENT_xyBarChart = "xyBarChart";
	public static final String ELEMENT_xyLineChart = "xyLineChart";
	public static final String ELEMENT_meterChart = "meterChart";
	public static final String ELEMENT_thermometerChart = "thermometerChart";
	public static final String ELEMENT_multiAxisChart = "multiAxisChart";
	public static final String ELEMENT_stackedAreaChart = "stackedAreaChart";
	public static final String ELEMENT_ganttChart = "ganttChart";

	public static final String ATTRIBUTE_isShowLegend = "isShowLegend";
	public static final String ATTRIBUTE_evaluationTime = "evaluationTime";
	public static final String ATTRIBUTE_evaluationGroup = "evaluationGroup";
	public static final String ATTRIBUTE_bookmarkLevel = "bookmarkLevel";
	public static final String ATTRIBUTE_customizerClass = "customizerClass";
	public static final String ATTRIBUTE_renderType = "renderType";
	public static final String ATTRIBUTE_theme = "theme";
	
	public static final String ELEMENT_bookmarkLevelExpression = "bookmarkLevelExpression";


	/**
	 * JRChartAxisFormatFactory associated constants
	 */
	public static final String ELEMENT_axisFormat = "axisFormat";
	public static final String ELEMENT_labelFont = "labelFont";
	public static final String ELEMENT_tickLabelFont = "tickLabelFont";
	public static final String ATTRIBUTE_labelColor = "labelColor";
	public static final String ATTRIBUTE_tickLabelColor = "tickLabelColor";
	public static final String ATTRIBUTE_tickLabelMask = "tickLabelMask";
	public static final String ATTRIBUTE_verticalTickLabels = "verticalTickLabels";
	public static final String ATTRIBUTE_axisLineColor = "axisLineColor";

	/**
	 * JRChartLegendFactory associated constants
	 */
	public static final String ATTRIBUTE_textColor = "textColor";
	public static final String ATTRIBUTE_backgroundColor = "backgroundColor";

	/**
	 * JRChartTitleFactory associated constants
	 */
	public static final String ATTRIBUTE_position = "position";
	public static final String ATTRIBUTE_color = "color";

	/**
	 * JRChartPlotFactory associated constants
	 */
	public static final String ELEMENT_plot = "plot";
	public static final String ELEMENT_piePlot = "piePlot";
	public static final String ELEMENT_pie3DPlot = "pie3DPlot";
	public static final String ELEMENT_barPlot = "barPlot";
	public static final String ELEMENT_bubblePlot = "bubblePlot";
	public static final String ELEMENT_linePlot = "linePlot";
	public static final String ELEMENT_timeSeriesPlot = "timeSeriesPlot";
	public static final String ELEMENT_bar3DPlot = "bar3DPlot";
	public static final String ELEMENT_highLowPlot = "highLowPlot";
	public static final String ELEMENT_candlestickPlot = "candlestickPlot";
	public static final String ELEMENT_areaPlot = "areaPlot";
	public static final String ELEMENT_scatterPlot = "scatterPlot";
	public static final String ELEMENT_multiAxisPlot = "multiAxisPlot";

	public static final String ELEMENT_valueDisplay = "valueDisplay";
	public static final String ELEMENT_itemLabel = "itemLabel";
	public static final String ELEMENT_dataRange = "dataRange";
	public static final String ELEMENT_meterInterval = "meterInterval";
	public static final String ELEMENT_categoryAxisFormat = "categoryAxisFormat";
	public static final String ELEMENT_valueAxisFormat = "valueAxisFormat";
	public static final String ELEMENT_xAxisFormat = "xAxisFormat";
	public static final String ELEMENT_yAxisFormat = "yAxisFormat";
	public static final String ELEMENT_timeAxisFormat = "timeAxisFormat";

	public static final String ELEMENT_lowExpression = "lowExpression";
	public static final String ELEMENT_highExpression = "highExpression";
	public static final String ELEMENT_categoryAxisLabelExpression = "categoryAxisLabelExpression";
	public static final String ELEMENT_valueAxisLabelExpression = "valueAxisLabelExpression";
	public static final String ELEMENT_domainAxisMinValueExpression = "domainAxisMinValueExpression";
	public static final String ELEMENT_domainAxisMaxValueExpression = "domainAxisMaxValueExpression";
	public static final String ELEMENT_rangeAxisMinValueExpression = "rangeAxisMinValueExpression";
	public static final String ELEMENT_rangeAxisMaxValueExpression = "rangeAxisMaxValueExpression";
	public static final String ELEMENT_xAxisLabelExpression = "xAxisLabelExpression";
	public static final String ELEMENT_yAxisLabelExpression = "yAxisLabelExpression";
	public static final String ELEMENT_timeAxisLabelExpression = "timeAxisLabelExpression";
	public static final String ELEMENT_taskExpression = "taskExpression";
	public static final String ELEMENT_subtaskExpression = "subtaskExpression";
	public static final String ELEMENT_percentExpression = "percentExpression";

	public static final String ATTRIBUTE_backgroundAlpha = "backgroundAlpha";
	public static final String ATTRIBUTE_foregroundAlpha = "foregroundAlpha";
	public static final String ATTRIBUTE_labelRotation = "labelRotation";

	public static final String ATTRIBUTE_mask = "mask";
	public static final String ATTRIBUTE_label = "label";
	public static final String ATTRIBUTE_alpha = "alpha";
	public static final String ATTRIBUTE_depthFactor = "depthFactor";
	public static final String ATTRIBUTE_isShowLabels = "isShowLabels";
	public static final String ATTRIBUTE_isShowTickLabels = "isShowTickLabels";
	public static final String ATTRIBUTE_scaleType = "scaleType";
	public static final String ATTRIBUTE_isShowTickMarks = "isShowTickMarks";
	public static final String ATTRIBUTE_isShowLines = "isShowLines";
	public static final String ATTRIBUTE_isShowShapes = "isShowShapes";
	public static final String ATTRIBUTE_xOffset = "xOffset";
	public static final String ATTRIBUTE_yOffset = "yOffset";
	public static final String ATTRIBUTE_isShowOpenTicks = "isShowOpenTicks";
	public static final String ATTRIBUTE_isShowCloseTicks = "isShowCloseTicks";
	public static final String ATTRIBUTE_isShowVolume = "isShowVolume";
	public static final String ATTRIBUTE_isCircular = "isCircular";
	public static final String ATTRIBUTE_labelFormat = "labelFormat";
	public static final String ATTRIBUTE_legendLabelFormat = "legendLabelFormat";

	/**
	 * JRSeriesColorFactory associated constants
	 */
	public static final String ELEMENT_seriesColor = "seriesColor";

	public static final String ATTRIBUTE_seriesOrder = "seriesOrder";

	/**
	 * JRXySeries associated constants
	 */
	public static final String ATTRIBUTE_autoSort = "autoSort";

	/**
	 * JRConditionalStyleFactory associated constants
	 */
	public static final String ELEMENT_conditionalStyle = "conditionalStyle";
	public static final String ELEMENT_conditionExpression = "conditionExpression";

	/**
	 * JRStyleFactory associated constants
	 */
	public static final String ELEMENT_style = "style";
	public static final String ELEMENT_styleExpression = "styleExpression";

	public static final String ATTRIBUTE_isDefault = "isDefault";
	public static final String ATTRIBUTE_mode = "mode";
	public static final String ATTRIBUTE_forecolor = "forecolor";
	public static final String ATTRIBUTE_backcolor = "backcolor";
	public static final String ATTRIBUTE_style = "style";
	public static final String ATTRIBUTE_origin = "origin";
	public static final String ATTRIBUTE_srcId = "srcId";
	public static final String ATTRIBUTE_printId = "printId";

	public static final String ATTRIBUTE_radius = "radius";

	// these are inherited by both images and texts.

	public static final String ATTRIBUTE_rotation = "rotation";
	public static final String ATTRIBUTE_isStyledText = "isStyledText";
	public static final String ATTRIBUTE_markup = "markup";
	public static final String ATTRIBUTE_pattern = "pattern";
	public static final String ATTRIBUTE_isBlankWhenNull = "isBlankWhenNull";

	public static final String ATTRIBUTE_fontSize = "fontSize";

	/**
	 * JRDatasetFactory associated constants
	 */
	public static final String ELEMENT_subDataset = "subDataset";
	public static final String ELEMENT_filterExpression = "filterExpression";

	/**
	 * JRDatasetRunFactory associated constants
	 */
	public static final String ELEMENT_datasetRun = "datasetRun";

	public static final String ELEMENT_parametersMapExpression = "parametersMapExpression";
	public static final String ELEMENT_connectionExpression = "connectionExpression";
	public static final String ELEMENT_dataSourceExpression = "dataSourceExpression";

	public static final String ATTRIBUTE_subDataset = "subDataset";

	/**
	 * JRDatasetRunParameterExpressionFactory associated constants
	 */
	public static final String ELEMENT_datasetParameterExpression = "datasetParameterExpression";

	/**
	 * JRDatasetRunParameterFactory associated constants
	 */
	public static final String ELEMENT_datasetParameter = "datasetParameter";

	/**
	 * JRElementDatasetFactory associated constants
	 */
	public static final String ELEMENT_dataset = "dataset";
	public static final String ELEMENT_categoryDataset = "categoryDataset";
	public static final String ELEMENT_timeSeriesDataset = "timeSeriesDataset";
	public static final String ELEMENT_timePeriodDataset = "timePeriodDataset";
	public static final String ELEMENT_xyzDataset = "xyzDataset";
	public static final String ELEMENT_xyDataset = "xyDataset";
	public static final String ELEMENT_pieDataset = "pieDataset";
	public static final String ELEMENT_valueDataset = "valueDataset";
	public static final String ELEMENT_highLowDataset = "highLowDataset";
	public static final String ELEMENT_ganttDataset = "ganttDataset";

	public static final String ELEMENT_pieSeries = "pieSeries";
	public static final String ELEMENT_categorySeries = "categorySeries";
	public static final String ELEMENT_xyzSeries = "xyzSeries";
	public static final String ELEMENT_xySeries = "xySeries";
	public static final String ELEMENT_timeSeries = "timeSeries";
	public static final String ELEMENT_timePeriodSeries = "timePeriodSeries";
	public static final String ELEMENT_ganttSeries = "ganttSeries";

	public static final String ELEMENT_incrementWhenExpression = "incrementWhenExpression";
	public static final String ELEMENT_keyExpression = "keyExpression";
	public static final String ELEMENT_valueExpression = "valueExpression";
	public static final String ELEMENT_labelExpression = "labelExpression";
	public static final String ELEMENT_otherKeyExpression = "otherKeyExpression";
	public static final String ELEMENT_otherLabelExpression = "otherLabelExpression";
	public static final String ELEMENT_seriesExpression = "seriesExpression";
	public static final String ELEMENT_categoryExpression = "categoryExpression";
	public static final String ELEMENT_xValueExpression = "xValueExpression";
	public static final String ELEMENT_yValueExpression = "yValueExpression";
	public static final String ELEMENT_zValueExpression = "zValueExpression";
	public static final String ELEMENT_timePeriodExpression = "timePeriodExpression";
	public static final String ELEMENT_startDateExpression = "startDateExpression";
	public static final String ELEMENT_endDateExpression = "endDateExpression";
	public static final String ELEMENT_dateExpression = "dateExpression";
	public static final String ELEMENT_openExpression = "openExpression";
	public static final String ELEMENT_closeExpression = "closeExpression";
	public static final String ELEMENT_volumeExpression = "volumeExpression";

	public static final String ATTRIBUTE_maxCount = "maxCount";
	public static final String ATTRIBUTE_minPercentage = "minPercentage";
	public static final String ATTRIBUTE_timePeriod = "timePeriod";

	/**
	 * JRElementFactory associated constants
	 */
	public static final String ELEMENT_reportElement = "reportElement";

	public static final String ATTRIBUTE_uuid = "uuid";
	public static final String ATTRIBUTE_key = "key";
	public static final String ATTRIBUTE_positionType = "positionType";
	public static final String ATTRIBUTE_stretchType = "stretchType";
	public static final String ATTRIBUTE_isPrintRepeatedValues = "isPrintRepeatedValues";
	public static final String ATTRIBUTE_x = "x";
	public static final String ATTRIBUTE_y = "y";
	public static final String ATTRIBUTE_width = "width";
	public static final String ATTRIBUTE_isRemoveLineWhenBlank = "isRemoveLineWhenBlank";
	public static final String ATTRIBUTE_isPrintInFirstWholeBand = "isPrintInFirstWholeBand";
	public static final String ATTRIBUTE_isPrintWhenDetailOverflows = "isPrintWhenDetailOverflows";
	public static final String ATTRIBUTE_printWhenGroupChanges = "printWhenGroupChanges";

	/**
	 * JRElementGroupFactory associated constants
	 */
	public static final String ELEMENT_elementGroup = "elementGroup";

	/**
	 * JREllipseFactory associated constants
	 */
	public static final String ELEMENT_ellipse = "ellipse";

	/**
	 * JRFieldFactory associated constants
	 */
	public static final String ELEMENT_field = "field";
	public static final String ELEMENT_fieldDescription = "fieldDescription";

	public static final String ATTRIBUTE_class = "class";

	public static final String ATTRIBUTE_nestedType = "nestedType";

	/**
	 * JRFieldFactory associated constants
	 */
	public static final String ELEMENT_font = "font";

	public static final String ATTRIBUTE_reportFont = "reportFont";
	public static final String ATTRIBUTE_fontName = "fontName";
	public static final String ATTRIBUTE_isBold = "isBold";
	public static final String ATTRIBUTE_isItalic = "isItalic";
	public static final String ATTRIBUTE_isUnderline = "isUnderline";
	public static final String ATTRIBUTE_isStrikeThrough = "isStrikeThrough";
	public static final String ATTRIBUTE_size = "size";
	public static final String ATTRIBUTE_pdfFontName = "pdfFontName";
	public static final String ATTRIBUTE_pdfEncoding = "pdfEncoding";
	public static final String ATTRIBUTE_isPdfEmbedded = "isPdfEmbedded";

	/**
	 * JRFrameFactory associated constants
	 */
	public static final String ELEMENT_frame = "frame";
	public static final String ATTRIBUTE_borderSplitType = "borderSplitType";

	/**
	 * JRGraphicElementFactory associated constants
	 */
	public static final String ELEMENT_graphicElement = "graphicElement";

	public static final String ATTRIBUTE_pen = "pen";
	public static final String ATTRIBUTE_fill = "fill";

	/**
	 * JRGroupFactory associated constants
	 */
	public static final String ELEMENT_group = "group";
	public static final String ELEMENT_groupExpression = "groupExpression";
	public static final String ELEMENT_groupHeader = "groupHeader";
	public static final String ELEMENT_groupFooter = "groupFooter";

	public static final String ATTRIBUTE_isStartNewColumn = "isStartNewColumn";
	public static final String ATTRIBUTE_isStartNewPage = "isStartNewPage";
	public static final String ATTRIBUTE_isResetPageNumber = "isResetPageNumber";
	public static final String ATTRIBUTE_isReprintHeaderOnEachPage = "isReprintHeaderOnEachPage";
	public static final String ATTRIBUTE_isReprintHeaderOnEachColumn = "isReprintHeaderOnEachColumn";
	public static final String ATTRIBUTE_minHeightToStartNewPage = "minHeightToStartNewPage";
	public static final String ATTRIBUTE_minDetailsToStartFromTop = "minDetailsToStartFromTop";
	public static final String ATTRIBUTE_footerPosition = "footerPosition";
	public static final String ATTRIBUTE_keepTogether = "keepTogether";
	public static final String ATTRIBUTE_preventOrphanFooter = "preventOrphanFooter";

	/**
	 * JRHyperlinkFactory associated constants
	 */
	public static final String ELEMENT_hyperlinkTooltipExpression = "hyperlinkTooltipExpression";
	public static final String ELEMENT_sectionHyperlink = "sectionHyperlink";
	public static final String ELEMENT_otherSectionHyperlink = "otherSectionHyperlink";
	public static final String ELEMENT_itemHyperlink = "itemHyperlink";
	public static final String ELEMENT_anchorNameExpression = "anchorNameExpression";
	public static final String ELEMENT_hyperlinkReferenceExpression = "hyperlinkReferenceExpression";
	public static final String ELEMENT_hyperlinkWhenExpression = "hyperlinkWhenExpression";
	public static final String ELEMENT_hyperlinkAnchorExpression = "hyperlinkAnchorExpression";
	public static final String ELEMENT_hyperlinkPageExpression = "hyperlinkPageExpression";

	public static final String ATTRIBUTE_hyperlinkType = "hyperlinkType";
	public static final String ATTRIBUTE_hyperlinkTarget = "hyperlinkTarget";

	/**
	 * JRHyperlinkFactory associated constants
	 */
	public static final String ELEMENT_hyperlinkParameterExpression = "hyperlinkParameterExpression";

	/**
	 * JRHyperlinkParameterFactory associated constants
	 */
	public static final String ELEMENT_hyperlinkParameter = "hyperlinkParameter";

	/**
	 * JRImageFactory associated constants
	 */
	public static final String ELEMENT_image = "image";
	public static final String ELEMENT_imageSource = "imageSource";
	public static final String ELEMENT_imageExpression = "imageExpression";

	public static final String ATTRIBUTE_scaleImage = "scaleImage";
	public static final String ATTRIBUTE_hAlign = "hAlign";
	public static final String ATTRIBUTE_hTextAlign = "hTextAlign";
	public static final String ATTRIBUTE_hImageAlign = "hImageAlign";
	public static final String ATTRIBUTE_vAlign = "vAlign";
	public static final String ATTRIBUTE_vTextAlign = "vTextAlign";
	public static final String ATTRIBUTE_vImageAlign = "vImageAlign";
	public static final String ATTRIBUTE_isUsingCache = "isUsingCache";
	public static final String ATTRIBUTE_isLazy = "isLazy";
	public static final String ATTRIBUTE_onErrorType = "onErrorType";
	
	public static final String ATTRIBUTE_runToBottom = "runToBottom";
	
	public static final String ATTRIBUTE_overflowType = "overflowType";

	/**
	 * JRLineFactory associated constants
	 */
	public static final String ELEMENT_line = "line";

	public static final String ATTRIBUTE_direction = "direction";

	/**
	 * JRScriptletFactory associated constants
	 */
	public static final String ELEMENT_scriptlet = "scriptlet";
	public static final String ELEMENT_scriptletDescription = "scriptletDescription";

	/**
	 * JRParameterFactory associated constants
	 */
	public static final String ELEMENT_parameter = "parameter";
	public static final String ELEMENT_parameterDescription = "parameterDescription";
	public static final String ELEMENT_defaultValueExpression = "defaultValueExpression";

	public static final String ATTRIBUTE_isForPrompting = "isForPrompting";

	/**
	 * JRPrintHyperlinkParameterValueFactory associated constants
	 */
	public static final String ELEMENT_hyperlinkParameterValue = "hyperlinkParameterValue";

	/**
	 * JRPrintImageFactory associated constants
	 */
	public static final String ATTRIBUTE_anchorName = "anchorName";
	public static final String ATTRIBUTE_hyperlinkReference = "hyperlinkReference";
	public static final String ATTRIBUTE_hyperlinkAnchor = "hyperlinkAnchor";
	public static final String ATTRIBUTE_hyperlinkPage = "hyperlinkPage";
	public static final String ATTRIBUTE_hyperlinkTooltip = "hyperlinkTooltip";

	/**
	 * JRPrintImageSourceFactory associated constants
	 */
	public static final String ATTRIBUTE_isEmbedded = "isEmbedded";

	/**
	 * JRPrintTextFactory associated constants
	 */
	public static final String ATTRIBUTE_textAlignment = "textAlignment";
	public static final String ATTRIBUTE_verticalAlignment = "verticalAlignment";
	public static final String ATTRIBUTE_runDirection = "runDirection";
	public static final String ATTRIBUTE_horizontalPosition = "horizontalPosition";
	public static final String ATTRIBUTE_textHeight = "textHeight";
	public static final String ATTRIBUTE_lineSpacingFactor = "lineSpacingFactor";
	public static final String ATTRIBUTE_leadingOffset = "leadingOffset";
	public static final String ATTRIBUTE_averageCharWidth = "averageCharWidth";
	public static final String ATTRIBUTE_valueClass = "valueClass";

	/**
	 * JRQueryFactory associated constants
	 */
	public static final String ELEMENT_queryString = "queryString";

	/**
	 * JRRectangleFactory associated constants
	 */
	public static final String ELEMENT_rectangle = "rectangle";

	/**
	 * JRSortFieldFactory associated constants
	 */
	public static final String ELEMENT_sortField = "sortField";

	public static final String ATTRIBUTE_order = "order";

	/**
	 * JRStaticTextFactory associated constants
	 */
	public static final String ELEMENT_staticText = "staticText";
	public static final String ELEMENT_text = "text";
	public static final String ELEMENT_textContent = "textContent";
	public static final String ATTRIBUTE_truncateIndex = "truncateIndex";
	public static final String ELEMENT_textTruncateSuffix = "textTruncateSuffix";
	public static final String ELEMENT_lineBreakOffsets = "lineBreakOffsets";
	public static final String LINE_BREAK_OFFSET_SEPARATOR = ",";

	/**
	 * JRSubreportExpressionFactory associated constants
	 */
	public static final String ELEMENT_subreportExpression = "subreportExpression";

	/**
	 * JRSubreportFactory associated constants
	 */
	public static final String ELEMENT_subreport = "subreport";

	/**
	 * JRSubreportParameterFactory associated constants
	 */
	public static final String ELEMENT_subreportParameter = "subreportParameter";
	public static final String ELEMENT_subreportParameterExpression = "subreportParameterExpression";

	/**
	 * JRSubreportReturnValueFactory associated constants
	 */
	public static final String ELEMENT_returnValue = "returnValue";

	public static final String ATTRIBUTE_subreportVariable = "subreportVariable";
	public static final String ATTRIBUTE_toVariable = "toVariable";
	public static final String ATTRIBUTE_fromVariable = "fromVariable";
	public static final String ELEMENT_expression = "expression";

	/**
	 * JRTextElementFactory associated constants
	 */
	public static final String ELEMENT_textElement = "textElement";

	/**
	 * JRTextFieldExpressionFactory associated constants
	 */
	public static final String ELEMENT_textFieldExpression = "textFieldExpression";
	public static final String ELEMENT_patternExpression = "patternExpression";

	/**
	 * JRTextFieldFactory  associated constants
	 */
	public static final String ELEMENT_textField = "textField";

	public static final String ATTRIBUTE_isStretchWithOverflow = "isStretchWithOverflow";
	public static final String ATTRIBUTE_textAdjust = "textAdjust";

	/**
	 * JRVariableFactory  associated constants
	 */
	public static final String ELEMENT_variable = "variable";
	public static final String ELEMENT_variableDescription = "variableDescription";
	public static final String ELEMENT_variableExpression = "variableExpression";
	public static final String ELEMENT_initialValueExpression = "initialValueExpression";

	public static final String ATTRIBUTE_resetType = "resetType";
	public static final String ATTRIBUTE_resetGroup = "resetGroup";
	public static final String ATTRIBUTE_incrementType = "incrementType";
	public static final String ATTRIBUTE_incrementGroup = "incrementGroup";
	public static final String ATTRIBUTE_calculation = "calculation";
	public static final String ATTRIBUTE_incrementerFactoryClass = "incrementerFactoryClass";

	/**
	 * JRReportFontFactory  associated constants
	 */
	public static final String ELEMENT_reportFont = "reportFont";

	public static final String ELEMENT_template = "template";


	public static final String TEMPLATE_ELEMENT_ROOT = "jasperTemplate";
	public static final String TEMPLATE_ELEMENT_INCLUDED_TEMPLATE = "template";
	
	public static final String ELEMENT_componentElement = "componentElement";
	
	public static final String ELEMENT_genericElement = "genericElement";
	public static final String ELEMENT_genericElementType = "genericElementType";
	public static final String ELEMENT_genericElementParameter = 
		"genericElementParameter";
	public static final String ELEMENT_genericElementParameter_valueExpression = 
		"valueExpression";
	public static final String ATTRIBUTE_namespace = "namespace";
	public static final String ATTRIBUTE_skipWhenNull = "skipWhenNull";

	public static final String ELEMENT_genericElementParameterValue = 
		"genericElementParameterValue";


	public static final String ELEMENT_multiAxisData = "multiAxisData";
	public static final String ELEMENT_multiAxisDataset = "multiAxisDataset";
	public static final String ELEMENT_dataAxis = "dataAxis";
	public static final String ATTRIBUTE_axis = "axis";
	public static final String ELEMENT_axisLevel = "axisLevel";
	public static final String ELEMENT_axisLevelBucket = "axisLevelBucket";
	public static final String ELEMENT_MULTI_AXIS_BUCKET_LABEL_EXPRESSION = "labelExpression";
	public static final String ELEMENT_bucketProperty = "bucketProperty";
	public static final String ELEMENT_multiAxisMeasure = "multiAxisMeasure";
	
	public static final String ATTRIBUTE_contentsPosition = "contentsPosition";
	

	private JRXmlConstants()
	{
	}
	
}

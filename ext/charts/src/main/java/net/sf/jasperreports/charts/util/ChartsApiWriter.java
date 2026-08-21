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
package net.sf.jasperreports.charts.util;

import java.awt.Color;
import java.util.List;
import java.util.SortedSet;

import net.sf.jasperreports.charts.ChartVisitor;
import net.sf.jasperreports.charts.JRAreaPlot;
import net.sf.jasperreports.charts.JRBarPlot;
import net.sf.jasperreports.charts.JRBubblePlot;
import net.sf.jasperreports.charts.JRCandlestickPlot;
import net.sf.jasperreports.charts.JRCategoryDataset;
import net.sf.jasperreports.charts.JRCategorySeries;
import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.charts.JRChartAxis;
import net.sf.jasperreports.charts.JRChartDataset;
import net.sf.jasperreports.charts.JRChartPlot;
import net.sf.jasperreports.charts.JRChartPlot.JRSeriesColor;
import net.sf.jasperreports.charts.JRDataRange;
import net.sf.jasperreports.charts.JRGanttDataset;
import net.sf.jasperreports.charts.JRGanttSeries;
import net.sf.jasperreports.charts.JRHighLowDataset;
import net.sf.jasperreports.charts.JRHighLowPlot;
import net.sf.jasperreports.charts.JRItemLabel;
import net.sf.jasperreports.charts.JRLinePlot;
import net.sf.jasperreports.charts.JRMeterPlot;
import net.sf.jasperreports.charts.JRMultiAxisPlot;
import net.sf.jasperreports.charts.JRPieDataset;
import net.sf.jasperreports.charts.JRPiePlot;
import net.sf.jasperreports.charts.JRPieSeries;
import net.sf.jasperreports.charts.JRScatterPlot;
import net.sf.jasperreports.charts.JRThermometerPlot;
import net.sf.jasperreports.charts.JRTimePeriodDataset;
import net.sf.jasperreports.charts.JRTimePeriodSeries;
import net.sf.jasperreports.charts.JRTimeSeries;
import net.sf.jasperreports.charts.JRTimeSeriesDataset;
import net.sf.jasperreports.charts.JRTimeSeriesPlot;
import net.sf.jasperreports.charts.JRValueDataset;
import net.sf.jasperreports.charts.JRValueDisplay;
import net.sf.jasperreports.charts.JRXyDataset;
import net.sf.jasperreports.charts.JRXySeries;
import net.sf.jasperreports.charts.JRXyzDataset;
import net.sf.jasperreports.charts.JRXyzSeries;
import net.sf.jasperreports.charts.design.JRDesignChart;
import net.sf.jasperreports.charts.type.PlotOrientationEnum;
import net.sf.jasperreports.charts.type.TimePeriodEnum;
import net.sf.jasperreports.engine.JRAnchor;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;
import net.sf.jasperreports.engine.type.HyperlinkTargetEnum;
import net.sf.jasperreports.engine.type.HyperlinkTypeEnum;
import net.sf.jasperreports.engine.util.JRApiWriter;
import net.sf.jasperreports.engine.util.JRApiWriterVisitor;
import net.sf.jasperreports.engine.util.JRStringUtil;


/**
 * A writer that generates the Java code required to produce a given report template programmatically, using the JasperReports API.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class ChartsApiWriter implements ChartVisitor // extends JRApiWriter
{
	private final JRApiWriter parent;
	private final String name;
	
	/**
	 *
	 */
	public ChartsApiWriter(JRApiWriterVisitor visitor)
	{
		this.parent = visitor.getApiWriter();
		this.name = visitor.getName();
	}
	
	
	@Override
	public void visitChart(JRChart chart) 
	{
		writeChartTag(chart, name);
	}


	/**
	 *
	 */
	private void writeChart( JRChart chart, String chartName)
	{
		if (chart != null)
		{
			parent.write( chartName + ".setShowLegend({0});\n", parent.getBooleanText(chart.getShowLegend()));
			parent.write( chartName + ".setEvaluationTime({0});\n", chart.getEvaluationTime(), EvaluationTimeEnum.NOW);
			parent.write( chartName + ".setEvaluationGroup(\"{0}\");\n", chart.getEvaluationGroup());
	
			if (chart.getLinkType() != null)
			{
				parent.write( chartName + ".setLinkType(\"{0}\");\n", JRStringUtil.escapeJavaStringLiteral(chart.getLinkType()), HyperlinkTypeEnum.NONE.getName());
			}
			if (chart.getLinkTarget() != null)
			{
				parent.write( chartName + ".setLinkTarget(\"{0}\");\n", JRStringUtil.escapeJavaStringLiteral(chart.getLinkTarget()), HyperlinkTargetEnum.SELF.getName());
			}
			parent.write( chartName + ".setBookmarkLevel({0, number, #});\n", chart.getBookmarkLevel(), JRAnchor.NO_BOOKMARK);

			if (chart.getCustomizerClass() != null)
			{
				parent.write( chartName + ".setCustomizerClass(\"{0}\");\n", JRStringUtil.escapeJavaStringLiteral(chart.getCustomizerClass()));
			}
			parent.write( chartName + ".setRenderType(\"{0}\");\n", JRStringUtil.escapeJavaStringLiteral(chart.getRenderType()));
			parent.write( chartName + ".setTheme(\"{0}\");\n", JRStringUtil.escapeJavaStringLiteral(chart.getTheme()));

			parent.writeReportElement( chart, chartName);
			parent.writeBox( chart.getLineBox(), chartName + ".getLineBox()");
	
			parent.write( chartName + ".setTitlePosition({0});\n", chart.getTitlePosition());
			parent.write( chartName + ".setTitleColor({0});\n", chart.getOwnTitleColor());
			if (chart.getTitleFont() != null)
			{
				parent.write( chartName + ".setTitleFont(new JRBaseFont());\n");
				parent.writeFont( chart.getTitleFont(), chartName + ".getTitleFont()");
			}
			parent.writeExpression( chart.getTitleExpression(), chartName, "TitleExpression");
			parent.write( chartName + ".setSubtitleColor({0});\n", chart.getOwnSubtitleColor());
			
			if (chart.getSubtitleFont() != null)
			{
				parent.write( chartName + ".setSubtitleFont(new JRBaseFont());\n");
				parent.writeFont( chart.getSubtitleFont(), chartName + ".getSubtitleFont()");
			}

			parent.writeExpression( chart.getSubtitleExpression(), chartName, "SubtitleExpression");
			parent.write( chartName + ".setLegendColor({0});\n", chart.getOwnLegendColor());
			parent.write( chartName + ".setLegendBackgroundColor({0});\n", chart.getOwnLegendBackgroundColor());
			parent.write( chartName + ".setLegendPosition({0});\n", chart.getLegendPosition());

			if (chart.getLegendFont() != null)
			{
				parent.write( chartName + ".setLegendFont(new JRBaseFont());\n");
				parent.writeFont( chart.getLegendFont(), chartName + ".getLegendFont()");
			}
			parent.writeExpression( chart.getBookmarkLevelExpression(), chartName, "BookmarkLevelExpression");
			parent.writeExpression( chart.getAnchorNameExpression(), chartName, "AnchorNameExpression");
			parent.writeExpression( chart.getHyperlinkReferenceExpression(), chartName, "HyperlinkReferenceExpression");
			parent.writeExpression( chart.getHyperlinkWhenExpression(), chartName, "HyperlinkWhenExpression");//FIXMENOW can we reuse hyperlink write method?
			parent.writeExpression( chart.getHyperlinkAnchorExpression(), chartName, "HyperlinkAnchorExpression");
			parent.writeExpression( chart.getHyperlinkPageExpression(), chartName, "HyperlinkPageExpression");
			parent.writeExpression( chart.getHyperlinkTooltipExpression(), chartName, "HyperlinkTooltipExpression");
			parent.writeHyperlinkParameters( chart.getHyperlinkParameters(), chartName);
	
			parent.flush();
		}
	}



	/**
	 *
	 */
	private void writeCategoryDataSet( JRCategoryDataset dataset, String parentName, String datasetNameSuffix)
	{
		if (dataset != null)
		{
			String datasetName = parentName + datasetNameSuffix;
			parent.write( "JRDesignCategoryDataset " + datasetName + " = new JRDesignCategoryDataset("+ parentName + ".getDataset());\n");
	
			parent.writeElementDataset( dataset, datasetName);
	
			JRCategorySeries[] categorySeries = dataset.getSeries();
			if (categorySeries != null && categorySeries.length > 0)
			{
				for (int i = 0; i < categorySeries.length; i++)
				{
					writeCategorySeries( categorySeries[i], datasetName, i);
				}
			}
	
			parent.write( parentName + ".setDataset(" + datasetName + ");\n");
			parent.flush();
		}
	}


	/**
	 * 
	 */
	private void writeTimeSeriesDataset( JRTimeSeriesDataset dataset, String parentName, String datasetNameSuffix)
	{
		if (dataset != null)
		{
			String datasetName = parentName + datasetNameSuffix;
			parent.write( "JRDesignTimeSeriesDataset " + datasetName + " =  new JRDesignTimeSeriesDataset(" + parentName + ".getDataset());\n");

			if (dataset.getTimePeriod() != null && dataset.getTimePeriod() != TimePeriodEnum.DAY)
			{
				parent.write( datasetName + ".setTimePeriod({0});\n", dataset.getTimePeriod());
			}
	
			parent.writeElementDataset( dataset, datasetName);
	
			JRTimeSeries[] timeSeries = dataset.getSeries();
			if ( timeSeries != null && timeSeries.length > 0 )
			{
				for ( int i = 0; i < timeSeries.length; i++ )
			{
					writeTimeSeries( timeSeries[i], datasetName, i  );
				}
			}

			parent.write( parentName + ".setDataset(" + datasetName + ");\n");
			parent.flush();
		}
	}


	/**
	 * 
	 */
	private void writeGanttDataset( JRGanttDataset dataset, String parentName, String datasetNameSuffix)
	{
		if (dataset != null)
		{
			String datasetName = parentName + datasetNameSuffix;
			parent.write( "JRDesignGanttDataset " + datasetName + " = new JRDesignGanttDataset(" + parentName + ".getDataset());\n");
			parent.writeElementDataset( dataset, datasetName);
	
			JRGanttSeries[] ganttSeries = dataset.getSeries();
			if (ganttSeries != null && ganttSeries.length > 0)
			{
				for (int i = 0; i < ganttSeries.length; i++)
				{
					writeGanttSeries( ganttSeries[i], datasetName, i);
				}
			}
			parent.write( parentName + ".setDataset(" + datasetName + ");\n");
			parent.flush();
		}
	}


	/**
	 * 
	 */
	private void writeTimePeriodDataset( JRTimePeriodDataset dataset, String parentName, String datasetNameSuffix)
	{
		if (dataset != null)
		{
			String datasetName = parentName + datasetNameSuffix;
			parent.write( "JRDesignTimePeriodDataset " + datasetName + " = new JRDesignTimePeriodDataset(" + parentName + ".getDataset());\n");
			parent.writeElementDataset( dataset, datasetName);
	
			JRTimePeriodSeries[] timePeriodSeries = dataset.getSeries();
			if ( timePeriodSeries != null && timePeriodSeries.length > 0 )
			{
				for ( int i = 0; i < timePeriodSeries.length; i++ )
				{
					writeTimePeriodSeries( timePeriodSeries[i], datasetName, i);
				}
			}
			parent.write( parentName + ".setDataset(" + datasetName + ");\n");
			parent.flush();
		}
	}


	/**
	 *
	 */
	private void writePieSeries( JRPieSeries pieSeries, String parentName, int index)
	{
		if (pieSeries != null)
		{
			String pieSeriesName = parentName + "PieSeries" + index;
			parent.write( "JRDesignPieSeries " + pieSeriesName + " = new JRDesignPieSeries();\n");
	
			parent.writeExpression( pieSeries.getKeyExpression(), pieSeriesName, "KeyExpression");
			parent.writeExpression( pieSeries.getValueExpression(), pieSeriesName, "ValueExpression");
			parent.writeExpression( pieSeries.getLabelExpression(), pieSeriesName, "LabelExpression");
			parent.writeHyperlink( pieSeries.getSectionHyperlink(),pieSeriesName, "SectionHyperlink");
			parent.write( parentName + ".addPieSeries(" + pieSeriesName + ");\n");
	
			parent.flush();
		}
	}

	/**
	 *
	 */
	private void writeCategorySeries( JRCategorySeries categorySeries, String parentName, int index)
	{
		if (categorySeries != null)
		{
			String categorySeriesName = parentName + "CategorySeries" + index;

			parent.write( "JRDesignCategorySeries " + categorySeriesName + " = new JRDesignCategorySeries();\n");

			parent.writeExpression( categorySeries.getSeriesExpression(), categorySeriesName, "SeriesExpression");
			parent.writeExpression( categorySeries.getCategoryExpression(), categorySeriesName, "CategoryExpression");
			parent.writeExpression( categorySeries.getValueExpression(), categorySeriesName, "ValueExpression");
			parent.writeExpression( categorySeries.getLabelExpression(), categorySeriesName, "LabelExpression");
			parent.writeHyperlink( categorySeries.getItemHyperlink(), categorySeriesName, "ItemHyperlink");
			parent.write( parentName + ".addCategorySeries(" + categorySeriesName + ");\n");
			parent.flush();
		}
	}

	/**
	 *
	 */
	private void writeXyzDataset( JRXyzDataset dataset, String parentName, String datasetNameSuffix)
	{
		if (dataset != null)
		{
			String datasetName = parentName + datasetNameSuffix;
			parent.write( "JRDesignXyzDataset " + datasetName + " = new JRDesignXyzDataset(" + parentName + ".getDataset());\n");
	
			parent.writeElementDataset( dataset, datasetName);
	
			JRXyzSeries[] series = dataset.getSeries();
			if ( series != null && series.length > 0 )
			{
				for ( int i = 0; i < series.length; i++ )
				{
					writeXyzSeries( series[i], datasetName, i);
				}
			}
			parent.write( parentName + ".setDataset(" + datasetName + ");\n");
			parent.flush();
		}
	}


	/**
	 *
	 */
	private void writeXyzSeries( JRXyzSeries series, String parentName, int index)
	{
		if (series != null)
		{
			String xyzSeriesName = parentName + "XyzSeries" + index;

			parent.write( "JRDesignXyzSeries " + xyzSeriesName + " = new JRDesignXyzSeries();\n");
	
			parent.writeExpression( series.getSeriesExpression(), xyzSeriesName, "SeriesExpression");
			parent.writeExpression( series.getXValueExpression(), xyzSeriesName, "XValueExpression");
			parent.writeExpression( series.getYValueExpression(), xyzSeriesName, "YValueExpression");
			parent.writeExpression( series.getZValueExpression(), xyzSeriesName, "ZValueExpression");
			parent.writeHyperlink( series.getItemHyperlink(), xyzSeriesName, "ItemHyperlink");
			parent.write( parentName + ".addXyzSeries(" + xyzSeriesName + ");\n");
			parent.flush();
		}
	}


	/**
	 *
	 */
	private void writeXySeries( JRXySeries xySeries, String parentName, int index)
	{
		if (xySeries != null)
		{
			String xySeriesName = parentName + "XySeries" + index;
			parent.write( "JRDesignXySeries " + xySeriesName + " = new JRDesignXySeries();\n");
			if (xySeries.getAutoSort() != null)
			{
				parent.write( xySeriesName + ".setAutoSort({0});\n", xySeries.getAutoSort());
			}
			parent.writeExpression( xySeries.getSeriesExpression(), xySeriesName, "SeriesExpression");
			parent.writeExpression( xySeries.getXValueExpression(), xySeriesName, "XValueExpression");
			parent.writeExpression( xySeries.getYValueExpression(), xySeriesName, "YValueExpression");
			parent.writeExpression( xySeries.getLabelExpression(), xySeriesName, "LabelExpression");
			parent.writeHyperlink( xySeries.getItemHyperlink(), xySeriesName, "ItemHyperlink");
			parent.write( parentName + ".addXySeries(" + xySeriesName + ");\n");
			parent.flush();
		}
	}


	/**
	 *
	 */
	private void writeXyDataset( JRXyDataset dataset, String parentName, String datasetNameSuffix)
	{
		if (dataset != null)
		{
			String datasetName = parentName + datasetNameSuffix;
			parent.write( "JRDesignXyDataset " + datasetName + " = new JRDesignXyDataset(" + parentName + ".getDataset());\n");
	
			parent.writeElementDataset( dataset, datasetName);
	
			JRXySeries[] xySeries = dataset.getSeries();
			if (xySeries != null && xySeries.length > 0)
			{
				for (int i = 0; i < xySeries.length; i++)
				{
					writeXySeries( xySeries[i], datasetName, i);
				}
			}
			parent.write( parentName + ".setDataset(" + datasetName + ");\n");
			parent.flush();
		}
	}

	/**
	 *
	 */
	private void writeTimeSeries( JRTimeSeries timeSeries, String parentName, int index)
	{
		if (timeSeries != null)
		{
			String timeSeriesName = parentName + "TimeSeries" + index;
			parent.write( "JRDesignTimeSeries " + timeSeriesName + " = new JRDesignTimeSeries();\n");
			parent.writeExpression( timeSeries.getSeriesExpression(), timeSeriesName, "SeriesExpression");
			parent.writeExpression( timeSeries.getTimePeriodExpression(), timeSeriesName, "TimePeriodExpression");
			parent.writeExpression( timeSeries.getValueExpression(), timeSeriesName, "ValueExpression");
			parent.writeExpression( timeSeries.getLabelExpression(), timeSeriesName, "LabelExpression");
			parent.writeHyperlink( timeSeries.getItemHyperlink(), timeSeriesName, "ItemHyperlink");
			parent.write( parentName + ".addTimeSeries(" + timeSeriesName + ");\n");
			parent.flush();
		}
	}


	/**
	 * 
	 */
	private void writeGanttSeries( JRGanttSeries ganttSeries, String parentName, int index)
	{
		if (ganttSeries != null)
		{
			String ganttSeriesName = parentName + "GanttSeries" + index;
			parent.write( "JRDesignGanttSeries " + ganttSeriesName + " = new JRDesignGanttSeries();\n");
			
			parent.writeExpression( ganttSeries.getSeriesExpression(), ganttSeriesName, "SeriesExpression");
			parent.writeExpression( ganttSeries.getTaskExpression(), ganttSeriesName, "TaskExpression");
			parent.writeExpression( ganttSeries.getSubtaskExpression(), ganttSeriesName, "SubtaskExpression");
			parent.writeExpression( ganttSeries.getStartDateExpression(), ganttSeriesName, "StartDateExpression");
			parent.writeExpression( ganttSeries.getEndDateExpression(), ganttSeriesName, "EndDateExpression");
			parent.writeExpression( ganttSeries.getPercentExpression(), ganttSeriesName, "PercentExpression");
			parent.writeExpression( ganttSeries.getLabelExpression(), ganttSeriesName, "LabelExpression");
			parent.writeHyperlink( ganttSeries.getItemHyperlink(), ganttSeriesName, "ItemHyperlink");
			parent.write( parentName + ".addGanttSeries(" + ganttSeriesName + ");\n");
			parent.flush();
		}
	}

	/**
	 * 
	 */
	private void writeTimePeriodSeries( JRTimePeriodSeries timePeriodSeries, String parentName, int index)
	{
		if (timePeriodSeries != null)
		{
			String timePeriodSeriesName = parentName + "TimePeriodSeries" + index;
			parent.write( "JRDesignTimePeriodSeries " + timePeriodSeriesName + " = new JRDesignTimePeriodSeries();\n");
			
			parent.writeExpression( timePeriodSeries.getSeriesExpression(), timePeriodSeriesName, "SeriesExpression");
			parent.writeExpression( timePeriodSeries.getStartDateExpression(), timePeriodSeriesName, "StartDateExpression");
			parent.writeExpression( timePeriodSeries.getEndDateExpression(), timePeriodSeriesName, "EndDateExpression");
			parent.writeExpression( timePeriodSeries.getValueExpression(), timePeriodSeriesName, "ValueExpression");
			parent.writeExpression( timePeriodSeries.getLabelExpression(), timePeriodSeriesName, "LabelExpression");
			parent.writeHyperlink( timePeriodSeries.getItemHyperlink(), timePeriodSeriesName, "ItemHyperlink");
			parent.write( parentName + ".addTimePeriodSeries(" + timePeriodSeriesName + ");\n");
			parent.flush();
		}
	}


	/**
	 *
	 */
	public void writePieDataset( JRPieDataset dataset, String parentName, String datasetNameSuffix)
	{
		if (dataset != null)
		{
			String datasetName = parentName + datasetNameSuffix;
			parent.write( "JRDesignPieDataset " + datasetName + " = new JRDesignPieDataset(" + parentName + ".getDataset());\n");
			parent.write( datasetName + ".setMaxCount({0, number, #});\n", dataset.getMaxCount());
			parent.write( datasetName + ".setMinPercentage({0});\n", dataset.getMinPercentage());
	
			parent.writeElementDataset( dataset, datasetName);
	
			JRPieSeries[] pieSeries = dataset.getSeries();
			if (pieSeries != null)
			{
				if (pieSeries.length > 1)
				{
					for (int i = 0; i < pieSeries.length; i++)
					{
						writePieSeries( pieSeries[i], datasetName, i);
					}
				}
				else
				{
					//preserve old syntax of single series pie datasets
					writePieSeries( pieSeries[0], datasetName, 0);
				}
			}
	
			parent.writeExpression( dataset.getOtherKeyExpression(), datasetName, "OtherKeyExpression");
			parent.writeExpression( dataset.getOtherLabelExpression(), datasetName, "OtherLabelExpression");
			parent.writeHyperlink( dataset.getOtherSectionHyperlink(), datasetName, "OtherSectionHyperlink");
			parent.write( parentName + ".setDataset(" + datasetName + ");\n");
			parent.flush();
		}
	}

	/**
	 * Writes the description of a value dataset to the output stream.
	 * @param dataset the value dataset to persist
	 */
	public void writeValueDataset( JRValueDataset dataset, String parentName, String datasetNameSuffix)
	{
		if (dataset != null)
		{
			String datasetName = parentName + datasetNameSuffix;
			parent.write( "JRDesignValueDataset " + datasetName + " = new JRDesignValueDataset(" + parentName + ".getDataset());\n");
			parent.writeElementDataset( dataset, datasetName);
			parent.writeExpression( dataset.getValueExpression(), datasetName, "ValueExpression");
			parent.write( parentName + ".setDataset(" + datasetName + ");\n");
			parent.flush();
		}
	}


	/**
	 * Writes the description of how to display a value in a valueDataset.
	 *
	 * @param valueDisplay the description to save
	 */
	public void writeValueDisplay( JRValueDisplay valueDisplay, String parentName)
	{
		if (valueDisplay != null)
		{
			String valueDisplayName = parentName + "ValueDisplay";

			parent.write( "JRDesignValueDisplay " + valueDisplayName + " = new JRDesignValueDisplay(" +parentName + ".getValueDisplay(), " + parentName + ".getChart());\n");
			
			parent.write( valueDisplayName + ".setColor({0});\n", valueDisplay.getColor());
			parent.write( valueDisplayName + ".setMask(\"{0}\");\n", valueDisplay.getMask());
			parent.write( valueDisplayName + ".setFont(new JRBaseFont());\n");
			if (valueDisplay.getFont() != null)
			{
				parent.write( valueDisplayName + ".setFont(new JRBaseFont());\n");
				parent.writeFont( valueDisplay.getFont(), valueDisplayName + ".getFont()");
			}
			
			parent.write( parentName + ".setValueDisplay(" + valueDisplayName + ");\n");
			
			parent.flush();
		}
	}

	/**
	 * Writes the description of how to display item labels in a category plot.
	 *
	 * @param itemLabel the description to save
	 */
	public void writeItemLabel( JRItemLabel itemLabel, String parentName, String itemLabelSuffix)
	{
		if (itemLabel != null)
		{
			String itemLabelName = parentName + itemLabelSuffix;
			parent.write( "JRDesignItemLabel " + itemLabelName + " = new JRDesignItemLabel("+ parentName + ".getItemLabel(), " + parentName + ".getChart());\n");
			parent.write( itemLabelName + ".setColor({0});\n", itemLabel.getColor());
			parent.write( itemLabelName + ".setBackgroundColor({0});\n", itemLabel.getBackgroundColor());
			if (itemLabel.getFont() != null)
			{
				parent.write( itemLabelName + ".setFont(new JRBaseFont());\n");
				parent.writeFont( itemLabel.getFont(), itemLabelName + ".getFont()");
			}
			
			parent.write( parentName + ".set" + itemLabelSuffix + "(" + itemLabelName + ");\n");
	
			parent.flush();
		}
	}

	/**
	 * Writes a data range block to the output stream.
	 *
	 * @param dataRange the range to write
	 */
	public void writeDataRange( JRDataRange dataRange, String parentName, String dataRangeSuffix)
	{
		if (dataRange != null)
		{
			String dataRangeName = parentName + dataRangeSuffix;
			parent.write( "JRDesignDataRange " + dataRangeName + " = new JRDesignDataRange(" + parentName + ".get" + dataRangeSuffix + "());\n");
			parent.writeExpression( dataRange.getLowExpression(), dataRangeName, "LowExpression");
			parent.writeExpression( dataRange.getHighExpression(), dataRangeName, "HighExpression");
			parent.write( parentName + ".set" + dataRangeSuffix + "(" + dataRangeName + ");\n");
			parent.flush();
		}
	}


	/**
	 * Writes a meter interval description to the output stream.
	 *
	 * @param interval the interval to write
	 */
	private void writeMeterInterval( JRMeterInterval interval, String parentName, String meterIntervalName)
	{
		if (interval != null)
		{
			parent.write( "JRMeterInterval " + meterIntervalName + " = new JRMeterInterval();\n");
			parent.write( meterIntervalName + ".setLabel(\"{0}\");\n", JRStringUtil.escapeJavaStringLiteral(interval.getLabel()));
			parent.write( meterIntervalName + ".setBackgroundColor({0});\n", interval.getBackgroundColor());
			parent.write( meterIntervalName + ".setAlpha({0});\n", interval.getAlpha());
			writeDataRange( interval.getDataRange(), meterIntervalName, "DataRange");
			parent.write( parentName + ".addInterval(" + meterIntervalName + ");\n");
			parent.flush();
		}
	}

	/**
	 * Writes out the contents of a series colors block for a chart.  Assumes the caller
	 * has already written the <code>&lt;seriesColors&gt;</code> tag.
	 *
	 * @param seriesColors the colors to write
	 */
	private void writeSeriesColors( SortedSet<JRSeriesColor> seriesColors, String parentName)
	{
		if (seriesColors == null || seriesColors.size() == 0)
		{
			return;
		}
		//FIXME why do we need an array?
		JRSeriesColor[] colors = seriesColors.toArray(new JRSeriesColor[seriesColors.size()]);
		for (int i = 0; i < colors.length; i++)
		{
			String seriesColorName = parentName + "SeriesColor" +i;
			parent.write( "JRBaseSeriesColor " + seriesColorName + " = new JRBaseSeriesColor(" + colors[i].getSeriesOrder() +", {0});\n", colors[i].getColor());
			parent.write( parentName + ".addSeriesColor(" + seriesColorName + ");\n");
			parent.flush();
		}
	}

	/**
	 * Write the information about a the data and layout that make up one range axis in
	 * a multiple axis chart.
	 *
	 * @param chartAxis the axis being written
	 */
	private void writeChartAxis( JRChartAxis chartAxis, String parentName, String axisName, String chartName)
	{
		if (chartAxis != null)
		{
			// Let the nested chart describe itself
			writeChartTag( chartAxis.getChart(), axisName +"Chart");
			
			parent.write( "JRDesignChartAxis " + axisName + " = new JRDesignChartAxis(" + parentName + ");\n");
			parent.write( axisName + ".setPosition({0});\n", chartAxis.getPosition());
			parent.write( axisName + ".setChart(" + axisName +"Chart);\n");
//			parent.write( parentName + ".setChart(" + axisName +"Chart);\n");
			parent.write( parentName + ".addAxis(" + axisName + ");\n");
			
			parent.flush();
		}
	}

	/**
	 *
	 *
	 */
	private void writePlot( JRChartPlot plot, String plotName)
	{
		if (plot != null)
		{
			parent.write( plotName + ".setBackcolor({0});\n", plot.getOwnBackcolor());

			if (plot.getOrientation() != null && plot.getOrientation() != PlotOrientationEnum.VERTICAL)
			{
				parent.write( plotName + ".setOrientation({0});\n", plot.getOrientation());
			}

			parent.write( plotName + ".setBackgroundAlpha({0});\n", plot.getBackgroundAlpha());
			parent.write( plotName + ".setForegroundAlpha({0});\n", plot.getForegroundAlpha());
			//write( plotName + ".setLabelRotation({0});\n", plot.getLabelRotationDouble());//FIXMECHART check the deprecation of this method; looks incomplete
			writeSeriesColors( plot.getSeriesColors(), plotName);
			parent.flush();
		}
	}


	/**
	 *
	 */
	public void writePieChart( JRChart chart, String chartName)
	{
		if (chart != null)
		{
			parent.write( "JRDesignChart " + chartName + " = new JRDesignChart(jasperDesign, ChartTypeEnum.PIE);\n");
			writeChart( chart, chartName);
			writePieDataset( (JRPieDataset)chart.getDataset(), chartName, "PieDataset");
			// write plot
			JRPiePlot plot = (JRPiePlot) chart.getPlot();
			if (plot != null)
			{
				String plotName = chartName + "PiePlot";
				parent.write( "JRDesignPiePlot " + plotName + " = (JRDesignPiePlot)" + chartName + ".getPlot();\n");
				parent.write( plotName + ".setShowLabels({0});\n", parent.getBooleanText(plot.getShowLabels()));
				parent.write( plotName + ".setCircular({0});\n", parent.getBooleanText(plot.getCircular()));
				parent.write( plotName + ".setLabelFormat(\"{0}\");\n", JRStringUtil.escapeJavaStringLiteral(plot.getLabelFormat()));
				parent.write( plotName + ".setLegendLabelFormat(\"{0}\");\n", JRStringUtil.escapeJavaStringLiteral(plot.getLegendLabelFormat()));
				
				writePlot( plot, plotName);
				writeItemLabel( plot.getItemLabel(),plotName, "ItemLabel");
				parent.flush();
			}
			parent.flush();
		}
	}


	/**
	 * @deprecated To be removed.
	 */
	public void writePie3DChart( JRChart chart, String chartName)
	{
		if (chart != null)
		{
			parent.write( "JRDesignChart " + chartName + " = new JRDesignChart(jasperDesign, ChartTypeEnum.PIE3D);\n");
			writeChart( chart, chartName);
			writePieDataset( (JRPieDataset)chart.getDataset(), chartName, "PieDataset");
			// write plot
			net.sf.jasperreports.charts.JRPie3DPlot plot = (net.sf.jasperreports.charts.JRPie3DPlot) chart.getPlot();
			if (plot != null)
			{
				String plotName = chartName + "Pie3DPlot";
				parent.write( "JRDesignPie3DPlot " + plotName + " = (JRDesignPie3DPlot)" + chartName + ".getPlot();\n");
				parent.write( plotName + ".setShowLabels({0});\n", parent.getBooleanText(plot.getShowLabels()));
				parent.write( plotName + ".setCircular({0});\n", parent.getBooleanText(plot.getCircular()));
				parent.write( plotName + ".setLabelFormat(\"{0}\");\n", JRStringUtil.escapeJavaStringLiteral(plot.getLabelFormat()));
				parent.write( plotName + ".setLegendLabelFormat(\"{0}\");\n", JRStringUtil.escapeJavaStringLiteral(plot.getLegendLabelFormat()));
				parent.write( plotName + ".setDepthFactor({0});\n", plot.getDepthFactor());
				
				writePlot( plot, plotName);
				writeItemLabel( plot.getItemLabel(),plotName, "ItemLabel");
				parent.flush();
			}
			parent.flush();
		}
	}


	/**
	 * Writes out the category axis format block.
	 *
	 * @param axisLabelFont font to use for the axis label
	 * @param axisLabelColor color to use for the axis label
	 * @param axisTickLabelFont font to use for the label of each tick mark
	 * @param axisTickLabelColor color to use for the label of each tick mark
	 * @param axisTickLabelMask formatting mask to use for the label of each tick mark
	 * @param axisVerticalTickLabels flag to render tick labels at 90 degrees
	 * @param labelRotation label rotation angle
	 * @param axisLineColor the color to use for the axis line and any tick marks
	 *
	 */
	public void writeCategoryAxisFormat(
		String indent, 
		JRFont axisLabelFont, 
		Color axisLabelColor,
		JRFont axisTickLabelFont, 
		Color axisTickLabelColor,
		String axisTickLabelMask, 
		Boolean axisVerticalTickLabels, 
		Double labelRotation, 
		Color axisLineColor,
		String parentName
		) 
	{
		if (axisLabelFont == null && axisLabelColor == null &&
			axisTickLabelFont == null && axisTickLabelColor == null && axisLineColor == null)
		{
			return;
		}

		parent.write( parentName + ".setCategoryAxisTickLabelRotation({0});\n", labelRotation);
		parent.write( parentName + ".setCategoryAxisLabelColor({0});\n", axisLabelColor);
		parent.write( parentName + ".setCategoryAxisTickLabelColor({0});\n", axisTickLabelColor);
		parent.write( parentName + ".setCategoryAxisLineColor({0});\n", axisLineColor);
		parent.write( parentName + ".setCategoryAxisTickLabelMask(\"{0}\");\n", JRStringUtil.escapeJavaStringLiteral(axisTickLabelMask));
		parent.write( parentName + ".setCategoryAxisVerticalTickLabels({0});\n", parent.getBooleanText(axisVerticalTickLabels));

		
		if (axisLabelFont != null)
		{
			parent.write( parentName + ".setCategoryAxisLabelFont(new JRBaseFont());\n");
			parent.writeFont( axisLabelFont, parentName + ".getCategoryAxisLabelFont()");
		}

		if (axisTickLabelFont != null)
		{
			parent.write( parentName + ".setCategoryAxisTickLabelFont(new JRBaseFont());\n");
			parent.writeFont( axisTickLabelFont, parentName + ".getCategoryAxisTickLabelFont()");
		}

		//write( parentName + ".set" + axisNameSuffix + "(" + axisName + ");\n");
		
		parent.flush();
	}
	
	
	
	/**
	 * Writes out the axis format block for a chart axis.
	 *
	 * @param axisLabelFont font to use for the axis label
	 * @param axisLabelColor color to use for the axis label
	 * @param axisTickLabelFont font to use for the label of each tick mark
	 * @param axisTickLabelColor color to use for the label of each tick mark
	 * @param axisTickLabelMask formatting mask to use for the label of each tick mark
	 * @param axisVerticalTickLabels flag to render tick labels at 90 degrees
	 * @param axisLineColor the color to use for the axis line and any tick marks
	 *
	 */
	public void writeAxisFormat(
		String indent,
		JRFont axisLabelFont, 
		Color axisLabelColor,
		JRFont axisTickLabelFont, 
		Color axisTickLabelColor,
		String axisTickLabelMask, 
		Boolean axisVerticalTickLabels, 
		Color axisLineColor,
		String parentName,
		String axisNameSuffix,
		boolean isToSet
		) 
	{
		if (axisLabelFont == null && axisLabelColor == null &&
				axisTickLabelFont == null && axisTickLabelColor == null && axisLineColor == null)
		{
			return;
		}
		String axisName = parentName + axisNameSuffix;
		if (isToSet)
		{
			parent.write( "JRAxisFormat " + axisName + " = new JRAxisFormat();\n");
		}
		parent.write( axisName + ".setLabelColor({0});\n", axisLabelColor);
		parent.write( axisName + ".setTickLabelColor({0});\n", axisTickLabelColor);
		parent.write( axisName + ".setLineColor({0});\n", axisLineColor);
		parent.write( axisName + ".setTickLabelMask(\"{0}\");\n", JRStringUtil.escapeJavaStringLiteral(axisTickLabelMask));
		parent.write( axisName + ".setVerticalTickLabel({0});\n", parent.getBooleanText(axisVerticalTickLabels));

		
		if (axisLabelFont != null)
		{
			parent.write( axisName + ".setLabelFont(new JRBaseFont());\n");
			parent.writeFont( axisLabelFont, axisName + ".getLabelFont()");
		}

		if (axisTickLabelFont != null)
		{
			parent.write( axisName + ".setTickLabelFont(new JRBaseFont());\n");
			parent.writeFont( axisTickLabelFont, axisName + ".getTickLabelFont()");
		}
		if (isToSet)//FIXMEAPIWRITER check this
		{
			parent.write( parentName + ".set" + axisNameSuffix + "(" + axisName + ");\n");
		}

		parent.flush();
	}

	/**
	 *
	 */
	private void writeBarPlot( JRBarPlot plot, String chartName)
	{
		if (plot != null)
		{
			String plotName = chartName + "BarPlot";
			parent.write( "JRDesignBarPlot " + plotName + " = (JRDesignBarPlot)" + chartName + ".getPlot();\n");
			parent.write( plotName + ".setShowLabels({0});\n", parent.getBooleanText(plot.getShowLabels()));
			parent.write( plotName + ".setShowTickLabels({0});\n", parent.getBooleanText(plot.getShowTickLabels()));
			parent.write( plotName + ".setShowTickMarks({0});\n", parent.getBooleanText(plot.getShowTickMarks()));
			writePlot( plot, plotName);
			
			writeItemLabel( plot.getItemLabel(), plotName, "ItemLabel");
			
			parent.writeExpression( plot.getCategoryAxisLabelExpression(), plotName, "CategoryAxisLabelExpression");
			writeCategoryAxisFormat(
					parent.getIndent(),
					plot.getCategoryAxisLabelFont(), plot.getOwnCategoryAxisLabelColor(),
					plot.getCategoryAxisTickLabelFont(), plot.getOwnCategoryAxisTickLabelColor(),
					plot.getCategoryAxisTickLabelMask(), plot.getCategoryAxisVerticalTickLabels(), 
					plot.getCategoryAxisTickLabelRotation(), plot.getOwnCategoryAxisLineColor(),
					plotName
					);
			
			parent.writeExpression( plot.getValueAxisLabelExpression(), plotName, "ValueAxisLabelExpression");
			writeAxisFormat(
					parent.getIndent(), 
					plot.getValueAxisLabelFont(), plot.getOwnValueAxisLabelColor(),
					plot.getValueAxisTickLabelFont(), plot.getOwnValueAxisTickLabelColor(),
					plot.getValueAxisTickLabelMask(), plot.getValueAxisVerticalTickLabels(), 
					plot.getOwnValueAxisLineColor(),
					plotName, "ValueAxisFormat", true
					);
			parent.writeExpression( plot.getDomainAxisMinValueExpression(), plotName, "DomainAxisMinValueExpression");
			parent.writeExpression( plot.getDomainAxisMaxValueExpression(), plotName, "DomainAxisMaxValueExpression");
			parent.writeExpression( plot.getRangeAxisMinValueExpression(), plotName, "RangeAxisMinValueExpression");
			parent.writeExpression( plot.getRangeAxisMaxValueExpression(), plotName, "RangeAxisMaxValueExpression");
	
			parent.flush();
		}
		
	}


	/**
	 *
	 */
	private void writeBubblePlot( JRBubblePlot plot, String chartName)
	{
		if (plot != null)
		{
			String plotName = chartName + "BubblePlot";
			parent.write( "JRDesignBubblePlot " + plotName + " = (JRDesignBubblePlot)" + chartName + ".getPlot();\n");
			parent.write( plotName + ".setScaleType({0});\n", plot.getScaleType());
			writePlot( plot, plotName);
			
			parent.writeExpression( plot.getXAxisLabelExpression(), plotName, "XAxisLabelExpression");
			writeAxisFormat(
					parent.getIndent(), plot.getXAxisLabelFont(), plot.getOwnXAxisLabelColor(),
					plot.getXAxisTickLabelFont(), plot.getOwnXAxisTickLabelColor(),
					plot.getXAxisTickLabelMask(), plot.getXAxisVerticalTickLabels(), plot.getOwnXAxisLineColor(),
					plotName, "XAxisFormat", true
					);
			parent.writeExpression( plot.getYAxisLabelExpression(), plotName, "YAxisLabelExpression");
			writeAxisFormat(
					parent.getIndent(), plot.getYAxisLabelFont(), plot.getOwnYAxisLabelColor(),
					plot.getYAxisTickLabelFont(), plot.getOwnYAxisTickLabelColor(),
					plot.getYAxisTickLabelMask(), plot.getYAxisVerticalTickLabels(), plot.getOwnYAxisLineColor(),
					plotName, "YAxisFormat", true
					);

			parent.writeExpression( plot.getDomainAxisMinValueExpression(), plotName, "DomainAxisMinValueExpression");
			parent.writeExpression( plot.getDomainAxisMaxValueExpression(), plotName, "DomainAxisMaxValueExpression");
			parent.writeExpression( plot.getRangeAxisMinValueExpression(), plotName, "RangeAxisMinValueExpression");
			parent.writeExpression( plot.getRangeAxisMaxValueExpression(), plotName, "RangeAxisMaxValueExpression");
	
			parent.flush();
		}
	}


	/**
	 *
	 */
	private void writeLinePlot( JRLinePlot plot, String chartName)
	{
		if (plot != null)
		{
			String plotName = chartName + "LinePlot";
			parent.write( "JRDesignLinePlot " + plotName + " = (JRDesignLinePlot)" + chartName + ".getPlot();\n");
			parent.write( plotName + ".setShowLines({0});\n", parent.getBooleanText(plot.getShowLines()));
			parent.write( plotName + ".setShowShapes({0});\n", parent.getBooleanText(plot.getShowShapes()));
			writePlot( plot, plotName);
			
			parent.writeExpression( plot.getCategoryAxisLabelExpression(), plotName, "CategoryAxisLabelExpression");
			writeCategoryAxisFormat(
					parent.getIndent(),
					plot.getCategoryAxisLabelFont(), plot.getOwnCategoryAxisLabelColor(),
					plot.getCategoryAxisTickLabelFont(), plot.getOwnCategoryAxisTickLabelColor(),
					plot.getCategoryAxisTickLabelMask(), plot.getCategoryAxisVerticalTickLabels(), 
					plot.getCategoryAxisTickLabelRotation(), plot.getOwnCategoryAxisLineColor(),
					plotName
					);
			
			parent.writeExpression( plot.getValueAxisLabelExpression(), plotName, "ValueAxisLabelExpression");
			writeAxisFormat(
					parent.getIndent(), 
					plot.getValueAxisLabelFont(), plot.getOwnValueAxisLabelColor(),
					plot.getValueAxisTickLabelFont(), plot.getOwnValueAxisTickLabelColor(),
					plot.getValueAxisTickLabelMask(), plot.getValueAxisVerticalTickLabels(), 
					plot.getOwnValueAxisLineColor(),
					plotName, "ValueAxisFormat", true
					);
			parent.writeExpression( plot.getDomainAxisMinValueExpression(), plotName, "DomainAxisMinValueExpression");
			parent.writeExpression( plot.getDomainAxisMaxValueExpression(), plotName, "DomainAxisMaxValueExpression");
			parent.writeExpression( plot.getRangeAxisMinValueExpression(), plotName, "RangeAxisMinValueExpression");
			parent.writeExpression( plot.getRangeAxisMaxValueExpression(), plotName, "RangeAxisMaxValueExpression");
	
			parent.flush();
		}
	}


	/**
	 * 
	 */
	private void writeTimeSeriesPlot( JRTimeSeriesPlot plot, String chartName)
	{
		if (plot != null)
		{
			String plotName = chartName + "TimeSeriesPlot";
			parent.write( "JRDesignTimeSeriesPlot " + plotName + " = (JRDesignTimeSeriesPlot)" + chartName + ".getPlot();\n");
			parent.write( plotName + ".setShowLines({0});\n", parent.getBooleanText(plot.getShowLines()));
			parent.write( plotName + ".setShowShapes({0});\n", parent.getBooleanText(plot.getShowShapes()));
			writePlot( plot, plotName);
			
			parent.writeExpression( plot.getTimeAxisLabelExpression(), plotName, "TimeAxisLabelExpression");
			writeAxisFormat(
					parent.getIndent(), plot.getTimeAxisLabelFont(), plot.getOwnTimeAxisLabelColor(),
					plot.getTimeAxisTickLabelFont(), plot.getOwnTimeAxisTickLabelColor(),
					plot.getTimeAxisTickLabelMask(), plot.getTimeAxisVerticalTickLabels(), 
					plot.getOwnTimeAxisLineColor(),
					plotName, "TimeAxisFormat", true
					);
			
			parent.writeExpression( plot.getValueAxisLabelExpression(), plotName, "ValueAxisLabelExpression");
			writeAxisFormat(
					parent.getIndent(), 
					plot.getValueAxisLabelFont(), plot.getOwnValueAxisLabelColor(),
					plot.getValueAxisTickLabelFont(), plot.getOwnValueAxisTickLabelColor(),
					plot.getValueAxisTickLabelMask(), plot.getValueAxisVerticalTickLabels(), 
					plot.getOwnValueAxisLineColor(),
					plotName, "ValueAxisFormat", true
					);
			parent.writeExpression( plot.getDomainAxisMinValueExpression(), plotName, "DomainAxisMinValueExpression");
			parent.writeExpression( plot.getDomainAxisMaxValueExpression(), plotName, "DomainAxisMaxValueExpression");
			parent.writeExpression( plot.getRangeAxisMinValueExpression(), plotName, "RangeAxisMinValueExpression");
			parent.writeExpression( plot.getRangeAxisMaxValueExpression(), plotName, "RangeAxisMaxValueExpression");
	
			parent.flush();
		}
	}

	/**
	 * @deprecated To be removed.
	 */
	public void writeBar3DPlot( net.sf.jasperreports.charts.JRBar3DPlot plot, String chartName)
	{
		if (plot != null)
		{
			String plotName = chartName + "Bar3DPlot";
			parent.write( "JRDesignBar3DPlot " + plotName + " = (JRDesignBar3DPlot)" + chartName + ".getPlot();\n");
			parent.write( plotName + ".setShowLabels({0});\n", parent.getBooleanText(plot.getShowLabels()));
			parent.write( plotName + ".setXOffset({0});\n", plot.getXOffset());
			parent.write( plotName + ".setYOffset({0});\n", plot.getYOffset());
			writePlot( plot, plotName);
			
			writeItemLabel( plot.getItemLabel(), plotName, "ItemLabel");
			
			parent.writeExpression( plot.getCategoryAxisLabelExpression(), plotName, "CategoryAxisLabelExpression");
			writeCategoryAxisFormat(
					parent.getIndent(),
					plot.getCategoryAxisLabelFont(), plot.getOwnCategoryAxisLabelColor(),
					plot.getCategoryAxisTickLabelFont(), plot.getOwnCategoryAxisTickLabelColor(),
					plot.getCategoryAxisTickLabelMask(), plot.getCategoryAxisVerticalTickLabels(), 
					plot.getCategoryAxisTickLabelRotation(), plot.getOwnCategoryAxisLineColor(),
					plotName
					);
			
			parent.writeExpression( plot.getValueAxisLabelExpression(), plotName, "ValueAxisLabelExpression");
			writeAxisFormat(
					parent.getIndent(), 
					plot.getValueAxisLabelFont(), plot.getOwnValueAxisLabelColor(),
					plot.getValueAxisTickLabelFont(), plot.getOwnValueAxisTickLabelColor(),
					plot.getValueAxisTickLabelMask(), plot.getValueAxisVerticalTickLabels(), 
					plot.getOwnValueAxisLineColor(),
					plotName, "ValueAxisFormat", true
					);
			parent.writeExpression( plot.getDomainAxisMinValueExpression(), plotName, "DomainAxisMinValueExpression");
			parent.writeExpression( plot.getDomainAxisMaxValueExpression(), plotName, "DomainAxisMaxValueExpression");
			parent.writeExpression( plot.getRangeAxisMinValueExpression(), plotName, "RangeAxisMinValueExpression");
			parent.writeExpression( plot.getRangeAxisMaxValueExpression(), plotName, "RangeAxisMaxValueExpression");
	
			parent.flush();
		}
	}


	/**
	 *
	 */
	public void writeBarChart( JRChart chart, String chartName)
	{
		if (chart != null)
		{
			parent.write( "JRDesignChart " + chartName + " = new JRDesignChart(jasperDesign, ChartTypeEnum.BAR);\n");
			writeChart( chart, chartName);
			writeCategoryDataSet( (JRCategoryDataset) chart.getDataset(), chartName, "CategoryDataset");
			writeBarPlot( (JRBarPlot) chart.getPlot(), chartName);
			parent.flush();
		}
	}


	/**
	 * @deprecated To be removed.
	 */
	public void writeBar3DChart( JRChart chart, String chartName)
	{
		if (chart != null)
		{
			parent.write( "JRDesignChart " + chartName + " = new JRDesignChart(jasperDesign, ChartTypeEnum.BAR3D);\n");
			writeChart( chart, chartName);
			writeCategoryDataSet( (JRCategoryDataset) chart.getDataset(), chartName, "CategoryDataset");
			writeBar3DPlot( (net.sf.jasperreports.charts.JRBar3DPlot) chart.getPlot(), chartName);
			parent.flush();
		}
	}


	/**
	 *
	 */
	public void writeBubbleChart( JRChart chart, String chartName)
	{
		if (chart != null)
		{
			parent.write( "JRDesignChart " + chartName + " = new JRDesignChart(jasperDesign, ChartTypeEnum.BUBBLE);\n");
			writeChart( chart, chartName);
			writeXyzDataset( (JRXyzDataset) chart.getDataset(), chartName, "XyzDataset");
			writeBubblePlot( (JRBubblePlot) chart.getPlot(), chartName);
			parent.flush();
		}
	}


	/**
	 *
	 */
	public void writeStackedBarChart( JRChart chart, String chartName)
	{
		if (chart != null)
		{
			parent.write( "JRDesignChart " + chartName + " = new JRDesignChart(jasperDesign, ChartTypeEnum.STACKEDBAR);\n");
			writeChart( chart, chartName);
			writeCategoryDataSet( (JRCategoryDataset) chart.getDataset(), chartName, "CategoryDataset");
			writeBarPlot( (JRBarPlot) chart.getPlot(), chartName);
			parent.flush();
		}
	}


	/**
	 * @deprecated To be removed.
	 */
	public void writeStackedBar3DChart( JRChart chart, String chartName)
	{
		if (chart != null)
		{
			parent.write( "JRDesignChart " + chartName + " = new JRDesignChart(jasperDesign, ChartTypeEnum.STACKEDBAR3D);\n");
			writeChart( chart, chartName);
			writeCategoryDataSet( (JRCategoryDataset) chart.getDataset(), chartName, "CategoryDataset");
			writeBar3DPlot( (net.sf.jasperreports.charts.JRBar3DPlot) chart.getPlot(), chartName);
			parent.flush();
		}
	}


	/**
	 *
	 */
	public void writeLineChart( JRChart chart, String chartName)
	{
		if (chart != null)
		{
			parent.write( "JRDesignChart " + chartName + " = new JRDesignChart(jasperDesign, ChartTypeEnum.LINE);\n");
			writeChart( chart, chartName);
			writeCategoryDataSet( (JRCategoryDataset) chart.getDataset(), chartName, "CategoryDataset");
			writeLinePlot( (JRLinePlot) chart.getPlot(), chartName);
			parent.flush();
		}
	}


	/**
	 * 
	 */
	public void writeTimeSeriesChart( JRChart chart, String chartName)
	{
		if (chart != null)
		{
			parent.write( "JRDesignChart " + chartName + " = new JRDesignChart(jasperDesign, ChartTypeEnum.TIMESERIES);\n");
			writeChart( chart, chartName);
			writeTimeSeriesDataset( (JRTimeSeriesDataset) chart.getDataset(), chartName, "TimeSeriesDataset");
			writeTimeSeriesPlot( (JRTimeSeriesPlot) chart.getPlot(), chartName);
			parent.flush();
		}
	}

	/**
	 * 
	 */
	public void writeHighLowDataset( JRHighLowDataset dataset, String parentName, String datasetNameSuffix)
	{
		if (dataset != null)
		{
			String datasetName = parentName + datasetNameSuffix;
			
			parent.write( "JRDesignHighLowDataset " + datasetName + " = (JRDesignHighLowDataset)" + parentName + ".getDataset();\n");
	
			parent.writeElementDataset( dataset, datasetName);
	
			parent.writeExpression( dataset.getSeriesExpression(), datasetName, "SeriesExpression");
			parent.writeExpression( dataset.getDateExpression(), datasetName, "DateExpression");
			parent.writeExpression( dataset.getHighExpression(), datasetName, "HighExpression");
			parent.writeExpression( dataset.getLowExpression(), datasetName, "LowExpression");
			parent.writeExpression( dataset.getOpenExpression(), datasetName, "OpenExpression");
			parent.writeExpression( dataset.getCloseExpression(), datasetName, "CloseExpression");
			parent.writeExpression( dataset.getVolumeExpression(), datasetName, "VolumeExpression");
			parent.writeHyperlink( dataset.getItemHyperlink(), datasetName, "ItemHyperlink");
			parent.write( parentName + ".setDataset(" + datasetName + ");\n");
			parent.flush();
		}
	}


	/**
	 * 
	 */
	public void writeHighLowChart( JRChart chart, String chartName)
	{
		if (chart != null)
		{
			parent.write( "JRDesignChart " + chartName + " = new JRDesignChart(jasperDesign, ChartTypeEnum.HIGHLOW);\n");
			writeChart( chart, chartName);
			writeHighLowDataset( (JRHighLowDataset) chart.getDataset(), chartName, "HighLowDataset");
			
			JRHighLowPlot plot = (JRHighLowPlot) chart.getPlot();
			if (plot != null)
			{
				String plotName = chartName + "HighLowPlot";
				parent.write( "JRDesignHighLowPlot " + plotName + " = (JRDesignHighLowPlot)" + chartName + ".getPlot();\n");
				parent.write( plotName + ".setShowOpenTicks({0});\n", parent.getBooleanText(plot.getShowOpenTicks()));
				parent.write( plotName + ".setShowCloseTicks({0});\n", parent.getBooleanText(plot.getShowCloseTicks()));

				writePlot( plot, plotName);
				parent.writeExpression( plot.getTimeAxisLabelExpression(), plotName, "TimeAxisLabelExpression");
				writeAxisFormat(
						parent.getIndent(), plot.getTimeAxisLabelFont(), plot.getOwnTimeAxisLabelColor(),
						plot.getTimeAxisTickLabelFont(), plot.getOwnTimeAxisTickLabelColor(),
						plot.getTimeAxisTickLabelMask(), plot.getTimeAxisVerticalTickLabels(), plot.getOwnTimeAxisLineColor(),
						plotName, "TimeAxisFormat", true
						);
				
				parent.writeExpression( plot.getValueAxisLabelExpression(), plotName, "ValueAxisLabelExpression");
				writeAxisFormat(
						parent.getIndent(), plot.getValueAxisLabelFont(), plot.getOwnValueAxisLabelColor(),
						plot.getValueAxisTickLabelFont(), plot.getOwnValueAxisTickLabelColor(),
						plot.getValueAxisTickLabelMask(), plot.getValueAxisVerticalTickLabels(), plot.getOwnValueAxisLineColor(),
						plotName, "ValueAxisFormat", true
						);
				parent.writeExpression( plot.getDomainAxisMinValueExpression(), plotName, "DomainAxisMinValueExpression");
				parent.writeExpression( plot.getDomainAxisMaxValueExpression(), plotName, "DomainAxisMaxValueExpression");
				parent.writeExpression( plot.getRangeAxisMinValueExpression(), plotName, "RangeAxisMinValueExpression");
				parent.writeExpression( plot.getRangeAxisMaxValueExpression(), plotName, "RangeAxisMaxValueExpression");
		
				parent.flush();
			}
			parent.flush();
		}
	}


	/**
	 * 
	 */
	public void writeGanttChart( JRChart chart, String chartName)
	{
		if (chart != null)
		{
			parent.write( "JRDesignChart " + chartName + " = new JRDesignChart(jasperDesign, ChartTypeEnum.GANTT);\n");
			writeChart( chart, chartName);
			writeGanttDataset( (JRGanttDataset) chart.getDataset(), chartName, "GanttDataset");
			writeBarPlot( (JRBarPlot) chart.getPlot(), chartName);
			parent.flush();
		}
	}


	/**
	 * 
	 */
	public void writeCandlestickChart( JRChart chart, String chartName)
	{
		if (chart != null)
		{
			parent.write( "JRDesignChart " + chartName + " = new JRDesignChart(jasperDesign, ChartTypeEnum.CANDLESTICK);\n");
			writeChart( chart, chartName);
			writeHighLowDataset( (JRHighLowDataset) chart.getDataset(), chartName, "HighLowDataset");
			
			JRCandlestickPlot plot = (JRCandlestickPlot) chart.getPlot();
			if (plot != null)
			{
				String plotName = chartName + "CandlestickPlot";
				
				parent.write( "JRDesignCandlestickPlot " + plotName + " = (JRDesignCandlestickPlot)" + chartName + ".getPlot();\n");
				parent.write( plotName + ".setShowVolume({0});\n", parent.getBooleanText(plot.getShowVolume()));
				writePlot( plot, plotName);
				parent.writeExpression( plot.getTimeAxisLabelExpression(), plotName, "TimeAxisLabelExpression");
				writeAxisFormat(
						parent.getIndent(), plot.getTimeAxisLabelFont(), plot.getOwnTimeAxisLabelColor(),
						plot.getTimeAxisTickLabelFont(), plot.getOwnTimeAxisTickLabelColor(),
						plot.getTimeAxisTickLabelMask(), plot.getTimeAxisVerticalTickLabels(), plot.getOwnTimeAxisLineColor(),
						plotName, "TimeAxisFormat", true
						);
				
				parent.writeExpression( plot.getValueAxisLabelExpression(), plotName, "ValueAxisLabelExpression");
				writeAxisFormat(
						parent.getIndent(), plot.getValueAxisLabelFont(), plot.getOwnValueAxisLabelColor(),
						plot.getValueAxisTickLabelFont(), plot.getOwnValueAxisTickLabelColor(),
						plot.getValueAxisTickLabelMask(), plot.getValueAxisVerticalTickLabels(), plot.getOwnValueAxisLineColor(),
						plotName, "ValueAxisFormat", true
						);
				parent.writeExpression( plot.getDomainAxisMinValueExpression(), plotName, "DomainAxisMinValueExpression");
				parent.writeExpression( plot.getDomainAxisMaxValueExpression(), plotName, "DomainAxisMaxValueExpression");
				parent.writeExpression( plot.getRangeAxisMinValueExpression(), plotName, "RangeAxisMinValueExpression");
				parent.writeExpression( plot.getRangeAxisMaxValueExpression(), plotName, "RangeAxisMaxValueExpression");
		
				parent.flush();
			}
			parent.flush();
		}
	}

	/**
	 *
	 */
	private void writeAreaPlot( JRAreaPlot plot, String chartName)
	{
		if (plot != null)
		{
			String plotName = chartName + "AreaPlot";
			
			parent.write( "JRDesignAreaPlot " + plotName + " = (JRDesignAreaPlot)" + chartName + ".getPlot();\n");
			writePlot( plot, plotName);
	
			parent.writeExpression( plot.getCategoryAxisLabelExpression(), plotName, "CategoryAxisLabelExpression");
			writeCategoryAxisFormat(
					parent.getIndent(),
					plot.getCategoryAxisLabelFont(), plot.getOwnCategoryAxisLabelColor(),
					plot.getCategoryAxisTickLabelFont(), plot.getOwnCategoryAxisTickLabelColor(),
					plot.getCategoryAxisTickLabelMask(), plot.getCategoryAxisVerticalTickLabels(), 
					plot.getCategoryAxisTickLabelRotation(), plot.getOwnCategoryAxisLineColor(),
					plotName
					);
			
			parent.writeExpression( plot.getValueAxisLabelExpression(), plotName, "ValueAxisLabelExpression");
			writeAxisFormat(
					parent.getIndent(), 
					plot.getValueAxisLabelFont(), plot.getOwnValueAxisLabelColor(),
					plot.getValueAxisTickLabelFont(), plot.getOwnValueAxisTickLabelColor(),
					plot.getValueAxisTickLabelMask(), plot.getValueAxisVerticalTickLabels(), 
					plot.getOwnValueAxisLineColor(),
					plotName, "ValueAxisFormat", true
					);
			parent.writeExpression( plot.getDomainAxisMinValueExpression(), plotName, "DomainAxisMinValueExpression");
			parent.writeExpression( plot.getDomainAxisMaxValueExpression(), plotName, "DomainAxisMaxValueExpression");
			parent.writeExpression( plot.getRangeAxisMinValueExpression(), plotName, "RangeAxisMinValueExpression");
			parent.writeExpression( plot.getRangeAxisMaxValueExpression(), plotName, "RangeAxisMaxValueExpression");
	
			parent.flush();
		}
	}


	/**
	 *
	 */
	public void writeAreaChart( JRChart chart, String chartName)
	{
		if (chart != null)
		{
			parent.write( "JRDesignChart " + chartName + " = new JRDesignChart(jasperDesign, ChartTypeEnum.AREA);\n");
			writeChart( chart, chartName);
			writeCategoryDataSet( (JRCategoryDataset) chart.getDataset(), chartName, "CategoryDataset");
			writeAreaPlot( (JRAreaPlot) chart.getPlot(), chartName);
			parent.flush();
		}
	}


	/**
	 *
	 */
	private void writeScatterPlot( JRScatterPlot plot, String chartName)
	{
		if (plot != null)
		{
			String plotName = chartName + "ScatterPlot";
			parent.write( "JRDesignScatterPlot " + plotName + " = (JRDesignScatterPlot)" + chartName + ".getPlot();\n");
			parent.write( plotName + ".setShowLines({0});\n", parent.getBooleanText(plot.getShowLines()));
			parent.write( plotName + ".setShowShapes({0});\n", parent.getBooleanText(plot.getShowShapes()));
			writePlot( plot, plotName);
			
			parent.writeExpression( plot.getXAxisLabelExpression(), plotName, "XAxisLabelExpression");
			writeAxisFormat(
					parent.getIndent(), 
					plot.getXAxisLabelFont(), plot.getOwnXAxisLabelColor(),
					plot.getXAxisTickLabelFont(), plot.getOwnXAxisTickLabelColor(),
					plot.getXAxisTickLabelMask(), plot.getXAxisVerticalTickLabels(), 
					plot.getOwnXAxisLineColor(),
					plotName, "XAxisFormat", true
					);
			
			parent.writeExpression( plot.getYAxisLabelExpression(), plotName, "YAxisLabelExpression");
			writeAxisFormat(
					parent.getIndent(), 
					plot.getYAxisLabelFont(), plot.getOwnYAxisLabelColor(),
					plot.getYAxisTickLabelFont(), plot.getOwnYAxisTickLabelColor(),
					plot.getYAxisTickLabelMask(), plot.getYAxisVerticalTickLabels(), 
					plot.getOwnYAxisLineColor(),
					plotName, "YAxisFormat", true
					);
			parent.writeExpression( plot.getDomainAxisMinValueExpression(), plotName, "DomainAxisMinValueExpression");
			parent.writeExpression( plot.getDomainAxisMaxValueExpression(), plotName, "DomainAxisMaxValueExpression");
			parent.writeExpression( plot.getRangeAxisMinValueExpression(), plotName, "RangeAxisMinValueExpression");
			parent.writeExpression( plot.getRangeAxisMaxValueExpression(), plotName, "RangeAxisMaxValueExpression");
	
			parent.flush();
		}
	}


	/**
	 *
	 */
	public void writeScatterChart( JRChart chart, String chartName)
	{
		if (chart != null)
		{
			parent.write( "JRDesignChart " + chartName + " = new JRDesignChart(jasperDesign, ChartTypeEnum.SCATTER);\n");
			writeChart( chart, chartName);
			writeXyDataset( (JRXyDataset) chart.getDataset(), chartName, "XyDataset");
			writeScatterPlot( (JRScatterPlot) chart.getPlot(), chartName);
			parent.flush();
		}
	}


	/**
	 *
	 */
	public void writeXyAreaChart( JRChart chart, String chartName)
	{
		if (chart != null)
		{
			parent.write( "JRDesignChart " + chartName + " = new JRDesignChart(jasperDesign, ChartTypeEnum.XYAREA);\n");
			writeChart( chart, chartName);
			writeXyDataset( (JRXyDataset) chart.getDataset(), chartName, "XyDataset");
			writeAreaPlot( (JRAreaPlot) chart.getPlot(), chartName);
			parent.flush();
		}
	}


	/**
	 *
	 */
	public void writeXyBarChart( JRChart chart, String chartName)
	{
		if (chart != null)
		{
			parent.write( "JRDesignChart " + chartName + " = new JRDesignChart(jasperDesign, ChartTypeEnum.XYBAR);\n");
			writeChart( chart, chartName);
			JRChartDataset dataset = chart.getDataset();

			if ( dataset.getDatasetType() == JRChartDataset.TIMESERIES_DATASET )
			{
				writeTimeSeriesDataset( (JRTimeSeriesDataset)dataset, chartName, "TimeSeriesDataset");
			}
			else if ( dataset.getDatasetType() == JRChartDataset.TIMEPERIOD_DATASET ){
				writeTimePeriodDataset( (JRTimePeriodDataset)dataset, chartName, "XyDataset");
			}
			else if ( dataset.getDatasetType() == JRChartDataset.XY_DATASET ){
				writeXyDataset( (JRXyDataset) chart.getDataset(), chartName, "XyDataset");
			}
			writeBarPlot( (JRBarPlot) chart.getPlot(), chartName);
			parent.flush();
		}
	}


	/**
	 *
	 */
	public void writeXyLineChart( JRChart chart, String chartName)
	{
		if (chart != null)
		{
			parent.write( "JRDesignChart " + chartName + " = new JRDesignChart(jasperDesign, ChartTypeEnum.XYLINE);\n");
			writeChart( chart, chartName);
			writeXyDataset( (JRXyDataset) chart.getDataset(), chartName, "XyDataset");
			writeLinePlot( (JRLinePlot) chart.getPlot(), chartName);
			parent.flush();
		}
	}


	/**
	 * Writes the definition of a meter chart to the output stream.
	 *
	 * @param chart the meter chart to write
	 */
	public void writeMeterChart( JRChart chart, String chartName)
	{
		if (chart != null)
		{
			parent.write( "JRDesignChart " + chartName + " = new JRDesignChart(jasperDesign, ChartTypeEnum.METER);\n");
			writeChart( chart, chartName);
			writeValueDataset( (JRValueDataset) chart.getDataset(), chartName, "ValueDataset");
			JRMeterPlot plot = (JRMeterPlot) chart.getPlot();
			if (plot != null)
			{
				String plotName = chartName + "MeterPlot";
				
				parent.write( "JRDesignMeterPlot " + plotName + " = (JRDesignMeterPlot)" + chartName + ".getPlot();\n");
				parent.write( plotName + ".setShape({0});\n", plot.getShape());
				parent.write( plotName + ".setMeterAngle({0, number, #});\n", plot.getMeterAngle());
				
				parent.write( plotName + ".setUnits(\"{0}\");\n", JRStringUtil.escapeJavaStringLiteral(plot.getUnits()));
				parent.write( plotName + ".setTickInterval({0});\n", plot.getTickInterval());
				parent.write( plotName + ".setMeterBackgroundColor({0});\n", plot.getMeterBackgroundColor());
				parent.write( plotName + ".setNeedleColor({0});\n", plot.getNeedleColor());
				parent.write( plotName + ".setTickColor({0});\n", plot.getTickColor());
				parent.write( plotName + ".setTickCount({0});\n", plot.getTickCount());
				
				writePlot( plot, plotName);
				if (plot.getTickLabelFont() != null)
				{
					parent.write( plotName + ".setTickLabelFont(new JRBaseFont());\n");
					parent.writeFont( plot.getTickLabelFont(), plotName + ".getTickLabelFont()");
					parent.flush();
				}
				writeValueDisplay( plot.getValueDisplay(), plotName);
				writeDataRange( plot.getDataRange(), plotName, "DataRange");

				List<JRMeterInterval> intervals = plot.getIntervals();
				if (intervals != null && intervals.size() > 0)
				{
					for (int i = 0; i < intervals.size(); i++)
					{
						JRMeterInterval meterInterval = intervals.get(i);
						writeMeterInterval( meterInterval, plotName, plotName+"Interval"+i);
					}
				}
				parent.flush();
				
			}
			parent.flush();
		}
	}


	/**
	 * Writes the description of a thermometer chart to the output stream.
	 *
	 * @param chart the thermometer chart to write
	 */
	public void writeThermometerChart( JRChart chart, String chartName)
	{
		if (chart != null)
		{
			parent.write( "JRDesignChart " + chartName + " = new JRDesignChart(jasperDesign, ChartTypeEnum.THERMOMETER);\n");
			writeChart( chart, chartName);
			writeValueDataset( (JRValueDataset) chart.getDataset(), chartName, "ValueDataset");
			JRThermometerPlot plot = (JRThermometerPlot) chart.getPlot();
			if (plot != null)
			{
				String plotName = chartName + "ThermometerPlot";
				parent.write( "JRDesignThermometerPlot " + plotName + " = (JRDesignThermometerPlot)" + chartName + ".getPlot();\n");
				parent.write( plotName + ".setValueLocation({0});\n", plot.getValueLocation());
				parent.write( plotName + ".setMercuryColor({0});\n", plot.getMercuryColor());
				writePlot( plot, plotName);
				writeValueDisplay( plot.getValueDisplay(), plotName);
				writeDataRange( plot.getDataRange(), plotName, "DataRange");

				if (plot.getLowRange() != null)
				{
					writeDataRange( plot.getLowRange(), plotName, "LowRange");
				}

				if (plot.getMediumRange() != null)
				{
					writeDataRange( plot.getMediumRange(), plotName, "MediumRange");
				}

				if (plot.getHighRange() != null)
				{
					writeDataRange( plot.getHighRange(), plotName, "HighRange");
				}
				parent.flush();
				
			}
			parent.flush();
		}
	}


	/**
	 * Writes the definition of a multiple axis chart to the output stream.
	 *
	 * @param chart the multiple axis chart to write
	 */
	public void writeMultiAxisChart( JRChart chart, String chartName)
	{
		if (chart != null)
		{
			parent.write( "JRDesignChart " + chartName + " = new JRDesignChart(jasperDesign, ChartTypeEnum.MULTI_AXIS);\n");
			writeChart( chart, chartName);
			JRMultiAxisPlot plot = (JRMultiAxisPlot) chart.getPlot();
			String plotName = chartName + "MultiAxisPlot";
			
			parent.write( "JRDesignMultiAxisPlot " + plotName + " = (JRDesignMultiAxisPlot)" + chartName + ".getPlot();\n");
			parent.write( plotName + ".setChart(" + chartName + ");\n");//FIXMECHART why is this needed since we get the plot from chart?
			writePlot( chart.getPlot(), plotName);
			List<JRChartAxis> axes = plot.getAxes();
			if (axes != null && axes.size() > 0)
			{
				for (int i = 0; i < axes.size(); i++)
				{
					JRChartAxis chartAxis = axes.get(i);
					writeChartAxis( chartAxis, plotName, plotName + "Axis" + i, chartName);
				}
			}
			parent.flush();
		}
	}

	/**
	 *
	 */
	public void writeStackedAreaChart( JRChart chart, String chartName)
	{
		if (chart != null)
		{
			parent.write( "JRDesignChart " + chartName + " = new JRDesignChart(jasperDesign, ChartTypeEnum.STACKEDAREA);\n");
			writeChart( chart, chartName);
			writeCategoryDataSet( (JRCategoryDataset) chart.getDataset(), chartName, "CategoryDataset");
			writeAreaPlot( (JRAreaPlot) chart.getPlot(), chartName);
			parent.flush();
		}
	}


	/**
	 * 
	 */
	public void writeChartTag( JRChart chart, String chartName)
	{
		switch (chart.getChartType()) {
			case AREA:
				writeAreaChart( chart, chartName);
				break;
			case BAR:
				writeBarChart( chart, chartName);
				break;
			case BAR3D:
				writeBar3DChart( chart, chartName);
				break;
			case BUBBLE:
				writeBubbleChart( chart, chartName);
				break;
			case CANDLESTICK:
				writeCandlestickChart( chart, chartName);
				break;
			case HIGHLOW:
				writeHighLowChart( chart, chartName);
				break;
			case LINE:
				writeLineChart( chart, chartName);
				break;
			case METER:
				writeMeterChart( chart, chartName);
				break;
			case MULTI_AXIS:
				writeMultiAxisChart( chart, chartName);
				break;
			case PIE:
				writePieChart( chart, chartName);
				break;
			case PIE3D:
				writePie3DChart( chart, chartName);
				break;
			case SCATTER:
				writeScatterChart( chart, chartName);
				break;
			case STACKEDBAR:
				writeStackedBarChart( chart, chartName);
				break;
			case STACKEDBAR3D:
				writeStackedBar3DChart( chart, chartName);
				break;
			case THERMOMETER:
				writeThermometerChart( chart, chartName);
				break;
			case TIMESERIES:
				writeTimeSeriesChart( chart, chartName);
				break;
			case XYAREA:
				writeXyAreaChart( chart, chartName);
				break;
			case XYBAR:
				writeXyBarChart( chart, chartName);
				break;
			case XYLINE:
				writeXyLineChart( chart, chartName);
				break;
			case STACKEDAREA:
				writeStackedAreaChart( chart, chartName);
				break;
			case GANTT:
				writeGanttChart( chart, chartName);
				break;
			default:
				throw 
					new JRRuntimeException(
						JRDesignChart.EXCEPTION_MESSAGE_KEY_UNSUPPORTED_CHART_TYPE,
						(Object[])null);
		}
	}
	
}

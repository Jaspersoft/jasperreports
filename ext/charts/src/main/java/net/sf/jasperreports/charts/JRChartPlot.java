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
package net.sf.jasperreports.charts;

import java.awt.Color;
import java.util.Collection;
import java.util.SortedSet;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.charts.base.JRBaseChartPlot.JRBaseSeriesColor;
import net.sf.jasperreports.charts.type.PlotOrientationEnum;
import net.sf.jasperreports.engine.JRCloneable;
import net.sf.jasperreports.engine.xml.JRXmlConstants;


/**
 * Chart plots define chart appearance and display details such as colors, legend or labels. Each plot may have different
 * characteristics, depending on the chart type it belongs to. This is the superinterface for all plots and contains common
 * properties.
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonPropertyOrder({
	"backcolor",
	"orientation",
	"backgroundAlpha",
	"foregroundAlpha",
	"labelRotation",
	"seriesColor"
	})
@JsonTypeInfo(use = Id.NAME, include = As.EXISTING_PROPERTY, property = "trick-to-avoid-serializing-the-type")
@JsonSubTypes({
	@JsonSubTypes.Type(value = JRBubblePlot.class),
	@JsonSubTypes.Type(value = JRCandlestickPlot.class),
	@JsonSubTypes.Type(value = JRCategoryPlot.class),
	@JsonSubTypes.Type(value = JRAreaPlot.class),
	@JsonSubTypes.Type(value = JRBarPlot.class),
	@JsonSubTypes.Type(value = JRBar3DPlot.class),
	@JsonSubTypes.Type(value = JRLinePlot.class),
	@JsonSubTypes.Type(value = JRHighLowPlot.class),
	@JsonSubTypes.Type(value = JRMeterPlot.class),
	@JsonSubTypes.Type(value = JRMultiAxisPlot.class),
	@JsonSubTypes.Type(value = JRPie3DPlot.class),
	@JsonSubTypes.Type(value = JRPiePlot.class),
	@JsonSubTypes.Type(value = JRScatterPlot.class),
	@JsonSubTypes.Type(value = JRThermometerPlot.class),
	@JsonSubTypes.Type(value = JRTimeSeriesPlot.class)
})
public interface JRChartPlot extends JRCloneable
{


	/**
	 * Gets the chart associated with this chart plot, if available. 
	 * Implementations can return null on this method, especially if the chart plot 
	 * is reused by multiple charts, which is not recommended since it prevents style properties 
	 * inheritence from parent chart.
	 */
	@JsonIgnore
	public JRChart getChart();
	
	/**
	 * Gets the chart background color.
	 */
	@JsonIgnore
	public Color getBackcolor();
	
	/**
	 *
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_backcolor)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_backcolor, isAttribute = true)
	public Color getOwnBackcolor();
	
	/**
	 * Sets the chart background color.
	 */
	@JsonSetter
	public void setBackcolor(Color backcolor);


	/**
	 * Gets the plot orientation (horizontal or vertical).
	 */
	@JsonInclude(Include.NON_EMPTY)
	@JacksonXmlProperty(isAttribute = true)
	public PlotOrientationEnum getOrientation();

	/**
	 * Sets the plot orientation (horizontal or vertical).
	 */
	public void setOrientation(PlotOrientationEnum orientation);

	/**
	 * Gets the transparency factor for this plot background. The range is from 0 to 1, where 0 means transparent and 1
	 * opaque. The default is 1.
	 * @return a float value between 0 and 1.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Float getBackgroundAlpha();
	
	/**
	 * Sets the transparency factor for this plot background. The range is from 0 to 1, where 0 means transparent and 1
	 * opaque. The default is 1.
	 */
	public void setBackgroundAlpha(Float backgroundAlpha);

	/**
	 * Gets the transparency factor for this plot foreground. The range is from 0 to 1, where 0 means transparent and 1
	 * opaque. The default is 1.
	 * @return a float value between 0 and 1.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Float getForegroundAlpha();
	
	/**
	 * Sets the transparency factor for this plot foreground. The range is from 0 to 1, where 0 means transparent and 1
	 * opaque. The default is 1.
	 */
	public void setForegroundAlpha(Float foregroundAlpha);

	/**
	 * Gets the angle in degrees to rotate the data axis labels.  The range is -360 to 360.  A positive value angles
	 * the label so it reads downwards wile a negative value angles the label so it reads upwards.  Only charts that
	 * use a category based axis (such as line or bar charts) support label rotation.
	 * @deprecated Replaced by {@link JRCategoryAxisFormat#getCategoryAxisTickLabelRotation()}.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Double getLabelRotation();
	
	/**
	 * Sets the angle in degrees to rotate the data axis labels.  The range is -360 to 360.  A positive value angles
	 * the label so it reads downwards wile a negative value angles the label so it reads upwards.  Only charts that
	 * use a category based axis (such as line or bar charts) support label rotation.
	 * @deprecated Replaced by {@link JRCategoryAxisFormat#setCategoryAxisTickLabelRotation(Double)}.
	 */
	public void setLabelRotation(Double labelRotation);
	
	/**
	 * Returns a list of all the defined series colors.  Every entry in the list is of type JRChartPlot.JRSeriesColor.
	 * If there are no defined series colors this method will return an empty list, not null. 
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_seriesColor)
	@JacksonXmlElementWrapper(useWrapping = false)
	public SortedSet<JRSeriesColor> getSeriesColors();
	
	/**
	 * Removes all defined series colors.
	 */
	public void clearSeriesColors();
	
	/**
	 * Adds the specified series color to the plot.
	 */
	public void addSeriesColor(JRSeriesColor seriesColor);
	
	/**
	 * Set the list of series colors.
	 * 
	 * @param colors the list of series colors ({@link JRSeriesColor} instances}
	 */
	public void setSeriesColors(Collection<JRSeriesColor> colors);
	
	/**
	 *
	 */
	public void collectExpressions(ChartsExpressionCollector collector);

	@JsonPropertyOrder({
		"order",
		"color"
		})
	@JsonDeserialize(as = JRBaseSeriesColor.class)
	public interface JRSeriesColor extends JRCloneable
	{
		/**
		 * Returns the series order that this color applies to.  The series order is relative to
		 * the series order of all other {@link JRSeriesColor}s defined for this plot.  The
		 * relative ordering defines the order of the colors in the series.
		 */
		@JsonGetter("order")
		@JacksonXmlProperty(localName = "order", isAttribute = true)
		public int getSeriesOrder();
		
		/**
		 * Returns the color to use for this series.
		 */
		@JacksonXmlProperty(isAttribute = true)
		public Color getColor();
	}
	
	/**
	 * 
	 */
	public Object clone(JRChart parentChart);
	
}

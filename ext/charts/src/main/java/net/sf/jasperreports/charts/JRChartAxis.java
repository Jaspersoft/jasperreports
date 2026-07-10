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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import net.sf.jasperreports.charts.design.JRDesignChartAxis;
import net.sf.jasperreports.charts.type.AxisPositionEnum;
import net.sf.jasperreports.engine.JRCloneable;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * Describes an axis that can be added to a multiple axis chart.  The name
 * "axis" is a bit of a misnomer, as it really contains information about
 * a new dataset to plot, the axis to plot it against, and how to render that
 * dataset.
 *
 * @author Barry Klawans (barry@users.sourceforge.net)
 */
@JsonPropertyOrder({
	JRXmlConstants.ATTRIBUTE_position,
	JRXmlConstants.ELEMENT_chart
	})
@JsonDeserialize(as = JRDesignChartAxis.class)
public interface JRChartAxis extends JRCloneable
{
	/**
	 * Gets the position of this axis.
	 *
	 * @return the position of this axis
	 */
	public AxisPositionEnum getPosition();


	/**
	 * Returns the chart that contains the dataset and plot to use for this
	 * axis.  The plot is used to figure out how to render the dataset when
	 * adding to the multiple axis chart.
	 *
	 * @return the chart that contains the dataset and plot for this axis
	 */
	@JsonTypeInfo(use = Id.NONE)
	public JRChart getChart();
	
	@JsonBackReference
	public JRMultiAxisPlot getMultiAxisPlot();
	
	public JRChartAxis clone(JRMultiAxisPlot multiAxisPlot);
}

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

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.charts.design.JRDesignPie3DPlot;

/**
 * Type of plot used for rendering Pie 3D charts.
 * <br/>
 * Special settings that the Pie 3D plot exposes are: 
 * <ul>
 * <li>the <code>circular</code> flag</li>
 * <li>the depth factor</li>
 * <li>the label format</li>
 * <li>the legend label format</li>
 * <li>the item label</li>
 * <li>the <code>showLabels</code> flag</li>
 * </ul>
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @deprecated To be removed.
 */
@JsonPropertyOrder({
	"backcolor",
	"orientation",
	"backgroundAlpha",
	"foregroundAlpha",
	"labelRotation",
	"circular",
	"labelFormat",
	"legendLabelFormat",
	"showLabels",
	"depthFactor",
	"seriesColor",
	"itemLabel"
	})
@JsonTypeName("pie3D")
@JsonDeserialize(as = JRDesignPie3DPlot.class)
public interface JRPie3DPlot extends JRPiePlot
{

	public static final double DEPTH_FACTOR_DEFAULT = 0.2;
	
	/**
	 * @return a numeric value ranging from 0 to 1 that represents the depth of the pie as 
	 * a percentage of the height of the plot area.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Double getDepthFactor();
	
}

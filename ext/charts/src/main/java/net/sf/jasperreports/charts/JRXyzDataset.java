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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import net.sf.jasperreports.charts.design.JRDesignXyzDataset;

/**
 * The XYZ dataset wraps series consisting of (x, y, z) items. 
 * It is used only by the Bubble chart
 * 
 * @author Flavius Sana (flavius_sana@users.sourceforge.net)
 */
@JsonPropertyOrder({
	"kind",
	"resetType",
	"resetGroup",
	"incrementType",
	"incrementGroup",
	"datasetRun",
	"incrementWhenExpression",
	"series"
	})
@JsonTypeName("xyz")
@JsonDeserialize(as = JRDesignXyzDataset.class)
public interface JRXyzDataset extends JRChartDataset {
	
	/**
	 * @return an array of {@link JRXyzSeries} objects representing the 
	 * series for the XYZ charts.
	 * @see JRXyzSeries
	 */
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRXyzSeries[] getSeries();

}

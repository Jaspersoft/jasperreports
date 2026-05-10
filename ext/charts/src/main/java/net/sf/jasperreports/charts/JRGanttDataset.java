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

import net.sf.jasperreports.charts.design.JRDesignGanttDataset;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * This dataset accommodates one or more data series consisting of values associated with 
 * tasks and subtasks. It is used to render the Gantt chart.
 * 
 * @author Peter Risko (peter@risko.hu)
 */
@JsonPropertyOrder({
	"kind",
	JRXmlConstants.ATTRIBUTE_resetType,
	JRXmlConstants.ATTRIBUTE_resetGroup,
	JRXmlConstants.ATTRIBUTE_incrementType,
	JRXmlConstants.ATTRIBUTE_incrementGroup,
	JRXmlConstants.ELEMENT_datasetRun,
	JRXmlConstants.ELEMENT_incrementWhenExpression,
	"series"
	})
@JsonTypeName("gantt")
@JsonDeserialize(as = JRDesignGanttDataset.class)
public interface JRGanttDataset extends JRChartDataset {

	/**
	 * @return an array of {@link JRGanttSeries} objects representing the 
	 * series for the Gantt chart
	 * @see JRGanttSeries
	 */
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRGanttSeries[] getSeries();


}

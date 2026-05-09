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

import net.sf.jasperreports.charts.design.JRDesignTimePeriodDataset;

/**
 * The Time Period dataset is very much like the Time Series dataset in that it wraps series 
 * consisting of (time period, numeric value) pairs. The only difference is that in this case 
 * the time periods are not chosen from a predefined list but can be arbitrary time intervals. 
 * This kind of dataset is for use only with XY Bar charts.
 *  
 * @author Flavius Sana (flavius_sana@users.sourceforge.net) 
 * @see JRTimeSeriesDataset
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
@JsonTypeName("timePeriod")
@JsonDeserialize(as = JRDesignTimePeriodDataset.class)
public interface JRTimePeriodDataset extends JRChartDataset {

	/**
	 * @return an array of {@link JRTimePeriodSeries} objects representing the 
	 * series for the Time Period chart
	 * @see JRTimePeriodSeries
	 */
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRTimePeriodSeries[] getSeries();
	
}

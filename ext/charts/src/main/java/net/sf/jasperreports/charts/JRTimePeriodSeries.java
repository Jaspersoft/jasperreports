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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import net.sf.jasperreports.charts.design.JRDesignTimePeriodSeries;
import net.sf.jasperreports.engine.JRCloneable;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRHyperlink;

/**
 * Represents the series for the Time Period dataset.
 * 
 * @author Flavius Sana (flavius_sana@users.sourceforge.net)
 */
@JsonPropertyOrder({
	"seriesExpression",
	"startDateExpression",
	"endDateExpression",
	"valueExpression",
	"labelExpression",
	"itemHyperlink"
	})
@JsonDeserialize(as = JRDesignTimePeriodSeries.class)
public interface JRTimePeriodSeries extends JRCloneable
{
	
	/**
	 * @return a <code>java.lang.Comparable</code> object that identifies each series.
	 */
	public JRExpression getSeriesExpression();
	
	/**
	 * @return a <code>java.util.Date</code> expression representing the 
	 * start of the time period
	 */
	public JRExpression getStartDateExpression();

	/**
	 * @return a <code>java.util.Date</code> expression representing the 
	 * end of the time period
	 */
	public JRExpression getEndDateExpression();
	
	/**
	 * @return a numeric expression representing the value associated with the time period.
	 */
	public JRExpression getValueExpression();
	
	/**
	 * @return the label expression. If present, its values will be used to customize 
	 * item labels in the resulting chart.
	 */
	public JRExpression getLabelExpression();

	
	/**
	 * Returns the hyperlink specification for chart items.
	 * <p>
	 * The hyperlink will be evaluated for every chart item and a image map will be created for the chart.
	 * </p>
	 * 
	 * @return hyperlink specification for chart items
	 */
	public JRHyperlink getItemHyperlink();

}

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

import net.sf.jasperreports.charts.design.JRDesignHighLowDataset;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRHyperlink;


/**
 * Although the name of this dataset is "High-Low", it can actually hold a series of 
 * (x, high, low, open, close, volume) items. It is used in combination with either 
 * a High-Low or a Candlestick chart.
 * 
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 */
@JsonPropertyOrder({
	"kind",
	"resetType",
	"resetGroup",
	"incrementType",
	"incrementGroup",
	"datasetRun",
	"incrementWhenExpression",
	"seriesExpression",
	"dateExpression",
	"highExpression",
	"lowExpression",
	"openExpression",
	"closeExpression",
	"volumeExpression",
	"itemHyperlink"
	})
@JsonTypeName("highLow")
@JsonDeserialize(as = JRDesignHighLowDataset.class)
public interface JRHighLowDataset extends JRChartDataset
{
	/**
	 * @return the expression of the series name. Currently only one series is supported inside 
	 * a High-Low or Candlestick chart. This limitation is documented inside JFreeChart, the 
	 * library used for the built-in chart support. However, this single series must 
	 * be identified by a <code>java.lang.Comparable</code> value returned by this expression, 
	 * and it must also be used as the series name in the chart's legend.
	 */
	public JRExpression getSeriesExpression();


	/**
	 * @return the expression of the date to which the current 
	 * (high, low, open, close, volume) item refers.
	 */
	public JRExpression getDateExpression();


	/**
	 * @return a numeric expression that will be part of the data item added to 
	 * the series when the dataset gets incremented.
	 */
	public JRExpression getHighExpression();


	/**
	 * @return a numeric expression that will be part of the data item added to 
	 * the series when the dataset gets incremented.
	 */
	public JRExpression getLowExpression();


	/**
	 * @return a numeric expression that will be part of the data item added to 
	 * the series when the dataset gets incremented.
	 */
	public JRExpression getOpenExpression();


	/**
	 * @return a numeric expression that will be part of the data item added to 
	 * the series when the dataset gets incremented.
	 */
	public JRExpression getCloseExpression();


	/**
	 * @return a numeric expression representing the volume value to use for the current 
	 * data item. It is used only for Candlestick charts.
	 */
	public JRExpression getVolumeExpression();
	
	
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

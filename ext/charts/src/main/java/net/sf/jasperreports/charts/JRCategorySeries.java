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

import net.sf.jasperreports.charts.design.JRDesignCategorySeries;
import net.sf.jasperreports.engine.JRCloneable;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRHyperlink;


/**
 * Represents the series for any Category dataset.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonPropertyOrder({
	"seriesExpression",
	"categoryExpression",
	"valueExpression",
	"labelExpression",
	"itemHyperlink"
	})
@JsonDeserialize(as = JRDesignCategorySeries.class)
public interface JRCategorySeries extends JRCloneable
{
	
	/**
	 * @return the expression of the series name. The value of this expression can be any 
	 * <code>java.lang.Comparable</code> object. 
	 * <br/>
	 * Note that this expression may return different values with each iteration, which in turn 
	 * will result in the dataset having multiple category series, even though a single 
	 * <code>&lt;categorySeries&gt;</code> tag was used inside <code>&lt;categoryDataset&gt;</code>. However, this 
	 * expression usually returns a <code>java.lang.String</code> constant, and there are several 
	 * <code>&lt;categorySeries&gt;</code> tags that introduce multiple category series in the dataset. 
	 */
	public JRExpression getSeriesExpression();

	/**
	 * @return the expression of the name of the category for each value inside the series 
	 * specified by the series expression. Categories are <code>java.lang.Comparable</code> objects 
	 * (not necessarily <code>java.lang.String</code> objects).
	 */
	public JRExpression getCategoryExpression();

	/**
	 * @return the value expression, a <code>java.lang.Number</code> value for each category in the specified series.
	 */
	public JRExpression getValueExpression();

	/**
	 * @return the label expression. If present, this expression allows 
	 * customization of the item labels in the chart.
	 */
	public JRExpression getLabelExpression();


	/**
	 * Returns the hyperlink specification for chart items.
	 * <p>
	 * The hyperlink will be evaluated for every chart item and an image map will be created for the chart.
	 * </p>
	 * 
	 * @return hyperlink specification for chart items
	 */
	public JRHyperlink getItemHyperlink();

}

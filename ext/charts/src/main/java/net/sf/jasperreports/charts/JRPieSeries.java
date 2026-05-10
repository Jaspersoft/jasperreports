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

import net.sf.jasperreports.charts.design.JRDesignPieSeries;
import net.sf.jasperreports.engine.xml.JRXmlConstants;
import net.sf.jasperreports.engine.JRCloneable;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRHyperlink;


/**
 * Represents the series for the Pie dataset.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonPropertyOrder({
	JRXmlConstants.ELEMENT_keyExpression,
	JRXmlConstants.ELEMENT_valueExpression,
	JRXmlConstants.ELEMENT_labelExpression,
	JRXmlConstants.ELEMENT_sectionHyperlink
	})
@JsonDeserialize(as = JRDesignPieSeries.class)
public interface JRPieSeries extends JRCloneable
{
	
	/**
	 * @return a <code>java.lang.Comparable</code> object representing 
	 * the categories that will make up the slices in the pie chart.
	 */
	public JRExpression getKeyExpression();

	/**
	 * @return a numeric expression that produces the values corresponding 
	 * to each category/key in the dataset.
	 */
	public JRExpression getValueExpression();

	/**
	 * @return the label expression. If this expression is missing, the chart 
	 * will display default labels for each slice in the pie chart. Use this 
	 * expression, which returns <code>java.lang.String</code> values, to customize 
	 * the item labels for the pie chart.
	 */
	public JRExpression getLabelExpression();

	/**
	 * Returns the hyperlink specification for chart sections.
	 * <p>
	 * The hyperlink will be evaluated for every chart section and an image map will be created for the chart.
	 * </p>
	 * 
	 * @return hyperlink specification for chart sections
	 */
	public JRHyperlink getSectionHyperlink();

}

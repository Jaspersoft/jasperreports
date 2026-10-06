/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2026 Actian Corporation, a division of HCL Software. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Actian,
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
package net.sf.jasperreports.charts.design;

import net.sf.jasperreports.charts.util.JRAxisFormat;
import net.sf.jasperreports.engine.JRExpression;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface JRDesignCategoryPlot 
{

	public static final String PROPERTY_CATEGORY_AXIS_LABEL_EXPRESSION = "categoryAxisLabelExpression";
	
	public static final String PROPERTY_VALUE_AXIS_LABEL_EXPRESSION = "valueAxisLabelExpression";
	
	public static final String PROPERTY_DOMAIN_AXIS_MINVALUE_EXPRESSION = "domainAxisMinValueExpression";
	
	public static final String PROPERTY_DOMAIN_AXIS_MAXVALUE_EXPRESSION = "domainAxisMaxValueExpression";
	
	public static final String PROPERTY_RANGE_AXIS_MINVALUE_EXPRESSION = "rangeAxisMinValueExpression";
	
	public static final String PROPERTY_RANGE_AXIS_MAXVALUE_EXPRESSION = "rangeAxisMaxValueExpression";


	/**
	 *
	 */
	public void setCategoryAxisLabelExpression(JRExpression categoryAxisLabelExpression);

	/**
	 *
	 */
	public void setValueAxisLabelExpression(JRExpression valueAxisLabelExpression);

	/**
	 *
	 */
	public void setDomainAxisMinValueExpression(JRExpression domainAxisMinValueExpression);

	/**
	 *
	 */
	public void setDomainAxisMaxValueExpression(JRExpression domainAxisMaxValueExpression);

	/**
	 *
	 */
	public void setRangeAxisMinValueExpression(JRExpression rangeAxisMinValueExpression);

	/**
	 *
	 */
	public void setRangeAxisMaxValueExpression(JRExpression rangeAxisMaxValueExpression);

	/**
	 * 
	 */
	public void setCategoryAxisFormat(JRAxisFormat axisFormat);

	/**
	 * 
	 */
	public void setValueAxisFormat(JRAxisFormat axisFormat);
}

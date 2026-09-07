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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.charts.design.JRDesignPieDataset;
import net.sf.jasperreports.engine.xml.JRXmlConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRHyperlink;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * This dataset is useful for rendering Pie charts. Data required for such charts 
 * comes in the form of key-value pairs. Each pair represents a slice in the pie chart.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonPropertyOrder({
	"kind",
	JRXmlConstants.ATTRIBUTE_resetType,
	JRXmlConstants.ATTRIBUTE_resetGroup,
	JRXmlConstants.ATTRIBUTE_incrementType,
	JRXmlConstants.ATTRIBUTE_incrementGroup,
	JRXmlConstants.ATTRIBUTE_minPercentage,
	JRXmlConstants.ATTRIBUTE_maxCount,
	JRXmlConstants.ELEMENT_datasetRun,
	JRXmlConstants.ELEMENT_incrementWhenExpression,
	"series",
	JRXmlConstants.ELEMENT_otherKeyExpression,
	JRXmlConstants.ELEMENT_otherLabelExpression,
	JRXmlConstants.ELEMENT_otherSectionHyperlink
	})
@JsonTypeName("pie")
@JsonDeserialize(as = JRDesignPieDataset.class)
public interface JRPieDataset extends JRChartDataset
{
	/**
	 * 
	 */
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			valueType = Boolean.class,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.DATASET},
			sinceVersion = PropertyConstants.VERSION_6_0_2
			)	
	public static final String PROPERTY_IGNORE_DUPLICATED_KEY = JRPropertiesUtil.PROPERTY_PREFIX + "chart.pie.ignore.duplicated.key";
	
	/**
	 * 
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Float getMinPercentage();

	/**
	 * 
	 */
	public void setMinPercentage(Float minPercentage);

	/**
	 * 
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Integer getMaxCount();

	/**
	 * 
	 */
	public void setMaxCount(Integer maxCount);

	/**
	 * @return an array of {@link JRPieSeries} objects representing the 
	 * series for the Pie chart
	 * @see JRPieSeries
	 */
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRPieSeries[] getSeries();

	/**
	 * 
	 */
	public JRExpression getOtherKeyExpression();
	
	/**
	 * 
	 */
	public JRExpression getOtherLabelExpression();
	
	/**
	 * Returns the hyperlink specification for the special Other chart section, if present.
	 * 
	 * @return hyperlink specification for the Other chart section
	 */
	public JRHyperlink getOtherSectionHyperlink();

}

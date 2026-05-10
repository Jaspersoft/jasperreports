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
package net.sf.jasperreports.engine;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.type.DatasetResetTypeEnum;
import net.sf.jasperreports.engine.type.IncrementTypeEnum;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * Element datasets are used to represent the report data needed to generate a chart or crosstab.
 * The dataset structure may vary with each chart type or crosstab.
 * This is the superinterface for all datasets and contains common dataset properties.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonPropertyOrder({
	JRXmlConstants.ATTRIBUTE_resetType,
	JRXmlConstants.ATTRIBUTE_resetGroup,
	JRXmlConstants.ATTRIBUTE_incrementType,
	JRXmlConstants.ATTRIBUTE_incrementGroup,
	JRXmlConstants.ELEMENT_datasetRun,
	JRXmlConstants.ELEMENT_incrementWhenExpression
	})
public interface JRElementDataset extends JRCloneable, DatasetRunHolder
{

	/**
	 * Gets the reset type. This specifies the range of report data used for filling the dataset.
	 * @return one of the reset constants in {@link DatasetResetTypeEnum}
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_resetType)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_resetType, isAttribute = true)
	public DatasetResetTypeEnum getDatasetResetType(); //has multiple defaults, global Include.NON_NULL works better here

	/**
	 * Gets the selected reset group in case of reset type group.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getResetGroup();

	/**
	 * Returns the increment type. This specifies dataset values increment step.
	 * @return one of the increment constants in {@link IncrementTypeEnum}.
	 */
	@JsonInclude(Include.NON_EMPTY)
	@JacksonXmlProperty(isAttribute = true)
	public IncrementTypeEnum getIncrementType();

	/**
	 * Gets the selected increment group in case of increment type group.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getIncrementGroup();

	/**
	 *  
	 */
	public void collectExpressions(JRExpressionCollector collector);

	/**
	 * Returns the sub dataset run for this chart dataset.
	 * 
	 * @return the sub dataset run for this chart dataset
	 */
	@Override
	public JRDatasetRun getDatasetRun();
	
	/**
	 * Returns the "increment when" expression.
	 * <p>
	 * This expression determines whether a dataset will be incremented or not.
	 * <p>
	 * The expression (if not null) is evaluated before each increment of the dataset.
	 * The increment will be carried on only when the result of the evaluation is <code>Boolean.TRUE</code>;
	 * if the result is null or false, the increment will not be performed.
	 * 
	 * 
	 * @return the "increment when" expression
	 */
	public JRExpression getIncrementWhenExpression();

}

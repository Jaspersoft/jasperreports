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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.design.JRDesignDatasetRun;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * Interface of an sub dataset instantiation.
 * <p/>
 * Once a dataset is declared inside a report template, it can be used only if it's actually
 * referenced by a chart, crosstab or other component. Simply declaring a dataset at the report 
 * level does not have any effect.
 * <p/>
 * When a dataset is referenced by a report component, a <i>dataset run</i> is instantiated. The
 * dataset runs through the supplied data source performing all the variable calculations and
 * the required data grouping.
 * <p/>
 * A dataset run declaration supplies the values for the dataset parameters as well as the
 * data source through which the dataset will iterate. Optionally, a <code>java.sql.Connection</code>
 * can be passed to the dataset instead of a {@link net.sf.jasperreports.engine.JRDataSource} instance, 
 * when there is a SQL query associated with the dataset. This query is executed by the engine using the
 * JDBC connection and the <code>java.sql.ResultSet</code> object obtained is iterated through.
 * <p/>
 * Dataset runs resemble subreports in the way parameters and the data source/connection
 * are passed in.
 * <p/>
 * Charts, crosstabs and other report components can reference datasets by instantiating and configuring dataset
 * runs. If no dataset run is specified for a component, the main dataset of the report is
 * used.
 * 
 * @see net.sf.jasperreports.engine.JRDataset
 * @see net.sf.jasperreports.charts.JRChartDataset#getDatasetType()
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
@JsonPropertyOrder({
	"uuid",
	"subDataset",
	"parameter",
	"parametersMapExpression",
	"connectionExpression",
	"dataSourceExpression",
	"returnValue"
	})
@JsonDeserialize(as = JRDesignDatasetRun.class)
public interface JRDatasetRun extends JRCloneable, JRIdentifiable, JRPropertiesHolder
{
	/**
	 * Returns the sub dataset name.
	 * 
	 * @return the sub dataset name
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_subDataset)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_subDataset, isAttribute = true)
	public String getDatasetName();
	
	
	/**
	 * Returns the parameters map expression.
	 * <p>
	 * The result of this expression is used as the parameters map when instantiating the dataset.
	 * 
	 * @return the parameters map expression
	 */
	public JRExpression getParametersMapExpression();
	
	
	/**
	 * Returns the list of parameter values.
	 * 
	 * @return the list of parameter values
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_parameter)
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRDatasetParameter[] getParameters();

	
	/**
	 * Returns the DB connection expression.
	 * <p>
	 * The result of this expression is used as the DB connection when instantiating the dataset.
	 * 
	 * @return the DB connection expression
	 */
	public JRExpression getConnectionExpression();

	
	/**
	 * Returns the data source expression.
	 * <p>
	 * The result of this expression is used as the data source when instantiating the dataset.
	 * 
	 * @return the data source expression
	 */
	public JRExpression getDataSourceExpression();
	
	/**
	 * Returns the list of values to be copied from the subdataset.
	 *
	 * @return the list of copied values.
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_returnValue)
	@JacksonXmlElementWrapper(useWrapping = false)
	public List<ReturnValue> getReturnValues();

}

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
package net.sf.jasperreports.engine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.type.WhenResourceMissingTypeEnum;
import net.sf.jasperreports.engine.xml.JRXmlConstants;
import net.sf.jasperreports.jackson.util.ParameterSerializer;
import net.sf.jasperreports.jackson.util.VariableSerializer;

/**
 * Interface representing a data set that can be used in a report.
 * <p/>
 * A dataset is a concept that lies somewhere between a data source and a subreport.
 * Datasets allow the engine to iterate through some virtual records, just as data sources do,
 * but they also enable calculations and data grouping during this iteration using variables
 * and groups. Because dataset declarations contain parameters, fields, variables, and
 * groups, they closely resemble subreports, but they completely lack any visual content
 * (that is, they have no sections or layout information at the dataset level).
 * <p/>
 * Datasets are useful for chart, crosstab and other components generation when you need to iterate through
 * data that is not the main report data source itself, in order to gather data for the component or
 * perform data bucketing for the crosstab. Before datasets, the use of subreports was the
 * only way to iterate through virtual records that were nested collections of virtual records
 * rather than part of the current report data source. However, subreports come with
 * unwanted visual settings and tend to complicate layout and report template structure.
 * <p/>
 * A data set consists of parameters, fields, variables, groups and an optional query.
 * When a data set gets instantiated, parameter values and a data source is passed to it.
 * <h3>Main Dataset</h3>
 * The report data source, along with the parameters, fields, variables, and groups declared
 * at the report level, represent the building blocks of the <i>main dataset</i> for the report. All
 * report templates implicitly declare and use this main dataset.
 * <p/>
 * The main dataset is responsible for iterating through the data source records, calculating
 * variables, filtering out records, and estimating group breaks during the report-filling
 * process.
 * <p/>
 * A report has one main dataset and multiple subdatasets that can be instantiated by charts, crosstabs 
 * and other components.
 * <h3>Subdatasets</h3>
 * User-defined datasets (or <i>subdatasets</i>) are declared in JRXML using the 
 * <code>&lt;subDataset&gt;</code> tag.
 * <p/>
 * The engine does not necessarily use a subdataset, once defined in the report. Subdatasets are 
 * instantiated and iterate through the supplied data source to calculate dataset variable values 
 * only if they are referenced by a chart, crosstab or other component's dataset run.
 * <p/>
 * Just like subreports, datasets, when instantiated, expect to receive parameter values and a
 * data source to iterate through. As a convenience, datasets can have an associated SQL
 * query that is executed by the engine if a java.sql.Connection object is supplied to
 * them instead of the usual data source.
 * <p/>
 * Datasets can also have scriptlets associated with them to allow making callbacks to userdefined
 * business logic during the dataset iteration, if further data manipulation is needed.
 * 
 * @see net.sf.jasperreports.engine.JRDatasetRun
 * @see net.sf.jasperreports.engine.JRReport#getMainDataset()
 * @see net.sf.jasperreports.engine.JRReport#getDatasets()
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
@JsonPropertyOrder({
	JRXmlConstants.ATTRIBUTE_name,
	JRXmlConstants.ATTRIBUTE_scriptletClass,
	JRXmlConstants.ATTRIBUTE_resourceBundle,
	JRXmlConstants.ATTRIBUTE_whenResourceMissingType,
	JRXmlConstants.ATTRIBUTE_uuid,
	"properties",
	JRXmlConstants.ELEMENT_property,
	"propertyExpressions",
	JRXmlConstants.ELEMENT_propertyExpression,
	"scriptlets",
	JRXmlConstants.ELEMENT_scriptlet,
	"parameters", // for JSON 
	JRXmlConstants.ELEMENT_parameter, // for XML
	"query",
	"fields", // for JSON 
	JRXmlConstants.ELEMENT_field, // for XML
	"sortFields", // for JSON 
	JRXmlConstants.ELEMENT_sortField, // for XML
	"variables", // for JSON 
	JRXmlConstants.ELEMENT_variable, // for XML
	JRXmlConstants.ELEMENT_filterExpression,
	"groups", // for JSON 
	JRXmlConstants.ATTRIBUTE_group // for XML
	})
@JsonDeserialize(as = JRDesignDataset.class)
public interface JRDataset extends JRPropertiesHolder, JRCloneable, JRIdentifiable
{

	
	/**
	 * Returns the dataset name.
	 * 
	 * @return the name of the dataset 
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getName();

	
	/**
	 * The name of the scriptlet class to be used when iterating this dataset.
	 * 
	 * @return the scriplet class name
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getScriptletClass();

	
	/**
	 * Returns the dataset's scriptlets.
	 * 
	 * @return the dataset's scriptlets
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_scriptlet)
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRScriptlet[] getScriptlets();


	/**
	 * Returns the list of dynamic/expression-based properties for this dataset.
	 * 
	 * @return an array containing the expression-based properties of this dataset
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_propertyExpression)
	@JacksonXmlElementWrapper(useWrapping = false)
	public DatasetPropertyExpression[] getPropertyExpressions();


	/**
	 * Returns the dataset's parameters.
	 * 
	 * @return the dataset's parameters
	 */
	@JsonSerialize(contentUsing = ParameterSerializer.class)
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_parameter)
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRParameter[] getParameters();

	
	/**
	 * Returns the query of the dataset.
	 * <p>
	 * The query is used by passing a connection is passed to the dataset when instantiating.
	 * 
	 * @return the query of the dataset
	 */
	public JRQuery getQuery();

	
	/**
	 * Returns the dataset's fields.
	 * 
	 * @return the dataset's fields
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_field)
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRField[] getFields();

	
	/**
	 * Returns the dataset's sort fields.
	 * 
	 * @return the dataset's sort fields
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_sortField)
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRSortField[] getSortFields();

	
	/**
	 * Returns the dataset's variables.
	 * 
	 * @return the dataset's variables
	 */
	@JsonSerialize(contentUsing = VariableSerializer.class)
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_variable)
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRVariable[] getVariables();

	
	/**
	 * Returns the dataset's groups.
	 * 
	 * @return the dataset's groups
	 */
	@JacksonXmlProperty(localName = "group")
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRGroup[] getGroups();
	
	
	/**
	 * Decides whether this dataset is the main report dataset or a sub dataset.
	 * 
	 * @return <code>true</code> if and only if this dataset is the main report dataset
	 */
	@JsonIgnore
	public boolean isMainDataset();


	/**
	 * Returns the resource bundle base name.
	 * <p>
	 * The resource bundle is used when evaluating expressions.
	 * 
	 * @return the resource bundle base name
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getResourceBundle();


	/**
	 * Returns the resource missing handling type.
	 * 
	 * @return the resource missing handling type
	 */
	@JsonInclude(Include.NON_EMPTY)
	@JacksonXmlProperty(isAttribute = true)
	public WhenResourceMissingTypeEnum getWhenResourceMissingType();
	
	
	/**
	 * Sets the resource missing handling type.
	 * @param whenResourceMissingType the resource missing handling type
	 */
	public void setWhenResourceMissingType(WhenResourceMissingTypeEnum whenResourceMissingType);

	
	/**
	 * Returns the dataset filter expression.
	 * <p>
	 * This expression is used to filter the rows of the 
	 * {@link JRDataSource data source} that this dataset will iterate on.
	 * </p>
	 * <p>
	 * This expression (if not null) is evaluated immediately after a new row is 
	 * {@link JRDataSource#next() produced} by the data source.
	 * The evaluation is performed using field and variable values corresponding to the new row. 
	 * When the result of the evaluation is <code>Boolean.TRUE</code> the row gets processed by the report
	 * filling engine.
	 * When the result is null or <code>Boolean.FALSE</code>, the current row will be skipped and the datasource will be asked for the next row.
	 * </p>
	 * 
	 * @return the dataset filter expression
	 */
	public JRExpression getFilterExpression();
}

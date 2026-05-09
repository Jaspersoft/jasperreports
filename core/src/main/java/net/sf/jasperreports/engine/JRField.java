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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * An abstract representation of a data source field. Each row in a dataset consists of one or more fields with unique
 * names. These names can be used in report expressions.
 * <h3>Report Fields</h3>
 * The report fields represent the only way to map data from the data source into the report
 * template and to use this data in report expressions to obtain the desired output. 
 * <p/>
 * When declaring report fields, make sure that the data source supplied at report-filling
 * time can provide values for all those fields.
 * <p/>
 * For example, if a
 * {@link net.sf.jasperreports.engine.JRResultSetDataSource} implementation is used along with 
 * the report's SQL query, make sure that there is a column for each field in the
 * result set obtained after the execution of the query. The corresponding column must bear
 * the same name and have the same data type as the field that maps it.
 * <p/>
 * If a field is declared without a corresponding column in the result set, an exception will
 * be thrown at runtime. The columns in the result set produced by the execution of the
 * SQL query that do not have corresponding fields in the report template will not affect the
 * report-filling operations, but they also won't be accessible for display on the report.
 * <p/>
 * Following are described the components of a report field definition.
 * <h3>Field Name</h3>
 * The <code>name</code> attribute of the <code>&lt;field&gt;</code> element is mandatory. It 
 * lets you reference the field in report expressions by name.
 * <h3>Field Class</h3>
 * The second attribute for a report field specifies the class name for the field values. Its
 * default value is <code>java.lang.String</code>, but it can be changed to any class available at
 * runtime. Regardless of the type of a report field, the engine makes the appropriate cast in
 * report expressions in which the <code>$F{}</code> token is used, making manual casts unnecessary.
 * <h3>Field Description</h3>
 * This additional text chunk can prove very useful when implementing a custom data
 * source, for example. You could store in it a key, or whatever information you might need
 * in order to retrieve the field's value from the custom data source at runtime.
 * <p/>
 * By using the optional <code>&lt;fieldDesciption&gt;</code> element instead of the field name, you can
 * easily overcome restrictions of field-naming conventions when retrieving the field values
 * from the data source:
 * <pre>
 *   &lt;field name="PersonName" class="java.lang.String"
 *     &lt;fieldDesciption&gt;PERSON NAME&lt;/fieldDesciption&gt;
 *   &lt;/field&gt;</pre>
 * The field description is less important than in previous versions of the library because
 * now even the field's name accepts dots, spaces, and other special characters.
 * <h3>Custom Field Properties</h3>
 * Just like the report template and report parameters, report fields can have custom-defined
 * properties, too. This comes in addition to the field description, which can be considered a
 * built-in report field property. Custom properties are useful in some cases where more
 * information or meta data needs to be associated with the report field definition. This
 * additional information can be leveraged by query executer or data source
 * implementations.
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonPropertyOrder({
	"name",
	"class",
	"description",
	"propertyExpression"
	})
@JsonDeserialize(as = JRDesignField.class)
public interface JRField extends JRPropertiesHolder, JRCloneable
{


	/**
	 * Gets the field unique name.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getName();
		
	/**
	 * Gets the field optional description.
	 */
	@JacksonXmlCData
	public String getDescription();
		
	/**
	 * Sets the field description.
	 */
	public void setDescription(String description);
		
	/**
	 * Gets the field value class. Field types cannot be primitives.
	 */
	@JsonIgnore
	public Class<?> getValueClass();
		
	/**
	 * Gets the field value class name.
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_class)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_class, isAttribute = true)
	public String getValueClassName();
		
	/**
	 * Returns the list of dynamic/expression-based properties for this field.
	 * 
	 * @return an array containing the expression-based properties of this field
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_propertyExpression)
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRPropertyExpression[] getPropertyExpressions();


}

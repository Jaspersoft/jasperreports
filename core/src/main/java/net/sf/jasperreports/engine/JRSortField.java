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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.design.JRDesignSortField;
import net.sf.jasperreports.engine.type.SortFieldTypeEnum;
import net.sf.jasperreports.engine.type.SortOrderEnum;


/**
 * Provides support for in-memory field-based data source sorting.
 * <p/>
 * JasperReports supports in-memory field-based data source sorting. This functionality can
 * be used, for instance, when data sorting is required and the data source implementation
 * does not support it (as in the case of the CSV data source).
 * <p/>
 * The sorting is activated by the presence of one or more <code>&lt;sortField&gt;</code> elements in the
 * report template. When at least one sort field is specified for the report, the original report
 * data source (either passed directly or provided by a query executer) is passed to a sorted 
 * {@link net.sf.jasperreports.engine.data.IndexedDataSource} instance that fetches all the records 
 * from it, performs an in-memory
 * sort according to the specified fields, and replaces the original data source in the
 * report-filling process.
 * <p/>
 * The sort field name should coincide with a report field name. Fields used for sorting
 * should have types that implement <code>java.util.Comparable</code>. Sorting will be performed
 * using the natural order for all fields except those of type <code>java.lang.String</code>, for which
 * a collator corresponding to the report fill locale is used.
 * <p/>
 * When several sort fields are specified, the sorting will be performed using the fields as
 * sort keys in the order in which they appear in the report template.
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @see net.sf.jasperreports.engine.data.IndexedDataSource
 * @see net.sf.jasperreports.engine.fill.DatasetSortUtil
 * @see net.sf.jasperreports.engine.fill.SortedDataSource
 */
@JsonPropertyOrder({
	"name",
	"order",
	"type"
	})
@JsonDeserialize(as = JRDesignSortField.class)
public interface JRSortField extends JRCloneable
{


	/**
	 * Gets the sort field name.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getName();
		
	/**
	 * Gets the sort order for the field.
	 */
	@JsonInclude(Include.NON_EMPTY)
	@JacksonXmlProperty(isAttribute = true)
	public SortOrderEnum getOrder();
		
	/**
	 * Gets the type of the sort field.
	 */
	@JsonInclude(Include.NON_EMPTY)
	@JacksonXmlProperty(isAttribute = true)
	public SortFieldTypeEnum getType();

}

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
package net.sf.jasperreports.components.list;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import net.sf.jasperreports.components.ComponentsExtensionsRegistryFactory;
import net.sf.jasperreports.crosstabs.JRCrosstab;
import net.sf.jasperreports.engine.DatasetRunHolder;
import net.sf.jasperreports.engine.JRCloneable;
import net.sf.jasperreports.engine.JRDatasetRun;
import net.sf.jasperreports.engine.JRVisitable;
import net.sf.jasperreports.engine.component.Component;
import net.sf.jasperreports.engine.type.PrintOrderEnum;

/**
 * List component interface.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
@JsonPropertyOrder({
	"printOrder",
	"ignoreWidth",
	"datasetRun",
	"contents"
	})
@JsonTypeName(ComponentsExtensionsRegistryFactory.LIST_COMPONENT_NAME)
@JsonDeserialize(as = StandardListComponent.class)
public interface ListComponent extends Component, JRCloneable, JRVisitable, DatasetRunHolder
{

	/**
	 * Returns the subdataset run information that will be used by this list.
	 * 
	 * <p>
	 * This information is required to instantiate a subdataset from the report.
	 * The data produced by the subdataset is fed to the list item contents.
	 * 
	 * @return the subdataset run information
	 */
	@Override
	JRDatasetRun getDatasetRun();

	/**
	 * Returns the list item contents.
	 * 
	 * <p>
	 * Each record produced by the list subdataset is used to fill the list
	 * item contents and the result is included in the generated report.
	 * 
	 * @return the list item contents
	 */
	ListContents getContents();

	/**
	 * Returns the print order of the list cells.
	 * 
	 * <p>
	 * The list cells can be either printed vertically one beneath another
	 * (on a single column), or horizontally on rows of 2 or more columns.
	 * </p>
	 * 
	 * <p>
	 * The default print order (used when no explicit order has been set)
	 * is vertical.
	 * </p>
	 * 
	 * @return the list print order if set, one of
	 * <ul>
	 * <li>{@link PrintOrderEnum#VERTICAL}</li>
	 * <li>{@link PrintOrderEnum#HORIZONTAL}</li>
	 * </ul>
	 * @see ListContents#getWidth()
	 */
	public PrintOrderEnum getPrintOrder();
	
	/**
	 * Returns the flag that determines whether the element width is to be ignored
	 * when filling this list.
	 * 
	 * <p>
	 * This flag only applies to horizontally filled reports.  If the flag is set,
	 * the list will be filled on a single row.
	 * </p>
	 * 
	 * <p>
	 * By default, the flag is not set.
	 * </p>
	 * 
	 * @see #getPrintOrder()
	 * @see JRCrosstab#setIgnoreWidth(Boolean)
	 */
	public Boolean getIgnoreWidth();

}
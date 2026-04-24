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
package net.sf.jasperreports.components.table;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.components.ComponentsExtensionsRegistryFactory;
import net.sf.jasperreports.engine.DatasetRunHolder;
import net.sf.jasperreports.engine.JRCloneable;
import net.sf.jasperreports.engine.JRDatasetRun;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRVisitable;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.component.Component;
import net.sf.jasperreports.engine.type.HorizontalPosition;
import net.sf.jasperreports.engine.xml.JRXmlConstants;
import net.sf.jasperreports.jackson.util.JRXmlSince;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
@JsonTypeName(ComponentsExtensionsRegistryFactory.TABLE_COMPONENT_NAME)
@JsonDeserialize(as = StandardTable.class)
public interface TableComponent extends Component, JRCloneable, JRVisitable, DatasetRunHolder
{
	/**
	 * Property that specifies a default value for the <code>whenNoDataType</code> attribute of table components.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_TABLE,
			defaultValue = "Blank",
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_0_0,
			valueType = WhenNoDataTypeTableEnum.class
			)
	public static final String CONFIG_PROPERTY_WHEN_NO_DATA_TYPE = JRPropertiesUtil.PROPERTY_PREFIX + "components.table.when.no.data.type";

	/**
	 * Property that specifies a default value for the <code>horizontalPosition</code> attribute of table components.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_TABLE,
			defaultValue = "Left",
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_7_0_2,
			valueType = HorizontalPosition.class
			)
	public static final String CONFIG_PROPERTY_HORIZONTAL_POSITION = JRPropertiesUtil.PROPERTY_PREFIX + "components.table.horizontal.position";

	/**
	 * Property that specifies a default value for the <code>shrinkWidth</code> attribute of table components.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_TABLE,
			defaultValue = "true",
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_7_0_2,
			valueType = Boolean.class
			)
	public static final String CONFIG_PROPERTY_SHRINK_WIDTH = JRPropertiesUtil.PROPERTY_PREFIX + "components.table.shrink.width";

	/**
	 * Property that specifies a default value for the <code>weight</code> attribute of a table component column.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_TABLE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.TABLE},
			sinceVersion = PropertyConstants.VERSION_7_0_2,
			valueType = Integer.class
			)
	public static final String CONFIG_PROPERTY_COLUMN_WEIGHT = JRPropertiesUtil.PROPERTY_PREFIX + "components.table.column.weight";

	@Override
	JRDatasetRun getDatasetRun();

	@JacksonXmlProperty(localName = "column")
	@JacksonXmlElementWrapper(useWrapping = false)
	List<BaseColumn> getColumns();
	
	@JacksonXmlProperty(isAttribute = true)
	WhenNoDataTypeTableEnum getWhenNoDataType();
	
	/**
	 * Returns the position of the table columns within the table element box.
	 * 
	 * @return the position of the table columns within the table element box, 
	 * <code>null</code> if no position is specified
	 */
	@JRXmlSince(JRConstants.VERSION_7_0_2)
	@JacksonXmlProperty(isAttribute = true)
	HorizontalPosition getHorizontalPosition();

	/**
	 * Specifies if the table box should shrink to accommodate the smaller total width of visible columns.
	 */
	@JRXmlSince(JRConstants.VERSION_7_0_2)
	@JacksonXmlProperty(isAttribute = true)
	Boolean shrinkWidth();

	Row getTableHeader();
	
	Row getTableFooter();
	
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_groupHeader)
	@JacksonXmlElementWrapper(useWrapping = false)
	List<GroupRow> getGroupHeaders();
	
	Row getGroupHeader(String groupName);
	
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_groupFooter)
	@JacksonXmlElementWrapper(useWrapping = false)
	List<GroupRow> getGroupFooters();
	
	Row getGroupFooter(String groupName);
	
	Row getColumnHeader();
	
	Row getColumnFooter();
	
	Row getDetail();
	
	BaseCell getNoData();
}

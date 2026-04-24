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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.JRCloneable;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRIdentifiable;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertyExpression;
import net.sf.jasperreports.engine.xml.JRXmlConstants;
import net.sf.jasperreports.jackson.util.JRXmlSince;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "kind")
@JsonSubTypes({
	@JsonSubTypes.Type(value = Column.class),
	@JsonSubTypes.Type(value = ColumnGroup.class)
})
public interface BaseColumn extends JRCloneable, JRPropertiesHolder, JRIdentifiable
{

	JRExpression getPrintWhenExpression();
	
	Cell getTableHeader();
	
	Cell getTableFooter();
	
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_groupHeader)
	@JacksonXmlElementWrapper(useWrapping = false)
	List<GroupCell> getGroupHeaders();
	
	Cell getGroupHeader(String groupName);
	
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_groupFooter)
	@JacksonXmlElementWrapper(useWrapping = false)
	List<GroupCell> getGroupFooters();
	
	Cell getGroupFooter(String groupName);
	
	Cell getColumnHeader();
	
	Cell getColumnFooter();
	
	@JacksonXmlProperty(isAttribute = true)
	Integer getWidth();
	
	/**
	 * Specifies the weight of the column when table columns are to be resized to occupy the entire width of the table component.
	 * A weight based system is used to resize columns, with columns having bigger weight taking more of the extra space available.
	 * Negative values represent multipliers of column width.
	 * For example, weight value -1 means the weight of column will be equals to its width value, while weight -2 means
	 * the column weight will be twice the value of the column width.
	 * A typical case would be to have weight -1 for all columns, meaning they will all grow proportionally to their initial width.
	 * Another typical case is to have weight 1 for all columns, meaning they will all grow with an equal amount, regardless of their initial width.
	 */
	@JRXmlSince(JRConstants.VERSION_7_0_2)
	@JacksonXmlProperty(isAttribute = true)
	Integer getWeight();

	<R> R visitColumn(ColumnVisitor<R> visitor);

	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_propertyExpression)
	@JacksonXmlElementWrapper(useWrapping = false)
	JRPropertyExpression[] getPropertyExpressions();

}

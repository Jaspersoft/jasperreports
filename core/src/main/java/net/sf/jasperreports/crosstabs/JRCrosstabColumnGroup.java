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
package net.sf.jasperreports.crosstabs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabColumnGroup;
import net.sf.jasperreports.crosstabs.type.CrosstabColumnPositionEnum;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * Crosstab column group interface.
 *
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
@JsonPropertyOrder({
	JRXmlConstants.ATTRIBUTE_name,
	"totalPosition",
	JRXmlConstants.ATTRIBUTE_height,
	JRXmlConstants.ATTRIBUTE_position,
	"mergeHeaderCells",
	"bucket",
	"header",
	"totalHeader",
	"crosstabHeader"
	})
@JsonDeserialize(as = JRDesignCrosstabColumnGroup.class)
public interface JRCrosstabColumnGroup extends JRCrosstabGroup
{
	/**
	 * Returns the height of the group headers.
	 * 
	 * @return the height of the group headers
	 * @see JRCrosstabGroup#getHeader()
	 * @see JRCrosstabGroup#getTotalHeader()
	 */
	@JacksonXmlProperty(isAttribute = true)
	public int getHeight();
	
	
	/**
	 * Returns the position of the header contents for header stretching.
	 * <p>
	 * The column group headers stretch horizontally when there are multiple sub group entries.
	 * The header contents will be adjusted to the new width depending on this attribute:
	 * <ul>
	 * 	<li>{@link CrosstabColumnPositionEnum#LEFT CrosstabColumnPositionEnum.LEFT} - the contents will be rendered on the left side of the header</li>
	 * 	<li>{@link CrosstabColumnPositionEnum#CENTER CrosstabColumnPositionEnum.CENTER} - the contents will be rendered on the center of the header</li>
	 * 	<li>{@link CrosstabColumnPositionEnum#RIGHT CrosstabColumnPositionEnum.RIGHT} - the contents will be rendered on the right side of the header</li>
	 * 	<li>{@link CrosstabColumnPositionEnum#STRETCH CrosstabColumnPositionEnum.STRETCH} - the contents will be proportionally stretched to the new header size</li>
	 * </ul>
	 * 
	 * @return the position of the header contents for header stretching
	 */
	@JsonInclude(Include.NON_EMPTY)
	@JacksonXmlProperty(isAttribute = true)
	public CrosstabColumnPositionEnum getPosition();
	
	/**
	 * Returns the crosstab header cell of the column group.
	 * 
	 * <p>
	 * The cell will be rendered at the left of the corresponding row of column headers, 
	 * potentially overlapping {@link JRCrosstab#getHeaderCell() the crosstab header cell}.
	 * </p>
	 * 
	 * <p>
	 * The width of the cell is the total width of row group headers, 
	 * and the height is the height of the corresponding column header.
	 * </p>
	 * 
	 * @return the crosstab header cell of the column group, or <code>null</code> if no header cell is present
	 * 
	 * @see JRCrosstab#getHeaderCell()
	 * @see #getHeight()
	 */
	public JRCellContents getCrosstabHeader();
}

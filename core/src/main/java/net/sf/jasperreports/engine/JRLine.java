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
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.design.JRDesignLine;
import net.sf.jasperreports.engine.type.LineDirectionEnum;


/**
 * An abstract representation of a graphic element representing a straight line.
 * <p/>
 * When displaying a line element, JasperReports draws one of the two diagonals of the
 * rectangle represented by the <code>x</code>, <code>y</code>, <code>width</code>, and 
 * <code>height</code> attributes specified for this element.
 * <p/>
 * The <code>direction</code> attribute (see {@link #getDirection()}) determines which 
 * one of the two diagonals of the rectangle  should be drawn:
 * <ul>
 * <li><code>TopDown</code> - draws a diagonal line from the top-left corner of the
 * rectangle to the bottom-right corner.</li>
 * <li><code>BottomUp</code> - draws a diagonal line from the bottom-left corner to the
 * upper-right corner.</li>
 * </ul>
 * <p/>
 * The default direction for a line is top-down.
 * <p/>
 * One can draw vertical lines by specifying <code>width="1"</code> and horizontal lines by setting
 * <code>height="1"</code>. For vertical lines, the direction is not important.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonPropertyOrder({
	"kind",
	"uuid",
	"key",
	"x",
	"y",
	"width",
	"height",
	"forecolor",
	"backcolor",
	"mode",
	"positionType",
	"stretchType",
	"printRepeatedValues",
	"printInFirstWholeBand",
	"printWhenDetailOverflows",
	"printWhenGroupChanges",
	"removeLineWhenBlank",
	"fill",
	"direction",
	"style",
	"printWhenExpression",
	"styleExpression",
	"property",
	"propertyExpression",
	"pen"
	})
@JsonTypeName("line")
@JsonDeserialize(as = JRDesignLine.class)
public interface JRLine extends JRGraphicElement
{


	/**
	 * Gets the line direction.
	 * @return a value representing one of the line direction constants in {@link LineDirectionEnum}
	 */
	@JsonInclude(Include.NON_EMPTY)
	@JacksonXmlProperty(isAttribute = true)
	public LineDirectionEnum getDirection();
	
	/**
	 * Sets the line direction.
	 * @param lineDirection a value representing one of the line direction constants in {@link LineDirectionEnum}
	 */
	public void setDirection(LineDirectionEnum lineDirection);

}

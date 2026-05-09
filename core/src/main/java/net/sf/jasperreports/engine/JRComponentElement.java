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

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import net.sf.jasperreports.engine.component.Component;
import net.sf.jasperreports.engine.design.JRDesignComponentElement;

/**
 * A report element that wraps an abstract component.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
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
	"style",
	"printWhenExpression",
	"styleExpression",
	"property",
	"propertyExpression",
	"component"
	})
@JsonTypeName("component")
@JsonDeserialize(as = JRDesignComponentElement.class)
public interface JRComponentElement extends JRElement
{
	String PROPERTY_COMPONENT_NAME = JRPropertiesUtil.PROPERTY_PREFIX + "components.name";
	
	/**
	 * Returns the component instance wrapped by this element.
	 * 
	 * @return the component instance
	 */
	Component getComponent();

}

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

import net.sf.jasperreports.engine.design.JRDesignScriptlet;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonPropertyOrder({
	JRXmlConstants.ATTRIBUTE_name,
	JRXmlConstants.ATTRIBUTE_class,
	"description",
	JRXmlConstants.ELEMENT_propertyExpression
	})
@JsonDeserialize(as = JRDesignScriptlet.class)
public interface JRScriptlet extends JRPropertiesHolder, JRCloneable
{

	String SCRIPTLET_PARAMETER_NAME_SUFFIX = "_SCRIPTLET";
	
	/**
	 *
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getName();
		
	/**
	 *
	 */
	@JacksonXmlCData
	public String getDescription();
		
	/**
	 *
	 */
	public void setDescription(String description);
		
	/**
	 *
	 */
	@JsonIgnore
	public Class<?> getValueClass();

	/**
	 *
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_class)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_class, isAttribute = true)
	public String getValueClassName();

	/**
	 * Returns the list of dynamic/expression-based properties for this scriptlet.
	 * 
	 * @return an array containing the expression-based properties of this scriptlet
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_propertyExpression)
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRPropertyExpression[] getPropertyExpressions();

}

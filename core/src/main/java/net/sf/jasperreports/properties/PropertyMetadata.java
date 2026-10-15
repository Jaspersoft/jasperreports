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
package net.sf.jasperreports.properties;

import java.util.List;

import net.sf.jasperreports.annotations.properties.PropertyScope;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public interface PropertyMetadata
{
	
	/**
	 * The property name.
	 * 
	 * @return the property name
	 */
	String getName();
	
	String getCategory();
	
	/**
	 * The name of the class where the property constant is declared.
	 * 
	 * @return the class name that contains the property constant declaration
	 */
	String getConstantDeclarationClass();
	
	/**
	 * The name of the property constant declaration (static) field.
	 * 
	 * @return the name of the  property constant declaration field
	 * @see #getConstantDeclarationClass()
	 */
	String getConstantFieldName();
	
	/**
	 * Short label for the property.
	 * 
	 * @return short property label
	 */
	String getLabel();
	
	/**
	 * Short description of the property.
	 * 
	 * @return short property description
	 */
	String getDescription();
	
	/**
	 * The default value of the property.
	 * 
	 * @return the default property value
	 */
	String getDefaultValue();
	
	/**
	 * The list of scopes at which the property can be set.
	 * 
	 * @return the list of scopes at which the property can be set
	 */
	List<PropertyScope> getScopes();
	
	/**
	 * The list of conditions under which the property is used
	 * (for instance using a specific query executer).
	 * 
	 * @return the list of conditions under which the property is used
	 */
	List<String> getScopeQualifications();
	
	/**
	 * The version in which the property was introduced.
	 * 
	 * @return the version in which the property was introduced
	 */
	String getSinceVersion();

	/**
	 * The type (as class name) of values that the property expects.
	 * 
	 * @return the type of values that the property expects
	 */
	String getValueType();
	
	boolean isDeprecated();
}

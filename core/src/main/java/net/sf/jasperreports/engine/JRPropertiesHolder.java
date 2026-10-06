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
package net.sf.jasperreports.engine;

import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import net.sf.jasperreports.jackson.util.PropertiesMapDeserializer;

/**
 * Interface to be implemented by objects that have properties attached.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public interface JRPropertiesHolder
{

	/**
	 * Checks whether the object has any properties.
	 * 
	 * @return whether the object has any properties
	 */
	public boolean hasProperties();
	
	/**
	 * Returns this object's properties map.
	 *  
	 * @return this object's properties map
	 */
	@JsonProperty("property")
	@JsonDeserialize(using = PropertiesMapDeserializer.class)
	@JsonMerge
	public JRPropertiesMap getPropertiesMap();

	/**
	 * Returns the parent properties holder, whose properties are used as defaults
	 * for this object.
	 * 
	 * @return the parent properties holder, or <code>null</code> if no parent
	 */
	public JRPropertiesHolder getParentProperties();
	
}

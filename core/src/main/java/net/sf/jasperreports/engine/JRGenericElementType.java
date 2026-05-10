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

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * A key identifying a generic element type.
 * 
 * <p>
 * This key is used to resolve registered export handlers for generic print
 * elements.
 * 
 * <p>
 * A type key is composed of a namespace (which is usually a URI associated with
 * the organization that implemented the generic element type) and a type name.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
@JsonPropertyOrder({
	JRXmlConstants.ATTRIBUTE_namespace,
	JRXmlConstants.ATTRIBUTE_name
	})
public class JRGenericElementType implements Serializable
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private final String namespace;
	private final String name;

	/**
	 * Creates a generic element type key.
	 * 
	 * @param namespace the type namespace
	 * @param name the type name
	 */
	@JsonCreator
	public JRGenericElementType(
		@JsonProperty("namespace") String namespace, 
		@JsonProperty("name") String name
		)
	{
		this.name = name;
		this.namespace = namespace;
	}
	
	/**
	 * Returns the type namespace.
	 * 
	 * @return the type namespace
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getNamespace()
	{
		return namespace;
	}

	/**
	 * Returns the type name.
	 * 
	 * @return the type name
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getName()
	{
		return name;
	}
	
	@Override
	public int hashCode()
	{
		int hash = 17;
		hash = 37 * hash + namespace.hashCode();
		hash = 37 * hash + name.hashCode();
		return hash;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}

		if (!(o instanceof JRGenericElementType))
		{
			return false;
		}
		
		JRGenericElementType key = (JRGenericElementType) o;
		return namespace.equals(key.namespace)
				&& name.equals(key.name);
	}

	@Override
	public String toString()
	{
		return namespace + "#" + name;
	}
	
}

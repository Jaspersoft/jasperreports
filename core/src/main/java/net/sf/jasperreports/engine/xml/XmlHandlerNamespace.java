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
package net.sf.jasperreports.engine.xml;

/**
 * XML namespace information used by an XML handler.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class XmlHandlerNamespace
{
	
	private String namespaceURI;
	private String publicSchemaLocation;
	private String internalSchemaResource;
	
	/**
	 * Returns the namespace used by the handler.
	 * 
	 * <p>
	 * The XML representation of the values will use this namespace.
	 * 
	 * @return the namespace used by the handler
	 */
	public String getNamespaceURI()
	{
		return namespaceURI;
	}
	
	/**
	 * Sets the namespace used by the handler.
	 * 
	 * @param namespaceURI
	 */
	public void setNamespaceURI(String namespaceURI)
	{
		this.namespaceURI = namespaceURI;
	}
	
	/**
	 * Returns the public location of the handler XML schema. 
	 * 
	 * <p>
	 * This would be listed in <code>schemaLocation</code> XML attributes.
	 * 
	 * @return the public location of the handler XML schema
	 */
	public String getPublicSchemaLocation()
	{
		return publicSchemaLocation;
	}
	
	/**
	 * Sets the public location of the handler XML schema.
	 * 
	 * @param publicSchemaLocation
	 */
	public void setPublicSchemaLocation(String publicSchemaLocation)
	{
		this.publicSchemaLocation = publicSchemaLocation;
	}
	
	/**
	 * Returns a resource name that resolves to an internal XML schema
	 * resource.
	 * 
	 * <p>
	 * If not null, the resource (which needs to be present on the classpath)
	 * will be used when parsing XML fragments instead of the public XML schema.
	 * 
	 * @return the name of the internal XML schema resource
	 */
	public String getInternalSchemaResource()
	{
		return internalSchemaResource;
	}
	
	/**
	 * Sets the name of the internal XML schema resource.
	 * 
	 * @param internalSchemaResource
	 */
	public void setInternalSchemaResource(String internalSchemaResource)
	{
		this.internalSchemaResource = internalSchemaResource;
	}

}

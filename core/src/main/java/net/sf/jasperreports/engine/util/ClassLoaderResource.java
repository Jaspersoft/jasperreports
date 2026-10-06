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
package net.sf.jasperreports.engine.util;

import java.net.URL;

/**
 * Details of a resource found on a classloader.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see JRLoader#getClassLoaderResources(String)
 */
public class ClassLoaderResource
{

	private final URL url;
	private final ClassLoader classLoader;
	
	/**
	 * Creates a classloader resource.
	 * 
	 * @param url the URL of the resource
	 * @param classLoader the classloader on which it was found
	 */
	public ClassLoaderResource(URL url, ClassLoader classLoader)
	{
		this.url = url;
		this.classLoader = classLoader;
	}

	/**
	 * Returns the URL of the resource.
	 * 
	 * @return the resource URL
	 */
	public URL getUrl()
	{
		return url;
	}

	/**
	 * Returns the classloader on which the resource was found.
	 * 
	 * @return the classloader that found the resource
	 */
	public ClassLoader getClassLoader()
	{
		return classLoader;
	}
	
}

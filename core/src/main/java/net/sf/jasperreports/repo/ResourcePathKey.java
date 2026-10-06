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
package net.sf.jasperreports.repo;

import net.sf.jasperreports.engine.util.ObjectUtils;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ResourcePathKey
{
	
	public static ResourcePathKey absolute(String resoucePath)
	{
		return new ResourcePathKey(null, resoucePath);
	}
	
	public static ResourcePathKey inContext(RepositoryContext repositoryContext, String resoucePath)
	{
		String contextPath = repositoryContext == null || repositoryContext.getResourceContext() == null ? null
				: repositoryContext.getResourceContext().getContextLocation();
		return new ResourcePathKey(contextPath, resoucePath);
	}

	private final String contextPath;
	
	private final String resoucePath;

	public ResourcePathKey(String contextPath, String resoucePath)
	{
		this.contextPath = contextPath;
		this.resoucePath = resoucePath;
	}

	public String getContextPath()
	{
		return contextPath;
	}

	public String getResoucePath()
	{
		return resoucePath;
	}

	@Override
	public int hashCode()
	{
		int hash = 47;
		hash = 29 * hash + ObjectUtils.hashCode(contextPath);
		hash = 29 * hash + ObjectUtils.hashCode(resoucePath);
		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof ResourcePathKey))
		{
			return false;
		}
		
		ResourcePathKey o = (ResourcePathKey) obj;
		return ObjectUtils.equals(contextPath, o.contextPath)
				&& ObjectUtils.equals(resoucePath, o.resoucePath);
	}

	@Override
	public String toString()
	{
		return "context " + contextPath + ", path " + resoucePath;
	}
	
}

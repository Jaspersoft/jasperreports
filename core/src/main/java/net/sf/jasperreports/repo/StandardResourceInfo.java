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

import java.io.File;
import java.nio.file.Path;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class StandardResourceInfo implements ResourceInfo
{

	public static StandardResourceInfo from(File resourceFile)
	{
		StandardResourceInfo info = new StandardResourceInfo();
		info.setRepositoryResourceLocation(resourceFile.getPath());
		info.setRepositoryContextLocation(resourceFile.getParent());
		return info;
	}

	public static StandardResourceInfo from(Path resourcePath)
	{
		StandardResourceInfo info = new StandardResourceInfo();
		info.setRepositoryResourceLocation(resourcePath.toString());
		Path parentPath = resourcePath.getParent();
		info.setRepositoryContextLocation(parentPath == null ? null : parentPath.toString());
		return info;
	}
	
	private String resourceLocation;

	private String contextLocation;
	
	@Override
	public String getRepositoryResourceLocation()
	{
		return resourceLocation;
	}

	public void setRepositoryResourceLocation(String resourceLocation)
	{
		this.resourceLocation = resourceLocation;
	}

	@Override
	public String getRepositoryContextLocation()
	{
		return contextLocation;
	}

	public void setRepositoryContextLocation(String contextLocation)
	{
		this.contextLocation = contextLocation;
	}

}

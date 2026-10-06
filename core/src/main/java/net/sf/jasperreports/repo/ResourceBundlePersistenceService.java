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

import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class ResourceBundlePersistenceService implements PersistenceService
{
	//private JasperReportsContext jasperReportsContext;
	
	/**
	 * 
	 */
	public ResourceBundlePersistenceService(JasperReportsContext jasperReportsContext)
	{
		//this.jasperReportsContext = jasperReportsContext;
	}

	
	@Override
	public Resource load(String uri, RepositoryService repositoryService)
	{
		return load(null, uri, repositoryService);
	}
	
	@Override
	public Resource load(RepositoryContext context, String uri, RepositoryService repositoryService)
	{
		ResourceBundleResource resource = null; 

		InputStreamResource isResource = repositoryService.getResource(context, uri, InputStreamResource.class);
		
		InputStream is = isResource == null ? null : isResource.getInputStream();
		if (is != null)
		{
			resource = new ResourceBundleResource();
			try
			{
				resource.setValue(new PropertyResourceBundle(is));
			}
			catch (IOException e)
			{
				throw new JRRuntimeException(e);
			}
			finally
			{
				try
				{
					is.close();
				}
				catch (IOException e)
				{
				}
			}
		}

		return resource;
	}

	
	@Override
	public void save(Resource resource, String uri, RepositoryService repositoryService)
	{
		//FIXMEREPO
	}
}

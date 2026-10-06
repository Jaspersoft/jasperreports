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
package net.sf.jasperreports.castor.repo;

import java.io.IOException;
import java.io.InputStream;

import net.sf.jasperreports.castor.util.CastorUtil;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.repo.InputStreamResource;
import net.sf.jasperreports.repo.PersistenceService;
import net.sf.jasperreports.repo.RepositoryContext;
import net.sf.jasperreports.repo.RepositoryService;
import net.sf.jasperreports.repo.Resource;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @deprecated To be removed.
 */
public class CastorObjectPersistenceService implements PersistenceService
{
	private JasperReportsContext jasperReportsContext;
	
	/**
	 * 
	 */
	public CastorObjectPersistenceService(JasperReportsContext jasperReportsContext)
	{
		this.jasperReportsContext = jasperReportsContext;
	}

	
	@Override
	public Resource load(String uri, RepositoryService repositoryService)
	{
		return load(null, uri, repositoryService);
	}
	
	@Override
	public Resource load(RepositoryContext context, String uri, RepositoryService repositoryService)
	{
		CastorResource<Object> resource = null; 

		InputStreamResource isResource = repositoryService.getResource(context, uri, InputStreamResource.class);
		
		InputStream is = isResource == null ? null : isResource.getInputStream();
		if (is != null)
		{
			resource = new CastorResource<>();
			try
			{
				resource.setValue(CastorUtil.getInstance(jasperReportsContext).read(is));
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

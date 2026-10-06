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
package net.sf.jasperreports.dataadapters.repo;

import net.sf.jasperreports.dataadapters.DataAdapter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.jackson.repo.JacksonObjectPersistenceService;
import net.sf.jasperreports.jackson.repo.JacksonResource;
import net.sf.jasperreports.repo.RepositoryContext;
import net.sf.jasperreports.repo.RepositoryService;
import net.sf.jasperreports.repo.Resource;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JacksonDataAdapterPersistenceService extends JacksonObjectPersistenceService
{

	/**
	 * 
	 */
	public JacksonDataAdapterPersistenceService(JasperReportsContext jasperReportsContext)
	{
		super(jasperReportsContext, DataAdapter.class);
	}

	
	@Override
	public Resource load(String uri, RepositoryService repositoryService)
	{
		return load(null, uri, repositoryService);
	}
	
	@Override
	public Resource load(RepositoryContext context, String uri, RepositoryService repositoryService)
	{
		DataAdapterResource dataAdapterResource = null;
		
		JacksonResource<?> resource = (JacksonResource<?>)super.load(context, uri, repositoryService);
		
		if (resource != null)
		{
			dataAdapterResource = new DataAdapterResource();
			dataAdapterResource.setValue((DataAdapter)resource.getValue());
		}
		
		return dataAdapterResource;
	}

}

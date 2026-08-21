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
package net.sf.jasperreports.engine.util;

import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.SimpleJasperReportsContext;
import net.sf.jasperreports.repo.DefaultRepositoryService;
import net.sf.jasperreports.repo.RepositoryService;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @deprecated To be removed.
 */
public class LocalJasperReportsContext extends SimpleJasperReportsContext
{
	/**
	 *
	 */
	private AtomicReference<List<RepositoryService>> localRepositoryServicesRef = new AtomicReference<>();
	protected volatile DefaultRepositoryService localRepositoryService;

	/**
	 *
	 */
	public LocalJasperReportsContext(JasperReportsContext parent)
	{
		super(parent);
	}

	/**
	 *
	 */
	public static JasperReportsContext getLocalContext(JasperReportsContext jasperReportsContext, Map<String,Object> parameterValues)
	{
		if (parameterValues.containsKey(JRParameter.REPORT_CLASS_LOADER))
		{
			LocalJasperReportsContext localJasperReportsContext = new LocalJasperReportsContext(jasperReportsContext);

			localJasperReportsContext.setClassLoader((ClassLoader)parameterValues.get(JRParameter.REPORT_CLASS_LOADER));

			return localJasperReportsContext;
		}

		return jasperReportsContext;
	}

	/**
	 *
	 */
	protected DefaultRepositoryService getLocalRepositoryService()
	{
		DefaultRepositoryService service = localRepositoryService;
		if (service == null)
		{
			synchronized (this)
			{
				service = localRepositoryService;
				if (service == null)
				{
					service = localRepositoryService = new DefaultRepositoryService(this);
				}
			}
		}
		return service;
	}

	/**
	 *
	 */
	public void setClassLoader(ClassLoader classLoader)
	{
		getLocalRepositoryService().setClassLoader(classLoader);
	}

	/**
	 *
	 */
	public void setURLStreamHandlerFactory(URLStreamHandlerFactory urlHandlerFactory)
	{
		getLocalRepositoryService().setURLStreamHandlerFactory(urlHandlerFactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> getExtensions(Class<T> extensionType)
	{
		DefaultRepositoryService localRepository = localRepositoryService;
		if (
			localRepository != null
			&& RepositoryService.class.equals(extensionType)
			)
		{
			// we cache repository service extensions from parent and replace the DefaultRepositoryService instance, if present among them 
			List<RepositoryService> localRepositoryServices = localRepositoryServicesRef.get();
			if (localRepositoryServices == null)
			{
				localRepositoryServices = getRepositoryServices(localRepository);
				if (!localRepositoryServicesRef.compareAndSet(null, localRepositoryServices))
				{
					localRepositoryServices = localRepositoryServicesRef.get();
				}
			}
			return (List<T>) localRepositoryServices;
		}
		return super.getExtensions(extensionType);
	}

	protected List<RepositoryService> getRepositoryServices(DefaultRepositoryService localRepository)
	{
		List<RepositoryService> repoServices = super.getExtensions(RepositoryService.class);
		if (repoServices == null)
		{
			return new ArrayList<>();
		}
		return
			repoServices
				.stream().map(s -> s instanceof DefaultRepositoryService ? localRepository : s)
				.collect(Collectors.toList()); //TODO unmodifiable?
	}
	
}

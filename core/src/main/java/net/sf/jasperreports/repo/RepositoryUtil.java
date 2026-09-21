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
package net.sf.jasperreports.repo;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.engine.util.JRLoader;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class RepositoryUtil
{
	private static final String EXCEPTION_MESSAGE_KEY_BYTE_DATA_LOADING_ERROR = "repo.byte.data.loading.error";
	private static final String EXCEPTION_MESSAGE_KEY_BYTE_DATA_NOT_FOUND = "repo.byte.data.not.found";
	private static final String EXCEPTION_MESSAGE_KEY_INPUT_STREAM_NOT_FOUND = "repo.input.stream.not.found";
	private static final String EXCEPTION_MESSAGE_KEY_REPORT_NOT_FOUND = "repo.report.not.found";
	private static final String EXCEPTION_MESSAGE_KEY_RESOURCET_NOT_FOUND = "repo.resource.not.found";
	
	private AtomicReference<List<RepositoryService>> repositoryServices = new AtomicReference<>();
	

	private RepositoryContext context;


	/**
	 *
	 */
	private RepositoryUtil(RepositoryContext context)//FIXMECONTEXT try to reuse utils as much as you can
	{
		this.context = context;
	}
	
	
	/**
	 *
	 */
	public static RepositoryUtil getInstance(JasperReportsContext jasperReportsContext)
	{
		return getInstance(SimpleRepositoryContext.of(jasperReportsContext));
	}
	
	public static RepositoryUtil getInstance(RepositoryContext repositoryContext)
	{
		return new RepositoryUtil(repositoryContext);
	}
	
	
	/**
	 * 
	 */
	private List<RepositoryService> getServices()
	{
		List<RepositoryService> cachedServices = repositoryServices.get();
		if (cachedServices != null)
		{
			return cachedServices;
		}
		
		List<RepositoryService> services = context.getJasperReportsContext().getExtensions(RepositoryService.class);
		
		// set if not already set
		if (repositoryServices.compareAndSet(null, services))
		{
			return services;
		}
		
		// already set in the meantime by another thread
		return repositoryServices.get();
	}
	
	
	/**
	 *
	 */
	public JasperReport getReport(ReportContext reportContext, String location) throws JRException 
	{
		JasperReport jasperReport = null;
		
		JasperDesignCache cache = JasperDesignCache.getInstance(context.getJasperReportsContext(), reportContext);
		if (cache != null)
		{
			jasperReport = cache.getJasperReport(location);
		}

		if (jasperReport == null)
		{
			ReportResource resource = getResourceFromLocation(location, ReportResource.class);
			if (resource == null)
			{
				throw 
					new JRException(
						EXCEPTION_MESSAGE_KEY_REPORT_NOT_FOUND,
						new Object[]{location});
			}

			jasperReport = resource.getReport();

			if (cache != null)
			{
				cache.set(location, jasperReport);
			}
		}

		return jasperReport;
	}


	/**
	 * 
	 */
	public <K extends Resource> K getResourceFromLocation(String location, Class<K> resourceType) throws JRException
	{
		K resource = null;
		List<RepositoryService> services = getServices();
		if (services != null)
		{
			for (RepositoryService service : services)
			{
				resource = service.getResource(context, location, resourceType);
				if (resource != null)
				{
					break;
				}
			}
		}
		if (resource == null)
		{
			throw 
			new JRException(
				EXCEPTION_MESSAGE_KEY_RESOURCET_NOT_FOUND,
				new Object[]{location});	//FIXMEREPO decide whether to return null or throw exception; check everywhere
		}
		return resource;
	}


	/**
	 *
	 */
	public InputStream getInputStreamFromLocation(String location) throws JRException
	{
		InputStream is = findInputStream(location);
		if (is == null)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_INPUT_STREAM_NOT_FOUND,
					new Object[]{location});
		}
		return is;
	}


	/**
	 *
	 */
	private InputStream findInputStream(String location) throws JRException
	{
		InputStreamResource inputStreamResource = null;
		List<RepositoryService> services = getServices();
		if (services != null)
		{
			for (RepositoryService service : services)
			{
				inputStreamResource = service.getResource(context, location, InputStreamResource.class);
				if (inputStreamResource != null)
				{
					break;
				}
			}
		}
		return inputStreamResource == null ? null : inputStreamResource.getInputStream();
	}
	
	
	/**
	 *
	 */
	public byte[] getBytesFromLocation(String location) throws JRException
	{
		try (InputStream is = findInputStream(location))
		{
			if (is == null)
			{
				throw 
					new JRException(
						EXCEPTION_MESSAGE_KEY_BYTE_DATA_NOT_FOUND,
						new Object[]{location});
			}
	
			return JRLoader.readBytes(is);
		}
		catch (IOException e)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_BYTE_DATA_LOADING_ERROR,
					new Object[]{location},
					e);
		}
	}
	
	public ResourceInfo getResourceInfo(String location)
	{
		ResourceInfo resourceInfo = null;
		List<RepositoryService> services = getServices();
		if (services != null)
		{
			for (RepositoryService service : services)
			{
				resourceInfo = service.getResourceInfo(context, location);
				if (resourceInfo != null)
				{
					break;
				}
			}
		}
		return resourceInfo;
	}
	
	public RepositoryContext getRepositoryContext()
	{
		return context;
	}
}

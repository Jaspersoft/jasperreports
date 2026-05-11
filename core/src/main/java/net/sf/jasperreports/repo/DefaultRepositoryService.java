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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLStreamHandlerFactory;
import java.nio.file.Path;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRResourcesUtil;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class DefaultRepositoryService implements StreamRepositoryService
{
	
	private static final Log log = LogFactory.getLog(DefaultRepositoryService.class);
	
	@Property(
			category = PropertyConstants.CATEGORY_REPOSITORY,
			defaultValue = PropertyConstants.BOOLEAN_TRUE,
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_6_11_0,
			valueType = Boolean.class
			)
	public static final String PROPERTY_FILES_ENABLED =
			JRPropertiesUtil.PROPERTY_PREFIX + "default.file.repository.enabled";
	
	public static final String EXCEPTION_MESSAGE_KEY_NOT_IMPLEMENTED = "repo.default.not.implemented";
	
	/**
	 * 
	 */
	protected JasperReportsContext jasperReportsContext;
	private boolean filesEnabled;

	/**
	 * 
	 */
	protected ClassLoader classLoader;
	protected URLStreamHandlerFactory urlHandlerFactory;

	/**
	 *
	 */
	public DefaultRepositoryService(JasperReportsContext jasperReportsContext) 
	{
		this.jasperReportsContext = jasperReportsContext;
		this.filesEnabled = JRPropertiesUtil.getInstance(jasperReportsContext).getBooleanProperty(
				PROPERTY_FILES_ENABLED, true);
	}
	
	/**
	 *
	 */
	public void setClassLoader(ClassLoader classLoader) 
	{
		this.classLoader = classLoader;
	}
	
	/**
	 *
	 */
	public void setURLStreamHandlerFactory(URLStreamHandlerFactory urlHandlerFactory) 
	{
		this.urlHandlerFactory = urlHandlerFactory;
	}
	
	@Override
	public InputStream getInputStream(String uri)
	{
		return getInputStream(SimpleRepositoryContext.of(jasperReportsContext), uri);
	}
	
	@Override
	public InputStream getInputStream(RepositoryContext context, String uri)
	{
		try
		{
			URL url = JRResourcesUtil.createURL(uri, urlHandlerFactory);
			if (url != null)
			{
				return JRLoader.getInputStream(url);
			}

			File file = resolveFile(context, uri);
			if (file != null)
			{
				return JRLoader.getInputStream(file);
			}

			url = JRResourcesUtil.findClassLoaderResource(uri, classLoader);
			if (url != null)
			{
				return JRLoader.getInputStream(url);
			}
		}
		catch (JRException e)
		{
			throw new JRRuntimeException(e);
		}
		
		return null;
	}

	protected File resolveFile(RepositoryContext context, String uri)
	{
		if (filesEnabled)
		{
			return JRResourcesUtil.resolveFile(context, uri);
		}
		
		return null;
	}
	
	@Override
	public OutputStream getOutputStream(String uri)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Resource getResource(String uri)
	{
		throw 
			new JRRuntimeException(
				EXCEPTION_MESSAGE_KEY_NOT_IMPLEMENTED,
				(Object[])null);//FIXMEREPO
	}
	
	@Override
	public void saveResource(String uri, Resource resource)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public <K extends Resource> K getResource(String uri, Class<K> resourceType)
	{
		return getResource(SimpleRepositoryContext.of(jasperReportsContext), uri, resourceType);
	}
	
	@Override
	public <K extends Resource> K getResource(RepositoryContext context, String uri, Class<K> resourceType)
	{
		PersistenceService persistenceService = PersistenceUtil.getInstance(jasperReportsContext).getService(DefaultRepositoryService.class, resourceType);
		if (persistenceService != null)
		{
			return (K) persistenceService.load(context, uri, this);
		}
		return null;
	}

	@Override
	public ResourceInfo getResourceInfo(RepositoryContext context, String location)
	{
		//detecting URLs
		URL url = JRResourcesUtil.createURL(location, urlHandlerFactory);
		if (url != null)
		{
			//not supporting paths relative to URLs
			return null;
		}

		File file = resolveFile(context, location);
		if (file != null)
		{
			try
			{
				//resolving to real path to eliminate .. and .
				Path path = file.toPath().toRealPath();
				return StandardResourceInfo.from(path);
			}
			catch (IOException e)
			{
				log.warn("Failed to resolve real path for file " + file, e);
				
				//using the paths as present in the File object
				return StandardResourceInfo.from(file);
			}
		}
		
		//TODO lucianc classloader resources
		return null;
	}


}

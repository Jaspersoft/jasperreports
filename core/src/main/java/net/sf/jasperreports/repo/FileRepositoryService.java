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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.util.JRResourcesUtil;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class FileRepositoryService implements StreamRepositoryService
{
	
	private static final Log log = LogFactory.getLog(FileRepositoryService.class);
	
	public static final String EXCEPTION_MESSAGE_KEY_NOT_IMPLEMENTED = "repo.file.not.implemented";
	
	private JasperReportsContext jasperReportsContext;
	private String root;
	private boolean resolveAbsolutePath;//FIXMEREPO consider giving up on this
	private volatile Path rootNormalizedPath;
	private Path rootRealPath;
	
	/**
	 * 
	 */
	public FileRepositoryService(
		JasperReportsContext jasperReportsContext,
		String root, 
		boolean resolveAbsolutePath
		)
	{
		this.jasperReportsContext = jasperReportsContext;
		this.root = root;
		this.resolveAbsolutePath = resolveAbsolutePath;
		setRootRealPath();
	}
	
	/**
	 * 
	 */
	public void setRoot(String root)
	{
		this.root = root;
		setRootRealPath();
	}
	
	/**
	 * 
	 */
	public String getRoot()
	{
		return root;
	}
	
	protected Path rootNormalizedPath()
	{
		Path rootPath = rootNormalizedPath;
		if (rootPath == null)
		{
			Path path = Paths.get(root);
			rootPath = rootNormalizedPath = path.normalize();
		}
		return rootPath;
	}
	
	@Override
	public InputStream getInputStream(String uri)
	{
		return getInputStream(SimpleRepositoryContext.of(jasperReportsContext), uri);
	}

	public File getFile(RepositoryContext context, String uri)
	{
		File file = null;
		if (uri != null)
		{
			file = JRResourcesUtil.resolveFile(context, uri, this::locateFile, this::locateContextFile);
			if (file != null && !file.isFile())
			{
				file = null;
			}
		}
		return file;
	}
	
	@Override
	public InputStream getInputStream(RepositoryContext context, String uri)
	{
		InputStream is = null;
		
		File file = getFile(context, uri);
		if (file != null)
		{
			try
			{
				is = new FileInputStream(file);
			}
			catch (IOException e)
			{
				throw new JRRuntimeException(e);
			}
		}
		
		return is;
	}
	
	protected File locateFile(String location)
	{
		File file = new File(getRoot(), location);
		if (file.exists())
		{
			//checking that syntactically the requested path is still inside the root
			//we are not dealing with symlinks/real paths for now
			Path rootPath = rootNormalizedPath();
			Path filePath = file.toPath().normalize();
			if (!filePath.startsWith(rootPath))
			{
				log.warn("Requested path " + location + " normalized to " + filePath
						+ ", which falls outside repository root path " + rootPath);
				file = null;
			}
		}
		else
		{
			if (resolveAbsolutePath)
			{
				file = new File(location);
				if (!file.exists())
				{
					file = null;
				}
			}
			else
			{
				file = null;
			}
		}
		return file;
	}
	
	protected File locateContextFile(File contextFolder, String location)
	{
		Path rootPath = rootNormalizedPath();
		Path contextPath = contextFolder.toPath().normalize();
		//already checked, but just to be sure
		if (!contextPath.startsWith(rootPath))
		{
			log.warn("Requested context " + contextFolder + " normalized to " + contextPath
					+ ", which falls outside repository root path " + rootPath);
			return null;
		}
		
		File file = new File(contextFolder, location);
		if (file.exists())
		{
			//checking that syntactically the requested path is still inside the root
			//we are not dealing with symlinks/real paths for now
			Path filePath = file.toPath().normalize();
			if (!filePath.startsWith(rootPath))
			{
				log.warn("Requested path " + location + " normalized to " + filePath
						+ ", which falls outside repository root path " + rootPath);
				file = null;
			}
		}
		else
		{
			file = null;
		}
		return file;
	}
	
	@Override
	public OutputStream getOutputStream(String uri)
	{
		File file = null;

		if (uri != null)
		{
			file = new File(getRoot(), uri);

			Path rootPath = rootNormalizedPath();
			Path filePath = file.toPath().normalize();
			if (!filePath.startsWith(rootPath))
			{
				throw new JRRuntimeException("Requested output path " + uri 
						+ " falls outside repository root path " + rootPath);
			}
		}

		OutputStream os = null;

		if (file != null)
		{
			try
			{
				os = new BufferedOutputStream(new FileOutputStream(file));
			}
			catch (IOException e)
			{
				throw new JRRuntimeException(e);
			}
		}
		
		return os;
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
		PersistenceService persistenceService = PersistenceUtil.getInstance(jasperReportsContext).getService(FileRepositoryService.class, resource.getClass());
		if (persistenceService != null)
		{
			persistenceService.save(resource, uri, this);
		}
	}
	
	@Override
	public <K extends Resource> K getResource(String uri, Class<K> resourceType)
	{
		return getResource(SimpleRepositoryContext.of(jasperReportsContext), uri, resourceType);
	}
	
	@Override
	public <K extends Resource> K getResource(RepositoryContext context, String uri, Class<K> resourceType)
	{
		PersistenceService persistenceService = PersistenceUtil.getInstance(jasperReportsContext).getService(FileRepositoryService.class, resourceType);
		if (persistenceService != null)
		{
			return (K) persistenceService.load(context, uri, this);
		}
		return null;
	}
	
	private void setRootRealPath()
	{
		Path path = Paths.get(root);
		try
		{
			rootRealPath = path.toRealPath();
		}
		catch (IOException e)
		{
			log.warn("Failed to resolve real path for root " + root, e);
			rootRealPath = null;
		}
	}

	@Override
	public ResourceInfo getResourceInfo(RepositoryContext context, String location)
	{
		File file = getFile(context, location);
		if (file != null)
		{
			try
			{
				Path filePath = file.toPath().toRealPath();
				if (rootRealPath != null && filePath.startsWith(rootRealPath))
				{
					Path relativePath = rootRealPath.relativize(filePath);
					return StandardResourceInfo.from(relativePath);
				}
				else if(resolveAbsolutePath)
				{
					return StandardResourceInfo.from(filePath);
				}
			}
			catch (IOException e)
			{
				log.warn("Failed to resolve real path for file " + file, e);
			}
		}
		return null;
	}
}

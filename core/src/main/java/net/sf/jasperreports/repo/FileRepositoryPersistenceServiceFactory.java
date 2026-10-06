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

import net.sf.jasperreports.engine.JasperReportsContext;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class FileRepositoryPersistenceServiceFactory implements PersistenceServiceFactory
{
	private static final FileRepositoryPersistenceServiceFactory INSTANCE = new FileRepositoryPersistenceServiceFactory();
	
	/**
	 * 
	 */
	public static FileRepositoryPersistenceServiceFactory getInstance()
	{
		return INSTANCE;
	}
	
	@Override
	public <K extends RepositoryService, L extends Resource> PersistenceService getPersistenceService(
		JasperReportsContext jasperReportsContext,
		Class<K> repositoryServiceType, 
		Class<L> resourceType
		) 
	{
		if (FileRepositoryService.class.isAssignableFrom(repositoryServiceType))
		{
			if (InputStreamResource.class.getName().equals(resourceType.getName()))
			{
				return new InputStreamPersistenceService();
			}
			else if (OutputStreamResource.class.getName().equals(resourceType.getName()))
			{
				return new OutputStreamPersistenceService();
			}
			else if (ReportResource.class.getName().equals(resourceType.getName()))
			{
				return new SerializedReportPersistenceService();
			}
			else if (ResourceBundleResource.class.getName().equals(resourceType.getName()))
			{
				return new ResourceBundlePersistenceService(jasperReportsContext);
			}
			else if (SerializableResource.class.isAssignableFrom(resourceType))
			{
				return new SerializedObjectPersistenceService();
			}
		}
		return null;
	}
}

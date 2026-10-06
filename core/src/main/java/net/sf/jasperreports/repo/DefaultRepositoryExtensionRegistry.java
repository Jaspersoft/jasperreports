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

import java.util.Collections;
import java.util.List;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.extensions.ExtensionsRegistry;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class DefaultRepositoryExtensionRegistry implements ExtensionsRegistry
{
	/**
	 * 
	 */
	private static final DefaultRepositoryExtensionRegistry INSTANCE =  new DefaultRepositoryExtensionRegistry();
	
	/**
	 * 
	 */
	public static DefaultRepositoryExtensionRegistry getInstance()
	{
		return INSTANCE;
	}
	
	@Override
	public <T> List<T> getExtensions(Class<T> extensionType)
	{
		if (RepositoryService.class.equals(extensionType))
		{
			return (List<T>) Collections.singletonList(new DefaultRepositoryService(DefaultJasperReportsContext.getInstance()));//FIXMEREPO check if this can be singleton
		}
		else if (PersistenceServiceFactory.class.equals(extensionType))
		{
			return (List<T>) Collections.singletonList(DefaultRepositoryPersistenceServiceFactory.getInstance());
		}
		return null;
	}
}

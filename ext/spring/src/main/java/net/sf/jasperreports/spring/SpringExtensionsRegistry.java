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
package net.sf.jasperreports.spring;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.map.ReferenceMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ListableBeanFactory;

import net.sf.jasperreports.extensions.ExtensionsRegistry;

/**
 * An {@link ExtensionsRegistry extension registry} which works by looking 
 * for beans of a specific extension type in a Spring beans factory.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
//TODO generic element fallback handlers
public class SpringExtensionsRegistry implements ExtensionsRegistry
{

	private static final Log log = LogFactory.getLog(SpringExtensionsRegistry.class);
	
	private final ListableBeanFactory beanFactory;
	
	private final ReferenceMap<Class<?>, String[]> extensionBeanNamesCache = 
		new ReferenceMap<>(
			ReferenceMap.ReferenceStrength.WEAK, ReferenceMap.ReferenceStrength.HARD
			);

	/**
	 * Creates a Spring-based extension registry.
	 * 
	 * @param beanFactory the Spring bean factory
	 */
	public SpringExtensionsRegistry(ListableBeanFactory beanFactory)
	{
		this.beanFactory = beanFactory;
	}
	
	/**
	 * Returns all beans that match the extension class.
	 */
	@Override
	public <T> List<T> getExtensions(Class<T> extensionType)
	{
		String[] beanNames = getExtensionBeanNames(extensionType);
		List<T> beans = new ArrayList<>(beanNames.length);
		for (String name : beanNames)
		{
			if (log.isDebugEnabled())
			{
				log.debug("Getting bean " + name + " as extension of type "
						+ extensionType.getName());
			}
			T bean = beanFactory.getBean(name, extensionType);
			beans.add(bean);
		}
		return beans;
	}

	protected String[] getExtensionBeanNames(Class<?> extensionType)
	{
		String[] beanNames;
		synchronized (extensionBeanNamesCache)
		{
			beanNames = extensionBeanNamesCache.get(extensionType);
		}
		
		if (beanNames == null)
		{
			// can be executed concurrently
			beanNames = findExtensionBeanNames(extensionType);
			
			synchronized (extensionBeanNamesCache)
			{
				// checking again
				if (extensionBeanNamesCache.get(extensionType) == null)
				{
					extensionBeanNamesCache.put(extensionType, beanNames);
				}
			}
		}
		return beanNames;
	}

	protected String[] findExtensionBeanNames(Class<?> extensionType)
	{
		String[] beanNames = beanFactory.getBeanNamesForType(extensionType);
		
		if (log.isDebugEnabled())
		{
			log.debug("Found " + beanNames.length + " beans for extension type " + extensionType);
		}
		
		return beanNames;
	}

}

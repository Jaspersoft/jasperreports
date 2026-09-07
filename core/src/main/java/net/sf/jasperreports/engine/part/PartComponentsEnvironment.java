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
package net.sf.jasperreports.engine.part;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.ReferenceMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.annotation.JsonTypeName;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.extensions.ExtensionsEnvironment;

/**
 * A class that provides access to {@link PartComponentsBundle component bundles}.
 * 
 * <p>
 * Component bundles are registered as JasperReports extensions of type
 * {@link PartComponentsBundle} via the central extension framework (see
 * {@link ExtensionsEnvironment}).
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public final class PartComponentsEnvironment
{
	
	private static final Log log = LogFactory.getLog(PartComponentsEnvironment.class);

	public static final String EXCEPTION_MESSAGE_KEY_COMPONENT_MANAGER_NOT_FOUND = "engine.part.component.manager.not.found";
	
	private final ReferenceMap<Object, List<PartComponentsBundle>> cache = 
		new ReferenceMap<>(
			ReferenceMap.ReferenceStrength.WEAK, ReferenceMap.ReferenceStrength.HARD
			);
	
	private JasperReportsContext jasperReportsContext;


	/**
	 *
	 */
	private PartComponentsEnvironment(JasperReportsContext jasperReportsContext)
	{
		this.jasperReportsContext = jasperReportsContext;
	}
	
	
	/**
	 *
	 */
	public static PartComponentsEnvironment getInstance(JasperReportsContext jasperReportsContext)
	{
		return new PartComponentsEnvironment(jasperReportsContext);
	}
	
	
	/**
	 * Returns the set of all component bundles present in the registry. 
	 * 
	 * @return the set of component bundles
	 */
	public Collection<PartComponentsBundle> getBundles()
	{
		List<PartComponentsBundle> components = getCachedBundles();
		return components;
	}
	
	protected List<PartComponentsBundle> getCachedBundles()
	{
		Object cacheKey = ExtensionsEnvironment.getExtensionsCacheKey();
		synchronized (cache)
		{
			return cache.computeIfAbsent(cacheKey, k -> findBundles());
		}
	}

	protected List<PartComponentsBundle> findBundles()
	{
		return jasperReportsContext.getExtensions(PartComponentsBundle.class);
	}
	
	/**
	 * Returns a component manager that corresponds to a particular component
	 * type key.
	 * 
	 * @param component the component type key
	 * @return the manager for the component type
	 * @throws JRRuntimeException if the registry does not contain the specified
	 * component type
	 */
	public PartComponentManager getManager(PartComponent component)
	{
		List<PartComponentsBundle> bundles = getCachedBundles();
		List<PartComponentManager> managers = bundles.stream().map(bundle -> bundle.getComponentManager(component))
				.filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
		if (managers.isEmpty())
		{
			throw new JRRuntimeException(EXCEPTION_MESSAGE_KEY_COMPONENT_MANAGER_NOT_FOUND,
					new Object[]{component.getClass().getName()});
		}
		
		if (managers.size() > 1)
		{
			log.warn("Found " + managers.size() + " components for " + component.getClass().getName());
		}
		return managers.get(0);
	}
	
	public String getComponentName(Class<? extends PartComponent> componentType)
	{
		for (Class<?> type = componentType; type != null; type = type.getSuperclass()) 
		{
			JsonTypeName componentSpec = type.getAnnotation(JsonTypeName.class);
			if (componentSpec != null)
			{
				return componentSpec.value();
			}
		}
		
		for (Class<?> itf : componentType.getInterfaces())
		{
			JsonTypeName componentSpec = itf.getAnnotation(JsonTypeName.class);
			if (componentSpec != null)
			{
				return componentSpec.value();
			}
		}
		
		throw new JRRuntimeException("Cannot detect name of part component of type " + componentType.getName());
	}

}

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
package net.sf.jasperreports.engine.component;

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
 * A class that provides access to {@link ComponentsBundle component bundles}.
 * 
 * <p>
 * Component bundles are registered as JasperReports extensions of type
 * {@link ComponentsBundle} via the central extension framework (see
 * {@link ExtensionsEnvironment}).
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public final class ComponentsEnvironment
{
	
	private static final Log log = LogFactory.getLog(ComponentsEnvironment.class);
	public static final String EXCEPTION_MESSAGE_KEY_COMPONENT_MANAGER_NOT_FOUND = "components.component.manager.not.found";
	
	private final ReferenceMap<Object, List<ComponentsBundle>> cache = 
		new ReferenceMap<>(
			ReferenceMap.ReferenceStrength.WEAK, ReferenceMap.ReferenceStrength.HARD
			);
	
	private JasperReportsContext jasperReportsContext;


	/**
	 *
	 */
	private ComponentsEnvironment(JasperReportsContext jasperReportsContext)
	{
		this.jasperReportsContext = jasperReportsContext;
	}
	
	
	/**
	 *
	 */
	public static ComponentsEnvironment getInstance(JasperReportsContext jasperReportsContext)
	{
		return new ComponentsEnvironment(jasperReportsContext);
	}
	
	
	/**
	 * Returns the set of all component bundles present in the registry. 
	 * 
	 * @return the set of component bundles
	 */
	public Collection<ComponentsBundle> getBundles()
	{
		return getCachedBundles();
	}
	
	protected List<ComponentsBundle> getCachedBundles()
	{
		Object cacheKey = ExtensionsEnvironment.getExtensionsCacheKey();
		synchronized (cache)
		{
			return cache.computeIfAbsent(cacheKey, k -> findBundles());
		}
	}

	protected List<ComponentsBundle> findBundles()
	{
		return jasperReportsContext.getExtensions(ComponentsBundle.class);
	}

	/**
	 * Returns a component manager that corresponds to a particular component.
	 * 
	 * @param component the component
	 * @return the manager for the component type
	 * @throws JRRuntimeException if the registry does not contain the specified
	 * component
	 */
	public ComponentManager getManager(Component component)
	{
		return getManager(component.getClass());
	}
	
	/**
	 * Returns a component manager that corresponds to a particular component type.
	 * 
	 * @param componentType the component type
	 * @return the manager for the component type
	 * @throws JRRuntimeException if the registry does not contain the specified
	 * component
	 */
	public ComponentManager getManager(Class<? extends Component> componentType)
	{
		List<ComponentsBundle> bundles = getCachedBundles();
		List<ComponentManager> managers = bundles.stream().map(bundle -> bundle.getComponentManager(componentType))
				.filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
		if (managers.isEmpty())
		{
			throw new JRRuntimeException(EXCEPTION_MESSAGE_KEY_COMPONENT_MANAGER_NOT_FOUND,
				new Object[]{componentType.getName()});
		}
		
		if (managers.size() > 1)
		{
			log.warn("Found " + managers.size() + " components for " + componentType.getName());
		}
		return managers.get(0);
	}
	
	public String getComponentName(Class<? extends Component> componentType)
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
		
		throw new JRRuntimeException("Cannot detect name of component of type " + componentType.getName());
	}

	public List<String> getComponentNames()
	{
		return getCachedBundles().stream()
			.flatMap(bundle -> bundle.getComponentTypes().stream())
			.map(this::getComponentName)
			.collect(Collectors.toList());
	}
}

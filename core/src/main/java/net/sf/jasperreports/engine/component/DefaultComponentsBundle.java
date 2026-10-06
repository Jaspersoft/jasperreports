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
package net.sf.jasperreports.engine.component;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

/**
 * The default {@link ComponentsBundle components bundle} implementation.
 *
 * <p>
 * A components bundle consists of a map of {@link ComponentManager component managers}.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class DefaultComponentsBundle implements ComponentsBundle
{

	private Map<Class<? extends Component>, ComponentManager> componentManagers;

	@Override
	public Set<Class<? extends Component>> getComponentTypes()
	{
		return componentManagers.keySet();
	}
	
	@Override
	public Optional<ComponentManager> getComponentManager(Class<? extends Component> componentType)
	{
		//TODO handle class hierarchies?
		return componentManagers.entrySet().stream()
				.filter(entry -> entry.getKey().isAssignableFrom(componentType))
				.findFirst().map(Entry::getValue);
	}
	
	/**
	 * Returns the internal map of component managers, indexed by component name.
	 * 
	 * @return the map of component managers
	 * @see #setComponentManagers(Map)
	 */
	public Map<Class<? extends Component>, ComponentManager> getComponentManagers()
	{
		return componentManagers;
	}

	/**
	 * Sets the map of component managers.
	 * 
	 * <p>
	 * The map needs to use component names as keys, and {@link ComponentManager}
	 * instances as values.
	 * 
	 * @param componentManagers the map of component managers
	 */
	public void setComponentManagers(Map<Class<? extends Component>, ComponentManager> componentManagers)
	{
		this.componentManagers = componentManagers;
	}
	
}

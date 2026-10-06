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
package net.sf.jasperreports.engine.part;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

/**
 * The default {@link PartComponentsBundle components bundle} implementation.
 *
 * <p>
 * A components bundle consists of a set of {@link PartComponent PartComponent} types,
 * a map of {@link PartComponentManager component managers} and a specific
 * {@link PartComponentManager PartComponentManagerr} instance to be retrieved.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class DefaultPartComponentsBundle implements PartComponentsBundle
{
	
	private Map<Class<? extends PartComponent>, PartComponentManager> componentManagers;

	@Override
	public Set<Class<? extends PartComponent>> getComponentTypes()
	{
		return componentManagers.keySet();
	}
	
	@Override
	public Optional<PartComponentManager> getComponentManager(PartComponent component)
	{
		//TODO handle class hierarchies?
		return componentManagers.entrySet().stream()
				.filter(entry -> entry.getKey().isInstance(component))
				.findFirst().map(Entry::getValue);
	}
	
	/**
	 * Returns the internal map of component managers, indexed by component type.
	 * 
	 * @return the map of component managers
	 * @see #setComponentManagers(Map)
	 */
	public Map<Class<? extends PartComponent>, PartComponentManager> getComponentManagers()
	{
		return componentManagers;
	}

	/**
	 * Sets the map of component managers.
	 * 
	 * <p>
	 * The map needs to use component types as keys, and {@link PartComponentManager}
	 * instances as values.
	 * 
	 * @param componentManagers the map of component managers
	 */
	public void setComponentManagers(Map<Class<? extends PartComponent>, PartComponentManager> componentManagers)
	{
		this.componentManagers = componentManagers;
	}
	
}

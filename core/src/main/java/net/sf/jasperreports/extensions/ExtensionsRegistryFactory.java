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
package net.sf.jasperreports.extensions;

import net.sf.jasperreports.engine.JRPropertiesMap;

/**
 * A factory of {@link ExtensionsRegistry} instance.
 * 
 * <p>
 * Such factories are used by
 * {@link DefaultExtensionsRegistry the default components registry} to
 * instantiate sub extensions registries.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see DefaultExtensionsRegistry#PROPERTY_REGISTRY_FACTORY_PREFIX
 */
public interface ExtensionsRegistryFactory
{

	/**
	 * Instantiates an extensions registry.
	 * 
	 * @param registryId the ID of the registry to instantiate.
	 * The ID can be used to identify a set of properties to be used
	 * when instantiating the registry.
	 * @param properties the map of properties that can be used to configure
	 * the registry instantiation process
	 * @return an extensions registry
	 * @see DefaultExtensionsRegistry#PROPERTY_REGISTRY_PREFIX
	 */
	ExtensionsRegistry createRegistry(String registryId, 
			JRPropertiesMap properties);

}

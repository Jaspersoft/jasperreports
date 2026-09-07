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
package net.sf.jasperreports.util;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRPropertiesUtil.PropertySuffix;
import net.sf.jasperreports.extensions.DefaultExtensionsRegistry;
import net.sf.jasperreports.extensions.ExtensionsRegistry;
import net.sf.jasperreports.extensions.ExtensionsRegistryFactory;
import net.sf.jasperreports.extensions.SingletonExtensionRegistry;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class IdentitySecretsProviderExtensionsRegistryFactory implements ExtensionsRegistryFactory 
{
	/**
	 * 
	 */
	public final static String IDENTITY_SECTRETS_PROVIDER_CATEGORY_PROPERTY_PREFIX = DefaultExtensionsRegistry.PROPERTY_REGISTRY_PREFIX + "identity.secrets.category.";

	@Override
	public ExtensionsRegistry createRegistry(String registryId, JRPropertiesMap properties) 
	{
		List<PropertySuffix> categoryProperties = JRPropertiesUtil.getProperties(properties, IDENTITY_SECTRETS_PROVIDER_CATEGORY_PROPERTY_PREFIX);
		Set<String> categories = categoryProperties.stream().map(PropertySuffix::getValue).collect(Collectors.toSet());
		return new SingletonExtensionRegistry<SecretsProviderFactory>(SecretsProviderFactory.class, new IdentitySecretsProviderFactory(categories));
	}
}

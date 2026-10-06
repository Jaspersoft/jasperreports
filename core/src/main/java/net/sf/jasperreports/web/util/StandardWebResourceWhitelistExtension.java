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
package net.sf.jasperreports.web.util;

import java.util.List;

import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRPropertiesUtil.PropertySuffix;
import net.sf.jasperreports.extensions.ExtensionsRegistry;
import net.sf.jasperreports.extensions.ExtensionsRegistryFactory;
import net.sf.jasperreports.extensions.SingletonExtensionRegistry;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class StandardWebResourceWhitelistExtension implements ExtensionsRegistryFactory
{

	@Override
	public ExtensionsRegistry createRegistry(String registryId, JRPropertiesMap properties)
	{
		StandardWebResourceWhitelist whitelist = new StandardWebResourceWhitelist();
		List<PropertySuffix> whitelistProps = JRPropertiesUtil.getProperties(properties, 
			StandardWebResourceFilter.PROPERTY_PREFIX_RESOURCE_PATHS);
		for (PropertySuffix propertySuffix : whitelistProps)
		{
			whitelist.addResources(propertySuffix.getValue());
		}
		whitelistProps = JRPropertiesUtil.getProperties(properties, 
			StandardWebResourceFilter.PROPERTY_PREFIX_RESOURCE_PATTERN);
		for (PropertySuffix propertySuffix : whitelistProps)
		{
			whitelist.addPattern(propertySuffix.getValue());
		}
		return new SingletonExtensionRegistry<>(WebResourceWhitelist.class, whitelist);
	}

}

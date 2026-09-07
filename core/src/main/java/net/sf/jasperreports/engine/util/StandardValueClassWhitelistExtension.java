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
package net.sf.jasperreports.engine.util;

import java.util.List;

import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRPropertiesUtil.PropertySuffix;
import net.sf.jasperreports.extensions.ExtensionsRegistry;
import net.sf.jasperreports.extensions.ExtensionsRegistryFactory;
import net.sf.jasperreports.extensions.SingletonExtensionRegistry;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class StandardValueClassWhitelistExtension implements ExtensionsRegistryFactory
{

	@Override
	public ExtensionsRegistry createRegistry(String registryId, JRPropertiesMap properties)
	{
		StandardValueClassWhitelist whitelist = new StandardValueClassWhitelist();
		List<PropertySuffix> whitelistProps = JRPropertiesUtil.getProperties(properties,
				ValueClassFilter.PROPERTY_PREFIX_CLASS_WHITELIST);
		for (PropertySuffix propertySuffix : whitelistProps)
		{
			whitelist.addWhitelist(propertySuffix.getValue());
		}
		return new SingletonExtensionRegistry<>(ValueClassWhitelist.class, whitelist);
	}

}

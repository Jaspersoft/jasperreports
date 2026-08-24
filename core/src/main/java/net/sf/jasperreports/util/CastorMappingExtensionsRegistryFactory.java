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

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRPropertiesUtil.PropertySuffix;
import net.sf.jasperreports.extensions.DefaultExtensionsRegistry;
import net.sf.jasperreports.extensions.ExtensionsRegistry;
import net.sf.jasperreports.extensions.ExtensionsRegistryFactory;
import net.sf.jasperreports.extensions.ListExtensionRegistry;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @deprecated To be removed.
 */
public class CastorMappingExtensionsRegistryFactory implements ExtensionsRegistryFactory
{

	/**
	 * 
	 */
	public final static String CASTOR_MAPPING_PROPERTY_PREFIX = 
		DefaultExtensionsRegistry.PROPERTY_REGISTRY_PREFIX + "castor.mapping.";
	
	public final static char CASTOR_MAPPING_VERSION_SEPARATOR = '@';
	
	@Override
	public ExtensionsRegistry createRegistry(String registryId, JRPropertiesMap properties)
	{
		List<PropertySuffix> castorMappingProperties = JRPropertiesUtil.getProperties(properties, CASTOR_MAPPING_PROPERTY_PREFIX);
		List<CastorMapping> castorMappings = new ArrayList<>();
		for (PropertySuffix castorMappingProp : castorMappingProperties)
		{
			String key;
			String version;
			String suffix = castorMappingProp.getSuffix();
			int versionSeparatorIndex = suffix.lastIndexOf(CASTOR_MAPPING_VERSION_SEPARATOR);
			if (versionSeparatorIndex < 0)
			{
				key = suffix;
				version = null;
			} 
			else
			{
				key = suffix.substring(0, versionSeparatorIndex);
				version = suffix.substring(versionSeparatorIndex + 1, suffix.length());
			}
			
			String castorMappingPath = castorMappingProp.getValue();
			CastorMapping mapping = new CastorMapping(key, version, castorMappingPath);
			castorMappings.add(mapping);
		}
		
		return new ListExtensionRegistry<CastorMapping>(CastorMapping.class, castorMappings);
	}

}

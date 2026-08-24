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
package net.sf.jasperreports.web.util;

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
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class ContentTypeMappingExtensionsRegistryFactory implements ExtensionsRegistryFactory {

	/**
	 * 
	 */
	public final static String CONTENT_TYPE_MAPPING_PROPERTY_PREFIX = DefaultExtensionsRegistry.PROPERTY_REGISTRY_PREFIX + "content.type.mapping.";

	@Override
	public ExtensionsRegistry createRegistry(String registryId, JRPropertiesMap properties) {
		List<PropertySuffix> contentTypeMappingProperties = JRPropertiesUtil.getProperties(properties, CONTENT_TYPE_MAPPING_PROPERTY_PREFIX);
		List<ContentTypeMapping> contentTypeMappings = new ArrayList<>();
		for (PropertySuffix contentTypeMappingProp : contentTypeMappingProperties) {
			contentTypeMappings.add(new ContentTypeMapping(contentTypeMappingProp.getSuffix(),contentTypeMappingProp.getValue()));
		}
		
		return new ListExtensionRegistry<ContentTypeMapping>(ContentTypeMapping.class, contentTypeMappings);
	}

}

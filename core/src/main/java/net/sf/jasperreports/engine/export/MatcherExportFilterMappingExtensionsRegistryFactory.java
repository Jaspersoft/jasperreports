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
package net.sf.jasperreports.engine.export;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRPropertiesUtil.PropertySuffix;
import net.sf.jasperreports.extensions.DefaultExtensionsRegistry;
import net.sf.jasperreports.extensions.ExtensionsRegistry;
import net.sf.jasperreports.extensions.ExtensionsRegistryFactory;
import net.sf.jasperreports.extensions.ListExtensionRegistry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class MatcherExportFilterMappingExtensionsRegistryFactory implements ExtensionsRegistryFactory
{
	private static final Log log = LogFactory.getLog(MatcherExportFilterMappingExtensionsRegistryFactory.class);

	/**
	 * 
	 */
	public final static String MATCHER_EXPORT_FILTER_MAPPING_PROPERTY_PREFIX = 
		DefaultExtensionsRegistry.PROPERTY_REGISTRY_PREFIX + "matcher.export.filter.";
	
	/**
	 * 
	 */
	public final static String MATCHER_EXPORT_FILTER_MAPPING_INCLUDES_PROPERTY_SUFFIX = ".includes";
	
	/**
	 * 
	 */
	public final static String MATCHER_EXPORT_FILTER_MAPPING_EXCLUDES_PROPERTY_SUFFIX = ".excludes";
	
	@Override
	public ExtensionsRegistry createRegistry(String registryId, JRPropertiesMap properties)
	{
		List<PropertySuffix> exportFilterMappingProperties = JRPropertiesUtil.getProperties(properties, MATCHER_EXPORT_FILTER_MAPPING_PROPERTY_PREFIX);
		List<MatcherExportFilterMapping> exportFilterMappings = new ArrayList<>();
		for (PropertySuffix exportFilterMappingProp : exportFilterMappingProperties)
		{
			String exporterKey = null;
			boolean isIncludes = true;
			String propSuffix = exportFilterMappingProp.getSuffix();
			if (propSuffix.endsWith(MATCHER_EXPORT_FILTER_MAPPING_INCLUDES_PROPERTY_SUFFIX))
			{
				exporterKey = propSuffix.substring(0, propSuffix.length() - MATCHER_EXPORT_FILTER_MAPPING_INCLUDES_PROPERTY_SUFFIX.length());
				isIncludes = true;
			}
			else if (propSuffix.endsWith(MATCHER_EXPORT_FILTER_MAPPING_EXCLUDES_PROPERTY_SUFFIX))
			{
				exporterKey = propSuffix.substring(0, propSuffix.length() - MATCHER_EXPORT_FILTER_MAPPING_EXCLUDES_PROPERTY_SUFFIX.length());
				isIncludes = false;
			}
			if (exporterKey == null)
			{
				if (log.isDebugEnabled())
				{
					log.debug("Matcher mapping property suffix is invalid : " + propSuffix);
				}
			}
			else
			{
				exportFilterMappings.add(
					new MatcherExportFilterMapping(
						exporterKey, 
						exportFilterMappingProp.getValue(),
						isIncludes
						)
					);
			}
		}
		
		return new ListExtensionRegistry<MatcherExportFilterMapping>(MatcherExportFilterMapping.class, exportFilterMappings);
	}

}

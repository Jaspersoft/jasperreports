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
package net.sf.jasperreports.engine.analytics.data;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class MappedPropertyValues implements PropertyValues
{

	private static final Log log = LogFactory.getLog(MappedPropertyValues.class);
	
	private final Map<String, Integer> propertyIndexes;
	private final Object[] propertyValues;
	
	public MappedPropertyValues(Map<String, Integer> propertyIndexes, Object[] propertyValues)
	{
		this.propertyIndexes = propertyIndexes;
		this.propertyValues = propertyValues;
	}
	
	@Override
	public Set<String> getPropertyNames()
	{
		return Collections.unmodifiableSet(propertyIndexes.keySet());
	}

	@Override
	public Object getPropertyValue(String name)
	{
		Integer propertyIndex = propertyIndexes.get(name);
		Object value;
		if (propertyIndex == null)
		{
			if (log.isDebugEnabled())
			{
				log.debug("property " + name + " not found");
			}
			
			value = null;
		}
		else
		{
			// assuming the index is in bounds
			value = propertyValues[propertyIndex];
		}
		return value;
	}

}

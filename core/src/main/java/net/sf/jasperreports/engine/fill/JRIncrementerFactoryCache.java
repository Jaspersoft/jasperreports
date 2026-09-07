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
package net.sf.jasperreports.engine.fill;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.collections4.map.ReferenceMap;

import net.sf.jasperreports.engine.JRRuntimeException;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class JRIncrementerFactoryCache
{


	/**
	 *
	 */
	private static Map<Class<?>,JRIncrementerFactory> factoriesMap = 
		new ReferenceMap<>(
			ReferenceMap.ReferenceStrength.WEAK, ReferenceMap.ReferenceStrength.HARD
			);


	/**
	 *
	 */
	public static synchronized JRIncrementerFactory getInstance(Class<?> factoryClass)
	{
		JRIncrementerFactory incrementerFactory = factoriesMap.get(factoryClass);

		if (incrementerFactory == null)
		{
			try
			{
				incrementerFactory = factoryClass.asSubclass(JRIncrementerFactory.class).getDeclaredConstructor().newInstance();
			}
			catch (InstantiationException | IllegalAccessException 
				| NoSuchMethodException | InvocationTargetException e)
			{
				throw new JRRuntimeException(e);
			}

			factoriesMap.put(factoryClass, incrementerFactory);
		}
		
		return incrementerFactory;
	}


	private JRIncrementerFactoryCache()
	{
	}
}

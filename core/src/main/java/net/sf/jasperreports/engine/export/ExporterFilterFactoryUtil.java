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

import java.lang.reflect.InvocationTargetException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.util.JRClassLoader;

/**
 * A {@link ExporterFilterFactory} utility class.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public final class ExporterFilterFactoryUtil
{
	public static final String EXCEPTION_MESSAGE_KEY_FACTORY_CLASS_NOT_FOUND = "export.filter.factory.class.not.found";
	public static final String EXCEPTION_MESSAGE_KEY_FACTORY_CLASS_INSTANCE_FAILURE = "export.filter.factory.class.instance.failure";
	
	//private static final JRSingletonCache cache = new JRSingletonCache(ExporterFilterFactory.class);

	/**
	 * Returns an exporter filter factory based on a factory class name.
	 * 
	 * @param factoryClassName the factory class name
	 * @return an exporter filter factory instance
	 * @throws JRException
	 */
	public static ExporterFilterFactory getFilterFactory(String factoryClassName) throws JRException
	{
		//return (ExporterFilterFactory) cache.getCachedInstance(factoryClassName);
		try
		{
			Class<?> clazz = JRClassLoader.resolveClassForName(factoryClassName);
			return clazz.asSubclass(ExporterFilterFactory.class).getDeclaredConstructor().newInstance();
		}
		catch (ClassNotFoundException e)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_FACTORY_CLASS_NOT_FOUND,
					new Object[]{factoryClassName}, 
					e);
		}
		catch (InstantiationException | IllegalAccessException 
			| NoSuchMethodException | InvocationTargetException e)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_FACTORY_CLASS_INSTANCE_FAILURE,
					new Object[]{factoryClassName}, 
					e);
		}
	}


	private ExporterFilterFactoryUtil()
	{
	}
}

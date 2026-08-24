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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.jasperreports.engine.JRRuntimeException;

/**
 * Class utilities.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public final class ClassUtils
{
	public static final String EXCEPTION_MESSAGE_KEY_CLASS_INSTANCE_ERROR = "util.class.instance.error";
	public static final String EXCEPTION_MESSAGE_KEY_CLASS_LOADING_ERROR = "util.class.loading.error";
	public static final String EXCEPTION_MESSAGE_KEY_CLASS_UNEXPECTED_TYPE = "util.class.unexpected.type";

	/**
	 * Instantiates a class.
	 * 
	 * <p>
	 * The class is expected to have a public no-argument constructor.
	 * 
	 * @param className the class name
	 * @param expectedType the expected (super) type of the result
	 * @return a newly created instance of the specified class
	 * @throws JRRuntimeException if the class cannot be loaded or instantiated,
	 * or if it does not implement the expected type
	 */
	public static final Object instantiateClass(String className, Class<?> expectedType)
	{
		try
		{
			Class<?> clazz = JRClassLoader.loadClassForName(className);
			if (!expectedType.isAssignableFrom(clazz))
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_CLASS_UNEXPECTED_TYPE,
						new Object[]{className, expectedType.getName()});
			}
			return clazz.getDeclaredConstructor().newInstance();
		}
		catch (ClassNotFoundException e)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_CLASS_LOADING_ERROR,
					new Object[]{className},
					e);
		}
		catch (InstantiationException | IllegalAccessException 
			| NoSuchMethodException | InvocationTargetException e)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_CLASS_INSTANCE_ERROR,
					new Object[]{className},
					e);
		}
	}
	
	/**
	 * @param clazz
	 * @return a list of interfaces implemented by this class and by its superclasses, 
	 * or an empty list in case there are no implemented interfaces
	 */
	public static List<Class<?>> getInterfaces(Class<?> clazz)
	{
		List<Class<?>> interfaces = new ArrayList<>();
		while (clazz != null)
		{
			interfaces.addAll(Arrays.asList(clazz.getInterfaces()));
			clazz = clazz.getSuperclass();
		}
		return interfaces;
	}

	private ClassUtils()
	{
	}
}

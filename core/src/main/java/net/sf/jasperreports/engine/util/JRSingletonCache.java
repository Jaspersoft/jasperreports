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
import java.util.Map;

import org.apache.commons.collections4.map.ReferenceMap;

import net.sf.jasperreports.engine.JRException;


/**
 * Utility to use as a soft cache of singleton instances.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRSingletonCache<T>
{
	private static final Object CONTEXT_KEY_NULL = new Object();
	public static final String EXCEPTION_MESSAGE_KEY_CLASS_NOT_COMPATIBLE = "util.singleton.cache.class.not.compatible";
	public static final String EXCEPTION_MESSAGE_KEY_CLASS_NOT_FOUND = "util.singleton.cache.class.not.found";
	public static final String EXCEPTION_MESSAGE_KEY_INSTANCE_ERROR = "util.singleton.cache.instance.error";
	
	private final ReferenceMap<Object, Map<String,T>> cache;
	private final Class<T> itf;

	/**
	 * Creates a cache of singleton instances.
	 * 
	 * @param itf a interface or class that should be implemented by all classes cached by this object
	 */
	public JRSingletonCache(Class<T> itf)
	{
		cache = new ReferenceMap<>(ReferenceMap.ReferenceStrength.WEAK, ReferenceMap.ReferenceStrength.SOFT);
		this.itf = itf;
	}

	/**
	 * Returns the singleton instance corresponding to a class.
	 * <p>
	 * The instance is first searched into the cache and created if not found.
	 * <p>
	 * The class is expected to have a no-argument constructor.
	 * 
	 * @param className
	 * @return the singleton instance corresponding to a class
	 * @throws JRException
	 */
	public synchronized T getCachedInstance(String className) throws JRException
	{
		Map<String,T> contextCache = getContextInstanceCache();
		T instance = contextCache.get(className);
		if (instance == null)
		{
			instance = createInstance(className);
			contextCache.put(className, instance);
		}
		return instance;
	}

	protected T createInstance(String className) throws JRException
	{
		try
		{
			@SuppressWarnings("unchecked")
			Class<? extends T> clazz = (Class<? extends T>) JRClassLoader.resolveClassForName(className);
			if (itf != null && !itf.isAssignableFrom(clazz))
			{
				throw 
					new JRException(
						EXCEPTION_MESSAGE_KEY_CLASS_NOT_COMPATIBLE,
						new Object[]{className, itf.getName()});
			}

			return clazz.getDeclaredConstructor().newInstance();
		}
		catch (ClassNotFoundException e)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_CLASS_NOT_FOUND,
					new Object[]{className},
					e);
		}
		catch (InstantiationException | IllegalAccessException 
			| NoSuchMethodException | InvocationTargetException e)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_INSTANCE_ERROR,
					new Object[]{className},
					e);
		}
	}

	protected Map<String,T> getContextInstanceCache()
	{
		Object contextKey = getContextKey();
		Map<String,T> contextCache = cache.get(contextKey);
		if (contextCache == null)
		{
			contextCache = new ReferenceMap<>();
			cache.put(contextKey, contextCache);
		}
		return contextCache;
	}
	
	protected Object getContextKey()
	{
		Object key = Thread.currentThread().getContextClassLoader();
		if (key == null)
		{
			key = CONTEXT_KEY_NULL;
		}
		return key;
	}
}

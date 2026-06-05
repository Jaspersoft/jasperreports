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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.design.CompiledClasses;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRClassLoader extends ClassLoader
{
	
	private static final Map<String, String> PRIMITIVE_COMPONENT_ENCODING;
	static
	{
		PRIMITIVE_COMPONENT_ENCODING = new HashMap<>();
		//taken from https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#getName--
		PRIMITIVE_COMPONENT_ENCODING.put(Boolean.TYPE.getName(), "Z");
		PRIMITIVE_COMPONENT_ENCODING.put(Byte.TYPE.getName(), "B");
		PRIMITIVE_COMPONENT_ENCODING.put(Character.TYPE.getName(), "C");
		PRIMITIVE_COMPONENT_ENCODING.put(Double.TYPE.getName(), "D");
		PRIMITIVE_COMPONENT_ENCODING.put(Float.TYPE.getName(), "F");
		PRIMITIVE_COMPONENT_ENCODING.put(Integer.TYPE.getName(), "I");
		PRIMITIVE_COMPONENT_ENCODING.put(Long.TYPE.getName(), "J");
		PRIMITIVE_COMPONENT_ENCODING.put(Short.TYPE.getName(), "S");
	}

	private static ProtectionDomainFactory protectionDomainFactory;
	
	public static synchronized ProtectionDomainFactory getProtectionDomainFactory()
	{
		if (protectionDomainFactory == null)
		{
			protectionDomainFactory = new SingleProtectionDomainFactory(JRClassLoader.class.getProtectionDomain());
		}

		return protectionDomainFactory;
	}
	
	/**
	 * Sets the protection to be used for classes loaded via
	 * the {@link #loadClassFromBytes(String, byte[]) loadClassFromBytes} method.
	 *
	 * By default, the protection domain of this class is used for the loaded classes. 
	 * 
	 * @param protectionDomain the protection domain to be used
	 * @see #loadClassFromBytes(String, byte[])
	 */
	public static void setProtectionDomain(ProtectionDomain protectionDomain)
	{
		SingleProtectionDomainFactory factory = new SingleProtectionDomainFactory(protectionDomain);
		setProtectionDomainFactory(factory);
	}
	
	/**
	 * Sets a protection domain factory to be used for creating protection domains
	 * for the classes loaded by instances of this class.
	 * <p>
	 * For every instance of this class,
	 * {@link ProtectionDomainFactory#getProtectionDomain(ClassLoader) getProtectionDomain} is called
	 * and the resulting protection domain is used when loading classes through the newly created
	 * classloader.
	 * 
	 * @param protectionDomainFactory the protection domain factory.
	 * @see ProtectionDomainFactory#getProtectionDomain(ClassLoader)
	 */
	public static void setProtectionDomainFactory(ProtectionDomainFactory protectionDomainFactory)
	{
		JRClassLoader.protectionDomainFactory = protectionDomainFactory;
	}
	
	private ProtectionDomain protectionDomain;
	
	private ClassFilter classLoaderFilter;
	
	/**
	 *
	 */
	protected JRClassLoader()
	{
		super();
	}

	protected JRClassLoader(ClassFilter classLoaderFilter)
	{
		super();
		
		this.classLoaderFilter = classLoaderFilter;
	}

	/**
	 *
	 */
	protected JRClassLoader(ClassLoader parent)
	{
		super(parent);
	}

	protected JRClassLoader(ClassLoader parent, ClassFilter classLoaderFilter)
	{
		super(parent);
		
		this.classLoaderFilter = classLoaderFilter;
	}


	/**
	 *
	 */
	public static Class<?> loadClassForName(String className) throws ClassNotFoundException
	{
		Class<?> clazz = null;

		String classRealName = className;
		ClassNotFoundException initialEx = null;

		try
		{
			clazz = loadClassForRealName(classRealName);
		}
		catch (ClassNotFoundException e)
		{
			initialEx = e;
		}
		
		int lastDotIndex = 0;
		while (clazz == null && (lastDotIndex = classRealName.lastIndexOf('.')) > 0)
		{
			classRealName = 
				classRealName.substring(0, lastDotIndex) + "$" + classRealName.substring(lastDotIndex + 1);
			try
			{
				clazz = loadClassForRealName(classRealName);
			}
			catch (ClassNotFoundException e)
			{
			}
		}
		
		if (clazz == null)
		{
			throw initialEx;
		}
		
		return clazz;
	}


	/**
	 *
	 */
	public static Class<?> loadClassForRealName(String className) throws ClassNotFoundException
	{
		Class<?> clazz = null;

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader != null)
		{
			try
			{
				clazz = Class.forName(className, true, classLoader);
			}
			catch (ClassNotFoundException e)
			{
				//if (log.isWarnEnabled())
				//	log.warn("Failure using Thread.currentThread().getContextClassLoader() in JRClassLoader class. Using JRClassLoader.class.getClassLoader() instead.");
			}
		}

		if (clazz == null)
		{
			classLoader = JRClassLoader.class.getClassLoader();
			if (classLoader == null)
			{
				clazz = Class.forName(className);
			}
			else
			{
				clazz = Class.forName(className, true, classLoader);
			}
		}

		return clazz;
	}


	/**
	 *
	 */
	public static Class<?> loadClassFromFile(String className, File file) throws IOException
	{
		Class<?> clazz = null;

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader != null)
		{
			try
			{
				clazz = 
					(new JRClassLoader(classLoader))
						.loadClass(className, file);
			}
			catch(NoClassDefFoundError e)
			{
				//if (log.isWarnEnabled())
				//	log.warn("Failure using Thread.currentThread().getContextClassLoader() in JRClassLoader class. Using JRClassLoader.class.getClassLoader() instead.");
			}
		}
	
		if (clazz == null)
		{
			classLoader = JRClassLoader.class.getClassLoader();
			if (classLoader == null)
			{
				clazz = 
					(new JRClassLoader())
						.loadClass(className, file);
			}
			else
			{
				clazz = 
					(new JRClassLoader(classLoader))
						.loadClass(className, file);
			}
		}
		
		return clazz;
	}


	/**
	 *
	 */
	public static Class<?> loadClassFromBytes(String className, byte[] bytecodes)
	{
		return loadClassFromBytes(null, className, bytecodes);
	}
	
	public static Class<?> loadClassFromBytes(ClassFilter classLoaderFilter, 
			String className, byte[] bytecodes)
	{
		return loadClassFromBytes(classLoaderFilter, className, CompiledClasses.forClass(className, bytecodes));
	}
	
	public static Class<?> loadClassFromBytes(ClassFilter classLoaderFilter, 
			String className, CompiledClasses compiledClasses)
	{
		Class<?> clazz = null;

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader != null)
		{
			try
			{
				clazz = 
					(new CompiledClassesLoader(classLoader, classLoaderFilter, compiledClasses))
						.loadCompiledClass(className);
			}
			catch(NoClassDefFoundError e)
			{
				//if (log.isWarnEnabled())
				//	log.warn("Failure using Thread.currentThread().getContextClassLoader() in JRClassLoader class. Using JRClassLoader.class.getClassLoader() instead.");
			}
		}
	
		if (clazz == null)
		{
			classLoader = JRClassLoader.class.getClassLoader();
			if (classLoader == null)
			{
				clazz = 
					(new CompiledClassesLoader(classLoaderFilter, compiledClasses))
						.loadCompiledClass(className);
			}
			else
			{
				clazz = 
					(new CompiledClassesLoader(classLoader, classLoaderFilter, compiledClasses))
						.loadCompiledClass(className);
			}
		}

		return clazz;
	}


	/**
	 *
	 */
	protected Class<?> loadClass(String className, File file) throws IOException
	{
		return loadClass(className, Files.readAllBytes(file.toPath()));
	}

	protected synchronized ProtectionDomain getProtectionDomain()
	{
		if (protectionDomain == null)
		{
			protectionDomain = getProtectionDomainFactory().getProtectionDomain(this);
		}
		return protectionDomain;
	}
	
	/**
	 *
	 */
	protected Class<?> loadClass(String className, byte[] bytecodes)
	{
		Class<?> clazz = null;

		clazz = 
			defineClass(
				className, 
				bytecodes, 
				0, 
				bytecodes.length,
				getProtectionDomain()
				);

		return clazz;
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException, JRRuntimeException
	{
		if (classLoaderFilter != null)
		{
			classLoaderFilter.checkClassVisibility(name);
		}
		
		return super.loadClass(name, resolve);
	}

	/**
	 *
	 */
	public static String getClassRealName(String className)
	{
		if (className == null)
		{
			return null;
		}
		
		int ltPos = className.indexOf('<');
		if (ltPos > 0)
		{
			int gtPos = className.lastIndexOf('>');
			if (gtPos > ltPos)
			{
				className = className.substring(0, ltPos) + className.substring(gtPos + 1);
			}
		}
		
		int arrayDimension = 0;
		int classNameEnd = className.length();
		int index = 0;
		int pos = 0;
		while (index < classNameEnd && (pos = className.indexOf('[', index)) >= 0)
		{
			if (index == 0)
			{
				classNameEnd = pos;
			}
			index = pos;
			arrayDimension++;
		}

		if (arrayDimension > 0)
		{
			StringBuilder sb = new StringBuilder();
			
			for(int i = 0; i < arrayDimension; i++)
			{
				sb.append('[');
			}
			
			String componentClass = className.substring(0, classNameEnd);
			String primitiveEncoding = PRIMITIVE_COMPONENT_ENCODING.get(componentClass);
			if (primitiveEncoding == null)
			{
				//non primitive component
				sb.append('L');
				sb.append(componentClass);
				sb.append(';');
			}
			else
			{
				sb.append(primitiveEncoding);
			}

			return sb.toString();
		}
		
		return className;
	}


}

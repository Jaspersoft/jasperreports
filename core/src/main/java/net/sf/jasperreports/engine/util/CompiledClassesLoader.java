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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.design.CompiledClasses;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class CompiledClassesLoader extends JRClassLoader
{

	private static final Log log = LogFactory.getLog(CompiledClassesLoader.class);
	
	private CompiledClasses compiledClasses;

	protected CompiledClassesLoader(ClassFilter classLoaderFilter,
			CompiledClasses compiledClasses)
	{
		super(classLoaderFilter);
		this.compiledClasses = compiledClasses;
	}

	protected CompiledClassesLoader(ClassLoader parent, ClassFilter classLoaderFilter,
			CompiledClasses compiledClasses)
	{
		super(parent, classLoaderFilter);
		this.compiledClasses = compiledClasses;
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException
	{
		byte[] classBytes = compiledClasses.getClassBytes(name);
		if (classBytes == null)
		{
			return super.findClass(name);
		}
		
		if (log.isDebugEnabled())
		{
			log.debug("found compiled class " + name);
		}
		
		return loadClass(name, classBytes);
	}
	
	protected Class<?> loadCompiledClass(String name)
	{
		byte[] classBytes = compiledClasses.getClassBytes(name);
		if (classBytes == null)
		{
			throw new JRRuntimeException("Unkonwn class " + name);
		}
		
		if (log.isDebugEnabled())
		{
			log.debug("loading compiled class " + name);
		}
		
		return loadClass(name, classBytes);
	}
	
}

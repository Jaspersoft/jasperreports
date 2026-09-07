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
package net.sf.jasperreports.dataadapters;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.ParameterContributorContext;
import net.sf.jasperreports.engine.util.CompositeClassloader;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class AbstractClasspathAwareDataAdapterService extends AbstractDataAdapterService 
{
	public static final String CURRENT_CLASS_LOADER = "CURRENT_CLASS_LOADER";

	/**
	 *
	 */
	public AbstractClasspathAwareDataAdapterService(ParameterContributorContext paramContribContext, ClasspathAwareDataAdapter dataAdapter) 
	{
		super(paramContribContext, dataAdapter);
	}

	/**
	 *
	 */
	protected ClassLoader getClassLoader(ClassLoader cloader)
	{ 
		Object obj = getJasperReportsContext().getValue(CURRENT_CLASS_LOADER);
		if (obj != null && obj instanceof ClassLoader)
			cloader = (ClassLoader)obj ; 
		URL[] localURLs = getPathClassloader();
		if (localURLs == null || localURLs.length == 0)
			return cloader;
		return new CompositeClassloader(new URLClassLoader(localURLs), cloader); 
	}

	protected URL[] getPathClassloader() {  
		ClasspathAwareDataAdapter dataAdapter = (ClasspathAwareDataAdapter)getDataAdapter();
		List<String> classpath = dataAdapter.getClasspath();
		if (classpath.isEmpty())
			return null;
		List<URL> urls = new ArrayList<>();
		for (String path : classpath) 
		{
			if (path == null || path.isEmpty())
				continue;
			try { 
				if (path.startsWith("\\w+?://"))
					urls.add( new URL(path));
				else
					urls.add( new URL("file", "", path));
			} catch (MalformedURLException e) {
				// e.printStackTrace();
				// We don't care if the entry cannot be found.
			}
		} 
		if (urls.isEmpty())
			return null;
		return  urls.toArray(new URL[urls.size()]) ;
	}

}

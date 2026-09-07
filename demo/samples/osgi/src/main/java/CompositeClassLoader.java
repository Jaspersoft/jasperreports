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
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


/**
 * A classloader that aggregates resource lookups and class loading across
 * multiple delegate classloaders. Used in OSGi environments where each bundle
 * has an isolated classloader and no single classloader can see resources
 * from all bundles.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class CompositeClassLoader extends ClassLoader
{
	private final List<ClassLoader> delegates;


	public CompositeClassLoader(List<ClassLoader> delegates)
	{
		super(null);
		this.delegates = delegates;
	}


	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException
	{
		for (ClassLoader delegate : delegates)
		{
			try
			{
				return delegate.loadClass(name);
			}
			catch (ClassNotFoundException e)
			{
				//try next
			}
		}
		throw new ClassNotFoundException(name);
	}


	@Override
	public URL getResource(String name)
	{
		for (ClassLoader delegate : delegates)
		{
			URL url = delegate.getResource(name);
			if (url != null)
			{
				return url;
			}
		}
		return null;
	}


	@Override
	public Enumeration<URL> getResources(String name) throws IOException
	{
		Set<URL> urls = new LinkedHashSet<>();
		for (ClassLoader delegate : delegates)
		{
			Enumeration<URL> delegateUrls = delegate.getResources(name);
			while (delegateUrls.hasMoreElements())
			{
				urls.add(delegateUrls.nextElement());
			}
		}
		return Collections.enumeration(urls);
	}
}

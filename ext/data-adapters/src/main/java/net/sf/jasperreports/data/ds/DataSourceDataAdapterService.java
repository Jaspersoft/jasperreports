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
package net.sf.jasperreports.data.ds;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

import net.sf.jasperreports.dataadapters.AbstractClasspathAwareDataAdapterService;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.ParameterContributorContext;
import net.sf.jasperreports.engine.util.JRClassLoader;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class DataSourceDataAdapterService extends
		AbstractClasspathAwareDataAdapterService {

	private static final String EXCEPTION_MESSAGE_KEY_INVALID_OBJECT_RETURNED = "data.ds.invalid.object.returned";
	
	/**
	 * 
	 */
	public DataSourceDataAdapterService(ParameterContributorContext paramContribContext, DataSourceDataAdapter dsDataAdapter) 
	{
		super(paramContribContext, dsDataAdapter);
	}

	public DataSourceDataAdapter getDataSourceDataAdapter() {
		return (DataSourceDataAdapter) getDataAdapter();
	}

	@Override
	protected ClassLoader getClassLoader(ClassLoader cloader) {
		Object obj = getJasperReportsContext().getValue(CURRENT_CLASS_LOADER);
		if (obj != null && obj instanceof ClassLoader)
			cloader = (ClassLoader) obj;
		URL[] localURLs = getPathClassloader();
		if (localURLs == null || localURLs.length == 0)
			return cloader;
		return new URLClassLoader(localURLs, cloader);
	}
	
	@Override
	public void contributeParameters(Map<String, Object> parameters) throws JRException 
	{
		DataSourceDataAdapter dsDataAdapter = getDataSourceDataAdapter();
		if (dsDataAdapter != null)
		{
			JRDataSource ds = null;

			ClassLoader oldThreadClassLoader = Thread.currentThread().getContextClassLoader();

			try 
			{
				Thread.currentThread().setContextClassLoader(getClassLoader(oldThreadClassLoader));

				Class<?> clazz = JRClassLoader.loadClassForRealName(dsDataAdapter.getFactoryClass());
				Object obj = null;
				Method method = clazz.getMethod( dsDataAdapter.getMethodToCall(), new Class[0]);
				if (!Modifier.isStatic(method.getModifiers()))
					obj = clazz.getDeclaredConstructor().newInstance();
				if (JRDataSource.class.isAssignableFrom(method.getReturnType()))
				{
					ds = (JRDataSource) method.invoke(obj,new Object[0]);
				}
				else
				{
					throw 
					new JRException(
						EXCEPTION_MESSAGE_KEY_INVALID_OBJECT_RETURNED,
						new Object[]{dsDataAdapter.getMethodToCall(), dsDataAdapter.getFactoryClass()});
				}
			}
			catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException 
				| IllegalAccessException | InstantiationException | NoClassDefFoundError e) 
			{
				throw new JRException(e);			
			} 
			finally
			{
				Thread.currentThread().setContextClassLoader(oldThreadClassLoader);
			}

			parameters.put(JRParameter.REPORT_DATA_SOURCE, ds);
		}
	}
}

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
package net.sf.jasperreports.engine.data;

import java.util.List;
import java.util.ListIterator;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRuntimeException;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class DataSourceCollection<D extends JRDataSource, P extends DataSourceProvider<D>> implements JRDataSource
{
	private static final String EXCEPTION_MESSAGE_KEY_METHOD_CALL_ERROR = "data.source.collection.method.call.error";
	
	protected final boolean empty;
	protected final ListIterator<? extends P> providerIterator;
	protected D currentDataSource;
	
	public DataSourceCollection(List<? extends P> dataSourceProviders) throws JRException
	{
		this.empty = dataSourceProviders.isEmpty();
		this.providerIterator = dataSourceProviders.listIterator();
		
		start();
	}

	protected final void start() throws JRException
	{
		if (providerIterator.hasNext())
		{
			DataSourceProvider<D> provider = providerIterator.next();
			this.currentDataSource = provider.getDataSource();
		}
	}
	
	public D currentDataSource()
	{
		return currentDataSource;
	}

	@Override
	public boolean next() throws JRException
	{
		if (empty)
		{
			return false;
		}
		
		if (currentDataSource != null && currentDataSource.next())
		{
			return true;
		}
		
		// go to the next data source that has records
		while (providerIterator.hasNext())
		{
			DataSourceProvider<D> provider = providerIterator.next();
			currentDataSource = provider.getDataSource();
			if (currentDataSource != null && currentDataSource.next())
			{
				return true;
			}
		}
		
		return false;
	}

	@Override
	public Object getFieldValue(JRField field) throws JRException
	{
		if (currentDataSource == null)
		{
			// should not happen
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_METHOD_CALL_ERROR,
					(Object[])null);
		}
		
		return currentDataSource.getFieldValue(field);
	}

}

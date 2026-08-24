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

import java.util.List;

import net.sf.jasperreports.data.bean.BeanDataAdapter;
import net.sf.jasperreports.data.bean.BeanDataAdapterService;
import net.sf.jasperreports.data.csv.CsvDataAdapter;
import net.sf.jasperreports.data.csv.CsvDataAdapterService;
import net.sf.jasperreports.data.ds.DataSourceDataAdapter;
import net.sf.jasperreports.data.ds.DataSourceDataAdapterService;
import net.sf.jasperreports.data.empty.EmptyDataAdapter;
import net.sf.jasperreports.data.empty.EmptyDataAdapterService;
import net.sf.jasperreports.data.jdbc.JdbcDataAdapter;
import net.sf.jasperreports.data.jdbc.JdbcDataAdapterContributorFactory;
import net.sf.jasperreports.data.jdbc.JdbcDataAdapterService;
import net.sf.jasperreports.data.jndi.JndiDataAdapter;
import net.sf.jasperreports.data.jndi.JndiDataAdapterService;
import net.sf.jasperreports.data.json.JsonDataAdapter;
import net.sf.jasperreports.data.json.JsonDataAdapterService;
import net.sf.jasperreports.data.provider.DataSourceProviderDataAdapter;
import net.sf.jasperreports.data.provider.DataSourceProviderDataAdapterService;
import net.sf.jasperreports.data.qe.QueryExecuterDataAdapter;
import net.sf.jasperreports.data.qe.QueryExecuterDataAdapterService;
import net.sf.jasperreports.data.random.RandomDataAdapter;
import net.sf.jasperreports.data.random.RandomDataAdapterService;
import net.sf.jasperreports.data.xml.XmlDataAdapter;
import net.sf.jasperreports.data.xml.XmlDataAdapterService;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.ParameterContributorContext;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class DefaultDataAdapterServiceFactory implements DataAdapterContributorFactory
{

	/**
	 *
	 */
	private static final DefaultDataAdapterServiceFactory INSTANCE = new DefaultDataAdapterServiceFactory();

	/**
	 *
	 */
	private DefaultDataAdapterServiceFactory()
	{
	}

	/**
	 *
	 */
	public static DefaultDataAdapterServiceFactory getInstance()
	{
		return INSTANCE;
	}
	
	@Override
	public DataAdapterService getDataAdapterService(ParameterContributorContext context, DataAdapter dataAdapter)
	{
		//JasperReportsContext jasperReportsContext = context.getJasperReportsContext();
		DataAdapterService dataAdapterService = null;
		
		if (dataAdapter instanceof BeanDataAdapter)
		{
			dataAdapterService = new BeanDataAdapterService(context, (BeanDataAdapter)dataAdapter);
		}
		else if (dataAdapter instanceof CsvDataAdapter)
		{
			dataAdapterService = new CsvDataAdapterService(context, (CsvDataAdapter)dataAdapter);
		}
		else if (dataAdapter instanceof DataSourceDataAdapter)
		{
			dataAdapterService = new DataSourceDataAdapterService(context, (DataSourceDataAdapter)dataAdapter);
		}
		else if (dataAdapter instanceof EmptyDataAdapter)
		{
			dataAdapterService = new EmptyDataAdapterService(context, (EmptyDataAdapter)dataAdapter);
		}
		else if (dataAdapter instanceof RandomDataAdapter)
		{
			dataAdapterService = new RandomDataAdapterService(context, (RandomDataAdapter)dataAdapter);
		}
		else if (dataAdapter instanceof JndiDataAdapter)
		{
			dataAdapterService = new JndiDataAdapterService(context, (JndiDataAdapter)dataAdapter);//FIXME maybe want some cache here
		}
		else if (dataAdapter instanceof DataSourceProviderDataAdapter)
		{
			dataAdapterService = new DataSourceProviderDataAdapterService(context, (DataSourceProviderDataAdapter)dataAdapter);
		}
		else if (dataAdapter instanceof QueryExecuterDataAdapter)
		{
			dataAdapterService = new QueryExecuterDataAdapterService(context, (QueryExecuterDataAdapter)dataAdapter);
		}
		else if (dataAdapter instanceof XmlDataAdapter)
		{
			dataAdapterService = new XmlDataAdapterService(context, (XmlDataAdapter)dataAdapter);
		}
		else if (dataAdapter instanceof JsonDataAdapter)
		{
			dataAdapterService = new JsonDataAdapterService(context, (JsonDataAdapter)dataAdapter);
		}
		else if (dataAdapter instanceof JdbcDataAdapter)
		{
			JasperReportsContext jasperReportsContext = context.getJasperReportsContext();
			
			List<JdbcDataAdapterContributorFactory> bundles = jasperReportsContext.getExtensions(
					JdbcDataAdapterContributorFactory.class);
			for (JdbcDataAdapterContributorFactory factory : bundles)
			{
				DataAdapterService service = factory.getDataAdapterService(context, (JdbcDataAdapter)dataAdapter);
				if (service != null)
				{
					dataAdapterService = service;
					break;
				}
			}

			if (dataAdapterService == null)
			{
				dataAdapterService = new JdbcDataAdapterService(context, (JdbcDataAdapter)dataAdapter);
			}
		}
		
		return dataAdapterService;
	}
  
}

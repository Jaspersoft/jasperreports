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
package net.sf.jasperreports.olap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.olap4j.OlapConnection;
import org.olap4j.OlapDatabaseMetaData;

import net.sf.jasperreports.data.xmla.XmlaDataAdapter;
import net.sf.jasperreports.dataadapters.AbstractDataAdapterService;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.ParameterContributorContext;
import net.sf.jasperreports.olap.xmla.JRXmlaQueryExecuterFactory;
import net.sf.jasperreports.olap.xmla.Olap4jXmlaQueryExecuter;
import net.sf.jasperreports.util.SecretsUtil;

/**
 * @author Veaceslov Chicu (schicu@users.sourceforge.net)
 */
public class XmlaDataAdapterService extends AbstractDataAdapterService 
{

	private static final Log log = LogFactory.getLog(XmlaDataAdapterService.class);
	
	private static final String EXCEPTION_MESSAGE_KEY_XMLA_CONNECTION = "data.xmla.connection";
	
	/**
	 * 
	 */
	public XmlaDataAdapterService(ParameterContributorContext paramContribContext, XmlaDataAdapter dataAdapter) 
	{
		super(paramContribContext, dataAdapter);
	}

	public XmlaDataAdapter getXmlaDataAdapter()
	{
		return (XmlaDataAdapter) getDataAdapter();
	}

	@Override
	public void contributeParameters(Map<String, Object> parameters)
			throws JRException {
		XmlaDataAdapter xmlaDA = getXmlaDataAdapter();
		if (xmlaDA != null) {
			parameters.put(JRXmlaQueryExecuterFactory.PARAMETER_XMLA_URL,
					xmlaDA.getXmlaUrl());
			parameters.put(
					JRXmlaQueryExecuterFactory.PARAMETER_XMLA_DATASOURCE,
					xmlaDA.getDatasource());
			parameters.put(JRXmlaQueryExecuterFactory.PARAMETER_XMLA_CATALOG,
					xmlaDA.getCatalog());

			String username = xmlaDA.getUsername();
			if (username != null && !username.isEmpty())
				parameters.put(JRXmlaQueryExecuterFactory.PARAMETER_XMLA_USER,
						username);

			String password = xmlaDA.getPassword();
			SecretsUtil secretService = SecretsUtil.getInstance(getJasperReportsContext());
			if (secretService != null)
				password = secretService.getSecret(SECRETS_CATEGORY, password);
			if (password != null && !password.isEmpty())
				parameters.put(JRXmlaQueryExecuterFactory.PARAMETER_XMLA_PASSWORD, password);
		}
	}

	@Override
	public void test() throws JRException
	{
		Map<String, Object> params = new HashMap<>();
		contributeParameters(params);
		
		Properties props = new Properties();
		putNonNull(props, Olap4jXmlaQueryExecuter.XMLA_SERVER, params.get(JRXmlaQueryExecuterFactory.PARAMETER_XMLA_URL));
		putNonNull(props, Olap4jXmlaQueryExecuter.XMLA_CATALOG, params.get(JRXmlaQueryExecuterFactory.PARAMETER_XMLA_CATALOG));
		putNonNull(props, Olap4jXmlaQueryExecuter.XMLA_DATA_SOURCE, params.get(JRXmlaQueryExecuterFactory.PARAMETER_XMLA_DATASOURCE));
		putNonNull(props, Olap4jXmlaQueryExecuter.XMLA_USER, params.get(JRXmlaQueryExecuterFactory.PARAMETER_XMLA_USER));
		putNonNull(props, Olap4jXmlaQueryExecuter.XMLA_PASSWORD, params.get(JRXmlaQueryExecuterFactory.PARAMETER_XMLA_PASSWORD));
		putNonNull(props, Olap4jXmlaQueryExecuter.OLAP4J_DRIVER, Olap4jXmlaQueryExecuter.OLAP4J_XMLA_DRIVER_CLASS);
		putNonNull(props, Olap4jXmlaQueryExecuter.OLAP4J_URL_PREFIX, Olap4jXmlaQueryExecuter.OLAP4J_XMLA_URL_PREFIX);
		
		try
		{
			// load driver  and Connection
			Class.forName(Olap4jXmlaQueryExecuter.OLAP4J_XMLA_DRIVER_CLASS);
			Connection connection = DriverManager.getConnection(Olap4jXmlaQueryExecuter.OLAP4J_XMLA_URL_PREFIX, props);
			OlapConnection olapConnection = connection.unwrap(OlapConnection.class);
			
			// doing something to validate the connection
			OlapDatabaseMetaData metaData = olapConnection.getMetaData();
			ResultSet datasources = null;
			try
			{
				// try olap4j 1.1 first
				Method method = OlapDatabaseMetaData.class.getMethod("getDatabases");
				datasources = (ResultSet) method.invoke(metaData);
			}
			catch (NoSuchMethodException e)
			{
				// not olap4j 1.1
				if (log.isDebugEnabled())
				{
					log.debug("OlapDatabaseMetaData.getDatabases method not found: " + e.getMessage());
				}
			}
			
			if (datasources == null)
			{
				try
				{
					// try olap4j 0.9
					Method method = OlapDatabaseMetaData.class.getMethod("getDatasources");
					datasources = (ResultSet) method.invoke(metaData);
				}
				catch (NoSuchMethodException e)
				{
					// not olap4j 0.9?  giving up
					if (log.isDebugEnabled())
					{
						log.debug("OlapDatabaseMetaData.getDatasources method not found: " + e.getMessage());
					}
				}
			}
			
			if (datasources != null)
			{
				// making sure the request is sent
				datasources.next();
				
				datasources.close();
			}
			
			connection.close();
		}
		catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | SQLException e)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_XMLA_CONNECTION, 
					null, 
					e);
		} 
		
		dispose();
	}
	
	private void putNonNull(Properties props, String key, Object value)
	{
		if (value != null)
		{
			props.put(key, value);
		}
	}

}

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
package net.sf.jasperreports.data.jdbc;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.dataadapters.AbstractClasspathAwareDataAdapterService;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.ParameterContributorContext;
import net.sf.jasperreports.engine.util.JRClassLoader;
import net.sf.jasperreports.util.SecretsUtil;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JdbcDataAdapterService extends AbstractClasspathAwareDataAdapterService 
{
	private static final Log log = LogFactory.getLog(JdbcDataAdapterService.class);
	public static final String EXCEPTION_MESSAGE_KEY_PASSWORD_REQUIRED = "data.jdbc.password.required";
	public static final String EXCEPTION_MESSAGE_KEY_INVALID_URL = "data.jdbc.invalid.url";
	public static final String EXCEPTION_MESSAGE_KEY_CONNECTION_NOT_CREATED = "data.jdbc.connection.not.created";
	
	private Connection connection = null; 

//	/**
//	 * This classloader is used to load JDBC drivers available in the set of
//	 * paths provided by classpathPaths.
//	 */
//	private ClassLoader classLoader = null;
//
//	/**
//	 * Same as getDriversClassLoader(false)
//	 * 
//	 * @return
//	 */
//	public ClassLoader getClassLoader() {
//		return getClassLoader(false);
//	}
//
//	/**
//	 * Return the classloader, an URLClassLoader made up with all the paths
//	 * defined to look for Drivers (mainly jars).
//	 * 
//	 * @param reload
//	 *            - if true, it forces a classloader rebuilt with the set of
//	 *            paths in classpathPaths.
//	 * @return
//	 */
//	public ClassLoader getClassLoader(boolean reload) {
//		if (classLoader == null || reload) {
//			List<String> paths = ((JdbcDataAdapter) getDataAdapter())
//					.getClasspathPaths();
//			List<URL> urls = new ArrayList<URL>();
//			for (String p : paths) {
//				FileResolver fileResolver = JRResourcesUtil
//						.getFileResolver(null);
//				File f = fileResolver.resolveFile(p);
//
//				if (f != null && f.exists()) {
//					try {
//						urls.add(f.toURI().toURL());
//					} catch (MalformedURLException e) {
//						// e.printStackTrace();
//						// We don't care if the entry cannot be found.
//					}
//				}
//			}
//
//			classLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]));
//		}
//
//		return classLoader;
//	}

	/**
	 * 
	 */
	public JdbcDataAdapterService(ParameterContributorContext paramContribContext, JdbcDataAdapter jdbcDataAdapter) 
	{
		super(paramContribContext, jdbcDataAdapter);
	}

	public JdbcDataAdapter getJdbcDataAdapter() {
		return (JdbcDataAdapter) getDataAdapter();
	}

	@Override
	public void contributeParameters(Map<String, Object> parameters) throws JRException 
	{
		try {
			connection = getConnection();
		} catch (SQLException e) {
			throw new JRException(e);
		}
		parameters.put(JRParameter.REPORT_CONNECTION, connection);
	}
	
	/**
	 * Some custom JDBC data adapters might require to tweak at runtime
	 * the URL information used to create the connection.
	 * 
	 * @return the custom (if needed) data adapter URL
	 */
	protected String getUrlForConnection() {
		JdbcDataAdapter jdbcDataAdapter = getJdbcDataAdapter();
		if (jdbcDataAdapter!=null) {
			return jdbcDataAdapter.getUrl();
		}
		else {
			return null;
		}
	}
	
	public Connection getConnection() throws SQLException{
		JdbcDataAdapter jdbcDataAdapter = getJdbcDataAdapter();
		if (jdbcDataAdapter != null) 
		{
			ClassLoader oldThreadClassLoader = Thread.currentThread().getContextClassLoader();

			try 
			{
				Thread.currentThread().setContextClassLoader(getClassLoader(oldThreadClassLoader));
				
				Class<?> clazz = JRClassLoader.loadClassForRealName(jdbcDataAdapter.getDriver());
				Driver driver = (Driver) clazz.getDeclaredConstructor().newInstance();
				
//				Driver driver = (Driver) (Class.forName(
//						jdbcDataAdapter.getDriver(), true, getClassLoader()))
//						.getDeclaredConstructor().newInstance();

				
				Properties	connectProps = new Properties();
				Map<String, String> map = jdbcDataAdapter.getProperties();
				if (map != null)
					for (Entry<String, String> entry : map.entrySet())
						connectProps.setProperty(entry.getKey(), entry.getValue());
				

				String password = jdbcDataAdapter.getPassword();
				SecretsUtil secretService = SecretsUtil.getInstance(getJasperReportsContext());
				if (secretService != null)
					password = secretService.getSecret(SECRETS_CATEGORY, password);

				connectProps.setProperty("user", jdbcDataAdapter.getUsername());
				connectProps.setProperty("password", password);
				
				connection = driver.connect(getUrlForConnection(), connectProps);
				if (connection == null)
				{
					boolean urlValid = driver.acceptsURL(getUrlForConnection());
					if (!urlValid)
					{
						throw new JRRuntimeException(EXCEPTION_MESSAGE_KEY_INVALID_URL, 
								new Object[] {getUrlForConnection(), jdbcDataAdapter.getDriver()});
					}
					
					throw new JRRuntimeException(EXCEPTION_MESSAGE_KEY_CONNECTION_NOT_CREATED, 
							new Object[] {getUrlForConnection()});
				}
				
				setupConnection(jdbcDataAdapter);
			}
			catch (ClassNotFoundException | InstantiationException | IllegalAccessException 
				| NoSuchMethodException | InvocationTargetException e) {
				throw new JRRuntimeException(e);
			} finally {
				Thread.currentThread().setContextClassLoader(oldThreadClassLoader);
			}
			return connection;
		}
		return null;
	}

	protected void setupConnection(JdbcDataAdapter dataAdapter) throws SQLException
	{
		JRPropertiesUtil props = JRPropertiesUtil.getInstance(getJasperReportsContext());
		JRDataset dataset = getParameterContributorContext().getDataset();
		
		Boolean autoCommit = getAutoCommit(dataAdapter, props, dataset);
		if (autoCommit != null)
		{
			if (log.isDebugEnabled())
			{
				log.debug("setting auto commit " + autoCommit + " on connection " + connection);
			}
			connection.setAutoCommit(autoCommit);
		}
		
		Boolean readOnly = getReadOnly(dataAdapter, props, dataset);
		if (readOnly != null)
		{
			if (log.isDebugEnabled())
			{
				log.debug("setting read only " + readOnly + " on connection " + connection);
			}
			connection.setReadOnly(readOnly);
		}
		
		Integer transactionIsolation = getTransactionIsolation(dataAdapter, props, dataset);
		if (transactionIsolation != null)
		{
			if (log.isDebugEnabled())
			{
				log.debug("setting transaction isolation " + transactionIsolation + " on connection " + connection);
			}
			connection.setTransactionIsolation(transactionIsolation);
		}
	}
	
	protected Boolean getAutoCommit(JdbcDataAdapter dataAdapter, 
			JRPropertiesUtil props, JRDataset dataset)
	{
		Boolean autoCommit = dataAdapter.getAutoCommit();
		if (autoCommit != null)
		{
			return autoCommit;
		}
		
		return props.getBooleanProperty(dataset, JdbcDataAdapter.PROPERTY_DEFAULT_AUTO_COMMIT);
	}
	
	protected Boolean getReadOnly(JdbcDataAdapter dataAdapter, 
			JRPropertiesUtil props, JRDataset dataset)
	{
		Boolean readOnly = dataAdapter.getReadOnly();
		if (readOnly != null)
		{
			return readOnly;
		}
		
		return props.getBooleanProperty(dataset, JdbcDataAdapter.PROPERTY_DEFAULT_READ_ONLY);
	}
	
	protected Integer getTransactionIsolation(JdbcDataAdapter dataAdapter, 
			JRPropertiesUtil props, JRDataset dataset)
	{
		TransactionIsolation transactionIsolation = dataAdapter.getTransactionIsolation();
		if (transactionIsolation != null)
		{
			return transactionIsolation.getLevel();
		}
		
		String prop = props.getProperty(dataset, JdbcDataAdapter.PROPERTY_DEFAULT_TRANSACTION_ISOLATION);
		if (prop != null && !prop.trim().isEmpty())
		{
			transactionIsolation = TransactionIsolation.valueOf(prop.trim());
			return transactionIsolation.getLevel();
		}
		return null;
	}

	public String getPassword() throws JRException {
		throw 
			new JRException(
				EXCEPTION_MESSAGE_KEY_PASSWORD_REQUIRED,
				(Object[])null);
	}

	@Override
	public void dispose() 
	{
		if (connection != null) 
		{
			try 
			{
				connection.close();
			}
			catch (Exception ex) 
			{
				if (log.isErrorEnabled())
					log.error("Error while closing the connection.", ex);
			}
		}
	}
}

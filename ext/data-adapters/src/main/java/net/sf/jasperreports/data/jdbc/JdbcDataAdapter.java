/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2026 Actian Corporation, a division of HCL Software. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Actian,
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

import java.sql.Driver;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.dataadapters.ClasspathAwareDataAdapter;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface JdbcDataAdapter extends ClasspathAwareDataAdapter
{
	
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			sinceVersion = PropertyConstants.VERSION_6_13_0,
			valueType = Boolean.class
			)
	String PROPERTY_DEFAULT_AUTO_COMMIT = JRPropertiesUtil.PROPERTY_PREFIX 
			+ "jdbc.data.adapter.auto.commit";
	
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			sinceVersion = PropertyConstants.VERSION_6_13_0,
			valueType = Boolean.class
			)
	String PROPERTY_DEFAULT_READ_ONLY = JRPropertiesUtil.PROPERTY_PREFIX 
			+ "jdbc.data.adapter.read.only";
	
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			sinceVersion = PropertyConstants.VERSION_6_13_0,
			valueType = TransactionIsolation.class
			)
	String PROPERTY_DEFAULT_TRANSACTION_ISOLATION = JRPropertiesUtil.PROPERTY_PREFIX 
			+ "jdbc.data.adapter.transaction.isolation";
	
	public String getDatabase();
	
	public void setDatabase(String database);
	
	public String getDriver();
	
	public void setDriver(String driver);
	
	public String getPassword();
	
	public void setPassword(String password);
	
	public boolean isSavePassword();
	
	public void setSavePassword(boolean savePassword);
	
	public String getUrl();
	
	public void setUrl(String url);
	
	public String getUsername();
	
	public void setUsername(String username);
	
	public String getServerAddress();
	
	public void setServerAddress(String serverAddress);
	
	/**
	 * Set the properties passed to the driver when creating connections.
	 * 
	 * @param properties the JDBC driver properties
	 * @see Driver#connect(String, java.util.Properties) 
	 */
	public void setProperties(Map<String, String> properties);

	/**
	 * Returns the properties passed to the driver when creating connections.
	 * 
	 * @return JDBC driver properties
	 */
	@JsonDeserialize(using = MapDeserializer.class)
	@JsonSerialize(using = MapSerializer.class)
	@JsonMerge
	public Map<String, String> getProperties();
	
	public Boolean getAutoCommit();
	
	public void setAutoCommit(Boolean autoCommit);
	
	public Boolean getReadOnly();
	
	public void setReadOnly(Boolean readOnly);
	
	public TransactionIsolation getTransactionIsolation();
	
	public void setTransactionIsolation(TransactionIsolation isolation);
}

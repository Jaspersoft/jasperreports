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

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonRootName;

import net.sf.jasperreports.dataadapters.AbstractClasspathAwareDataAdapter;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */

@JsonRootName(value = "jdbcDataAdapter")
public class JdbcDataAdapterImpl extends AbstractClasspathAwareDataAdapter implements JdbcDataAdapter
{
	private String driver;
	private String username;
	private String password = null;
	private String url;
	private String database;
	private boolean savePassword;
	private String serverAddress;
	private Map<String, String> properties;
	private Boolean autoCommit;
	private Boolean readOnly;
	private TransactionIsolation transactionIsolation;
	
	public JdbcDataAdapterImpl() {
		setName("New JDBC Data Adapter");
	}

	@Override
	public String getDatabase() {
		return database;
	}
	
	@Override
	public void setDatabase(String database) {
		this.database = database;
	}
	
	@Override
	public String getDriver() {
		return driver;
	}
	
	@Override
	public void setDriver(String driver) {
		this.driver = driver;
	}
	
	@Override
	public String getPassword() {
		return password;
	}
	
	@Override
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public boolean isSavePassword() {
		return savePassword;
	}
	
	@Override
	public void setSavePassword(boolean savePassword) {
		this.savePassword = savePassword;
	}
	
	@Override
	public String getUrl() {
		return url;
	}
	
	@Override
	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override
	public String getUsername() {
		return username;
	}
	
	@Override
	public void setUsername(String username) {
		this.username = username;
	}
	
	@Override
	public String getServerAddress() {
		return this.serverAddress;
	}
	
	@Override
	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	@Override
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	@Override
	public Map<String, String> getProperties() {
		return properties;
	}

	@Override
	public Boolean getAutoCommit()
	{
		return autoCommit;
	}

	@Override
	public void setAutoCommit(Boolean autoCommit)
	{
		this.autoCommit = autoCommit;
	}

	@Override
	public Boolean getReadOnly()
	{
		return readOnly;
	}

	@Override
	public void setReadOnly(Boolean readOnly)
	{
		this.readOnly = readOnly;
	}

	@Override
	public TransactionIsolation getTransactionIsolation()
	{
		return transactionIsolation;
	}

	@Override
	public void setTransactionIsolation(TransactionIsolation transactionIsolation)
	{
		this.transactionIsolation = transactionIsolation;
	}
}

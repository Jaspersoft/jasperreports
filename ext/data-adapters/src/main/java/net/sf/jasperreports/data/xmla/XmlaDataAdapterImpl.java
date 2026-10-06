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
package net.sf.jasperreports.data.xmla;

import com.fasterxml.jackson.annotation.JsonRootName;

import net.sf.jasperreports.dataadapters.AbstractDataAdapter;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */

@JsonRootName(value = "xmlaDataAdapter")
public class XmlaDataAdapterImpl extends AbstractDataAdapter implements
		XmlaDataAdapter {
	private String xmlaUrl;
	private String datasource;
	private String catalog;
	private String cube;

	private String username;
	private String password = null;
	private boolean savePassword;

	@Override
	public String getXmlaUrl() {
		return xmlaUrl;
	}

	@Override
	public void setXmlaUrl(String xmlaUrl) {
		this.xmlaUrl = xmlaUrl;
	}

	@Override
	public String getDatasource() {
		return datasource;
	}

	@Override
	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}

	@Override
	public String getCatalog() {
		return catalog;
	}

	@Override
	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	@Override
	public String getCube() {
		return cube;
	}

	@Override
	public void setCube(String cube) {
		this.cube = cube;
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
	public String getUsername() {
		return username;
	}

	@Override
	public void setUsername(String username) {
		this.username = username;
	}

}

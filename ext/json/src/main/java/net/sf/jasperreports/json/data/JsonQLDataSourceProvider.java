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
package net.sf.jasperreports.json.data;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.data.RewindableDataSourceProvider;
import net.sf.jasperreports.engine.data.TextDataSourceAttributes;

/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class JsonQLDataSourceProvider implements RewindableDataSourceProvider<JsonQLDataSource>
{

	private JasperReportsContext jasperReportsContext;
	private String jsonSource;
	private String queryString;
	private TextDataSourceAttributes textAttributes;

	public JsonQLDataSourceProvider(JasperReportsContext jasperReportsContext, String jsonSource, String queryString, TextDataSourceAttributes textAttributes)
	{
		this.jasperReportsContext = jasperReportsContext;
		this.jsonSource = jsonSource;
		this.queryString = queryString;
		this.textAttributes = textAttributes;
	}

	@Override
	public JsonQLDataSource getDataSource() throws JRException
	{
		JsonQLDataSource jsonQLDataSource = new JsonQLDataSource(jasperReportsContext, jsonSource, queryString);
		jsonQLDataSource.setTextAttributes(textAttributes);
		return jsonQLDataSource;
	}

	@Override
	public void rewind()
	{
		// we don't need to do anything here
	}

}

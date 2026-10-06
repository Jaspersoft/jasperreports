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
package net.sf.jasperreports.json.query;

import java.io.InputStream;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.data.RewindableDataSourceProvider;
import net.sf.jasperreports.engine.data.TextDataSourceAttributes;
import net.sf.jasperreports.engine.query.QueryExecutionContext;
import net.sf.jasperreports.engine.query.SimpleQueryExecutionContext;
import net.sf.jasperreports.engine.util.JRStringUtil;
import net.sf.jasperreports.json.data.JsonQLDataSource;
import net.sf.jasperreports.json.data.JsonQLDataSourceProvider;

/**
 * Simple JSON query executer implementation.
 * 
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class JsonQLQueryExecuter extends AbstractJsonQueryExecuter<JsonQLDataSource>
{
	public static final String CANONICAL_LANGUAGE = "JSONQL";

	/**
	 *
	 */
	public JsonQLQueryExecuter(
		JasperReportsContext jasperReportsContext,
		JRDataset dataset, 
		Map<String, ? extends JRValueParameter> parametersMap
		)
	{
		this(SimpleQueryExecutionContext.of(jasperReportsContext), 
				dataset, parametersMap);
	}
	
	public JsonQLQueryExecuter(
		QueryExecutionContext context,
		JRDataset dataset, 
		Map<String, ? extends JRValueParameter> parametersMap
		)
	{
		super(context, dataset, parametersMap);
	}

	@Override
	protected String getCanonicalQueryLanguage()
	{
		return CANONICAL_LANGUAGE;
	}

	@Override
	protected String getParameterReplacement(String parameterName)
	{
		Object parameterValue = getParameterValue(parameterName);

		if (parameterValue == null) {
			return null;
		}

		Class<?> valueClass= parameterValue.getClass();

		if (Number.class.isAssignableFrom(valueClass) || Boolean.class.equals(valueClass)) {
			return String.valueOf(parameterValue);
		}

		StringBuilder sb = new StringBuilder("\"");
		sb.append(JRStringUtil.escapeJavaStringLiteral(String.valueOf(parameterValue)));
		sb.append("\"");

		return sb.toString();
	}

	@Override
	protected JsonQLDataSource getJsonDataInstance(InputStream jsonInputStream) throws JRException {
		return new JsonQLDataSource(jsonInputStream, getQueryString());
	}

	@Override
	protected JsonQLDataSource getJsonDataInstance(String jsonSource) throws JRException {
		return new JsonQLDataSource(getRepositoryContext(), jsonSource, getQueryString());
	}

	@Override
	protected RewindableDataSourceProvider<JsonQLDataSource> getJsonDataProviderInstance(String source, TextDataSourceAttributes textAttributes) {
		return new JsonQLDataSourceProvider(getJasperReportsContext(), source, getQueryString(), textAttributes);
	}
}

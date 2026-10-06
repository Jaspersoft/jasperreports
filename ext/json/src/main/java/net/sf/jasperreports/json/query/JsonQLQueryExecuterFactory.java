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

import java.util.Map;

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.query.JRQueryExecuter;
import net.sf.jasperreports.engine.query.QueryExecutionContext;
import net.sf.jasperreports.engine.query.SimpleQueryExecutionContext;

/**
 * JSON QL query executer factory.
 * <p/>
 * The factory creates {@link JsonQLQueryExecuter JsonQLQueryExecuter}
 * query executers.
 *
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class JsonQLQueryExecuterFactory extends JsonQueryExecuterFactory
{
	
	public static final String JSONQL_QUERY_EXECUTER_NAME = "net.sf.jasperreports.query.executer:JSONQL";
	
	@Override
	public JRQueryExecuter createQueryExecuter(
		JasperReportsContext jasperReportsContext,
		JRDataset dataset, 
		Map<String, ? extends JRValueParameter> parameters
		) throws JRException
	{
		return createQueryExecuter(SimpleQueryExecutionContext.of(jasperReportsContext), 
				dataset, parameters);
	}
	
	@Override
	public JRQueryExecuter createQueryExecuter(
		QueryExecutionContext context,
		JRDataset dataset, 
		Map<String, ? extends JRValueParameter> parameters
		) throws JRException
	{
		return new JsonQLQueryExecuter(context, dataset, parameters);
	}

	@Override
	public String getDesignation()
	{
		return JSONQL_QUERY_EXECUTER_NAME;
	}

}

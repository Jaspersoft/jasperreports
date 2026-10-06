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
package net.sf.jasperreports.poi.query;

import java.util.Map;

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.query.AbstractXlsQueryExecuterFactory;
import net.sf.jasperreports.engine.query.JRQueryExecuter;
import net.sf.jasperreports.engine.query.QueryExecutionContext;
import net.sf.jasperreports.engine.query.SimpleQueryExecutionContext;

/**
 * Query executer factory for XLS file type.
 * <p/>
 * The factory creates {@link net.sf.jasperreports.poi.query.XlsQueryExecuter XlsQueryExecuter}
 * query executers.
 * 
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 * @deprecated Replaced by {@link ExcelQueryExecuterFactory}.
 */
public class XlsQueryExecuterFactory extends AbstractXlsQueryExecuterFactory 
{
	private final static Object[] XLS_BUILTIN_PARAMETERS = {
			XLS_WORKBOOK, "org.apache.poi.ss.usermodel.Workbook",
			XLS_INPUT_STREAM, "java.io.InputStream",
			XLS_FILE, "java.io.File",
			XLS_SOURCE, "java.lang.String",
			XLS_COLUMN_NAMES, "java.lang.String",
			XLS_COLUMN_INDEXES, "java.lang.String",
			XLS_COLUMN_NAMES_ARRAY, "java.lang.String[]",
			XLS_COLUMN_INDEXES_ARRAY, "java.lang.Integer[]",
			XLS_DATE_FORMAT, "java.text.DateFormat",
			XLS_DATE_PATTERN, "java.lang.String",
			XLS_NUMBER_FORMAT, "java.text.NumberFormat",
			XLS_NUMBER_PATTERN, "java.lang.String",
			XLS_USE_FIRST_ROW_AS_HEADER, "java.lang.Boolean",
			XLS_LOCALE, "java.util.Locale",
			XLS_LOCALE_CODE, "java.lang.String",
			XLS_TIMEZONE, "java.util.TimeZone",
			XLS_TIMEZONE_ID, "java.lang.String",
			XLS_SHEET_SELECTION, "java.lang.String"
			};
	
	@Override
	public Object[] getBuiltinParameters() {
		return XLS_BUILTIN_PARAMETERS;
	}

	@Override
	public JRQueryExecuter createQueryExecuter(
		JasperReportsContext jasperReportsContext, 
		JRDataset dataset, 
		Map<String,? extends JRValueParameter> parameters
		) throws JRException 
	{
		return createQueryExecuter(SimpleQueryExecutionContext.of(jasperReportsContext), 
				dataset, parameters);
	}

	@Override
	public JRQueryExecuter createQueryExecuter(
		QueryExecutionContext context, 
		JRDataset dataset, 
		Map<String,? extends JRValueParameter> parameters
		) throws JRException 
	{
		return new XlsQueryExecuter(context, dataset, parameters);
	}

	@Override
	public boolean supportsQueryParameterType(String className) {
		return true;
	}
}

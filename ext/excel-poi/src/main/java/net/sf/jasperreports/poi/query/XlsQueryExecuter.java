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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Workbook;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.query.AbstractXlsQueryExecuter;
import net.sf.jasperreports.engine.query.AbstractXlsQueryExecuterFactory;
import net.sf.jasperreports.engine.query.QueryExecutionContext;
import net.sf.jasperreports.engine.query.SimpleQueryExecutionContext;
import net.sf.jasperreports.poi.data.XlsDataSource;

/**
 * XLS query executer implementation.
 * 
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 * @deprecated Replaced by {@link ExcelQueryExecuter}.
 */
public class XlsQueryExecuter extends AbstractXlsQueryExecuter {
	
	private static final Log log = LogFactory.getLog(XlsQueryExecuter.class);
	
	/**
	 * 
	 */
	protected XlsQueryExecuter(
		JasperReportsContext jasperReportsContext, 
		JRDataset dataset, 
		Map<String,? extends JRValueParameter> parametersMap
		)
	{
		this(SimpleQueryExecutionContext.of(jasperReportsContext), 
				dataset, parametersMap);
	}
	
	protected XlsQueryExecuter(
		QueryExecutionContext context, 
		JRDataset dataset, 
		Map<String,? extends JRValueParameter> parametersMap
		) 
	{
		super(context, dataset, parametersMap);
	}

	@Override
	public JRDataSource createDatasource() throws JRException {
		XlsDataSource datasource = null;
		try {
			Workbook workbook = (Workbook) getParameterValue(AbstractXlsQueryExecuterFactory.XLS_WORKBOOK);
			if (workbook != null) {
				datasource = new XlsDataSource(workbook);
			} else {
				InputStream xlsInputStream = (InputStream) getParameterValue(AbstractXlsQueryExecuterFactory.XLS_INPUT_STREAM);
				if (xlsInputStream != null) {
					datasource = new XlsDataSource(xlsInputStream);
				} else {
					File xlsFile = (File) getParameterValue(AbstractXlsQueryExecuterFactory.XLS_FILE);
					if (xlsFile != null) {
						datasource = new XlsDataSource(xlsFile);
					} else {
						String xlsSource = getStringParameterOrProperty(AbstractXlsQueryExecuterFactory.XLS_SOURCE);
						if (xlsSource != null) {
							datasource = new XlsDataSource(getRepositoryContext(), xlsSource);
						} else {
							if (log.isWarnEnabled()){
								log.warn("No XLS source was provided.");
							}
						}
					}
				}
			}
		} catch (IOException e) {
			throw new JRException(e);
		}
		
		if (datasource != null) {
			initDatasource(datasource);
		}
		
		return datasource;
	}
	
}

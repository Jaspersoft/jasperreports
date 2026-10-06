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
import net.sf.jasperreports.poi.data.JRXlsxDataSource;

/**
 * XLS query executer implementation.
 * 
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 * @deprecated Replaced by {@link ExcelQueryExecuter}.
 */
public class JRXlsxQueryExecuter extends AbstractXlsQueryExecuter
{
	
	private static final Log log = LogFactory.getLog(JRXlsxQueryExecuter.class);
	
	private JRXlsxDataSource datasource;
	
	/**
	 * 
	 */
	protected JRXlsxQueryExecuter(
		JasperReportsContext jasperReportsContext, 
		JRDataset dataset, 
		Map<String,? extends JRValueParameter> parametersMap
		)
	{
		this(SimpleQueryExecutionContext.of(jasperReportsContext), 
				dataset, parametersMap);
	}
	
	protected JRXlsxQueryExecuter(
		QueryExecutionContext context, 
		JRDataset dataset, 
		Map<String,? extends JRValueParameter> parametersMap
		) 
	{
		super(context, dataset, parametersMap);
	}

	@Override
	public JRDataSource createDatasource() throws JRException {
		try {
			Workbook workbook = (Workbook) getParameterValue(JRXlsxQueryExecuterFactory.XLSX_WORKBOOK);
			if (workbook == null)
			{
				workbook = (Workbook) getParameterValue(AbstractXlsQueryExecuterFactory.XLS_WORKBOOK, true);
			}
			if (workbook != null) {
				datasource = new JRXlsxDataSource(workbook);
			} else {
				InputStream xlsxInputStream = (InputStream) getParameterValue(JRXlsxQueryExecuterFactory.XLSX_INPUT_STREAM);
				if (xlsxInputStream == null)
				{
					xlsxInputStream = (InputStream) getParameterValue(AbstractXlsQueryExecuterFactory.XLS_INPUT_STREAM, true);
				}
				if (xlsxInputStream != null) {
					datasource = new JRXlsxDataSource(xlsxInputStream);
				} else {
					File xlsxFile = (File) getParameterValue(JRXlsxQueryExecuterFactory.XLSX_FILE);
					if (xlsxFile == null)
					{
						xlsxFile = (File) getParameterValue(AbstractXlsQueryExecuterFactory.XLS_FILE, true);
					}
					if (xlsxFile != null) {
						datasource = new JRXlsxDataSource(xlsxFile);
					} else {
						String xlsxSource = getStringParameterOrProperty(JRXlsxQueryExecuterFactory.XLSX_SOURCE);
						if (xlsxSource == null)
						{
							xlsxSource = getStringParameterOrProperty(AbstractXlsQueryExecuterFactory.XLS_SOURCE);
						}
						if (xlsxSource != null) {
							datasource = new JRXlsxDataSource(getRepositoryContext(), xlsxSource);//TODO
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

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
package net.sf.jasperreports.poi.data;

import java.io.IOException;
import java.util.Map;

import net.sf.jasperreports.data.excel.ExcelFormatEnum;
import net.sf.jasperreports.data.xls.AbstractXlsDataAdapterService;
import net.sf.jasperreports.data.xls.XlsDataAdapter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.ParameterContributorContext;
import net.sf.jasperreports.engine.data.AbstractXlsDataSource;
import net.sf.jasperreports.poi.query.ExcelQueryExecuterFactory;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class XlsDataAdapterService extends AbstractXlsDataAdapterService
{
	
	/**
	 * 
	 */
	public XlsDataAdapterService(ParameterContributorContext paramContribContext, XlsDataAdapter xlsDataAdapter)
	{
		super(paramContribContext, xlsDataAdapter);
	}
	
	@Override
	public void contributeParameters(Map<String, Object> parameters) throws JRException
	{
		super.contributeParameters(parameters);

		XlsDataAdapter xlsDataAdapter = getXlsDataAdapter();
		if (xlsDataAdapter != null)
		{
			if (xlsDataAdapter.isQueryExecuterMode())
			{	
				parameters.put(ExcelQueryExecuterFactory.XLS_FORMAT, ExcelFormatEnum.XLS);//add this just for the sake of ExcelQueryExecuter, which is called when queryMode=true
			}
		}
	}

	@Override
	protected AbstractXlsDataSource getXlsDataSource() throws JRException
	{
		AbstractXlsDataSource dataSource = null;
		try
		{
			dataSource = new ExcelDataSource(dataStream, ExcelFormatEnum.XLS);
		}
		catch (IOException e)
		{
			throw new JRException(e);
		}
		return dataSource;
	}
	
}

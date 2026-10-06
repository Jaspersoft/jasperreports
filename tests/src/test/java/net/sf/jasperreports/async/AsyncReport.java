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
package net.sf.jasperreports.async;

import java.util.Map;

import net.sf.jasperreports.Report;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.AsynchronousFillHandle;
import net.sf.jasperreports.engine.fill.FillListener;
import net.sf.jasperreports.web.servlets.AsyncJasperPrintAccessor;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class AsyncReport extends Report
{

	public AsyncReport(String jrxml, String jrpxml)
	{
		super(jrxml, jrpxml);
	}
	
	public void runReport(Map<String, Object> params, FillListener fillListener)
	{
		try
		{
			Map<String, Object> reportParams = reportParams(params);
			AsynchronousFillHandle asyncHandle = AsynchronousFillHandle.createHandle(
					jasperReportsContext, report, reportParams);
			
			if (fillListener != null)
			{
				asyncHandle.addFillListener(fillListener);
			}
			
			AsyncJasperPrintAccessor accessor = new AsyncJasperPrintAccessor(asyncHandle);
			asyncHandle.startFill();
			JasperPrint print = accessor.getFinalJasperPrint();
			reportComplete(null, reportParams, print);
		}
		catch (JRException e)
		{
			throw new RuntimeException(e);
		}
	}

}

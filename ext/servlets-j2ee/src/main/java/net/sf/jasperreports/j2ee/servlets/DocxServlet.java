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
package net.sf.jasperreports.j2ee.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.util.FileBufferedOutputStream;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;


/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class DocxServlet extends BaseHttpServlet
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;


	@Override
	public void service(
		HttpServletRequest request,
		HttpServletResponse response
		) throws IOException, ServletException
	{
		List<JasperPrint> jasperPrintList = BaseHttpServlet.getJasperPrintList(request);

		if (jasperPrintList == null)
		{
			throw new ServletException("No JasperPrint documents found on the HTTP session.");
		}
		
		Boolean isBuffered = Boolean.valueOf(request.getParameter(BaseHttpServlet.BUFFERED_OUTPUT_REQUEST_PARAMETER));
		if (isBuffered)
		{
			FileBufferedOutputStream fbos = new FileBufferedOutputStream();
			JRDocxExporter exporter = new JRDocxExporter(DefaultJasperReportsContext.getInstance());
			exporter.setExporterInput(SimpleExporterInput.getInstance(jasperPrintList));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(fbos));
			
			try 
			{
				exporter.exportReport();
				fbos.close();

				if (fbos.size() > 0)
				{
					response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
					response.setHeader("Content-Disposition", "inline; filename=\"file.docx\"");
					response.setContentLength(fbos.size());
					ServletOutputStream outputStream = response.getOutputStream();
	
					try
					{
						fbos.writeData(outputStream);
						fbos.dispose();
						outputStream.flush();
					}
					finally
					{
						if (outputStream != null)
						{
							try
							{
								outputStream.close();
							}
							catch (IOException ex)
							{
							}
						}
					}
				}
			} 
			catch (JRException e) 
			{
				throw new ServletException(e);
			}
			finally
			{
				fbos.close();
				fbos.dispose();
			}
		}
		else
		{
			response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
			response.setHeader("Content-Disposition", "inline; filename=\"file.docx\"");
			
			JRDocxExporter exporter = new JRDocxExporter(DefaultJasperReportsContext.getInstance());
			exporter.setExporterInput(SimpleExporterInput.getInstance(jasperPrintList));
			
			OutputStream outputStream = response.getOutputStream();
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

			try 
			{
				exporter.exportReport();
			} 
			catch (JRException e) 
			{
				throw new ServletException(e);
			}
			finally
			{
				if (outputStream != null)
				{
					try
					{
						outputStream.close();
					}
					catch (IOException ex)
					{
					}
				}
			}
		}
	}


}

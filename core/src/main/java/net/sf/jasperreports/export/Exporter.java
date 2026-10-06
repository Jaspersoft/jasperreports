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
package net.sf.jasperreports.export;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.ReportContext;


/**
 * All document exporting in JasperReports is done through this interface. There is an implementation of
 * this interface for every document format that JasperReports supports at the moment.
 * <p>
 * Export customization is realized by setting an ExporterConfiguration instance on the exporter and/or provide
 * an instance of the same ExporterConfiguration interface for each item to be exported in batch mode using 
 * {@link ExporterInputItem#getConfiguration()}.
 * <p>
 * An important aspect is the output type of each exporter. There are three types of exporters depending on
 * the type of output they produce:
 * <ul>
 * <li>exporters that export to text or character based file formats (HTML, RTF, CSV, TXT, XML exporters);
 * <li>exporters that export to binary file formats (PDF and XLS exporters)
 * <li>exporters that export directly to graphic devices (Graphics2D and Java Print Service exporters)
 * </ul>
 *<p>
 * All existing exporters fall into two categories depending on the way the content of the documents they
 * produce could be structured:
 * <ul>
 * <li> there are the exporters which target document formats which support free-form page content. These
 * are the Grapchis2D, PDF, RTF and XML exporters.
 * <li> the second category of exporters groups those exporters that target document formats which only
 * support relative positioning of elements on a page or a grid-based layout. In this category we have the
 * HTML, XLS and CSV exporters.
 * </ul>
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface Exporter<I extends ExporterInput, IC extends ReportExportConfiguration, C extends ExporterConfiguration, O extends ExporterOutput>
{


	/**
	 * Provides the input for this exporter.
	 */
	public void setExporterInput(I input);


	/**
	 * Provides the output for this exporter.
	 */
	public void setExporterOutput(O output);


	/**
	 * 
	 */
	public void setConfiguration(IC configuration);


	/**
	 * 
	 */
	public void setConfiguration(C configuration);


	/**
	 *
	 */
	public void setReportContext(ReportContext reportContext);

	
	/**
	 *
	 */
	public ReportContext getReportContext();

	
	/**
	 * Performs the export.
	 */
	public void exportReport() throws JRException;
	

}

/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2025 Cloud Software Group, Inc. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
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
package net.sf.jasperreports.pdf;

import com.lowagie.text.pdf.PdfWriter;

import net.sf.jasperreports.engine.export.JRExporterContext;
import net.sf.jasperreports.pdf.common.PdfProducer;


/**
 * A context that represents information about an PDF export process.
 * 
 * @see JRPdfExporter
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public interface JRPdfExporterContext extends JRExporterContext
{

	/**
	 * Returns the {@link PdfWriter} instance used by the exporter.
	 * 
	 * @return the exporter's {@link PdfWriter} instance
	 */
	PdfWriter getPdfWriter();
	
	PdfProducer getPdfProducer();
	
}

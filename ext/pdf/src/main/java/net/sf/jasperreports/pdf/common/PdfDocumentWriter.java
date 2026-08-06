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
package net.sf.jasperreports.pdf.common;

import java.io.IOException;
import java.io.InputStream;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.pdf.PdfExporterConfiguration;
import net.sf.jasperreports.pdf.type.PdfPrintScalingEnum;
import net.sf.jasperreports.pdf.type.PdfVersionEnum;
import net.sf.jasperreports.pdf.type.PdfaConformanceEnum;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public interface PdfDocumentWriter
{

	void setPdfVersion(PdfVersionEnum pdfVersion);

	void setMinimalPdfVersion(PdfVersionEnum minimalVersion);

	void setFullCompression();
	
	void setEncryption(PdfExporterConfiguration configuration) throws JRException;

	void setPrintScaling(PdfPrintScalingEnum printScaling);

	void setNoSpaceCharRatio();

	void setTabOrderStructure();

	void setLanguage(String language);

	void setPdfaConformance(PdfaConformanceEnum pdfaConformance);

	void createXmpMetadata(String title, String subject, String keywords, boolean isTagged);

	void setRgbTransparencyBlending(boolean rgbTransparencyBlending);

	void setIccProfilePath(String iccProfilePath, InputStream iccIs) throws IOException;

	void addJavaScript(String pdfJavaScript);

	void setDisplayMetadataTitle();

}

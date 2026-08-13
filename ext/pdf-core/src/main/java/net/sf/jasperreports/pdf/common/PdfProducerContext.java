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

import java.awt.color.ColorSpace;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.Locale;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.PrintPageFormat;
import net.sf.jasperreports.engine.fonts.FontUtil;
import net.sf.jasperreports.engine.util.JRStyledTextUtil;
import net.sf.jasperreports.pdf.JRPdfExporter;
import net.sf.jasperreports.pdf.type.PdfVersionEnum;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public interface PdfProducerContext
{
	
	JRPdfExporter getExporter();
	
	JasperReportsContext getJasperReportsContext();
	
	JRPropertiesUtil getProperties();

	FontUtil getFontUtil();

	JRStyledTextUtil getStyledTextUtil();

	boolean isTagged();

	void setMinimalVersion(PdfVersionEnum version);

	JasperPrint getCurrentJasperPrint();
	
	void setFont(Map<Attribute,Object> attributes, Locale locale, boolean setFontLines,
			FontRecipient recipient);
	
	JRException handleDocumentException(Exception e);
	
	ColorSpace getCMYKColorSpace();
	
	int getOffsetX();
	
	int getOffsetY();
	
	PrintPageFormat getCurrentPageFormat();

}

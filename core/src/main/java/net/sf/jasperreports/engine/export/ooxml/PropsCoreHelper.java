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
package net.sf.jasperreports.engine.export.ooxml;

import java.io.Writer;

import net.sf.jasperreports.engine.JasperReportsContext;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class PropsCoreHelper extends BaseHelper
{
	public static final String PROPERTY_TITLE = "dc:title";
	public static final String PROPERTY_SUBJECT = "dc:subject";
	public static final String PROPERTY_CREATOR = "dc:creator";
	public static final String PROPERTY_KEYWORDS = "cp:keywords";
	
	/**
	 * 
	 */
	public PropsCoreHelper(JasperReportsContext jasperReportsContext, Writer writer)
	{
		super(jasperReportsContext, writer);
	}

	/**
	 *
	 */
	public void exportHeader()
	{
		write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		write("<coreProperties xmlns=\"http://schemas.openxmlformats.org/package/2006/metadata/core-properties\" " 
				+ "xmlns:cp=\"http://schemas.openxmlformats.org/package/2006/metadata/core-properties\" " 
				+ "xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" " 
				+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
	}
	

	/**
	 * 
	 */
	public void exportProperty(String propName, String propValue)
	{
		write("  <" + propName + ">" + propValue + "</" + propName + ">\n");	
	}
	

	/**
	 *
	 */
	public void exportFooter()
	{
		write("</coreProperties>\n");
	}
}

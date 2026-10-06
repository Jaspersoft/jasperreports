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
public class DocxContentTypesHelper extends BaseHelper
{
	/**
	 * 
	 */
	public DocxContentTypesHelper(JasperReportsContext jasperReportsContext, Writer writer)
	{
		super(jasperReportsContext, writer);
	}

	/**
	 *
	 */
	public void exportHeader()
	{
		write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">\n"
				+ "  <Default Extension=\"gif\" ContentType=\"image/gif\"/>\n"
				+ "  <Default Extension=\"jpeg\" ContentType=\"image/jpeg\"/>\n"
				+ "  <Default Extension=\"jpg\" ContentType=\"image/jpeg\"/>\n"
				+ "  <Default Extension=\"png\" ContentType=\"image/png\"/>\n"
				+ "  <Default Extension=\"tiff\" ContentType=\"image/tiff\"/>\n"
				+ "  <Default Extension=\"webp\" ContentType=\"image/webp\"/>\n"
				+ "  <Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>\n"
				+ "  <Default Extension=\"xml\" ContentType=\"application/xml\"/>\n"
				+ "  <Default Extension=\"ttf\" ContentType=\"application/x-font-ttf\"/>\n"
				+ "  <Default Extension=\"otf\" ContentType=\"application/x-font-ttf\"/>\n"
				+ "  <Default Extension=\"eot\" ContentType=\"application/x-font-ttf\"/>\n"
				+ "  <Override PartName=\"/word/document.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml\"/>\n"
				+ "  <Override PartName=\"/word/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml\"/>\n"
				+ "  <Override PartName=\"/word/fontTable.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.fontTable+xml\"/>\n"
				+ "  <Override PartName=\"/word/settings.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.settings+xml\"/>\n"
				+ "  <Override PartName=\"/docProps/app.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.extended-properties+xml\"/>\n"
				+ "  <Override PartName=\"/docProps/core.xml\" ContentType=\"application/vnd.openxmlformats-package.core-properties+xml\"/>\n"
				+ "");
	}
	

	/**
	 * 
	 */
	public void exportHeader(int index)
	{
		write("  <Override PartName=\"/word/header" + index + ".xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.header+xml\"/>\n");
	}
	

	/**
	 *
	 */
	public void exportFooter()
	{
		write("</Types>\n");
	}

}

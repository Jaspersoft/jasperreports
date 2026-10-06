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
public class PptxContentTypesHelper extends BaseHelper
{
	/**
	 * 
	 */
	public PptxContentTypesHelper(JasperReportsContext jasperReportsContext, Writer writer)
	{
		super(jasperReportsContext, writer);
	}

	/**
	 *
	 */
	public void exportHeader()
	{
		write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		write("<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">\n");
		write("  <Default Extension=\"gif\" ContentType=\"image/gif\"/>\n");
		write("  <Default Extension=\"jpeg\" ContentType=\"image/jpeg\"/>\n");
		write("  <Default Extension=\"jpg\" ContentType=\"image/jpeg\"/>\n");
		write("  <Default Extension=\"png\" ContentType=\"image/png\"/>\n");
		write("  <Default Extension=\"tiff\" ContentType=\"image/tiff\"/>\n");
		write("  <Default Extension=\"webp\" ContentType=\"image/webp\"/>\n");
		write("  <Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>\n");
		write("  <Default Extension=\"xml\" ContentType=\"application/xml\"/>\n");
		write("  <Default Extension=\"ttf\" ContentType=\"application/x-font-ttf\"/>\n");
		write("  <Default Extension=\"otf\" ContentType=\"application/x-font-ttf\"/>\n");
		write("  <Default Extension=\"eot\" ContentType=\"application/x-fontdata\"/>\n");
		write("  <Override PartName=\"/ppt/theme/theme1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.theme+xml\"/>\n");
		write("  <Override PartName=\"/ppt/tableStyles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.presentationml.tableStyles+xml\"/>\n");
		write("  <Override PartName=\"/ppt/slideMasters/slideMaster1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.presentationml.slideMaster+xml\"/>\n");
		write("  <Override PartName=\"/ppt/slideLayouts/slideLayout1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.presentationml.slideLayout+xml\"/>\n");
		write("  <Override PartName=\"/ppt/presentation.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.presentationml.presentation.main+xml\"/>\n");
		write("  <Override PartName=\"/docProps/app.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.extended-properties+xml\"/>\n");
		write("  <Override PartName=\"/docProps/core.xml\" ContentType=\"application/vnd.openxmlformats-package.core-properties+xml\"/>\n");
	}
	

	/**
	 * 
	 */
	public void exportSlide(int index)
	{
		write("  <Override PartName=\"/ppt/slides/slide" + index + ".xml\" ContentType=\"application/vnd.openxmlformats-officedocument.presentationml.slide+xml\"/>\n");
	}
	

	/**
	 *
	 */
	public void exportFooter()
	{
		write("</Types>\n");
	}

}

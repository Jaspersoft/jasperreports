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
package net.sf.jasperreports.engine.export.ooxml;

import java.io.Writer;

import net.sf.jasperreports.engine.JasperReportsContext;


/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class DocxRelsHelper extends BaseHelper
{

	/**
	 * 
	 */
	public DocxRelsHelper(JasperReportsContext jasperReportsContext, Writer writer)
	{
		super(jasperReportsContext, writer);
	}

	/**
	 * 
	 */
	public void exportHeader()
	{
		write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		write("<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">\n");
		write(" <Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/>\n");
		write(" <Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/settings\" Target=\"settings.xml\"/>\n");
		write(" <Relationship Id=\"rId3\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/fontTable\" Target=\"fontTable.xml\"/>\n");
	}
	
	/**
	 * 
	 */
	public void exportImage(String imageName)
	{
		write(" <Relationship Id=\"" + imageName + "\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/image\" Target=\"media/" + imageName + "\"/>\n");
	}
	
	/**
	 * 
	 */
	public void exportImageLink(String imageId, String url, String targetMode)
	{
		if (url != null)
		{
			write(" <Relationship Id=\"" + imageId + "\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/hyperlink\" Target=\"" + url + "\"" + targetMode + "/>\n");
		}
	}
	
//	/**
//	 * 
//	 */
//	public void exportHyperlink(String id, String href)
//	{
//		write(" <Relationship Id=\"" + id + "\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/hyperlink\" Target=\"" + href + "\"/>\n");
//	}
	
	/**
	 * 
	 */
	public void exportHeader(int headerIndex)
	{
		write(" <Relationship Id=\"header" + headerIndex + "\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/header\" Target=\"header" + headerIndex + ".xml\"/>\n");
	}
	
	/**
	 * 
	 */
	public void exportFooter()
	{
		write("</Relationships>\n");
	}
	
}

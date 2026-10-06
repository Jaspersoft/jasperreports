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
 * @author Sanda Zaharia(shertage@users.sourceforge.net)
 */
public class DocxFontTableHelper extends BaseHelper
{
	/**
	 * 
	 */
	public DocxFontTableHelper(JasperReportsContext jasperReportsContext, Writer writer)
	{
		super(jasperReportsContext, writer);
	}

	public void exportHeader()
	{
		write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		write("<w:fonts\n");
		write(" xmlns:mc=\"http://schemas.openxmlformats.org/markup-compatibility/2006\" \n" + 
				" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" \n" + 
				" xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" \n" + 
				" xmlns:w14=\"http://schemas.microsoft.com/office/word/2010/wordml\" \n" + 
				" xmlns:w15=\"http://schemas.microsoft.com/office/word/2012/wordml\" \n" + 
				" xmlns:w16cid=\"http://schemas.microsoft.com/office/word/2016/wordml/cid\" \n" + 
				" xmlns:w16se=\"http://schemas.microsoft.com/office/word/2015/wordml/symex\" \n" + 
				" mc:Ignorable=\"w14 w15 w16se w16cid\">\n"); 
	}
	
	public void exportFooter()
	{
		write(" </w:fonts>\n");
	}
}

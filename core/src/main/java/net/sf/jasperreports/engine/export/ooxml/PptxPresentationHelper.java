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

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.export.LengthUtil;
import net.sf.jasperreports.engine.util.FileBufferedWriter;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class PptxPresentationHelper extends BaseHelper
{
	private FileBufferedWriter fontsWriter;
	private int dpi;
	
	/**
	 * 
	 */
	public PptxPresentationHelper(JasperReportsContext jasperReportsContext, Writer writer, FileBufferedWriter fontsWriter, int dpi)
	{
		super(jasperReportsContext, writer);
		this.fontsWriter = fontsWriter;
		this.dpi = dpi;
	}

	/**
	 *
	 */
	public void exportHeader(boolean isEmbedFonts)
	{
		write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
		write("<p:presentation\n");
		if (isEmbedFonts)
		{
			write(" embedTrueTypeFonts=\"1\"\n"); 
		}
		write(" xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\"\n"); 
		write(" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"\n"); 
		write(" xmlns:p=\"http://schemas.openxmlformats.org/presentationml/2006/main\">\n");
		write("<p:sldMasterIdLst><p:sldMasterId id=\"2147483648\" r:id=\"rIdSm\"/></p:sldMasterIdLst>\n");
		write("<p:sldIdLst>\n");
	}
	

	/**
	 * 
	 */
	public void exportSlide(int index)
	{
		write("<p:sldId id=\"256" + index + "\" r:id=\"rId" + index + "\"/>\n");
	}
	

	/**
	 *
	 */
	public void exportFooter(JasperPrint jasperPrint)
	{
		write("</p:sldIdLst>\n");
		write("<p:sldSz cx=\"" + LengthUtil.emu(jasperPrint.getPageWidth(), dpi) + "\" cy=\"" + LengthUtil.emu(jasperPrint.getPageHeight(), dpi) + "\" type=\"custom\"/>\n");//FIXMEPART pptx does not work in batch mode
		write("<p:notesSz cx=\"6858000\" cy=\"9144000\"/>\n");
		write("<p:embeddedFontLst>\n");
		fontsWriter.writeData(writer);
		write("</p:embeddedFontLst>\n");
		write("</p:presentation>\n");
	}
}

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
import net.sf.jasperreports.engine.base.JRBasePrintText;
import net.sf.jasperreports.engine.export.LengthUtil;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class DocxSettingsHelper extends BaseHelper
{
	
	/**
	 * 
	 */
	private int dpi;

	/**
	 *
	 */
	public DocxSettingsHelper(JasperReportsContext jasperReportsContext, Writer writer, int dpi)
	{
		super(jasperReportsContext, writer);
		this.dpi = dpi;
	}

	/**
	 * 
	 */
	public void export(JasperPrint jasperPrint, boolean isEmbedFonts)
	{
		write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
		write("<w:settings\n");
		write(" xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">\n"); 
		if (isEmbedFonts)
		{
			write("  <w:embedTrueTypeFonts w:val=\"true\" />\n"); 
		}
		write("  <w:defaultTabStop w:val=\"" 
			+ LengthUtil.twip(new JRBasePrintText(jasperPrint.getDefaultStyleProvider()).getParagraph().getTabStopWidth(), dpi)
			+ "\"/>\n");
		write("</w:settings>");
	}
	
}

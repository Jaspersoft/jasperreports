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
package net.sf.jasperreports.engine.export.oasis;

import java.awt.Color;
import java.io.IOException;

import net.sf.jasperreports.engine.export.LengthUtil;
import net.sf.jasperreports.engine.util.JRColorUtil;


/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class TableStyle extends Style
{
	/**
	 *
	 */
	private int width;
	private int pageFormatIndex;
	private boolean isFrame;
	private boolean isPageBreak;
	private Color tabColor;
	private int reportDpi;

	/**
	 *
	 */
	public TableStyle(WriterHelper styleWriter, int width, int pageFormatIndex, boolean isFrame, boolean isPageBreak, Color tabColor, int reportDpi)
	{
		super(styleWriter);
		this.width = width;
		this.pageFormatIndex = pageFormatIndex;
		this.isFrame = isFrame;
		this.isPageBreak = isPageBreak;
		this.tabColor = tabColor;
		this.reportDpi = reportDpi;
	}
	
	@Override
	public String getId()
	{
		return "" + width + "|" + pageFormatIndex + "|" + isFrame + "|" + isPageBreak + "|" + tabColor; 
	}

	@Override
	public void write(String tableStyleName) throws IOException
	{
		styleWriter.write(" <style:style style:name=\"" + tableStyleName + "\"");
		if (!isFrame)
		{
			styleWriter.write(" style:master-page-name=\"master_" + pageFormatIndex +"\"");
		}
		styleWriter.write(" style:family=\"table\">\n");
		styleWriter.write("   <style:table-properties");		
		styleWriter.write(" table:align=\"left\" style:width=\"" + LengthUtil.inchFloor4Dec(width, reportDpi) + "in\"");
		if (isPageBreak)
		{
			styleWriter.write(" fo:break-before=\"page\"");
		}
		if (tabColor != null)
		{
			styleWriter.write(" tableooo:tab-color=\"#" + JRColorUtil.getColorHexa(tabColor) + "\"");
		}
//		FIXMEODT
//		if (tableWidth != null)
//		{
//			styleWriter.write(" style:width=\""+ tableWidth +"in\"");
//		}
//		if (align != null)
//		{
//			styleWriter.write(" table:align=\""+ align +"\"");
//		}
//		if (margin != null)
//		{
//			styleWriter.write(" fo:margin=\""+ margin +"\"");
//		}
//		if (backGroundColor != null)
//		{
//			styleWriter.write(" fo:background-color=\""+ backGroundColor +"\"");
//		}
		styleWriter.write("/>\n");
		styleWriter.write(" </style:style>\n");
		styleWriter.flush();
	}
}


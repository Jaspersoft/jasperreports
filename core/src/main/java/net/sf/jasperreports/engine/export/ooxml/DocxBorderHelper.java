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

import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.export.LengthUtil;
import net.sf.jasperreports.engine.util.JRColorUtil;


/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class DocxBorderHelper extends BaseHelper
{

	/**
	 *
	 */
	private int dpi;

	/**
	 *
	 */
	public DocxBorderHelper(JasperReportsContext jasperReportsContext, Writer writer, int dpi)
	{
		super(jasperReportsContext, writer);
		this.dpi = dpi;
	}
	
	/**
	 *
	 */
	public void exportBorder(JRLineBox box)
	{
		if (box != null)
		{
			exportBorder(new DocxBorderInfo(box, dpi));
		}
	}

	/**
	 *
	 */
	private void exportBorder(DocxBorderInfo info)
	{
		if (info.hasBorder())
		{
			write("      <w:tcBorders>\n");
			exportBorder(info, DocxBorderInfo.TOP_BORDER);
			exportBorder(info, DocxBorderInfo.LEFT_BORDER);
			exportBorder(info, DocxBorderInfo.BOTTOM_BORDER);
			exportBorder(info, DocxBorderInfo.RIGHT_BORDER);
			write("      </w:tcBorders>\n");
		}
	}

	/**
	 *
	 */
	public void exportPadding(JRLineBox box)
	{
		write("      <w:tcMar>\n");
		exportPadding(box.getTopPadding(), DocxBorderInfo.TOP_BORDER);
		exportPadding(box.getLeftPadding(), DocxBorderInfo.LEFT_BORDER);
		exportPadding(box.getBottomPadding(), DocxBorderInfo.BOTTOM_BORDER);
		exportPadding(box.getRightPadding(), DocxBorderInfo.RIGHT_BORDER);
		write("      </w:tcMar>\n");
	}

	/**
	 *
	 */
	private void exportBorder(DocxBorderInfo info, int side)
	{
		if (info.borderWidth[side] != null)
		{
			write("<w:" + DocxBorderInfo.BORDER[side] +" w:val=\"" + info.borderStyle[side] + "\" w:sz=\"" + info.borderWidth[side] + "\" w:space=\"0\"");
			if (info.borderColor[side] != null)//FIXMEDOCX check this; use default color?
			{
				write(" w:color=\"" + JRColorUtil.getColorHexa(info.borderColor[side]) + "\"");
			}
			write(" />\n");
		}
	}
	
	/**
	 *
	 */
	private void exportPadding(Integer padding, int side)
	{
		if (padding != null)
		{
			write("       <w:" + DocxBorderInfo.BORDER[side] +" w:w=\"" + LengthUtil.twip(padding, dpi) + "\" w:type=\"dxa\" />\n");
		}
	}

}

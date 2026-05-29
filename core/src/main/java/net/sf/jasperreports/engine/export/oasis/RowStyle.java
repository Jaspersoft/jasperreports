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

import java.io.IOException;

import net.sf.jasperreports.engine.export.LengthUtil;


/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class RowStyle extends Style
{
	/**
	 *
	 */
	private int rowHeight;
	private int reportDpi;

	/**
	 *
	 */
	public RowStyle(WriterHelper styleWriter, int rowHeight, int reportDpi)
	{
		super(styleWriter);
		this.rowHeight = rowHeight;
		this.reportDpi = reportDpi;
	}
	
	@Override
	public String getId()
	{
		return "" + rowHeight;
	}

	@Override
	public void write(String rowStyleName) throws IOException
	{
		styleWriter.write(" <style:style style:name=\"" + rowStyleName + "\"");
		styleWriter.write(" style:family=\"table-row\">\n");
		styleWriter.write("   <style:table-row-properties");		
		if(rowHeight < 0)
		{
			styleWriter.write(" style:use-optimal-row-height=\"true\"");
		}
		else
		{
			styleWriter.write(" style:use-optimal-row-height=\"false\"");
			styleWriter.write(" style:row-height=\"" + LengthUtil.inchFloor4Dec(rowHeight, reportDpi) + "in\"");
		}
		styleWriter.write("/>\n");
		styleWriter.write(" </style:style>\n");
		styleWriter.flush();
	}
}


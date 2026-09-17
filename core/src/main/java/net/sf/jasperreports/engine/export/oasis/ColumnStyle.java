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
public class ColumnStyle extends Style
{
	/**
	 *
	 */
	private int columnWidth;
	private int reportDpi;

	/**
	 *
	 */
	public ColumnStyle(WriterHelper styleWriter, int columnWidth, int reportDpi)
	{
		super(styleWriter);
		this.columnWidth = columnWidth;
		this.reportDpi = reportDpi;
	}
	
	@Override
	public String getId()
	{
		return "" + columnWidth;
	}

	@Override
	public void write(String columnStyleName) throws IOException
	{
		styleWriter.write(" <style:style style:name=\"" + columnStyleName + "\"");
		styleWriter.write(" style:family=\"table-column\">\n");
		styleWriter.write("   <style:table-column-properties");		
		styleWriter.write(" style:column-width=\"" + LengthUtil.inchFloor4Dec(columnWidth, reportDpi) + "in\"");
		styleWriter.write("/>\n");
		styleWriter.write(" </style:style>\n");
		styleWriter.flush();
	}
}


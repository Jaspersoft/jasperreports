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
package net.sf.jasperreports.engine.export;

import java.awt.Color;

import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.util.ObjectUtils;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class GridCellStyle
{
	
	private final Color backcolor;
	private final Color forecolor;
	private final JRLineBox box;
	
	public GridCellStyle(Color backcolor, Color forecolor, JRLineBox box)
	{
		this.backcolor = backcolor;
		this.forecolor = forecolor;
		this.box = box;
	}

	@Override
	public int hashCode()
	{
		return (backcolor == null ? 0 : (17 * backcolor.hashCode())) 
				^ (forecolor == null ? 0 : (37 * forecolor.hashCode())) 
				^ (box == null ? 0 : (67 * box.hashCode()));
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof GridCellStyle))
		{
			return false;
		}
		
		GridCellStyle style = (GridCellStyle) obj;
		return ObjectUtils.equals(backcolor, style.backcolor)
				&& ObjectUtils.equals(forecolor, style.forecolor)
				// not using identical() for box because it doesn't compare getDefaultLineColor()
				&& ObjectUtils.equals(box, style.box);
	}

	@Override
	public String toString()
	{
		// JRLineBox doesn't have toString, using Object.toString anyway
		return "{" + backcolor + ", " + forecolor + ", " + box + "}";
	}

	public Color getBackcolor()
	{
		return backcolor;
	}

	public Color getForecolor()
	{
		return forecolor;
	}

	public JRLineBox getBox()
	{
		return box;
	}
	
}

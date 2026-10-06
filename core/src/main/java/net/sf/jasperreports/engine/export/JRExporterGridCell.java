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
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.type.ModeEnum;
	
	
/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class JRExporterGridCell
{
	/**
	 *
	 */
	public static final byte TYPE_EMPTY_CELL = 1; 
	public static final byte TYPE_OCCUPIED_CELL = 2; 
	public static final byte TYPE_ELEMENT_CELL = 3; 


	
	private GridCellStyle style;

	/**
	 *
	 */
	public JRExporterGridCell()
	{
	}

	protected JRExporterGridCell(GridCellStyle style)
	{
		this.style = style;
	}

	public abstract GridCellSize getSize();
	
	public int getWidth()
	{
		return getSize().getWidth();
	}

	public int getHeight()
	{
		return getSize().getHeight();
	}

	public int getColSpan()
	{
		return getSize().getColSpan();
	}

	public int getRowSpan()
	{
		return getSize().getRowSpan();
	}

	public GridCellStyle getStyle()
	{
		return style;
	}

	public void setStyle(GridCellStyle style)
	{
		this.style = style;
	}

	public Color getBackcolor()//FIXMENOW who uses this and why?
	{
		return style == null ? null : style.getBackcolor();
	}


	/**
	 *
	 */
	public JRLineBox getBox()
	{
		return style == null ? null : style.getBox();
	}

	public void setBox(JRLineBox box)
	{
		// should not happen
		throw new UnsupportedOperationException("Cannot set the box on a cell of type " + getType());
	}


	public Color getForecolor()
	{
		return style == null ? null : style.getForecolor();
	}

	public Color getCellBackcolor()
	{
		Color color;
		JRPrintElement element = getElement();
		if (element != null && element.getMode() == ModeEnum.OPAQUE)
		{
			color = element.getBackcolor();
		}
		else
		{
			color = getBackcolor();
		}
		return color;
	}

	public abstract byte getType();
	
	public abstract JRPrintElement getElement();
	
	public abstract String getElementAddress();

	public abstract String getProperty(String propName);
}

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

import java.awt.Color;

import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.export.LengthUtil;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class DocxBorderInfo
{
	/**
	 *
	 */
	protected static final String[] BORDER = new String[]{"top", "left", "bottom", "right"};
	protected static final int TOP_BORDER = 0;
	protected static final int LEFT_BORDER = 1;
	protected static final int BOTTOM_BORDER = 2;
	protected static final int RIGHT_BORDER = 3;
	
	protected Color[] borderColor = new Color[4];
	protected String[] borderWidth = new String[4];
	protected String[] borderStyle = new String[4];
	protected int dpi;

	/**
	 *
	 */
	public DocxBorderInfo(JRLineBox box, int dpi)
	{
		this.dpi = dpi;
		setBorder(box.getTopPen(), TOP_BORDER);
		setBorder(box.getLeftPen(), LEFT_BORDER);
		setBorder(box.getBottomPen(), BOTTOM_BORDER);
		setBorder(box.getRightPen(), RIGHT_BORDER);
	}
	
	/**
	 *
	 */
	public DocxBorderInfo(JRPen pen, int dpi)
	{
		this.dpi = dpi;
		if (
			borderWidth[TOP_BORDER] == null
			&& borderWidth[LEFT_BORDER] == null
			&& borderWidth[BOTTOM_BORDER] == null
			&& borderWidth[RIGHT_BORDER] == null
			)
		{
			setBorder(pen, TOP_BORDER);
			setBorder(pen, LEFT_BORDER);
			setBorder(pen, BOTTOM_BORDER);
			setBorder(pen, RIGHT_BORDER);
		}
	}

	/**
	 *
	 */
	protected boolean hasBorder() 
	{
		return	
			borderWidth[TOP_BORDER] != null
			|| borderWidth[LEFT_BORDER] != null
			|| borderWidth[BOTTOM_BORDER] != null
			|| borderWidth[RIGHT_BORDER] != null;
	}

	/**
	 *
	 */
	private void setBorder(JRPen pen, int side)
	{
		float width = pen.getLineWidth() == null ? 0 : pen.getLineWidth();
		String style = null;

		if (width > 0f)
		{
			switch (pen.getLineStyle())
			{
				case DOTTED :
				{
					style = "dotted";
					break;
				}
				case DASHED :
				{
					style = "dashSmallGap";
					break;
				}
				case DOUBLE :
				{
					style = "double";
					break;
				}
				case SOLID :
				default :
				{
					style = "single";
					break;
				}
			}

			borderWidth[side] = String.valueOf(LengthUtil.halfPoint(width, dpi));
		}
		else
		{
			style = "none";
		}

		borderStyle[side] = style;
		borderColor[side] = pen.getLineColor();
	}

}

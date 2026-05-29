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

import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.export.LengthUtil;
import net.sf.jasperreports.engine.util.JRColorUtil;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class BorderStyle extends Style
{
	/**
	 *
	 */
	private static final String[] border = new String[]{"top", "left", "bottom", "right"};
	protected static final int TOP_BORDER = 0;
	protected static final int LEFT_BORDER = 1;
	protected static final int BOTTOM_BORDER = 2;
	protected static final int RIGHT_BORDER = 3;
	
	private String[] borderColor = new String[4];
	private String[] borderWidth = new String[4];
	private String[] borderStyle = new String[4];
	private String[] borderPadding = new String[4];
	protected int reportDpi;

	/**
	 *
	 */
	public BorderStyle(WriterHelper styleWriter, int reportDpi)
	{
		super(styleWriter);
		this.reportDpi = reportDpi;
	}
	
	/**
	 *
	 */
	public void setBox(JRLineBox box)
	{
		if (box != null)
		{
			appendBorder(box.getTopPen(), TOP_BORDER);
			borderPadding[TOP_BORDER] = String.valueOf(LengthUtil.inchFloor4Dec(box.getTopPadding(), reportDpi));
			appendBorder(box.getLeftPen(), LEFT_BORDER);
			borderPadding[LEFT_BORDER] = String.valueOf(LengthUtil.inchFloor4Dec(box.getLeftPadding(), reportDpi));
			appendBorder(box.getBottomPen(), BOTTOM_BORDER);
			borderPadding[BOTTOM_BORDER] = String.valueOf(LengthUtil.inchFloor4Dec(box.getBottomPadding(), reportDpi));
			appendBorder(box.getRightPen(), RIGHT_BORDER);
			borderPadding[RIGHT_BORDER] = String.valueOf(LengthUtil.inchFloor4Dec(box.getRightPadding(), reportDpi));
		}
	}

	/**
	 *
	 */
	public void setPen(JRPen pen)
	{
		if (
			borderWidth[TOP_BORDER] == null
			&& borderWidth[LEFT_BORDER] == null
			&& borderWidth[BOTTOM_BORDER] == null
			&& borderWidth[RIGHT_BORDER] == null
			)
		{
			appendBorder(pen, TOP_BORDER);
			appendBorder(pen, LEFT_BORDER);
			appendBorder(pen, BOTTOM_BORDER);
			appendBorder(pen, RIGHT_BORDER);
		}
	}

	@Override
	public String getId()
	{
		return 
			borderWidth[TOP_BORDER] + "|" + borderColor[TOP_BORDER] + "|" + borderStyle[TOP_BORDER] + "|" + borderPadding[TOP_BORDER]
			+ "|" + borderWidth[LEFT_BORDER] + "|" + borderColor[LEFT_BORDER] + "|" + borderStyle[LEFT_BORDER] + "|" + borderPadding[LEFT_BORDER]
			+ "|" + borderWidth[BOTTOM_BORDER] + "|" + borderColor[BOTTOM_BORDER] + "|" + borderStyle[BOTTOM_BORDER] + "|" + borderPadding[BOTTOM_BORDER]
			+ "|" + borderWidth[RIGHT_BORDER] + "|" + borderColor[RIGHT_BORDER] + "|" + borderStyle[RIGHT_BORDER] + "|" + borderPadding[RIGHT_BORDER]; 
	}

	/**
	 *
	 */
	protected void writeBorder(int side)
	{
		if (borderWidth[side] != null)
		{
			styleWriter.write(" fo:border-");
			styleWriter.write(border[side]);
			styleWriter.write("=\"");
			styleWriter.write(borderWidth[side]);
			styleWriter.write("in ");
			styleWriter.write(borderStyle[side]); 
			styleWriter.write(" #");
			styleWriter.write(borderColor[side]);
			styleWriter.write("\"");
		}

		if (borderPadding[side] != null)
		{
			styleWriter.write(" fo:padding-");
			styleWriter.write(border[side]);
			styleWriter.write("=\"");
			styleWriter.write(borderPadding[side]);
			styleWriter.write("in\"");
		}
	}

	/**
	 *
	 */
	private void appendBorder(JRPen pen, int side)
	{
		double width = pen.getLineWidth();
		String style = null;

		if (width > 0f)
		{
			switch (pen.getLineStyle())//FIXMEBORDER is this working? deal with double border too.
			{
				case DOTTED :
				{
					style = "dotted";
					break;
				}
				case DASHED :
				{
					style = "dashed";
					break;
				}
				case SOLID :
				default :
				{
					style = "solid";
					break;
				}
			}

			borderWidth[side] = String.valueOf(LengthUtil.inchFloor4Dec(width, reportDpi));
		}
		else
		{
			style = "none";
		}

		borderStyle[side] = style;
		borderColor[side] = JRColorUtil.getColorHexa(pen.getLineColor());
	}

}


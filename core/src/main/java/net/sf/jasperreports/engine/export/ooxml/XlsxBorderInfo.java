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
import net.sf.jasperreports.engine.type.LineDirectionEnum;
import net.sf.jasperreports.engine.util.ObjectUtils;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class XlsxBorderInfo
{
	/**
	 *
	 */
	protected static final String TOP_BORDER = "top";
	protected static final String LEFT_BORDER = "left";
	protected static final String BOTTOM_BORDER = "bottom";
	protected static final String RIGHT_BORDER = "right";
	protected static final String DIAGONAL_BORDER = "diagonal";
	
	protected Color topBorderColor;
	protected Color leftBorderColor;
	protected Color bottomBorderColor;
	protected Color rightBorderColor;
	protected Color diagonalBorderColor;
	protected XlsxBorderStyle topBorderStyle;
	protected XlsxBorderStyle leftBorderStyle;
	protected XlsxBorderStyle bottomBorderStyle;
	protected XlsxBorderStyle rightBorderStyle;
	protected XlsxBorderStyle diagonalBorderStyle;
	protected LineDirectionEnum direction;

	/**
	 *
	 */
	public XlsxBorderInfo(JRLineBox box)
	{
		setTopBorder(box.getTopPen());
		setLeftBorder(box.getLeftPen());
		setBottomBorder(box.getBottomPen());
		setRightBorder(box.getRightPen());
	}
	
	/**
	 *
	 */
	public XlsxBorderInfo(JRLineBox box, LineDirectionEnum direction)
	{
		if (direction != null)
		{
			setDiagonalBorder(box.getPen());
		}
		else
		{
			setTopBorder(box.getTopPen());
			setLeftBorder(box.getLeftPen());
			setBottomBorder(box.getBottomPen());
			setRightBorder(box.getRightPen());
		}
		this.direction = direction;
	}
	
	/**
	 *
	 */
	public XlsxBorderInfo(JRPen pen)
	{
		setTopBorder(pen);
		setLeftBorder(pen);
		setBottomBorder(pen);
		setRightBorder(pen);
	}

	@Override
	public int hashCode()
	{
		int hash = 47 + ObjectUtils.hashCode(topBorderStyle);
		hash = 29 * hash + ObjectUtils.hashCode(topBorderColor);
		hash = 29 * hash + ObjectUtils.hashCode(leftBorderStyle);
		hash = 29 * hash + ObjectUtils.hashCode(leftBorderColor);
		hash = 29 * hash + ObjectUtils.hashCode(bottomBorderStyle);
		hash = 29 * hash + ObjectUtils.hashCode(bottomBorderColor);
		hash = 29 * hash + ObjectUtils.hashCode(rightBorderStyle);
		hash = 29 * hash + ObjectUtils.hashCode(rightBorderColor);
		hash = 29 * hash + ObjectUtils.hashCode(diagonalBorderStyle);
		hash = 29 * hash + ObjectUtils.hashCode(diagonalBorderColor);
		hash = 29 * hash + ObjectUtils.hashCode(direction);
		return hash;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof XlsxBorderInfo))
		{
			return false;
		}

		XlsxBorderInfo info = (XlsxBorderInfo) obj;
		return topBorderStyle == info.topBorderStyle
				&& ObjectUtils.equals(topBorderColor, info.topBorderColor)
				&& leftBorderStyle == info.leftBorderStyle
				&& ObjectUtils.equals(leftBorderColor, info.leftBorderColor)
				&& bottomBorderStyle == info.bottomBorderStyle
				&& ObjectUtils.equals(bottomBorderColor, info.bottomBorderColor)
				&& rightBorderStyle == info.rightBorderStyle
				&& ObjectUtils.equals(rightBorderColor, info.rightBorderColor)
				&& diagonalBorderStyle == info.diagonalBorderStyle
				&& ObjectUtils.equals(diagonalBorderColor, info.diagonalBorderColor)
				&& direction == info.direction;				
	}
	
	public LineDirectionEnum getDirection() 
	{
		return this.direction;
	}

	private void setTopBorder(JRPen pen)
	{
		topBorderStyle = borderStyle(pen);
		topBorderColor = pen.getLineColor();
	}

	private void setLeftBorder(JRPen pen)
	{
		leftBorderStyle = borderStyle(pen);
		leftBorderColor = pen.getLineColor();
	}

	private void setBottomBorder(JRPen pen)
	{
		bottomBorderStyle = borderStyle(pen);
		bottomBorderColor = pen.getLineColor();
	}

	private void setRightBorder(JRPen pen)
	{
		rightBorderStyle = borderStyle(pen);
		rightBorderColor = pen.getLineColor();
	}

	private void setDiagonalBorder(JRPen pen)
	{
		diagonalBorderStyle = borderStyle(pen);
		diagonalBorderColor = pen.getLineColor();
	}

	/**
	 *
	 */
	private static XlsxBorderStyle borderStyle(JRPen pen)
	{
		float width = pen.getLineWidth() == null ? 0 : pen.getLineWidth();
		XlsxBorderStyle style = null;

		if (width > 0f)
		{
			switch (pen.getLineStyle())
			{
				case DOUBLE :
				{
					style = XlsxBorderStyle.DOUBLE;
					break;
				}
				case DOTTED :
				{
					style = XlsxBorderStyle.DOTTED;
					break;
				}
				case DASHED :
				{
					if (width >= 1f)
					{
						style = XlsxBorderStyle.MEDIUM_DASHED;
					}
					else
					{
						style = XlsxBorderStyle.DASHED;
					}
					break;
				}
				case SOLID :
				default :
				{
					if (width >= 2f)
					{
						style = XlsxBorderStyle.THICK;
					}
					else if (width >= 1f)
					{
						style = XlsxBorderStyle.MEDIUM;
					}
					else if (width >= 0.5f)
					{
						style = XlsxBorderStyle.THIN;
					}
					else
					{
						style = XlsxBorderStyle.HAIR;
					}
					break;
				}
			}
		}

		return style;
	}

}

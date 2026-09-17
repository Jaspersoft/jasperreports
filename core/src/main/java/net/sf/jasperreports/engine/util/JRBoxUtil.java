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
package net.sf.jasperreports.engine.util;

import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.base.JRBoxPen;
import net.sf.jasperreports.engine.type.RotationEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class JRBoxUtil
{

	/**
	 * 
	 */
	public static JRLineBox copyBordersNoPadding(JRLineBox box, boolean keepLeft, boolean keepRight, boolean keepTop, boolean keepBottom, JRLineBox complementaryBox)
	{
		JRLineBox clone = box.clone(box.getBoxContainer());
		
		clone.setTopPadding(0);
		clone.setLeftPadding(0);
		clone.setBottomPadding(0);
		clone.setRightPadding(0);
		
		if (!keepLeft || box.getLeftPen().getLineWidth() == null || box.getLeftPen().getLineWidth() <= 0f)
		{
			if (complementaryBox != null)
			{
				clone.getLeftPen().setLineWidth(getBoxLineWidth(complementaryBox.getLeftPen()));
				clone.getLeftPen().setLineColor(complementaryBox.getLeftPen().getLineColor());
				clone.getLeftPen().setLineStyle(complementaryBox.getLeftPen().getLineStyle());
			}
			else
			{
				clone.getLeftPen().setLineWidth(0f);
			}
		}
		
		if (!keepRight || box.getRightPen().getLineWidth() == null || box.getRightPen().getLineWidth() <= 0f)
		{
			if (complementaryBox != null)
			{
				clone.getRightPen().setLineWidth(getBoxLineWidth(complementaryBox.getRightPen()));
				clone.getRightPen().setLineColor(complementaryBox.getRightPen().getLineColor());
				clone.getRightPen().setLineStyle(complementaryBox.getRightPen().getLineStyle());
			}
			else
			{
				clone.getRightPen().setLineWidth(0f);
			}
		}
		
		if (!keepTop || box.getTopPen().getLineWidth() == null || box.getTopPen().getLineWidth() <= 0f)
		{
			if (complementaryBox != null)
			{
				clone.getTopPen().setLineWidth(getBoxLineWidth(complementaryBox.getTopPen()));
				clone.getTopPen().setLineColor(complementaryBox.getTopPen().getLineColor());
				clone.getTopPen().setLineStyle(complementaryBox.getTopPen().getLineStyle());
			}
			else
			{
				clone.getTopPen().setLineWidth(0f);
			}
		}
		
		if (!keepBottom || box.getBottomPen().getLineWidth() == null || box.getBottomPen().getLineWidth() <= 0f)
		{
			if (complementaryBox != null)
			{
				clone.getBottomPen().setLineWidth(getBoxLineWidth(complementaryBox.getBottomPen()));
				clone.getBottomPen().setLineColor(complementaryBox.getBottomPen().getLineColor());
				clone.getBottomPen().setLineStyle(complementaryBox.getBottomPen().getLineStyle());
			}
			else
			{
				clone.getBottomPen().setLineWidth(0f);
			}
		}
		
		return clone;
	}

	
	/**
	 * 
	 */
	public static void reset(JRLineBox box, boolean resetLeft, boolean resetRight, boolean resetTop, boolean resetBottom)
	{
		if (resetLeft)
		{
			box.getLeftPen().setLineWidth(0f);
		}
		
		if (resetRight)
		{
			box.getRightPen().setLineWidth(0f);
		}

		if (resetTop)
		{
			box.getTopPen().setLineWidth(0f);
		}
		
		if (resetBottom)
		{
			box.getBottomPen().setLineWidth(0f);
		}
	}
	

	/**
	 * 
	 */
	public static void copy(JRLineBox source, JRLineBox dest)
	{
		dest.setLeftPadding(source.getOwnLeftPadding());
		dest.copyLeftPen(source.getLeftPen());
		dest.setRightPadding(source.getOwnRightPadding());
		dest.copyRightPen(source.getRightPen());
		dest.setTopPadding(source.getOwnTopPadding());
		dest.copyTopPen(source.getTopPen());
		dest.setBottomPadding(source.getOwnBottomPadding());
		dest.copyBottomPen(source.getBottomPen());
		dest.setPadding(source.getOwnPadding());
		dest.copyPen(source.getPen());
	}
	

	/**
	 * 
	 */
	public static void rotate(JRLineBox box, RotationEnum rotation)
	{
		switch (rotation)
		{
			case LEFT : 
			{
				JRBoxPen topPen = box.getTopPen();
				Integer topPadding = box.getTopPadding();
				
				box.copyTopPen(box.getLeftPen());
				box.setTopPadding(box.getLeftPadding());

				box.copyLeftPen(box.getBottomPen());
				box.setLeftPadding(box.getBottomPadding());
				
				box.copyBottomPen(box.getRightPen());
				box.setBottomPadding(box.getRightPadding());
				
				box.copyRightPen(topPen);
				box.setRightPadding(topPadding);

				break;
			}
			case RIGHT : 
			{
				JRBoxPen topPen = box.getTopPen();
				Integer topPadding = box.getTopPadding();
				
				box.copyTopPen(box.getRightPen());
				box.setTopPadding(box.getRightPadding());

				box.copyRightPen(box.getBottomPen());
				box.setRightPadding(box.getBottomPadding());

				box.copyBottomPen(box.getLeftPen());
				box.setBottomPadding(box.getLeftPadding());

				box.copyLeftPen(topPen);
				box.setLeftPadding(topPadding);
				
				break;
			}
			case UPSIDE_DOWN : 
			{
				JRBoxPen topPen = box.getTopPen();
				Integer topPadding = box.getTopPadding();
				
				box.copyTopPen(box.getBottomPen());
				box.setTopPadding(box.getBottomPadding());

				box.copyBottomPen(topPen);
				box.setBottomPadding(topPadding);
				
				JRBoxPen leftPen = box.getLeftPen();
				Integer leftPadding = box.getLeftPadding();
				
				box.copyLeftPen(box.getRightPen());
				box.setLeftPadding(box.getRightPadding());

				box.copyRightPen(leftPen);
				box.setRightPadding(leftPadding);

				break;
			}
			case NONE :
			default :
			{
			}
		}
	}
	

	public static void eraseBox(JRLineBox box)
	{
		box.setBottomPadding(0);
		box.setTopPadding(0);
		box.setLeftPadding(0);
		box.setRightPadding(0);
		box.getBottomPen().setLineWidth(0f);
		box.getTopPen().setLineWidth(0f);
		box.getLeftPen().setLineWidth(0f);
		box.getRightPen().setLineWidth(0f);
	}
	
	
	public static boolean hasOwnValues(JRLineBox box)
	{
		return 
			box.getOwnPadding() != null
			|| JRPenUtil.hasOwnValues(box.getPen())
			|| box.getOwnTopPadding() != null
			|| JRPenUtil.hasOwnValues(box.getTopPen())
			|| box.getOwnLeftPadding() != null
			|| JRPenUtil.hasOwnValues(box.getLeftPen())
			|| box.getOwnBottomPadding() != null
			|| JRPenUtil.hasOwnValues(box.getBottomPen())
			|| box.getOwnRightPadding() != null
			|| JRPenUtil.hasOwnValues(box.getRightPen());
	}
	
	
	private static float getBoxLineWidth(JRPen pen)
	{
		Float width = pen.getLineWidth();
		return width == null ? 0f : width;
	}

	private JRBoxUtil()
	{
	}
}

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

import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.base.JRBoxPen;
import net.sf.jasperreports.engine.export.JRExporterGridCell;
import net.sf.jasperreports.engine.export.LengthUtil;
import net.sf.jasperreports.engine.export.OccupiedGridCell;
import net.sf.jasperreports.engine.type.LineStyleEnum;
import net.sf.jasperreports.engine.util.JRColorUtil;


/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class PptxBorderHelper extends BaseHelper
{

	/**
	 *
	 */
	private int dpi;

	/**
	 *
	 */
	public PptxBorderHelper(JasperReportsContext jasperReportsContext, Writer writer, int dpi)
	{
		super(jasperReportsContext, writer);
		this.dpi = dpi;
	}
	
	/**
	 * 
	 */
	protected void exportBorder(
		JRExporterGridCell gridCell,
		JRExporterGridCell topGridCell,
		JRExporterGridCell leftGridCell,
		JRExporterGridCell rightGridCell,
		JRExporterGridCell bottomGridCell
		)
	{
		JRBoxPen pen = null;
		
		if (gridCell != null && gridCell.getBox() != null)
		{
			pen = gridCell.getBox().getLeftPen();
		}
		if ((pen == null || pen.getLineWidth() == null || pen.getLineWidth() == 0) && leftGridCell != null) // ideally, here we should test for null, but the grid cell box always resolves to non null value through style resolver
		{
			if (leftGridCell.getType() == JRExporterGridCell.TYPE_OCCUPIED_CELL)
			{
				OccupiedGridCell occupiedGridCell = (OccupiedGridCell)leftGridCell;
				leftGridCell = occupiedGridCell.getOccupier();
			}
			if (leftGridCell.getBox() != null)
			{
				pen = leftGridCell.getBox().getRightPen(); // reinforce border from adjacent cell as this is how pptx tables work; otherwise noFill border from table style would have priority
			}
		}
		if (pen != null && pen.getLineWidth() != null && pen.getLineWidth() > 0) // ideally, here we should test for null, but the grid cell box always resolves to non null value through style resolver
		{
			exportBorder("lnL", pen);
		}
		
		pen = null;
		if (gridCell != null && gridCell.getBox() != null)
		{
			pen = gridCell.getBox().getRightPen();
		}
		if ((pen == null || pen.getLineWidth() == null || pen.getLineWidth() == 0) && rightGridCell != null)
		{
			if (rightGridCell.getType() == JRExporterGridCell.TYPE_OCCUPIED_CELL)
			{
				OccupiedGridCell occupiedGridCell = (OccupiedGridCell)rightGridCell;
				rightGridCell = occupiedGridCell.getOccupier();
			}
			if (rightGridCell.getBox() != null)
			{
				pen = rightGridCell.getBox().getLeftPen();
			}
		}
		if (pen != null && pen.getLineWidth() != null && pen.getLineWidth() > 0)
		{
			exportBorder("lnR", pen);
		}
		
		pen = null;
		if (gridCell != null && gridCell.getBox() != null)
		{
			pen = gridCell.getBox().getTopPen();
		}
		if ((pen == null || pen.getLineWidth() == null || pen.getLineWidth() == 0) && topGridCell != null)
		{
			if (topGridCell.getType() == JRExporterGridCell.TYPE_OCCUPIED_CELL)
			{
				OccupiedGridCell occupiedGridCell = (OccupiedGridCell)topGridCell;
				topGridCell = occupiedGridCell.getOccupier();
			}
			if (topGridCell.getBox() != null)
			{
				pen = topGridCell.getBox().getBottomPen();
			}
		}
		if (pen != null && pen.getLineWidth() != null && pen.getLineWidth() > 0)
		{
			exportBorder("lnT", pen);
		}
		
		pen = null;
		if (gridCell != null && gridCell.getBox() != null)
		{
			pen = gridCell.getBox().getBottomPen();
		}
		if ((pen == null || pen.getLineWidth() == null || pen.getLineWidth() == 0) && bottomGridCell != null)
		{
			if (bottomGridCell.getType() == JRExporterGridCell.TYPE_OCCUPIED_CELL)
			{
				OccupiedGridCell occupiedGridCell = (OccupiedGridCell)bottomGridCell;
				bottomGridCell = occupiedGridCell.getOccupier();
			}
			if (bottomGridCell.getBox() != null)
			{
				pen = bottomGridCell.getBox().getTopPen();
			}
		}
		if (pen != null && pen.getLineWidth() != null && pen.getLineWidth() > 0)
		{
			exportBorder("lnB", pen);
		}
	}
	
	/**
	 * 
	 */
	protected void exportBorder(String side, JRBoxPen pen)
	{
		write("<a:" + side + " w=\"" + LengthUtil.emu(pen.getLineWidth(), dpi) + "\">\n");
		write("  <a:solidFill>\n");
		write("    <a:srgbClr val=\"" + JRColorUtil.getColorHexa(pen.getLineColor()) + "\"/>\n");
		write("  </a:solidFill>\n");
		write("  <a:prstDash val=\"" + getLineStyle(pen.getLineStyle()) + "\"/>\n");
		write("</a:" + side + ">\n");
	}
	
	/**
	 * 
	 */
	protected String getLineStyle(LineStyleEnum lineStyleEnum)
	{
		String lineStyle = null;
		switch(lineStyleEnum)
		{
			case DASHED :
			{
				lineStyle = "dash";
				break;
			}
			case DOTTED :
			{
				lineStyle = "dot";
				break;
			}
			case SOLID :
			default :
			{
				lineStyle = "solid";
				break;
			}
		}
		return lineStyle;
	}

}

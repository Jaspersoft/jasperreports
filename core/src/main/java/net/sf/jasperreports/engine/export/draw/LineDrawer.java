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

/*
 * Contributors:
 * Eugene D - eugenedruy@users.sourceforge.net 
 * Adrian Jackson - iapetus@users.sourceforge.net
 * David Taylor - exodussystems@users.sourceforge.net
 * Lars Kristensen - llk@users.sourceforge.net
 */
package net.sf.jasperreports.engine.export.draw;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;

import net.sf.jasperreports.engine.JRPrintLine;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.type.LineDirectionEnum;
import net.sf.jasperreports.engine.type.LineStyleEnum;
import net.sf.jasperreports.engine.util.JRPenUtil;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class LineDrawer extends ElementDrawer<JRPrintLine>
{
	private final int reportDpi;

	/**
	 *
	 */
	public LineDrawer(JasperReportsContext jasperReportsContext, int reportDpi)
	{
		super(jasperReportsContext);
		this.reportDpi = reportDpi;
	}
	
	
	@Override
	public void draw(Graphics2D grx, JRPrintLine line, int offsetX, int offsetY)
	{
		grx.setColor(line.getLinePen().getLineColor());
		
		Stroke stroke = JRPenUtil.getStroke(line.getLinePen(), BasicStroke.CAP_BUTT, reportDpi);

		if (stroke != null)
		{
			grx.setStroke(stroke);
			
			float lineWidth = JRPenUtil.getLineWidthOrDefault(line.getLinePen(), reportDpi);
			float dpiScale = (float) reportDpi / JasperPrint.DEFAULT_REPORT_DPI;
			int dpiOne = Math.max(1, (int) dpiScale);

			if (line.getWidth() <= dpiOne)
			{
				if (line.getHeight() > dpiOne)
				{
					//Vertical line
					if (line.getLinePen().getLineStyle() ==LineStyleEnum.DOUBLE)
					{
						grx.translate(0.5 * dpiScale - lineWidth / 3, 0);
						grx.drawLine(
							line.getX() + offsetX,
							line.getY() + offsetY,
							line.getX() + offsetX,
							line.getY() + offsetY + line.getHeight()
							);
						grx.translate(2 * lineWidth / 3, 0);
						grx.drawLine(
							line.getX() + offsetX,
							line.getY() + offsetY,
							line.getX() + offsetX,
							line.getY() + offsetY + line.getHeight()
							);
						grx.translate(-0.5 * dpiScale - lineWidth / 3, 0);
					}
					else
					{
						grx.translate(0.5 * dpiScale, 0);
						grx.drawLine(
							line.getX() + offsetX,
							line.getY() + offsetY,
							line.getX() + offsetX,
							line.getY() + offsetY + line.getHeight()
							);
						grx.translate(-0.5 * dpiScale, 0);
					}
				}
			}
			else
			{
				if (line.getHeight() <= dpiOne)
				{
					//Horizontal line
					if (line.getLinePen().getLineStyle() == LineStyleEnum.DOUBLE)
					{
						grx.translate(0, 0.5 * dpiScale - lineWidth / 3);
						grx.drawLine(
							line.getX() + offsetX,
							line.getY() + offsetY,
							line.getX() + offsetX + line.getWidth(),
							line.getY() + offsetY
							);
						grx.translate(0, 2 * lineWidth / 3);
						grx.drawLine(
							line.getX() + offsetX,
							line.getY() + offsetY,
							line.getX() + offsetX + line.getWidth(),
							line.getY() + offsetY
							);
						grx.translate(0, -0.5 * dpiScale - lineWidth / 3);
					}
					else
					{
						grx.translate(0, 0.5 * dpiScale);
						grx.drawLine(
							line.getX() + offsetX,
							line.getY() + offsetY,
							line.getX() + offsetX + line.getWidth(),
							line.getY() + offsetY
							);
						grx.translate(0, -0.5 * dpiScale);
					}
				}
				else
				{
					//Oblique line
					if (line.getDirection() != LineDirectionEnum.BOTTOM_UP)
					{
						if (line.getLinePen().getLineStyle() == LineStyleEnum.DOUBLE)
						{
							double xtrans = lineWidth / (3 * Math.sqrt(1 + Math.pow(line.getWidth(), 2) / Math.pow(line.getHeight(), 2))); 
							double ytrans = lineWidth / (3 * Math.sqrt(1 + Math.pow(line.getHeight(), 2) / Math.pow(line.getWidth(), 2))); 
							grx.translate(xtrans, -ytrans);
							grx.drawLine(
								line.getX() + offsetX, 
								line.getY() + offsetY,
								line.getX() + offsetX + line.getWidth(),  
								line.getY() + offsetY + line.getHeight()
								);
							grx.translate(-2 * xtrans, 2 * ytrans);
							grx.drawLine(
								line.getX() + offsetX, 
								line.getY() + offsetY,
								line.getX() + offsetX + line.getWidth(),  
								line.getY() + offsetY + line.getHeight()
								);
							grx.translate(xtrans, -ytrans);
						}
						else
						{
							grx.drawLine(
								line.getX() + offsetX, 
								line.getY() + offsetY,
								line.getX() + offsetX + line.getWidth(),  
								line.getY() + offsetY + line.getHeight()
								);
						}
					}
					else
					{
						if (line.getLinePen().getLineStyle() == LineStyleEnum.DOUBLE)
						{
							double xtrans = lineWidth / (3 * Math.sqrt(1 + Math.pow(line.getWidth(), 2) / Math.pow(line.getHeight(), 2))); 
							double ytrans = lineWidth / (3 * Math.sqrt(1 + Math.pow(line.getHeight(), 2) / Math.pow(line.getWidth(), 2))); 
							grx.translate(-xtrans, -ytrans);
							grx.drawLine(
								line.getX() + offsetX, 
								line.getY() + offsetY + line.getHeight(),
								line.getX() + offsetX + line.getWidth(),  
								line.getY() + offsetY
								);
							grx.translate(2 * xtrans, 2 * ytrans);
							grx.drawLine(
								line.getX() + offsetX, 
								line.getY() + offsetY + line.getHeight(),
								line.getX() + offsetX + line.getWidth(),  
								line.getY() + offsetY
								);
							grx.translate(-xtrans, -ytrans);
						}
						else
						{
							grx.drawLine(
								line.getX() + offsetX, 
								line.getY() + offsetY + line.getHeight(),
								line.getX() + offsetX + line.getWidth(),  
								line.getY() + offsetY
								);
						}
					}
				}
			}
		}
	}

}

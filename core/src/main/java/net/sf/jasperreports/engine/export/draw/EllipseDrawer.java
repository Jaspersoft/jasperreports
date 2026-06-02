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
import java.awt.geom.AffineTransform;

import net.sf.jasperreports.engine.JRPrintEllipse;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.type.LineStyleEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.util.JRPenUtil;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class EllipseDrawer extends ElementDrawer<JRPrintEllipse>
{
	private final int reportDpi;

	/**
	 *
	 */
	public EllipseDrawer(JasperReportsContext jasperReportsContext, int reportDpi)
	{
		super(jasperReportsContext);
		this.reportDpi = reportDpi;
	}
	
	
	@Override
	public void draw(Graphics2D grx, JRPrintEllipse ellipse, int offsetX, int offsetY)
	{
		int width = ellipse.getWidth();
		int height = ellipse.getHeight();
		
		if (ellipse.getMode() == ModeEnum.OPAQUE)
		{
			grx.setColor(ellipse.getBackcolor());
			grx.fillOval(
				ellipse.getX() + offsetX, 
				ellipse.getY() + offsetY, 
				width,
				height
				);
		}

		grx.setColor(ellipse.getLinePen().getLineColor());

		Stroke stroke = JRPenUtil.getStroke(ellipse.getLinePen(), BasicStroke.CAP_SQUARE, reportDpi);

		if (stroke != null)
		{
			grx.setStroke(stroke);
			
			AffineTransform oldTx = grx.getTransform();

			if (ellipse.getLinePen().getLineStyle() == LineStyleEnum.DOUBLE)
			{
				float lineWidth = JRPenUtil.getLineWidthOrDefault(ellipse.getLinePen(), reportDpi);
				
				grx.translate(
					ellipse.getX() + offsetX - lineWidth / 3, 
					ellipse.getY() + offsetY - lineWidth / 3
					);
				grx.scale(
					(width + 2 * lineWidth / 3) 
						/ width, 
					(height + 2 * lineWidth / 3) 
						/ height 
					);
				grx.drawOval(
					0, 
					0, 
					width,
					height
					);
				
				grx.setTransform(oldTx);

				grx.translate(
					ellipse.getX() + offsetX + lineWidth / 3, 
					ellipse.getY() + offsetY + lineWidth / 3
					);
				if (width > 2 * lineWidth / 3)
				{
					grx.scale(
						(width - 2 * lineWidth / 3) 
							/ width, 
						1 
						);
				}
				if (height > 2 * lineWidth / 3)
				{
					grx.scale(
						1, 
						(height - 2 * lineWidth / 3) 
							/ height 
						);
				}
				grx.drawOval(
					0, 
					0, 
					width,
					height
					);
			}
			else
			{
				grx.translate(
					ellipse.getX() + offsetX, 
					ellipse.getY() + offsetY
					);
				grx.drawOval(
					0, 
					0, 
					width,
					height
					);
			}

			grx.setTransform(oldTx);
		}
	}


}

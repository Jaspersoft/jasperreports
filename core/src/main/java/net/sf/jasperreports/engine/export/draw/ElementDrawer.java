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

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.type.LineStyleEnum;
import net.sf.jasperreports.engine.util.JRPenUtil;



/**
 * @param <T> the type of the element that the drawer supports
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class ElementDrawer<T extends JRPrintElement>
{
	private final JasperReportsContext jasperReportsContext;

	/**
	 *
	 */
	public ElementDrawer(JasperReportsContext jasperReportsContext)
	{
		this.jasperReportsContext = jasperReportsContext;
	}
	
	
	/**
	 *
	 */
	public JasperReportsContext getJasperReportsContext()
	{
		return jasperReportsContext;
	}

	
	/**
	 *
	 */
	public abstract void draw(Graphics2D grx, T element, int offsetX, int offsetY) throws JRException;
	
	
	/**
	 *
	 */
	protected void drawBox(Graphics2D grx, JRLineBox box, JRPrintElement element, int offsetX, int offsetY)
	{
		drawTopPen(
			grx, 
			box.getTopPen(), 
			box.getLeftPen(), 
			box.getRightPen(), 
			element, 
			offsetX, 
			offsetY
			);
		drawLeftPen(
			grx, 
			box.getTopPen(), 
			box.getLeftPen(), 
			box.getBottomPen(), 
			element, 
			offsetX, 
			offsetY
			);
		drawBottomPen(
			grx, 
			box.getLeftPen(), 
			box.getBottomPen(), 
			box.getRightPen(), 
			element, 
			offsetX, 
			offsetY
			);
		drawRightPen(
			grx, 
			box.getTopPen(), 
			box.getBottomPen(), 
			box.getRightPen(), 
			element, 
			offsetX, 
			offsetY
			);
	}

	
	/**
	 *
	 */
	protected void drawPen(Graphics2D grx, JRPen pen, JRPrintElement element, int offsetX, int offsetY)
	{
		drawTopPen(grx, pen, pen, pen, element, offsetX, offsetY);
		drawLeftPen(grx, pen, pen, pen, element, offsetX, offsetY);
		drawBottomPen(grx, pen, pen, pen, element, offsetX, offsetY);
		drawRightPen(grx, pen, pen, pen, element, offsetX, offsetY);
	}

	
	/**
	 *
	 */
	protected void drawTopPen(
		Graphics2D grx, 
		JRPen topPen, 
		JRPen leftPen, 
		JRPen rightPen, 
		JRPrintElement element, 
		int offsetX, 
		int offsetY
		)
	{
		Stroke topStroke = JRPenUtil.getStroke(topPen, BasicStroke.CAP_BUTT);
		int width = element.getWidth();
		float leftOffset = leftPen.getLineWidth() == null ? 0 : leftPen.getLineWidth() / 2;
		float rightOffset = rightPen.getLineWidth() == null ? 0 : rightPen.getLineWidth() / 2;
		
		if (topStroke != null && width > 0)
		{
			grx.setStroke(topStroke);
			grx.setColor(topPen.getLineColor());
	
			AffineTransform oldTx = grx.getTransform();

			if (topPen.getLineStyle() == LineStyleEnum.DOUBLE)
			{
				float topPenWidth = topPen.getLineWidth() == null ? 0 : topPen.getLineWidth();

				grx.translate(
					element.getX() + offsetX - leftOffset, 
					element.getY() + offsetY - topPenWidth / 3
					);
				grx.scale(
					(width + leftOffset + rightOffset) 
						/ width, 
					1
					);
				grx.drawLine(
					0, 
					0, 
					width,
					0
					);

				grx.setTransform(oldTx);

				grx.translate(
					element.getX() + offsetX + leftOffset / 3, 
					element.getY() + offsetY + topPenWidth / 3
					);
				if(width > (leftOffset + rightOffset) / 3)
				{
					grx.scale(
						(width - (leftOffset + rightOffset) / 3) 
							/ width, 
						1
						);
				}
				grx.drawLine(
					0, 
					0, 
					width,
					0
					);
			}
			else
			{
				grx.translate(
					element.getX() + offsetX - leftOffset, 
					element.getY() + offsetY
					);
				grx.scale(
					(width + leftOffset + rightOffset) 
						/ width, 
					1
					);
				grx.drawLine(
					0, 
					0, 
					width,
					0
					);
			}
			
			grx.setTransform(oldTx);
		}
	}

	
	/**
	 *
	 */
	protected void drawLeftPen(
		Graphics2D grx, 
		JRPen topPen, 
		JRPen leftPen, 
		JRPen bottomPen, 
		JRPrintElement element, 
		int offsetX, 
		int offsetY
		)
	{
		Stroke leftStroke = JRPenUtil.getStroke(leftPen, BasicStroke.CAP_BUTT);
		int height = element.getHeight();
		float topOffset = topPen.getLineWidth() == null ? 0 : topPen.getLineWidth() / 2;
		float bottomOffset = bottomPen.getLineWidth() == null ? 0 : bottomPen.getLineWidth() / 2;
		
		if (leftStroke != null && height > 0)
		{
			grx.setStroke(leftStroke);
			grx.setColor(leftPen.getLineColor());
	
			AffineTransform oldTx = grx.getTransform();

			if (leftPen.getLineStyle() == LineStyleEnum.DOUBLE)
			{
				float leftPenWidth = leftPen.getLineWidth() == null ? 0 : leftPen.getLineWidth();

				grx.translate(
					element.getX() + offsetX - leftPenWidth / 3, 
					element.getY() + offsetY - topOffset
					);
				grx.scale(
						1,
						(height + (topOffset + bottomOffset)) 
							/ height 
						);
				grx.drawLine(
					0, 
					0, 
					0,
					height
					);

				grx.setTransform(oldTx);

				grx.translate(
					element.getX() + offsetX + leftPenWidth / 3, 
					element.getY() + offsetY + topOffset / 3
					);
				if(height > (topOffset + bottomOffset) / 3)
				{
					grx.scale(
						1,
						(height - (topOffset + bottomOffset) / 3) 
							/ height
						);
				}
				grx.drawLine(
					0, 
					0, 
					0,
					height
					);
			}
			else
			{
				grx.translate(
					element.getX() + offsetX, 
					element.getY() + offsetY - topOffset
					);
				grx.scale(
					1,
					(height + topOffset + bottomOffset) 
						/ height
					);
				grx.drawLine(
					0, 
					0, 
					0,
					height
					);
			}

			grx.setTransform(oldTx);
		}
	}

	
	/**
	 *
	 */
	protected void drawBottomPen(
		Graphics2D grx, 
		JRPen leftPen, 
		JRPen bottomPen, 
		JRPen rightPen, 
		JRPrintElement element, 
		int offsetX, 
		int offsetY
		)
	{
		Stroke bottomStroke = JRPenUtil.getStroke(bottomPen, BasicStroke.CAP_BUTT);
		int width = element.getWidth();
		int height = element.getHeight();
		float leftOffset = leftPen.getLineWidth() == null ? 0 : leftPen.getLineWidth() / 2;
		float rightOffset = rightPen.getLineWidth() == null ? 0 : rightPen.getLineWidth() / 2;
		
		if (bottomStroke != null && width > 0)
		{
			grx.setStroke(bottomStroke);
			grx.setColor(bottomPen.getLineColor());
	
			AffineTransform oldTx = grx.getTransform();

			if (bottomPen.getLineStyle() == LineStyleEnum.DOUBLE)
			{
				float bottomPenWidth = bottomPen.getLineWidth() == null ? 0 : bottomPen.getLineWidth();

				grx.translate(
					element.getX() + offsetX - leftOffset, 
					element.getY() + offsetY + height + bottomPenWidth / 3
					);
				grx.scale(
					(width + leftOffset + rightOffset) 
						/ width, 
					1
					);
				grx.drawLine(
					0, 
					0,
					width,
					0
					);

				grx.setTransform(oldTx);

				grx.translate(
					element.getX() + offsetX + leftOffset / 3, 
					element.getY() + offsetY + height - bottomPenWidth / 3
					);
				if(width > (leftOffset + rightOffset) / 3)
				{
					grx.scale(
						(width - (leftOffset + rightOffset) / 3) 
							/ width, 
						1
						);
				}
				grx.drawLine(
					0, 
					0,
					width,
					0
					);
			}
			else
			{
				grx.translate(
					element.getX() + offsetX - leftOffset, 
					element.getY() + offsetY + height
					);
				grx.scale(
					(width + leftOffset + rightOffset) 
						/ width, 
					1
					);
				grx.drawLine(
					0, 
					0,
					width,
					0
					);
			}

			grx.setTransform(oldTx);
		}
	}

	
	/**
	 *
	 */
	protected void drawRightPen(
		Graphics2D grx, 
		JRPen topPen, 
		JRPen bottomPen, 
		JRPen rightPen, 
		JRPrintElement element, 
		int offsetX, 
		int offsetY
		)
	{
		Stroke rightStroke = JRPenUtil.getStroke(rightPen, BasicStroke.CAP_BUTT);
		int height = element.getHeight();
		int width = element.getWidth();
		float topOffset = topPen.getLineWidth() == null ? 0 : topPen.getLineWidth() / 2;
		float bottomOffset = bottomPen.getLineWidth() == null ? 0 : bottomPen.getLineWidth() / 2;
		
		if (rightStroke != null && height > 0)
		{
			grx.setStroke(rightStroke);
			grx.setColor(rightPen.getLineColor());
	
			AffineTransform oldTx = grx.getTransform();

			if (rightPen.getLineStyle() == LineStyleEnum.DOUBLE)
			{
				float rightPenWidth = rightPen.getLineWidth() == null ? 0 : rightPen.getLineWidth();

				grx.translate(
					element.getX() + offsetX + width + rightPenWidth / 3, 
					element.getY() + offsetY - topOffset
					);
				grx.scale(
					1,
					(height + topOffset + bottomOffset) 
						/ height 
					);
				grx.drawLine(
					0,
					0,
					0,
					height
					);

				grx.setTransform(oldTx);

				grx.translate(
					element.getX() + offsetX + width - rightPenWidth / 3, 
					element.getY() + offsetY + topOffset / 3
					);
				if(height > (topOffset + bottomOffset) / 3)
				{
					grx.scale(
						1,
						(height - (topOffset + bottomOffset) / 3) 
							/ height 
						);
				}
				grx.drawLine(
					0,
					0,
					0,
					height
					);
			}
			else
			{
				grx.translate(
					element.getX() + offsetX + width, 
					element.getY() + offsetY - topOffset
					);
				grx.scale(
					1,
					(height + topOffset + bottomOffset) 
						/ height 
					);
				grx.drawLine(
					0,
					0,
					0,
					height
					);
			}

			grx.setTransform(oldTx);
		}
	}
	
}

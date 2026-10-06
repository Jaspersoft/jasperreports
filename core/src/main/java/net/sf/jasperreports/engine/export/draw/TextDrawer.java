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

/*
 * Contributors:
 * Eugene D - eugenedruy@users.sourceforge.net 
 * Adrian Jackson - iapetus@users.sourceforge.net
 * David Taylor - exodussystems@users.sourceforge.net
 * Lars Kristensen - llk@users.sourceforge.net
 */
package net.sf.jasperreports.engine.export.draw;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.export.AwtTextRenderer;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.util.JRStyledText;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class TextDrawer extends ElementDrawer<JRPrintText>
{

	/**
	 *
	 */
	protected AwtTextRenderer textRenderer;

	
	/**
	 *
	 */
	public TextDrawer(
		JasperReportsContext jasperReportsContext,
		AwtTextRenderer textRenderer
		)
	{
		super(jasperReportsContext);
		this.textRenderer = textRenderer;
	}
	
	
	@Override
	public void draw(Graphics2D grx, JRPrintText text, int offsetX, int offsetY)
	{
		textRenderer.initialize(grx, text, offsetX, offsetY);
		
		JRStyledText styledText = textRenderer.getStyledText();
		
		if (styledText == null)
		{
			return;
		}

		double angle = 0;
		
		switch (text.getRotation())
		{
			case LEFT :
			{
				angle = - Math.PI / 2;
				break;
			}
			case RIGHT :
			{
				angle = Math.PI / 2;
				break;
			}
			case UPSIDE_DOWN :
			{
				angle = Math.PI;
				break;
			}
			case NONE :
			default :
			{
			}
		}
		
		Shape oldClip = grx.getClip();

		grx.rotate(angle, textRenderer.getX(), textRenderer.getY());

		if (text.getMode() == ModeEnum.OPAQUE)
		{
			grx.setColor(text.getBackcolor());
			grx.fillRect(textRenderer.getX(), textRenderer.getY(), textRenderer.getWidth(), textRenderer.getHeight()); 
		}
//		else
//		{
//			/*
//			grx.setColor(text.getForecolor());
//			grx.setStroke(new BasicStroke(1));
//			grx.drawRect(x, y, width, height);
//			*/
//		}

		grx.clip(
			new Rectangle(
				textRenderer.getX() + textRenderer.getLeftPadding(),
				textRenderer.getY() + textRenderer.getTopPadding(), 
				textRenderer.getWidth() - textRenderer.getLeftPadding() - textRenderer.getRightPadding(), 
				textRenderer.getHeight() - textRenderer.getTopPadding() - textRenderer.getBottomPadding()
				)
			);

		try
		{
			String allText = textRenderer.getPlainText();
			if (allText.length() > 0)
			{
				grx.setColor(text.getForecolor());

				/*   */
				textRenderer.render();
			}
		}
		finally
		{
			grx.rotate(-angle, textRenderer.getX(), textRenderer.getY());
			grx.setClip(oldClip);
		}
		
		/*   */
		drawBox(grx, text.getLineBox(), text, offsetX, offsetY);
	}

	
}

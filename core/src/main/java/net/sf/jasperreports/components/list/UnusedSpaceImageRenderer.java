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
package net.sf.jasperreports.components.list;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.renderers.AbstractRenderer;
import net.sf.jasperreports.renderers.Graphics2DRenderable;

/**
 * Am image renderer used to mark the unused vertical space in a list
 * component preview.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class UnusedSpaceImageRenderer extends AbstractRenderer implements Graphics2DRenderable
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	// transparent light grey
	private static final Color FILL = new Color(224, 224, 224, 128);
	
	public static final UnusedSpaceImageRenderer INSTANCE = new UnusedSpaceImageRenderer();

	private final Shape clip;
	private final int lineGap = 15;
	private final int lineWidth = 10;
	
	public UnusedSpaceImageRenderer()
	{
		this(null);
	}
	
	public UnusedSpaceImageRenderer(Shape clip)
	{
		this.clip = clip;
	}
	
	@Override
	public void render(
		JasperReportsContext jasperReportsContext,
		Graphics2D grx, 
		Rectangle2D rectangle
		) throws JRException
	{
		Graphics2D graphics = (Graphics2D) grx.create();
		try
		{
			graphics.translate(rectangle.getX(), rectangle.getY());
			graphics.setColor(FILL);
			
			if (clip != null)
			{
				graphics.clip(clip);
			}
			
			int width = (int) rectangle.getWidth();
			int limit = width + (int) rectangle.getHeight();
			int increment = lineGap + lineWidth;
			int reverseOffset = (width - 4 * lineWidth / 3) % increment;
			for (int x = 0; x <= limit; x += increment)
			{
				graphics.fillPolygon(
						new int[]{x, x + lineWidth, 0, 0},
						new int[]{0, 0, x + lineWidth, x},
						4);

				graphics.fillPolygon(
						new int[]{width - x - reverseOffset, width - x - lineWidth - reverseOffset, width, width},
						new int[]{0, 0, x + lineWidth, x},
						4);
			}
		}
		finally
		{
			graphics.dispose();
		}
	}
}

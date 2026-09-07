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
package net.sf.jasperreports.renderers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.type.ImageTypeEnum;
import net.sf.jasperreports.engine.util.JRImageLoader;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class AbstractRenderToImageDataRenderer extends AbstractRenderToImageAwareRenderer implements Graphics2DRenderable, DataRenderable, DimensionRenderable
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public static final String EXCEPTION_MESSAGE_KEY_DIMENSION_MUST_OVERRIDE = "engine.renderable.svg.dimension.must.override";
	public static final String EXCEPTION_MESSAGE_KEY_DIMENSION_NULL_NOT_ALLOWED = "engine.renderable.svg.dimension.null.not.allowed";

	
	@Override
	public Dimension2D getDimension(JasperReportsContext jasperReportsContext) throws JRException
	{
		throw 
			new JRException(
				EXCEPTION_MESSAGE_KEY_DIMENSION_MUST_OVERRIDE, 
				new Object[]{this.getClass().getName()}
				);
	}


	@Override
	public byte[] getData(JasperReportsContext jasperReportsContext) throws JRException
	{
		Dimension2D dimension = getDimension(jasperReportsContext);
		
		if (dimension == null)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_DIMENSION_NULL_NOT_ALLOWED, 
					(Object[])null
					);
		}

		int dpi = getImageDataDPI(jasperReportsContext);
		// the dimension is expressed in report pixels, so the raster is sized from the physical
		// size of the element rather than from a fixed resolution
		double scale = dpi / (double)getReportDpi();
		
		BufferedImage bi =
			new BufferedImage(
				(int) (scale * dimension.getWidth()),
				(int) (scale * dimension.getHeight()),
				BufferedImage.TYPE_INT_ARGB // always produce PNGs with transparency
				);

		Graphics2D g = createGraphics(bi);
		try
		{
			g.scale(scale, scale);
			Color backcolor = getBackcolor();
			if (backcolor != null)
			{
				g.setColor(backcolor);
				g.fillRect(0, 0, (int)dimension.getWidth(), (int)dimension.getHeight());
			}
			render(jasperReportsContext, g, new Rectangle((int)dimension.getWidth(), (int)dimension.getHeight()));
		}
		finally
		{
			g.dispose();
		}
		
		return JRImageLoader.getInstance(jasperReportsContext).loadBytesFromAwtImage(bi, ImageTypeEnum.PNG); // always produce PNGs with transparency
	}


	protected Color getBackcolor()
	{
		return null;
	}
}

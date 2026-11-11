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
package net.sf.jasperreports.barbecue;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.output.OutputException;

/**
 * Barcode drawing logic shared by {@link BarbecueBarcodeRenderer} and {@link BarbecueRendererImpl}.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
final class BarbecueRenderingUtil
{

	private BarbecueRenderingUtil()
	{
	}

	static Dimension2D getDimension(Barcode barcode, RotationEnum rotation)
	{
		if (rotation != null) 
		{
			switch(rotation)
			{
				case LEFT:
				case RIGHT:
					return new Dimension((int)barcode.getSize().getHeight(),(int)barcode.getSize().getWidth());
				default:
					return barcode.getSize();
			}
		} else 
		{
			return barcode.getSize();
		}
	}

	static void render(Barcode barcode, RotationEnum rotation, Graphics2D grx, Rectangle2D rectangle)
	{
		AffineTransform origTransform = grx.getTransform();
		try
		{
			Dimension size = barcode.getSize();

			if (rotation != null)
			{
				switch (rotation)
				{
					case LEFT:
						grx.translate(rectangle.getX(), rectangle.getY() + rectangle.getHeight());
						grx.rotate((-1) * Math.PI / 2);
						if (rectangle.getWidth() != size.getHeight() 
								|| rectangle.getHeight() != size.getWidth())
						{
							grx.scale(rectangle.getHeight() / size.getWidth(), 
									rectangle.getWidth() / size.getHeight());
							
						}
						break;
					case RIGHT: 
						grx.translate(rectangle.getX() + rectangle.getWidth(), rectangle.getY());
						grx.rotate(Math.PI / 2);
						if (rectangle.getWidth() != size.getHeight() 
								|| rectangle.getHeight() != size.getWidth())
						{
							grx.scale(rectangle.getHeight() / size.getWidth(), 
									rectangle.getWidth() / size.getHeight());
							
						}
						break;
					case UPSIDE_DOWN:
						grx.translate(rectangle.getX() + rectangle.getWidth(), rectangle.getY() + rectangle.getHeight());
						grx.rotate(Math.PI);
						if (rectangle.getWidth() != size.getWidth() 
								|| rectangle.getHeight() != size.getHeight())
						{
							grx.scale(rectangle.getWidth() / size.getWidth(), 
									rectangle.getHeight() / size.getHeight());
						}
						break;
					case NONE:
						grx.translate(rectangle.getX(), rectangle.getY());
						if (rectangle.getWidth() != size.getWidth() 
								|| rectangle.getHeight() != size.getHeight())
						{
							grx.scale(rectangle.getWidth() / size.getWidth(), 
									rectangle.getHeight() / size.getHeight());
						}
						break;
				}
			} else
			{
				grx.translate(rectangle.getX(), rectangle.getY());
				if (rectangle.getWidth() != size.getWidth() 
						|| rectangle.getHeight() != size.getHeight())
				{
					grx.scale(rectangle.getWidth() / size.getWidth(), 
							rectangle.getHeight() / size.getHeight());
				}
			}
			
			barcode.draw(grx, 0, 0);
		}
		catch (OutputException e)
		{
			throw new JRRuntimeException(e);
		}
		finally
		{
			grx.setTransform(origTransform);
		}
	}

}

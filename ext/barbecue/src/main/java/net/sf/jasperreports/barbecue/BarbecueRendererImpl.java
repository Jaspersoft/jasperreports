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
package net.sf.jasperreports.barbecue;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.renderers.AbstractRenderer;
import net.sf.jasperreports.renderers.DimensionRenderable;
import net.sf.jasperreports.renderers.Graphics2DRenderable;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.output.OutputException;


/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class BarbecueRendererImpl extends AbstractRenderer implements Graphics2DRenderable, DimensionRenderable
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private Barcode barcode;
	
	private RotationEnum rotation;

	private Font font;

	private transient Dimension barcodeSize;

	public BarbecueRendererImpl(Barcode barcode) 
	{
		this.barcode = barcode;
	}

	/**
	 * Returns the size of the barcode, measured by drawing it on a scratch graphics context.
	 * Barbecue measures the barcode text only when not in headless mode, otherwise the text has no height,
	 * so it falls outside the barcode size and gets clipped when rendering. Drawing the barcode stores
	 * the size of what was actually drawn, text included, regardless of the environment.
	 * Measuring is done here at export time rather than at fill time, because drawing leaves
	 * non serializable encoding state in some barcodes (e.g. Code128), which would prevent saving the filled report.
	 */
	protected Dimension getBarcodeSize()
	{
		if (barcodeSize == null)
		{
			Graphics2D grx = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).createGraphics();
			try
			{
				barcode.draw(grx, 0, 0);
			}
			catch (OutputException e)
			{
				throw new JRRuntimeException(e);
			}
			finally
			{
				grx.dispose();
			}
			barcodeSize = barcode.getSize();
		}
		return barcodeSize;
	}
	
	@Override
	public Dimension2D getDimension(JasperReportsContext jasperReportsContext)
	{
		if (rotation != null) 
		{
			switch (rotation)
			{
				case LEFT:
				case RIGHT:
					return new Dimension((int)getBarcodeSize().getHeight(),(int)getBarcodeSize().getWidth());
				default:
					return getBarcodeSize();
			}
		} else 
		{
			return getBarcodeSize();
		}
	}

	@Override
	public void render(JasperReportsContext jasperReportsContext, Graphics2D grx, Rectangle2D rectangle) 
	{
		AffineTransform origTransform = grx.getTransform();
		try
		{
			Dimension size = getBarcodeSize();

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
			
			if (font == null)
			{
				// renderers deserialized from reports filled before the font was kept here,
				// or created by callers that do not provide the font; the library layout applies
				barcode.draw(grx, 0, 0);
			}
			else
			{
				barcode.output(new BarbecueGraphicsOutput(grx, font, barcode.getForeground(), barcode.getBackground()));
			}
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

	public void setRotation(RotationEnum rotation){
		this.rotation = rotation;
	}

	/**
	 * Sets the font of the barcode text, which needs to be the font set on the barcode.
	 * Barbecue does not expose the font of the barcode, and the font is needed for drawing the barcode text
	 * inside its label area, see {@link BarbecueGraphicsOutput}.
	 */
	public void setFont(Font font)
	{
		this.font = font;
	}
	
}

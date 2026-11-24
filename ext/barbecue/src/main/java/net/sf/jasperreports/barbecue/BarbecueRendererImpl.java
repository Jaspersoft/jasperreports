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
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.renderers.AbstractRenderer;
import net.sf.jasperreports.renderers.DimensionRenderable;
import net.sf.jasperreports.renderers.Graphics2DRenderable;
import net.sourceforge.barbecue.Barcode;


/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @deprecated Replaced by {@link BarbecueBarcodeRenderer}, which does not serialize the barcode component.
 * Deserializing instances of this class requires the classes of the barcode component object graph to be added
 * to the deserialization class whitelist.
 */
@Deprecated
public class BarbecueRendererImpl extends AbstractRenderer implements Graphics2DRenderable, DimensionRenderable
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private Barcode barcode;

	/**
	 * The rotation applied by the renderer itself, which is only set in renderers of reports filled
	 * before the rotation was moved to the image, see {@link #setRotation(RotationEnum)}.
	 */
	private RotationEnum rotation;

	private transient Dimension barcodeSize;

	public BarbecueRendererImpl(Barcode barcode) 
	{
		this.barcode = barcode;
	}

	/**
	 * Returns the size of the barcode, measured by drawing it on a scratch graphics context
	 * (see {@link BarbecueRenderingUtil#measure(Barcode)}).
	 * Measuring is done here at export time rather than at fill time, because drawing leaves
	 * non serializable encoding state in some barcodes (e.g. Code128), which would prevent saving the filled report.
	 */
	private Dimension getBarcodeSize()
	{
		if (barcodeSize == null)
		{
			barcodeSize = BarbecueRenderingUtil.measure(barcode);
		}
		return barcodeSize;
	}
	
	@Override
	public Dimension2D getDimension(JasperReportsContext jasperReportsContext)
	{
		return BarbecueRenderingUtil.getDimension(getBarcodeSize(), rotation);
	}

	@Override
	public void render(JasperReportsContext jasperReportsContext, Graphics2D grx, Rectangle2D rectangle) 
	{
		// no font, so the barcode draws its text itself, as this renderer always did
		BarbecueRenderingUtil.render(barcode, getBarcodeSize(), null, rotation, grx, rectangle);
	}

	/**
	 * Sets a rotation for the renderer to apply when drawing the barcode.
	 * The barbecue component no longer calls this, as it draws the barcode upright and rotates the image instead,
	 * so that rotation and image alignment work as for any other image. The rotation is still applied
	 * when set, as it is in renderers of reports filled and saved before that, whose images are not rotated.
	 */
	public void setRotation(RotationEnum rotation){
		this.rotation = rotation;
	}
	
}

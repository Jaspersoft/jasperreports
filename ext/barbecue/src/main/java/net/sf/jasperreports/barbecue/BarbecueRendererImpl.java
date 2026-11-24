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
	
	private RotationEnum rotation;

	private Font font;

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
		// the font is null for renderers deserialized from reports filled before the font was kept here,
		// or created by callers that do not provide the font; the library layout applies then
		BarbecueRenderingUtil.render(barcode, getBarcodeSize(), font, rotation, grx, rectangle);
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

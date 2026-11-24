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
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.renderers.AbstractRenderer;
import net.sf.jasperreports.renderers.DimensionRenderable;
import net.sf.jasperreports.renderers.Graphics2DRenderable;
import net.sourceforge.barbecue.Barcode;


/**
 * Barbecue barcode renderer.
 * <p>
 * Only the barcode information is serialized, the barcode component is recreated on deserialization.
 * This is because the barbecue barcode component is a Swing component, and deserializing it
 * would require a large object graph of AWT/Swing classes to be included in the deserialization class whitelist.
 * </p>
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class BarbecueBarcodeRenderer extends AbstractRenderer implements Graphics2DRenderable, DimensionRenderable
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private final BarcodeInfo barcodeInfo;
	
	private RotationEnum rotation;
	
	private transient Barcode barcode;
	
	private transient Dimension barcodeSize;

	/**
	 * Creates a renderer for a barcode.
	 * 
	 * @param barcodeInfo the barcode information, which should not be modified after the renderer is created
	 * @throws net.sf.jasperreports.engine.JRRuntimeException if the barcode could not be created
	 */
	public BarbecueBarcodeRenderer(BarcodeInfo barcodeInfo) 
	{
		this.barcodeInfo = barcodeInfo;
		// creating the barcode right away, as before, so that errors raised by barbecue for invalid data
		// (not all invalid data is detected on creation) occur at fill time and not later when the renderer is used
		this.barcode = BarcodeProviders.createBarcode(barcodeInfo);
	}
	
	public BarcodeInfo getBarcodeInfo()
	{
		return barcodeInfo;
	}
	
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
		BarbecueRenderingUtil.render(barcode, getBarcodeSize(), barcodeInfo.getFont(), rotation, grx, rectangle);
	}
	
	public RotationEnum getRotation()
	{
		return rotation;
	}
	
	public void setRotation(RotationEnum rotation)
	{
		this.rotation = rotation;
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		
		if (barcodeInfo == null)
		{
			throw new InvalidObjectException("Missing barcode information");
		}
		
		barcode = BarcodeProviders.createBarcode(barcodeInfo);
	}
	
}

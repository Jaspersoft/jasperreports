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
package net.sf.jasperreports.okapi;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.renderers.AbstractRenderer;
import net.sf.jasperreports.renderers.DimensionRenderable;
import net.sf.jasperreports.renderers.Graphics2DRenderable;
import uk.org.okapibarcode.backend.Symbol;
import uk.org.okapibarcode.output.Java2DRenderer;


/**
 * Draws an Okapi barcode upright, at its natural size in points; the image element
 * that holds it carries the rotation and alignment.
 * <p>
 * The symbol is kept only in memory: after deserialization it is encoded again from
 * the {@link OkapiSymbolSpec}.
 * </p>
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class OkapiRenderer extends AbstractRenderer implements Graphics2DRenderable, DimensionRenderable
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/**
	 * Okapi's {@link Java2DRenderer} casts every coordinate to int, so it draws at this
	 * magnification into a context scaled down by the same factor: each coordinate is then
	 * off by at most a hundredth of a module.
	 */
	private static final int MAGNIFICATION = 100;

	private final OkapiSymbolSpec spec;
	private transient Symbol symbol;

	OkapiRenderer(OkapiSymbolSpec spec, Symbol symbol)
	{
		this.spec = spec;
		this.symbol = symbol;
	}

	OkapiSymbolSpec getSpec()
	{
		return spec;
	}

	protected synchronized Symbol getSymbol(JasperReportsContext jasperReportsContext)
	{
		if (symbol == null)
		{
			symbol = spec.createSymbol(jasperReportsContext);
		}
		return symbol;
	}

	@Override
	public Dimension2D getDimension(JasperReportsContext jasperReportsContext)
	{
		Symbol okapiSymbol = getSymbol(jasperReportsContext);
		return new Size(okapiSymbol.getWidth() * spec.getModulePt(), okapiSymbol.getHeight() * spec.getModulePt());
	}

	@Override
	public void render(JasperReportsContext jasperReportsContext, Graphics2D grx, Rectangle2D rectangle)
	{
		Symbol okapiSymbol = getSymbol(jasperReportsContext);
		double naturalWidth = okapiSymbol.getWidth() * spec.getModulePt();
		double naturalHeight = okapiSymbol.getHeight() * spec.getModulePt();
		Graphics2D graphics = (Graphics2D) grx.create();
		try
		{
			graphics.translate(rectangle.getX(), rectangle.getY());
			graphics.scale(rectangle.getWidth() / naturalWidth, rectangle.getHeight() / naturalHeight);
			graphics.scale(spec.getModulePt() / MAGNIFICATION, spec.getModulePt() / MAGNIFICATION);
			new Java2DRenderer(graphics, MAGNIFICATION, toOkapi(spec.getPaper()), toOkapi(spec.getInk()))
				.render(okapiSymbol);
		}
		finally
		{
			graphics.dispose();
		}
	}

	private static uk.org.okapibarcode.graphics.Color toOkapi(Color color)
	{
		return color == null ? null : new uk.org.okapibarcode.graphics.Color(color.getRed(), color.getGreen(), color.getBlue());
	}

	/**
	 * A double-precision dimension.
	 */
	private static final class Size extends Dimension2D
	{
		private double width;
		private double height;

		Size(double width, double height)
		{
			this.width = width;
			this.height = height;
		}

		@Override
		public double getWidth()
		{
			return width;
		}

		@Override
		public double getHeight()
		{
			return height;
		}

		@Override
		public void setSize(double width, double height)
		{
			this.width = width;
			this.height = height;
		}
	}
}

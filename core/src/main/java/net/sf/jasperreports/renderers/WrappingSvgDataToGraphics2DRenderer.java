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
package net.sf.jasperreports.renderers;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.batik.dom.svg.SVGDocumentFactory;
import org.w3c.dom.svg.SVGDocument;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintImageAreaHyperlink;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;


/**
 * SVG renderer implementation based on <a href="http://xmlgraphics.apache.org/batik/">Batik</a>.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class WrappingSvgDataToGraphics2DRenderer extends AbstractSvgDataToGraphics2DRenderer
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private final DataRenderable dataRenderer;
	private final AreaHyperlinksRenderable areaHyperlinksRenderer;

	/**
	 *
	 */
	public WrappingSvgDataToGraphics2DRenderer(DataRenderable dataRenderer)
	{
		super(null);
		
		this.dataRenderer = dataRenderer;
		this.areaHyperlinksRenderer = dataRenderer instanceof AreaHyperlinksRenderable ? (AreaHyperlinksRenderable)dataRenderer : null;
	}

	@Override
	protected SVGDocument getSvgDocument(
		JasperReportsContext jasperReportsContext,
		SVGDocumentFactory documentFactory
		) throws JRException
	{
		try
		{
			return 
				documentFactory.createSVGDocument(
					null, 
					new ByteArrayInputStream(
						dataRenderer.getData(jasperReportsContext)
						)
					);
		}
		catch (IOException e)
		{
			throw new JRRuntimeException(e);
		}
	}

	@Override
	public List<JRPrintImageAreaHyperlink> getImageAreaHyperlinks(Rectangle2D renderingArea) throws JRException
	{
		return areaHyperlinksRenderer == null ? super.getImageAreaHyperlinks(renderingArea) : areaHyperlinksRenderer.getImageAreaHyperlinks(renderingArea);
	}

	@Override
	public boolean hasImageAreaHyperlinks()
	{
		return areaHyperlinksRenderer == null ? super.hasImageAreaHyperlinks() : areaHyperlinksRenderer.hasImageAreaHyperlinks();
	}

	@Override
	public byte[] getData(JasperReportsContext jasperReportsContext) throws JRException 
	{
		return dataRenderer.getData(jasperReportsContext);
	}

	@Override
	public int getImageDataDPI(JasperReportsContext jasperReportsContext)
	{
		if (dataRenderer instanceof RenderToImageAwareRenderable)
		{
			return ((RenderToImageAwareRenderable) dataRenderer).getImageDataDPI(jasperReportsContext);
		}
		
		return super.getImageDataDPI(jasperReportsContext);
	}

	@Override
	public Graphics2D createGraphics(BufferedImage bi)
	{
		if (dataRenderer instanceof RenderToImageAwareRenderable)
		{
			return ((RenderToImageAwareRenderable) dataRenderer).createGraphics(bi);
		}
		
		return super.createGraphics(bi);
	}
}

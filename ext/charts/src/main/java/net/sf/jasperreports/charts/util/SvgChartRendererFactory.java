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
package net.sf.jasperreports.charts.util;

import java.awt.geom.Rectangle2D;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.jfree.chart.JFreeChart;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import net.sf.jasperreports.engine.JRPrintImageAreaHyperlink;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.renderers.Renderable;
import net.sf.jasperreports.renderers.SimpleRenderToImageAwareDataRenderer;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class SvgChartRendererFactory extends AbstractChartRenderableFactory
{
	
	@Override
	public Renderable getRenderable(
		JasperReportsContext jasperReportsContext,
		JFreeChart chart, 
		ChartHyperlinkProvider chartHyperlinkProvider,
		Rectangle2D rectangle,
		int reportDpi
		)
	{
		double dpiScale = (double)reportDpi / JasperPrint.DEFAULT_REPORT_DPI;
		Rectangle2D chartArea = ChartUtil.toChartArea(rectangle, dpiScale);

		DOMImplementation domImpl = 
			GenericDOMImplementation.getDOMImplementation();
		Document document = 
			domImpl.createDocument(null, "svg", null);
		SVGGraphics2D grx = 
			new SVGGraphics2D(document);
		
		grx.setSVGCanvasSize(rectangle.getBounds().getSize());
		if (dpiScale != 1d)
		{
			grx.scale(dpiScale, dpiScale);
		}

		List<JRPrintImageAreaHyperlink> areaHyperlinks = null;

		if (chartHyperlinkProvider != null && chartHyperlinkProvider.hasHyperlinks())
		{
			areaHyperlinks = ChartUtil.getImageAreaHyperlinks(chart, chartHyperlinkProvider, grx, chartArea, dpiScale);
		}
		else
		{
			chart.draw(grx, chartArea);
		}

		try
		{
			StringWriter swriter = new StringWriter();
			grx.stream(swriter);
			byte[] svgData = swriter.getBuffer().toString().getBytes(StandardCharsets.UTF_8);
			return new SimpleRenderToImageAwareDataRenderer(svgData, areaHyperlinks);
		}
		catch (SVGGraphics2DIOException e)
		{
			throw new JRRuntimeException(e);
		}
	}

}

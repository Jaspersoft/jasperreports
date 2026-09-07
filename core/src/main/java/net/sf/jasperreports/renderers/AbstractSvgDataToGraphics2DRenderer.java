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

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import java.util.List;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.FontFamilyResolver;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.ViewBox;
import org.apache.batik.dom.svg.SVGDocumentFactory;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.parser.DefaultLengthHandler;
import org.apache.batik.parser.LengthHandler;
import org.apache.batik.parser.LengthParser;
import org.apache.batik.parser.ParseException;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGPreserveAspectRatio;
import org.w3c.dom.svg.SVGSVGElement;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintImageAreaHyperlink;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.util.SimpleDimension2D;


/**
 * SVG renderer implementation based on <a href="http://xmlgraphics.apache.org/batik/">Batik</a>.
 *
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public abstract class AbstractSvgDataToGraphics2DRenderer extends AbstractRenderToImageAwareRenderer implements DataRenderable, Graphics2DRenderable, DimensionRenderable, AreaHyperlinksRenderable
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private List<JRPrintImageAreaHyperlink> areaHyperlinks;

	private transient SoftReference<GraphicsNode> rootNodeRef;
	private transient Dimension2D documentSize;

	/**
	 * Creates a SVG renderer.
	 *
	 * @param areaHyperlinks a list of {@link JRPrintImageAreaHyperlink area hyperlinks}
	 */
	protected AbstractSvgDataToGraphics2DRenderer(List<JRPrintImageAreaHyperlink> areaHyperlinks)
	{
		this.areaHyperlinks = areaHyperlinks;
	}

	@Override
	public Graphics2D createGraphics(BufferedImage bi)
	{
		Graphics2D graphics = GraphicsUtil.createGraphics(bi);
		return graphics;
	}

	@Override
	public void render(JasperReportsContext jasperReportsContext, Graphics2D grx, Rectangle2D rectangle) throws JRException
	{
		GraphicsNode rootNode = getRootNode(jasperReportsContext);

		AffineTransform transform =
			ViewBox.getPreserveAspectRatioTransform(
				new float[]{0, 0, (float) documentSize.getWidth(), (float) documentSize.getHeight()},
				SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_NONE, 
				true,
				(float) rectangle.getWidth(), 
				(float) rectangle.getHeight()
				);
		Graphics2D graphics = (Graphics2D) grx.create();
		try
		{
			graphics.translate(rectangle.getX(), rectangle.getY());
			graphics.transform(transform);

			// CompositeGraphicsNode not thread safe
			synchronized (rootNode)
			{
				rootNode.paint(graphics);
			}
		}
		finally
		{
			graphics.dispose();
		}
	}

	@Override
	public Dimension2D getDimension(JasperReportsContext jasperReportsContext)
	{
		try
		{
			getRootNode(jasperReportsContext);
			return documentSize;
		}
		catch (JRException e)
		{
			throw new JRRuntimeException(e);
		}
	}

	protected synchronized GraphicsNode getRootNode(JasperReportsContext jasperReportsContext) throws JRException
	{
		if (rootNodeRef == null || rootNodeRef.get() == null)
		{
			FontFamilyResolver fontFamilyResolver = BatikFontFamilyResolver.getInstance(jasperReportsContext);
			
			UserAgent userAgentForDoc = 
				new BatikUserAgent(
					jasperReportsContext,
					fontFamilyResolver,
					BatikUserAgent.PIXEL_TO_MM_72_DPI,
					null
					);
			
			SVGDocumentFactory documentFactory =
				new SAXSVGDocumentFactory(userAgentForDoc.getXMLParserClassName(), true);
			documentFactory.setValidating(userAgentForDoc.isXMLParserValidating());

			SVGDocument document = getSvgDocument(jasperReportsContext, documentFactory);
			
			Float width = null;
			Float height = null;

			SVGSVGElement rootElement = document.getRootElement();
			if (rootElement != null) //very unlikely for rootElement to be null; but even so, the dimension will be calculated like before
			{
				Rectangle2D viewBox = null;
				
				String viewBoxStr = rootElement.getAttributeNS(null, SVGConstants.SVG_VIEW_BOX_ATTRIBUTE);
				if (viewBoxStr != null && !viewBoxStr.isEmpty()) 
				{
					float[] rect = ViewBox.parseViewBoxAttribute(rootElement, viewBoxStr, null);
					viewBox = new Rectangle2D.Float(rect[0], rect[1], rect[2], rect[3]);
				}

				width = parseLength(rootElement, SVGConstants.SVG_WIDTH_ATTRIBUTE);
				height = parseLength(rootElement, SVGConstants.SVG_HEIGHT_ATTRIBUTE);
				
				if (width == null)
				{
					if (height == null)
					{
						if (viewBox == null)
						{
							width = 300f;
							height = 150f;
						}
						else
						{
							width = (float)viewBox.getWidth();
							height = (float)viewBox.getHeight();
						}
					}
					else
					{
						if (viewBox == null)
						{
							width = 300f;
						}
						else
						{
							width = (float)(height * viewBox.getWidth() / viewBox.getHeight());
						}
					}
				}
				else
				{
					if (height == null)
					{
						if (viewBox == null)
						{
							height = 150f;
						}
						else
						{
							height = (float)(width * viewBox.getHeight() / viewBox.getWidth());
						}
					}
				}
			}
			
			Node svgNode = document.getElementsByTagName("svg").item(0);
			Node svgWidthNode = svgNode.getAttributes().getNamedItem("width");
			Node svgHeightNode = svgNode.getAttributes().getNamedItem("height");
			String strSvgWidth = svgWidthNode == null ? null : svgWidthNode.getNodeValue().trim();
			String strSvgHeight = svgHeightNode == null ? null : svgHeightNode.getNodeValue().trim();
			
			float pixel2mm = BatikUserAgent.PIXEL_TO_MM_72_DPI;
			if (
				(strSvgWidth != null && strSvgWidth.endsWith("mm"))
				|| (strSvgHeight != null && strSvgHeight.endsWith("mm"))
				)
			{
				pixel2mm = BatikUserAgent.PIXEL_TO_MM_96_DPI;
			}
			
			UserAgent userAgentForCtx = 
				new BatikUserAgent(
					jasperReportsContext,
					fontFamilyResolver,
					pixel2mm,
					width == null ? null : new SimpleDimension2D(width, height)
					);
				
			BridgeContext ctx = new BridgeContext(userAgentForCtx);
			ctx.setDynamic(true);
			GVTBuilder builder = new GVTBuilder();
			GraphicsNode rootNode = builder.build(ctx, document);
			rootNodeRef = new SoftReference<>(rootNode);

			//copying the document size object because it has a reference to SVGSVGElementBridge,
			//which prevents rootNodeRef from being cleared by the garbage collector
			Dimension2D svgSize = ctx.getDocumentSize();
			documentSize = new SimpleDimension2D(svgSize.getWidth(), svgSize.getHeight());
		}
		
		return rootNodeRef.get();
	}

	private static Float parseLength(SVGSVGElement el, String attrName) 
	{
		String attrValue = el.getAttributeNS(null, attrName);
		if (attrValue == null || attrValue.isEmpty()) 
		{
			return null;
		}
		float value[] = {0};
		LengthParser lp = new LengthParser();
		LengthHandler lh = new DefaultLengthHandler()
		{
			@Override
			public void lengthValue(float v) throws ParseException
			{
				value[0] = v;
			}
		};
		lp.setLengthHandler(lh);
		lp.parse(attrValue);
		return value[0];
	}

	/**
	 * 
	 */
	protected abstract SVGDocument getSvgDocument(
		JasperReportsContext jasperReportsContext,
		SVGDocumentFactory documentFactory
		) throws JRException;

	
	@Override
	public List<JRPrintImageAreaHyperlink> getImageAreaHyperlinks(Rectangle2D renderingArea) throws JRException
	{
		return areaHyperlinks;
	}

	@Override
	public boolean hasImageAreaHyperlinks()
	{
		return areaHyperlinks != null && !areaHyperlinks.isEmpty();
	}
}

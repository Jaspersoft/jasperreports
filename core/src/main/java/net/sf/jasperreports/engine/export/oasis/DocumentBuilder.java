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

/*
 * Special thanks to Google 'Summer of Code 2005' program for supporting this development
 * 
 * Contributors:
 * Majid Ali Khan - majidkk@users.sourceforge.net
 * Frank Schönheit - Frank.Schoenheit@Sun.COM
 */
package net.sf.jasperreports.engine.export.oasis;

import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintElementIndex;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.export.JRExporterGridCell;
import net.sf.jasperreports.engine.export.JRHyperlinkProducer;
import net.sf.jasperreports.engine.export.zip.FileBufferedZipEntry;
import net.sf.jasperreports.engine.type.ImageTypeEnum;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.engine.util.JRTypeSniffer;
import net.sf.jasperreports.renderers.DataRenderable;
import net.sf.jasperreports.renderers.Renderable;
import net.sf.jasperreports.renderers.RenderersCache;
import net.sf.jasperreports.renderers.WrappingImageDataToGraphics2DRenderer;
import net.sf.jasperreports.renderers.WrappingRenderToImageDataRenderer;
import net.sf.jasperreports.renderers.util.RendererUtil;



/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class DocumentBuilder 
{
	/**
	 *
	 */
	protected static final String JR_PAGE_ANCHOR_PREFIX = "JR_PAGE_ANCHOR_";
	public static final String IMAGE_NAME_PREFIX = "img_";
	protected static final int IMAGE_NAME_PREFIX_LEGTH = IMAGE_NAME_PREFIX.length();
	
	/**
	 *
	 */
	protected final Map<String, String> rendererToImagePathMap = new HashMap<>();
	protected final RenderersCache renderersCache = new RenderersCache(getJasperReportsContext());
	protected final OasisZip oasisZip;
	
	
	/**
	 *
	 */
	public DocumentBuilder(OasisZip oasisZip)
	{
		this.oasisZip = oasisZip;
	}
	
	
	/**
	 *
	 */
	public static String getImageName(JRPrintElementIndex printElementIndex)
	{
		return IMAGE_NAME_PREFIX + printElementIndex.toString();
	}

	/**
	 *
	 */
	public static JRPrintElementIndex getPrintElementIndex(String imageName)
	{
		if (!imageName.startsWith(IMAGE_NAME_PREFIX))
		{
			throw 
				new JRRuntimeException(
					JRAbstractExporter.EXCEPTION_MESSAGE_KEY_INVALID_IMAGE_NAME,
					new Object[]{imageName});
		}

		return JRPrintElementIndex.parsePrintElementIndex(imageName.substring(IMAGE_NAME_PREFIX_LEGTH));
	}

	/**
	 *
	 */
	protected String getHyperlinkURL(JRPrintHyperlink link)
	{
		return getHyperlinkURL(link, true);
	}
	
	/**
	 *
	 */
	protected String getHyperlinkURL(JRPrintHyperlink link, boolean isOnePagePerSheet)
	{
		String href = null;
		JRHyperlinkProducer customHandler = getHyperlinkProducer(link);
		if (customHandler == null)
		{
			switch (link.getHyperlinkType())
			{
				case REFERENCE :
				{
					if (link.getHyperlinkReference() != null)
					{
						href = link.getHyperlinkReference();
					}
					break;
				}
				case LOCAL_ANCHOR :
				{
					if (link.getHyperlinkAnchor() != null)
					{
						href = "#" + link.getHyperlinkAnchor();
					}
					break;
				}
				case LOCAL_PAGE :
				{
					if (link.getHyperlinkPage() != null)
					{
						href = "#" + JR_PAGE_ANCHOR_PREFIX + getReportIndex() + "_" + (isOnePagePerSheet ? link.getHyperlinkPage().toString() : "1");
					}
					break;
				}
				case REMOTE_ANCHOR :
				{
					if (
						link.getHyperlinkReference() != null &&
						link.getHyperlinkAnchor() != null
						)
					{
						href = link.getHyperlinkReference() + "#" + link.getHyperlinkAnchor();
					}
					break;
				}
				case REMOTE_PAGE :
				{
					if (
						link.getHyperlinkReference() != null &&
						link.getHyperlinkPage() != null
						)
					{
						href = link.getHyperlinkReference() + "#" + JR_PAGE_ANCHOR_PREFIX + "0_" + link.getHyperlinkPage().toString();
					}
					break;
				}
				case NONE :
				default :
				{
					break;
				}
			}
		}
		else
		{
			href = customHandler.getHyperlink(link);
		}

		return href;
	}

	/**
	 *
	 */
	protected RenderersCache getRenderersCache()
	{
		return renderersCache;
	}

	/**
	 *
	 */
	protected String getImagePath(
		Renderable renderer, 
		Dimension dimension, 
		Color backcolor, 
		JRExporterGridCell gridCell,
//		boolean isLazy,
		RenderersCache imageRenderersCache
		) throws JRException
	{
		String imagePath = null;
		
//		if (isLazy)  // honouring lazy images in ods/odt is unlike any other export except html and xml
//		{
//			// we do not cache imagePath for lazy images because the short location string is already cached inside the render itself
//			imagePath = RendererUtil.getResourceLocation(renderer);
//		}
//		else
//		{
			// by the time we get here, the resource renderer has already been loaded from cache
			
			if (
				renderer instanceof DataRenderable //we do not cache imagePath for non-data renderers because they render width different width/height each time
				&& rendererToImagePathMap.containsKey(renderer.getId())
				)
			{
				imagePath = rendererToImagePathMap.get(renderer.getId());
			}
			else
			{
				JRPrintElementIndex imageIndex = getElementIndex(gridCell);
				
				DataRenderable imageRenderer = 
					RendererUtil.getInstance(getJasperReportsContext()).getImageDataRenderable(
						imageRenderersCache,
						renderer, 
						dimension, 
						backcolor
						);

				byte[] data = imageRenderer.getData(getJasperReportsContext());
				
				if (ImageTypeEnum.WEBP == JRTypeSniffer.getImageTypeValue(data))
				{
					WrappingImageDataToGraphics2DRenderer graphics2DRenderer = new WrappingImageDataToGraphics2DRenderer(imageRenderer);
					data = new WrappingRenderToImageDataRenderer(graphics2DRenderer, graphics2DRenderer, null).getData(getJasperReportsContext());
				}
				
				oasisZip.addEntry(//FIXMEODT optimize with a different implementation of entry
					new FileBufferedZipEntry(
						"Pictures/" + DocumentBuilder.getImageName(imageIndex),
						data
						)
					);

				String imageName = DocumentBuilder.getImageName(imageIndex);
				imagePath = "Pictures/" + imageName;

				if (imageRenderer == renderer)
				{
					//cache imagePath only for true ImageRenderable instances because the wrapping ones render with different width/height each time
					rendererToImagePathMap.put(renderer.getId(), imagePath);
				}
			}
//		}

		return imagePath;
	}

	/**
	 *
	 */
	protected JRPrintElementIndex getElementIndex(JRExporterGridCell gridCell)
	{
		JRPrintElementIndex imageIndex =
			new JRPrintElementIndex(
					getReportIndex(),
					getPageIndex(),
					gridCell.getElementAddress()
					);
		return imageIndex;
	}

	/**
	 *
	 */
	public abstract JRStyledText getStyledText(JRPrintText text);

	/**
	 *
	 */
	public abstract Locale getTextLocale(JRPrintText text);

	/**
	 *
	 */
	public abstract String getInvalidCharReplacement();
	
	/**
	 * 
	 */
	protected abstract void insertPageAnchor(TableBuilder tableBuilder);

	/**
	 * 
	 */
	protected abstract JRHyperlinkProducer getHyperlinkProducer(JRPrintHyperlink link);

	/**
	 * 
	 */
	protected abstract JasperReportsContext getJasperReportsContext();

	/**
	 * 
	 */
	protected abstract int getReportIndex();

	/**
	 * 
	 */
	protected abstract int getPageIndex();

}
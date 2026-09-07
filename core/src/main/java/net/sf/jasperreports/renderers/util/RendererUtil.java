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
package net.sf.jasperreports.renderers.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.type.ImageTypeEnum;
import net.sf.jasperreports.engine.type.OnErrorTypeEnum;
import net.sf.jasperreports.engine.util.JRImageLoader;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRTypeSniffer;
import net.sf.jasperreports.renderers.DataRenderable;
import net.sf.jasperreports.renderers.Graphics2DRenderable;
import net.sf.jasperreports.renderers.Renderable;
import net.sf.jasperreports.renderers.RenderersCache;
import net.sf.jasperreports.renderers.ResourceRenderer;
import net.sf.jasperreports.renderers.SimpleDataRenderer;
import net.sf.jasperreports.renderers.WrappingRenderToImageDataRenderer;
import net.sf.jasperreports.renderers.util.SvgDataSniffer.SvgInfo;
import net.sf.jasperreports.renderers.util.XmlDataSniffer.XmlSniffResult;
import net.sf.jasperreports.repo.RepositoryContext;
import net.sf.jasperreports.repo.RepositoryUtil;
import net.sf.jasperreports.repo.SimpleRepositoryContext;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class RendererUtil
{
	
	private static final Log log = LogFactory.getLog(RendererUtil.class);
	
	public static final String EXCEPTION_MESSAGE_KEY_IMAGE_ERROR = "engine.renderable.util.image.error";
	public static final String EXCEPTION_MESSAGE_KEY_RENDERABLE_MUST_IMPLEMENT_INTERFACE = "engine.renderable.must.implement.interface";
	
	public static final Renderable NO_IMAGE_RENDERER = ResourceRenderer.getInstance(JRImageLoader.NO_IMAGE_RESOURCE, false); 

	public static final String SVG_MIME_TYPE = "image/svg+xml";
	public static final String SVG_FILE_EXTENSION = "svg";
	
	protected static final String SVG_ROOT_ELEMENT = "svg";
	protected static final String SVG_ROOT_ELEMENT_QNAME_SUFFIX = ":" + SVG_ROOT_ELEMENT;

	/**
	 *
	 */
	private final RepositoryContext context;
	private SvgDataSniffer svgDataSniffer;

	/**
	 *
	 */
	private RendererUtil(RepositoryContext context)
	{
		this.context = context;
	}


	/**
	 *
	 */
	public static RendererUtil getInstance(JasperReportsContext jasperReportsContext)
	{
		return getInstance(SimpleRepositoryContext.of(jasperReportsContext));
	}

	public static RendererUtil getInstance(RepositoryContext context)
	{
		return new RendererUtil(context);
	}


	/**
	 *
	 */
	public boolean isSvgData(byte[] data)
	{
		SvgInfo svgInfo = getSvgInfo(data);
		return svgInfo != null;
	}


	/**
	 *
	 */
	public SvgDataSniffer.SvgInfo getSvgInfo(byte[] data)
	{
		if (JRTypeSniffer.getImageTypeValue(data) == ImageTypeEnum.UNKNOWN)
		{
			XmlSniffResult xmlSniff = XmlDataSniffer.sniffXml(data);
			if (xmlSniff != null)
			{
				String rootElement = xmlSniff.getRootElementName();
				if (rootElement == null //the sniffer did not determine the root element, trying SVG to be sure
						|| isSvgRootElement(rootElement))
				{
					return getSvgDataSniffer().getSvgInfo(data);
				}
			}
		}
		
		return null;
	}
	
	protected boolean isSvgRootElement(String rootElementName)
	{
		return rootElementName.equalsIgnoreCase(SVG_ROOT_ELEMENT)
				|| rootElementName.toLowerCase().endsWith(SVG_ROOT_ELEMENT_QNAME_SUFFIX);
	}


	/**
	 *
	 */
	public boolean isSvgData(DataRenderable dataRenderable) throws JRException
	{
		return isSvgData(dataRenderable.getData(context.getJasperReportsContext()));//TODO pass report context?
	}


	/**
	 *
	 */
	public synchronized SvgDataSniffer getSvgDataSniffer()
	{
		if (svgDataSniffer == null)
		{
			svgDataSniffer = SvgDataSniffer.getInstance(context.getJasperReportsContext());
		}
		return svgDataSniffer;
	}


	/**
	 *
	 */
	public Renderable getNonLazyRenderable(String resourceLocation, OnErrorTypeEnum onErrorType) throws JRException
	{
		byte[] data;

		try
		{
			data = RepositoryUtil.getInstance(context).getBytesFromLocation(resourceLocation);
		}
		catch (Exception e)
		{
			if (log.isDebugEnabled())
			{
				log.debug("handled image error with type " + onErrorType + " for location " + resourceLocation, e);
			}
			
			return handleImageError(e, onErrorType); 
		}
		
		return SimpleDataRenderer.getInstance(data);
	}

	
	/**
	 *
	 */
	public Renderable getRenderable(Image img, OnErrorTypeEnum onErrorType) throws JRException
	{
		ImageTypeEnum type = ImageTypeEnum.JPEG;
		if (img instanceof RenderedImage)
		{
			ColorModel colorModel = ((RenderedImage) img).getColorModel();
			//if the image has transparency, encode as PNG
			if (colorModel.hasAlpha() 
					&& colorModel.getTransparency() != Transparency.OPAQUE)
			{
				type = ImageTypeEnum.PNG;
			}
		}
		
		return getRenderable(img, type, onErrorType);
	}


	/**
	 * Creates and returns an instance of the JRImageRenderer class after encoding the image object using an image
	 * encoder that supports the supplied image type.
	 * 
	 * @param image the java.awt.Image object to wrap into a JRImageRenderer instance
	 * @param imageType the type of the image as specified by one of the constants defined in the ImageTypeEnum
	 * @param onErrorType one of the error type constants defined in the {@link OnErrorTypeEnum}.
	 * @return the image renderer instance
	 */
	public Renderable getRenderable(Image image, ImageTypeEnum imageType, OnErrorTypeEnum onErrorType) throws JRException
	{
		byte[] data = null;
		try
		{
			data = JRImageLoader.getInstance(context.getJasperReportsContext()).loadBytesFromAwtImage(image, imageType);
		}
		catch (Exception e)
		{
			if (log.isDebugEnabled())
			{
				log.debug("handled image error with type " + onErrorType, e);
			}

			return handleImageError(e, onErrorType);
		}
		return SimpleDataRenderer.getInstance(data);
	}


	public Renderable getRenderable(byte[] data)
	{
		return SimpleDataRenderer.getInstance(data);
	}
	
	/**
	 *
	 */
	public Renderable getRenderable(InputStream is, OnErrorTypeEnum onErrorType) throws JRException
	{
		byte[] data = null;
		try
		{
			data = JRLoader.loadBytes(is);
		}
		catch (Exception e)
		{
			if (log.isDebugEnabled())
			{
				log.debug("handled image error with type " + onErrorType, e);
			}

			return handleImageError(e, onErrorType); 
		}
		return SimpleDataRenderer.getInstance(data);
	}


	/**
	 *
	 */
	public Renderable getRenderable(URL url, OnErrorTypeEnum onErrorType) throws JRException
	{
		byte[] data = null;
		try
		{
			data = JRLoader.loadBytes(url);
		}
		catch (Exception e)
		{
			if (log.isDebugEnabled())
			{
				log.debug("handled image error with type " + onErrorType + " for URL " + url, e);
			}

			return handleImageError(e, onErrorType); 
		}
		return SimpleDataRenderer.getInstance(data);
	}


	/**
	 *
	 */
	public Renderable getRenderable(File file, OnErrorTypeEnum onErrorType) throws JRException
	{
		byte[] data = null;
		try
		{
			data = JRLoader.loadBytes(file);
		}
		catch (Exception e)
		{
			if (log.isDebugEnabled())
			{
				log.debug("handled image error with type " + onErrorType + " for file " + file, e);
			}

			return handleImageError(e, onErrorType); 
		}
		return SimpleDataRenderer.getInstance(data);
	}


	/**
	 * 
	 */
	public Renderable handleImageError(Exception error, OnErrorTypeEnum onErrorType) throws JRException
	{
		Renderable errorRenderable;
		if (error instanceof JRException)
		{
			errorRenderable = getOnErrorRenderer(onErrorType, (JRException) error);
		}
		else if (error instanceof JRRuntimeException)
		{
			errorRenderable = getOnErrorRenderer(onErrorType, (JRRuntimeException) error);
		}
		else if (error instanceof RuntimeException)
		{
			throw (RuntimeException) error;
		}
		else
		{
			// we shouldn't get here normally
			if (log.isDebugEnabled())
			{
				log.debug("got unexpected image exception of type " + error.getClass().getName(), error);
			}
			
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_IMAGE_ERROR,
					(Object[])null,
					error
					);
		}
		return errorRenderable;
	}

	/**
	 *
	 */
	public Renderable getOnErrorRenderer(OnErrorTypeEnum onErrorType, JRException e) throws JRException
	{
		Renderable renderer = null;
		
		switch (OnErrorTypeEnum.getValueOrDefault(onErrorType))
		{
			case ICON :
			{
				renderer = NO_IMAGE_RENDERER;
				break;
			}
			case BLANK :
			{
				break;
			}
			case ERROR :
			default :
			{
				throw e;
			}
		}

		return renderer;
	}

	/**
	 *
	 */
	public Renderable getOnErrorRenderer(OnErrorTypeEnum onErrorType, JRRuntimeException e) throws JRRuntimeException
	{
		Renderable renderer = null;
		
		switch (OnErrorTypeEnum.getValueOrDefault(onErrorType))
		{
			case ICON :
			{
				renderer = NO_IMAGE_RENDERER;
				break;
			}
			case BLANK :
			{
				break;
			}
			case ERROR :
			default :
			{
				throw e;
			}
		}

		return renderer;
	}


	/**
	 *
	 */
	public static boolean isLazy(Renderable renderer)
	{
		boolean isLazy = false;

		if (renderer instanceof ResourceRenderer)
		{
			isLazy = ((ResourceRenderer)renderer).isLazy();
		}
		
		return isLazy;
	}


	/**
	 *
	 */
	public static String getResourceLocation(Renderable renderer)
	{
		String resourceLocation = null;

		if (renderer instanceof ResourceRenderer)
		{
			resourceLocation = ((ResourceRenderer)renderer).getResourceLocation();
		}
		
		return resourceLocation;
	}

	
	/**
	 * 
	 */
	public DataRenderable getDataRenderable(
		Renderable renderer, 
		Dimension dimension, 
		Color backcolor
		) throws JRException
	{
		DataRenderable imageRenderer = null;
		
		if (renderer != null)
		{
			if (renderer instanceof DataRenderable)
			{
				imageRenderer = (DataRenderable)renderer;
			}
			else
			{
				Graphics2DRenderable grxRenderer = null;
				
				if (renderer instanceof Graphics2DRenderable)
				{
					grxRenderer = (Graphics2DRenderable)renderer;
				}
				else
				{
					throw 
						new JRException(
							EXCEPTION_MESSAGE_KEY_RENDERABLE_MUST_IMPLEMENT_INTERFACE,
							new Object[]{
								renderer.getClass().getName(),
								DataRenderable.class.getName() 
									+ " or " + Graphics2DRenderable.class.getName() 
								}
							);
				}

				imageRenderer =
					new WrappingRenderToImageDataRenderer(
						grxRenderer,
						dimension,
						backcolor
						);
			}
		}
			
		return imageRenderer;
	}

	
	/**
	 * 
	 */
	public DataRenderable getImageDataRenderable(
		RenderersCache renderersCache,
		Renderable renderer, 
		Dimension dimension, 
		Color backcolor
		) throws JRException
	{
		DataRenderable imageRenderer = null;
		
		if (renderer != null)
		{
			if (renderer instanceof DataRenderable)
			{
				boolean isSvgData = isSvgData((DataRenderable)renderer);
				if (isSvgData)
				{
					imageRenderer =
						new WrappingRenderToImageDataRenderer(
							(Graphics2DRenderable)renderersCache.getWrappingRenderable(renderer.getId(), (DataRenderable)renderer), 
							dimension, 
							backcolor
							);
				}
				else
				{
					imageRenderer = (DataRenderable)renderer;
				}
			}
			else
			{
				Graphics2DRenderable grxRenderer = null;
				
				if (renderer instanceof Graphics2DRenderable)
				{
					grxRenderer = (Graphics2DRenderable)renderer;
				}
				else
				{
					throw 
						new JRException(
							EXCEPTION_MESSAGE_KEY_RENDERABLE_MUST_IMPLEMENT_INTERFACE,
							new Object[]{
								renderer.getClass().getName(),
								DataRenderable.class.getName() 
									+ " or " + Graphics2DRenderable.class.getName() 
								}
							);
				}

				imageRenderer =
					new WrappingRenderToImageDataRenderer(
						grxRenderer,
						dimension,
						backcolor
						);
			}
		}
			
		return imageRenderer;
	}
}

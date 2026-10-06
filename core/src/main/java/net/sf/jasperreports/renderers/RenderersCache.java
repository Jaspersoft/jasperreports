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

import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.type.OnErrorTypeEnum;
import net.sf.jasperreports.renderers.util.RendererUtil;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class RenderersCache
{
	private final RendererUtil rendererUtil;
	private final Map<String, Renderable> resourceRenderers;
	private final Map<String, Renderable> wrappingDataRenderers;
	
	
	/**
	 * 
	 */
	public RenderersCache(JasperReportsContext jasperReportsContext)
	{
		this.rendererUtil = RendererUtil.getInstance(jasperReportsContext);
		this.resourceRenderers = new HashMap<>();
		this.wrappingDataRenderers = new HashMap<>();
	}


	/**
	 * 
	 */
	public synchronized Renderable getLoadedRenderer(ResourceRenderer resourceRenderer) throws JRException
	{
		Renderable loadedRenderer;
		String resourceRendererId = resourceRenderer.getId();
		if (resourceRenderers.containsKey(resourceRendererId))
		{
			loadedRenderer = resourceRenderers.get(resourceRendererId);
		}
		else
		{
			// since we force OnErrorTypeEnum.ERROR, it is guaranteed that the method will return a DataRenderable implementation
			loadedRenderer = rendererUtil.getNonLazyRenderable(resourceRenderer.getResourceLocation(), OnErrorTypeEnum.ERROR);
			resourceRenderers.put(resourceRendererId, loadedRenderer);
		}
		
		return loadedRenderer;
	}


	/**
	 * 
	 */
	public DimensionRenderable getDimensionRenderable(Renderable renderer) throws JRException
	{
		DimensionRenderable dimensionRenderer;
		
		if (renderer instanceof DimensionRenderable)
		{
			//do not cache renderers that are already dimension renderers
			dimensionRenderer = (DimensionRenderable)renderer;
		}
		else if (renderer instanceof DataRenderable)
		{
			dimensionRenderer = (DimensionRenderable)getWrappingRenderable(renderer.getId(), (DataRenderable)renderer);
		}
		else
		{
			dimensionRenderer = null; // returning a dimension renderable is not mandatory
		}
		
		return dimensionRenderer;
	}


	/**
	 * 
	 */
	public Graphics2DRenderable getGraphics2DRenderable(Renderable renderer) throws JRException
	{
		Graphics2DRenderable grxRenderer = null;
		
		if (renderer != null)
		{
			if (renderer instanceof Graphics2DRenderable)
			{
				//do not cache renderers that are already graphics2d renderers
				grxRenderer = (Graphics2DRenderable)renderer;
			}
			else if (renderer instanceof DataRenderable)
			{
				grxRenderer = (Graphics2DRenderable)getWrappingRenderable(renderer.getId(), (DataRenderable)renderer);
			}
			else
			{
				throw 
					new JRException(
						RendererUtil.EXCEPTION_MESSAGE_KEY_RENDERABLE_MUST_IMPLEMENT_INTERFACE,
						new Object[]{
							renderer.getClass().getName(),
							DataRenderable.class.getName() 
								+ " or " + Graphics2DRenderable.class.getName() 
							}
						);
			}
		}
		
		return grxRenderer;
	}

	
	/**
	 * 
	 */
	public synchronized Renderable getWrappingRenderable(String rendererId, DataRenderable dataRenderer) throws JRException
	{
		Renderable wrappingRenderer = null;
		
		if (wrappingDataRenderers.containsKey(rendererId))
		{
			wrappingRenderer = wrappingDataRenderers.get(rendererId);
		}
		else
		{
			boolean isSvgData = rendererUtil.isSvgData(dataRenderer);
			if (isSvgData)
			{
				wrappingRenderer = new WrappingSvgDataToGraphics2DRenderer(dataRenderer);
			}
			else
			{
				wrappingRenderer = new WrappingImageDataToGraphics2DRenderer(dataRenderer);
			}

			wrappingDataRenderers.put(rendererId, wrappingRenderer);
		}
		
		return wrappingRenderer;
	}
}

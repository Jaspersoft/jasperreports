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
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * Provides functionality for image rendering.
 * <p/>
 * The content of an image element can come either directly from an image file like a JPG,
 * GIF, PNG, or can be a Scalable Vector Graphics (SVG) file that is rendered using some
 * business logic or a special graphics API like a charting or a barcode library. Either way,
 * JasperReports treats images in a very transparent way because it relies on a special
 * interface called {@link net.sf.jasperreports.renderers.Renderable} to offer a common
 * way to render images.
 * <p/>
 * The {@link net.sf.jasperreports.renderers.Graphics2DRenderable} interface has a method called 
 * {@link Graphics2DRenderable#render(JasperReportsContext, Graphics2D, Rectangle2D)},
 * which gets called by the engine each time it needs to draw the image
 * on a given device or graphic context. This approach provides the best quality for the
 * SVG images when they must be drawn on unknown devices or zoomed into without
 * losing sharpness.
 * <p/>
 * The {@link net.sf.jasperreports.renderers.DataRenderable} interface has a method called 
 * {@link DataRenderable#getData(JasperReportsContext)},
 * which gets called by the engine each time it needs the actual image data either to embed it
 * directly into the exported documents, or to use it to draw the image or the graphic onto a device 
 * or graphic context.
 * <p/>
 * When a renderable object provides only binary data, the engine uses wrapping renderable implementations
 * to add rendering functionality to the source renderable object. On the other hand, when the renderable object
 * provides only direct rendering functionality, the engine wraps it into special renderable implementations that
 * would allow producing image data out of the source rendering routines.
 * <p/>
 * The library comes with a default implementation for the 
 * {@link net.sf.jasperreports.renderers.DataRenderable} interface that
 * wraps images that come from files or binary image data in JPG, GIF, or PNG format.
 * This is the {@link net.sf.jasperreports.renderers.SimpleDataRenderer} class.
 * For wrapping SVG data, the {@link net.sf.jasperreports.renderers.SimpleRenderToImageAwareDataRenderer} is better
 * suited because it allows providing additional information about required resolution of the graphic, when converted to
 * and image, as the engine needs to do that for certain document formats at export time.
 * <p/>
 * Image renderers are serializable because inside the generated document for each image is
 * a renderer object kept as reference, which is serialized along with the whole
 * JasperPrint object.
 * <p/>
 * When a {@link net.sf.jasperreports.renderers.SimpleDataRenderer} instance is serialized, 
 * so is the binary image data it contains.
 * <p/>
 * However, if the image element must be lazy loaded (see the <code>isLazy</code> image attribute), then the
 * engine will not load the binary image data at report-filling time. Rather, it stores inside
 * the renderer only the <code>java.lang.String</code> location of the image. The actual image data
 * is loaded only when needed for rendering at report-export or view time.
 * Lazy image renderers are represented by the the {@link ResourceRenderer} implementation and require
 * the image data to be loaded at export time for certain export formats.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface Renderable extends Serializable
{
	/**
	 * Specifies the image resolution in dots-per-inch, for the images created by the engine when rasterizing SVGs or when clipping other renderers.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_OTHER,
			defaultValue = "the resolution of the report",
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_4_1_1,
			valueType = Integer.class
			)
	public static final String PROPERTY_IMAGE_DPI = JRPropertiesUtil.PROPERTY_PREFIX + "image.dpi";

	/**
	 *
	 */
	public String getId();
}

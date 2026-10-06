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
package net.sf.jasperreports.engine;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.xml.JRXmlConstants;


/**
 * An abstract representation that provides basic functionality for images.
 * <p/>
 * The most complex graphics on a report are the images. Just as for text field elements,
 * their content is dynamically evaluated at runtime, using a report expression.
 * <h3>Scaling Images</h3>
 * Since images are loaded at runtime, there is no way to know their exact size when
 * creating the report template. The dimensions of the image element specified at design
 * time may differ from the actual image loaded at runtime. One must define how the image
 * should adapt to the original image element dimensions specified in the report template.
 * The <code>scaleImage</code> attribute (see {@link #getScaleImage()}) allows users 
 * to choose one of the following values:
 * <dl>
 * <dt><code>Clip</code></dt>
 * <dd>If the actual image is larger than the image element size, it will
 * be cut off so that it keeps its original resolution, and only the region that fits the
 * specified size will be displayed.</dd>
 * <dt><code>FillFrame</code></dt>
 * <dd>If the dimensions of the actual image do not fit those
 * specified for the image element that displays it, the image is forced to obey them
 * and stretch itself so that it fits in the designated output area. It will be deformed if
 * necessary.</dd>
 * <dt><code>RetainShape</code></dt>
 * <dd>If the actual image does not fit into the image
 * element, it can be adapted to those dimensions while keeping its original
 * undeformed proportions.</dd>
 * <dt><code>RealHeight</code></dt>
 * <dd>The image can be stretched vertically to
 * match the actual image height, while preserving the declared width of the image
 * element.</dd>
 * <dt><code>RealSize</code></dt>
 * <dd>The image can be stretched vertically to
 * match the actual image height, while adjusting the width of the image element to
 * match the actual image width.</dd>
 * </dl>
 * <h3>Stretching Images</h3>
 * The last two options allow report designers to specify a minimum height for the image
 * element in the report template and to rely on the fact that the image element will stretch
 * at fill time to accommodate the actual size of the rendered picture.
 * <p/>
 * Note that, as for stretchable text fields, only the image height can increase. This follows
 * the JasperReports principle that design element widths are fixed, and that the report
 * flows downwards while data is fed to it.
 * <p/>
 * As mentioned above, there are two scale types that allow images to stretch: <code>RealHeight</code>
 * and <code>RealSize</code>. The difference between them is that the former always preserves the
 * width of the image element, while the latter adjusts the width of the image when the
 * actual image width is smaller than the design image width. Adjusting the image width is
 * useful when a border needs to be drawn around the image; if the image width is not
 * adjusted, then the border might get drawn according to the design image width and some
 * empty space would be left between the image and the border.
 * <p/>
 * In order for image stretching to work, the actual image size needs to be known at report
 * fill time. For images that use image files as sources, the image size will always be
 * known. For images that use other type of renderers, the image size is determined by
 * calling the <code>getDimension()</code> method on the image renderer object, which is an instance
 * of a class that implements {@link net.sf.jasperreports.renderers.Renderable}. If the
 * method returns a null dimension, the actual image size will not be known and the image
 * will not stretch, but will render inside the area given by the design image width and
 * height.
 * <p/>
 * Another inherent limitation is that images that have delayed evaluation will not be able
 * to stretch. Stretching such images is not supported because the actual image size can only
 * be know after the image has been positioned in the generated report and other elements
 * have been placed beneath the image, and stretching the image at this point would disturb
 * the layout of the generated report.
 * <p/>
 * Stretching images will always preserve proportions and will never get deformed. Any
 * scaling which performed to the image will be performed uniformly on the horizontal and
 * vertical axes.
 * <p/>
 * If the actual image width is larger than the design width of the image element, the image
 * will be scaled down to fit the element width set at design time. In other words, the design
 * image width acts as an upper limit of the width of the generated image.
 * <p/>
 * When the engine stretches an image, if the actual image height is bigger than the vertical
 * space left to the bottom of the page, the image will cause the band to overflow and the
 * engine will render the image on the new report page or column. If the vertical space
 * available here is still not enough to render the image at its actual size, the image will be
 * scaled down to fit the available height.
 * <p/>
 * Note that if the actual height of the image is smaller than the declared height of the
 * image, the height of the generated image will not be decreased. The produced image will
 * always be at least as tall as the design image element, its height can only be increased
 * when the report is filled.
 * 
 * @see net.sf.jasperreports.renderers.Renderable
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface JRCommonImage extends JRImageAlignment, JRCommonGraphicElement, JRBoxContainer
{

	/**
	 * Gets the image scale type.
	 * @return a value representing one of the scale type constants in {@link ScaleImageEnum}
	 */
	@JsonIgnore
	public ScaleImageEnum getScaleImage();
	
	/**
	 * Gets the image own scale type.
	 * @return a value representing one of the scale type constants in {@link ScaleImageEnum}
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_scaleImage)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_scaleImage, isAttribute = true)
	public ScaleImageEnum getOwnScaleImage();
	
	/**
	 * Sets the image scale type.
	 * @param scaleImageEnum a value representing one of the scale type constants in {@link ScaleImageEnum}
	 */
	@JsonSetter
	public void setScaleImage(ScaleImageEnum scaleImageEnum);

	/**
	 * Gets the image rotation.
	 * @return a value representing one of the image rotation constants in {@link RotationEnum}
	 */
	@JsonIgnore
	public RotationEnum getRotation();
	
	/**
	 * Gets the image own rotation.
	 * @return a value representing one of the image rotation constants in {@link RotationEnum}
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_rotation)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_rotation, isAttribute = true)
	public RotationEnum getOwnRotation();
	
	/**
	 * Sets the image rotation.
	 * @param rotationEnum a value representing one of the image rotation constants in {@link RotationEnum}
	 */
	@JsonSetter
	public void setRotation(RotationEnum rotationEnum);
	

}

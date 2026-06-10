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
package net.sf.jasperreports.engine.type;

import net.sf.jasperreports.engine.JRImage;
import net.sf.jasperreports.renderers.DimensionRenderable;


/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public enum ScaleImageEnum implements NamedEnum
{
	/**
	 * A constant value specifying that if the actual image is larger than the image element size, it will be cut off so
	 * that it keeps its original resolution, and only the region that fits the specified size will be displayed.
	 */
	CLIP("Clip"),

	/**
	 * A constant value specifying that if the dimensions of the actual image do not fit those specified for the
	 * image element that displays it, the image can be forced to obey them and stretch itself so that it fits
	 * in the designated output area.
	 */
	FILL_FRAME("FillFrame"),
	
	/**
	 * A constant value specifying that if the actual image does not fit into the image element, it can be adapted
	 * to those dimensions without needing to change its original proportions.
	 */
	RETAIN_SHAPE("RetainShape"),
	
	/**
	 * A scale image type that adjusts the image element height to the actual image dimensions.
	 * The image element height stretches to accommodate the actual image, but does not shrink
	 * below its declared value. If the actual image is wider than the image element, the image
	 * is proportionally shrunk to fit the element width and the element height is adjusted
	 * accordingly. The image is then rendered proportionally within the element boundaries,
	 * preserving its aspect ratio.
	 *
	 * <p>
	 * Several restrictions apply to the image element stretching:
	 * <ul>
	 * 	<li>It only works when the image renderer implements
	 *  {@link DimensionRenderable}.</li>
	 * 	<li>Images with delayed evaluation (see {@link JRImage#getEvaluationTime()})
	 * do not stretch the element; the image is proportionally shrunk to fit the declared
	 * element dimensions.</li>
	 * 	<li>An image overflows (to the next page/column) only once; after that,
	 * the image is rendered in the available space by proportionally
	 * shrinking its size.</li>
	 * </ul>
	 * </p>
	 * 
	 * @see #REAL_SIZE
	 */
	REAL_HEIGHT("RealHeight"),
	
	/**
	 * A scale image type that adjusts the image element height in the same way as
	 * {@link #REAL_HEIGHT}, and in addition reduces the image element
	 * width to match the width occupied by the rendered image.
	 * 
	 * <p>
	 * This is useful when, for instance, a border needs to be drawn around
	 * the image element, tightly wrapping the rendered image content.
	 * </p>
	 */
	REAL_SIZE("RealSize");
	
	/**
	 *
	 */
	private final transient String name;

	private ScaleImageEnum(String name)
	{
		this.name = name;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	/**
	 *
	 */
	public static ScaleImageEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
}

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

import java.awt.geom.Rectangle2D;
import java.util.List;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintImageAreaHyperlink;
import net.sf.jasperreports.engine.JasperReportsContext;


/**
 * The is a default implementation for the 
 * {@link net.sf.jasperreports.renderers.DataRenderable} interface that
 * wraps images that come from files or binary image data in JPG, GIF, or PNG format.
 * It can also wrap SVG data, but for better quality when converting to image,
 * the {@link SimpleRenderToImageAwareDataRenderer} implementation is recommended.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class SimpleDataRenderer extends AbstractRenderer implements DataRenderable, AreaHyperlinksRenderable
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	/**
	 *
	 */
	private final byte[] data;
	private final List<JRPrintImageAreaHyperlink> areaHyperlinks;

	/**
	 *
	 */
	protected SimpleDataRenderer(byte[] data)
	{
		this(data, null);
	}

	/**
	 * 
	 */
	public SimpleDataRenderer(byte[] data, List<JRPrintImageAreaHyperlink> areaHyperlinks) 
	{
		this.data = data;
		this.areaHyperlinks = areaHyperlinks;
	}

	/**
	 *
	 */
	public static SimpleDataRenderer getInstance(byte[] data)
	{
		if (data != null) 
		{
			return new SimpleDataRenderer(data);
		}
		return null;
	}


	@Override
	public byte[] getData(JasperReportsContext jasperReportsContext) throws JRException
	{
		return data;
	}

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

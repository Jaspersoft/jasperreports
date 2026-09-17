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
import java.awt.image.BufferedImage;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;


/**
 * This interface should be implemented by renderable object which want to benefit from better resolution and control
 * the way the graphic context is created when they are converted to image data, during export to certain document formats.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface RenderToImageAwareRenderable
{
	/**
	 * 
	 */
	public int getImageDataDPI(JasperReportsContext jasperReportsContext);

	/**
	 * Returns the resolution of the report this renderer belongs to. Renderer dimensions are
	 * expressed in the pixels of the report, so the resolution is needed to work out the physical
	 * size an image has to be rasterized at.
	 */
	default int getReportDpi()
	{
		return JasperPrint.DEFAULT_REPORT_DPI;
	}

	/**
	 * 
	 */
	public Graphics2D createGraphics(BufferedImage bi);
}

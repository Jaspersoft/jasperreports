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
package net.sf.jasperreports.export;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class SimpleGraphics2DReportConfiguration extends SimpleReportExportConfiguration implements Graphics2DReportConfiguration
{
	private Float zoomRatio;
	private Boolean isMinimizePrinterJobSize;
	private Boolean isIgnoreMissingFont;
	private Boolean isWhitePageBackground;

	/**
	 * 
	 */
	public SimpleGraphics2DReportConfiguration()
	{
	}
	
	@Override
	public Float getZoomRatio()
	{
		return zoomRatio;
	}
	
	/**
	 * 
	 */
	public void setZoomRatio(Float zoomRatio)
	{
		this.zoomRatio = zoomRatio;
	}
	
	@Override
	public Boolean isMinimizePrinterJobSize()
	{
		return isMinimizePrinterJobSize;
	}
	
	/**
	 * 
	 */
	public void setMinimizePrinterJobSize(Boolean isMinimizePrinterJobSize)
	{
		this.isMinimizePrinterJobSize = isMinimizePrinterJobSize;
	}

	@Override
	public Boolean isIgnoreMissingFont()
	{
		return isIgnoreMissingFont;
	}
	
	/**
	 * 
	 */
	public void setIgnoreMissingFont(Boolean isIgnoreMissingFont)
	{
		this.isIgnoreMissingFont = isIgnoreMissingFont;
	}

	@Override
	public Boolean isWhitePageBackground()
	{
		return isWhitePageBackground;
	}

	/**
	 * Sets the flag that determines whether to draw white page backgrounds.
	 * 
	 * @param isWhitePageBackground whether to draw white page backgrounds
	 * @see Graphics2DReportConfiguration#PROPERTY_WHITE_PAGE_BACKGROUND
	 * @since 6.5.0
	 */
	public void setWhitePageBackground(Boolean isWhitePageBackground)
	{
		this.isWhitePageBackground = isWhitePageBackground;
	}
}

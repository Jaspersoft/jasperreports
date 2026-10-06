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

import net.sf.jasperreports.export.type.HtmlBorderCollapseEnum;
import net.sf.jasperreports.export.type.HtmlSizeUnitEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class SimpleHtmlReportConfiguration extends SimpleReportExportConfiguration implements HtmlReportConfiguration
{
	private Boolean isRemoveEmptySpaceBetweenRows;
	private Boolean isWhitePageBackground;
	private Boolean isWrapBreakWord;
	private HtmlSizeUnitEnum sizeUnit;
	private HtmlBorderCollapseEnum borderCollapse;
	private Boolean isIgnorePageMargins;
	private Boolean accessibleHtml;
	private Float zoomRatio;
	private Boolean isIgnoreHyperlink;
	private Boolean isEmbedImage;
	private Boolean isEmbeddedSvgUseFonts;
	private Boolean isConvertSvgToImage;
	private Boolean isUseBackgroundImageToAlign;
	private Boolean includeElementUUID;

	
	/**
	 * 
	 */
	public SimpleHtmlReportConfiguration()
	{
	}

	@Override
	public Boolean isRemoveEmptySpaceBetweenRows()
	{
		return isRemoveEmptySpaceBetweenRows;
	}
	
	/**
	 * 
	 */
	public void setRemoveEmptySpaceBetweenRows(Boolean isRemoveEmptySpaceBetweenRows)
	{
		this.isRemoveEmptySpaceBetweenRows = isRemoveEmptySpaceBetweenRows;
	}

	@Override
	public Boolean isWhitePageBackground()
	{
		return isWhitePageBackground;
	}

	/**
	 * 
	 */
	public void setWhitePageBackground(Boolean isWhitePageBackground)
	{
		this.isWhitePageBackground = isWhitePageBackground;
	}
	
	@Override
	public Boolean isWrapBreakWord()
	{
		return isWrapBreakWord;
	}
	
	/**
	 * 
	 */
	public void setWrapBreakWord(Boolean isWrapBreakWord)
	{
		this.isWrapBreakWord = isWrapBreakWord;
	}
	
	@Override
	public HtmlSizeUnitEnum getSizeUnit()
	{
		return sizeUnit;
	}
	
	/**
	 * 
	 */
	public void setSizeUnit(HtmlSizeUnitEnum sizeUnit)
	{
		this.sizeUnit = sizeUnit;
	}
	
	@Override
	public HtmlBorderCollapseEnum getBorderCollapseValue()
	{
		return borderCollapse;
	}
	
	/**
	 * 
	 */
	public void setBorderCollapse(HtmlBorderCollapseEnum borderCollapse)
	{
		this.borderCollapse = borderCollapse;
	}
	
	@Override
	public Boolean isIgnorePageMargins()
	{
		return isIgnorePageMargins;
	}
	
	/**
	 * 
	 */
	public void setIgnorePageMargins(Boolean isIgnorePageMargins)
	{
		this.isIgnorePageMargins = isIgnorePageMargins;
	}
	
	@Override
	public Boolean isAccessibleHtml()
	{
		return accessibleHtml;
	}
	
	/**
	 * 
	 */
	public void setAccessibleHtml(Boolean accessibleHtml)
	{
		this.accessibleHtml = accessibleHtml;
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
	public Boolean isIgnoreHyperlink()
	{
		return isIgnoreHyperlink;
	}
	
	/**
	 * 
	 */
	public void setIgnoreHyperlink(Boolean isIgnoreHyperlink)
	{
		this.isIgnoreHyperlink = isIgnoreHyperlink;
	}
	
	@Override
	public Boolean isEmbedImage()
	{
		return isEmbedImage;
	}
	
	/**
	 * 
	 */
	public void setEmbedImage(Boolean isEmbedImage)
	{
		this.isEmbedImage = isEmbedImage;
	}
	
	@Override
	public Boolean isEmbeddedSvgUseFonts()
	{
		return isEmbeddedSvgUseFonts;
	}
	
	/**
	 * 
	 */
	public void setEmbeddedSvgUseFonts(Boolean isEmbeddedSvgUseFonts)
	{
		this.isEmbeddedSvgUseFonts = isEmbeddedSvgUseFonts;
	}
	
	@Override
	public Boolean isConvertSvgToImage()
	{
		return isConvertSvgToImage;
	}
	
	/**
	 * 
	 */
	public void setConvertSvgToImage(Boolean isConvertSvgToImage)
	{
		this.isConvertSvgToImage = isConvertSvgToImage;
	}
	
	@Override
	public Boolean isUseBackgroundImageToAlign()
	{
		return isUseBackgroundImageToAlign;
	}
	
	/**
	 * 
	 */
	public void setUseBackgroundImageToAlign(Boolean isUseBackgroundImageToAlign)
	{
		this.isUseBackgroundImageToAlign = isUseBackgroundImageToAlign;
	}

	@Override
	public Boolean isIncludeElementUUID()
	{
		return includeElementUUID;
	}

	public void setIncludeElementUUID(Boolean includeElementUUID)
	{
		this.includeElementUUID = includeElementUUID;
	}

}

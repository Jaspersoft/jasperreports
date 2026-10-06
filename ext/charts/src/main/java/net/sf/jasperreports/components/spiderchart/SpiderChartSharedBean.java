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
package net.sf.jasperreports.components.spiderchart;

import java.io.Serializable;

import net.sf.jasperreports.charts.util.ChartHyperlinkProvider;
import net.sf.jasperreports.engine.JRConstants;


/**
 * 
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class SpiderChartSharedBean implements Serializable
{

	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private String renderType;
	private Double maxValue;
	private String titleText;
	private String subtitleText;
	private ChartHyperlinkProvider hyperlinkProvider;
	
	private SpiderDataset dataset;
	
	public SpiderChartSharedBean()
	{
		
	}
	
	public SpiderChartSharedBean(
			String renderType,
			Double maxValue,
			String titleText,
			String subtitleText,
			ChartHyperlinkProvider hyperlinkProvider,
			SpiderDataset dataset
			)
	{
		setRenderType(renderType);
		setMaxValue(maxValue);
		setTitleText(titleText);
		setSubtitleText(subtitleText);
		setHyperlinkProvider(hyperlinkProvider);
		setDataset(dataset);
	}
	
	public ChartHyperlinkProvider getHyperlinkProvider()
	{
		return hyperlinkProvider;
	}

	/**
	 * @return the maxValue
	 */
	public Double getMaxValue() 
	{
		return maxValue;
	}

	/**
	 * @return the titleText
	 */
	public String getTitleText() 
	{
		return titleText;
	}

	/**
	 * @return the subtitleText
	 */
	public String getSubtitleText() 
	{
		return subtitleText;
	}

	/**
	 * @return the renderType
	 */
	public String getRenderType() 
	{
		return renderType;
	}

	/**
	 * @param renderType the renderType to set
	 */
	public void setRenderType(String renderType) 
	{
		this.renderType = renderType;
	}

	/**
	 * @param hyperlinkProvider the ChartHyperlinkProvider to set
	 */
	public void setHyperlinkProvider(ChartHyperlinkProvider hyperlinkProvider) 
	{
		this.hyperlinkProvider = hyperlinkProvider;
	}

	/**
	 * @param maxValue the maxValue to set
	 */
	public void setMaxValue(Double maxValue) 
	{
		this.maxValue = maxValue;
	}

	/**
	 * @param titleText the titleText to set
	 */
	public void setTitleText(String titleText) 
	{
		this.titleText = titleText;
	}

	/**
	 * @param subtitleText the subtitleText to set
	 */
	public void setSubtitleText(String subtitleText) 
	{
		this.subtitleText = subtitleText;
	}

	/**
	 * @return the dataset
	 */
	public SpiderDataset getDataset() {
		return dataset;
	}

	/**
	 * @param dataset the dataset to set
	 */
	public void setDataset(SpiderDataset dataset) {
		this.dataset = dataset;
	}

}

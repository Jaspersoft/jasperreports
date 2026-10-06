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
package net.sf.jasperreports.chartthemes.simple;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonRootName("chart-theme")
@JsonIgnoreProperties("eventSupport")
public class ChartThemeSettings implements JRChangeEventsSupport, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/**
	 *
	 */
	public static final String PROPERTY_chartSettings = "chartSettings";
	public static final String PROPERTY_titleSettings = "titleSettings";
	public static final String PROPERTY_subtitleSettings = "subtitleSettings";
	public static final String PROPERTY_legendSettings = "legendSettings";
	public static final String PROPERTY_plotSettings = "plotSettings";
	public static final String PROPERTY_domainAxisSettings = "domainAxisSettings";
	public static final String PROPERTY_rangeAxisSettings = "rangeAxisSettings";

	/**
	 *
	 */
	@JacksonXmlProperty(localName = "chart-settings")
	private ChartSettings chartSettings = new ChartSettings();
	
	@JacksonXmlProperty(localName = "title-settings")
	private TitleSettings titleSettings = new TitleSettings();
	
	@JacksonXmlProperty(localName = "subtitle-settings")
	private TitleSettings subtitleSettings = new TitleSettings();
	
	@JacksonXmlProperty(localName = "legend-settings")
	private LegendSettings legendSettings = new LegendSettings();
	
	@JacksonXmlProperty(localName = "plot-settings")
	private PlotSettings plotSettings = new PlotSettings();
	
	@JacksonXmlProperty(localName = "domain-axis-settings")
	private AxisSettings domainAxisSettings = new AxisSettings();
	
	@JacksonXmlProperty(localName = "range-axis-settings")
	private AxisSettings rangeAxisSettings = new AxisSettings();
	
	/**
	 *
	 */
	public ChartThemeSettings()
	{
	}
	
	/**
	 *
	 */
	public ChartSettings getChartSettings()
	{
		return chartSettings;
	}
	
	/**
	 *
	 */
	public void setChartSettings(ChartSettings chartSettings)
	{
		ChartSettings old = getChartSettings();
		this.chartSettings = chartSettings;
		getEventSupport().firePropertyChange(PROPERTY_chartSettings, old, getChartSettings());
	}

	/**
	 * @return the subtitleSettings
	 */
	public TitleSettings getSubtitleSettings() {
		return subtitleSettings;
	}

	/**
	 * @param subtitleSettings the subtitleSettings to set
	 */
	public void setSubtitleSettings(TitleSettings subtitleSettings) {
		TitleSettings old = getSubtitleSettings();
		this.subtitleSettings = subtitleSettings;
		getEventSupport().firePropertyChange(PROPERTY_subtitleSettings, old, getSubtitleSettings());
	}

	/**
	 * @return the titleSettings
	 */
	public TitleSettings getTitleSettings() {
		return titleSettings;
	}

	/**
	 * @param titleSettings the titleSettings to set
	 */
	public void setTitleSettings(TitleSettings titleSettings) {
		TitleSettings old = getTitleSettings();
		this.titleSettings = titleSettings;
		getEventSupport().firePropertyChange(PROPERTY_titleSettings, old, getTitleSettings());
	}

	/**
	 * @return the legendSettings
	 */
	public LegendSettings getLegendSettings() {
		return legendSettings;
	}

	/**
	 * @param legendSettings the legendSettings to set
	 */
	public void setLegendSettings(LegendSettings legendSettings) {
		LegendSettings old = getLegendSettings();
		this.legendSettings = legendSettings;
		getEventSupport().firePropertyChange(PROPERTY_legendSettings, old, getLegendSettings());
	}


	private transient JRPropertyChangeSupport eventSupport;
	
	@Override
	public JRPropertyChangeSupport getEventSupport()
	{
		synchronized (this)
		{
			if (eventSupport == null)
			{
				eventSupport = new JRPropertyChangeSupport(this);
			}
		}
		
		return eventSupport;
	}

	/**
	 * @return the plotSettings
	 */
	public PlotSettings getPlotSettings() {
		return plotSettings;
	}

	/**
	 * @param plotSettings the plotSettings to set
	 */
	public void setPlotSettings(PlotSettings plotSettings) {
		PlotSettings old = getPlotSettings();
		this.plotSettings = plotSettings;
		getEventSupport().firePropertyChange(PROPERTY_plotSettings, old, getPlotSettings());
	}

	/**
	 * @return the domainAxisSettings
	 */
	public AxisSettings getDomainAxisSettings() {
		return domainAxisSettings;
	}

	/**
	 * @param domainAxisSettings the domainAxisSettings to set
	 */
	public void setDomainAxisSettings(AxisSettings domainAxisSettings) {
		AxisSettings old = getDomainAxisSettings();
		this.domainAxisSettings = domainAxisSettings;
		getEventSupport().firePropertyChange(PROPERTY_domainAxisSettings, old, getDomainAxisSettings());
	}

	/**
	 * @return the rangeAxisSettings
	 */
	public AxisSettings getRangeAxisSettings() {
		return rangeAxisSettings;
	}

	/**
	 * @param rangeAxisSettings the rangeAxisSettings to set
	 */
	public void setRangeAxisSettings(AxisSettings rangeAxisSettings) {
		AxisSettings old = getRangeAxisSettings();
		this.rangeAxisSettings = rangeAxisSettings;
		getEventSupport().firePropertyChange(PROPERTY_rangeAxisSettings, old, getRangeAxisSettings());
	}

}

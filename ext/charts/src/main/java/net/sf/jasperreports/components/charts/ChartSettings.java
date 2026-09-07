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
package net.sf.jasperreports.components.charts;

import java.awt.Color;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.charts.type.EdgeEnum;
import net.sf.jasperreports.components.spiderchart.StandardChartSettings;
import net.sf.jasperreports.engine.JRAnchor;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.JRHyperlink;
import net.sf.jasperreports.engine.xml.JRXmlConstants;


/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
@JsonPropertyOrder({
	"showLegend",
	JRXmlConstants.ATTRIBUTE_backcolor,
	"titlePosition",
	"titleColor",
	"subtitleColor",
	"legendColor",
	"legendBackgroundColor",
	"legendPosition",
	JRXmlConstants.ATTRIBUTE_renderType,
	JRXmlConstants.ATTRIBUTE_customizerClass,
	JRXmlConstants.ATTRIBUTE_bookmarkLevel,
	JRXmlConstants.ATTRIBUTE_hyperlinkType,
	JRXmlConstants.ATTRIBUTE_hyperlinkTarget,
	"linkType",
	"linkTarget",
	JRXmlConstants.ELEMENT_titleExpression,
	"titleFont",
	JRXmlConstants.ELEMENT_subtitleExpression,
	"subtitleFont",
	"legendFont",
	JRXmlConstants.ELEMENT_anchorNameExpression,
	JRXmlConstants.ELEMENT_bookmarkLevelExpression,
	JRXmlConstants.ELEMENT_hyperlinkReferenceExpression,
	JRXmlConstants.ELEMENT_hyperlinkWhenExpression,
	JRXmlConstants.ELEMENT_hyperlinkAnchorExpression,
	JRXmlConstants.ELEMENT_hyperlinkPageExpression,
	JRXmlConstants.ELEMENT_hyperlinkTooltipExpression,
	JRXmlConstants.ELEMENT_hyperlinkParameter
	})
@JsonDeserialize(as = StandardChartSettings.class)
public interface ChartSettings extends JRAnchor, JRHyperlink, Serializable
{

	/**
	 * 
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Boolean getShowLegend();

	/**
	 *
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Color getBackcolor();

	/**
	 *
	 */
	public JRFont getTitleFont();

	/**
	 *
	 */
	@JacksonXmlProperty(isAttribute = true)
	public EdgeEnum getTitlePosition();

	/**
	 *
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Color getTitleColor();

	/**
	 *
	 */
	public JRFont getSubtitleFont();

	/**
	 *
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Color getSubtitleColor();

	/**
	 *
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Color getLegendBackgroundColor(); 

	/**
	 *
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Color getLegendColor();
	
	/**
	 *
	 */
	public JRFont getLegendFont();

	/**
	 *
	 */
	@JacksonXmlProperty(isAttribute = true)
	public EdgeEnum getLegendPosition();


	/**
	 *
	 */
	public JRExpression getTitleExpression();
 
	/**
	 *
	 */
	public JRExpression getSubtitleExpression();

	/**
	 *
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getRenderType();
	
	/**
	 * Gets a user specified chart customizer class name.
	 * @see ChartCustomizer
 	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getCustomizerClass();

}

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
package net.sf.jasperreports.components.charts;

import java.awt.Color;

import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.charts.fill.ChartsFillObjectFactory;
import net.sf.jasperreports.charts.type.EdgeEnum;
import net.sf.jasperreports.components.spiderchart.SpiderChartCompiler;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.JRHyperlinkParameter;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.type.HyperlinkTargetEnum;
import net.sf.jasperreports.engine.type.HyperlinkTypeEnum;


/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class FillChartSettings implements ChartSettings
{

	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	/**
	 *
	 */
	protected ChartSettings parent;

	/**
	 *
	 */
	public FillChartSettings(ChartSettings chartSettings, ChartsFillObjectFactory factory)
	{
		factory.getParent().put(chartSettings, this);
		parent = chartSettings;
	}

	/**
	 * @see net.sf.jasperreports.engine.JRAnchor#getAnchorNameExpression()
	 */
	@Override
	public JRExpression getAnchorNameExpression() {
		return parent.getAnchorNameExpression();
	}
	
	/**
	 * @see net.sf.jasperreports.engine.JRAnchor#getBookmarkLevelExpression()
	 */
	@Override
	public JRExpression getBookmarkLevelExpression() {
		return parent.getBookmarkLevelExpression();
	}

	/**
	 * @see net.sf.jasperreports.engine.JRAnchor#getBookmarkLevel()
	 */
	@Override
	public int getBookmarkLevel() {
		
		return parent.getBookmarkLevel();
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		
		ChartSettings clone = null;
		
		try
		{
			clone = (ChartSettings)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new JRRuntimeException(e);
		}
		return clone;
	}

	/**
	 * @see net.sf.jasperreports.components.charts.ChartSettings#getLegendBackgroundColor()
	 */
	@Override
	public Color getLegendBackgroundColor() {
		
		return parent.getLegendBackgroundColor();
	}

	/**
	 * @see net.sf.jasperreports.components.charts.ChartSettings#getLegendColor()
	 */
	@Override
	public Color getLegendColor() {
		
		return parent.getLegendColor();
	}

	/**
	 * @see net.sf.jasperreports.components.charts.ChartSettings#getLegendFont()
	 */
	@Override
	public JRFont getLegendFont() {
		
		return parent.getLegendFont();
	}

	/**
	 * @see net.sf.jasperreports.components.charts.ChartSettings#getLegendPosition()
	 */
	@Override
	public EdgeEnum getLegendPosition() {
		
		return parent.getLegendPosition();
	}

	/**
	 * @see net.sf.jasperreports.components.charts.ChartSettings#getRenderType()
	 */
	@Override
	public String getRenderType() {
		
		return parent.getRenderType() == null ? JRChart.RENDER_TYPE_DRAW : parent.getRenderType();
	}

	/**
	 * @see net.sf.jasperreports.components.charts.ChartSettings#getShowLegend()
	 */
	@Override
	public Boolean getShowLegend() {
		
		return parent.getShowLegend();
	}

	/**
	 * @see net.sf.jasperreports.components.charts.ChartSettings#getSubtitleColor()
	 */
	@Override
	public Color getSubtitleColor() {
		
		return parent.getSubtitleColor();
	}

	/**
	 * @see net.sf.jasperreports.components.charts.ChartSettings#getSubtitleExpression()
	 */
	@Override
	public JRExpression getSubtitleExpression() {
		
		return parent.getSubtitleExpression();
	}

	/**
	 * @see net.sf.jasperreports.components.charts.ChartSettings#getSubtitleFont()
	 */
	@Override
	public JRFont getSubtitleFont() {
		
		return parent.getSubtitleFont();
	}

	/**
	 * @see net.sf.jasperreports.components.charts.ChartSettings#getTitleColor()
	 */
	@Override
	public Color getTitleColor() {
		
		return parent.getTitleColor();
	}

	/**
	 * @see net.sf.jasperreports.components.charts.ChartSettings#getTitleExpression()
	 */
	@Override
	public JRExpression getTitleExpression() {
		
		return parent.getTitleExpression();
	}

	/**
	 * @see net.sf.jasperreports.components.charts.ChartSettings#getTitleFont()
	 */
	@Override
	public JRFont getTitleFont() {
		
		return parent.getTitleFont();
	}

	/**
	 * @see net.sf.jasperreports.components.charts.ChartSettings#getTitlePosition()
	 */
	@Override
	public EdgeEnum getTitlePosition() {
		
		return parent.getTitlePosition();
	}

	/**
	 * @see net.sf.jasperreports.engine.JRHyperlink#getHyperlinkAnchorExpression()
	 */
	@Override
	public JRExpression getHyperlinkAnchorExpression() {
		
		return parent.getHyperlinkAnchorExpression();
	}

	/**
	 * @see net.sf.jasperreports.engine.JRHyperlink#getHyperlinkPageExpression()
	 */
	@Override
	public JRExpression getHyperlinkPageExpression() {
		
		return parent.getHyperlinkPageExpression();
	}

	/**
	 * @see net.sf.jasperreports.engine.JRHyperlink#getHyperlinkParameters()
	 */
	@Override
	public JRHyperlinkParameter[] getHyperlinkParameters() {
		
		return parent.getHyperlinkParameters();
	}

	/**
	 * @see net.sf.jasperreports.engine.JRHyperlink#getHyperlinkReferenceExpression()
	 */
	@Override
	public JRExpression getHyperlinkReferenceExpression() {
		
		return parent.getHyperlinkReferenceExpression();
	}

	/**
	 * @see net.sf.jasperreports.engine.JRHyperlink#getHyperlinkWhenExpression()
	 */
	@Override
	public JRExpression getHyperlinkWhenExpression() {
		
		return parent.getHyperlinkWhenExpression();
	}
	
	/**
	 * @see net.sf.jasperreports.engine.JRHyperlink#getHyperlinkTarget()
	 */
	@Override
	public HyperlinkTargetEnum getHyperlinkTarget() {
		
		return parent.getHyperlinkTarget();
	}
	
	/**
	 * @see net.sf.jasperreports.engine.JRHyperlink#getHyperlinkTooltipExpression()
	 */
	@Override
	public JRExpression getHyperlinkTooltipExpression() {
		
		return parent.getHyperlinkTooltipExpression();
	}

	/**
	 * @see net.sf.jasperreports.engine.JRHyperlink#getHyperlinkType()
	 */
	@Override
	public HyperlinkTypeEnum getHyperlinkType() {
		
		return parent.getHyperlinkType();
	}

	/**
	 * @see net.sf.jasperreports.engine.JRHyperlink#getLinkTarget()
	 */
	@Override
	public String getLinkTarget() {
		
		return parent.getLinkTarget();
	}

	/**
	 * @see net.sf.jasperreports.engine.JRHyperlink#getLinkType()
	 */
	@Override
	public String getLinkType() {
		
		return parent.getLinkType();
	}
	
	/**
	 * @see net.sf.jasperreports.components.charts.ChartSettings#getLegendColor()
	 */
	@Override
	public Color getBackcolor() {
		
		return parent.getBackcolor();
	}

	@Override
	public String getCustomizerClass()
	{
		return parent.getCustomizerClass();
	}
	
	/**
	 *
	 */
	public void collectExpressions(JRExpressionCollector collector)
	{
		SpiderChartCompiler.collectExpressions(this, collector);
	}

}

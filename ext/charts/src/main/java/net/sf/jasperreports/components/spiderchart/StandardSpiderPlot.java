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

import java.awt.Color;

import net.sf.jasperreports.components.spiderchart.type.SpiderRotationEnum;
import net.sf.jasperreports.components.spiderchart.type.TableOrderEnum;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;


/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class StandardSpiderPlot implements SpiderPlot, JRChangeEventsSupport
{


	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public static final String PROPERTY_LABEL_FONT = "labelFont";
	
	public static final String PROPERTY_MAX_VALUE_EXPRESSION = "maxValueExpression";
	
	public static final String PROPERTY_ROTATION = "rotation";
	
	public static final String PROPERTY_TABLE_ORDER = "tableOrder";
	
	public static final String PROPERTY_WEB_FILLED = "webFilled";
	
	public static final String PROPERTY_START_ANGLE = "startAngle";
	
	public static final String PROPERTY_HEAD_PERCENT = "headPercent";
	
	public static final String PROPERTY_INTERIOR_GAP = "interiorGap";
	
	public static final String PROPERTY_AXIS_LINE_COLOR = "axisLineColor";
	
	public static final String PROPERTY_AXIS_LINE_WIDTH = "axisLineWidth";
	
	public static final String PROPERTY_LABEL_GAP = "labelGap";
	
	public static final String PROPERTY_LABEL_COLOR = "labelColor";

	public static final String PROPERTY_BACKCOLOR = "backcolor";

	public static final String PROPERTY_BACKGROUND_ALPHA = "backgroundAlpha";
	
	public static final String PROPERTY_FOREGROUND_ALPHA = "foregroundAlpha";

	
	protected JRFont labelFont;
	protected JRExpression maxValueExpression;
	protected SpiderRotationEnum rotation;
	protected TableOrderEnum tableOrder;
	protected Boolean webFilled;
	protected Double startAngle;
	protected Double headPercent;
	protected Double interiorGap;
	protected Color axisLineColor;
	protected Float axisLineWidth;
	protected Double labelGap;
	protected Color labelColor;

	protected Color backcolor;
	protected Float backgroundAlpha;
	protected Float foregroundAlpha;

	/**
	 *
	 */
	public StandardSpiderPlot()
	{
	}

	/**
	 *
	 */
	public StandardSpiderPlot(SpiderPlot spiderPlot, JRBaseObjectFactory factory)
	{
		labelFont = spiderPlot.getLabelFont();
		rotation = spiderPlot.getRotation();
		tableOrder = spiderPlot.getTableOrder();
		webFilled = spiderPlot.getWebFilled();
		startAngle = spiderPlot.getStartAngle();
		headPercent = spiderPlot.getHeadPercent();
		interiorGap = spiderPlot.getInteriorGap();
		axisLineColor = spiderPlot.getAxisLineColor();
		axisLineWidth = spiderPlot.getAxisLineWidth();
		labelGap = spiderPlot.getLabelGap();
		labelColor = spiderPlot.getLabelColor();
		backcolor = spiderPlot.getBackcolor();
		backgroundAlpha = spiderPlot.getBackgroundAlpha();
		foregroundAlpha = spiderPlot.getForegroundAlpha();

		maxValueExpression = factory.getExpression( spiderPlot.getMaxValueExpression() );
	}

	/**
	 * @return the labelFont
	 */
	@Override
	public JRFont getLabelFont() {
		return labelFont;
	}

	/**
	 * @return the maxValueExpression
	 */
	@Override
	public JRExpression getMaxValueExpression() {
		return maxValueExpression;
	}

	/**
	 * @return the rotation
	 */
	@Override
	public SpiderRotationEnum getRotation() {
		return rotation;
	}

	/**
	 * @return the tableOrder
	 */
	@Override
	public TableOrderEnum getTableOrder() {
		return tableOrder;
	}

	/**
	 * @return the webFilled
	 */
	@Override
	public Boolean getWebFilled() {
		return webFilled;
	}

	/**
	 * @return the startAngle
	 */
	@Override
	public Double getStartAngle() {
		return startAngle;
	}

	/**
	 * @return the headPercent
	 */
	@Override
	public Double getHeadPercent() {
		return headPercent;
	}

	/**
	 * @return the interiorGap
	 */
	@Override
	public Double getInteriorGap() {
		return interiorGap;
	}

	/**
	 * @return the axisLineColor
	 */
	@Override
	public Color getAxisLineColor() {
		return axisLineColor;
	}

	/**
	 * @return the axisLineWidth
	 */
	@Override
	public Float getAxisLineWidth() {
		return axisLineWidth;
	}

	/**
	 * @return the labelGap
	 */
	@Override
	public Double getLabelGap() {
		return labelGap;
	}

	/**
	 * @return the labelColor
	 */
	@Override
	public Color getLabelColor() {
		return labelColor;
	}

	/**
	 *
	 */
	public void setLabelFont(JRFont labelFont)
	{
		Object old = this.labelFont;
		this.labelFont = labelFont;
		getEventSupport().firePropertyChange(PROPERTY_LABEL_FONT, old, this.labelFont);
	}

	/**
	 *
	 */
	public void setMaxValueExpression(JRExpression maxValueExpression)
	{
		Object old = this.maxValueExpression;
		this.maxValueExpression = maxValueExpression;
		getEventSupport().firePropertyChange(PROPERTY_MAX_VALUE_EXPRESSION, old, this.maxValueExpression);
	}

	/**
	 *
	 */
	public void setRotation(SpiderRotationEnum rotation)
	{
		Object old = this.rotation;
		this.rotation = rotation;
		getEventSupport().firePropertyChange(PROPERTY_ROTATION, old, this.rotation);
	}

	/**
	 *
	 */
	public void setTableOrder(TableOrderEnum tableOrder)
	{
		Object old = this.tableOrder;
		this.tableOrder = tableOrder;
		getEventSupport().firePropertyChange(PROPERTY_TABLE_ORDER, old, this.tableOrder);
	}

	/**
	 *
	 */
	public void setWebFilled(Boolean webFilled)
	{
		Object old = this.webFilled;
		this.webFilled = webFilled;
		getEventSupport().firePropertyChange(PROPERTY_WEB_FILLED, old, this.webFilled);
	}


	/**
	 *
	 */
	public void setStartAngle(Double startAngle)
	{
		Object old = this.startAngle;
		this.startAngle = startAngle;
		getEventSupport().firePropertyChange(PROPERTY_START_ANGLE, old, this.startAngle);
	}


	/**
	 *
	 */
	public void setHeadPercent(Double headPercent)
	{
		Object old = this.headPercent;
		this.headPercent = headPercent;
		getEventSupport().firePropertyChange(PROPERTY_HEAD_PERCENT, old, this.headPercent);
	}


	/**
	 *
	 */
	public void setInteriorGap(Double interiorGap)
	{
		Object old = this.interiorGap;
		this.interiorGap = interiorGap;
		getEventSupport().firePropertyChange(PROPERTY_INTERIOR_GAP, old, this.interiorGap);
	}


	/**
	 *
	 */
	public void setAxisLineColor(Color axisLineColor)
	{
		Object old = this.axisLineColor;
		this.axisLineColor = axisLineColor;
		getEventSupport().firePropertyChange(PROPERTY_AXIS_LINE_COLOR, old, this.axisLineColor);
	}

	
	/**
	 *
	 */
	public void setAxisLineWidth(Float axisLineWidth)
	{
		Object old = this.axisLineWidth;
		this.axisLineWidth = axisLineWidth;
		getEventSupport().firePropertyChange(PROPERTY_AXIS_LINE_WIDTH, old, this.axisLineWidth);
	}

	/**
	 *
	 */
	public void setLabelGap(Double labelGap)
	{
		Object old = this.labelGap;
		this.labelGap = labelGap;
		getEventSupport().firePropertyChange(PROPERTY_LABEL_GAP, old, this.labelGap);
	}


	/**
	 *
	 */
	public void setLabelColor(Color labelColor)
	{
		Object old = this.labelColor;
		this.labelColor = labelColor;
		getEventSupport().firePropertyChange(PROPERTY_LABEL_COLOR, old, this.labelColor);
	}
	
	/**
	 * @return the backcolor
	 */
	@Override
	public Color getBackcolor() {
		return backcolor;
	}

	/**
	 * @param backcolor the backcolor to set
	 */
	public void setBackcolor(Color backcolor) {
		Object old = this.backcolor;
		this.backcolor = backcolor;
		getEventSupport().firePropertyChange(PROPERTY_BACKCOLOR, old, this.backcolor);
	}

	/**
	 * @return the backgroundAlpha
	 */
	@Override
	public Float getBackgroundAlpha() {
		return backgroundAlpha;
	}

	/**
	 * @param backgroundAlpha the backgroundAlpha to set
	 */
	public void setBackgroundAlpha(Float backgroundAlpha) {
		Object old = this.backgroundAlpha;
		this.backgroundAlpha = backgroundAlpha;
		getEventSupport().firePropertyChange(PROPERTY_BACKGROUND_ALPHA, old, this.backgroundAlpha);
	}

	/**
	 * @return the foregroundAlpha
	 */
	@Override
	public Float getForegroundAlpha() {
		return foregroundAlpha;
	}

	/**
	 * @param foregroundAlpha the foregroundAlpha to set
	 */
	public void setForegroundAlpha(Float foregroundAlpha) {
		Object old = this.foregroundAlpha;
		this.foregroundAlpha = foregroundAlpha;
		getEventSupport().firePropertyChange(PROPERTY_FOREGROUND_ALPHA, old, this.foregroundAlpha);
	}

	public void collectExpressions(JRExpressionCollector collector)
	{
		SpiderChartCompiler.collectExpressions(this, collector);
	}

	@Override
	public Object clone()
	{
		StandardSpiderPlot clone = null;
		
		try
		{
			clone = (StandardSpiderPlot)super.clone();
			clone.eventSupport = null;
		}
		catch (CloneNotSupportedException e)
		{
			throw new JRRuntimeException(e);
		}
		
		return clone;
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
	
}

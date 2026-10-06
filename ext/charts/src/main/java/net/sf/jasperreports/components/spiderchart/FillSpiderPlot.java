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

import net.sf.jasperreports.charts.fill.ChartsFillObjectFactory;
import net.sf.jasperreports.components.spiderchart.type.SpiderRotationEnum;
import net.sf.jasperreports.components.spiderchart.type.TableOrderEnum;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRFont;


/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class FillSpiderPlot implements SpiderPlot
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	

	/**
	 *
	 */
	protected SpiderPlot parent;

	/**
	 *
	 */
	public FillSpiderPlot(SpiderPlot spiderPlot, ChartsFillObjectFactory factory)
	{
		factory.getParent().put(spiderPlot, this);
		parent = spiderPlot;
	}
		

	/**
	 * @return the labelFont
	 */
	@Override
	public JRFont getLabelFont() {
		return parent.getLabelFont();
	}

	/**
	 * @return the maxValueExpression
	 */
	@Override
	public JRExpression getMaxValueExpression() {
		return parent.getMaxValueExpression();
	}

	/**
	 * @return the rotation
	 */
	@Override
	public SpiderRotationEnum getRotation() {
		return parent.getRotation();
	}

	/**
	 * @return the tableOrder
	 */
	@Override
	public TableOrderEnum getTableOrder() {
		return parent.getTableOrder();
	}

	/**
	 * @return the webFilled
	 */
	@Override
	public Boolean getWebFilled() {
		return parent.getWebFilled();
	}

	/**
	 * @return the startAngle
	 */
	@Override
	public Double getStartAngle() {
		return parent.getStartAngle();
	}

	/**
	 * @return the headPercent
	 */
	@Override
	public Double getHeadPercent() {
		return parent.getHeadPercent();
	}

	/**
	 * @return the interiorGap
	 */
	@Override
	public Double getInteriorGap() {
		return parent.getInteriorGap();
	}

	/**
	 * @return the axisLineColor
	 */
	@Override
	public Color getAxisLineColor() {
		return parent.getAxisLineColor();
	}

	/**
	 * @return the axisLineWidth
	 */
	@Override
	public Float getAxisLineWidth() {
		return parent.getAxisLineWidth();
	}

	/**
	 * @return the labelGap
	 */
	@Override
	public Double getLabelGap() {
		return parent.getLabelGap();
	}

	/**
	 * @return the labelColor
	 */
	@Override
	public Color getLabelColor() {
		return parent.getLabelColor();
	}


	@Override
	public Color getBackcolor() {
		return parent.getBackcolor();
	}


	@Override
	public Float getBackgroundAlpha() {
		return parent.getBackgroundAlpha();
	}


	@Override
	public Float getForegroundAlpha() {
		return parent.getForegroundAlpha();
	}

	@Override
	public Object clone() 
	{
		throw new UnsupportedOperationException();
	}
}

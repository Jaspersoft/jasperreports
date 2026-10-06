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
package net.sf.jasperreports.charts.fill;

import java.awt.Color;

import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.charts.JRValueDisplay;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.fill.JRFillObjectFactory;

/**
 * @author Barry Klawans (bklawans@users.sourceforge.net)
 */
public class JRFillValueDisplay implements JRValueDisplay
{

	/**
	 *
	 */
	protected JRValueDisplay parent;

	/**
	 *
	 */
	protected JRChart chart;

	/**
	 *
	 */
	public JRFillValueDisplay(JRValueDisplay valueDisplay, JRFillObjectFactory factory)
	{
		factory.put(valueDisplay, this);

		parent = valueDisplay;
		
		chart = (JRChart)factory.getVisitResult(valueDisplay.getChart());
	}

	@Override
	public JRChart getChart()
	{
		return chart;
	}
	
	@Override
	public Color getColor()
	{
		return parent.getColor();
	}

	@Override
	public String getMask(){
		return parent.getMask();
	}

	@Override
	public JRFont getFont()
	{
		return parent.getFont();
	}
	
	@Override
	public Object clone() 
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public JRValueDisplay clone(JRChart parentChart)
	{
		throw new UnsupportedOperationException();
	}
}

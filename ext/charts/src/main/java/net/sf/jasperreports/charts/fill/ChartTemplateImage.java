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

import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JROrigin;
import net.sf.jasperreports.engine.base.JRBasePen;
import net.sf.jasperreports.engine.fill.JRTemplateImage;
import net.sf.jasperreports.engine.fill.JRTemplatePrintImage;
import net.sf.jasperreports.engine.type.FillEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @see JRTemplatePrintImage
 */
public class ChartTemplateImage extends JRTemplateImage
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	

	/**
	 *
	 */
	protected ChartTemplateImage(JROrigin origin, JRDefaultStyleProvider defaultStyleProvider, JRChart chart)
	{
		super(origin, defaultStyleProvider);
		
		setChart(chart);
	}

	/**
	 *
	 */
	protected void setChart(JRChart chart)
	{
		super.setElement(chart);
		
		linePen = new JRBasePen(this);
		
		getLinePen().setLineWidth(0f);
		setFill(FillEnum.SOLID);
		
		copyLineBox(chart.getLineBox());

		setLinkType(chart.getLinkType());
		setLinkTarget(chart.getLinkTarget());
	}
}

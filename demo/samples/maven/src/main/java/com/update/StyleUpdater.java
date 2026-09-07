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
package com.update;

import java.awt.Color;

import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.ReportUpdater;


/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class StyleUpdater implements ReportUpdater 
{
	
	@Override
	public JasperDesign update(JasperDesign jasperDesign)
	{
		JRDesignStyle style = (JRDesignStyle)jasperDesign.getStyles()[0];
		style.setForecolor(Color.BLUE);
		style.setFontSize(14f);
		style.setBold(Boolean.TRUE);
		return jasperDesign;
	}
}

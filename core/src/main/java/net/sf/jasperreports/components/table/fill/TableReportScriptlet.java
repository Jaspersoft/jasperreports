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
package net.sf.jasperreports.components.table.fill;

import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.fill.JRVerticalFiller;

/**
 * This scriptlet implementation for table component ended up not being used as a scriptlet,
 * because its prior technique to detect the presence of at least one detail band on the page
 * was not accurate in case the detail was overflowing onto the new page.
 * 
 * Its current technique uses a flag in the report filler to detect the presence of a detail band
 * and thus this scriptlet implementation itself is only used as a vehicle to add a built-in parameter
 * to the table component subreport, to give access to the filler and its internal the flag.  
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class TableReportScriptlet extends JRDefaultScriptlet
{

	public boolean hasDetailOnPage()
	{
		return ((JRVerticalFiller)dataset.getFiller()).hasDetailOnPage();
	}
	
}

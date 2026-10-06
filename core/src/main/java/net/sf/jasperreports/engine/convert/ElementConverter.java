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

/*
 * Contributors:
 * Eugene D - eugenedruy@users.sourceforge.net 
 * Adrian Jackson - iapetus@users.sourceforge.net
 * David Taylor - exodussystems@users.sourceforge.net
 * Lars Kristensen - llk@users.sourceforge.net
 */
package net.sf.jasperreports.engine.convert;

import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRGraphicElement;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.base.JRBasePrintGraphicElement;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class ElementConverter
{

	/**
	 *
	 */
	public abstract JRPrintElement convert(ReportConverter reportConverter, JRElement element);


	/**
	 * 
	 */
	protected void copyElement(ReportConverter reportConverter, JRElement element, JRPrintElement printElement)
	{
		reportConverter.copyBaseAttributes(element, printElement);
		
		JRPropertiesUtil.getInstance(reportConverter.getJasperReportsContext()).transferProperties(element, printElement, JasperPrint.PROPERTIES_PRINT_TRANSFER_PREFIX);
	}

	
	/**
	 *
	 */
	protected void copyGraphicElement(ReportConverter reportConverter, JRGraphicElement element, JRBasePrintGraphicElement printElement)
	{
		copyElement(reportConverter, element, printElement);
		
		printElement.copyPen(element.getLinePen());
		
		printElement.setFill(element.getOwnFill());
	}


}

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
package net.sf.jasperreports.engine.util;

import java.util.Iterator;
import java.util.Map;

import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.PrintPart;
import net.sf.jasperreports.engine.PrintParts;
import net.sf.jasperreports.engine.base.StandardPrintParts;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class PartsUtil
{

	public static PartsUtil instance(JasperReportsContext jasperReportsContext)
	{
		return new PartsUtil(jasperReportsContext);
	}
	
	private final JasperReportsContext jasperReportsContext;

	public PartsUtil(JasperReportsContext jasperReportsContext)
	{
		this.jasperReportsContext = jasperReportsContext;
	}
	
	public PrintParts getVisibleParts(JasperPrint jasperPrint)
	{
		StandardPrintParts visibleParts = new StandardPrintParts();
		if (jasperPrint.hasParts())
		{
			JRPropertiesUtil properties = JRPropertiesUtil.getInstance(jasperReportsContext);
			for (Iterator<Map.Entry<Integer, PrintPart>> iterator = jasperPrint.getParts().partsIterator(); iterator.hasNext();)
			{
				Map.Entry<Integer, PrintPart> partEntry = iterator.next();
				PrintPart part = partEntry.getValue();
				if (properties.getBooleanProperty(part, PrintPart.PROPERTY_VISIBLE, true))
				{
					visibleParts.addPart(partEntry.getKey(), part);
				}
			}		
		}
		return visibleParts;
	}
	
}

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
package net.sf.jasperreports.engine.export;

import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JasperReportsContext;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class GenericElementsFilterDecorator implements ResetableExporterFilter
{

	private final ExporterFilter filter;
	private final String exporterKey;
	private final GenericElementHandlerEnviroment handlerEnvironment;
	
	public GenericElementsFilterDecorator(JasperReportsContext jasperReportsContext, String exporterKey, ExporterFilter filter)
	{
		this.filter = filter;
		this.exporterKey = exporterKey;
		
		this.handlerEnvironment = GenericElementHandlerEnviroment.getInstance(jasperReportsContext);
	}

	@Override
	public boolean isToExport(JRPrintElement element)
	{
		if (element instanceof JRGenericPrintElement)
		{
			JRGenericPrintElement genericElement = (JRGenericPrintElement) element;
			GenericElementHandler handler = handlerEnvironment.getElementHandler(
					genericElement.getGenericType(), exporterKey);
			if (handler == null || !handler.toExport(genericElement))
			{
				return false;
			}
		}
		
		return filter == null || filter.isToExport(element);
	}

	@Override
	public void reset()
	{
		if (filter instanceof ResetableExporterFilter)
		{
			((ResetableExporterFilter) filter).reset();
		}
	}

}

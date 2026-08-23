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
package net.sf.jasperreports.engine.export;

import java.util.List;

import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRRuntimeException;

/**
 * An exporter filter that consists of several exporter filter, and filters
 * elements through each of them.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see #isToExport(JRPrintElement)
 */
public class ExporterFilterContainer implements ResetableExporterFilter
{
	public static final String EXCEPTION_MESSAGE_KEY_NULL_FILTERS_LIST = "export.filter.null.filters.list";

	private final List<ExporterFilter> filters;
	
	/**
	 * Constructs a container for a list of exporter filters.
	 * 
	 * @param filters the list of filters
	 */
	public ExporterFilterContainer(List<ExporterFilter> filters)
	{
		if (filters == null)
		{
			throw 
			new JRRuntimeException(
				EXCEPTION_MESSAGE_KEY_NULL_FILTERS_LIST,
				(Object[])null);
		}
		
		this.filters = filters;
	}

	/**
	 * Returns <code>true</code> if the element is not filtered by any of
	 * the contained filters.
	 */
	@Override
	public boolean isToExport(JRPrintElement element)
	{
		boolean export = true;
		for (ExporterFilter filter : filters)
		{
			if (!filter.isToExport(element))
			{
				export = false;
				break;
			}
		}
		return export;
	}

	@Override
	public void reset()
	{
		for (ExporterFilter filter : filters)
		{
			if (filter instanceof ResetableExporterFilter)
			{
				((ResetableExporterFilter) filter).reset();
			}
		}
	}

}

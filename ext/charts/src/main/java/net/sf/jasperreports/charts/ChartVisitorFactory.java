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
package net.sf.jasperreports.charts;

import java.util.List;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRVisitor;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net) 
 */
public final class ChartVisitorFactory
{
	private static final ChartVisitorFactory INSTANCE = new ChartVisitorFactory();

	/**
	 * 
	 */
	private ChartVisitorFactory()
	{
	}

	/**
	 * 
	 */
	public static ChartVisitorFactory getInstance()
	{
		return INSTANCE;
	}

	/**
	 * 
	 */
	public ChartVisitor getChartVisitor(JRVisitor visitor)
	{
		List<ElementVisitorAdapter> adapters = DefaultJasperReportsContext.getInstance().getExtensions(ElementVisitorAdapter.class);
		for (ElementVisitorAdapter adapter : adapters)
		{
			ChartVisitor chartVisitor = adapter.getChartVisitor(visitor);
			if (chartVisitor != null)
			{
				return chartVisitor;
			}
		}
		return  null;
	}

}

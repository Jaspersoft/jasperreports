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
package net.sf.jasperreports.engine;

import java.util.Arrays;
import java.util.List;

import net.sf.jasperreports.engine.fill.DatasetFillContext;

/**
 * A dataset filter that wraps several other filters and applies them as a conjunction.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class CompositeDatasetFilter implements DatasetFilter
{

	/**
	 * Combines two filter in a conjunction.
	 * 
	 * @param filter the first filter
	 * @param other the second filter
	 */
	public static DatasetFilter combine(DatasetFilter filter, DatasetFilter other)
	{
		DatasetFilter combined;
		if (filter == null)
		{
			combined = other;
		}
		else if (other == null)
		{
			combined = filter;
		}
		else
		{
			combined = new CompositeDatasetFilter(filter, other);
		}
		return combined;
	}
	
	private List<? extends DatasetFilter> filters;
	
	/**
	 * Creates a conjunction for several filters.
	 * 
	 * @param filters the filters
	 */
	public CompositeDatasetFilter(DatasetFilter ... filters)
	{
		this.filters = Arrays.asList(filters);
	}

	public CompositeDatasetFilter(List<? extends DatasetFilter> filters)
	{
		this.filters = filters;
	}
	
	@Override
	public void init(DatasetFillContext context)
	{
		for (DatasetFilter filter : filters)
		{
			filter.init(context);
		}
	}

	@Override
	public boolean matches(EvaluationType evaluation)
	{
		boolean matches = true;
		for (DatasetFilter filter : filters)
		{
			if (!filter.matches(evaluation))
			{
				matches = false;
				break;
			}
		}

		return matches;
	}
	
	public List<? extends DatasetFilter> getFilters() {
		return filters;
	}

	public void setFilters(List<? extends DatasetFilter> filters) {
		this.filters = filters;
	}

}

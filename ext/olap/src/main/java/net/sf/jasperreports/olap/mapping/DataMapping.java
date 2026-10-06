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
package net.sf.jasperreports.olap.mapping;

import java.util.Iterator;
import java.util.List;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
@SuppressWarnings("rawtypes")
public class DataMapping implements Mapping
{
	private final boolean formatted;
	private final List filter;
	private final List positions;
	
	public DataMapping(boolean formatted, List filter, List positions)
	{
		this.formatted = formatted;
		this.filter = filter;
		this.positions = positions;
	}
	
	
	public List getFilter()
	{
		return filter;
	}
	
	public boolean isFormatted()
	{
		return formatted;
	}
	
	public List getPositions()
	{
		return positions;
	}

	@Override
	public Iterator memberMappings()
	{
		return filter == null ? new EmptyIt() : filter.iterator();
	}
	
	protected static class EmptyIt implements Iterator
	{
		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasNext()
		{
			return false;
		}

		@Override
		public Object next()
		{
			return null;
		}
	}
}

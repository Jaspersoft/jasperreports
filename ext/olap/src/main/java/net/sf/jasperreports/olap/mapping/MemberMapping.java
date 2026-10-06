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


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class MemberMapping implements Mapping
{
	private final Member member;
	private final MemberProperty property;
	
	public MemberMapping(Member member, MemberProperty property)
	{
		this.member = member;
		this.property = property;
	}

	public Member getMember()
	{
		return member;
	}

	public MemberProperty getProperty()
	{
		return property;
	}

	@Override
	public Iterator memberMappings()
	{
		return new SingleIt(member);
	}
	
	protected static class SingleIt implements Iterator
	{
		boolean first;
		final Object o;
		
		SingleIt (Object o)
		{
			this.o = o;
			first = true;
		}
		
		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasNext()
		{
			boolean next = first;
			first = false;
			return next;
		}

		@Override
		public Object next()
		{
			return o;
		}
	}
}

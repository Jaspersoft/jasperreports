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

import net.sf.jasperreports.olap.result.JROlapMember;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class Member
{
	private final TuplePosition pos;
	private final MemberDepth depth;
	
	public Member(TuplePosition pos, MemberDepth depth)
	{
		this.pos = pos;
		this.depth = depth;
	}

	public Axis getAxis()
	{
		return pos.getAxis();
	}

	public MemberDepth getDepth()
	{
		return depth;
	}

	public TuplePosition getPosition()
	{
		return pos;
	}
	
	public boolean matches(JROlapMember member)
	{
		boolean match;
		int memberDepth = member.getDepth();
		
		if (depth == null)
		{
			match = true;
		}
		else
		{
			match = memberDepth == depth.getDepth();
		}
		return match;
	}

	public JROlapMember ancestorMatch(JROlapMember member)
	{
		JROlapMember ancestor;
		int memberDepth = member.getDepth();
		
		if (depth == null)
		{
			ancestor = member;
		}
		else if (depth.getDepth() <= memberDepth)
		{
			ancestor = member;
			for (int i = depth.getDepth(); i < memberDepth && ancestor != null; ++i)
			{
				ancestor = ancestor.getParentMember();
			}
		}
		else
		{
			ancestor = null;
		}
		
		return ancestor;
	}
}

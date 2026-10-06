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
package net.sf.jasperreports.olap.olap4j;

import java.util.Iterator;

import net.sf.jasperreports.olap.result.JROlapMember;
import net.sf.jasperreports.olap.result.JROlapMemberTuple;

import org.olap4j.Position;
import org.olap4j.metadata.Member;


/**
 * @author swood
 */
public class Olap4jTuple implements JROlapMemberTuple
{

	private final Olap4jMember[] members;

	public Olap4jTuple(Position position, Olap4jFactory factory)
	{
		members = new Olap4jMember[position.getMembers().size()];
		int idx = 0;
		for (Iterator<Member> it = position.getMembers().iterator(); it.hasNext(); ++idx)
		{
			Member member = it.next();
			members[idx] = factory.createMember(member);
		}
	}

	@Override
	public JROlapMember[] getMembers()
	{
		return members;
	}

}

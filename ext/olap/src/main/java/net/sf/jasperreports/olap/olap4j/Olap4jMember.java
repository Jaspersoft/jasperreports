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

import net.sf.jasperreports.olap.result.JROlapMember;

import org.olap4j.metadata.Member;
import org.olap4j.metadata.NamedList;
import org.olap4j.metadata.Property;


/**
 * @author swood
 */
public class Olap4jMember implements JROlapMember
{

	private final Member member;
	private final Olap4jMember parent;

	public Olap4jMember(Member member, Olap4jFactory factory)
	{
		this.member = member;
		this.parent = factory.createMember(member.getParentMember());
	}
	
	@Override
	public int getDepth()
	{
		return member.getDepth();
	}

	@Override
	public String getName()
	{
		return member.getName();
	}

	@Override
	public JROlapMember getParentMember()
	{
		return parent;
	}

	@Override
	public Object getPropertyValue(String propertyName)
	{
		NamedList<Property> properties = member.getProperties();
		return properties.get(propertyName);
	}

	@Override
	public String getUniqueName()
	{
		return member.getUniqueName();
	}

	@Override
	public Object getMember()
	{
		return member;
	}

}

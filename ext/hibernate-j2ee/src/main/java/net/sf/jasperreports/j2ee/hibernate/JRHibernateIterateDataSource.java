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
package net.sf.jasperreports.j2ee.hibernate;

import java.util.Iterator;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRewindableDataSource;

/**
 * Hibernate data source that uses <code>org.hibernate.Query.iterate()</code>.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRHibernateIterateDataSource extends JRHibernateAbstractDataSource implements JRRewindableDataSource
{
	private Iterator<?> iterator;
	
	public JRHibernateIterateDataSource(JRHibernateQueryExecuter queryExecuter, boolean useFieldDescription)
	{
		super(queryExecuter, useFieldDescription, false);
		
		moveFirst();
	}

	@Override
	public boolean next() throws JRException
	{
		if (iterator != null && iterator.hasNext())
		{
			setCurrentRowValue(iterator.next());
			return true;
		}

		return false;
	}

	@Override
	public void moveFirst()
	{
		iterator = queryExecuter.iterate();
	}
}

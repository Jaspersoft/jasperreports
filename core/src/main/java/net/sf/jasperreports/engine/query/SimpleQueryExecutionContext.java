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
package net.sf.jasperreports.engine.query;

import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.repo.RepositoryContext;
import net.sf.jasperreports.repo.SimpleRepositoryContext;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class SimpleQueryExecutionContext implements QueryExecutionContext
{

	public static SimpleQueryExecutionContext of(JasperReportsContext jasperReportsContext)
	{
		return of(jasperReportsContext, SimpleRepositoryContext.of(jasperReportsContext));
	}

	public static SimpleQueryExecutionContext of(JasperReportsContext jasperReportsContext,
			RepositoryContext repositoryContext)
	{
		SimpleQueryExecutionContext context = new SimpleQueryExecutionContext();
		context.setJasperReportsContext(jasperReportsContext);
		context.setRepositoryContext(repositoryContext);
		return context;
	}
	
	private JasperReportsContext jasperReportsContext;
	
	private RepositoryContext repositoryContext;
	
	@Override
	public JasperReportsContext getJasperReportsContext()
	{
		return jasperReportsContext;
	}

	public void setJasperReportsContext(JasperReportsContext jasperReportsContext)
	{
		this.jasperReportsContext = jasperReportsContext;
	}

	@Override
	public RepositoryContext getRepositoryContext()
	{
		return repositoryContext;
	}

	public void setRepositoryContext(RepositoryContext repositoryContext)
	{
		this.repositoryContext = repositoryContext;
	}

}

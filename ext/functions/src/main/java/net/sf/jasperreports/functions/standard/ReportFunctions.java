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
package net.sf.jasperreports.functions.standard;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.data.HierarchicalDataSource;
import net.sf.jasperreports.engine.fill.SortedDataSource;
import net.sf.jasperreports.functions.AbstractFunctionSupport;
import net.sf.jasperreports.functions.annotations.Function;
import net.sf.jasperreports.functions.annotations.FunctionCategories;
import net.sf.jasperreports.functions.annotations.FunctionParameter;
import net.sf.jasperreports.functions.annotations.FunctionParameters;
import net.sf.jasperreports.repo.RepositoryContext;
import net.sf.jasperreports.repo.RepositoryUtil;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
@FunctionCategories({ReportCategory.class})
public class ReportFunctions extends AbstractFunctionSupport
{

	public static final String EXCEPTION_DATA_SOURCE_NOT_HIERARCHICAL = "data.source.not.hierarchical";
	
	@Function("ORIGINAL_DATA_SOURCE")
	public JRDataSource ORIGINAL_DATA_SOURCE()
	{
		try
		{
			return originalDataSource();
		}
		catch (JRException e)
		{
			throw new JRRuntimeException(e);
		}
	}

	protected JRDataSource originalDataSource() throws JRException
	{
		JRDataSource dataSource = (JRDataSource) getContext().getParameterValue(JRParameter.REPORT_DATA_SOURCE);
		if (dataSource instanceof SortedDataSource)
		{
			dataSource = ((SortedDataSource) dataSource).getOriginalDataSource();
		}
		return dataSource;
	}
	
	@Function("SUB_DATA_SOURCE")
	public JRDataSource SUB_DATA_SOURCE()
	{
		try
		{
			HierarchicalDataSource<?> hierarchicalDataSource = hierarchicalDataSource();
			return hierarchicalDataSource.subDataSource();
		}
		catch (JRException e)
		{
			throw new JRRuntimeException(e);
		}
	}
	
	@Function("SUB_DATA_SOURCE")
	@FunctionParameters({@FunctionParameter("expression")})
	public JRDataSource SUB_DATA_SOURCE(String expression)
	{
		try
		{
			HierarchicalDataSource<?> hierarchicalDataSource = hierarchicalDataSource();
			return hierarchicalDataSource.subDataSource(expression);
		}
		catch (JRException e)
		{
			throw new JRRuntimeException(e);
		}
	}

	protected HierarchicalDataSource<?> hierarchicalDataSource() throws JRException
	{
		JRDataSource dataSource = originalDataSource();
		if (dataSource == null)
		{
			//usually caught in JREvaluator
			throw new NullPointerException();
		}
		if (!(dataSource instanceof HierarchicalDataSource<?>))
		{
			throw new JRRuntimeException(EXCEPTION_DATA_SOURCE_NOT_HIERARCHICAL, 
					new Object[] {dataSource.getClass().getName()}); 
		}
		HierarchicalDataSource<?> hierarchicalDataSource = (HierarchicalDataSource<?>) dataSource;
		return hierarchicalDataSource;
	}
	
	@Function("RESOURCE_DATA")
	@FunctionParameters({@FunctionParameter("location")})
	public byte[] RESOURCE_DATA(String location)
	{
		RepositoryUtil repository;
		Object repositoryContext = getContext().getParameterValue(JRParameter.REPOSITORY_CONTEXT, true);
		if (repositoryContext instanceof RepositoryContext)
		{
			repository = RepositoryUtil.getInstance((RepositoryContext) repositoryContext);
		}
		else
		{
			JasperReportsContext jasperReportsContext = (JasperReportsContext) getContext().getParameterValue(
					JRParameter.JASPER_REPORTS_CONTEXT);
			repository = RepositoryUtil.getInstance(jasperReportsContext);
		}

		try
		{
			return repository.getBytesFromLocation(location);
		}
		catch (JRException e)
		{
			throw new JRRuntimeException(e);
		}
	}
}

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
package net.sf.jasperreports.hibernate;

import java.util.Map;

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.query.AbstractQueryExecuterFactory;
import net.sf.jasperreports.engine.query.HibernateConstants;
import net.sf.jasperreports.engine.query.JRQueryExecuter;
import net.sf.jasperreports.engine.util.Designated;

/**
 * Query executer factory for HQL queries that uses Hibernate 3.
 * <p/>
 * The factory creates {@link net.sf.jasperreports.hibernate.JRHibernateQueryExecuter JRHibernateQueryExecuter}
 * query executers. 
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRHibernateQueryExecuterFactory extends AbstractQueryExecuterFactory implements Designated
{
	
	public final static Object[] HIBERNATE_BUILTIN_PARAMETERS = {
			//passing the parameter type as class name and not class in order to 
			//avoid a dependency on Hibernate classes so that reports that have
			//HQL queries would load even when Hibernate is not present
			HibernateConstants.PARAMETER_HIBERNATE_SESSION,  "org.hibernate.Session",
	};
	
	/**
	 * Returns an array containing the {@link HibernateConstants#PARAMETER_HIBERNATE_SESSION PARAMETER_HIBERNATE_SESSION} 
	 * parameter.
	 */
	@Override
	public Object[] getBuiltinParameters()
	{
		return HIBERNATE_BUILTIN_PARAMETERS;
	}

	@Override
	public JRQueryExecuter createQueryExecuter(
		JasperReportsContext jasperReportsContext, 
		JRDataset dataset, 
		Map<String, ? extends JRValueParameter> parameters
		) throws JRException
	{
		return new JRHibernateQueryExecuter(jasperReportsContext, dataset, parameters);
	}

	/**
	 * Returns <code>true</code> for all parameter types.
	 */
	@Override
	public boolean supportsQueryParameterType(String className)
	{
		return true;
	}

	@Override
	public String getDesignation()
	{
		return HibernateConstants.QUERY_EXECUTER_NAME_HQL;
	}
}

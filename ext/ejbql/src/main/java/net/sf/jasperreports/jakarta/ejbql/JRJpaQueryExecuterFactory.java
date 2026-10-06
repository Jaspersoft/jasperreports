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
package net.sf.jasperreports.jakarta.ejbql;

import java.util.Map;

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.query.AbstractQueryExecuterFactory;
import net.sf.jasperreports.engine.query.EjbqlConstants;
import net.sf.jasperreports.engine.query.JRQueryExecuter;
import net.sf.jasperreports.engine.util.Designated;

/**
 * Java Persistence API query executer factory for EJBQL queries.
 * <p/>
 * The factory creates {@link net.sf.jasperreports.jakarta.ejbql.JRJpaQueryExecuter JRJpaQueryExecuter}
 * query executers. 
 * 
 * @author Marcel Overdijk (marceloverdijk@hotmail.com)
 */
public class JRJpaQueryExecuterFactory extends AbstractQueryExecuterFactory implements Designated 
{
	private static final Object[] JPA_BUILTIN_PARAMETERS = {
		EjbqlConstants.PARAMETER_JPA_ENTITY_MANAGER,  "jakarta.persistence.EntityManager",
		EjbqlConstants.PARAMETER_JPA_QUERY_HINTS_MAP, "java.util.Map"
		};	
		
	@Override
	public Object[] getBuiltinParameters() {
		return JPA_BUILTIN_PARAMETERS;
	}
	
	@Override
	public JRQueryExecuter createQueryExecuter(
		JasperReportsContext jasperReportsContext,
		JRDataset dataset, 
		Map<String,? extends JRValueParameter> parameters
		) throws JRException 
	{
		return new JRJpaQueryExecuter(jasperReportsContext, dataset, parameters);
	}

	/**
	 * Returns <code>true</code> for all parameter types.
	 */
	@Override
	public boolean supportsQueryParameterType(String className) {
		return true;
	}

	@Override
	public String getDesignation()
	{
		return EjbqlConstants.QUERY_EXECUTER_NAME_EJBQL;
	}
}

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
package net.sf.jasperreports.olap.xmla;

import java.util.Map;

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.query.AbstractQueryExecuterFactory;
import net.sf.jasperreports.engine.query.JRQueryExecuter;


/**
 * @author Michael Günther (m.guenther at users.sourceforge.net)
 */
public class JRXmlaQueryExecuterFactory extends AbstractQueryExecuterFactory
{

	public final static String PARAMETER_XMLA_URL = "XMLA_URL";

	public final static String PARAMETER_XMLA_DATASOURCE = "XMLA_DATASOURCE";

	public final static String PARAMETER_XMLA_CATALOG = "XMLA_CATALOG";
	
	public final static String PARAMETER_XMLA_USER = "XMLA_USER";
	
	public final static String PARAMETER_XMLA_PASSWORD = "XMLA_PASSWORD";


	private final static Object[] XMLA_BUILTIN_PARAMETERS = { 
		PARAMETER_XMLA_URL, "java.lang.String", 
		PARAMETER_XMLA_DATASOURCE, "java.lang.String", 
		PARAMETER_XMLA_CATALOG, "java.lang.String",
		PARAMETER_XMLA_USER, "java.lang.String",
		PARAMETER_XMLA_PASSWORD, "java.lang.String",
	};

	@Override
	public Object[] getBuiltinParameters()
	{
		return XMLA_BUILTIN_PARAMETERS;
	}

	@Override
	public JRQueryExecuter createQueryExecuter(
		JasperReportsContext jasperReportsContext, 
		JRDataset dataset, 
		Map<String, ? extends JRValueParameter> parameters
		) throws JRException
	{
		return new JRXmlaQueryExecuter(jasperReportsContext, dataset, parameters);
	}

	@Override
	public boolean supportsQueryParameterType(String className)
	{
		return true;
	}

}

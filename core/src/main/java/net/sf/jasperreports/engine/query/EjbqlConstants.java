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

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface EjbqlConstants
{
	public static final String QUERY_EXECUTER_NAME_EJBQL = "net.sf.jasperreports.query.executer:EJBQL";

	/**
	 * EJBQL query language.
	 */
	public static final String QUERY_LANGUAGE_EJBQL = "ejbql";

	/**
	 * Built-in parameter holding the value of the <code>javax.persistence.EntityManager</code> to be used for creating the query.
	 */
	public static final String PARAMETER_JPA_ENTITY_MANAGER = "JPA_ENTITY_MANAGER";	

	/**
	 * Built-in parameter (optional) holding the value of the query hints map.
	 * Each named/value pair will be set as query hint against the query.
	 */
	public static final String PARAMETER_JPA_QUERY_HINTS_MAP = "JPA_QUERY_HINTS_MAP";	
	
	
}

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
 * Extension type that bundles query clause functions.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see JRClauseFunction
 */
public interface QueryClauseFunctionBundle
{

	/**
	 * Returns a clause function that applies to the specified query language and clause Id.
	 * 
	 * @param queryLanguage the query language
	 * @param clauseId the clause Id
	 * @return a corresponding query function or <code>null</code> if the bundle doesn't know
	 * of such clause
	 */
	JRClauseFunction getFunction(String queryLanguage, String clauseId);

}

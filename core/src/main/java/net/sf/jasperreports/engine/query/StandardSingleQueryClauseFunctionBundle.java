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

import java.util.HashMap;
import java.util.Map;

/**
 * Query clause function bundle for a single query language.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class StandardSingleQueryClauseFunctionBundle 
	implements QueryClauseFunctionBundle
{

	// this bundle only knows of one language
	private final String queryLanguage;
	private Map<String, JRClauseFunction> functions;
	
	/**
	 * Creates a bundle for a query language
	 * 
	 * @param queryLanguage the query language
	 */
	public StandardSingleQueryClauseFunctionBundle(String queryLanguage)
	{
		this.queryLanguage = queryLanguage;
		this.functions = new HashMap<>();
	}
	
	/**
	 * Registers a query clause function.
	 * 
	 * @param clauseId the clause Id
	 * @param function the function implementation
	 */
	public void addFunction(String clauseId, JRClauseFunction function)
	{
		functions.put(clauseId, function);
	}
	
	@Override
	public JRClauseFunction getFunction(String queryLanguage, String clauseId)
	{
		if (!this.queryLanguage.equals(queryLanguage))
		{
			// not the language of this bundle
			return null;
		}
		
		return functions.get(clauseId);
	}

}

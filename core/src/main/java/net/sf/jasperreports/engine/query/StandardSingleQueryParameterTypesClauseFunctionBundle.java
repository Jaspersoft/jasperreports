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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class StandardSingleQueryParameterTypesClauseFunctionBundle implements
		ParameterTypesClauseFunctionBundle
{
	
	private final String queryLanguage;
	private Map<String, Collection<? extends ParameterTypesClauseFunction>> clauseFunctions;

	public StandardSingleQueryParameterTypesClauseFunctionBundle(
			String queryLanguage)
	{
		this.queryLanguage = queryLanguage;
		this.clauseFunctions = new HashMap<>();
	}

	public void setFunctions(String clauseId, ParameterTypesClauseFunction ...functions)
	{
		clauseFunctions.put(clauseId, Arrays.asList(functions));
	}

	public void setFunctions(String clauseId, ParameterTypesClauseFunction function)
	{
		clauseFunctions.put(clauseId, Collections.singleton(function));
	}

	@Override
	public Collection<? extends ParameterTypesClauseFunction> getTypeFunctions(
			String queryLanguage, String clauseId)
	{
		if (!this.queryLanguage.equals(queryLanguage))
		{
			// not the language of this bundle
			return null;
		}
		
		return clauseFunctions.get(clauseId);
	}

}

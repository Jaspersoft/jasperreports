/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2025 Cloud Software Group, Inc. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
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
package net.sf.jasperreports.compilers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionChunk;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.properties.PropertyConstants;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ReportSourceCompilation<P extends JRParameter>
{
	
	@Property(
			category = PropertyConstants.CATEGORY_COMPILE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_13_0
			)
	public static final String PROPERTY_LEGACY_SOURCE_INCLUDED_PARAMETERS = 
			JRPropertiesUtil.PROPERTY_PREFIX + "legacy.compiler.source.included.parameters";
	
	@Property(
			category = PropertyConstants.CATEGORY_COMPILE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_13_0
			)
	public static final String PROPERTY_LEGACY_SOURCE_INCLUDED_FIELDS = 
			JRPropertiesUtil.PROPERTY_PREFIX + "legacy.compiler.source.included.fields";
	
	@Property(
			category = PropertyConstants.CATEGORY_COMPILE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_13_0
			)
	public static final String PROPERTY_LEGACY_SOURCE_INCLUDED_VARIABLES = 
			JRPropertiesUtil.PROPERTY_PREFIX + "legacy.compiler.source.included.variables";
	
	public static final String SOURCE_INCLUDED_ALL = "*";
	
	public static final String SOURCE_INCLUDED_SEPARATOR = ",";

	private final List<JRExpression> expressions;
	private final Map<String, P> parameters;
	private final Map<String, JRField> fields;
	private final Map<String, JRVariable> variables;
	private final JRVariable[] variablesArray;
	
	public ReportSourceCompilation(JasperReportsContext jasperReportsContext, JasperDesign report,
			ReportExpressionsCompilation expressionsCompilation,
			Map<String, P> parameters, Map<String, JRField> fields,
			Map<String, JRVariable> variables, JRVariable[] variablesArray)
	{
		this.expressions = expressionsCompilation.getSourceExpressions();
		
		JRPropertiesUtil properties = JRPropertiesUtil.getInstance(jasperReportsContext);
		
		String includedParamsProp = properties.getProperty(report, PROPERTY_LEGACY_SOURCE_INCLUDED_PARAMETERS);
		if (includedParamsProp != null && includedParamsProp.trim().equals(SOURCE_INCLUDED_ALL))
		{
			this.parameters = parameters;
		}
		else
		{
			Set<String> includedParams = collectIncluded(parameters, includedParamsProp, 
					JRExpressionChunk.TYPE_PARAMETER, expressions);
			this.parameters = filter(parameters, includedParams);
		}
		
		String includedFieldsProp = properties.getProperty(report, PROPERTY_LEGACY_SOURCE_INCLUDED_FIELDS);
		if (includedFieldsProp != null && includedFieldsProp.trim().equals(SOURCE_INCLUDED_ALL))
		{
			this.fields = fields;
		}
		else
		{
			Set<String> includedFields = collectIncluded(fields, includedFieldsProp, 
					JRExpressionChunk.TYPE_FIELD, expressions);
			this.fields = filter(fields, includedFields);
		}
		
		String includedVarsProp = properties.getProperty(report, PROPERTY_LEGACY_SOURCE_INCLUDED_VARIABLES);
		if (includedVarsProp != null && includedVarsProp.trim().equals(SOURCE_INCLUDED_ALL))
		{
			this.variables = variables;
			this.variablesArray = variablesArray;
		}
		else
		{
			Set<String> includedVariables = collectIncluded(variables, includedVarsProp, 
					JRExpressionChunk.TYPE_VARIABLE, expressions);
			this.variables = filter(variables, includedVariables);
			this.variablesArray = filterVariables(variablesArray, includedVariables);
		}
	}

	public boolean hasSource()
	{
		return !expressions.isEmpty()
				|| (parameters != null && !parameters.isEmpty())
				|| (fields != null && !fields.isEmpty())
				|| (variables != null && !variables.isEmpty());
	}

	public List<JRExpression> getExpressions()
	{
		return expressions;
	}

	public Map<String, P> getParameters()
	{
		return parameters;
	}

	public Map<String, JRField> getFields()
	{
		return fields;
	}

	public Map<String, JRVariable> getVariables()
	{
		return variables;
	}

	public JRVariable[] getVariablesArray()
	{
		return variablesArray;
	}
	
	protected static Set<String> collectIncluded(Map<String, ?> originalMap, 
			String includeList, byte expressionChunkType, List<JRExpression> expressions)
	{
		Set<String> collected = new HashSet<>();
		if (includeList != null)
		{
			collectList(originalMap, includeList, collected);
		}
		collectExpressionsIncluded(collected, expressionChunkType, expressions);
		return collected;
	}
	
	protected static void collectExpressionsIncluded(Set<String> collected, byte expressionChunkType, 
			List<JRExpression> expressions)
	{
		for (JRExpression expression : expressions)
		{
			JRExpressionChunk[] chunks = expression.getChunks();
			if (chunks != null)
			{
				for (JRExpressionChunk chunk : chunks)
				{
					if (chunk.getType() == expressionChunkType)
					{
						collected.add(chunk.getText());
					}
				}
			}
		}
	}

	protected static void collectList(Map<String, ?> originalMap, String includeList, Set<String> collected)
	{
		String[] split = includeList.split(SOURCE_INCLUDED_SEPARATOR);
		for (String token : split)
		{
			String name = token.trim();
			if (!name.isEmpty() && originalMap.containsKey(name))
			{
				collected.add(name);
			}
		}
	}
	
	protected static <T> Map<String, T> filter(Map<String, T> original, Set<String> included)
	{
		if (original == null || original.isEmpty())
		{
			return original;
		}
		
		Map<String, T> filtered = new LinkedHashMap<>();
		for (Entry<String, T> entry : original.entrySet())
		{
			if (included.contains(entry.getKey()))
			{
				filtered.put(entry.getKey(), entry.getValue());
			}
		}
		return filtered.size() == original.size() ? original : filtered;
	}
	
	protected static JRVariable[] filterVariables(JRVariable[] original, Set<String> included)
	{
		if (original == null || original.length == 0)
		{
			return original;
		}
		
		List<JRVariable> sourceVars = 
			Arrays.stream(original).filter(v -> included.contains(v.getName())).collect(Collectors.toList());
		return 
			sourceVars.size() == original.length ? original 
				: sourceVars.toArray(new JRVariable[sourceVars.size()]);
	}
	
}

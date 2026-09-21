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
package net.sf.jasperreports.engine.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.util.Pair;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ParameterTypeSelectorClauseFunction implements JRClauseFunction
{
	
	private static final Log log = LogFactory.getLog(ParameterTypeSelectorClauseFunction.class);
	private static final String EXCEPTION_MESSAGE_KEY_QUERY_PARAMETER_TYPE_SELECTOR_CLAUSE_IMPLEMENTATION_NOT_FOUND = "query.parameter.type.selector.clause.implementation.not.found";
	private static final String EXCEPTION_MESSAGE_KEY_QUERY_PARAMETER_TYPE_SELECTOR_CLAUSE_REQUIRED_TOKEN_NOT_FOUND = "query.parameter.type.selector.clause.required.token.not.found";
	
	private static final String CONTEXT_KEY_FUNCTION_PER_TYPES_CACHE = 
			"net.sf.jasperreports.engine.query.ParameterTypeSelectorClauseFunction.cache";
	
	private final int[] parameterPositions;
	
	public ParameterTypeSelectorClauseFunction(int ... parameterPositions)
	{
		this.parameterPositions = parameterPositions;
	}

	@Override
	public void apply(JRClauseTokens clauseTokens, JRQueryClauseContext queryContext)
	{
		List<Class<?>> parameterTypes = new ArrayList<>(parameterPositions.length);
		for (int position : parameterPositions)
		{
			Class<?> parameterType = determineParameterType(clauseTokens,
					queryContext, position);
			parameterTypes.add(parameterType);
		}
		
		JRClauseFunction function = getForParameterTypes(clauseTokens, queryContext, parameterTypes);
		if (function == null)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_QUERY_PARAMETER_TYPE_SELECTOR_CLAUSE_IMPLEMENTATION_NOT_FOUND,
					new Object[]{clauseTokens.getClauseId(), parameterTypes});
		}
		
		function.apply(clauseTokens, queryContext);
	}

	protected Class<?> determineParameterType(JRClauseTokens clauseTokens,
			JRQueryClauseContext queryContext, int parameterPosition)
	{
		String parameterName = clauseTokens.getToken(parameterPosition);
		if (parameterName == null)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_QUERY_PARAMETER_TYPE_SELECTOR_CLAUSE_IMPLEMENTATION_NOT_FOUND,
					new Object[]{parameterPosition, clauseTokens.getClauseId()});
		}
		
		// the method throws an exception if it doesn't find the parameter, 
		// so we don't have to handle that case here
		JRValueParameter parameter = queryContext.getValueParameter(parameterName);
		
		Class<?> parameterType;
		Object parameterValue = parameter.getValue();
		if (parameterValue == null)
		{
			// if we don't have a value, use the declared type
			parameterType = parameter.getValueClass();
		}
		else
		{
			// use the actual value
			parameterType = parameterValue.getClass();
		}
		
		if (log.isDebugEnabled())
		{
			log.debug("query clause parameter " + parameterName 
					+ " at position " + parameterPosition 
					+ " has type " + parameterType.getName());
		}
		
		return parameterType;
	}

	protected JRClauseFunction getForParameterTypes(JRClauseTokens clauseTokens, 
			JRQueryClauseContext queryContext, List<Class<?>> parameterTypes)
	{
		Map<Object, JRClauseFunction> cache = getCache(queryContext);
		Object typesKey = parameterTypesFunctionCacheKey(clauseTokens, queryContext, parameterTypes);
		JRClauseFunction function = cache.get(typesKey);
		if (function == null)
		{
			function = selectForParameterTypes(clauseTokens, queryContext, parameterTypes);
			cache.put(typesKey, function);
		}
		else
		{
			if (log.isDebugEnabled())
			{
				log.debug("found cached function " + function 
						+ " for clause " + clauseTokens.getClauseId() + " with types " + parameterTypes);
			}
		}
		return function;
	}

	protected Map<Object, JRClauseFunction> getCache(
			JRQueryClauseContext queryContext)
	{
		@SuppressWarnings("unchecked")
		Map<Object, JRClauseFunction> cache = (Map<Object, JRClauseFunction>) queryContext.getJasperReportsContext().getOwnValue(
				CONTEXT_KEY_FUNCTION_PER_TYPES_CACHE);
		if (cache == null)
		{
			// we need a concurrent map as the context and cache might be used by several threads
			cache = new ConcurrentHashMap<>();
			
			// we don't need to handle race conditions here as it's a lightweight cache
			queryContext.getJasperReportsContext().setValue(CONTEXT_KEY_FUNCTION_PER_TYPES_CACHE, cache);
		}
		return cache;
	}

	protected Object parameterTypesFunctionCacheKey(JRClauseTokens clauseTokens, JRQueryClauseContext queryContext, 
			List<Class<?>> parameterTypes)
	{
		Object typesKey;
		int size = parameterTypes.size();
		if (size == 1)
		{
			// use the single type as direct key
			typesKey = parameterTypes.get(0);
		}
		else if (size == 2)
		{
			// use a pair of the two types
			typesKey = new Pair<Class<?>, Class<?>>(parameterTypes.get(0), parameterTypes.get(1));
		}
		else
		{
			// use the list itself as key
			typesKey = parameterTypes;
		}
		
		Pair<String, String> clauseKey = new Pair<>(queryContext.getCanonicalQueryLanguage(), clauseTokens.getClauseId());
		return new Pair<Pair<String, String>, Object>(clauseKey, typesKey);
	}

	protected JRClauseFunction selectForParameterTypes(JRClauseTokens clauseTokens, 
			JRQueryClauseContext queryContext, List<Class<?>> parameterTypes)
	{
		String queryLanguage = queryContext.getCanonicalQueryLanguage();
		String clauseId = clauseTokens.getClauseId();
		
		if (log.isDebugEnabled())
		{
			log.debug("selecting clause function " + clauseId + " for language " + queryLanguage
					+ " and parameter types " + parameterTypes);
		}
		
		// fetch extensions
		List<ParameterTypesClauseFunctionBundle> functionsBundles = queryContext.getJasperReportsContext().getExtensions(
				ParameterTypesClauseFunctionBundle.class);
		List<Pair<List<Class<?>>, JRClauseFunction>> candidateFunctions = new ArrayList<>();
		for (ParameterTypesClauseFunctionBundle functionsBundle : functionsBundles)
		{
			Collection<? extends ParameterTypesClauseFunction> functions = functionsBundle.getTypeFunctions(queryLanguage, clauseId);
			if (functions != null)
			{
				// collect candidates by checking the types
				for (ParameterTypesClauseFunction typesFunction : functions)
				{
					List<Class<?>> supportedTypes = findSupportedTypes(typesFunction, parameterTypes);
					if (supportedTypes != null)
					{
						JRClauseFunction function = typesFunction.getFunction();
						if (log.isDebugEnabled())
						{
							log.debug("found candidate function " + function
									+ " for types " + supportedTypes);
						}
						
						Pair<List<Class<?>>, JRClauseFunction> candidate = new Pair<>(supportedTypes, function);
						candidateFunctions.add(candidate);
					}
				}
			}
		}
		
		return selectFromCandidates(candidateFunctions);
	}

	protected JRClauseFunction selectFromCandidates(List<Pair<List<Class<?>>, JRClauseFunction>> candidateFunctions)
	{
		if (candidateFunctions.isEmpty())
		{
			return null;
		}
		
		if (candidateFunctions.size() == 1)
		{
			// only one candidate, return it
			return candidateFunctions.get(0).second();
		}
		
		// sort the candidates based on type specificity
		Collections.sort(candidateFunctions, TypesCandidateComparator.INSTANCE);
		// and return the first (which is actually the most specific or one of the most specific)
		JRClauseFunction function = candidateFunctions.get(0).second();
		if (log.isDebugEnabled())
		{
			log.debug("selected function " + function);
		}
		return function;
	}

	protected List<Class<?>> findSupportedTypes(
			ParameterTypesClauseFunction typesFunction,
			List<Class<?>> parameterTypes)
	{
		Collection<Class<?>> functionTypes = typesFunction.getSupportedTypes();
		List<Class<?>> supportedTypes = new ArrayList<>(parameterTypes.size());
		for (Class<?> paramType : parameterTypes)
		{
			Class<?> supportedType = findSupportedType(functionTypes, paramType);
			if (supportedType == null)
			{
				break;
			}
			else
			{
				supportedTypes.add(supportedType);
			}
		}
		
		if (supportedTypes.size() == parameterTypes.size())
		{
			// we found a supported type for each parameter
			return supportedTypes;
		}
		return null;
	}
	
	protected Class<?> findSupportedType(Collection<Class<?>> supportedTypes, Class<?> parameterType)
	{
		for (Class<?> supportedType : supportedTypes)
		{
			if (supportedType.isAssignableFrom(parameterType))
			{
				return supportedType;
			}
		}
		return null;
	}
}

final class TypesCandidateComparator implements Comparator<Pair<List<Class<?>>, JRClauseFunction>>
{
	private static final String EXCEPTION_MESSAGE_KEY_QUERY_PARAMETER_TYPE_SELECTOR_CANDIDATE_TYPE_SIZE_MISMATCH = "query.parameter.type.selector.candidate.type.size.mismatch";

	protected static final TypesCandidateComparator INSTANCE = new TypesCandidateComparator();
	
	private TypesCandidateComparator()
	{
	}
	
	@Override
	public int compare(Pair<List<Class<?>>, JRClauseFunction> o1,
			Pair<List<Class<?>>, JRClauseFunction> o2)
	{
		List<Class<?>> types1 = o1.first();
		List<Class<?>> types2 = o2.first();
		
		// should not happen, but checking
		if (types1.size() != types2.size())
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_QUERY_PARAMETER_TYPE_SELECTOR_CANDIDATE_TYPE_SIZE_MISMATCH,
					new Object[]{types1.size(), types2.size()});
		}
		
		// perform a lexicographical comparison by comparing each type sequentially until we find a difference
		int order = 0;
		for (Iterator<Class<?>> it1 = types1.iterator(), it2 = types2.iterator(); it1.hasNext() && it2.hasNext(); )
		{
			Class<?> type1 = it1.next();
			Class<?> type2 = it2.next();
			int typesOrder = compareTypes(type1, type2);
			if (typesOrder != 0)
			{
				order = typesOrder;
				break;
			}
		}
		
		return order;
	}
	
	protected int compareTypes(Class<?> type1, Class<?> type2)
	{
		if (type1.equals(type2))
		{
			return 0;
		}
		
		// more specific classes are "smaller"
		if (type1.isAssignableFrom(type2))
		{
			return 1;
		}
		
		if (type2.isAssignableFrom(type1))
		{
			return -1;
		}
		
		// if the classes are independent, return an arbitrary order
		return type1.getName().compareTo(type2.getName());
	}
	
}
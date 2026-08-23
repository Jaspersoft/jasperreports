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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRQuery;
import net.sf.jasperreports.engine.JRQueryChunk;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.util.JRQueryChunkHandler;
import net.sf.jasperreports.engine.util.JRQueryParser;
import net.sf.jasperreports.properties.PropertyConstants;
import net.sf.jasperreports.repo.RepositoryContext;

/**
 * Base abstract query executer.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public abstract class JRAbstractQueryExecuter implements JRQueryExecuter
{
	public static final String EXCEPTION_MESSAGE_KEY_NUMERIC_TYPE_REQUIRED = "query.numeric.type.required";
	public static final String EXCEPTION_MESSAGE_KEY_PARAMETER_NOT_FOUND = "query.parameter.not.found";
	public static final String EXCEPTION_MESSAGE_KEY_QUERY_CLAUSE_CIRCULARLY_NESTED_PARAMETER = "query.clause.circularly.nested.parameter";
	public static final String EXCEPTION_MESSAGE_KEY_QUERY_CLAUSE_ID_FIRST_TOKEN_MISSING = "query.clause.id.first.token.missing";
	public static final String EXCEPTION_MESSAGE_KEY_QUERY_CLAUSE_NOT_FOUND = "query.clause.not.found";
	public static final String EXCEPTION_MESSAGE_KEY_QUERY_PARAMETER_CLAUSE_DISABLED = "query.parameter.clause.disabled";
	public static final String EXCEPTION_MESSAGE_KEY_UNSUPPORTED_PARAMETER_TYPE = "query.unsupported.parameter.type";

	/**
	 * Flag property that determines whether query parameter clause text injection (<code>$P!{}</code>) is enabled.
	 * <p/>
	 * When set to <code>false</code>, any use of <code>$P!{}</code> parameter clause syntax in a query will result
	 * in an error. This can be used to prevent parameter values from being directly injected as text into queries.
	 * <p/>
	 * The property is enabled by default.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_SECURITY,
			defaultValue = PropertyConstants.BOOLEAN_TRUE,
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_7_0_7,
			valueType = Boolean.class
			)
	public static final String PROPERTY_QUERY_PARAMETER_CLAUSE_ENABLED = JRPropertiesUtil.PROPERTY_PREFIX + "query.parameter.clause.enabled";

	public static final String GET_COLLECTED = "getCollectedParameterNames()";
	
	protected class VisitExceptionWrapper extends RuntimeException
	{
		private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
		
		public VisitExceptionWrapper(Exception cause)
		{
			super(cause);
		}
	}
	
	protected static interface QueryParameterVisitor
	{
		void visit(QueryParameter parameter) throws VisitExceptionWrapper;

		void visit(ValuedQueryParameter valuedQueryParameter) throws VisitExceptionWrapper;
	}
	
	protected static interface QueryParameterEntry
	{
		void accept(QueryParameterVisitor visitor) throws VisitExceptionWrapper;
	}
	
	/**
	 * A parameter present in the query.
	 */
	protected static class QueryParameter implements QueryParameterEntry
	{
		protected static final int COUNT_SINGLE = -1;
		
		private final String name;
		private final int count;
		private final boolean ignoreNulls;
		
		public QueryParameter(String name)
		{
			this(name, COUNT_SINGLE, false);
		}
		
		public QueryParameter(String name, int count)
		{
			this(name, count, false);
		}
		
		public QueryParameter(String name, int count, boolean ignoreNulls)
		{
			this.name = name;
			this.count = count;
			this.ignoreNulls = ignoreNulls;
		}
		
		/**
		 * Decides whether the parameter has multiple values.
		 * 
		 * @return whether the parameter has multiple values.
		 */
		public boolean isMulti()
		{
			return count != COUNT_SINGLE;
		}
		
		/**
		 * Returns the number of parameter values.
		 * 
		 * @return the number of parameter values
		 * @see #isMulti()
		 */
		public int getCount()
		{
			return count;
		}
		
		/**
		 * Returns the name of the report parameter.
		 * 
		 * @return the name of the report parameter
		 */
		public String getName()
		{
			return name;
		}
		
		/**
		 * 
		 * @return a flag indicating if the null values in a multiparameter value should be ignored
		 * 
		 */
		public boolean isIgnoreNulls()
		{
			return ignoreNulls;
		}

		@Override
		public void accept(QueryParameterVisitor visitor) throws VisitExceptionWrapper
		{
			visitor.visit(this);
		}
	}
	
	protected static class ValuedQueryParameter implements QueryParameterEntry
	{
		private final Class<?> type;
		private final Object value;

		public ValuedQueryParameter(Class<?> type, Object value)
		{
			this.type = type;
			this.value = value;
		}

		@Override
		public void accept(QueryParameterVisitor visitor) throws VisitExceptionWrapper
		{
			visitor.visit(this);
		}

		public Class<?> getType()
		{
			return type;
		}

		public Object getValue()
		{
			return value;
		}
	}
	
	/**
	 * Clause function registry.
	 */
	protected final Map<String,JRClauseFunction> clauseFunctions = new HashMap<>();
	
	private final QueryExecutionContext context;
	private final JRPropertiesUtil propertiesUtil;
	protected final JRDataset dataset;
	private final Map<String,? extends JRValueParameter> parametersMap;
	
	private String queryString;
	
	/**
	 * List of {@link QueryParameter query parameters}.
	 */
	private List<QueryParameterEntry> queryParameters;
	
	private Set<String> parameterClauseStack;
	
	/**
	 * 
	 */
	protected JRAbstractQueryExecuter(
		JasperReportsContext jasperReportsContext, 
		JRDataset dataset, 
		Map<String, ? extends JRValueParameter> parametersMap
		)
	{
		this(SimpleQueryExecutionContext.of(jasperReportsContext), dataset, parametersMap);
	}
	
	protected JRAbstractQueryExecuter(
		QueryExecutionContext context, 
		JRDataset dataset, 
		Map<String, ? extends JRValueParameter> parametersMap
		)
	{
		this.context = context;
		this.propertiesUtil = JRPropertiesUtil.getInstance(context.getJasperReportsContext());
		this.dataset = dataset;
		this.parametersMap = parametersMap;
		
		queryString = "";
		queryParameters = new ArrayList<>();
	}

	/**
	 * 
	 */
	protected JasperReportsContext getJasperReportsContext()
	{
		return context.getJasperReportsContext();
	}

	protected QueryExecutionContext getQueryExecutionContext()
	{
		return context;
	}

	protected RepositoryContext getRepositoryContext()
	{
		return context.getRepositoryContext();
	}

	/**
	 * 
	 */
	protected JRPropertiesUtil getPropertiesUtil()
	{
		return propertiesUtil;
	}

	/**
	 * Registers a clause function.
	 * 
	 * @param id the function ID
	 * @param function the function
	 */
	protected void registerClauseFunction(String id, JRClauseFunction function)
	{
		clauseFunctions.put(id, function);
	}
	
	/**
	 * Unregisters a clause function.
	 * 
	 * @param id the function ID
	 */
	protected void unregisterClauseFunction(String id)
	{
		clauseFunctions.remove(id);
	}
	
	/**
	 * Resolves a clause function ID to a function instance.
	 * 
	 * @param id the function ID
	 * @return the clause function registered for the ID
	 * @throws JRRuntimeException if no function for the ID is found
	 */
	protected JRClauseFunction resolveFunction(String id)
	{
		// first look in explicitly registered functions
		// this is mostly kept for backward compatibility
		JRClauseFunction function = clauseFunctions.get(id);
		if (function == null)
		{
			function = findExtensionQueryFunction(id);
			if (function == null)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_QUERY_CLAUSE_NOT_FOUND,
						new Object[]{id});
			}
		}
		return function;
	}

	protected JRClauseFunction findExtensionQueryFunction(String id)
	{
		JRClauseFunction function = null;
		//FIXME should we also use the current query language?
		String queryLanguage = getCanonicalQueryLanguage();
		// look for extensions
		List<QueryClauseFunctionBundle> functionBundles = getJasperReportsContext().getExtensions(
				QueryClauseFunctionBundle.class);
		for (QueryClauseFunctionBundle functionBundle : functionBundles)
		{
			function = functionBundle.getFunction(queryLanguage, id);
			if (function != null)
			{
				// use the first found
				break;
			}
		}
		return function;
	}
	
	/**
	 * Returns a canonical query language for this query executer implementation.
	 * 
	 * <p>
	 * The canonical language is used to retrieve extensions for the query executer.
	 * </p>
	 * 
	 * <p>
	 * The default implementation returns the runtime query language used in the dataset,
	 * but query executer implementations should override this method and return a fixed
	 * language.
	 * </p>
	 * 
	 * @return a canonical query language
	 */
	protected String getCanonicalQueryLanguage()
	{
		// by default returning the current query language because we can't force 
		// existing external implementations to implement the method.
		// but internal implementations override the method and specify a fixed
		// language name for extensions.
		return dataset.getQuery().getLanguage();
	}
	
	/**
	 * Parses the query and replaces the parameter clauses by the parameter values and
	 * the parameters by the return value of {@link #getParameterReplacement(String) getParameterReplacement}.
	 *
	 */
	protected void parseQuery()
	{
		parameterClauseStack = new HashSet<>();
		
		JRQuery query = dataset.getQuery();
		
		if (query != null)
		{
			JRQueryChunk[] chunks = query.getChunks();
			if (chunks != null && chunks.length > 0)
			{
				StringBuffer sbuffer = new StringBuffer();
				for (JRQueryChunk chunk : chunks)
				{
					appendQueryChunk(sbuffer, chunk);
				}

				queryString = sbuffer.toString();
			}
		}
	}


	protected void appendQueryChunk(StringBuffer sbuffer, JRQueryChunk chunk)
	{
		switch (chunk.getType())
		{
			case JRQueryChunk.TYPE_PARAMETER_CLAUSE :
			{
				appendParameterClauseChunk(sbuffer, chunk.getText());
				break;
			}
			case JRQueryChunk.TYPE_PARAMETER :
			{
				appendParameterChunk(sbuffer, chunk.getText());
				break;
			}
			case JRQueryChunk.TYPE_CLAUSE_TOKENS :
			{
				appendClauseChunk(sbuffer, chunk.getTokens());
				break;
			}
			case JRQueryChunk.TYPE_TEXT :
			default :
			{
				appendTextChunk(sbuffer, chunk.getText());
				break;
			}
		}
	}


	protected void appendTextChunk(StringBuffer sbuffer, String text)
	{
		sbuffer.append(text);
	}


	protected void appendParameterChunk(StringBuffer sbuffer, String chunkText)
	{
		String parameterName = chunkText;
		checkParameter(parameterName);

		sbuffer.append(getParameterReplacement(parameterName));
		addQueryParameter(chunkText);
	}


	/**
	 * Records a query parameter.
	 * 
	 * @param parameterName the parameter name
	 * @see #getCollectedParameters()
	 */
	protected void addQueryParameter(String parameterName)
	{
		QueryParameter param = new QueryParameter(parameterName);
		queryParameters.add(param);
	}


	/**
	 * Records a multi-valued query parameter.
	 * 
	 * @param parameterName the parameter name
	 * @param count the value count
	 * @see #getCollectedParameters()
	 * @see QueryParameter#isMulti()
	 */
	protected void addQueryMultiParameters(String parameterName, int count)
	{
		QueryParameter param = new QueryParameter(parameterName, count);
		queryParameters.add(param);
	}


	/**
	 * Records a multi-valued query parameter which ignore null values.
	 * 
	 * @param parameterName the parameter name
	 * @param count the value count
	 * @see #getCollectedParameters()
	 * @see QueryParameter#isMulti()
	 */
	protected void addQueryMultiParameters(String parameterName, int count, boolean ignoreNulls)
	{
		QueryParameter param = new QueryParameter(parameterName, count, ignoreNulls);
		queryParameters.add(param);
	}
	
	protected void addQueryParameter(Class<?> type, Object value)
	{
		ValuedQueryParameter param = new ValuedQueryParameter(type, value);
		queryParameters.add(param);
	}


	protected void appendParameterClauseChunk(final StringBuffer sbuffer, String chunkText)
	{
		boolean parameterClauseEnabled = getPropertiesUtil().getBooleanProperty(
				PROPERTY_QUERY_PARAMETER_CLAUSE_ENABLED, true);
		if (!parameterClauseEnabled)
		{
			throw
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_QUERY_PARAMETER_CLAUSE_DISABLED,
					new Object[]{chunkText});
		}

		String parameterName = chunkText;
		checkParameter(parameterName);
		
		if (!parameterClauseStack.add(parameterName))
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_QUERY_CLAUSE_CIRCULARLY_NESTED_PARAMETER,
					new Object[]{parameterName});
		}
		
		try
		{
			Object parameterValue = getParameterValue(parameterName);
			String clauseText = String.valueOf(parameterValue);
			JRQueryChunkHandler nestedChunkHandler = new JRQueryChunkHandler()
			{
				@Override
				public void handleParameterChunk(String text)
				{
					appendParameterChunk(sbuffer, text);
				}

				@Override
				public void handleParameterClauseChunk(String text)
				{
					appendParameterClauseChunk(sbuffer, text);
				}

				@Override
				public void handleTextChunk(String text)
				{
					appendTextChunk(sbuffer, text);
				}

				@Override
				public void handleClauseChunk(String[] tokens, char tokenSeparator)
				{
					appendClauseChunk(sbuffer, tokens);
				}
			};
			JRQueryParser.instance().parse(clauseText, nestedChunkHandler);
		}
		finally
		{
			parameterClauseStack.remove(parameterName);
		}
	}


	/**
	 * Handles a {@link JRQueryChunk#TYPE_CLAUSE_TOKENS clause query chunk}.
	 * <p>
	 * The default implementation considers the first token as a
	 * {@link JRClauseFunction clause function} ID and delegates the call to the
	 * function.
	 * </p>
	 * <p>
	 * Extending query executers can override this to implement custom query clause handling.
	 * </p>
	 * 
	 * @param sbuffer the query text buffer
	 * @param clauseTokens clause tokens
	 * @see #registerClauseFunction(String, JRClauseFunction)
	 * @throws JRRuntimeException if there is no first token or no clause function is found for the ID
	 */
	protected void appendClauseChunk(final StringBuffer sbuffer, String[] clauseTokens)
	{
		JRClauseTokens tokens = new JRClauseTokens(clauseTokens);
		String id = tokens.getClauseId();
		if (id == null)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_QUERY_CLAUSE_ID_FIRST_TOKEN_MISSING,
					(Object[])null);
		}
		
		JRClauseFunction function = resolveFunction(id);
		applyClause(function, tokens, sbuffer);
	}

	
	protected void applyClause(JRClauseFunction function, JRClauseTokens tokens, final StringBuffer sbuffer)
	{
		function.apply(tokens, new JRQueryClauseContext()
		{
			@Override
			public void addQueryMultiParameters(String parameterName, int count)
			{
				addQueryMultiParameters(parameterName, count, false);
			}

			@Override
			public void addQueryMultiParameters(String parameterName, int count, boolean ignoreNulls)
			{
				JRAbstractQueryExecuter.this.addQueryMultiParameters(parameterName, count, ignoreNulls);
			}

			@Override
			public void addQueryParameter(String parameterName)
			{
				JRAbstractQueryExecuter.this.addQueryParameter(parameterName);
			}

			@Override
			public void addQueryParameter(Class<?> type, Object value)
			{
				JRAbstractQueryExecuter.this.addQueryParameter(type, value);
			}

			@Override
			public JRDataset getDataset()
			{
				return JRAbstractQueryExecuter.this.dataset;
			}

			@Override
			public JRValueParameter getValueParameter(String parameterName)
			{
				return JRAbstractQueryExecuter.this.getValueParameter(parameterName);
			}

			@Override
			public StringBuffer queryBuffer()
			{
				return sbuffer;
			}

			@Override
			public JasperReportsContext getJasperReportsContext()
			{
				return JRAbstractQueryExecuter.this.getJasperReportsContext();
			}

			@Override
			public String getCanonicalQueryLanguage()
			{
				return JRAbstractQueryExecuter.this.getCanonicalQueryLanguage();
			}
		});
	}

	
	/**
	 * Returns the parsed query string with the parameter clauses replaced by the parameter values and 
	 * the parameters replaced by {@link #getParameterReplacement(String) getParameterReplacement}.
	 * 
	 * @return the parsed query string
	 */
	protected String getQueryString()
	{
		return queryString;
	}
	
	
	/**
	 * Returns the list of parameter names in the order in which they appear in the query.
	 * 
	 * @return the list of parameter names
	 */
	protected List<String> getCollectedParameterNames()
	{
		List<String> parameterNames = new ArrayList<>(queryParameters.size());
		for (QueryParameterEntry paramEntry : queryParameters)
		{
			if (!(paramEntry instanceof QueryParameter))
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_UNSUPPORTED_PARAMETER_TYPE,
						new Object[]{"getCollectedParameterNames()", paramEntry.getClass().getName()});
			}
			
			QueryParameter param = (QueryParameter) paramEntry;
			parameterNames.add(param.getName());
		}
		return parameterNames;
	}
	
	
	/**
	 * Returns the list of {@link QueryParameter query parameters} in the order in which they appear in the query.
	 * 
	 * @return the list of query parameters
	 */
	protected List<QueryParameter> getCollectedParameters()
	{
		List<QueryParameter> params = new ArrayList<>(queryParameters.size());
		for (QueryParameterEntry parameterEntry : queryParameters)
		{
			if (!(parameterEntry instanceof QueryParameter))
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_UNSUPPORTED_PARAMETER_TYPE,
						new Object[]{"getCollectedParameters()", parameterEntry.getClass().getName()});
			}
			params.add((QueryParameter) parameterEntry);
		}
		return Collections.unmodifiableList(params);
	}
	
	protected void visitQueryParameters(QueryParameterVisitor visitor) throws VisitExceptionWrapper
	{
		for (QueryParameterEntry queryParameter : queryParameters)
		{
			queryParameter.accept(visitor);
		}
	}
	
	
	/**
	 * Returns the value of a fill parameter.
	 * @param parameterName the parameter name
	 * @param ignoreMissing if <code>true</code>, the method will return null for non existing parameters;
	 * 		otherwise, an exception will be thrown if the parameter does not exist
	 * @return the parameter value
	 */
	protected Object getParameterValue(String parameterName, boolean ignoreMissing)
	{
		if (ignoreMissing)
		{
			JRValueParameter parameter = getValueParameter(JRParameter.REPORT_PARAMETERS_MAP, false);
			@SuppressWarnings("unchecked")
			Map<String,Object> parametersMap = (Map<String,Object>)parameter.getValue(); 			
			return parametersMap.get(parameterName);
		}
		
		JRValueParameter parameter = getValueParameter(parameterName, ignoreMissing);
		return parameter == null ? null : parameter.getValue();
	}
	
	
	/**
	 * Returns the value of a fill parameter.
	 * @param parameterName the parameter name
	 * @return the parameter value
	 */
	protected Object getParameterValue(String parameterName)
	{
		return getParameterValue(parameterName, false);
	}
	
	
	/**
	 * 
	 */
	protected boolean parameterHasValue(String parameter)
	{
		JRValueParameter reportParametersMap = getValueParameter(JRParameter.REPORT_PARAMETERS_MAP, false);
		@SuppressWarnings("unchecked")
		Map<String,Object> parametersMap = (Map<String,Object>)reportParametersMap.getValue();  
		return parametersMap.containsKey(parameter);
	}
	
	
	/**
	 * 
	 */
	protected String getStringParameter(String parameter, String property)
	{
		if (parameterHasValue(parameter))
		{
			return (String)getParameterValue(parameter, true);
		}
		else
		{
			return 
				getPropertiesUtil().getProperty(
					dataset.getPropertiesMap(),
					property
					);
		}
	}
	
	
	/**
	 * 
	 */
	protected String getStringParameterOrProperty(String name)
	{
		return getStringParameter(name, name);
	}
	
	
	/**
	 * 
	 */
	protected boolean getBooleanParameter(String parameter, String property, boolean defaultValue)
	{
		if (parameterHasValue(parameter))
		{
			Boolean booleanValue = (Boolean)getParameterValue(parameter, true);
			if (booleanValue == null)
			{
				return getPropertiesUtil().getBooleanProperty(property);
			}
			else
			{
				return booleanValue;
			}
		}
		else
		{
			return 
				getPropertiesUtil().getBooleanProperty(
					dataset.getPropertiesMap(),
					property,
					defaultValue
					);
		}
	}
	
	
	/**
	 * 
	 */
	protected boolean getBooleanParameterOrProperty(String name, boolean defaultValue)
	{
		return getBooleanParameter(name, name, defaultValue);
	}
	
	
	/**
	 * 
	 */
	protected Boolean getBooleanParameter(String parameter, String property)
	{
		if (parameterHasValue(parameter))
		{
			Boolean booleanValue = (Boolean)getParameterValue(parameter, true);
			if (booleanValue == null)
			{
				return getPropertiesUtil().getBooleanProperty(property);
			}
			else
			{
				return booleanValue;
			}
		}
		else
		{
			return 
				getPropertiesUtil().getBooleanProperty(
					dataset.getPropertiesMap(),
					property
					);
		}
	}
	
	
	/**
	 * 
	 */
	protected Boolean getBooleanParameterOrProperty(String name)
	{
		return getBooleanParameter(name, name);
	}
	
	
	
	
	/**
	 * 
	 */
	protected int getIntegerParameter(String parameter, String property, int defaultValue)
	{
		if (parameterHasValue(parameter))
		{
			Integer integerValue = (Integer)getParameterValue(parameter, true);
			if (integerValue == null)
			{
				return getPropertiesUtil().getIntegerProperty(property);
			}
			else
			{
				return integerValue;
			}
		}
		else
		{
			return 
				getPropertiesUtil().getIntegerProperty(
					dataset.getPropertiesMap(),
					property,
					defaultValue
					);
		}
	}
	
	
	/**
	 * 
	 */
	protected int getIntegerParameterOrProperty(String name, int defaultValue)
	{
		return getIntegerParameter(name, name, defaultValue);
	}
	
	
	/**
	 * 
	 */
	protected Integer getIntegerParameter(String parameter, String property)
	{
		if (parameterHasValue(parameter))
		{
			Integer integerValue = (Integer)getParameterValue(parameter, true);
			if (integerValue == null)
			{
				return getPropertiesUtil().getIntegerProperty(property);
			}
			else
			{
				return integerValue;
			}
		}
		else
		{
			return 
				getPropertiesUtil().getIntegerProperty(
					dataset.getPropertiesMap(),
					property
					);
		}
	}
	
	
	/**
	 * 
	 */
	protected Integer getIntegerParameterOrProperty(String name)
	{
		return getIntegerParameter(name, name);
	}
	
	
	protected void checkParameter(String parameterName)
	{
		if (!parametersMap.containsKey(parameterName))
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_PARAMETER_NOT_FOUND,
					new Object[]{parameterName});
		}
	}
	
	
	/**
	 * Return a value parameter from the parameters map.
	 * 
	 * @param parameterName the parameter name
	 * @param ignoreMissing if <code>true</code>, the method will return null for non existing parameters;
	 * 		otherwise, an exception will be thrown if the parameter does not exist
	 * @return the parameter
	 */
	protected JRValueParameter getValueParameter(String parameterName, boolean ignoreMissing)
	{
		JRValueParameter parameter = parametersMap.get(parameterName);
		
		if (parameter == null && !ignoreMissing)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_PARAMETER_NOT_FOUND,
					new Object[]{parameterName});
		}
		
		return parameter;
	}

	
	/**
	 * Return a value parameter from the parameters map.
	 * 
	 * @param parameterName the parameter name
	 * @return the parameter
	 */
	protected JRValueParameter getValueParameter(String parameterName)
	{
		return getValueParameter(parameterName, false);
	}

	
	/**
	 * Returns the replacement text for a query parameter.
	 * 
	 * @param parameterName the parameter name
	 * @return the replacement text
	 * @see JRQueryChunk#TYPE_PARAMETER
	 */
	protected abstract String getParameterReplacement(String parameterName);
}

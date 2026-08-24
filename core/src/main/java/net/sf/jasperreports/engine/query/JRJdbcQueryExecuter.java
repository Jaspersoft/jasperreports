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

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Types;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import javax.sql.rowset.CachedRowSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.util.JRSingletonCache;


/**
 * JDBC query executer for SQL queries.
 * <p/>
 * This query executer implementation offers built-in support for SQL queries.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRJdbcQueryExecuter extends JRAbstractQueryExecuter
{
	private static final Log log = LogFactory.getLog(JRJdbcQueryExecuter.class);
	public static final String EXCEPTION_MESSAGE_KEY_MULTI_PARAMETERS_CANNOT_CONTAIN_NULL_VALUES = "query.multi.parameters.cannot.contain.null.values";
	public static final String EXCEPTION_MESSAGE_KEY_QUERY_STATEMENT_CANCEL_ERROR = "query.statement.cancel.error";
	public static final String EXCEPTION_MESSAGE_KEY_QUERY_STATEMENT_EXECUTE_ERROR = "query.statement.execute.error";
	public static final String EXCEPTION_MESSAGE_KEY_QUERY_STATEMENT_PREPARE_ERROR = "query.statement.prepare.error";
	public static final String EXCEPTION_MESSAGE_KEY_QUERY_STATEMENT_TIMEOUT_LIMIT_EXCEEDED = "query.statement.timeout.limit.exceeded";
	public static final String EXCEPTION_MESSAGE_KEY_UNEXPECTED_MULTI_PARAMETER_TYPE = "query.unexpected.multi.parameter.type";

	public static final String CANONICAL_LANGUAGE = "SQL";
	
	public static final String CLAUSE_ID_IN = "IN";
	public static final String CLAUSE_ID_NOTIN = "NOTIN";
	
	public static final String CLAUSE_ID_EQUAL = "EQUAL";
	public static final String CLAUSE_ID_NOTEQUAL = "NOTEQUAL";
	
	public static final String CLAUSE_ID_LESS = "LESS";
	public static final String CLAUSE_ID_GREATER = "GREATER";
	public static final String CLAUSE_ID_LESS_OR_EQUAL = "LESS]";
	public static final String CLAUSE_ID_GREATER_OR_EQUAL = "[GREATER";
	
	public static final String CLAUSE_ID_BETWEEN = "BETWEEN";
	public static final String CLAUSE_ID_BETWEEN_CLOSED = "[BETWEEN]";
	public static final String CLAUSE_ID_BETWEEN_LEFT_CLOSED = "[BETWEEN";
	public static final String CLAUSE_ID_BETWEEN_RIGHT_CLOSED = "BETWEEN]";
	
	protected static final String TYPE_FORWARD_ONLY = "forwardOnly";
	protected static final String TYPE_SCROLL_INSENSITIVE = "scrollInsensitive";
	protected static final String TYPE_SCROLL_SENSITIVE = "scrollSensitive";
	
	protected static final String CONCUR_READ_ONLY = "readOnly";
	protected static final String CONCUR_UPDATABLE = "updatable";
	
	protected static final String HOLD_CURSORS_OVER_COMMIT = "hold";
	protected static final String CLOSE_CURSORS_AT_COMMIT = "close";
	
	protected static final String CACHED_ROWSET_CLASS = "com.sun.rowset.CachedRowSetImpl";
	
	private static final JRSingletonCache<ProcedureCallHandlerFactory> procedureCallHandlerFactoryCache = 
			new JRSingletonCache<>(ProcedureCallHandlerFactory.class);

	protected Connection connection;
	
	/**
	 * The statement used to fire the query.
	 */
	protected PreparedStatement statement;

	protected ResultSet resultSet;
	
	private boolean isCachedRowSet;

	private TimeZone parametersTimeZone;
	private boolean parametersTimeZoneOverride;
	private TimeZone fieldsTimeZone;
	private boolean fieldsTimeZoneOverride;
	
	private boolean isProcedureCall;
	private ProcedureCallHandler procedureCallHandler;
	
	/**
	 * 
	 */
	public JRJdbcQueryExecuter(
		JasperReportsContext jasperReportsContext, 
		JRDataset dataset, 
		Map<String,? extends JRValueParameter> parameters
		)
	{
		super(jasperReportsContext, dataset, parameters);
		
		connection = (Connection) getParameterValue(JRParameter.REPORT_CONNECTION);
		if (connection == null)
		{
			if (log.isWarnEnabled())
			{
				log.warn("The supplied java.sql.Connection object is null.");
			}
		} 
		else if (log.isDebugEnabled())
		{
			try
			{
				DatabaseMetaData metaData = connection.getMetaData();
				log.debug("DB is " + metaData.getDatabaseProductName()
						+ " version " + metaData.getDatabaseProductVersion()
						+ " (" + metaData.getDatabaseMajorVersion()
						+ "/" + metaData.getDatabaseMinorVersion() + ")");
				log.debug("driver is " + metaData.getDriverName()
						+ " version " + metaData.getDriverVersion()
						+ " (" + metaData.getDriverMajorVersion()
						+ "/" + metaData.getDriverMinorVersion() + ")");
				log.debug("jdbc " + metaData.getJDBCMajorVersion()
						+ "/" + metaData.getJDBCMinorVersion());
				log.debug("connection URL is " + metaData.getURL());
			}
			catch (SQLException e)
			{
				log.debug("failed to read connection metadata", e);
			}
		}
		
		isCachedRowSet = getBooleanParameterOrProperty(JRJdbcQueryExecuterFactory.PROPERTY_CACHED_ROWSET, false);
		
		setTimeZone();

		registerFunctions();
		
		parseQuery();		
	}

	
	/**
	 * Registers built-in {@link JRClauseFunction clause functions}.
	 * @see #registerFunctions()
	 * @see #appendClauseChunk(StringBuffer, String[])
	 */
	protected void registerFunctions()
	{
		// keeping empty for backwards compatibility, the functions are now registered
		// as extensions by JDBCQueryClauseFunctionsExtensions
	}

	@Override
	protected String getCanonicalQueryLanguage()
	{
		return CANONICAL_LANGUAGE;
	}

	protected void setTimeZone()
	{
		String timeZoneIdParam = (String) getParameterValue(JRJdbcQueryExecuterFactory.PROPERTY_TIME_ZONE, true);
		String timeZoneIdProp = getPropertiesUtil().getProperty(dataset, JRJdbcQueryExecuterFactory.PROPERTY_TIME_ZONE);

		if (log.isDebugEnabled())
		{
			log.debug("system timezone is " + TimeZone.getDefault());
			log.debug("report timezone is " + getParameterValue(JRParameter.REPORT_TIME_ZONE, true));
			log.debug("JDBC timezone parameter is " + timeZoneIdParam);
			log.debug("JDBC timezone property is " + timeZoneIdProp);
		}
		
		String parametersTimeZoneId = (String) getParameterValue(JRJdbcQueryExecuterFactory.PROPERTY_PARAMETERS_TIME_ZONE, true);
		if (log.isDebugEnabled())
		{
			log.debug("JDBC parameters timezone parameter is " + parametersTimeZoneId);
		}
		parametersTimeZoneId = parametersTimeZoneId == null ? timeZoneIdParam : parametersTimeZoneId;
		if (parametersTimeZoneId != null)
		{
			parametersTimeZoneOverride = true;
		}
		else
		{
			parametersTimeZoneId = getPropertiesUtil().getProperty(dataset, JRJdbcQueryExecuterFactory.PROPERTY_PARAMETERS_TIME_ZONE);
			if (log.isDebugEnabled())
			{
				log.debug("JDBC parameters timezone property is " + parametersTimeZoneId);
			}
			parametersTimeZoneId = parametersTimeZoneId == null ? timeZoneIdProp : parametersTimeZoneId;
		}
		parametersTimeZone = resolveTimeZone(parametersTimeZoneId);
		if (log.isDebugEnabled())
		{
			log.debug("parameters timezone " + parametersTimeZone);
		}
		
		String fieldsTimeZoneId = (String) getParameterValue(JRJdbcQueryExecuterFactory.PROPERTY_FIELDS_TIME_ZONE, true);
		if (log.isDebugEnabled())
		{
			log.debug("JDBC fields timezone parameter is " + fieldsTimeZoneId);
		}
		fieldsTimeZoneId = fieldsTimeZoneId == null ? timeZoneIdParam : fieldsTimeZoneId;
		if (fieldsTimeZoneId != null)
		{
			fieldsTimeZoneOverride = true;
		}
		else
		{
			fieldsTimeZoneId = getPropertiesUtil().getProperty(dataset, JRJdbcQueryExecuterFactory.PROPERTY_FIELDS_TIME_ZONE);
			if (log.isDebugEnabled())
			{
				log.debug("JDBC fields timezone property is " + fieldsTimeZoneId);
			}
			fieldsTimeZoneId = fieldsTimeZoneId == null ? timeZoneIdProp : fieldsTimeZoneId;
		}
		fieldsTimeZone = resolveTimeZone(fieldsTimeZoneId);
		if (log.isDebugEnabled())
		{
			log.debug("fields timezone " + fieldsTimeZone);
		}
	}
	
	protected TimeZone resolveTimeZone(String timezoneId)
	{
		TimeZone tz;
		if (timezoneId == null || timezoneId.length() == 0)
		{
			tz = null;
		}
		else if (timezoneId.equals(JRParameter.REPORT_TIME_ZONE))
		{
			// using the report timezone
			tz = (TimeZone) getParameterValue(JRParameter.REPORT_TIME_ZONE, true);
		}
		else
		{
			// resolving as tz ID
			tz = TimeZone.getTimeZone(timezoneId);
		}
		return tz;
	}


	@Override
	protected String getParameterReplacement(String parameterName)
	{
		return "?";
	}

	
	/* (non-Javadoc)
	 * @see net.sf.jasperreports.engine.util.JRQueryExecuter#createDatasource()
	 */
	@Override
	public JRDataSource createDatasource() throws JRException
	{
		JRResultSetDataSource dataSource = null;
		
		createStatement();
		
		if (statement != null)
		{
			try
			{
				if (log.isDebugEnabled())
				{
					log.debug("Executing query");
				}
				
				ResultSet queryResult;
				if (isProcedureCall)
				{
					queryResult = procedureCallHandler.execute();
				}
				else
				{
					queryResult = statement.executeQuery();
				}
				
				if (log.isDebugEnabled())
				{
					log.debug("Query execution done");
				}
				
				if (isCachedRowSet)
				{
					CachedRowSet cachedRowSet;
					try
					{
						Class<? extends CachedRowSet> clazz = (Class<? extends CachedRowSet>)Class.forName(CACHED_ROWSET_CLASS);
						Constructor<? extends CachedRowSet> constructor = clazz.getConstructor();
						cachedRowSet = constructor.newInstance();
					}
					catch (Exception e)
					{
						throw new JRException(e);
					}
					
					cachedRowSet.populate(queryResult);
					closeStatement();
					resultSet = cachedRowSet;
				}
				else
				{
					resultSet = queryResult;
				}
				
				dataSource = new JRResultSetDataSource(getJasperReportsContext(), resultSet);
				dataSource.setTimeZone(fieldsTimeZone, fieldsTimeZoneOverride);
				
				TimeZone reportTimeZone = (TimeZone) getParameterValue(JRParameter.REPORT_TIME_ZONE, true);
				dataSource.setReportTimeZone(reportTimeZone);
			}
			catch (SQLTimeoutException e)
			{
				throw
					new JRException(
						EXCEPTION_MESSAGE_KEY_QUERY_STATEMENT_TIMEOUT_LIMIT_EXCEEDED,
						new Object[]{dataset.getName()},
						e);
			}
			catch (SQLException e)
			{
				throw 
					new JRException(
						EXCEPTION_MESSAGE_KEY_QUERY_STATEMENT_EXECUTE_ERROR,
						new Object[]{dataset.getName()},
						e);
			}
		}
		
		return dataSource;
	}
	
	
	protected void createStatement() throws JRException
	{
		String queryString = getQueryString();
		
		if (log.isDebugEnabled())
		{
			log.debug("SQL query string: " + queryString);
		}
		
		if (connection != null && queryString != null && queryString.trim().length() > 0)
		{
			try
			{
				isProcedureCall = isProcedureCall(queryString);
				CallableStatement callableStatement = null;
				
				String type = getPropertiesUtil().getProperty(dataset,	JRJdbcQueryExecuterFactory.PROPERTY_JDBC_RESULT_SET_TYPE);
				String concurrency = getPropertiesUtil().getProperty(dataset, JRJdbcQueryExecuterFactory.PROPERTY_JDBC_CONCURRENCY);
				String holdability = getPropertiesUtil().getProperty(dataset, JRJdbcQueryExecuterFactory.PROPERTY_JDBC_HOLDABILITY);
				
				if (type == null && concurrency == null && holdability == null)
				{
					if (isProcedureCall)
					{
						statement = callableStatement = connection.prepareCall(queryString);
					}
					else
					{
						statement = connection.prepareStatement(queryString);
					}
				}
				else
				{
					type = type == null ? TYPE_FORWARD_ONLY : type; 
					concurrency = concurrency == null ? CONCUR_READ_ONLY : concurrency; 
			
					if (holdability == null)
					{
						if (isProcedureCall)
						{
							statement = callableStatement =
									connection.prepareCall(
										queryString, 
										getResultSetType(type), 
										getConcurrency(concurrency)
										);
						}
						else
						{
							statement = 
									connection.prepareStatement(
										queryString, 
										getResultSetType(type), 
										getConcurrency(concurrency)
										);
						}
					}
					else
					{
						if (isProcedureCall)
						{
							statement = callableStatement =
									connection.prepareCall(
										queryString, 
										getResultSetType(type), 
										getConcurrency(concurrency),
										getHoldability(holdability, connection)
										);
						}
						else
						{
							statement = 
									connection.prepareStatement(
										queryString, 
										getResultSetType(type), 
										getConcurrency(concurrency),
										getHoldability(holdability, connection)
										);
						}
					}
				}
				
				int fetchSize = getPropertiesUtil().getIntegerProperty(dataset,
						JRJdbcQueryExecuterFactory.PROPERTY_JDBC_FETCH_SIZE,
						0);
				if (fetchSize != 0)
				{
					statement.setFetchSize(fetchSize);
				}
				
				int maxFieldSize = getPropertiesUtil().getIntegerProperty(dataset,
						JRJdbcQueryExecuterFactory.PROPERTY_JDBC_MAX_FIELD_SIZE,
						0);//FIXMENOW check the default of all zero default properties
				if (maxFieldSize != 0)
				{
					statement.setMaxFieldSize(maxFieldSize);
				}
				
				Integer queryTimeoutValue = getPropertiesUtil().getIntegerProperty(dataset,
						JRJdbcQueryExecuterFactory.PROPERTY_JDBC_QUERY_TIMEOUT);
				if (queryTimeoutValue != null && queryTimeoutValue >= 0)
				{
					statement.setQueryTimeout(queryTimeoutValue);
				}
				
				Integer reportMaxCount = (Integer) getParameterValue(JRParameter.REPORT_MAX_COUNT);
				if (reportMaxCount != null)
				{
					statement.setMaxRows(reportMaxCount);
				}
				
				if (isProcedureCall)
				{
					procedureCallHandler.init(callableStatement);
				}

				visitQueryParameters(new QueryParameterVisitor()
				{
					int paramIdx = 1;
					
					@Override
					public void visit(QueryParameter queryParameter)
					{
						try
						{
							if (queryParameter.isMulti())
							{
								paramIdx += setStatementMultiParameters(paramIdx, queryParameter.getName(), queryParameter.isIgnoreNulls());
							}
							else
							{
								setStatementParameter(paramIdx, queryParameter.getName());
								++paramIdx;
							}
						}
						catch (SQLException e)
						{
							throw new VisitExceptionWrapper(e);
						}
					}
					
					@Override
					public void visit(ValuedQueryParameter valuedQueryParameter)
					{
						// assuming a single value for now
						Class<?> type = valuedQueryParameter.getType();
						Object value = valuedQueryParameter.getValue();
						if (type == null)
						{
							type = value == null ? Object.class : value.getClass();
						}
						
						if (log.isDebugEnabled())
						{
							log.debug("Parameter #" + paramIdx + " (of type " + type.getName() + "): " + value);
						}
						
						try
						{
							setStatementParameter(paramIdx, type, value, dataset);// using only dataset properties for now
							++paramIdx;
						}
						catch (SQLException e)
						{
							throw new VisitExceptionWrapper(e);
						}
					}
				});
			}
			catch (VisitExceptionWrapper e)
			{
				throw 
					new JRException(
						EXCEPTION_MESSAGE_KEY_QUERY_STATEMENT_PREPARE_ERROR,
						new Object[]{queryString}, 
						e.getCause());
			}
			catch (SQLException e)
			{
				throw 
					new JRException(
						EXCEPTION_MESSAGE_KEY_QUERY_STATEMENT_PREPARE_ERROR,
						new Object[]{queryString}, 
						e);
			}
		}
	}

	protected boolean isProcedureCall(String queryString) throws SQLException
	{
		String factoryClass = JRPropertiesUtil.getInstance(getJasperReportsContext()).getProperty(dataset, ProcedureCallHandlerFactory.PROPERTY_PROCEDURE_CALL_HANDLER_FACTORY);
		if (factoryClass != null && factoryClass.trim().length() > 0)
		{
			try
			{
				ProcedureCallHandlerFactory factory = procedureCallHandlerFactoryCache.getCachedInstance(factoryClass);
				procedureCallHandler = factory.createProcedureCallHandler();
			}
			catch (JRException e)
			{
				throw new JRRuntimeException(e);
			}
		}
		
		return procedureCallHandler == null ? false : procedureCallHandler.isHandling(connection, queryString);
	}


	public ResultSet getResultSet() {
		return resultSet;
	}


	protected void setStatementParameter(int parameterIndex, String parameterName) throws SQLException
	{
		JRValueParameter parameter = getValueParameter(parameterName);
		Class<?> clazz = parameter.getValueClass();
		Object parameterValue = parameter.getValue();
		
		if (log.isDebugEnabled())
		{
			log.debug("Parameter #" + parameterIndex + " (" + parameterName + " of type " + clazz.getName() + "): " + parameterValue);
		}

		setStatementParameter(parameterIndex, clazz, parameterValue, parameter);
	}


	protected int setStatementMultiParameters(int parameterIndex, String parameterName, boolean ignoreNulls) throws SQLException
	{
		JRValueParameter parameter = getValueParameter(parameterName);
		Object paramValue = parameter.getValue();
		int count;
		int index = 0;
		if (paramValue.getClass().isArray())
		{
			int arrayCount = Array.getLength(paramValue);
			for (count = 0; count < arrayCount; ++count)
			{
				Object value = Array.get(paramValue, count);
				if (!ignoreNulls || value != null)
				{
					setStatementMultiParameter(parameterIndex + index, parameterName, count, value, parameter);
					++index;
				}
			}
		}
		else if (paramValue instanceof Collection<?>)
		{
			Collection<?> values = (Collection<?>) paramValue;
			count = 0;
			for (Iterator<?> it = values.iterator(); it.hasNext(); ++count)
			{
				Object value = it.next();
				
				if (!ignoreNulls || value != null)
				{
					setStatementMultiParameter(parameterIndex + index, parameterName, count, value, parameter);
					++index;
				}
			}
		}
		else
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_UNEXPECTED_MULTI_PARAMETER_TYPE,
					(Object[])null);
		}
		return index;
	}

	
	protected void setStatementMultiParameter(int parameterIndex, String parameterName, int valueIndex, Object value,
			JRPropertiesHolder properties) throws SQLException
	{
		if (value == null)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_MULTI_PARAMETERS_CANNOT_CONTAIN_NULL_VALUES,
					(Object[])null);
		}
		
		Class<?> type = value.getClass();
		
		if (log.isDebugEnabled())
		{
			log.debug("Parameter #" + parameterIndex + 
					" (" + parameterName + "[" + valueIndex + "] of type " + type.getName() + "): " + value);
		}
		
		setStatementParameter(parameterIndex, type, value, properties);
	}

	
	protected void setStatementParameter(int parameterIndex, Class<?> parameterType, Object parameterValue,
			JRPropertiesHolder properties) throws SQLException
	{
		if (java.lang.Boolean.class.isAssignableFrom(parameterType))
		{
			if (parameterValue == null)
			{
				statement.setNull(parameterIndex, Types.BIT);
			}
			else
			{
				statement.setBoolean(parameterIndex, (Boolean)parameterValue);
			}
		}
		else if (java.lang.Byte.class.isAssignableFrom(parameterType))
		{
			if (parameterValue == null)
			{
				statement.setNull(parameterIndex, Types.TINYINT);
			}
			else
			{
				statement.setByte(parameterIndex, (Byte)parameterValue);
			}
		}
		else if (java.lang.Double.class.isAssignableFrom(parameterType))
		{
			if (parameterValue == null)
			{
				statement.setNull(parameterIndex, Types.DOUBLE);
			}
			else
			{
				statement.setDouble(parameterIndex, (Double)parameterValue);
			}
		}
		else if (java.lang.Float.class.isAssignableFrom(parameterType))
		{
			if (parameterValue == null)
			{
				statement.setNull(parameterIndex, Types.FLOAT);
			}
			else
			{
				statement.setFloat(parameterIndex, (Float)parameterValue);
			}
		}
		else if (java.lang.Integer.class.isAssignableFrom(parameterType))
		{
			if (parameterValue == null)
			{
				statement.setNull(parameterIndex, Types.INTEGER);
			}
			else
			{
				statement.setInt(parameterIndex, (Integer)parameterValue);
			}
		}
		else if (java.lang.Long.class.isAssignableFrom(parameterType))
		{
			if (parameterValue == null)
			{
				statement.setNull(parameterIndex, Types.BIGINT);
			}
			else
			{
				statement.setLong(parameterIndex, (Long)parameterValue);
			}
		}
		else if (java.lang.Short.class.isAssignableFrom(parameterType))
		{
			if (parameterValue == null)
			{
				statement.setNull(parameterIndex, Types.SMALLINT);
			}
			else
			{
				statement.setShort(parameterIndex, (Short)parameterValue);
			}
		}
		else if (java.math.BigDecimal.class.isAssignableFrom(parameterType))
		{
			if (parameterValue == null)
			{
				statement.setNull(parameterIndex, Types.DECIMAL);
			}
			else
			{
				statement.setBigDecimal(parameterIndex, (BigDecimal)parameterValue);
			}
		}
		else if (java.lang.String.class.isAssignableFrom(parameterType))
		{
			if (parameterValue == null)
			{
				statement.setNull(parameterIndex, Types.VARCHAR);
			}
			else
			{
				statement.setString(parameterIndex, parameterValue.toString());
			}
		}
		else if (java.sql.Timestamp.class.isAssignableFrom(parameterType))
		{
			setTimestamp(parameterIndex, parameterValue, properties);
		}
		else if (java.sql.Time.class.isAssignableFrom(parameterType))
		{
			setTime(parameterIndex, parameterValue, properties);
		}
		else if (java.util.Date.class.isAssignableFrom(parameterType))
		{
			setDate(parameterIndex, parameterValue, properties);
		}
		else
		{
			if (isProcedureCall)
			{
				boolean handled = procedureCallHandler.setParameterValue(parameterIndex, parameterType, parameterValue);
				if (handled)
				{
					return;
				}
			}
			
			if (parameterValue == null)
			{
				statement.setNull(parameterIndex, Types.JAVA_OBJECT);
			}
			else
			{
				statement.setObject(parameterIndex, parameterValue);
			}
		}
	}


	protected void setTimestamp(int parameterIndex, Object parameterValue, JRPropertiesHolder properties)
			throws SQLException
	{
		if (parameterValue == null)
		{
			statement.setNull(parameterIndex, Types.TIMESTAMP);
		}
		else
		{
			Calendar cal = getParameterCalendar(properties);
			if (log.isDebugEnabled())
			{
				log.debug("setting timestamp parameter " + parameterIndex
						+ " as " + parameterValue
						+ " (" + ((java.sql.Timestamp) parameterValue).getTime() + ")"
						+ " with calendar " + cal);
			}
			if (cal == null)
			{
				statement.setTimestamp(parameterIndex, (java.sql.Timestamp) parameterValue);
			}
			else
			{
				statement.setTimestamp(parameterIndex, (java.sql.Timestamp) parameterValue, cal);
			}
		}
	}


	protected void setTime(int parameterIndex, Object parameterValue, JRPropertiesHolder properties)
			throws SQLException
	{
		if (parameterValue == null)
		{
			statement.setNull(parameterIndex, Types.TIME);
		}
		else
		{
			Calendar cal = getParameterCalendar(properties);
			if (log.isDebugEnabled())
			{
				log.debug("setting time parameter " + parameterIndex
						+ " as " + parameterValue
						+ " (" + ((java.sql.Time) parameterValue).getTime() + ")"
						+ " with calendar " + cal);
			}
			if (cal == null)
			{
				statement.setTime(parameterIndex, (java.sql.Time) parameterValue);
			}
			else
			{
				statement.setTime(parameterIndex, (java.sql.Time) parameterValue, cal);
			}
		}
	}


	protected void setDate(int parameterIndex, Object parameterValue, JRPropertiesHolder properties)
			throws SQLException
	{
		if (parameterValue == null)
		{
			statement.setNull(parameterIndex, Types.DATE);
		}
		else
		{
			Calendar cal = getParameterCalendar(properties);
			if (log.isDebugEnabled())
			{
				log.debug("setting date parameter " + parameterIndex
						+ " as " + parameterValue
						+ " (" + ((java.util.Date) parameterValue).getTime() + ")"
						+ " with calendar " + cal);
			}
			if (cal == null)
			{
				statement.setDate(parameterIndex, new java.sql.Date(((java.util.Date)parameterValue).getTime()));
			}
			else
			{
				statement.setDate(parameterIndex, new java.sql.Date(((java.util.Date)parameterValue).getTime()), cal);
			}
		}
	}

	protected Calendar getParameterCalendar(JRPropertiesHolder properties)
	{
		TimeZone tz;
		if (parametersTimeZoneOverride)
		{
			// if we have a parameter, use it
			tz = parametersTimeZone;
		}
		else
		{
			if (properties.hasProperties() && properties.getPropertiesMap().containsProperty(
					JRJdbcQueryExecuterFactory.PROPERTY_TIME_ZONE))
			{
				// read the parameter level property
				String timezoneId = getPropertiesUtil().getProperty(properties, 
						JRJdbcQueryExecuterFactory.PROPERTY_TIME_ZONE);
				if (log.isDebugEnabled())
				{
					log.debug("parameter timezone property " + timezoneId);
				}
				tz = resolveTimeZone(timezoneId);
			}
			else
			{
				// dataset/default property
				tz = parametersTimeZone;
			}
		}

		// using default JVM locale for the calendar
		Calendar cal = tz == null ? null : Calendar.getInstance(tz);
		return cal;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jasperreports.engine.util.JRQueryExecuter#close()
	 */
	@Override
	public synchronized void close()
	{
		if (resultSet != null)
		{
			try
			{
				resultSet.close();
			}
			catch (SQLException e)
			{
				log.error("Error while closing result set.", e);
			}
			finally
			{
				resultSet = null;
			}
		}
		
		if (statement != null)
		{
			closeStatement();
		}
	}

	protected void closeStatement()
	{
		try
		{
			statement.close();
		}
		catch (SQLException e)
		{
			log.error("Error while closing statement.", e);
		}
		finally
		{
			statement = null;
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jasperreports.engine.util.JRQueryExecuter#cancelQuery()
	 */
	@Override
	public synchronized boolean cancelQuery() throws JRException
	{
		if (statement != null)
		{
			try
			{
				statement.cancel();
				return true;
			}
			catch (Exception e)
			{
				throw 
					new JRException(
						EXCEPTION_MESSAGE_KEY_QUERY_STATEMENT_CANCEL_ERROR,
						null,
						e);
			}
		}
		
		return false;
	}
	
	protected static int getResultSetType(String type)
	{
		if (TYPE_FORWARD_ONLY.equals(type))
		{
			return ResultSet.TYPE_FORWARD_ONLY;
		}
		else if (TYPE_SCROLL_INSENSITIVE.equals(type))
		{
			return ResultSet.TYPE_SCROLL_INSENSITIVE;
		}
		else if (TYPE_SCROLL_SENSITIVE.equals(type))
		{
			return ResultSet.TYPE_SCROLL_SENSITIVE;
		}
		
		return ResultSet.TYPE_FORWARD_ONLY;
	}
	
	protected static int getConcurrency(String concurrency)
	{
		if (CONCUR_READ_ONLY.equals(concurrency))
		{
			return ResultSet.CONCUR_READ_ONLY;
		}
		else if (CONCUR_UPDATABLE.equals(concurrency))
		{
			return ResultSet.CONCUR_UPDATABLE;
		}
		
		return ResultSet.CONCUR_READ_ONLY;
	}
	
	protected static int getHoldability(String holdability, Connection connection) throws SQLException
	{
		if (HOLD_CURSORS_OVER_COMMIT.equals(holdability))
		{
			return ResultSet.HOLD_CURSORS_OVER_COMMIT;
		}
		else if (CLOSE_CURSORS_AT_COMMIT.equals(holdability))
		{
			return ResultSet.CLOSE_CURSORS_AT_COMMIT;
		}
		
		return connection.getHoldability();
	}
	
}

/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2023 Cloud Software Group, Inc. All rights reserved.
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

/*
 * Contributors:
 * S. Brett Sutton - bsutton@idatam.com.au
 */
package net.sf.jasperreports.engine;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.query.JRJdbcQueryExecuterFactory;
import net.sf.jasperreports.engine.util.JRImageLoader;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * This is a default implementation of the
 * {@link net.sf.jasperreports.engine.JRDataSource} interface. Since most reports are
 * generated using data from a relational database, JasperReports includes by default this
 * implementation that wraps a <code>java.sql.ResultSet</code> object.
 * <p>
 * This class can be instantiated intentionally to wrap already loaded result sets before
 * passing them to the report-filling routines, but it is also used by the reporting engine to
 * wrap the data retrieved from the database after having executed the report query (if
 * present) through JDBC.

 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRResultSetDataSource implements JRDataSource
{

	private static final Log log = LogFactory.getLog(JRResultSetDataSource.class);

	/**
	 * Property specifying the result set column name for the dataset field.
	 */
	@Property (
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.FIELD},
			scopeQualifications = {JRJdbcQueryExecuterFactory.QUERY_EXECUTER_NAME},
			sinceVersion = PropertyConstants.VERSION_6_3_1
	)
	public static final String PROPERTY_FIELD_COLUMN_NAME = JRPropertiesUtil.PROPERTY_PREFIX + "sql.field.column.name";
	
	/**
	 * Property specifying the result set column label for the dataset field.
	 */
	@Property (
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.FIELD},
			scopeQualifications = {JRJdbcQueryExecuterFactory.QUERY_EXECUTER_NAME},
			sinceVersion = PropertyConstants.VERSION_6_3_1
	)
	public static final String PROPERTY_FIELD_COLUMN_LABEL = JRPropertiesUtil.PROPERTY_PREFIX + "sql.field.column.label";
	
	/**
	 * Property specifying the result set column index for the dataset field.
	 */
	@Property (
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.FIELD},
			scopeQualifications = {JRJdbcQueryExecuterFactory.QUERY_EXECUTER_NAME},
			sinceVersion = PropertyConstants.VERSION_6_3_1,
			valueType = Integer.class
	)
	public static final String PROPERTY_FIELD_COLUMN_INDEX = JRPropertiesUtil.PROPERTY_PREFIX + "sql.field.column.index";
	
	public static final String INDEXED_COLUMN_PREFIX = "COLUMN_";
	private static final int INDEXED_COLUMN_PREFIX_LENGTH = INDEXED_COLUMN_PREFIX.length();
	
	public static final String EXCEPTION_MESSAGE_KEY_RESULT_SET_CLOB_VALUE_READ_FAILURE = "data.result.set.clob.value.read.failure";
	public static final String EXCEPTION_MESSAGE_KEY_RESULT_SET_COLUMN_INDEX_OUT_OF_RANGE = "data.result.set.column.index.out.of.range";
	public static final String EXCEPTION_MESSAGE_KEY_RESULT_SET_FIELD_VALUE_NOT_RETRIEVED = "data.result.set.field.value.not.retrieved";
	public static final String EXCEPTION_MESSAGE_KEY_RESULT_SET_METADATA_NOT_RETRIEVED = "data.result.set.metadata.not.retrieved";
	public static final String EXCEPTION_MESSAGE_KEY_RESULT_SET_UNKNOWN_COLUMN_NAME = "data.result.set.unknown.column.name";
	public static final String EXCEPTION_MESSAGE_KEY_RESULT_SET_UNKNOWN_COLUMN_LABEL = "data.result.set.unknown.column.label";
	public static final String EXCEPTION_MESSAGE_KEY_RESULT_SET_NEXT_RECORD_NOT_RETRIEVED = "data.result.set.next.record.not.retrieved";


	/**
	 *
	 */
	private JasperReportsContext jasperReportsContext;
	private ResultSet resultSet;
	private Map<String,Integer> columnIndexMap = new HashMap<>();

	private TimeZone timeZone;
	private boolean timeZoneOverride;
	private TimeZone reportTimeZone;
	private Map<JRField, Calendar> fieldCalendars = new HashMap<>();

	private int rowCount;
	
	private Boolean supportsTypedGetObjectMethod;

	/**
	 *
	 */
	public JRResultSetDataSource(JasperReportsContext jasperReportsContext, ResultSet resultSet)
	{
		this.jasperReportsContext = jasperReportsContext;
		this.resultSet = resultSet;
		this.rowCount = 0;
	}


	/**
	 * @see #JRResultSetDataSource(JasperReportsContext, ResultSet)
	 */
	public JRResultSetDataSource(ResultSet resultSet)
	{
		this(DefaultJasperReportsContext.getInstance(), resultSet);
	}

	/**
	 * Access the result set that this data source is based on.
	 * 
	 * @return the result set used by this data source
	 */
	public ResultSet getResultSet()
	{
		return resultSet;
	}

	@Override
	public boolean next() throws JRException
	{
		boolean hasNext = false;
		
		if (resultSet != null)
		{
			try
			{
				hasNext = resultSet.next();
			}
			catch (SQLException e)
			{
				throw 
					new JRException(
						EXCEPTION_MESSAGE_KEY_RESULT_SET_NEXT_RECORD_NOT_RETRIEVED, 
						null,
						e);
			}

			if (hasNext)
			{
				++rowCount;
			}
			else if (log.isDebugEnabled())
			{
				log.debug("read " + rowCount + " rows from result set");
			}
		}
		
		return hasNext;
	}


	@Override
	public Object getFieldValue(JRField field) throws JRException
	{
		Object objValue = null;

		if (field != null && resultSet != null)
		{
			Integer columnIndex = getColumnIndex(field);
			Class<?> clazz = field.getValueClass();

			try
			{
				if (clazz.equals(java.lang.Boolean.class))
				{
					objValue = resultSet.getBoolean(columnIndex);
					if(resultSet.wasNull())
					{
						objValue = null;
					}
				}
				else if (clazz.equals(java.lang.Byte.class))
				{
					objValue = resultSet.getByte(columnIndex);
					if(resultSet.wasNull())
					{
						objValue = null;
					}
				}
				else if (
					clazz.equals(java.util.Date.class)
					|| clazz.equals(java.sql.Date.class)
					)
				{
					objValue = readDate(columnIndex, field);
				}
				else if (clazz.equals(java.sql.Timestamp.class))
				{
					objValue = readTimestamp(columnIndex, field);
				}
				else if (clazz.equals(java.sql.Time.class))
				{
					objValue = readTime(columnIndex, field);
				}
				else if (clazz.equals(java.lang.Double.class))
				{
					objValue = resultSet.getDouble(columnIndex);
					if(resultSet.wasNull())
					{
						objValue = null;
					}
				}
				else if (clazz.equals(java.lang.Float.class))
				{
					objValue = resultSet.getFloat(columnIndex);
					if(resultSet.wasNull())
					{
						objValue = null;
					}
				}
				else if (clazz.equals(java.lang.Integer.class))
				{
					objValue = resultSet.getInt(columnIndex);
					if(resultSet.wasNull())
					{
						objValue = null;
					}
				}
				else if (clazz.equals(java.io.InputStream.class))
				{
					byte[] bytes = readBytes(columnIndex);
					
					if(bytes == null)
					{
						objValue = null;
					}
					else
					{
						objValue = new ByteArrayInputStream(bytes);
					}					
				}
				else if (clazz.equals(java.lang.Long.class))
				{
					objValue = resultSet.getLong(columnIndex);
					if(resultSet.wasNull())
					{
						objValue = null;
					}
				}
				else if (clazz.equals(java.lang.Short.class))
				{
					objValue = resultSet.getShort(columnIndex);
					if(resultSet.wasNull())
					{
						objValue = null;
					}
				}
				else if (clazz.equals(java.math.BigDecimal.class))
				{
					objValue = resultSet.getBigDecimal(columnIndex);
					if(resultSet.wasNull())
					{
						objValue = null;
					}
				}
				else if (clazz.equals(java.lang.String.class))
				{
					int columnType = resultSet.getMetaData().getColumnType(columnIndex);
					switch (columnType)
					{
						case Types.CLOB:
							Clob clob = resultSet.getClob(columnIndex);
							if (resultSet.wasNull())
							{
								objValue = null;
							}
							else
							{
								objValue = clobToString(clob);
							}
							break;
							
						default:
							objValue = resultSet.getString(columnIndex);
							if(resultSet.wasNull())
							{
								objValue = null;
							}
							break;
					}
				}
				else if (clazz.equals(Clob.class))
				{
					objValue = resultSet.getClob(columnIndex);
					if(resultSet.wasNull())
					{
						objValue = null;
					}
				}
				else if (clazz.equals(Reader.class))
				{
					Reader reader = null;
					long size = -1;
					
					int columnType = resultSet.getMetaData().getColumnType(columnIndex);
					switch (columnType)
					{
						case Types.CLOB:
							Clob clob = resultSet.getClob(columnIndex);
							if (!resultSet.wasNull())
							{
								reader = clob.getCharacterStream();
								size = clob.length();
							}
							break;
							
						default:
							reader = resultSet.getCharacterStream(columnIndex);
							if (resultSet.wasNull())
							{
								reader = null; 
							}
					}
					
					if (reader == null)
					{
						objValue = null;
					}
					else
					{
						objValue = getArrayReader(reader, size);
					}
				}
				else if (clazz.equals(Blob.class))
				{
					objValue = resultSet.getBlob(columnIndex);
					if(resultSet.wasNull())
					{
						objValue = null;
					}
				}
				else if (clazz.equals(Image.class))
				{
					byte[] bytes = readBytes(columnIndex);
					
					if(bytes == null)
					{
						objValue = null;
					}
					else
					{
						objValue = JRImageLoader.getInstance(jasperReportsContext).loadAwtImageFromBytes(bytes);
					}					
				}
				else if (clazz.equals(byte[].class))
				{
					objValue = readBytes(columnIndex);
				}
				else if (clazz.equals(Object.class))
				{
					// there is no point in getting typed value when class is java.lang.Object;
					// some drivers even fail trying to convert to java.lang.Object, if typed getObject(int, Class)
					// getter is used as in the next else block
					objValue = resultSet.getObject(columnIndex);
				}
				else
				{
					if (supportsTypedGetObjectMethod == null)
					{
						try
						{
							objValue = resultSet.getObject(columnIndex, clazz);
							supportsTypedGetObjectMethod = true;
						}
						catch (AbstractMethodError e)
						{
							supportsTypedGetObjectMethod = false;
							objValue = resultSet.getObject(columnIndex);
						}
						catch (SQLException e)
						{
							// although implemented by the driver, the getObject(int, Class) might still fail for some other reason,
							// so we fallback to getObject(int), but it does not mean we are not going to try it again next time
							if (log.isDebugEnabled())
							{
								log.debug("ResultSet.getObject(int, Class) method call failed.", e);
							}
							
							objValue = resultSet.getObject(columnIndex);
						}
					}
					else
					{
						if (supportsTypedGetObjectMethod)
						{
							objValue = resultSet.getObject(columnIndex, clazz);
						}
						else
						{
							objValue = resultSet.getObject(columnIndex);
						}
					}
				}
			}
			catch (Exception e)
			{
				throw 
					new JRException(
						EXCEPTION_MESSAGE_KEY_RESULT_SET_FIELD_VALUE_NOT_RETRIEVED,
						new Object[]{field.getName(), clazz.getName()}, 
						e);
			}
		}
		
		return objValue;
	}


	protected Object readDate(Integer columnIndex, JRField field) throws SQLException
	{
		Calendar calendar = getFieldCalendar(field);
		java.sql.Date objValue = calendar == null ? resultSet.getDate(columnIndex)
				: resultSet.getDate(columnIndex, calendar);
		if(resultSet.wasNull())
		{
			objValue = null;
		} 
		if (log.isDebugEnabled())
		{
			log.debug("date field " + field.getName()
					+ " is " + (objValue == null ? "null"
							: (objValue + " (" + objValue.getTime() + ")")));
		}
		return objValue;
	}


	protected Object readTimestamp(Integer columnIndex, JRField field) throws SQLException
	{
		Calendar calendar = getFieldCalendar(field);
		java.sql.Timestamp objValue = calendar == null ? resultSet.getTimestamp(columnIndex)
				: resultSet.getTimestamp(columnIndex, calendar);
		if(resultSet.wasNull())
		{
			objValue = null;
		}
		if (log.isDebugEnabled())
		{
			log.debug("timestamp field " + field.getName()
					+ " is " + (objValue == null ? "null"
							: (objValue + " (" + objValue.getTime() + ")")));
		}
		return objValue;
	}


	protected Object readTime(Integer columnIndex, JRField field) throws SQLException
	{
		Calendar calendar = getFieldCalendar(field);
		java.sql.Time objValue = calendar == null ? resultSet.getTime(columnIndex)
				: resultSet.getTime(columnIndex, calendar);
		if(resultSet.wasNull())
		{
			objValue = null;
		}
		if (log.isDebugEnabled())
		{
			log.debug("time field " + field.getName()
					+ " is " + (objValue == null ? "null"
							: (objValue + " (" + objValue.getTime() + ")")));
		}
		return objValue;
	}





	/**
	 *
	 */
	private Integer getColumnIndex(JRField field) throws JRException
	{
		String fieldName = field.getName();
		Integer columnIndex = columnIndexMap.get(fieldName);
		if (columnIndex == null)
		{
			try
			{
				columnIndex = searchColumnByLabel(field);
				
				if (columnIndex == null)
				{
					columnIndex = searchColumnByName(field);
				}
				
				if (columnIndex == null)
				{
					columnIndex = searchColumnByIndex(field);
				}
				
				if (columnIndex == null)
				{
					columnIndex = searchColumnByLabel(fieldName);
				}
				
				if (columnIndex == null)
				{
					columnIndex = searchColumnByName(fieldName);
				}
				
				if (columnIndex == null && fieldName.startsWith(INDEXED_COLUMN_PREFIX))
				{
					columnIndex = searchColumnByIndex(fieldName.substring(INDEXED_COLUMN_PREFIX_LENGTH));
				}
				
				if (columnIndex == null)
				{
					throw 
						new JRException(
							EXCEPTION_MESSAGE_KEY_RESULT_SET_UNKNOWN_COLUMN_NAME,
							new Object[]{fieldName});
				}
			}
			catch (SQLException e)
			{
				throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_RESULT_SET_METADATA_NOT_RETRIEVED, 
					null, 
					e);
			}

			if (log.isDebugEnabled())
			{
				try
				{
					ResultSetMetaData metaData = resultSet.getMetaData();
					log.debug("field " + fieldName 
							+ " has type " + metaData.getColumnType(columnIndex)
							+ "/" + metaData.getColumnTypeName(columnIndex)
							+ ", class " + metaData.getColumnClassName(columnIndex));
				}
				catch (SQLException e)
				{
					log.debug("failed to read result set metadata", e);
				}
			}
			
			columnIndexMap.put(fieldName, columnIndex);
		}
		
		return columnIndex;
	}


	protected Integer searchColumnByName(JRField field) throws SQLException, JRException
	{
		if (field.hasProperties())
		{
			String name = field.getPropertiesMap().getProperty(PROPERTY_FIELD_COLUMN_NAME);
			if (name != null)
			{
				Integer columnIndex = searchColumnByName(name);
				if (columnIndex == null)
				{
					throw 
						new JRException(
							EXCEPTION_MESSAGE_KEY_RESULT_SET_UNKNOWN_COLUMN_NAME,
							new Object[]{name});
				}
				return columnIndex;
			}
		}

		return null;
	}


	protected Integer searchColumnByName(String name) throws SQLException
	{
		Integer columnIndex = null;
		ResultSetMetaData metadata = resultSet.getMetaData();
		for (int i = 1; i <= metadata.getColumnCount(); i++)
		{
			String columnName = metadata.getColumnName(i);
			if (name.equalsIgnoreCase(columnName))
			{
				columnIndex = i;
				break;
			}
		}
		return columnIndex;
	}


	protected Integer searchColumnByLabel(JRField field) throws SQLException, JRException
	{
		if (field.hasProperties())
		{
			String label = field.getPropertiesMap().getProperty(PROPERTY_FIELD_COLUMN_LABEL);
			if (label != null)
			{
				Integer columnIndex = searchColumnByLabel(label);
				if (columnIndex == null)
				{
					throw 
						new JRException(
							EXCEPTION_MESSAGE_KEY_RESULT_SET_UNKNOWN_COLUMN_LABEL,
							new Object[]{label});
				}
				return columnIndex;
			}
		}
		return null;
	}


	protected Integer searchColumnByLabel(String label) throws SQLException
	{
		Integer columnIndex = null;
		ResultSetMetaData metadata = resultSet.getMetaData();
		for (int i = 1; i <= metadata.getColumnCount(); i++)
		{
			String columnLabel = metadata.getColumnLabel(i);
			if (columnLabel != null && label.equalsIgnoreCase(columnLabel))
			{
				columnIndex = i;
				break;
			}
		}
		return columnIndex;
	}


	protected Integer searchColumnByIndex(JRField field) throws SQLException, JRException
	{
		if (field.hasProperties())
		{
			String index = field.getPropertiesMap().getProperty(PROPERTY_FIELD_COLUMN_INDEX);
			if (index != null)
			{
				return searchColumnByIndex(index);
			}
		}

		return null;
	}


	protected Integer searchColumnByIndex(String index) throws SQLException, JRException
	{
		Integer columnIndex = Integer.valueOf(index);
		if (
			columnIndex <= 0
			|| columnIndex > resultSet.getMetaData().getColumnCount()
			)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_RESULT_SET_COLUMN_INDEX_OUT_OF_RANGE,
					new Object[]{columnIndex});
		}

		return columnIndex;
	}


	protected String clobToString(Clob clob) throws JRException
	{
		try
		{
			int bufSize = 8192;
			char[] buf = new char[bufSize];
			
			Reader reader = new BufferedReader(clob.getCharacterStream(), bufSize);
			StringBuilder str = new StringBuilder((int) clob.length());
			
			for (int read = reader.read(buf); read > 0; read = reader.read(buf))
			{
				str.append(buf, 0, read);
			}

			return str.toString();
		}
		catch (SQLException | IOException e)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_RESULT_SET_CLOB_VALUE_READ_FAILURE, 
					null, 
					e);
		}
	}

	protected CharArrayReader getArrayReader(Reader reader, long size) throws IOException
	{
		char[] buf = new char[8192];
		CharArrayWriter bufWriter = new CharArrayWriter((size > 0) ? (int) size : 8192);
		
		BufferedReader bufReader = new BufferedReader(reader, 8192);
		for (int read = bufReader.read(buf); read > 0; read = bufReader.read(buf))
		{
			bufWriter.write(buf, 0, read);
		}
		bufWriter.flush();
		
		return new CharArrayReader(bufWriter.toCharArray());
	}

	protected byte[] readBytes(Integer columnIndex) throws SQLException, IOException
	{
		InputStream is = null;
		long size = -1;
		
		int columnType = resultSet.getMetaData().getColumnType(columnIndex);
		switch (columnType)
		{
			case Types.BLOB:
				Blob blob = resultSet.getBlob(columnIndex);
				if (!resultSet.wasNull())
				{
					is = blob.getBinaryStream();
					size = blob.length();
				}
				break;
				
			default:
				is = resultSet.getBinaryStream(columnIndex);
				if (resultSet.wasNull())
				{
					is = null; 
				}
		}
		
		byte[] bytes = null;
		if (is != null)
		{
			bytes = readBytes(is, size);
		}
		
		return bytes;
	}

	protected byte[] readBytes(InputStream is, long size) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream(size > 0 ? (int) size : 1000);
		byte[] bytes = new byte[1000];
		int ln = 0;
		try
		{
			while ((ln = is.read(bytes)) > 0)
			{
				baos.write(bytes, 0, ln);
			}
			baos.flush();
		}
		finally
		{
			try
			{
				baos.close();
			}
			catch(IOException e)
			{
			}
		}
		return baos.toByteArray();
	}

	/**
	 * Sets the default time zone to be used for retrieving date/time values from the 
	 * result set.
	 * 
	 * In most cases no explicit time zone conversion would be required for retrieving 
	 * date/time values from the DB, and this parameter should be null.  
	 * 
	 * @param timeZone the default time zone
	 * @param override whether the default time zone overrides time zones specified
	 * as field-level properties
	 * @see JRJdbcQueryExecuterFactory#PROPERTY_TIME_ZONE
	 */
	public void setTimeZone(TimeZone timeZone, boolean override)
	{
		this.timeZone = timeZone;
		this.timeZoneOverride = override;
	}
	
	/**
	 * Sets the report time zone, which is the one used to display datetime values in the report.
	 * 
	 * The time zone is used when the {@link JRJdbcQueryExecuterFactory#PROPERTY_TIME_ZONE} property
	 * is set to REPORT_TIME_ZONE.
	 * 
	 * @param reportTimeZone the time zone used to display datetime values in the report
	 */
	public void setReportTimeZone(TimeZone reportTimeZone)
	{
		this.reportTimeZone = reportTimeZone;
	}
	
	protected Calendar getFieldCalendar(JRField field)
	{
		if (fieldCalendars.containsKey(field))
		{
			return fieldCalendars.get(field);
		}
		
		Calendar calendar = createFieldCalendar(field);
		fieldCalendars.put(field, calendar);
		if (log.isDebugEnabled())
		{
			log.debug("calendar for field " + field.getName()
					+ " is " + calendar);
		}
		return calendar;
	}

	protected Calendar createFieldCalendar(JRField field)
	{
		TimeZone tz;
		if (timeZoneOverride)
		{
			// if we have a parameter, use it
			tz = timeZone;
		}
		else
		{
			if (field.hasProperties() && field.getPropertiesMap().containsProperty(
					JRJdbcQueryExecuterFactory.PROPERTY_TIME_ZONE))
			{
				// read the field level property
				String timezoneId = JRPropertiesUtil.getInstance(jasperReportsContext).getProperty(field, 
						JRJdbcQueryExecuterFactory.PROPERTY_TIME_ZONE);
				tz = resolveTimeZone(timezoneId);
			}
			else
			{
				// dataset/default property
				tz = timeZone;
			}
		}

		// using default JVM locale for the calendar
		Calendar cal = tz == null ? null : Calendar.getInstance(tz);
		return cal;
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
			tz = reportTimeZone;
		}
		else
		{
			// resolving as tz ID
			tz = TimeZone.getTimeZone(timezoneId);
		}
		return tz;
	}

}

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
package net.sf.jasperreports.data.cache;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;

import net.sf.jasperreports.engine.JRField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Report data cache handler that collects data in column stores.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ColumnDataCacheHandler implements DataCacheHandler
{

	private static final Log log = LogFactory.getLog(ColumnDataCacheHandler.class);

	private final static int DEFAULT_BUFFER_STORE_SIZE = 4096;
	
	private boolean recordingEnabled;

	private volatile DataSnapshot snapshot;
	private int bufferStoreSize = DEFAULT_BUFFER_STORE_SIZE;
	
	public ColumnDataCacheHandler()
	{
		recordingEnabled = true;
	}
	
	@Override
	public boolean isRecordingEnabled()
	{
		return recordingEnabled;
	}

	@Override
	public DataRecorder createDataRecorder()
	{
		if (log.isDebugEnabled())
		{
			log.debug("creating data recorder");
		}
		
		return new DataCollector();
	}

	@Override
	public DataSnapshot getDataSnapshot()
	{
		return snapshot;
	}
	
	protected void disableCaching()
	{
		if (log.isDebugEnabled())
		{
			log.debug("caching disabled");
		}
		
		this.recordingEnabled = false;
	}

	protected void setDataSnapshot(DataSnapshot snapshot)
	{
		this.snapshot = snapshot;
	}

	@Override
	public boolean isSnapshotPopulated()
	{
		return snapshot != null;
	}
	
	protected ColumnStore createColumnStore(JRField field)
	{
		return new TypedColumnStore(this, field.getValueClass());
	}
	
	protected ColumnStore createColumnStore(Class<?> type)
	{
		BufferColumnStore bufferStore = null;
		if (Integer.class.equals(type))
		{
			bufferStore = new LongArrayStore(bufferStoreSize, NumberToIntegerTransformer.instance());
		}
		else if (Long.class.equals(type))
		{
			bufferStore = new LongArrayStore(bufferStoreSize, NumberToLongTransformer.instance());
		}
		else if (Short.class.equals(type))
		{
			bufferStore = new LongArrayStore(bufferStoreSize, NumberToShortTransformer.instance());
		}
		else if (Byte.class.equals(type))
		{
			bufferStore = new LongArrayStore(bufferStoreSize, NumberToByteTransformer.instance());
		}
		else if (Character.class.equals(type))
		{
			bufferStore = new LongArrayStore(bufferStoreSize, NumberToCharTransformer.instance());
		}
		else if (Double.class.equals(type))
		{
			bufferStore = new DoubleArrayStore(bufferStoreSize);
		}
		else if (Float.class.equals(type))
		{
			bufferStore = new FloatArrayStore(bufferStoreSize); 
		}
		else if (String.class.equals(type))
		{
			bufferStore = new ObjectArrayStore<String>(String.class, bufferStoreSize); 
		}
		else if (java.sql.Date.class.isAssignableFrom(type))//allow subclasses
		{
			bufferStore = new DateStore(bufferStoreSize, NumberToSQLDateTransformer.instance()); 
		}
		else if (Timestamp.class.isAssignableFrom(type))
		{
			bufferStore = new TimestampStore(bufferStoreSize);
		}
		else if (Time.class.isAssignableFrom(type))
		{
			bufferStore = new DateStore(bufferStoreSize, NumberToSQLTimeTransformer.instance()); 
		}
		else if (Date.class.isAssignableFrom(type))
		{
			bufferStore = new DateStore(bufferStoreSize, NumberToDateTransformer.instance()); 
		}
		else if (BigInteger.class.isAssignableFrom(type))
		{
			bufferStore = new BigIntegerStore(bufferStoreSize);
		}
		else if (BigDecimal.class.isAssignableFrom(type))
		{
			bufferStore = new BigDecimalStore(bufferStoreSize);
		}
		else if (Boolean.class.equals(type))
		{
			bufferStore = new BooleanStore(bufferStoreSize);
		}
		
		ColumnStore store = null;
		if (bufferStore != null)
		{
			store = new BlockColumnStore(bufferStore);
			
			if (log.isDebugEnabled())
			{
				log.debug("created block store " + store + " with buffer " + bufferStore
						+ ", buffer size " + bufferStoreSize);
			}
		}
		return store;
	}
	
	class DataCollector implements DataRecorder
	{

		private final ColumnDataSnapshot dataSnapshot;
		
		public DataCollector()
		{
			this.dataSnapshot = new ColumnDataSnapshot();
		}

		@Override
		public DatasetRecorder createRecorder()
		{
			if (log.isDebugEnabled())
			{
				log.debug("Creating ColumnDataCollector");
			}
			
			ColumnDataCollector collector = new ColumnDataCollector();
			return collector;
		}
		
		@Override
		public void addRecordResult(Object key, Object result)
		{
			ColumnCacheData data = (ColumnCacheData) result;
			if (log.isDebugEnabled())
			{
				log.debug("adding cached data of size " + data.size() + " for for " + key);
			}
			
			if (!data.isSerializable())
			{
				if (log.isDebugEnabled())
				{
					log.debug("cached data not serializable at key " + key);
				}
				
				// mark the snapshot as unpersistable
				disablePersistence();
			}
			
			dataSnapshot.addCachedData(key, data);
		}

		@Override
		public void setSnapshotPopulated()
		{
			if (isEnabled())
			{
				setDataSnapshot(dataSnapshot);
			}
		}

		@Override
		public void disableRecording()
		{
			disableCaching();
		}

		@Override
		public void disablePersistence()
		{
			dataSnapshot.setPersistable(false);
		}

		@Override
		public boolean isEnabled()
		{
			return isRecordingEnabled();
		}
	}
	
	class ColumnDataCollector implements DatasetRecorder
	{
		private JRField[] fields;
		private ColumnStore[] columns;
		private int size;
		private boolean ended;
		private LinkedHashMap<String, Object> parameters;
		
		public ColumnDataCollector()
		{
		}

		@Override
		public void start(JRField[] datasetFields)
		{
			fields = (datasetFields == null) ? new JRField[0] : datasetFields;
			
			columns = new ColumnStore[fields.length];
			for (int i = 0; i < fields.length; i++)
			{
				JRField field = fields[i];
				ColumnStore columnStore = createColumnStore(field);
				if (columnStore == null)
				{
					if (log.isDebugEnabled())
					{
						log.debug("Field " + field.getName() + " of type " + field.getValueClassName()
								+ " not cacheable");
					}
					
					disableCaching();
					return;
				}
				
				if (log.isDebugEnabled())
				{
					log.debug("created store " + columnStore + " for field " + field.getName());
				}
				
				columns[i] = columnStore;
			}
			
			size = 0;
			parameters = null;
			ended = false;
		}

		@Override
		public void addParameter(String name, Object value)
		{
			if (parameters == null)
			{
				parameters = new LinkedHashMap<>();
			}
			
			parameters.put(name, value);
		}

		@Override
		public void addRecord(Object[] values)
		{
			if (!isRecordingEnabled())
			{
				// nothing to do
				return;
			}
			
			if (values == null || values.length != columns.length)
			{
				throw new IllegalArgumentException();
			}
			
			for (int i = 0; i < columns.length; i++)
			{
				columns[i].addValue(values[i]);
			}
			
			++size;
		}

		@Override
		public Object end()
		{
			if (!isRecordingEnabled())
			{
				// nothing to do
				return null;
			}
			
			if (log.isDebugEnabled())
			{
				log.debug("Recorded cached data source of " + size + " records");
			}
			
			ended = true;
			
			String[] fieldNames;
			if (fields == null)
			{
				fieldNames = new String[0];
			}
			else
			{
				fieldNames = new String[fields.length];
				for (int i = 0; i < fields.length; i++)
				{
					fieldNames[i] = fields[i].getName();
				}
			}
			
			ColumnValues[] values = new ColumnValues[columns.length];
			for (int i = 0; i < columns.length; i++)
			{
				values[i] = columns[i].createValues();
			}
			
			StandardColumnCacheData data = new StandardColumnCacheData(fieldNames, size, values, parameters);
			return data;
		}

		@Override
		public boolean hasEnded()
		{
			return ended;
		}
	}
}

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
package net.sf.jasperreports.data.cache;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.data.IndexedDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ColumnDataSnapshot implements DataSnapshot, Serializable
{
	
	private static final Log log = LogFactory.getLog(ColumnDataSnapshot.class);
	
	private static final String EXCEPTION_MESSAGE_KEY_SNAPSHOT_CANNOT_BE_PERSISTED = "data.cache.snapshot.cannot.be.persisted";

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private Map<Object, ColumnCacheData> cachedData;
	private transient boolean persistable;

	public ColumnDataSnapshot()
	{
		cachedData = new LinkedHashMap<>();
		persistable = true;
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		if (!persistable)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_SNAPSHOT_CANNOT_BE_PERSISTED,
					(Object[])null);
		}
		
		out.defaultWriteObject();
	}
	
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		this.persistable = true;

		in.defaultReadObject();
	}

	@Override
	public boolean hasCachedData(Object key)
	{
		return cachedData.containsKey(key);
	}

	@Override
	public CachedDataset getCachedData(Object key) throws DataSnapshotException
	{
		ColumnCacheData cacheData = cachedData.get(key);
		if (cacheData == null)
		{
			if (log.isDebugEnabled())
			{
				log.debug("No cached data exists for " + key);
			}
			
			return null;
		}
		
		if (log.isDebugEnabled())
		{
			log.debug("Found cached data source of " + cacheData.size() + " records for " + key);
		}
		
		IndexedDataSource dataSource = cacheData.createDataSource();
		
		Map<String, Object> parameters = cacheData.getParameters();
		StandardCachedDataset dataset = new StandardCachedDataset(dataSource, parameters);
		return dataset;
	}

	public void addCachedData(Object key, ColumnCacheData data)
	{
		cachedData.put(key, data);
	}

	@Override
	public boolean isPersistable()
	{
		return persistable;
	}

	public void setPersistable(boolean persistable)
	{
		this.persistable = persistable;
	}

}

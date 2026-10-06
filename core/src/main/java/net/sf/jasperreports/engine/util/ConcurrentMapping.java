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
package net.sf.jasperreports.engine.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.sf.jasperreports.engine.JRRuntimeException;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ConcurrentMapping<K, V, C>
{

	public static interface Mapper<K, V, C>
	{
		V compute(K key, C context);
	}
	
	private static class Entry<V>
	{
		private static enum Status
		{
			PENDING,
			AVAILABLE,
			ERROR;
		}
		
		private static class Result<V>
		{
			private Status status;
			private V value;
			
			private Result(Status status, V value)
			{
				this.status = status;
				this.value = value;
			}
			
			public boolean isAvailable()
			{
				return status == Status.AVAILABLE;
			}
			
			public boolean isError()
			{
				return status == Status.ERROR;
			}
		}
		
		private static final Result<?> RESULT_PENDING = new Result<>(Status.PENDING, null);
		private static final Result<?> RESULT_ERROR = new Result<>(Status.ERROR, null);
		
		@SuppressWarnings("unchecked")
		private static <V> Result<V> pendingResult()
		{
			return (Result<V>) RESULT_PENDING;
		}
		
		@SuppressWarnings("unchecked")
		private static <V> Result<V> errorResult()
		{
			return (Result<V>) RESULT_ERROR;
		}
		
		private final Lock lock;
		private final Condition statusCondition;
		private volatile Result<V> result;
		
		private Entry()
		{
			this.lock = new ReentrantLock();
			this.statusCondition = this.lock.newCondition();
			this.result = pendingResult();
		}
		
		private void lock()
		{
			try
			{
				lock.lockInterruptibly();
			}
			catch (InterruptedException e)
			{
				throw new JRRuntimeException(e);
			}
		}
		
		private void unlock()
		{
			lock.unlock();
		}
		
		private void setValue(V value)
		{
			this.result = new Result<>(Status.AVAILABLE, value);
			this.statusCondition.signalAll();
		}
		
		private void setError()
		{
			this.result = errorResult();
			this.statusCondition.signalAll();
		}
		
		private void waitForStatus()
		{
			try
			{
				while (result.status == Entry.Status.PENDING)
				{
					statusCondition.await();
				}
			}
			catch (InterruptedException e)
			{
				throw new JRRuntimeException(e);
			}
		}
	}

	private final Mapper<K, V, C> mapper;
	private ConcurrentHashMap<K, Entry<V>> entries;
	
	public ConcurrentMapping(Mapper<K, V, C> mapper)
	{
		this.mapper = mapper;
		this.entries = new ConcurrentHashMap<>();
	}
	
	public V get(K key, C context)
	{
		Entry<V> existingEntry = entries.get(key);
		Entry<V> newEntry = null;
		V result;
		if (existingEntry == null)
		{
			newEntry = new Entry<>();
			existingEntry = entries.putIfAbsent(key, newEntry);
			if (existingEntry == null)
			{
				result = getNew(newEntry, key, context);
			}
			else
			{
				result = getExisting(existingEntry, key, context);
			}
		}
		else
		{
			result = getExisting(existingEntry, key, context);
		}
		return result;
	}
	
	private V getExisting(Entry<V> entry, K key, C context)
	{
		V result;
		Entry.Result<V> entryResult = entry.result;
		if (entryResult.isAvailable())
		{
			result = entryResult.value;
		}
		else
		{
			entry.lock();
			try
			{
				entry.waitForStatus();
				
				entryResult = entry.result;
				if (entryResult.isAvailable())
				{
					result = entryResult.value;
				}
				else if (entryResult.isError())
				{
					//compute again
					entry.result = Entry.pendingResult();
					result = getNew(entry, key, context);
				}
				else
				{
					//should not happen
					throw new JRRuntimeException("Unexpected entry status " + entry.result.status);
				}
			}
			finally
			{
				entry.unlock();
			}
		}
		return result;
	}

	private V getNew(Entry<V> entry, K key, C context)
	{
		entry.lock();
		try
		{
			V value = null;
			boolean success = false;
			try
			{
				value = mapper.compute(key, context);
				success = true;
				return value;
			}
			finally
			{
				if (success)
				{
					entry.setValue(value);
				}
				else
				{
					entry.setError();
				}
			}
		}
		finally
		{
			entry.unlock();
		}
	}
	
	public void clear()
	{
		entries.clear();
	}
	
	public Iterator<V> currentValues()
	{
		return new ValuesIterator();
	}
	
	private class ValuesIterator implements Iterator<V>
	{
		private Iterator<Entry<V>> entriesIterator;
		private Entry.Result<V> currentResult;

		private ValuesIterator()
		{
			entriesIterator = entries.values().iterator();
		}
		
		private void findAvailable()
		{
			currentResult = null;
			while (currentResult == null && entriesIterator.hasNext())
			{
				Entry<V> entry = entriesIterator.next();
				Entry.Result<V> result = entry.result;
				if (result.isAvailable())
				{
					currentResult = result;
				}
			}
		}

		@Override
		public boolean hasNext()
		{
			return currentResult != null;
		}

		@Override
		public V next()
		{
			if (currentResult == null)
			{
				throw new NoSuchElementException();
			}
			
			V value = currentResult.value;
			findAvailable();
			return value;
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
		
	}
}

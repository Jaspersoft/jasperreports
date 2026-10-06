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

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class AdditionalEntryMap<K, V> extends AbstractMap<K, V>
{

	private Map<K, V> decorated;
	private K additionalKey;
	private V additionalValue;
	
	private AdditionalEntrySet entrySet;
	private AdditionalKeySet keySet;
	private AdditionalValueCollection valueCollection;

	public AdditionalEntryMap(Map<K, V> decorated, K additionalKey, V additionalValue)
	{
		if (decorated == null || additionalKey == null || additionalValue == null)
		{
			throw new IllegalArgumentException("The arguments cannot be null");
		}
		
		if (decorated.containsKey(additionalKey))
		{
			throw new IllegalArgumentException("The additional key should not be present in the map");
		}
		
		this.decorated = decorated;
		this.additionalKey = additionalKey;
		this.additionalValue = additionalValue;
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet()
	{
		Set<Map.Entry<K, V>> set = entrySet;
		if (set == null)
		{
			set = entrySet = new AdditionalEntrySet();
		}
		return set;
	}

	@Override
	public int size()
	{
		return decorated.size() + 1;
	}

	@Override
	public boolean isEmpty()
	{
		return false;
	}

	@Override
	public boolean containsValue(Object value)
	{
		return decorated.containsValue(value) || additionalValue.equals(value);
	}

	@Override
	public boolean containsKey(Object key)
	{
		return decorated.containsKey(key) || additionalKey.equals(key);
	}

	@Override
	public V get(Object key)
	{
		if (additionalKey.equals(key))
		{
			return additionalValue;
		}
		return decorated.get(key);
	}

	@Override
	public Set<K> keySet()
	{
		Set<K> set = keySet;
		if (set == null)
		{
			set = keySet = new AdditionalKeySet();
		}
		return set;
	}

	@Override
	public Collection<V> values()
	{
		Collection<V> collection = valueCollection;
		if (collection == null)
		{
			collection = valueCollection = new AdditionalValueCollection();
		}
		return collection;
	}
	
	protected class AdditionalEntrySet extends AbstractSet<Map.Entry<K, V>>
	{
		private Set<java.util.Map.Entry<K, V>> decoratedSet;

		protected AdditionalEntrySet()
		{
			decoratedSet = decorated.entrySet();
		}

		@Override
		public Iterator<Map.Entry<K, V>> iterator()
		{
			Iterator<Map.Entry<K, V>> decoratedIterator = decoratedSet.iterator();
			Map.Entry<K, V> additionalEntry = new SimpleImmutableEntry<>(additionalKey, additionalValue);
			return new AdditionalIterator<>(decoratedIterator, additionalEntry);
		}

		@Override
		public int size()
		{
			return decoratedSet.size() + 1;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean contains(Object o)
		{
			if (!(o instanceof Map.Entry))
			{
				return false;
			}
			
			if (decoratedSet.contains(o))
			{
				return true;
			}
			
			Map.Entry<K, V> entry = (Map.Entry<K, V>) o;
			K key = entry.getKey();
			V value = entry.getValue();
			return additionalKey.equals(key) && additionalValue.equals(value);
		}
	}
	
	protected class AdditionalKeySet extends AbstractSet<K>
	{
		private Set<K> decoratedSet;

		protected AdditionalKeySet()
		{
			decoratedSet = decorated.keySet();
		}

		@Override
		public Iterator<K> iterator()
		{
			Iterator<K> decoratedIterator = decoratedSet.iterator();
			return new AdditionalIterator<>(decoratedIterator, additionalKey);
		}

		@Override
		public int size()
		{
			return decoratedSet.size() + 1;
		}

		@Override
		public boolean contains(Object o)
		{
			if (decoratedSet.contains(o))
			{
				return true;
			}
			
			return additionalKey.equals(o);
		}
	}
	
	protected class AdditionalValueCollection extends AbstractCollection<V>
	{
		private Collection<V> decoratedCollection;

		protected AdditionalValueCollection()
		{
			decoratedCollection = decorated.values();
		}

		@Override
		public Iterator<V> iterator()
		{
			Iterator<V> decoratedIterator = decoratedCollection.iterator();
			return new AdditionalIterator<>(decoratedIterator, additionalValue);
		}

		@Override
		public int size()
		{
			return decoratedCollection.size() + 1;
		}

		@Override
		public boolean contains(Object o)
		{
			if (decoratedCollection.contains(o))
			{
				return true;
			}
			
			return additionalValue.equals(o);
		}
	}
	
	protected static class AdditionalIterator<E> implements Iterator<E>
	{
		private Iterator<E> decoratedIterator;
		private E additional;
		private boolean additionalReturned;

		protected AdditionalIterator(Iterator<E> decoratedIterator, E additional)
		{
			this.decoratedIterator = decoratedIterator;
			this.additional = additional;
		}

		@Override
		public boolean hasNext()
		{
			if (decoratedIterator.hasNext())
			{
				return true;
			}
			
			return !additionalReturned;
		}

		@Override
		public E next()
		{
			if (decoratedIterator.hasNext())
			{
				return decoratedIterator.next();
			}
			
			if (additionalReturned)
			{
				throw new NoSuchElementException();
			}
			
			additionalReturned = true;
			return additional;
		}

		@Override
		public void remove()
		{
	        throw new UnsupportedOperationException("remove");
		}
		
	}
}

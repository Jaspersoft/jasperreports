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
package net.sf.jasperreports;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import net.sf.jasperreports.engine.util.AdditionalEntryMap;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class AdditionalEntryMapTest
{
	
	private static final String INEXISTING = "foo";

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void nullMapTest()
	{
		new AdditionalEntryMap<Object, Object>(null, "key", "val");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void nullKeyTest()
	{
		new AdditionalEntryMap<Object, Object>(new HashMap<>(), null, "val");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void nullValueTest()
	{
		new AdditionalEntryMap<Object, Object>(new HashMap<>(), "key", null);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void mapContainsTest()
	{
		HashMap<Object, Object> map = new HashMap<>();
		map.put("k1", "v1");
		map.put("k2", "v2");
		map.put("k3", "v3");
		new AdditionalEntryMap<Object, Object>(map, "k2", "v4");
	}
	
	@Test(dataProvider = "mapData")
	public void mapTest(Object[][] pairs, Object addKey, Object addValue)
	{
		Map<Object, Object> baseMap = createBaseMap(pairs, false);
		AdditionalEntryMap<Object, Object> addMap = new AdditionalEntryMap<>(
				baseMap, addKey, addValue);
		assert !addMap.isEmpty();
		assert addMap.size() == pairs.length + 1;
		
		HashMap<Object, Object> newMap = new HashMap<>(baseMap);
		newMap.put(addKey, addValue);
		assert addMap.equals(newMap);		
	}
	
	@Test(dataProvider = "mapData")
	public void linkedMapTest(Object[][] pairs, Object addKey, Object addValue)
	{
		Map<Object, Object> baseMap = createBaseMap(pairs, true);
		AdditionalEntryMap<Object, Object> addMap = new AdditionalEntryMap<>(
				baseMap, addKey, addValue);
		assert !addMap.isEmpty();
		assert addMap.size() == pairs.length + 1;
		
		HashMap<Object, Object> newMap = new HashMap<>(baseMap);
		newMap.put(addKey, addValue);
		assert addMap.equals(newMap);		
	}
	
	@Test(dataProvider = "mapData")
	public void containsKeyTest(Object[][] pairs, Object addKey, Object addValue)
	{
		AdditionalEntryMap<Object, Object> map = createAdditionalMap(pairs, addKey, addValue, false);
		for (Object[] pair : pairs)
		{
			assert map.containsKey(pair[0]);
		}
		assert map.containsKey(addKey);
		assert !map.containsKey(INEXISTING);
	}
	
	@Test(dataProvider = "mapData")
	public void containsValueTest(Object[][] pairs, Object addKey, Object addValue)
	{
		AdditionalEntryMap<Object, Object> map = createAdditionalMap(pairs, addKey, addValue, false);
		for (Object[] pair : pairs)
		{
			assert map.containsValue(pair[1]);
		}
		assert map.containsValue(addValue);
		assert !map.containsValue(INEXISTING);
	}
	
	@Test(dataProvider = "mapData")
	public void getValueTest(Object[][] pairs, Object addKey, Object addValue)
	{
		AdditionalEntryMap<Object, Object> map = createAdditionalMap(pairs, addKey, addValue, false);
		for (Object[] pair : pairs)
		{
			assert nullSafeEquals(pair[1], map.get(pair[0]));
		}
		assert addValue.equals(map.get(addKey));
		assert map.get(INEXISTING) == null;
	}
	
	@Test(dataProvider = "mapData")
	public void keySetTest(Object[][] pairs, Object addKey, Object addValue)
	{
		AdditionalEntryMap<Object, Object> map = createAdditionalMap(pairs, addKey, addValue, true);
		Set<Object> keys = map.keySet();
		assert !keys.isEmpty();
		assert keys.size() == pairs.length + 1;
		
		for (Object[] pair : pairs)
		{
			assert keys.contains(pair[0]);
		}
		assert keys.contains(addKey);
		assert !keys.contains(INEXISTING);
		
		Iterator<Object> keyIterator = keys.iterator();
		for (Object[] pair : pairs)
		{
			assert keyIterator.hasNext();
			assert nullSafeEquals(pair[0], keyIterator.next());
		}
		assert keyIterator.hasNext();
		assert addKey.equals(keyIterator.next());
		assert !keyIterator.hasNext();
		try
		{
			keyIterator.next();
			assert false;
		}
		catch (NoSuchElementException e)
		{
		}
	}
	
	@Test(dataProvider = "mapData")
	public void valuesTest(Object[][] pairs, Object addKey, Object addValue)
	{
		AdditionalEntryMap<Object, Object> map = createAdditionalMap(pairs, addKey, addValue, true);
		Collection<Object> values = map.values();
		assert !values.isEmpty();
		assert values.size() == pairs.length + 1;
		
		for (Object[] pair : pairs)
		{
			assert values.contains(pair[1]);
		}
		assert values.contains(addValue);
		assert !values.contains(INEXISTING);
		
		Iterator<Object> valuesIterator = values.iterator();
		for (Object[] pair : pairs)
		{
			assert valuesIterator.hasNext();
			assert nullSafeEquals(pair[1], valuesIterator.next());
		}
		assert valuesIterator.hasNext();
		assert addValue.equals(valuesIterator.next());
		assert !valuesIterator.hasNext();
		try
		{
			valuesIterator.next();
			assert false;
		}
		catch (NoSuchElementException e)
		{
		}
	}
	
	@Test(dataProvider = "mapData")
	public void entriesTest(Object[][] pairs, Object addKey, Object addValue)
	{
		AdditionalEntryMap<Object, Object> map = createAdditionalMap(pairs, addKey, addValue, false);
		Set<Entry<Object, Object>> entries = map.entrySet();
		assert !entries.isEmpty();
		assert entries.size() == pairs.length + 1;
		
		for (Object[] pair : pairs)
		{
			assert entries.contains(entry(pair[0], pair[1]));
		}
		assert entries.contains(entry(addKey, addValue));
		assert !entries.contains(INEXISTING);
		
		Map<Object, Object> baseMap = createBaseMap(pairs, false);
		Set<Entry<Object, Object>> baseEntries = baseMap.entrySet();
		
		Iterator<Entry<Object, Object>> entryIterator = entries.iterator();
		Entry<Object, Object> entry;
		for (int i = 0; i < pairs.length; i++)
		{
			assert entryIterator.hasNext();
			entry = entryIterator.next();
			assert baseEntries.remove(entry);
		}
		assert baseEntries.isEmpty();
		assert entryIterator.hasNext();
		entry = entryIterator.next();
		assert addKey.equals(entry.getKey());
		assert addValue.equals(entry.getValue());
		assert !entryIterator.hasNext();
		try
		{
			entryIterator.next();
			assert false;
		}
		catch (NoSuchElementException e)
		{
		}
	}
	
	@Test(dataProvider = "mapData")
	public void linkedEntriesTest(Object[][] pairs, Object addKey, Object addValue)
	{
		AdditionalEntryMap<Object, Object> map = createAdditionalMap(pairs, addKey, addValue, true);
		Set<Entry<Object, Object>> entries = map.entrySet();
		assert !entries.isEmpty();
		assert entries.size() == pairs.length + 1;
		
		for (Object[] pair : pairs)
		{
			assert entries.contains(entry(pair[0], pair[1]));
		}
		assert entries.contains(entry(addKey, addValue));
		assert !entries.contains(INEXISTING);
		
		Iterator<Entry<Object, Object>> entryIterator = entries.iterator();
		Entry<Object, Object> entry;
		for (Object[] pair : pairs)
		{
			assert entryIterator.hasNext();
			entry = entryIterator.next();
			assert nullSafeEquals(pair[0], entry.getKey());
			assert nullSafeEquals(pair[1], entry.getValue());
		}
		assert entryIterator.hasNext();
		entry = entryIterator.next();
		assert addKey.equals(entry.getKey());
		assert addValue.equals(entry.getValue());
		assert !entryIterator.hasNext();
	}
	
	private Map.Entry<Object, Object> entry(Object key, Object value)
	{
		return new AbstractMap.SimpleImmutableEntry<>(key, value);
	}

	private AdditionalEntryMap<Object, Object> createAdditionalMap(Object[][] pairs, Object addKey, Object addValue,
			boolean linked)
	{
		Map<Object, Object> map = createBaseMap(pairs, linked);
		AdditionalEntryMap<Object, Object> addMap = new AdditionalEntryMap<>(
				map, addKey, addValue);
		return addMap;
	}

	private Map<Object, Object> createBaseMap(Object[][] pairs, boolean linked)
	{
		Map<Object, Object> map = linked ? new LinkedHashMap<>() : new HashMap<>();
		for (Object[] pair : pairs)
		{
			assert pair.length == 2;
			map.put(pair[0], pair[1]);
		}
		assert map.size() == pairs.length;
		return map;
	}
	
	@DataProvider
	public Object[][] mapData()
	{
		return new Object[][]{
			{new Object[][]{}, "k", "v"},
			{new Object[][]{{"a", "b"}}, "k", "v"},
			{new Object[][]{{"a", 1}, {"c", 2}}, "k", 3},
			{new Object[][]{{1, "b"}, {2, "d"}, {3, "f"}}, 0, "v"},
			{new Object[][]{{"a", "b"}, {"c", "d"}, {null, "f"}}, "k", "v"},
			{new Object[][]{{"a", "b"}, {"c", null}, {"e", "f"}}, "k", "v"},
			{new Object[][]{{null, 1}, {2, null}, {4, 5}}, 6, 7},
			{new Object[][]{{null, null}, {"c", null}, {"e", "f"}}, "k", "v"},
		};
	}
	
	private boolean nullSafeEquals(Object o1, Object o2)
	{
		return o1 == null ? o2 == null : (o2 != null && o1.equals(o2));
	}
	
}

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
package net.sf.jasperreports;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import net.sf.jasperreports.engine.util.CharPredicateCache;
import net.sf.jasperreports.engine.util.CharPredicateCache.Result;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class CharPredicateCacheTest
{
	
	@Test(dataProvider = "notCacheableCodepoints", expectedExceptions = IllegalArgumentException.class)
	public void notCacheable(int code)
	{
		CharPredicateCache cache = new CharPredicateCache();
		assert cache.getCached(code) == Result.NOT_CACHEABLE;
		
		cache.set(code, true);
	}
	
	@DataProvider
	public Object[][] notCacheableCodepoints()
	{
		List<Object[]> codepoints = new ArrayList<>();
		codepoints.add(new Object[]{-1});
		codepoints.add(new Object[]{-2});
		codepoints.add(new Object[]{Integer.MIN_VALUE});
		codepoints.add(new Object[]{0x30000});
		codepoints.add(new Object[]{0x30001});
		codepoints.add(new Object[]{Integer.MAX_VALUE});
		return codepoints.toArray(new Object[codepoints.size()][]);
	}
	
	
	@Test(dataProvider = "codepoints")
	public void cache(int code, int distance)
	{
		CharPredicateCache cache = new CharPredicateCache();
		assert cache.getCached(code) == Result.NOT_FOUND;
		
		cache.set(code, true);
		assert cache.getCached(code) == Result.TRUE;
		
		cache.set(code, false);
		assert cache.getCached(code) == Result.FALSE;
		
		cache.set(code, true);
		assert cache.getCached(code) == Result.TRUE;
		
		if (code - distance >= 0)
		{
			assert cache.getCached(code - distance) == Result.NOT_FOUND;
			
			cache.set(code - distance, false);
			assert cache.getCached(code) == Result.TRUE;
			assert cache.getCached(code - distance) == Result.FALSE;
			
			cache.set(code - distance, true);
			assert cache.getCached(code) == Result.TRUE;
			assert cache.getCached(code - distance) == Result.TRUE;
		}
		
		if (code + distance <= 0x2FFFF)
		{
			assert cache.getCached(code + distance) == Result.NOT_FOUND;
			
			cache.set(code + distance, false);
			assert cache.getCached(code) == Result.TRUE;
			assert cache.getCached(code + distance) == Result.FALSE;
			
			cache.set(code + distance, true);
			assert cache.getCached(code) == Result.TRUE;
			assert cache.getCached(code + distance) == Result.TRUE;
		}
		
		cache.set(code, false);
		assert cache.getCached(code) == Result.FALSE;
		
		if (code - distance >= 0)
		{
			cache.set(code - distance, true);
			assert cache.getCached(code) == Result.FALSE;
			assert cache.getCached(code - distance) == Result.TRUE;
			
			cache.set(code - distance, false);
			assert cache.getCached(code) == Result.FALSE;
			assert cache.getCached(code - distance) == Result.FALSE;
		}
		
		if (code + distance <= 0x2FFFF)
		{
			cache.set(code + distance, true);
			assert cache.getCached(code) == Result.FALSE;
			assert cache.getCached(code + distance) == Result.TRUE;
			
			cache.set(code + distance, false);
			assert cache.getCached(code) == Result.FALSE;
			assert cache.getCached(code + distance) == Result.FALSE;
		}
	}
	
	
	@DataProvider
	public Object[][] codepoints()
	{
		List<Object[]> codepoints = new ArrayList<>();
		for (int i = 0; i < 0x2ffff; i += 0x400)
		{
			codepoints.add(new Object[]{i, 1});
			codepoints.add(new Object[]{i, 2});
			codepoints.add(new Object[]{i, 3});
			codepoints.add(new Object[]{i, 0xff});
			codepoints.add(new Object[]{i, 0x100});
			codepoints.add(new Object[]{i, 0x3ff});
			codepoints.add(new Object[]{i, 0x400});
			codepoints.add(new Object[]{i, 0x401});
			codepoints.add(new Object[]{i, 0x7ff});
			codepoints.add(new Object[]{i, 0x800});
			codepoints.add(new Object[]{i, 0x801});
			codepoints.add(new Object[]{i, 0xfff});
			codepoints.add(new Object[]{i, 0x1000});
			codepoints.add(new Object[]{i, 0x1001});
			codepoints.add(new Object[]{i, 0x1fff});
			codepoints.add(new Object[]{i, 0x2000});
			codepoints.add(new Object[]{i, 0x2001});
			codepoints.add(new Object[]{i, 0xffff});
			codepoints.add(new Object[]{i, 0x10000});
			codepoints.add(new Object[]{i, 0x10001});

			codepoints.add(new Object[]{i + 0x3ff, 1});
			codepoints.add(new Object[]{i + 0x3ff, 2});
			codepoints.add(new Object[]{i + 0x3ff, 3});
			codepoints.add(new Object[]{i + 0x3ff, 0xff});
			codepoints.add(new Object[]{i + 0x3ff, 0x100});
			codepoints.add(new Object[]{i + 0x3ff, 0x3ff});
			codepoints.add(new Object[]{i + 0x3ff, 0x400});
			codepoints.add(new Object[]{i + 0x3ff, 0x401});
			codepoints.add(new Object[]{i + 0x3ff, 0x7ff});
			codepoints.add(new Object[]{i + 0x3ff, 0x800});
			codepoints.add(new Object[]{i + 0x3ff, 0x801});
			codepoints.add(new Object[]{i + 0x3ff, 0xfff});
			codepoints.add(new Object[]{i + 0x3ff, 0x1000});
			codepoints.add(new Object[]{i + 0x3ff, 0x1001});
			codepoints.add(new Object[]{i + 0x3ff, 0x1fff});
			codepoints.add(new Object[]{i + 0x3ff, 0x2000});
			codepoints.add(new Object[]{i + 0x3ff, 0x2001});
			codepoints.add(new Object[]{i + 0x3ff, 0xffff});
			codepoints.add(new Object[]{i + 0x3ff, 0x10000});
			codepoints.add(new Object[]{i + 0x3ff, 0x10001});
		}
		return codepoints.toArray(new Object[codepoints.size()][]);
	}
}

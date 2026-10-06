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
package net.sf.jasperreports.util;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.util.ObjectUtils;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ObjectUtilsTest
{
	@Test(dataProvider = "propertiesEqualPairs")
	public void propertiesEqual(String[] props1Array, String[] props2Array)
	{
		JRPropertiesMap props1 = propsArrayToMap(props1Array);
		JRPropertiesMap props2 = propsArrayToMap(props2Array);
		assert ObjectUtils.equals(props1, props2);
		assert ObjectUtils.equals(props2, props1);
	}
	
	@Test(dataProvider = "propertiesNotEqualPairs")
	public void propertiesNotEqual(String[] props1Array, String[] props2Array)
	{
		JRPropertiesMap props1 = propsArrayToMap(props1Array);
		JRPropertiesMap props2 = propsArrayToMap(props2Array);
		assert !ObjectUtils.equals(props1, props2);
		assert !ObjectUtils.equals(props2, props1);
	}
	
	private static JRPropertiesMap propsArrayToMap(String[] propsArray)
	{
		if (propsArray == null)
		{
			return null;
		}
	
		assert propsArray.length % 2 == 0;
		JRPropertiesMap props = new JRPropertiesMap();
		for (int i = 0; i < propsArray.length; i++)
		{
			String name = propsArray[i];
			++i;
			String value = propsArray[i];
			props.setProperty(name, value);
		}
		return props;
	}

	@DataProvider
	public Object[][] propertiesEqualPairs()
	{
		return new Object[][] {
			{null, null},
			{null, new String[] {}},
			{new String[] {}, new String[] {}},
			{new String[] {"a", "b"}, new String[] {"a", "b"}},
			{new String[] {"a", "b", "c", "d"}, new String[] {"a", "b", "c", "d"}},
			{new String[] {"a", "b", "c", "d", "e", "f"}, new String[] {"a", "b", "c", "d", "e", "f"}},
		};
	}

	@DataProvider
	public Object[][] propertiesNotEqualPairs()
	{
		return new Object[][] {
			{null, new String[] {"a", "b"}},
			{null, new String[] {"a", "b", "c", "d"}},
			{new String[] {}, new String[] {"a", "b"}},
			{new String[] {}, new String[] {"a", "b", "c", "d"}},
			{new String[] {"a", "b"}, new String[] {"a", "c"}},
			{new String[] {"a", "b"}, new String[] {"c", "b"}},
			{new String[] {"a", "b"}, new String[] {"b", "a"}},
			{new String[] {"a", "b"}, new String[] {"a", "b", "c", "d"}},
			{new String[] {"a", "b"}, new String[] {"c", "d", "a", "b"}},
			{new String[] {"a", "b", "c", "d"}, new String[] {"c", "d", "a", "b"}},
			{new String[] {"a", "b", "c", "d"}, new String[] {"a", "e", "c", "d"}},
			{new String[] {"a", "b", "c", "d"}, new String[] {"a", "b", "c", "e"}},
			{new String[] {"a", "b", "c", "d"}, new String[] {"a", "b", "c", "d", "e", "f"}},
			{new String[] {"a", "b", "c", "d"}, new String[] {"e", "f", "a", "b", "c", "d"}},
			{new String[] {"a", "b", "c", "d", "e", "f"}, new String[] {"a", "b", "c", "d", "e", "g"}},
			{new String[] {"a", "b", "c", "d", "e", "f"}, new String[] {"a", "g", "c", "d", "e", "f"}},
			{new String[] {"a", "b", "c", "d", "e", "f"}, new String[] {"a", "b", "c", "g", "e", "f"}},
			{new String[] {"a", "b", "c", "d", "e", "f"}, new String[] {"g", "b", "c", "d", "e", "f"}},
			{new String[] {"a", "b", "c", "d", "e", "f"}, new String[] {"a", "b", "g", "d", "e", "g"}},
		};
	}

}

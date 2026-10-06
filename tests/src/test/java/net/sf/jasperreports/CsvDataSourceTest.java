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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRCsvDataSource;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class CsvDataSourceTest
{
	
	@Test(dataProvider = "detectedColumnNames")
	public void detectColumnNames(String[] csvColumns, String[] resultColumns) throws JRException
	{
		assert csvColumns.length == resultColumns.length;
		
		StringBuilder csvData = new StringBuilder();
		for (int i = 0; i < csvColumns.length; i++)
		{
			csvData.append(csvColumns[i]);
			csvData.append(i + 1 < csvColumns.length ? ',' : '\n');
		}
		StringReader input = new StringReader(csvData.toString());
		
		JRCsvDataSource dataSource = new JRCsvDataSource(input);
		dataSource.setUseFirstRowAsHeader(true);
		boolean next = dataSource.next();
		assert !next;
		
		Map<String, Integer> columns = dataSource.getColumnNames();
		assert columns != null;
		assert columns.size() == resultColumns.length;
		
		int index = 0;
		for (Entry<String, Integer> entry : columns.entrySet())
		{
			String column = entry.getKey();
			Integer colIdx = entry.getValue();
			assert colIdx == index;
			assert column.equals(resultColumns[index]);
			++index;
		}
	}
	
	private static final String COLUMN_0 = JRCsvDataSource.INDEXED_COLUMN_PREFIX + 0;
	private static final String COLUMN_1 = JRCsvDataSource.INDEXED_COLUMN_PREFIX + 1;
	private static final String COLUMN_2 = JRCsvDataSource.INDEXED_COLUMN_PREFIX + 2;
	private static final String COLUMN_3 = JRCsvDataSource.INDEXED_COLUMN_PREFIX + 3;
	
	@DataProvider
	public Object[][] detectedColumnNames()
	{
		List<Object[]> names = new ArrayList<>();
		names.add(new Object[]{new String[]{"a"}, new String[]{"a"}});
		names.add(new Object[]{new String[]{"a" ,"b"}, new String[]{"a", "b"}});
		names.add(new Object[]{new String[]{"a" ,"b" ,"c"}, new String[]{"a", "b", "c"}});
		names.add(new Object[]{new String[]{"c" ,"aa" ,"b"}, new String[]{"c", "aa", "b"}});
		names.add(new Object[]{new String[]{"a" ,"a"}, new String[]{"a", COLUMN_1}});
		names.add(new Object[]{new String[]{"a" ,"a" ,"b"}, new String[]{"a", COLUMN_1, "b"}});
		names.add(new Object[]{new String[]{"a" ,"b" ,"a"}, new String[]{"a", "b", COLUMN_2}});
		names.add(new Object[]{new String[]{"a" ,"b" ,"b"}, new String[]{"a", "b", COLUMN_2}});
		names.add(new Object[]{new String[]{"a" ,"a" ,"a"}, new String[]{"a", COLUMN_1, COLUMN_2}});
		names.add(new Object[]{new String[]{COLUMN_1, COLUMN_0}, new String[]{COLUMN_1, COLUMN_0}});
		names.add(new Object[]{new String[]{COLUMN_0, COLUMN_0}, new String[]{COLUMN_0, COLUMN_1}});
		names.add(new Object[]{new String[]{COLUMN_1, COLUMN_1}, new String[]{COLUMN_0, COLUMN_1}});
		names.add(new Object[]{new String[]{COLUMN_2, COLUMN_1, COLUMN_0}, new String[]{COLUMN_2, COLUMN_1, COLUMN_0}});
		names.add(new Object[]{new String[]{COLUMN_1, COLUMN_2, COLUMN_0}, new String[]{COLUMN_1, COLUMN_2, COLUMN_0}});
		names.add(new Object[]{new String[]{COLUMN_2, COLUMN_0, COLUMN_1}, new String[]{COLUMN_2, COLUMN_0, COLUMN_1}});
		names.add(new Object[]{new String[]{COLUMN_0, COLUMN_0, COLUMN_0}, new String[]{COLUMN_0, COLUMN_1, COLUMN_2}});
		names.add(new Object[]{new String[]{COLUMN_1, COLUMN_1, COLUMN_1}, new String[]{COLUMN_0, COLUMN_1, COLUMN_2}});
		names.add(new Object[]{new String[]{COLUMN_2, COLUMN_2, COLUMN_2}, new String[]{COLUMN_0, COLUMN_1, COLUMN_2}});
		names.add(new Object[]{new String[]{"a", COLUMN_1, COLUMN_1}, new String[]{"a", COLUMN_1, COLUMN_2}});
		names.add(new Object[]{new String[]{"a", COLUMN_2, COLUMN_2}, new String[]{"a", COLUMN_1, COLUMN_2}});
		names.add(new Object[]{new String[]{COLUMN_2, "a", COLUMN_2}, new String[]{COLUMN_0, "a", COLUMN_2}});
		names.add(new Object[]{new String[]{"a" ,"a" ,"a", "a"}, new String[]{"a", COLUMN_1, COLUMN_2, COLUMN_3}});
		names.add(new Object[]{new String[]{COLUMN_0, COLUMN_0, COLUMN_0, COLUMN_0}, new String[]{COLUMN_0, COLUMN_1, COLUMN_2, COLUMN_3}});
		names.add(new Object[]{new String[]{COLUMN_1, COLUMN_1, COLUMN_1, COLUMN_1}, new String[]{COLUMN_0, COLUMN_1, COLUMN_2, COLUMN_3}});
		names.add(new Object[]{new String[]{COLUMN_2, COLUMN_2, COLUMN_2, COLUMN_2}, new String[]{COLUMN_0, COLUMN_1, COLUMN_2, COLUMN_3}});
		names.add(new Object[]{new String[]{COLUMN_3, COLUMN_3, COLUMN_3, COLUMN_3}, new String[]{COLUMN_0, COLUMN_1, COLUMN_2, COLUMN_3}});
		return names.toArray(new Object[names.size()][]);
	}

}

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
package net.sf.jasperreports.engine.data;

import java.util.Map;

import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRMapArrayDataSource implements JRRewindableDataSource
{
	

	/**
	 *
	 */
	private Object[] records;
	private int index = -1;
	

	/**
	 *
	 */
	public JRMapArrayDataSource(Object[] array)
	{
		records = array;
	}
	

	@Override
	public boolean next()
	{
		index++;

		if (records != null)
		{
			return (index < records.length);
		}

		return false;
	}
	
	
	@Override
	public Object getFieldValue(JRField field)
	{
		Object value = null;
		
		Map<String,?> currentRecord = (Map<String,?>)records[index];

		if (currentRecord != null)
		{
			value = currentRecord.get(field.getName());
		}

		return value;
	}

	
	@Override
	public void moveFirst()
	{
		this.index = -1;
	}

	/**
	 * Returns the underlying map array used by this data source.
	 * 
	 * @return the underlying map array
	 */
	public Object[] getData()
	{
		return records;
	}

	/**
	 * Returns the total number of records/maps that this data source
	 * contains.
	 * 
	 * @return the total number of records of this data source
	 */
	public int getRecordCount()
	{
		return records == null ? 0 : records.length;
	}
	
	/**
	 * Clones this data source by creating a new instance that reuses the same
	 * underlying map array.
	 * 
	 * @return a clone of this data source
	 */
	public JRMapArrayDataSource cloneDataSource()
	{
		return new JRMapArrayDataSource(records);
	}


}

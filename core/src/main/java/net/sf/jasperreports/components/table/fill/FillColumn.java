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
package net.sf.jasperreports.components.table.fill;

import java.util.List;

import net.sf.jasperreports.components.table.BaseColumn;
import net.sf.jasperreports.components.table.Column;
import net.sf.jasperreports.components.table.ColumnGroup;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;

/**
 * 
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class FillColumn implements JRPropertiesHolder
{

	private BaseColumn tableColumn;
	private int width;
	private List<FillColumn> subcolumns;
	private final Integer defaultWeight;
	private int subColsTotalWeight; 
	private Integer colSpan;

	private JRPropertiesMap properties;
	
	public FillColumn(
		Column column, 
		Integer defaultWeight,
		JRPropertiesMap properties
		)
	{
		this(
			column,  
			column.getWidth(),
			null, 
			0,
			defaultWeight,
			properties
			);
	}
	
	public FillColumn(
		ColumnGroup columnGroup, 
		int width,
		List<FillColumn> subcolumns, 
		int subColsTotalWeight,
		JRPropertiesMap properties
		)
	{
		this(
			columnGroup,  
			width,
			subcolumns, 
			subColsTotalWeight,
			null,
			properties
			);
	}
	
	private FillColumn(
		BaseColumn tableColumn, 
		int width,
		List<FillColumn> subcolumns, 
		int subColsTotalWeight,
		Integer defaultWeight,
		JRPropertiesMap properties
		)
	{
		super();
		
		this.tableColumn = tableColumn;
		this.width = width;
		this.subcolumns = subcolumns;
		this.subColsTotalWeight = subColsTotalWeight;
		this.defaultWeight = defaultWeight;
		this.properties = properties;
	}

	public BaseColumn getTableColumn()
	{
		return tableColumn;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getWeight()
	{
		return tableColumn.getWeight() == null ? (defaultWeight == null ? subColsTotalWeight : defaultWeight) : tableColumn.getWeight();
	}

	public int getSubColsTotalWeight()
	{
		return subColsTotalWeight;
	}

	public int getColSpan()
	{
		if (colSpan == null)
		{
			colSpan = 1;
			if (subcolumns != null && subcolumns.size() > 0)
			{
				colSpan = 0;
				for (FillColumn subcolumn : subcolumns)
				{
					colSpan += subcolumn.getColSpan();
				}
			}
		}
		return colSpan;
	}

	public List<FillColumn> getSubcolumns()
	{
		return subcolumns;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			//quick exit
			return true;
		}
		
		if (!(obj instanceof FillColumn))
		{
			return false;
		}
		
		FillColumn col = (FillColumn) obj;
		
		return tableColumn == col.tableColumn // instance comparison
			&& width == col.width
			&& (subcolumns == null ? col.subcolumns == null : 
				col.subcolumns != null && subcolumns.equals(col.subcolumns));
	}

	@Override
	public int hashCode()
	{
		int hash = tableColumn.hashCode();
		hash = hash * 37 + width;
		hash = hash * 37 + (subcolumns == null ? 0 : subcolumns.hashCode());
		return hash;
	}
	
	@Override
	public boolean hasProperties()
	{
		return properties != null && properties.hasProperties();
	}

	@Override
	public JRPropertiesMap getPropertiesMap()
	{
		return properties;
	}

	@Override
	public JRPropertiesHolder getParentProperties()
	{
		return null;
	}

}

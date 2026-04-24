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
package net.sf.jasperreports.engine.design;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.base.JRBaseSortField;
import net.sf.jasperreports.engine.type.SortFieldTypeEnum;
import net.sf.jasperreports.engine.type.SortOrderEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRDesignSortField extends JRBaseSortField
{


	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_NAME = "name";
	public static final String PROPERTY_TYPE = "type";


	/**
	 *
	 */
	public JRDesignSortField()
	{
	}
	

	/**
	 *
	 */
	public JRDesignSortField(
		String name,
		SortFieldTypeEnum type,
		SortOrderEnum order
		)
	{
		this.name = name;
		this.type = type;
		this.order = order;
	}
	

	/**
	 *
	 */
	public void setName(String name)
	{
		Object old = this.name;
		this.name = name;
		getEventSupport().firePropertyChange(PROPERTY_NAME, old, this.name);
	}
	

	/**
	 *
	 */
	public void setType(SortFieldTypeEnum type)
	{
		Object old = this.type;
		this.type = type;
		getEventSupport().firePropertyChange(PROPERTY_TYPE, old, this.type);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof JRDesignSortField) {
			JRDesignSortField compareTo = (JRDesignSortField)obj;
			if (this.name != null) {
				return 
					this.name.equals(compareTo.getName()) 
					&& SortFieldTypeEnum.getValueOrDefault(this.type) ==
						SortFieldTypeEnum.getValueOrDefault(compareTo.getType());
			} else {
				return false;
			}
		}
		return false;
	}
	
}

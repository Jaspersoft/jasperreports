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

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;


/**
 * A data source implementation that wraps an array of JavaBean objects.
 * <p>
 * It is common to access application data through object persistence layers like EJB,
 * Hibernate, or JDO. Such applications may need to generate reports using data they
 * already have available as arrays or collections of in-memory JavaBean objects.
 * </p><p>
 * This JavaBean-compliant data source can be used when data comes in an array 
 * of JavaBean objects.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRBeanArrayDataSource extends JRAbstractBeanDataSource
{
	

	/**
	 *
	 */
	private Object[] data;
	private int index = -1;
	

	/**
	 *
	 */
	public JRBeanArrayDataSource(Object[] beanArray)
	{
		this(beanArray, true);
	}
	

	/**
	 *
	 */
	public JRBeanArrayDataSource(Object[] beanArray, boolean isUseFieldDescription)
	{
		super(isUseFieldDescription);
		
		this.data = beanArray;
	}
	

	@Override
	public boolean next()
	{
		this.index++;

		if (this.data != null)
		{
			return (this.index < this.data.length);
		}

		return false;
	}
	
	
	@Override
	public Object getFieldValue(JRField field) throws JRException
	{
		return getFieldValue(data[this.index], field);
	}

	
	@Override
	public void moveFirst()
	{
		this.index = -1;
	}

	/**
	 * Returns the underlying bean array used by this data source.
	 * 
	 * @return the underlying bean array
	 */
	public Object[] getData()
	{
		return data;
	}

	/**
	 * Returns the total number of records/beans that this data source
	 * contains.
	 * 
	 * @return the total number of records of this data source
	 */
	public int getRecordCount()
	{
		return data == null ? 0 : data.length;
	}
	
	/**
	 * Clones this data source by creating a new instance that reuses the same
	 * underlying bean array. 
	 * 
	 * @return a clone of this data source
	 */
	public JRBeanArrayDataSource cloneDataSource()
	{
		return new JRBeanArrayDataSource(data);
	}

}

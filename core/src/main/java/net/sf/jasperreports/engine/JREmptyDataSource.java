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
package net.sf.jasperreports.engine;


/**
 * A simple data source implementation that simulates a data source with a given number of virtual records inside.
 * It is called empty data source because even though it has one or more records inside, all the report fields
 * are null for all the virtual records of the data source.
 * <p>
 * The simplest implementation of the {@link net.sf.jasperreports.engine.JRDataSource}
 * interface, this class can be used in reports that do not display data from the supplied data
 * source, but rather from parameters, and when only the number of virtual rows in the data
 * source is important.
 * 
 * @see net.sf.jasperreports.engine.JRDataSource
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JREmptyDataSource implements JRRewindableDataSource
{


	/**
	 *
	 */
	private int count = 1;
	private int index;


	/**
	 *
	 */
	public JREmptyDataSource()
	{
	}


	/**
	 *
	 */
	public JREmptyDataSource(int count)
	{
		this.count = count;
	}


	@Override
	public boolean next()
	{
		return (index++ < count);
	}


	@Override
	public Object getFieldValue(JRField field)
	{
		return null;
	}


	@Override
	public void moveFirst()
	{
		this.index = 0;
	}


}

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
package net.sf.jasperreports.engine.analytics.data;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class StandardMeasureValue implements MeasureValue
{

	private Measure measure;
	private Object value;

	public StandardMeasureValue()
	{
	}
	
	public StandardMeasureValue(Measure measure, Object value)
	{
		this.measure = measure;
		this.value = value;
	}

	@Override
	public Measure getMeasure()
	{
		return measure;
	}

	public void setMeasure(Measure measure)
	{
		this.measure = measure;
	}

	@Override
	public boolean hasValue()
	{
		return true;
	}

	@Override
	public Object getValue()
	{
		return value;
	}

	public void setValue(Object value)
	{
		this.value = value;
	}
	
	@Override
	public String toString()
	{
		return value + " (" + measure + ")";
	}

}

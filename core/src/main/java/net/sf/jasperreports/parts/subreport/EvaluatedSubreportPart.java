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
package net.sf.jasperreports.parts.subreport;

import java.util.Map;

import net.sf.jasperreports.engine.fill.FillDatasetPosition;
import net.sf.jasperreports.engine.fill.JasperReportSource;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class EvaluatedSubreportPart
{
	
	private JasperReportSource jasperReportSource;
	
	private FillDatasetPosition datasetPosition;
	private boolean cacheIncluded;

	private Map<String, Object> parameterValues;
	
	public JasperReportSource getJasperReportSource()
	{
		return jasperReportSource;
	}
	
	public void setJasperReportSource(JasperReportSource jasperReportSource)
	{
		this.jasperReportSource = jasperReportSource;
	}

	public FillDatasetPosition getDatasetPosition()
	{
		return datasetPosition;
	}

	public void setDatasetPosition(FillDatasetPosition datasetPosition)
	{
		this.datasetPosition = datasetPosition;
	}

	public boolean isCacheIncluded()
	{
		return cacheIncluded;
	}

	public void setCacheIncluded(boolean cacheIncluded)
	{
		this.cacheIncluded = cacheIncluded;
	}

	public Map<String, Object> getParameterValues()
	{
		return parameterValues;
	}

	public void setParameterValues(Map<String, Object> parameterValues)
	{
		this.parameterValues = parameterValues;
	}

	public void setParameterValue(String name, Object value)
	{
		parameterValues.put(name, value);
	}
}

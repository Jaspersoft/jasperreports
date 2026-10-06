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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class SimpleReportContext implements ReportContext, Serializable
{
	
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private static final Random RND = new Random();
	
	private final String id;
	private final Map<String, Object> parameters;

	public SimpleReportContext()
	{
		this.id = System.currentTimeMillis() 
				+ "_" + System.identityHashCode(this)
				+ "_" + RND.nextInt();
		this.parameters = new HashMap<>();
	}
	
	@Override
	public String getId()
	{
		return id;
	}

	@Override
	public Object getParameterValue(String parameterName)
	{
		return parameters.get(parameterName);
	}

	@Override
	public void setParameterValue(String parameterName, Object value)
	{
		parameters.put(parameterName, value);
	}

	@Override
	public boolean containsParameter(String parameterName)
	{
		return parameters.containsKey(parameterName);
	}

	@Override
	public Map<String, Object> getParameterValues() {
		return parameters;
	}

	@Override
	public Object removeParameterValue(String parameterName)
	{
		return parameters.remove(parameterName);
	}

	@Override
	public void clearParameterValues()
	{
		parameters.clear();
	}

}

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
package net.sf.jasperreports.repo;

import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.ReportContext;



/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JasperDesignReportResourceCache
{
	/**
	 * 
	 */
	private static final String PARAMETER_JASPER_DESIGN_REPORT_RESOURCE_CACHE = "net.sf.jasperreports.parameter.jasperdesign.report.resource.cache";

	/**
	 * 
	 */
	private Map<String, JasperDesignReportResource> cachedResourcesMap = new HashMap<>();

	/**
	 * 
	 */
	public static JasperDesignReportResourceCache getInstance(ReportContext reportContext)
	{
		JasperDesignReportResourceCache cache = (JasperDesignReportResourceCache)reportContext.getParameterValue(PARAMETER_JASPER_DESIGN_REPORT_RESOURCE_CACHE);
		
		if (cache == null)
		{
			cache = new JasperDesignReportResourceCache();
			reportContext.setParameterValue(PARAMETER_JASPER_DESIGN_REPORT_RESOURCE_CACHE, cache);
		}
		
		return cache;
	}
	
	/**
	 * 
	 */
	public JasperDesignReportResource getResource(String uri)
	{
		return cachedResourcesMap.get(uri);
	}

	/**
	 * 
	 */
	public void setResource(String uri, JasperDesignReportResource resource)
	{
		cachedResourcesMap.put(uri, resource);
	}
}

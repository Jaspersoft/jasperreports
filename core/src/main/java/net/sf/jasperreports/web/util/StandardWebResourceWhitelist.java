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
package net.sf.jasperreports.web.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class StandardWebResourceWhitelist implements WebResourceWhitelist
{
	private static final Log log = LogFactory.getLog(StandardWebResourceWhitelist.class);

	public static final String WHITELIST_SEPARATOR = ",";
	
	private Set<String> resourceWhitelist;
	private List<Pattern> whitelistPatterns;
	
	public StandardWebResourceWhitelist()
	{
		this.resourceWhitelist = new HashSet<>();
		this.whitelistPatterns = new ArrayList<>();
	}
	
	@Override
	public boolean includesResource(String resource)
	{
		if (resourceWhitelist.contains(resource))
		{
			return true;
		}
		
		if (!whitelistPatterns.isEmpty())
		{
			for (Pattern pattern : whitelistPatterns)
			{
				if (pattern.matcher(resource).matches())
				{
					if (log.isDebugEnabled()) 
					{
						log.debug("Resource " + resource + " matched pattern " + pattern);
					}
					return true;
				}
			}
		}
		return false;
	}

	public void addResources(String resources)
	{
		StringTokenizer tkzer = new StringTokenizer(resources, WHITELIST_SEPARATOR);
		while (tkzer.hasMoreTokens())
		{
			resourceWhitelist.add(tkzer.nextToken());
		}
	}
	
	public void addPattern(String pattern)
	{
		whitelistPatterns.add(Pattern.compile(pattern));
	}
}

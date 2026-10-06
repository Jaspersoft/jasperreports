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
package net.sf.jasperreports.engine.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class StandardClassWhitelist implements ClassWhitelist
{
	
	public static final String WHITELIST_SEPARATOR = ",";
	
	private static final String WHITELIST_SEPARATOR_PATTERN = Pattern.quote(WHITELIST_SEPARATOR);

	private static final char WILDCARD = '*';
	
	private static final Pattern WILDCARD_PATTERN = Pattern.compile("\\*+");
	
	private Set<String> classWhitelist;
	private List<Pattern> whitelistPatterns;
	
	public StandardClassWhitelist()
	{
		this.classWhitelist = new HashSet<>();
		this.whitelistPatterns = new ArrayList<>();
	}
	
	@Override
	public boolean includesClass(String className)
	{
		if (classWhitelist.contains(className))
		{
			return true;
		}
		
		if (!whitelistPatterns.isEmpty())
		{
			for (Pattern pattern : whitelistPatterns)
			{
				if (pattern.matcher(className).matches())
				{
					return true;
				}
			}
		}
		return false;
	}

	public void addClass(String className)
	{
		classWhitelist.add(className);
	}
	
	public void addWhitelist(String whitelist)
	{
		String[] classes = whitelist.split(WHITELIST_SEPARATOR_PATTERN);
		for (String whitelistClass : classes)
		{
			whitelistClass = whitelistClass.trim();
			if (!whitelistClass.isEmpty())
			{
				if (whitelistClass.indexOf(WILDCARD) >= 0)
				{
					addPattern(whitelistClass);
				}
				else
				{
					classWhitelist.add(whitelistClass);
				}
			}
		}
	}

	protected void addPattern(String classWildcard)
	{
		Matcher matcher = WILDCARD_PATTERN.matcher(classWildcard);
		StringBuilder patternStr = new StringBuilder();
		int prevIndex = 0;
		while (matcher.find())
		{
			int matchStart = matcher.start();
			int matchEnd = matcher.end();
			if (matchStart > prevIndex)
			{
				patternStr.append(Pattern.quote(classWildcard.substring(prevIndex, matchStart)));
			}
			
			if (matchStart + 1 == matchEnd)
			{
				//single * - use class name pattern
				//class names allow almost any character, see Character.isJavaIdentifierPart
				//allowing anything except points to exclude packages
				patternStr.append("[^\\.]*");
			}
			else
			{
				//multiple * - use class and package pattern
				patternStr.append(".*");
			}
			
			prevIndex = matchEnd;
		}
		
		if (prevIndex < classWildcard.length())
		{
			patternStr.append(Pattern.quote(classWildcard.substring(prevIndex, classWildcard.length())));
		}
		
		Pattern pattern = Pattern.compile(patternStr.toString());
		whitelistPatterns.add(pattern);
	}
	
}

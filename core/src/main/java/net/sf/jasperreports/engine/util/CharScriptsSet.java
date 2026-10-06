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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ibm.icu.lang.UScript;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class CharScriptsSet
{
	
	private Set<Integer> includedScripts;
	private Set<Integer> excludedScripts;
	private boolean excludedCommon;
	private boolean excludedInherited;

	public CharScriptsSet(List<String> includedScriptNames, List<String> excludedScriptNames)
	{
		if (includedScriptNames != null)
		{
			includedScripts = new HashSet<>(includedScriptNames.size() * 4 / 3, .75f);
			for (String script : includedScriptNames)
			{
				int scriptCode = resolveScript(script);
				if (scriptCode != UScript.INVALID_CODE)
				{
					includedScripts.add(scriptCode);
				}
			}
			
			if (includedScripts.isEmpty())
			{
				includedScripts = null;
			}
		}
		
		if (excludedScriptNames != null)
		{
			excludedScripts = new HashSet<>(excludedScriptNames.size() * 4 / 3, .75f);
			for (String script : excludedScriptNames)
			{
				int scriptCode = resolveScript(script);
				if (scriptCode != UScript.INVALID_CODE)
				{
					excludedScripts.add(scriptCode);
				}
			}
			
			if (excludedScripts.isEmpty())
			{
				excludedScripts = null;
			}
			else
			{
				excludedCommon = excludedScripts.contains(UScript.COMMON);
				excludedInherited = excludedScripts.contains(UScript.INHERITED);
			}
		}
	}
	
	private int resolveScript(String script)
	{
		int scriptCode = UScript.getCodeFromName(script);
		return scriptCode;
	}
	
	public boolean includesCharacter(int codePoint)
	{
		if (includedScripts == null && excludedScripts == null)
		{
			return true;
		}
		
		int codeScript = UScript.getScript(codePoint);
		if (codeScript == UScript.UNKNOWN)
		{
			//include by default
			return true;
		}
		
		if (codeScript == UScript.COMMON)
		{
			//COMMON is included unless explicitly excluded
			return !excludedCommon;
		}
		
		if (codeScript == UScript.INHERITED)
		{
			//INHERITED is included unless explicitly excluded
			return !excludedInherited;
		}
		
		if (includedScripts != null && includedScripts.contains(codeScript))
		{
			//the codepoint script is explicitly included
			return true;
		}
		
		if (excludedScripts != null && excludedScripts.contains(codeScript))
		{
			//the codepoint script is explicitly excluded
			return false;
		}
		
		if (includedScripts == null)
		{
			//not excluded
			return true;
		}
		
		for (Integer script : includedScripts)
		{
			if (UScript.hasScript(codePoint, script))
			{
				//included as a secondary/extension script
				return true;
			}
		}
		
		//not included
		return false;
	}
	
}

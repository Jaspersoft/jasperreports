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
package net.sf.jasperreports.engine.fonts;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRRuntimeException;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class SimpleFontSetFamily implements FontSetFamily
{
	
	private String familyName;
	private boolean primary;
	private List<String> includedScripts;
	private List<String> excludedScripts;

	@Override
	public String getFamilyName()
	{
		return familyName;
	}

	public void setFamilyName(String familyName)
	{
		this.familyName = familyName;
	}

	@Override
	public boolean isPrimary()
	{
		return primary;
	}

	public void setPrimary(boolean primary)
	{
		this.primary = primary;
	}

	@Override
	public List<String> getIncludedScripts()
	{
		return includedScripts;
	}

	public void setIncludedScripts(List<String> includedScripts)
	{
		this.includedScripts = includedScripts;
	}
	
	public void addIncludedScript(String script)
	{
		if (includedScripts == null)
		{
			includedScripts = new ArrayList<>(4);
		}
		
		includedScripts.add(script);
	}

	@Override
	public List<String> getExcludedScripts()
	{
		return excludedScripts;
	}

	public void setExcludedScripts(List<String> excludedScripts)
	{
		this.excludedScripts = excludedScripts;
	}
	
	public void addExcludedScript(String script)
	{
		if (excludedScripts == null)
		{
			excludedScripts = new ArrayList<>(4);
		}
		
		excludedScripts.add(script);
	}

	@Override
	public Object clone()
	{
		try
		{
			SimpleFontSetFamily clone = (SimpleFontSetFamily) super.clone();
			if (includedScripts != null)
			{
				clone.includedScripts = new ArrayList<>(includedScripts);
			}
			if (excludedScripts != null)
			{
				clone.excludedScripts = new ArrayList<>(excludedScripts);
			}
			return clone;
		}
		catch (CloneNotSupportedException e)
		{
			throw new JRRuntimeException(e);
		}
	}

}

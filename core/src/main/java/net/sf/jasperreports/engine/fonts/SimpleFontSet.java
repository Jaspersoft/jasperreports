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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.util.JRCloneUtils;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class SimpleFontSet implements FontSet
{
	
	private String name;
	private List<FontSetFamily> families;
	private Map<String, String> exportFonts;

	@Override
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public List<FontSetFamily> getFamilies()
	{
		return families;
	}

	public void setFamilies(List<FontSetFamily> families)
	{
		this.families = families;
	}
	
	public void addFamily(FontSetFamily fontSetFamily)
	{
		if (families == null)
		{
			families = new ArrayList<>();
		}
		
		families.add(fontSetFamily);
	}
	
	public Map<String, String> getExportFonts()
	{
		return exportFonts;
	}

	public void setExportFonts(Map<String, String> exportFonts)
	{
		this.exportFonts = exportFonts;
	}

	@Override
	public String getExportFont(String key)
	{
		String exportFont = exportFonts == null ? null : (String) exportFonts.get(key);
		return exportFont;
	}

	@Override
	public Object clone()
	{
		try
		{
			SimpleFontSet clone = (SimpleFontSet) super.clone();
			clone.families = JRCloneUtils.cloneList(families);
			if (exportFonts != null)
			{
				clone.exportFonts = new HashMap<>(exportFonts);
			}
			return clone;
		}
		catch (CloneNotSupportedException e)
		{
			throw new JRRuntimeException(e);
		}
	}

}

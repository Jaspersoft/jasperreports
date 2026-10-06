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

import java.util.Locale;
import java.util.Objects;

import net.sf.jasperreports.engine.util.ObjectUtils;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class FontSetInfoKey
{

	private final String name;
	private final Locale locale;
	private final boolean ignoreMissingFonts;
	
	public FontSetInfoKey(String name, Locale locale, boolean ignoreMissingFonts)
	{
		this.name = name;
		this.locale = locale;
		this.ignoreMissingFonts = ignoreMissingFonts;
	}

	@Override
	public int hashCode()
	{
		int hash = 47;
		hash = 29 * hash + ObjectUtils.hashCode(name);
		hash = 29 * hash + ObjectUtils.hashCode(locale);
		hash = 29 * hash + Boolean.hashCode(ignoreMissingFonts);
		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FontSetInfoKey other = (FontSetInfoKey) obj;
		return Objects.equals(name, other.name) 
				&& Objects.equals(locale, other.locale)
				&& ignoreMissingFonts == other.ignoreMissingFonts;
	}

}

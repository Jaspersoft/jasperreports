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

import java.awt.Font;



/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class FontInfo
{

	/**
	 * 
	 */
	private FontFamily family;
	private FontFace face;
	private int style = Font.PLAIN;
	
	/**
	 * 
	 */
	public FontInfo(
		FontFamily family,
		FontFace face,
		int style
		)
	{
		this.family = family;
		this.face = face;
		this.style = style;
	}
	
	/**
	 * 
	 */
	public FontFamily getFontFamily()
	{
		return family;
	}
	
	/**
	 * 
	 */
	public void setFontFamily(FontFamily family)
	{
		this.family = family;
	}
	
	/**
	 * 
	 */
	public FontFace getFontFace()
	{
		return face;
	}
	
	/**
	 * 
	 */
	public void setFontFace(FontFace face)
	{
		this.face = face;
	}
	
	/**
	 * 
	 */
	public int getStyle()
	{
		return style;
	}
	
	/**
	 * 
	 */
	public void setStyle(int style)
	{
		this.style = style;
	}
	
	@Override
	public String toString()
	{
		return "family " + (family == null ? null : family.getName())
				+ ", face " + (face == null ? null : face.getName())
				+ ", style " + style;
	}
}

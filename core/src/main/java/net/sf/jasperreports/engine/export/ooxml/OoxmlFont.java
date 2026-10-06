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
package net.sf.jasperreports.engine.export.ooxml;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class OoxmlFont
{
	private String id;
	
	private String regular;
	private String bold;
	private String italic;
	private String boldItalic;
	
	/**
	 * 
	 */
	private OoxmlFont(String id)
	{
		this.id = id;
	}
	
	/**
	 * 
	 */
	public static OoxmlFont getInstance(String id)
	{
		return new OoxmlFont(id);
	}
	
	/**
	 * 
	 */
	public String getId()
	{
		return id;
	}
	
	public String getRegular()
	{
		return regular;
	}
	
	public void setRegular(String regular)
	{
		this.regular = regular;
	}
	
	public String getBold()
	{
		return bold;
	}
	
	public void setBold(String bold)
	{
		this.bold = bold;
	}
	
	public String getItalic()
	{
		return italic;
	}
	
	public void setItalic(String italic)
	{
		this.italic = italic;
	}

	public String getBoldItalic()
	{
		return boldItalic;
	}
	
	public void setBoldItalic(String boldItalic)
	{
		this.boldItalic = boldItalic;
	}
}

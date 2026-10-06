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

import net.sf.jasperreports.renderers.AreaHyperlinksRenderable;


/**
 * A component of an image map.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see AreaHyperlinksRenderable
 */
public class JRPrintImageAreaHyperlink implements Serializable
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private JRPrintImageArea area;
	private JRPrintHyperlink hyperlink;

	
	/**
	 * Creates a blank image area.
	 */
	public JRPrintImageAreaHyperlink()
	{
	}

	
	/**
	 * Creates an image area by specifying its attributes.
	 * 
	 * @param area the area
	 * @param hyperlink the hyperlink information
	 */
	public JRPrintImageAreaHyperlink(JRPrintImageArea area, JRPrintHyperlink hyperlink)
	{
		this.area = area;
		this.hyperlink = hyperlink;
	}

	
	/**
	 * Returns the area of the image map component.
	 * 
	 * @return the area of the image map component
	 */
	public JRPrintImageArea getArea()
	{
		return area;
	}
	
	
	/**
	 * Sets the area of the image map component.
	 * 
	 * @param area the area
	 */
	public void setArea(JRPrintImageArea area)
	{
		this.area = area;
	}
	
	
	/**
	 * Returns the hyperlink information of the image map component.
	 * 
	 * @return the hyperlink information of the image map component
	 */
	public JRPrintHyperlink getHyperlink()
	{
		return hyperlink;
	}
	
	
	/**
	 * Sets the hyperlink information of the image map component.
	 * 
	 * @param hyperlink the hyperlink information
	 */
	public void setHyperlink(JRPrintHyperlink hyperlink)
	{
		this.hyperlink = hyperlink;
	}
	
}

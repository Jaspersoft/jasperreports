/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2025 Cloud Software Group, Inc. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
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
package net.sf.jasperreports.engine.fill;

import java.awt.Color;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.JRPrintGraphicElement;
import net.sf.jasperreports.engine.type.FillEnum;


/**
 * Base implementation of {@link net.sf.jasperreports.engine.JRPrintGraphicElement} that uses
 * a {@link net.sf.jasperreports.engine.fill.JRTemplateGraphicElement} instance to
 * store common attributes. 
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRTemplatePrintGraphicElement extends JRTemplatePrintElement implements JRPrintGraphicElement
{


	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public JRTemplatePrintGraphicElement()
	{
		
	}
	
	/**
	 *
	 * @param originator
	 */
	protected JRTemplatePrintGraphicElement(JRTemplateGraphicElement graphicElement, PrintElementOriginator originator)
	{
		super(graphicElement, originator);
	}

	@Override
	public JRPen getLinePen()
	{
		return ((JRTemplateGraphicElement)template).getLinePen();
	}
		

	@Override
	public FillEnum getFill()
	{
		return ((JRTemplateGraphicElement)this.template).getFill();
	}

	@Override
	public FillEnum getOwnFill()
	{
		return ((JRTemplateGraphicElement)this.template).getOwnFill();
	}

	@Override
	public void setFill(FillEnum fill)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Color getDefaultLineColor() 
	{
		return ((JRTemplateGraphicElement)template).getDefaultLineColor();
	}

	
}

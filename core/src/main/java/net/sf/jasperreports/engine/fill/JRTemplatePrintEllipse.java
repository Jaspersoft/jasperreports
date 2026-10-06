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
package net.sf.jasperreports.engine.fill;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRPrintEllipse;
import net.sf.jasperreports.engine.PrintElementVisitor;


/**
 * Base implementation of {@link net.sf.jasperreports.engine.JRPrintEllipse} that uses
 * a {@link net.sf.jasperreports.engine.fill.JRTemplateEllipse} instance to
 * store common attributes. 
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRTemplatePrintEllipse extends JRTemplatePrintGraphicElement implements JRPrintEllipse
{

	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public JRTemplatePrintEllipse()
	{
	}
	
	/**
	 * Creates a print ellipse element.
	 * 
	 * @param ellipse the template ellipse that the element will use
	 * @param originator
	 */
	public JRTemplatePrintEllipse(JRTemplateEllipse ellipse, PrintElementOriginator originator)
	{
		super(ellipse, originator);
	}

	@Override
	public <T> void accept(PrintElementVisitor<T> visitor, T arg)
	{
		visitor.visit(this, arg);
	}

}

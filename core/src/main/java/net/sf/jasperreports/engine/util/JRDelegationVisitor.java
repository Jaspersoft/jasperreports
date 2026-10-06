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

import net.sf.jasperreports.crosstabs.JRCrosstab;
import net.sf.jasperreports.engine.JRBreak;
import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRElementGroup;
import net.sf.jasperreports.engine.JREllipse;
import net.sf.jasperreports.engine.JRFrame;
import net.sf.jasperreports.engine.JRGenericElement;
import net.sf.jasperreports.engine.JRImage;
import net.sf.jasperreports.engine.JRLine;
import net.sf.jasperreports.engine.JRRectangle;
import net.sf.jasperreports.engine.JRStaticText;
import net.sf.jasperreports.engine.JRSubreport;
import net.sf.jasperreports.engine.JRTextField;
import net.sf.jasperreports.engine.JRVisitor;


/**
 * Abstract delegation visitor.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public abstract class JRDelegationVisitor implements JRVisitor
{

	private final JRVisitor visitor;
	
	/**
	 * Creates a delegation visitor.
	 * 
	 * @param visitor the visitor to which calls would be delegated to
	 */
	public JRDelegationVisitor(JRVisitor visitor)
	{
		this.visitor = visitor;
	}
	
	/**
	 * Returns the visitor to which calls are delegated to.
	 * 
	 * @return the visitor to which calls are delegated to
	 */
	public JRVisitor getVisitor()
	{
		return visitor;
	}
	
	@Override
	public void visitBreak(JRBreak breakElement)
	{
		visitor.visitBreak(breakElement);
	}

	@Override
	public void visitCrosstab(JRCrosstab crosstab)
	{
		visitor.visitCrosstab(crosstab);
	}

	@Override
	public void visitElementGroup(JRElementGroup elementGroup)
	{
		visitor.visitElementGroup(elementGroup);
	}

	@Override
	public void visitEllipse(JREllipse ellipse)
	{
		visitor.visitEllipse(ellipse);
	}

	@Override
	public void visitFrame(JRFrame frame)
	{
		visitor.visitFrame(frame);
	}

	@Override
	public void visitImage(JRImage image)
	{
		visitor.visitImage(image);
	}

	@Override
	public void visitLine(JRLine line)
	{
		visitor.visitLine(line);
	}

	@Override
	public void visitRectangle(JRRectangle rectangle)
	{
		visitor.visitRectangle(rectangle);
	}

	@Override
	public void visitStaticText(JRStaticText staticText)
	{
		visitor.visitStaticText(staticText);
	}

	@Override
	public void visitSubreport(JRSubreport subreport)
	{
		visitor.visitSubreport(subreport);
	}

	@Override
	public void visitTextField(JRTextField textField)
	{
		visitor.visitTextField(textField);
	}

	@Override
	public void visitComponentElement(JRComponentElement componentElement)
	{
		visitor.visitComponentElement(componentElement);
	}

	@Override
	public void visitGenericElement(JRGenericElement element)
	{
		visitor.visitGenericElement(element);
	}

}

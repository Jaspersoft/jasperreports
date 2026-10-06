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
 * Abstract implementation of {@link JRVisitor} that has empty visit methods for
 * all visitable types.
 * 
 * This class can be used as base class by visitors that do not want to implement
 * all methods.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public abstract class JRVisitorSupport implements JRVisitor
{

	@Override
	public void visitBreak(JRBreak breakElement)
	{
		// NOOP
	}

	@Override
	public void visitCrosstab(JRCrosstab crosstab)
	{
		// NOOP
	}

	@Override
	public void visitElementGroup(JRElementGroup elementGroup)
	{
		// NOOP
	}

	@Override
	public void visitEllipse(JREllipse ellipse)
	{
		// NOOP
	}

	@Override
	public void visitFrame(JRFrame frame)
	{
		// NOOP
	}

	@Override
	public void visitImage(JRImage image)
	{
		// NOOP
	}

	@Override
	public void visitLine(JRLine line)
	{
		// NOOP
	}

	@Override
	public void visitRectangle(JRRectangle rectangle)
	{
		// NOOP
	}

	@Override
	public void visitStaticText(JRStaticText staticText)
	{
		// NOOP
	}

	@Override
	public void visitSubreport(JRSubreport subreport)
	{
		// NOOP
	}

	@Override
	public void visitTextField(JRTextField textField)
	{
		// NOOP
	}

	@Override
	public void visitComponentElement(JRComponentElement componentElement)
	{
		// NOOP
	}

	@Override
	public void visitGenericElement(JRGenericElement element)
	{
		// NOOP
	}

}

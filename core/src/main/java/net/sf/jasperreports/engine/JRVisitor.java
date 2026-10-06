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

import net.sf.jasperreports.crosstabs.JRCrosstab;



/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface JRVisitor
{

	/**
	 *
	 */
	public void visitBreak(JRBreak breakElement);

	/**
	 *
	 */
	public void visitCrosstab(JRCrosstab crosstab);

	/**
	 *
	 */
	public void visitElementGroup(JRElementGroup elementGroup);

	/**
	 *
	 */
	public void visitEllipse(JREllipse ellipse);

	/**
	 *
	 */
	public void visitFrame(JRFrame frame);

	/**
	 *
	 */
	public void visitImage(JRImage image);

	/**
	 *
	 */
	public void visitLine(JRLine line);

	/**
	 *
	 */
	public void visitRectangle(JRRectangle rectangle);

	/**
	 *
	 */
	public void visitStaticText(JRStaticText staticText);

	/**
	 *
	 */
	public void visitSubreport(JRSubreport subreport);

	/**
	 *
	 */
	public void visitTextField(JRTextField textField);

	/**
	 * Visits a component wrapper element.
	 * 
	 * @param componentElement the element to visit
	 */
	public void visitComponentElement(JRComponentElement componentElement);

	/**
	 * Visits a generic report element.
	 * 
	 * @param element the element to visit
	 */
	public void visitGenericElement(JRGenericElement element);
}

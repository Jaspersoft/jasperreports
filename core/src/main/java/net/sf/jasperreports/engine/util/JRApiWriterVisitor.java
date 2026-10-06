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


public class JRApiWriterVisitor implements JRVisitor
{
	
	private JRApiWriter apiWriter;
	private String name;
	
	/**
	 *
	 */
	public JRApiWriterVisitor(JRApiWriter apiWriter)
	{
		this.apiWriter = apiWriter;
	}
	
	/**
	 *
	 */
	public JRApiWriter getApiWriter()
	{
		return apiWriter;
	}

	@Override
	public void visitBreak(JRBreak breakElement)
	{
		apiWriter.writeBreak(breakElement, name);
	}

	@Override
	public void visitCrosstab(JRCrosstab crosstab)
	{
		apiWriter.writeCrosstab(crosstab, name);
	}

	@Override
	public void visitElementGroup(JRElementGroup elementGroup)
	{
		apiWriter.writeElementGroup(elementGroup, name);
	}

	@Override
	public void visitEllipse(JREllipse ellipse)
	{
		apiWriter.writeEllipse(ellipse, name);
	}

	@Override
	public void visitFrame(JRFrame frame)
	{
		apiWriter.writeFrame(frame, name);
	}

	@Override
	public void visitImage(JRImage image)
	{
		apiWriter.writeImage(image, name);
	}

	@Override
	public void visitLine(JRLine line)
	{
		apiWriter.writeLine(line, name);
	}

	@Override
	public void visitRectangle(JRRectangle rectangle)
	{
		apiWriter.writeRectangle(rectangle, name);
	}

	@Override
	public void visitStaticText(JRStaticText staticText)
	{
		apiWriter.writeStaticText(staticText, name);
	}

	@Override
	public void visitSubreport(JRSubreport subreport)
	{
		apiWriter.writeSubreport(subreport, name);
	}

	@Override
	public void visitTextField(JRTextField textField)
	{
		apiWriter.writeTextField(textField, name);
	}

	@Override
	public void visitComponentElement(JRComponentElement componentElement)
	{
		apiWriter.writeComponentElement(componentElement, name);
	}

	@Override
	public void visitGenericElement(JRGenericElement element)
	{
		apiWriter.writeGenericElement(element, name);
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

}

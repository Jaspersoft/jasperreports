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
package net.sf.jasperreports.virtualization;

import java.util.UUID;

import org.testng.annotations.Test;

import net.sf.jasperreports.engine.fill.DefaultPrintElementOriginator;
import net.sf.jasperreports.engine.fill.JRTemplateEllipse;
import net.sf.jasperreports.engine.fill.JRTemplateLine;
import net.sf.jasperreports.engine.fill.JRTemplatePrintEllipse;
import net.sf.jasperreports.engine.fill.JRTemplatePrintGraphicElement;
import net.sf.jasperreports.engine.fill.JRTemplatePrintLine;
import net.sf.jasperreports.engine.fill.JRTemplatePrintRectangle;
import net.sf.jasperreports.engine.fill.JRTemplateRectangle;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class GraphicElementsTest extends BaseElementsTests
{

	@Test
	public void line()
	{
		JRTemplateLine template = new JRTemplateLine(null, null);
		JRTemplatePrintLine line = new JRTemplatePrintLine(template, new DefaultPrintElementOriginator(12));
		setElement(line);
		compareSerialized(line);
	}

	@Test
	public void rectangle()
	{
		JRTemplateRectangle template = new JRTemplateRectangle(null, null);
		JRTemplatePrintRectangle rectangle= new JRTemplatePrintRectangle(template, new DefaultPrintElementOriginator(12));
		setElement(rectangle);
		compareSerialized(rectangle);
	}

	@Test
	public void ellipse()
	{
		JRTemplateEllipse template = new JRTemplateEllipse(null, null);
		JRTemplatePrintEllipse ellipse = new JRTemplatePrintEllipse(template, new DefaultPrintElementOriginator(12));
		setElement(ellipse);
		compareSerialized(ellipse);
	}

	protected void setElement(JRTemplatePrintGraphicElement element)
	{
		element.setUUID(UUID.randomUUID());
		element.setX(10);
		element.setY(20);
		element.setWidth(50);
		element.setHeight(30);
	}

}

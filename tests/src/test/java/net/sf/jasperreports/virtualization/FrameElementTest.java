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

import java.util.List;
import java.util.UUID;

import org.testng.annotations.Test;

import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.fill.DefaultPrintElementOriginator;
import net.sf.jasperreports.engine.fill.JRTemplateFrame;
import net.sf.jasperreports.engine.fill.JRTemplatePrintFrame;
import net.sf.jasperreports.engine.fill.JRTemplatePrintText;
import net.sf.jasperreports.engine.fill.JRTemplateText;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class FrameElementTest extends BaseElementsTests
{

	@Test
	public void frameTest()
	{
		JRTemplatePrintFrame frame = frame();
		JRTemplatePrintFrame read = compareSerialized(frame);
		List<JRPrintElement> readElement = read.getElements();
		assert readElement.size() == 2;
		assert readElement.get(0) instanceof JRTemplatePrintText;
		assert ((JRTemplatePrintText) readElement.get(0)).getOriginalText().equals("1");
		assert ((JRTemplatePrintText) readElement.get(1)).getOriginalText().equals("2");
	}
	
	protected JRTemplatePrintFrame frame()
	{
		JRTemplateFrame template = new JRTemplateFrame(null, null);
		JRTemplatePrintFrame frame = new JRTemplatePrintFrame(template, new DefaultPrintElementOriginator(10));
		frame.setUUID(UUID.randomUUID());
		frame.setX(10);
		frame.setY(20);
		frame.setWidth(50);
		frame.setHeight(30);
		
		frame.addElement(textElement("1"));
		frame.addElement(textElement("2"));
		
		return frame;
	}
	
	protected JRTemplatePrintText textElement(String text)
	{
		JRTemplateText template = new JRTemplateText(null, null);
		JRTemplatePrintText element = new JRTemplatePrintText(template, new DefaultPrintElementOriginator(10));
		element.setUUID(UUID.randomUUID());
		element.setX(10);
		element.setY(20);
		element.setWidth(50);
		element.setHeight(30);
		element.setText(text);
		element.setValue(text);
		element.setLineSpacingFactor(1.2f);
		element.setLeadingOffset(2f);
		element.setTextHeight(20f);
		return element;
	}

}

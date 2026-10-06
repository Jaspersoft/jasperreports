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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.testng.annotations.Test;

import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.base.VirtualElementsData;
import net.sf.jasperreports.engine.fill.DefaultPrintElementOriginator;
import net.sf.jasperreports.engine.fill.JREvaluationTime;
import net.sf.jasperreports.engine.fill.JRTemplateFrame;
import net.sf.jasperreports.engine.fill.JRTemplatePrintFrame;
import net.sf.jasperreports.engine.fill.JRTemplatePrintText;
import net.sf.jasperreports.engine.fill.JRTemplateText;
import net.sf.jasperreports.engine.fill.JRVirtualizationContext;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class VirtualElementsDataTest extends BaseElementsTests
{

	@Test
	public void basicData()
	{
		JRTemplatePrintText text1 = textElement("1");
		JRTemplatePrintText text2 = textElement("2");
		JRTemplatePrintText text3 = textElement("3");
		
		List<JRPrintElement> elements = Arrays.<JRPrintElement>asList(text1, text2, text3);
		
		JRVirtualizationContext virtualizationContext = createVirtualizationContext();
		for (JRPrintElement element : elements)
		{
			virtualizationContext.cacheTemplate(element);
		}
		
		VirtualElementsData data = new VirtualElementsData(elements);
		VirtualElementsData readData = passThroughSerialization(virtualizationContext, data);
		assert readData != data;
		
		List<JRPrintElement> readElements = readData.getElements();
		assert readElements.size() == 3;
		compareXml(text1, readElements.get(0));
		compareXml(text2, readElements.get(1));
		compareXml(text3, readElements.get(2));
	}

	@Test
	public void evaluations()
	{
		JRTemplatePrintText text1 = textElement("1");
		JRTemplatePrintText text2 = textElement("2");
		JRTemplatePrintText frameText3 = textElement("3");
		JRTemplatePrintText frameText4 = textElement("4");
		JRTemplatePrintText text5 = textElement("5");
		
		JRTemplatePrintFrame frame = frame();
		frame.addElement(frameText3);
		frame.addElement(frameText4);
		
		List<JRPrintElement> elements = Arrays.<JRPrintElement>asList(text1, text2, frame, text5);
		
		JRVirtualizationContext virtualizationContext = createVirtualizationContext();
		for (JRPrintElement element : elements)
		{
			virtualizationContext.cacheTemplate(element);
		}
		
		VirtualElementsData data = new VirtualElementsData(elements);
		
		Map<JRPrintElement, Integer> reportEvaluations = new HashMap<>();
		reportEvaluations.put(text5, 7);
		reportEvaluations.put(frameText4, 9);
		data.setElementEvaluations(3, JREvaluationTime.EVALUATION_TIME_REPORT, reportEvaluations);
		
		Map<JRPrintElement, Integer> groupEvaluations = new HashMap<>();
		groupEvaluations.put(text1, 7);
		groupEvaluations.put(text5, 8);
		groupEvaluations.put(frameText3, 10);
		data.setElementEvaluations(5, JREvaluationTime.getGroupEvaluationTime("g"), groupEvaluations);
		
		VirtualElementsData readData = passThroughSerialization(virtualizationContext, data);
		List<JRPrintElement> readElements = readData.getElements();
		assert readElements.size() == 4;
		JRTemplatePrintText readText1 = (JRTemplatePrintText) readElements.get(0);
		JRTemplatePrintText readText2 = (JRTemplatePrintText) readElements.get(1);
		JRTemplatePrintFrame readFrame = (JRTemplatePrintFrame) readElements.get(2);
		JRTemplatePrintText readFrameText3 = (JRTemplatePrintText) readFrame.getElements().get(0);
		JRTemplatePrintText readFrameText4 = (JRTemplatePrintText) readFrame.getElements().get(1);
		JRTemplatePrintText readText5 = (JRTemplatePrintText) readElements.get(3);
		compareXml(text1, readText1);
		compareXml(text2, readText2);
		compareXml(frame, readFrame);
		compareXml(frameText3, readFrameText3);
		compareXml(frameText4, readFrameText4);
		compareXml(text5, readText5);
		
		Map<JRPrintElement, Integer> readReportEvaluations = readData.getElementEvaluations(3, JREvaluationTime.EVALUATION_TIME_REPORT);
		assert readReportEvaluations.size() == 2;
		assert !readReportEvaluations.containsKey(text1);
		assert readReportEvaluations.containsKey(readText5);
		assert readReportEvaluations.get(readText5) == 7;
		assert readReportEvaluations.get(readFrameText4) == 9;
		
		Map<JRPrintElement, Integer> readGroupEvaluations = readData.getElementEvaluations(5, JREvaluationTime.getGroupEvaluationTime("g"));
		assert readGroupEvaluations.size() == 3;
		assert readGroupEvaluations.get(readText1) == 7;
		assert readGroupEvaluations.get(readText5) == 8;
		assert readGroupEvaluations.get(readFrameText3) == 10;
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
	protected JRTemplatePrintFrame frame()
	{
		JRTemplateFrame template = new JRTemplateFrame(null, null);
		JRTemplatePrintFrame frame = new JRTemplatePrintFrame(template, new DefaultPrintElementOriginator(10));
		frame.setUUID(UUID.randomUUID());
		frame.setX(10);
		frame.setY(20);
		frame.setWidth(50);
		frame.setHeight(30);
		
		return frame;
	}
	
}

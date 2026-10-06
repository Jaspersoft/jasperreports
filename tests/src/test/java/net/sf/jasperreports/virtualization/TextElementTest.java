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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.testng.annotations.Test;

import net.sf.jasperreports.engine.JRPrintHyperlinkParameter;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameters;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.base.JRBasePrintText;
import net.sf.jasperreports.engine.fill.DefaultPrintElementOriginator;
import net.sf.jasperreports.engine.fill.JREvaluationTime;
import net.sf.jasperreports.engine.fill.JRRecordedValues;
import net.sf.jasperreports.engine.fill.JRRecordedValuesPrintText;
import net.sf.jasperreports.engine.fill.JRTemplatePrintElement;
import net.sf.jasperreports.engine.fill.JRTemplatePrintText;
import net.sf.jasperreports.engine.fill.JRTemplateText;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class TextElementTest extends BaseElementsTests
{

	@Test
	public void basicText()
	{
		JRTemplatePrintText text = textElement();
		passThroughElementSerialization(text);
	}

	@Test
	public void noCachedTemplate()
	{
		JRTemplatePrintText text = textElement();
		JRTemplatePrintElement readText = passThroughSerialization(text);
		assert readText.getTemplate() != null;
		assert readText.getTemplate() != text.getTemplate();
		assert readText.getTemplate().isIdentical(text.getTemplate());
	}

	@Test
	public void noUUID()
	{
		JRTemplatePrintText text = textElement();
		text.setUUID(null);
		JRTemplatePrintText read = compareSerialized(text);
		assert read.getUUID() == null;
	}
	
	@Test
	public void properties()
	{
		JRTemplatePrintText text = textElement();
		text.getPropertiesMap().setProperty("x", null);
		text.getPropertiesMap().setProperty("y", "a");
		text.getPropertiesMap().setProperty("z", "b");
		
		JRTemplatePrintText read = compareSerialized(text);
		assert read.hasProperties();
		assert read.getPropertiesMap().getPropertyNames().length == 3;
	}
	
	@Test
	public void templateProperties()
	{
		JRTemplatePrintText text = textElement();
		JRPropertiesMap baseProperties = text.getTemplate().getPropertiesMap();
		baseProperties.setProperty("x", "a");
		
		text.getPropertiesMap().setBaseProperties(baseProperties);
		text.getPropertiesMap().setProperty("y", "b");
		
		JRTemplatePrintText read = compareSerialized(text);
		assert read.hasProperties();
		assert read.getPropertiesMap().getBaseProperties() != null;
		assert read.getPropertiesMap().getBaseProperties().getProperty("x").equals("a");
		assert read.getPropertiesMap().getPropertyNames().length == 2;
	}
	
	@Test
	public void customProperties()
	{
		JRTemplatePrintText text = textElement();
		JRPropertiesMap baseProperties = new JRPropertiesMap();
		baseProperties.setProperty("x", "a");
		text.getPropertiesMap().setBaseProperties(baseProperties);
		text.getPropertiesMap().setProperty("y", "b");
		
		JRTemplatePrintText read = compareSerialized(text);
		assert read.hasProperties();
		assert read.getPropertiesMap().getBaseProperties() != null;
		assert read.getPropertiesMap().getBaseProperties().getProperty("x").equals("a");
		assert read.getPropertiesMap().getPropertyNames().length == 2;
	}

	@Test
	public void baseSerialization()
	{
		JRTemplatePrintText text = textElement();
		JRTemplatePrintText read = compareSerialized(text);
		assert read.getOriginalText() == read.getValue();
	}

	@Test
	public void textValue()
	{
		JRTemplatePrintText text = textElement();
		text.setValue(50);
		JRTemplatePrintText read = compareSerialized(text);
		assert read.getValue() instanceof Integer;
		assert ((Integer) read.getValue()) == 50;
	}
	
	@Test
	public void breakOffsets()
	{
		JRTemplatePrintText text = textElement();
		text.setLineBreakOffsets(new short[]{40, 70, 400});
		JRTemplatePrintText read = compareSerialized(text);
		assert read.getLineBreakOffsets() != null;
		assert read.getLineBreakOffsets().length == 3;
	}
	
	@Test
	public void zeroBreakOffsets()
	{
		JRTemplatePrintText text = textElement();
		text.setLineBreakOffsets(JRPrintText.ZERO_LINE_BREAK_OFFSETS);
		JRTemplatePrintText read = compareSerialized(text);
		assert read.getLineBreakOffsets() == JRPrintText.ZERO_LINE_BREAK_OFFSETS;
	}
	
	@Test
	public void anchor()
	{
		JRTemplatePrintText text = textElement();
		text.setAnchorName("a");
		JRTemplatePrintText read = compareSerialized(text);
		assert read.getAnchorName().equals("a");
	}
	
	@Test
	public void bookmarkLevel()
	{
		JRTemplatePrintText text = textElement();
		text.setAnchorName("a");
		text.setBookmarkLevel(2);
		JRTemplatePrintText read = compareSerialized(text);
		assert read.getBookmarkLevel() == 2;
	}
	
	@Test
	public void truncateIndex()
	{
		JRTemplatePrintText text = textElement();
		text.setTextTruncateIndex(5);
		JRTemplatePrintText read = compareSerialized(text);
		assert read.getTextTruncateIndex() == 5;
	}
	
	@Test
	public void truncateSuffix()
	{
		JRTemplatePrintText text = textElement();
		text.setTextTruncateIndex(5);
		text.setTextTruncateSuffix("..");
		JRTemplatePrintText read = compareSerialized(text);
		assert read.getTextTruncateSuffix().equals("..");
	}
	
	@Test
	public void hyperlinkReference()
	{
		JRTemplatePrintText text = textElement();
		text.setHyperlinkReference("ref");
		JRTemplatePrintText read = compareSerialized(text);
		assert read.getHyperlinkReference().equals("ref");
	}
	
	@Test
	public void hyperlinkAnchor()
	{
		JRTemplatePrintText text = textElement();
		text.setHyperlinkAnchor("ref");
		JRTemplatePrintText read = compareSerialized(text);
		assert read.getHyperlinkAnchor().equals("ref");
	}
	
	@Test
	public void hyperlinkPage()
	{
		JRTemplatePrintText text = textElement();
		text.setHyperlinkPage(3);
		JRTemplatePrintText read = compareSerialized(text);
		assert read.getHyperlinkPage() == 3;
	}
	
	@Test
	public void hyperlinkTooltip()
	{
		JRTemplatePrintText text = textElement();
		text.setHyperlinkTooltip("ref");
		JRTemplatePrintText read = compareSerialized(text);
		assert read.getHyperlinkTooltip().equals("ref");
	}
	
	@Test
	public void hyperlinkParameters()
	{
		JRTemplatePrintText text = textElement();
		
		JRPrintHyperlinkParameters params = new JRPrintHyperlinkParameters();
		JRPrintHyperlinkParameter param1 = new JRPrintHyperlinkParameter();
		param1.setName("a");
		param1.setValue("x");
		params.addParameter(param1);
		JRPrintHyperlinkParameter param2 = new JRPrintHyperlinkParameter();
		param2.setName("a");
		param2.setValueClass(Integer.class.getName());
		param2.setValue(5);
		params.addParameter(param2);
		text.setHyperlinkParameters(params);
		
		JRTemplatePrintText read = compareSerialized(text);
		assert read.getHyperlinkParameters() != null;
		assert read.getHyperlinkParameters().getParameters().size() == 2;
		assert read.getHyperlinkParameters().getParameters().get(0).getValue().equals("x");
	}
	
	@Test 
	public void recordedValues()
	{
		JRTemplateText template = new JRTemplateText(null, null);
		JRRecordedValuesPrintText text = new JRRecordedValuesPrintText(template, new DefaultPrintElementOriginator(10));
		setTextElement(text);
		
		Set<JREvaluationTime> evaluationTimes = new HashSet<>();
		evaluationTimes.add(JREvaluationTime.EVALUATION_TIME_REPORT);
		evaluationTimes.add(JREvaluationTime.getGroupEvaluationTime("g"));
		text.initRecordedValues(evaluationTimes);
		
		JRRecordedValues values = text.getRecordedValues();
		values.recordFieldValue("f1", "x");
		values.recordFieldValue("f2", 5);
		values.recordVariableValue("v1", 7.5d);
		
		JRRecordedValuesPrintText read = passThroughElementSerialization(text);
		JRRecordedValues readValues = read.getRecordedValues();
		assert readValues != null;
		assert readValues != values;
		
		Set<JREvaluationTime> readEvaluationTimes = readValues.getEvaluationTimes();
		assert readEvaluationTimes.size() == 2;
		assert readEvaluationTimes.contains(JREvaluationTime.EVALUATION_TIME_REPORT);
		assert readEvaluationTimes.contains(JREvaluationTime.getGroupEvaluationTime("g"));
		
		Map<String, Object> readFieldValues = readValues.getRecordedFieldValues();
		assert readFieldValues.size() == 2;
		assert readFieldValues.get("f1").equals("x");
		assert readFieldValues.get("f2").equals(5);
		
		Map<String, Object> readVarValues = readValues.getRecordedVariableValues();
		assert readVarValues.size() == 1;
		assert readVarValues.get("v1").equals(7.5d);
	}

	@Test 
	public void basePrintText()
	{
		JRBasePrintText text = new JRBasePrintText(null);
		text.setUUID(UUID.randomUUID());
		text.setX(10);
		text.setY(20);
		text.setWidth(50);
		text.setHeight(30);
		text.setText("foo bar");
		text.setValue("foo bar");
		text.setLineSpacingFactor(1.2f);
		text.setLeadingOffset(2f);
		text.setTextHeight(20f);
		
		JRBasePrintText read = passThroughSerialization(text);
		compareXml(read, text);
	}
	
	protected JRTemplatePrintText textElement()
	{
		JRTemplateText template = new JRTemplateText(null, null);
		JRTemplatePrintText text = new JRTemplatePrintText(template, new DefaultPrintElementOriginator(10));
		setTextElement(text);
		return text;
	}

	protected void setTextElement(JRTemplatePrintText text)
	{
		text.setUUID(UUID.randomUUID());
		text.setX(10);
		text.setY(20);
		text.setWidth(50);
		text.setHeight(30);
		text.setText("foo bar");
		text.setValue("foo bar");
		text.setLineSpacingFactor(1.2f);
		text.setLeadingOffset(2f);
		text.setTextHeight(20f);
	}
	
}

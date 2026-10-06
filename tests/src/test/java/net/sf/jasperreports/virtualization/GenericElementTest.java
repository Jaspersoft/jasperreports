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

import net.sf.jasperreports.engine.JRGenericElementType;
import net.sf.jasperreports.engine.fill.DefaultPrintElementOriginator;
import net.sf.jasperreports.engine.fill.JREvaluationTime;
import net.sf.jasperreports.engine.fill.JRRecordedValues;
import net.sf.jasperreports.engine.fill.JRRecordedValuesGenericPrintElement;
import net.sf.jasperreports.engine.fill.JRTemplateGenericElement;
import net.sf.jasperreports.engine.fill.JRTemplateGenericPrintElement;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

import org.testng.annotations.Test;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class GenericElementTest extends BaseElementsTests
{

	@Test
	public void basicElement()
	{
		JRTemplateGenericPrintElement element = genericElement();
		compareSerialized(element);
	}
	
	@Test 
	public void recordedValues()
	{
		JRTemplateGenericElement template = genericTemplate();
		JRRecordedValuesGenericPrintElement element = new JRRecordedValuesGenericPrintElement(template, new DefaultPrintElementOriginator(12));
		setElement(element);
		
		Set<JREvaluationTime> evaluationTimes = new HashSet<>();
		evaluationTimes.add(JREvaluationTime.EVALUATION_TIME_REPORT);
		evaluationTimes.add(JREvaluationTime.getGroupEvaluationTime("g"));
		element.initRecordedValues(evaluationTimes);
		
		JRRecordedValues values = element.getRecordedValues();
		values.recordFieldValue("f1", "x");
		values.recordFieldValue("f2", 5);
		values.recordVariableValue("v1", 7.5d);
		
		JRRecordedValuesGenericPrintElement read = passThroughElementSerialization(element);
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

	protected JRTemplateGenericPrintElement genericElement()
	{
		JRTemplateGenericElement template = genericTemplate();
		JRTemplateGenericPrintElement element = new JRTemplateGenericPrintElement(template, new DefaultPrintElementOriginator(12));
		setElement(element);
		return element;
	}

	protected JRTemplateGenericElement genericTemplate()
	{
		JRGenericElementType type = new JRGenericElementType(JRXmlConstants.JASPERREPORTS_NAMESPACE, "test");
		JRTemplateGenericElement template = new JRTemplateGenericElement(null, null, type);
		return template;
	}

	protected void setElement(JRTemplateGenericPrintElement element)
	{
		element.setUUID(UUID.randomUUID());
		element.setX(10);
		element.setY(20);
		element.setWidth(50);
		element.setHeight(30);
		
		element.setParameterValue("a", 13);
		element.setParameterValue("b", "y");
	}

}

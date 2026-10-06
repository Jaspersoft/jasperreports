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

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.fill.JREvaluationTime;
import net.sf.jasperreports.engine.fill.JRRecordedValues;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRValuesSerializationTest extends BaseSerializationTests
{
	@Test(dataProvider = "simpleEvaluationTimes")
	public void simpleEvaluationTime(JREvaluationTime evaluationTime)
	{
		JREvaluationTime read = passThroughSerialization(evaluationTime);
		assert read == evaluationTime;
	}
	
	@DataProvider
	public Object[][] simpleEvaluationTimes()
	{
		return new Object[][]{
				{JREvaluationTime.EVALUATION_TIME_NOW},	
				{JREvaluationTime.EVALUATION_TIME_REPORT},	
				{JREvaluationTime.EVALUATION_TIME_PAGE},	
				{JREvaluationTime.EVALUATION_TIME_COLUMN},	
		};
	}
	
	@Test
	public void bandEvaluationTime()
	{
		JREvaluationTime evalTime = JREvaluationTime.getBandEvaluationTime(7);
		JREvaluationTime read = passThroughSerialization(evalTime);
		assert read != evalTime;
		assert read.equals(evalTime);
	}
	
	@Test
	public void groupEvaluationTime()
	{
		JREvaluationTime evalTime = JREvaluationTime.getGroupEvaluationTime("g");
		JREvaluationTime read = passThroughSerialization(evalTime);
		assert read != evalTime;
		assert read.equals(evalTime);
	}
	
	@Test
	public void recordedValues()
	{
		Set<JREvaluationTime> evaluationTimes = new HashSet<>();
		evaluationTimes.add(JREvaluationTime.EVALUATION_TIME_REPORT);
		evaluationTimes.add(JREvaluationTime.getGroupEvaluationTime("g"));
		
		JRRecordedValues values = new JRRecordedValues(evaluationTimes);
		values.recordFieldValue("f1", "x");
		values.recordFieldValue("f2", 5);
		values.recordVariableValue("v1", 7.5d);
		
		JRRecordedValues readValues = passThroughSerialization(values);
		assert readValues != values;
		
		Set<JREvaluationTime> readEvalTimes = readValues.getEvaluationTimes();
		assert readEvalTimes.size() == 2;
		assert readEvalTimes.contains(JREvaluationTime.EVALUATION_TIME_REPORT);
		assert readEvalTimes.contains(JREvaluationTime.getGroupEvaluationTime("g"));
		
		Map<String, Object> readFieldValues = readValues.getRecordedFieldValues();
		assert readFieldValues.size() == 2;
		assert readFieldValues.get("f1").equals("x");
		assert readFieldValues.get("f2").equals(5);
		
		Map<String, Object> readVarValues = readValues.getRecordedVariableValues();
		assert readVarValues.size() == 1;
		assert readVarValues.get("v1").equals(7.5d);
	}
	
	@Test
	public void recordedValuesNoFields()
	{
		Set<JREvaluationTime> evaluationTimes = new HashSet<>();
		evaluationTimes.add(JREvaluationTime.EVALUATION_TIME_REPORT);
		
		JRRecordedValues values = new JRRecordedValues(evaluationTimes);
		values.recordVariableValue("v1", 7.5d);
		values.recordVariableValue("v2", "y");
		
		JRRecordedValues readValues = passThroughSerialization(values);
		assert readValues != values;
		
		Set<JREvaluationTime> readEvalTimes = readValues.getEvaluationTimes();
		assert readEvalTimes.size() == 1;
		assert readEvalTimes.contains(JREvaluationTime.EVALUATION_TIME_REPORT);
		
		Map<String, Object> readFieldValues = readValues.getRecordedFieldValues();
		assert readFieldValues == null;
		
		Map<String, Object> readVarValues = readValues.getRecordedVariableValues();
		assert readVarValues.size() == 2;
		assert readVarValues.get("v1").equals(7.5d);
		assert readVarValues.get("v2").equals("y");
	}
	
	@Test
	public void recordedValuesNoVariables()
	{
		Set<JREvaluationTime> evaluationTimes = new HashSet<>();
		evaluationTimes.add(JREvaluationTime.getBandEvaluationTime(-99));
		
		JRRecordedValues values = new JRRecordedValues(evaluationTimes);
		Date now = new Date();
		values.recordFieldValue("f1", now);
		
		JRRecordedValues readValues = passThroughSerialization(values);
		assert readValues != values;
		
		Set<JREvaluationTime> readEvalTimes = readValues.getEvaluationTimes();
		assert readEvalTimes.size() == 1;
		assert readEvalTimes.contains(JREvaluationTime.getBandEvaluationTime(-99));
		
		Map<String, Object> readFieldValues = readValues.getRecordedFieldValues();
		assert readFieldValues.size() == 1;
		assert readFieldValues.get("f1").equals(now);
		
		Map<String, Object> readVarValues = readValues.getRecordedVariableValues();
		assert readVarValues == null;
	}
}

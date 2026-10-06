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
package net.sf.jasperreports.engine.virtualization;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.jasperreports.engine.fill.JREvaluationTime;
import net.sf.jasperreports.engine.fill.JRRecordedValues;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class RecordedValuesSerializer implements ObjectSerializer<JRRecordedValues>
{

	@Override
	public int typeValue()
	{
		return SerializationConstants.OBJECT_TYPE_RECORDED_VALUES;
	}

	@Override
	public ReferenceType defaultReferenceType()
	{
		return ReferenceType.IDENTITY;
	}

	@Override
	public boolean defaultStoreReference()
	{
		// objects are not shared
		return false;
	}

	@Override
	public void write(JRRecordedValues values, VirtualizationOutput out) throws IOException
	{
		Set<JREvaluationTime> evaluationTimes = values.getEvaluationTimes();
		out.writeIntCompressed(evaluationTimes.size());
		for (JREvaluationTime evaluationTime : evaluationTimes)
		{
			out.writeJRObject(evaluationTime);
		}
		
		//FIXME field  and variable name sets repeat, keep in memory
		Map<String, Object> recordedFieldValues = values.getRecordedFieldValues();
		writeMap(out, recordedFieldValues);
		
		Map<String, Object> recordedVariableValues = values.getRecordedVariableValues();
		writeMap(out, recordedVariableValues);
	}

	protected void writeMap(VirtualizationOutput out, Map<String, Object> values) throws IOException
	{
		//FIXME use a HashMap serializer?
		if (values == null)
		{
			out.writeIntCompressed(0);
		}
		else
		{
			out.writeIntCompressed(values.size() + 1);
			for (Entry<String, Object> varEntry : values.entrySet())
			{
				out.writeJRObject(varEntry.getKey());
				out.writeJRObject(varEntry.getValue());
			}
		}
	}

	@Override
	public JRRecordedValues read(VirtualizationInput in) throws IOException
	{
		int evalTimesCount = in.readIntCompressed();
		HashSet<JREvaluationTime> evaluationTimes = new HashSet<>(evalTimesCount * 4 / 3 + 1, .75f);
		for (int i = 0; i < evalTimesCount; i++)
		{
			evaluationTimes.add((JREvaluationTime) in.readJRObject());
		}
		
		Map<String, Object> recordedFieldValues = readMap(in);
		Map<String, Object> recordedVariableValues = readMap(in);

		JRRecordedValues values = new JRRecordedValues(evaluationTimes, recordedFieldValues, recordedVariableValues);
		return values;
	}

	protected Map<String, Object> readMap(VirtualizationInput in) throws IOException
	{
		int size = in.readIntCompressed();
		if (size == 0)
		{
			return null;
		}
		
		Map<String, Object> map = new HashMap<>(size * 4 / 3 + 1, .75f);
		for (int i = 0; i < size - 1; i++)
		{
			String key = (String) in.readJRObject();
			Object value = in.readJRObject();
			map.put(key, value);
		}
		return map;
	}
}

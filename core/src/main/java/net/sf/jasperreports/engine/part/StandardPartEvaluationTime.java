/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2025 Cloud Software Group, Inc. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
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
package net.sf.jasperreports.engine.part;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.type.PartEvaluationTimeType;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class StandardPartEvaluationTime implements PartEvaluationTime, Serializable
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;//FIXMEBOOK readObject to preserve constants
	
	private static final String EXCEPTION_MESSAGE_KEY_INVALID_EVALUATION_TYPE = "engine.part.invalid.evaluation.type";
	private static final String EXCEPTION_MESSAGE_KEY_INVALID_SIMPLE_EVALUATION_TYPE = "engine.part.invalid.simple.evaluation.type";

	public final static StandardPartEvaluationTime EVALUATION_NOW = new StandardPartEvaluationTime(PartEvaluationTimeType.NOW, null);
	
	public final static StandardPartEvaluationTime EVALUATION_REPORT = new StandardPartEvaluationTime(PartEvaluationTimeType.REPORT, null);
	
	public static StandardPartEvaluationTime forType(String typeName)
	{
		PartEvaluationTimeType type = PartEvaluationTimeType.byName(typeName);
		if (type == null)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_INVALID_EVALUATION_TYPE,
					new Object[]{typeName});
		}
		
		StandardPartEvaluationTime evaluationTime;
		switch (type)
		{
		case NOW:
			evaluationTime = EVALUATION_NOW;
			break;
		case REPORT:
			evaluationTime = EVALUATION_REPORT;
			break;
		case GROUP:
		default:
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_INVALID_SIMPLE_EVALUATION_TYPE,
					new Object[]{typeName});
		}
		return evaluationTime;
	}
	
	public static StandardPartEvaluationTime forGroup(String groupName)
	{
		return new StandardPartEvaluationTime(PartEvaluationTimeType.GROUP, groupName);
	}
	
	private final PartEvaluationTimeType type;
	private final String groupName;
	
	@JsonCreator
	protected StandardPartEvaluationTime(
		@JsonProperty(JRXmlConstants.ATTRIBUTE_evaluationTime) PartEvaluationTimeType type, 
		@JsonProperty(JRXmlConstants.ATTRIBUTE_evaluationGroup) String groupName
		)
	{
		this.type = type;
		this.groupName = groupName;
	}

	@Override
	public PartEvaluationTimeType getEvaluationTimeType()
	{
		return type;
	}

	@Override
	public String getEvaluationGroup()
	{
		return groupName;
	}

}

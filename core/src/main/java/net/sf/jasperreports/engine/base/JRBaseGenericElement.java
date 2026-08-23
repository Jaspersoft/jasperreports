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
package net.sf.jasperreports.engine.base;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRGenericElement;
import net.sf.jasperreports.engine.JRGenericElementParameter;
import net.sf.jasperreports.engine.JRGenericElementType;
import net.sf.jasperreports.engine.JRVisitor;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;


/**
 * A read-only implementation of {@link JRGenericElement}
 * that is included in compiled reports.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRBaseGenericElement extends JRBaseElement implements JRGenericElement
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private JRGenericElementType genericType;
	private List<JRGenericElementParameter> parameters;
	private EvaluationTimeEnum evaluationTime;
	private String evaluationGroup;
	
	/**
	 * Creates a generic element by copying an existing element.
	 * 
	 * @param element the element to copy
	 * @param factory the object factory to be used for creating sub objects
	 */
	public JRBaseGenericElement(JRGenericElement element,
			JRBaseObjectFactory factory)
	{
		super(element, factory);

		this.genericType = element.getGenericType();
		this.evaluationTime = element.getEvaluationTime();
		this.evaluationGroup = element.getEvaluationGroup();
		
		JRGenericElementParameter[] elementParameters = element.getParameters();
		this.parameters = new ArrayList<>(elementParameters.length);
		for (JRGenericElementParameter elementParameter : elementParameters)
		{
			JRGenericElementParameter parameter = factory.getGenericElementParameter(
					elementParameter);
			this.parameters.add(parameter);
		}
	}

	@Override
	public JRGenericElementType getGenericType()
	{
		return genericType;
	}

	@Override
	public JRGenericElementParameter[] getParameters()
	{
		return parameters.toArray(new JRGenericElementParameter[parameters.size()]);
	}

	@Override
	public void collectExpressions(JRExpressionCollector collector)
	{
		collector.collect(this);
	}

	@Override
	public void visit(JRVisitor visitor)
	{
		visitor.visitGenericElement(this);
	}

	@Override
	public String getEvaluationGroup()
	{
		return evaluationGroup;
	}

	@Override
	public EvaluationTimeEnum getEvaluationTime()
	{
		return evaluationTime;
	}
}

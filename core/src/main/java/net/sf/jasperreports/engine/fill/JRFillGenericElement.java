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
package net.sf.jasperreports.engine.fill;

import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRGenericElement;
import net.sf.jasperreports.engine.JRGenericElementParameter;
import net.sf.jasperreports.engine.JRGenericElementType;
import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRVisitor;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;

/**
 * A {@link JRGenericElement} used during report fill.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRFillGenericElement extends JRFillElement implements
		JRGenericElement
{
	
	private JRGenericElementParameter[] parameters;
	private Map<String,Object> parameterValues = new HashMap<>();
	
	public JRFillGenericElement(JRBaseFiller filler, JRGenericElement element,
			JRFillObjectFactory factory)
	{
		super(filler, element, factory);
		
		this.parameters = element.getParameters();
	}
	
	public JRFillGenericElement(JRFillGenericElement element,
			JRFillCloneFactory factory)
	{
		super(element, factory);
		
		this.parameters = element.parameters;
	}

	@Override
	protected void evaluate(byte evaluation) throws JRException
	{
		initDelayedEvaluations();
		reset();
		evaluatePrintWhenExpression(evaluation);

		if (isPrintWhenExpressionNull() || isPrintWhenTrue())
		{
			if (isEvaluateNow())
			{
				evaluateProperties(evaluation);
				evaluateStyle(evaluation);
				
				evaluateElement(evaluation);
			}
		}
	}
	
	@Override
	protected void collectDelayedEvaluations()
	{
		super.collectDelayedEvaluations();
		
		for (JRGenericElementParameter parameter : parameters)
		{
			collectDelayedEvaluations(parameter.getValueExpression());
		}
	}

	protected void evaluateElement(byte evaluation) throws JRException
	{
		parameterValues.clear();
		for (JRGenericElementParameter parameter : parameters)
		{
			Object value = null;
			JRExpression valueExpression = parameter.getValueExpression();
			if (valueExpression != null)
			{
				value = evaluateExpression(valueExpression, evaluation);
			}
			
			if (value != null || !parameter.isSkipWhenEmpty())
			{
				parameterValues.put(parameter.getName(), value);
			}
		}
	}

	@Override
	protected boolean prepare(
		int availableHeight, 
		boolean isOverflow,
		boolean isOverflowAllowed
		) throws JRException
	{
		boolean willOverflow = false;

		super.prepare(availableHeight, isOverflow, isOverflowAllowed);
		
		if (!isToPrint())
		{
			return willOverflow;
		}
		
		boolean isToPrint = true;
		boolean isReprinted = false;

		if (isOverflow && isAlreadyPrinted() && !isPrintWhenDetailOverflows())
		{
			isToPrint = false;
		}

		if (isToPrint && availableHeight < getRelativeY() + getHeight())
		{
			isToPrint = false;
			willOverflow = true;
		}

		if (isToPrint && isOverflow && isPrintWhenDetailOverflows()
				&& (isAlreadyPrinted() || !isPrintRepeatedValues()))
		{
			isReprinted = true;
		}
		
		setToPrint(isToPrint);
		setReprinted(isReprinted);
		
		return willOverflow;
	}

	@Override
	protected JRPrintElement fill() throws JRException
	{
		JRTemplateGenericPrintElement printElement;
		if (isEvaluateAuto())
		{
			JRRecordedValuesGenericPrintElement recordedValuesElement = 
				new JRRecordedValuesGenericPrintElement(getTemplate(), printElementOriginator, parameters.length);
			copyBasicAttributes(recordedValuesElement);
			initDelayedEvaluationPrint(recordedValuesElement);
			printElement = recordedValuesElement;
		}
		else
		{
			printElement = new JRTemplateGenericPrintElement(getTemplate(), printElementOriginator, parameters.length);
			copyBasicAttributes(printElement);
			if (isEvaluateNow())
			{
				copy(printElement);
			}
			else
			{
				filler.addBoundElement(this, printElement, 
						getEvaluationTime(), getEvaluationGroup(), band);
			}
		}
		return printElement;
	}

	protected void copyBasicAttributes(JRGenericPrintElement printElement)
	{
		printElement.setUUID(this.getUUID());
		printElement.setX(this.getX());
		printElement.setY(this.getRelativeY());
		printElement.setWidth(getWidth());
		printElement.setHeight(this.getStretchHeight());
	}
	
	protected JRTemplateGenericElement getTemplate()
	{
		return (JRTemplateGenericElement) getElementTemplate();
	}

	@Override
	protected JRTemplateElement createElementTemplate()
	{
		return new JRTemplateGenericElement(
				getElementOrigin(), 
				filler.getJasperPrint().getDefaultStyleProvider(), 
				this);
	}

	protected void copy(JRGenericPrintElement printElement)
	{
		for (Map.Entry<String,Object> entry : parameterValues.entrySet())
		{
			String name = entry.getKey();
			Object value = entry.getValue();
			printElement.setParameterValue(name, value);
		}
		
		transferProperties(printElement);
	}

	@Override
	protected void resolveElement(JRPrintElement element, byte evaluation)
			throws JRException
	{
		JRGenericPrintElement genericElement = (JRGenericPrintElement) element;
		evaluateElement(evaluation);
		copy(genericElement);
	}

	@Override
	protected void rewind() throws JRException
	{
		// NOOP
	}

	@Override
	public JRGenericElementType getGenericType()
	{
		return ((JRGenericElement) parent).getGenericType();
	}

	@Override
	public JRGenericElementParameter[] getParameters()
	{
		return parameters;
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
	public JRFillCloneable createClone(JRFillCloneFactory factory)
	{
		return new JRFillGenericElement(this, factory);
	}

	@Override
	public EvaluationTimeEnum getEvaluationTime()
	{
		return EvaluationTimeEnum.getValueOrDefault(((JRGenericElement)this.parent).getEvaluationTime());
	}
	
	@Override
	public String getEvaluationGroup()
	{
		return ((JRGenericElement) parent).getEvaluationGroup();
	}

}

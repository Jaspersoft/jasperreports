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
package net.sf.jasperreports.engine.fill;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JROrigin;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JRVisitable;
import net.sf.jasperreports.engine.JRVisitor;
import net.sf.jasperreports.engine.component.Component;
import net.sf.jasperreports.engine.component.ComponentManager;
import net.sf.jasperreports.engine.component.ComponentsEnvironment;
import net.sf.jasperreports.engine.component.ConditionalStyleAwareFillComponent;
import net.sf.jasperreports.engine.component.FillComponent;
import net.sf.jasperreports.engine.component.FillContext;
import net.sf.jasperreports.engine.component.FillPrepareResult;
import net.sf.jasperreports.engine.component.StretchableFillComponent;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;

/**
 * A {@link JRComponentElement} which is used during report fill.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRFillComponentElement extends JRFillElement implements JRComponentElement, FillContext
{

	private FillComponent fillComponent;
	private boolean filling;
	private List<JRFillDatasetRun> componentDatasetRuns;
	
	public JRFillComponentElement(JRBaseFiller filler, JRComponentElement element,
			JRFillObjectFactory factory)
	{
		super(filler, element, factory);
		
		Component elementComponent = element.getComponent();
		ComponentManager manager = ComponentsEnvironment.getInstance(filler.getJasperReportsContext()).getManager(elementComponent);
		
		factory.trackDatasetRuns();
		fillComponent = manager.getComponentFillFactory(filler.getJasperReportsContext()).toFillComponent(elementComponent, factory);
		fillComponent.initialize(this);
		this.componentDatasetRuns = factory.getTrackedDatasetRuns();
	}

	public JRFillComponentElement(JRFillComponentElement element,
			JRFillCloneFactory factory)
	{
		super(element, factory);
		
		Component elementComponent = element.getComponent();
		ComponentManager manager = ComponentsEnvironment.getInstance(filler.getJasperReportsContext()).getManager(elementComponent);
		fillComponent = manager.getComponentFillFactory(filler.getJasperReportsContext()).cloneFillComponent(element.fillComponent, factory);
		fillComponent.initialize(this);
	}

	@Override
	protected void setBand(JRFillBand band)
	{
		super.setBand(band);
		
		if (componentDatasetRuns != null && !componentDatasetRuns.isEmpty())
		{
			for (JRFillDatasetRun datasetRun : componentDatasetRuns)
			{
				datasetRun.setBand(band);
			}
		}
	}

	@Override
	protected void evaluate(byte evaluation) throws JRException
	{
		reset();
		evaluatePrintWhenExpression(evaluation);

		if (isPrintWhenExpressionNull() || isPrintWhenTrue())
		{
			evaluateProperties(evaluation);
			evaluateStyle(evaluation);
			
			fillComponent.evaluate(evaluation);
		}
		
		filling = false;
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

		if (!filling 
				&& isOverflow && isAlreadyPrinted() && !isPrintWhenDetailOverflows())
		{
			isToPrint = false;
		}

		if (isToPrint && availableHeight <  getRelativeY() + getHeight())
		{
			isToPrint = false;
			willOverflow = true;
		}

		if (!filling && isToPrint && isOverflow && isPrintWhenDetailOverflows()
				&& (isAlreadyPrinted() || !isPrintRepeatedValues()))
		{
			isReprinted = true;
		}

		if (isToPrint)
		{
			FillPrepareResult result = fillComponent.prepare(availableHeight - getRelativeY(), isOverflowAllowed);
			
			isToPrint = result.isToPrint();
			willOverflow = result.willOverflow();
			setPrepareHeight(result.getStretchHeight());
			
			// if the component will overflow, set the filling flag to true
			// to know next time that the component is continuing
			filling = willOverflow;
		}
		
		setToPrint(isToPrint);
		setReprinted(isReprinted);
		
		return willOverflow;
	}

	@Override
	protected void setStretchHeight(int stretchHeight)
	{
		super.setStretchHeight(stretchHeight);
		
		StretchableFillComponent stretchableFillComponent = 
			fillComponent instanceof StretchableFillComponent ? (StretchableFillComponent)fillComponent : null;
		if (stretchableFillComponent != null)
		{
			stretchableFillComponent.setStretchHeight(stretchHeight);
		}
	}

	@Override
	public void setConditionalStylesContainer(JRFillElementContainer conditionalStylesContainer)
	{
		super.setConditionalStylesContainer(conditionalStylesContainer);
		
		ConditionalStyleAwareFillComponent conditionalStyleAwareFillComponent = 
			fillComponent instanceof ConditionalStyleAwareFillComponent ? (ConditionalStyleAwareFillComponent)fillComponent : null;
		if (conditionalStyleAwareFillComponent != null)
		{
			conditionalStyleAwareFillComponent.setConditionalStylesContainer(conditionalStylesContainer);
		}
	}

	@Override
	protected JRPrintElement fill() throws JRException
	{
		return fillComponent.fill();
	}

	@Override
	protected JRTemplateElement createElementTemplate()
	{
		// not called
		return null;
	}

	@Override
	protected void resolveElement(JRPrintElement element, byte evaluation, 
			JREvaluationTime evaluationTime) throws JRException
	{
		performDelayedEvaluation(element, evaluation);
	}
	
	@Override
	protected void resolveElement(JRPrintElement element, byte evaluation)
			throws JRException
	{
		fillComponent.evaluateDelayedElement(element, evaluation);
	}

	@Override
	protected void rewind() throws JRException
	{
		fillComponent.rewind();
		filling = false;
	}

	@Override
	public void collectExpressions(JRExpressionCollector collector)
	{
		collector.collect(this);
	}

	@Override
	public void visit(JRVisitor visitor)
	{
		visitor.visitComponentElement(this);

		// visiting the fill component, not the parent component
		if (fillComponent instanceof JRVisitable)
		{
			((JRVisitable) fillComponent).visit(visitor);
		}
	}

	@Override
	public JRFillCloneable createClone(JRFillCloneFactory factory)
	{
		return new JRFillComponentElement(this, factory);
	}

	@Override
	public JRComponentElement getParent()
	{
		return (JRComponentElement) parent;
	}

	@Override
	public Component getComponent()
	{
		return ((JRComponentElement) parent).getComponent();
	}
	
	@Override
	public Object evaluate(JRExpression expression, byte evaluation)
			throws JRException
	{
		return super.evaluateExpression(expression, evaluation);
	}

	@Override
	public JRFillDataset getFillDataset()
	{
		return expressionEvaluator.getFillDataset();
	}

	@Override
	public JRComponentElement getComponentElement()
	{
		return this;
	}

	@Override
	public int getElementSourceId()
	{
		return printElementOriginator.getSourceElementId();
	}
	
	@Override
	public PrintElementOriginator getPrintElementOriginator()
	{
		return printElementOriginator;
	}

	@Override
	public JROrigin getElementOrigin()
	{
		return super.getElementOrigin();
	}

	@Override
	public int getElementPrintY()
	{
		return getRelativeY();
	}

	@Override
	public JRStyle getElementStyle()
	{
		return getStyle();
	}

	@Override
	public void registerDelayedEvaluation(JRPrintElement printElement, 
			EvaluationTimeEnum evaluationTime, String evaluationGroup)
	{
		filler.addBoundElement(this, printElement, 
				evaluationTime, evaluationGroup, band);
	}

	@Override
	public Locale getReportLocale()
	{
		return filler.getLocale();
	}

	@Override
	public ResourceBundle getReportResourceBundle()
	{
		return filler.getResourceBundle();
	}

	@Override
	public TimeZone getReportTimezone()
	{
		return filler.getTimeZone();
	}

	@Override
	public JRBaseFiller getFiller()
	{
		return filler;
	}

	@Override
	public FillContainerContext getFillContainerContext()
	{
		return fillContainerContext;
	}

}

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
package net.sf.jasperreports.engine.part;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRPart;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRPropertyExpression;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.PrintPart;
import net.sf.jasperreports.engine.fill.JRFillExpressionEvaluator;
import net.sf.jasperreports.engine.fill.JRFillObjectFactory;
import net.sf.jasperreports.engine.fill.PartReportFiller;
import net.sf.jasperreports.engine.util.ElementalPropertiesHolder;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class FillPart
{

	private JRPart reportPart;
	private JRFillExpressionEvaluator expressionEvaluator;
	private PartReportFiller reportFiller;
	
	private JRPropertiesHolder staticPartProperties;
	private List<JRPropertyExpression> propertyExpressions;
	private PartFillComponent fillComponent;

	public FillPart(JRPart part, JRFillObjectFactory fillFactory)
	{
		this.reportPart = part;
		this.expressionEvaluator = fillFactory.getExpressionEvaluator();
		reportFiller = (PartReportFiller) fillFactory.getReportFiller();//FIXMEBOOK
		
		PartComponent component = part.getComponent();
		
		JasperReportsContext jasperReportsContext = fillFactory.getReportFiller().getJasperReportsContext();
		
		staticPartProperties = new ElementalPropertiesHolder();
		JRPropertiesUtil.getInstance(jasperReportsContext).transferProperties(part, staticPartProperties, 
				PrintPart.PROPERTIES_TRANSFER_PREFIX);
		
		JRPropertyExpression[] partPropertyExpressions = part.getPropertyExpressions();
		propertyExpressions = partPropertyExpressions == null ? new ArrayList<>(0)
			: new ArrayList<>(Arrays.asList(partPropertyExpressions));
		
		PartComponentsEnvironment partsEnv = PartComponentsEnvironment.getInstance(jasperReportsContext);
		PartComponentManager componentManager = partsEnv.getManager(component);
		PartComponentFillFactory componentFactory = componentManager.getComponentFillFactory(jasperReportsContext);
		
		//fillFactory.trackDatasetRuns();FIXMEBOOK
		this.fillComponent = componentFactory.toFillComponent(component, fillFactory);
		fillComponent.initialize(new Context());
	}
	
	public JRPart getPart()
	{
		return reportPart;
	}
	
	public EvaluatedPart evaluate(byte evaluation) throws JRException
	{
		boolean toPrint = evaluatePrintWhenExpression(evaluation);
		if (!toPrint)
		{
			return EvaluatedPart.NO_PRINT;
		}
		
		JRPropertiesHolder partProperties = evaluateProperties(evaluation);
		String partName = evaluatePartNameExpression(evaluation);
		
		Object evaluatedComponent = fillComponent.evaluate(evaluation);
		
		return new EvaluatedPart(partName, partProperties, evaluatedComponent);
	}
	
	public void fill(EvaluatedPart evaluatedPart, PartPrintOutput output) throws JRException
	{
		fillComponent.fill(evaluatedPart, output);
	}

	protected boolean evaluatePrintWhenExpression(byte evaluation) throws JRException
	{
		JRExpression expression = reportPart.getPrintWhenExpression();
		boolean result;
		if (expression == null)
		{
			result = true;
		}
		else
		{
			Boolean expressionResult = (Boolean) expressionEvaluator.evaluate(expression, evaluation);
			result = expressionResult != null && expressionResult;
		}
		return result;
	}
	
	protected JRPropertiesHolder evaluateProperties(byte evaluation) throws JRException
	{
		JRPropertiesMap dynamicProperties = new JRPropertiesMap();
		for (JRPropertyExpression prop : propertyExpressions)
		{
			String value = (String) expressionEvaluator.evaluate(prop.getValueExpression(), evaluation);
			dynamicProperties.setProperty(prop.getName(), value);
		}
		
		JRPropertiesHolder printPartProperties;
		if (dynamicProperties.isEmpty())
		{
			printPartProperties = staticPartProperties;
		}
		else
		{
			JRPropertiesMap props = new JRPropertiesMap();
			props.setBaseProperties(staticPartProperties.getPropertiesMap());
			printPartProperties = new ElementalPropertiesHolder(props);
			JRPropertiesUtil.getInstance(reportFiller.getJasperReportsContext()).transferProperties(
					dynamicProperties, printPartProperties, PrintPart.PROPERTIES_TRANSFER_PREFIX);
		}
		return printPartProperties;
	}

	protected String evaluatePartNameExpression(byte evaluation) throws JRException
	{
		JRExpression expression = reportPart.getPartNameExpression();
		return expression == null ? null : (String) expressionEvaluator.evaluate(expression, evaluation);
	}

	public PartEvaluationTime getEvaluationTime()
	{
		PartEvaluationTime evaluationTime = reportPart.getEvaluationTime();
		return evaluationTime == null || evaluationTime.getEvaluationTimeType() == null ? StandardPartEvaluationTime.EVALUATION_NOW : evaluationTime;
	}

	protected class Context implements PartFillContext
	{
		@Override
		public JRPart getPart()
		{
			return reportPart;
		}

		@Override
		public FillPart getFillPart()
		{
			return FillPart.this;
		}

		@Override
		public PartReportFiller getFiller()
		{
			return reportFiller;
		}

		@Override
		public Object evaluate(JRExpression expression, byte evaluation) throws JRException
		{
			return reportFiller.evaluateExpression(expression, evaluation);
		}
	}
}

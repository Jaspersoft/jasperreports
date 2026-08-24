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

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRHyperlink;
import net.sf.jasperreports.engine.JRHyperlinkParameter;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameter;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameters;
import net.sf.jasperreports.engine.base.JRBasePrintHyperlink;


/**
 * Utility class used to evaluate custom hyperlink parameters.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public final class JRFillHyperlinkHelper
{

	/**
	 * Evaluates a list of hyperlink parameters and produces a hyperlink parameters set
	 * that can be associated with a print element.
	 *  
	 * @param hyperlink the hyperlink instance
	 * @param expressionEvaluator the expression evaluator to use for evaluation parameter value
	 * @param evaluationType the evaluation type
	 * @return a print hyperlink parameters set
	 * @throws JRException
	 */
	public static JRPrintHyperlinkParameters evaluateHyperlinkParameters(
			JRHyperlink hyperlink, 
			JRFillExpressionEvaluator expressionEvaluator,
			byte evaluationType) throws JRException
	{
		JRHyperlinkParameter[] hyperlinkParameters = hyperlink.getHyperlinkParameters();
		JRPrintHyperlinkParameters printParameters;
		if (hyperlinkParameters == null)
		{
			printParameters = null;
		}
		else
		{
			printParameters = new JRPrintHyperlinkParameters();
			for (JRHyperlinkParameter hyperlinkParameter : hyperlinkParameters)
			{
				JRExpression valueExpression = hyperlinkParameter.getValueExpression();
				Class<?> valueClass;
				Object value;
				if (valueExpression == null)
				{
					value = null;
					valueClass = Object.class;
				}
				else
				{
					value = expressionEvaluator.evaluate(valueExpression, evaluationType);
					valueClass = value == null ? Object.class : value.getClass();
				}
				
				JRPrintHyperlinkParameter printParam = new JRPrintHyperlinkParameter(hyperlinkParameter.getName(), valueClass.getName(), value);
				printParameters.addParameter(printParam);
			}
		}
		return printParameters;
	}
	
	
	/**
	 * Evaluate a hyperlink specification.
	 * 
	 * @param hyperlink the hyperlink specification
	 * @param expressionEvaluator the expression evaluator to use for evaluation the hyperlink expressions
	 * @param evaluationType the evaluation type, as in {@link JRFillExpressionEvaluator#evaluate(JRExpression, byte) JRFillExpressionEvaluator.evaluate(JRExpression, byte)}
	 * @return a {@link JRPrintHyperlink print hyperlink} resulted from the expression evaluations.
	 * @throws JRException
	 */
	public static JRPrintHyperlink evaluateHyperlink(JRHyperlink hyperlink,
			JRFillExpressionEvaluator expressionEvaluator,
			byte evaluationType) throws JRException
	{
		if (hyperlink == null)
		{
			return null;
		}
		
		Boolean hyperlinkWhen = (Boolean) expressionEvaluator.evaluate(hyperlink.getHyperlinkWhenExpression(), evaluationType);
		if (hyperlink.getHyperlinkWhenExpression() != null && !Boolean.TRUE.equals(hyperlinkWhen))
		{
			return null;
		}
		
		JRBasePrintHyperlink printHyperlink = new JRBasePrintHyperlink();
		printHyperlink.setLinkType(hyperlink.getLinkType());
		printHyperlink.setLinkTarget(hyperlink.getLinkTarget());
		printHyperlink.setHyperlinkReference((String) expressionEvaluator.evaluate(hyperlink.getHyperlinkReferenceExpression(), evaluationType));
		printHyperlink.setHyperlinkAnchor((String) expressionEvaluator.evaluate(hyperlink.getHyperlinkAnchorExpression(), evaluationType));
		printHyperlink.setHyperlinkPage((Integer) expressionEvaluator.evaluate(hyperlink.getHyperlinkPageExpression(), evaluationType));
		printHyperlink.setHyperlinkTooltip((String) expressionEvaluator.evaluate(hyperlink.getHyperlinkTooltipExpression(), evaluationType));
		printHyperlink.setHyperlinkParameters(evaluateHyperlinkParameters(hyperlink, expressionEvaluator, evaluationType));
		return printHyperlink;
	}
	
	
	private JRFillHyperlinkHelper()
	{
	}
}

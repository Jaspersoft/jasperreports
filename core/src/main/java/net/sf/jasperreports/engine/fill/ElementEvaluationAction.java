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
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Delayed evaluation action that evaluates a print element.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ElementEvaluationAction implements EvaluationBoundAction
{
	private static final Log log = LogFactory.getLog(ElementEvaluationAction.class);
	
	protected final JRFillElement element;
	protected final JRPrintElement printElement;
	private double dpiScale = 1d;

	public ElementEvaluationAction(JRFillElement element, JRPrintElement printElement)
	{
		this.element = element;
		this.printElement = printElement;
	}
	
	public double getDpiScale()
	{
		return dpiScale;
	}

	public void multiplyDpiScale(double scale)
	{
		this.dpiScale *= scale;
	}

	@Override
	public void execute(BoundActionExecutionContext executionContext) throws JRException
	{
		if (log.isDebugEnabled())
		{
			log.debug("resolving element " + printElement + " by " + element
					+ " on " + executionContext.getEvaluationTime());
		}
		
		JREvaluationTime evaluationTime = executionContext.getEvaluationTime();
		
		if (evaluationTime.getType() == EvaluationTimeEnum.MASTER)
		{
			int currentPageIndex = executionContext.getCurrentPageIndex();
			int totalPages = executionContext.getTotalPages();
			element.filler.setMasterPageVariables(currentPageIndex, totalPages);
		}
		
		element.resolveElement(printElement, 
				executionContext.getExpressionEvaluationType(), evaluationTime);
		
		if (dpiScale != 1d)
		{
			OffsetElementsUtil.scaleTextProperties(printElement, dpiScale);
		}
	}

	@Override
	public String toString()
	{
		return "delayed evaluation {element: " + element
				+ ", printElement: " + printElement
				+ "}";
	}
}
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRPrintElement;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ElementRangeContents implements ElementEvaluationsCollector, ElementEvaluationsSource
{

	private final List<JRPrintElement> elements;
	private final Map<JREvaluationTime, Map<JRPrintElement, JRFillElement>> evaluations;
	
	public ElementRangeContents()
	{
		this.elements = new ArrayList<>();
		this.evaluations = new LinkedHashMap<>();
	}
	
	public void addElement(JRPrintElement element)
	{
		elements.add(element);
	}
	
	public List<JRPrintElement> getElements()
	{
		return elements;
	}

	@Override
	public void collect(JRPrintElement printElement, JRFillElement fillElement, JREvaluationTime evaluationTime)
	{
		evaluations.computeIfAbsent(evaluationTime, k -> new LinkedHashMap<>()).put(printElement, fillElement);
	}
	
	public boolean hasEvaluations()
	{
		return !evaluations.isEmpty();
	}

	@Override
	public Map<JRPrintElement, JRFillElement> getEvaluations(JREvaluationTime evaluationTime)
	{
		return evaluations.get(evaluationTime);
	}
	
}

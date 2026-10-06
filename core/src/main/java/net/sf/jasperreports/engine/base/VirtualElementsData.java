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
package net.sf.jasperreports.engine.base;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.fill.JREvaluationTime;
import net.sf.jasperreports.engine.util.Pair;
import net.sf.jasperreports.engine.virtualization.VirtualizationInput;
import net.sf.jasperreports.engine.virtualization.VirtualizationOutput;
import net.sf.jasperreports.engine.virtualization.VirtualizationSerializable;

/**
 * Virtual data type used by report pages to virtualize elements.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class VirtualElementsData implements Serializable, VirtualizationSerializable
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private Map<Pair<Integer, JREvaluationTime>, Map<JRPrintElement, Integer>> elementEvaluations;
	private List<JRPrintElement> elements;
	
	public VirtualElementsData()
	{
	}
	
	public VirtualElementsData(List<JRPrintElement> elements)
	{
		this.elements = elements;
	}
	
	public void setElementEvaluations(int fillerId, JREvaluationTime evaluationTime, Map<JRPrintElement, Integer> evaluations)
	{
		if (elementEvaluations == null)
		{
			elementEvaluations = new HashMap<>();
		}
		
		elementEvaluations.put(new Pair<>(fillerId, evaluationTime), evaluations);
	}

	public Map<JRPrintElement, Integer> getElementEvaluations(int fillerId, JREvaluationTime evaluationTime)
	{
		return elementEvaluations == null ? null : elementEvaluations.get(
				new Pair<Integer, JREvaluationTime>(fillerId, evaluationTime));
	}
	
	public List<JRPrintElement> getElements()
	{
		return elements;
	}

	@Override
	public void writeVirtualized(VirtualizationOutput out) throws IOException
	{
		boolean hasEvaluations = elementEvaluations != null && !elementEvaluations.isEmpty();
		
		if (hasEvaluations)
		{
			out.writeIntCompressed(elementEvaluations.size());
			for (Entry<Pair<Integer, JREvaluationTime>, Map<JRPrintElement, Integer>> entry : elementEvaluations.entrySet())
			{
				Pair<Integer, JREvaluationTime> key = entry.getKey();
				out.writeIntCompressed(key.first());
				out.writeJRObject(key.second());
				
				Map<JRPrintElement, Integer> evaluations = entry.getValue();
				if (evaluations == null || evaluations.isEmpty())
				{
					out.writeIntCompressed(0);
				}
				else
				{
					out.writeIntCompressed(evaluations.size());
					for (Entry<JRPrintElement, Integer> evaluationEntry : evaluations.entrySet())
					{
						JRPrintElement element = evaluationEntry.getKey();
						// print elements do not track references by default, forcing
						out.writeJRObject(element, true, true);
						out.writeIntCompressed(evaluationEntry.getValue());
					}
				}
			}
		}
		else
		{
			out.writeIntCompressed(0);
		}
		
		out.writeIntCompressed(elements.size());
		for (JRPrintElement element : elements)
		{
			if (hasEvaluations)
			{
				// looking up references to elements written as part of elementEvaluations
				//FIXME find a solution that doesn't require reference lookup
				out.writeJRObject(element, true, false);
			}
			else
			{
				out.writeJRObject(element);
			}
		}
	}

	@Override
	public void readVirtualized(VirtualizationInput in) throws IOException
	{
		int evaluationsCount = in.readIntCompressed();
		boolean hasEvaluations = evaluationsCount > 0;
		if (hasEvaluations)
		{
			elementEvaluations = new HashMap<>(
					evaluationsCount * 4 / 3 + 1, .75f);
			
			for (int i = 0; i < evaluationsCount; i++)
			{
				int fillerId = in.readIntCompressed();
				JREvaluationTime evalTime = (JREvaluationTime) in.readJRObject();
				
				int count = in.readIntCompressed();
				Map<JRPrintElement, Integer> evaluations;
				if (count == 0)
				{
					evaluations = null;
				}
				else
				{
					evaluations = new HashMap<>(count * 4 / 3 + 1, .75f);//FIXME use LinkedHashMap?
					for (int j = 0; j < count; j++)
					{
						// these elements are references in the elements list, storing references
						JRPrintElement element = (JRPrintElement) in.readJRObject(true);
						int value = in.readIntCompressed();
						evaluations.put(element, value);
					}
				}
				
				elementEvaluations.put(new Pair<>(fillerId, evalTime), evaluations);
			}
		}
		
		int size = in.readIntCompressed();
		elements = new ArrayList<>(size);
		for (int i = 0; i < size; i++)
		{
			JRPrintElement element = (JRPrintElement) in.readJRObject();
			elements.add(element);
		}
	}
	
	public VirtualElementsData copy()
	{
		ArrayList<JRPrintElement> elementsCopy = new ArrayList<>(elements);
		VirtualElementsData copy = new VirtualElementsData(elementsCopy);
		
		if (elementEvaluations != null)
		{
			for (Entry<Pair<Integer, JREvaluationTime>, Map<JRPrintElement, Integer>> entry : elementEvaluations.entrySet())
			{
				Pair<Integer, JREvaluationTime> key = entry.getKey();
				Map<JRPrintElement, Integer> evals = entry.getValue();
				LinkedHashMap<JRPrintElement, Integer> evalsClone = new LinkedHashMap<>(evals);
				copy.setElementEvaluations(key.first(), key.second(), evalsClone);
			}
		}
		return copy;
	}
}

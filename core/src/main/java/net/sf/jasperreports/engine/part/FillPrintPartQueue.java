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

import java.util.function.Supplier;

import net.sf.jasperreports.engine.JRException;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class FillPrintPartQueue implements FillPartQueue
{

	private PrintPartList partList;
	
	public FillPrintPartQueue(PartPrintOutput output)
	{
		FilledPrintPart initialPart = new FilledPrintPart(output);
		this.partList = new PrintPartList(initialPart);
	}

	@Override
	public FillPrintPart head()
	{
		return partList.head();
	}

	@Override
	public void beforeEvaluate(FillPart part) 
	{
		//NOOP
	}
	
	@Override
	public void finishParts()
	{
		assert partList.isCollapsed();
	}

	@Override
	public void fillPart(FillPart fillPart, EvaluatedPart evaluatedPart, Supplier<PartPrintOutput> localOutputSupplier) throws JRException
	{
		PartPrintOutput appendOutput = partList.tail().getOutput();
		if (appendOutput != null)
		{
			// can write directly to the previous output
			fillPart.fill(evaluatedPart, appendOutput);
		}
		else
		{
			// previous part is delayed, creating a new part with local output
			PartPrintOutput localOutput = localOutputSupplier.get();
			fillPart.fill(evaluatedPart, localOutput);
			
			// adding to the queue
			appendOutput(localOutput);
		}
	}
	
	protected FilledPrintPart appendOutput(PartPrintOutput output)
	{
		FilledPrintPart filledPart = new FilledPrintPart(output);
		partList.append(filledPart);
		return filledPart;
	}

	@Override
	public DelayedPrintPart appendDelayed(FillPart fillPart)
	{
		DelayedPrintPart delayedPart = new DelayedPrintPart(fillPart);
		partList.append(delayedPart);
		return delayedPart;
	}

	@Override
	public void fillDelayed(DelayedPrintPart part, EvaluatedPart evaluatedPart, Supplier<PartPrintOutput> localOutputSupplier) throws JRException
	{
		PartPrintOutput appendOutput = part.previousPart().getOutput();
		FillPart fillPart = part.getFillPart();
		if (appendOutput != null)
		{
			fillPart.fill(evaluatedPart, appendOutput);
			partList.remove(part);
			partList.collapse(part.previousPart());
		}
		else
		{
			PartPrintOutput localOutput = localOutputSupplier.get();
			fillPart.fill(evaluatedPart, localOutput);
			FilledPrintPart filledPart = new FilledPrintPart(localOutput);
			partList.replace(part, filledPart);
			partList.collapse(filledPart);
		}
	}
	
	@Override
	public void dispose()
	{
		//NOOP
	}
	
	@Override
	public void cancelParts()
	{
		//NOOP
	}
	
	@Override
	public boolean useMainVirtualizationContext()
	{
		return true;
	}
}

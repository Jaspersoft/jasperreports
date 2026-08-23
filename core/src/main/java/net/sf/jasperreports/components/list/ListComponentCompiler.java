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
package net.sf.jasperreports.components.list;

import net.sf.jasperreports.engine.JRDatasetRun;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.component.Component;
import net.sf.jasperreports.engine.component.ComponentCompiler;
import net.sf.jasperreports.engine.design.JRVerifier;
import net.sf.jasperreports.engine.type.PrintOrderEnum;

/**
 * Compile-time handler of {@link ListComponent list component} instances.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ListComponentCompiler implements ComponentCompiler
{

	@Override
	public void collectExpressions(Component component,
			JRExpressionCollector collector)
	{
		ListComponent listComponent = (ListComponent) component;
		JRDatasetRun datasetRun = listComponent.getDatasetRun();
		collector.collect(datasetRun);
		
		JRExpressionCollector datasetCollector = collector.getDatasetCollector(
				datasetRun.getDatasetName());
		JRElement[] elements = listComponent.getContents().getElements();
		if (elements != null)
		{
			for (int i = 0; i < elements.length; i++)
			{
				elements[i].collectExpressions(datasetCollector);
			}
		}
	}

	@Override
	public Component toCompiledComponent(Component component,
			JRBaseObjectFactory baseFactory)
	{
		ListComponent listComponent = (ListComponent) component;
		StandardListComponent compiledComponent = new StandardListComponent(
				listComponent, baseFactory);
		return compiledComponent;
	}

	@Override
	public void verify(Component component, JRVerifier verifier)
	{
		ListComponent listComponent = (ListComponent) component;

		JRDatasetRun datasetRun = listComponent.getDatasetRun();
		if (datasetRun == null)
		{
			verifier.addBrokenRule("No list subdataset run set", listComponent);
		}
		else
		{
			verifier.verifyDatasetRun(datasetRun);
		}
		
		ListContents listContents = listComponent.getContents();
		
		if (listContents == null)
		{
			verifier.addBrokenRule("No list contents set", listComponent);
		}
		else
		{
			PrintOrderEnum listPrintOrder = listComponent.getPrintOrder() == null ? PrintOrderEnum.VERTICAL : listComponent.getPrintOrder();
			
			Boolean listIgnoreWidth = listComponent.getIgnoreWidth();
			boolean ignoreWidth = listIgnoreWidth != null && listIgnoreWidth;
			
			if (listContents.getHeight() < 0)
			{
				verifier.addBrokenRule("List contents height must be positive.", listContents);
			}
			
			int elementWidth = verifier.getCurrentComponentElement().getWidth();
			Integer width = listContents.getWidth();
			int contentsWidth;
			if (width == null)
			{
				contentsWidth = elementWidth;
				
				if (listPrintOrder == PrintOrderEnum.HORIZONTAL)
				{
					verifier.addBrokenRule("List contents width must be set for horizontal lists", 
							listContents);
				}
			}
			else
			{
				contentsWidth = width;
				
				if (width <= 0)
				{
					verifier.addBrokenRule("List contents width must be positive.", listContents);
				}
				
				if (!ignoreWidth && listPrintOrder == PrintOrderEnum.HORIZONTAL 
						&& width > elementWidth)
				{
					verifier.addBrokenRule(
							"List contents width is larger than the list element width", 
							listComponent);
				}
			}
			
			String subdataset = datasetRun == null ? null : datasetRun.getDatasetName();
			if (subdataset != null)
			{
				verifier.pushSubdatasetContext(subdataset);
			}
			try
			{
				verifyContents(verifier, listContents, contentsWidth);
			}
			finally
			{
				if (subdataset != null)
				{
					verifier.popSubdatasetContext();
				}
			}			
		}
	}

	protected void verifyContents(JRVerifier verifier, ListContents listContents, int contentsWidth)
	{
		JRElement[] elements = listContents.getElements();
		if (elements != null)
		{
			for (JRElement element : elements)
			{
				verifier.verifyElement(element);
				
				if (element.getX() < 0 || element.getY() < 0)
				{
					verifier.addBrokenRule("Element must be placed at positive coordinates.", 
							element);
				}
				
				if (element.getY() + element.getHeight() > listContents.getHeight())
				{
					verifier.addBrokenRule("Element reaches outside list contents height: y = " 
							+ element.getY() + ", height = " + element.getHeight() 
							+ ", list contents height = " + listContents.getHeight() + ".", element);
				}
				
				if (element.getX() + element.getWidth() > contentsWidth)
				{
					verifier.addBrokenRule("Element reaches outside list contents width: x = " 
							+ element.getX() + ", width = " + element.getWidth() 
							+ ", list contents width = " + contentsWidth + ".", element);
				}
			}
		}
	}

}

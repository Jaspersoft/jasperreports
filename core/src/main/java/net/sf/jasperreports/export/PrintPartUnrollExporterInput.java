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
package net.sf.jasperreports.export;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.PrintPart;
import net.sf.jasperreports.engine.PrintParts;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class PrintPartUnrollExporterInput implements ExporterInput
{
	private Class<? extends ReportExportConfiguration> itemConfigurationInterface;
	private List<ExporterInputItem> partItems;
	
	/**
	 * 
	 */
	public PrintPartUnrollExporterInput(ExporterInput exporterInput, Class<? extends ReportExportConfiguration> itemConfigurationInterface)
	{
		this.itemConfigurationInterface = itemConfigurationInterface;
		partItems = new ArrayList<>();
		
		for (ExporterInputItem item : exporterInput.getItems())
		{
			JasperPrint jasperPrint = item.getJasperPrint();
			//SortedMap<Integer, PrintPart> parts = jasperPrint.getParts();
			if (jasperPrint.hasParts())
			{
				//exporting each part to a separate sheet irrespective of the visibility flag
				PrintParts parts = jasperPrint.getParts();
				Iterator<Map.Entry<Integer, PrintPart>> it = parts.partsIterator();
				Map.Entry<Integer, PrintPart> part = it.next();
				while (it.hasNext())
				{
					Map.Entry<Integer, PrintPart> next = it.next();
					addPartItem(item, part.getValue(), part.getKey(), next.getKey());
					part = next;
				}
				
				addPartItem(item, part.getValue(), part.getKey(), jasperPrint.getPages().size());
			}
			else
			{
				partItems.add(item);
			}
		}
	}

	private void addPartItem(ExporterInputItem item, PrintPart part, int partStartIndex, int partEndIndex)
	{
		JasperPrint jasperPrint = item.getJasperPrint();
		ReportExportConfiguration configuration = item.getConfiguration();
		Integer configStartPageIndex = null;
		Integer configEndPageIndex = null;
		if (configuration != null)
		{
			Integer configPageIndex = configuration.getPageIndex();
			configStartPageIndex = configPageIndex == null ? configuration.getStartPageIndex() : configPageIndex;
			configEndPageIndex = configPageIndex == null ? configuration.getEndPageIndex() : configPageIndex;
		}

		int itemStartIndex = configStartPageIndex == null ? partStartIndex : Math.max(configStartPageIndex, partStartIndex);
		int itemEndIndex = configStartPageIndex == null ? partEndIndex: Math.min(configEndPageIndex + 1, partEndIndex);
		if (itemStartIndex < itemEndIndex)
		{
			ReadOnlyPartJasperPrint itemJasperPrint = new ReadOnlyPartJasperPrint(jasperPrint, part, 
					partStartIndex, partEndIndex);
			
			ReportExportConfiguration itemConfiguration;
			if ((configStartPageIndex == null || configStartPageIndex == itemStartIndex - partStartIndex)
					&& (configEndPageIndex == null || configEndPageIndex == itemEndIndex - partStartIndex - 1))
			{
				itemConfiguration = configuration;
			}
			else
			{
				itemConfiguration = overrideConfiguration(configuration, 
						itemStartIndex - partStartIndex, itemEndIndex - partStartIndex - 1);
			}
			
			partItems.add(new SimpleExporterInputItem(itemJasperPrint, itemConfiguration));
		}
	}

	private ReportExportConfiguration overrideConfiguration(ReportExportConfiguration configuration, 
			int startIndex, int endIndex)
	{
		return (ReportExportConfiguration) Proxy.newProxyInstance(
				JRAbstractExporter.class.getClassLoader(),
				new Class<?>[] {itemConfigurationInterface},
				new InvocationHandler()
				{
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
					{
						if (method.getName().equals("getPageIndex"))
						{
							return null;
						}
						if (method.getName().equals("getStartPageIndex"))
						{
							return startIndex;
						}
						if (method.getName().equals("getEndPageIndex"))
						{
							return endIndex;
						}
						return method.invoke(configuration, args);
					}
				});
	}

	@Override
	public List<ExporterInputItem> getItems()
	{
		return partItems;
	}
}
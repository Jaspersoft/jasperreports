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
package net.sf.jasperreports.components.items;

import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDatasetRun;
import net.sf.jasperreports.engine.JRElementDataset;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.design.JRVerifier;

/**
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class ItemCompiler
{
	
	public static void collectExpressions(ItemData data, JRExpressionCollector collector)
	{
		if (data != null)
		{
			JRExpressionCollector datasetCollector = collector;

			JRElementDataset dataset = data.getDataset();
			JRDatasetRun datasetRun = dataset == null ? null : dataset.getDatasetRun();
			if (datasetRun != null)
			{
				collector.collect(datasetRun);
				datasetCollector = collector.getDatasetCollector(datasetRun.getDatasetName());
			}

			List<Item> items = data.getItems();
			if (items != null && !items.isEmpty())
			{
				for (Item item : items)
				{
					List<ItemProperty> itemProperties = item.getProperties();
					if (itemProperties != null)
					{
						for (ItemProperty property : itemProperties)
						{
							datasetCollector.addExpression(property.getValueExpression());
						}
					}
				}
			}
		}
	}
	
	public static void verifyItemData(JRVerifier verifier, ItemData itemData, String itemName, String[] requiredNames, Map<String, String> alternativeNamesMap)
	{
		if (itemData.getDataset() != null)
		{
			verifier.verifyElementDataset(itemData.getDataset());
		}
		
		List<Item> items = itemData.getItems();
		if (items != null)
		{
			for (Item item : items)
			{
				verifyItem(verifier, item, itemName, requiredNames, alternativeNamesMap);
			}
		}
	}

	/**
	 * Verifies if required properties or their alternatives are present in the item properties list. Alternative 
	 * property names are read from the <code>alternativeNamesMap</code> parameter. 
	 * <br/>
	 * For instance, a required latitude property can be provided either directly, using the <code>latitude</code> item 
	 * property, or by processing the alternative <code>address</code> property. If at least one of the <code>latitude</code> 
	 * or <code>address</code> properties are present in the item properties list, the latitude requirement is fulfilled.
	 * 
	 * @param verifier
	 * @param item
	 * @param itemName
	 * @param requiredNames
	 * @param alternativeNamesMap
	 */
	public static void verifyItem(JRVerifier verifier, Item item, String itemName, String[] requiredNames, Map<String, String> alternativeNamesMap)
	{
		if (requiredNames != null && requiredNames.length > 0){
			List<ItemProperty> itemProperties = item.getProperties();
			if (itemProperties != null && !itemProperties.isEmpty())
			{
				for (String reqName :requiredNames)
				{
					boolean hasProperty = false;
					for (ItemProperty itemProperty : itemProperties) {
						if (itemProperty.getName().equals(reqName)
							|| (alternativeNamesMap != null && itemProperty.getName().equals(alternativeNamesMap.get(reqName)))) {
							hasProperty = true;
							break;
						}
					}
					if (!hasProperty) 
					{
						verifier.addBrokenRule("No '" + reqName + "' property set for the " + itemName + " item.", itemProperties);
					}
				}
			}
		}
	}
	
}

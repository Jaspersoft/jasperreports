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
package net.sf.jasperreports.components.items.fill;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.sf.jasperreports.components.items.Item;
import net.sf.jasperreports.components.items.ItemData;
import net.sf.jasperreports.engine.JRElementDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.component.FillContextProvider;
import net.sf.jasperreports.engine.fill.JRFillExpressionEvaluator;
import net.sf.jasperreports.engine.fill.JRFillObjectFactory;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class FillItemData
{

	/**
	 *
	 */
	protected ItemData itemData;
	protected List<FillItem> itemsList;
	protected FillItemDataset fillItemDataset;
	protected FillContextProvider fillContextProvider;
	private List<Map<String,Object>> evaluatedItems = null;
	
	/**
	 *
	 */
	public FillItemData(
		FillContextProvider fillContextProvider,
		ItemData itemData, 
		JRFillObjectFactory factory
		)// throws JRException
	{
		factory.put(itemData, this);
		
		this.itemData = itemData;
		this.fillContextProvider = fillContextProvider;

		if (itemData.getDataset() != null)
		{
			fillItemDataset = new FillItemDataset(this, factory);
		}

		/*   */
		List<Item> srcItemList = itemData.getItems();
		if (srcItemList != null && !srcItemList.isEmpty())
		{
			itemsList = 
				srcItemList.stream().filter(item -> item != null)
					.map(item -> getFillItem(fillContextProvider, item, factory))
					.collect(Collectors.toCollection(ArrayList::new));
		}
	}
	
	/**
	 *
	 */
	public JRElementDataset getDataset()
	{
		return itemData.getDataset();
	}
	
	/**
	 *
	 */
	public void initialize()
	{
		evaluatedItems = null;
	}
	
	/**
	 *
	 */
	public void evaluateItems(JRFillExpressionEvaluator evaluator, byte evaluation) throws JRException
	{
		if (itemsList != null)
		{
			for(FillItem item : itemsList)
			{
				item.evaluateProperties(evaluator, evaluation);
			}
		}
	}
	
	/**
	 *
	 */
	public List<Map<String,Object>> getEvaluateItems(byte evaluation) throws JRException
	{
		if (fillItemDataset != null)
		{
			fillItemDataset.setEvaluation(evaluation);
			fillItemDataset.evaluateDatasetRun(evaluation);
		}
		else if (itemsList != null)
		{
			if (getDataset() == null)
			{
				evaluateItems(fillContextProvider.getFillContext(), evaluation);
			}
			
			addEvaluateItems();
		}
		
		return evaluatedItems;
	}
	
	/**
	 *
	 */
	public void addEvaluateItems()
	{
		if (itemsList != null)
		{
			if (evaluatedItems == null || getDataset() == null)
			{
				evaluatedItems = new ArrayList<>();
			}

			for(FillItem item : itemsList)
			{
				evaluatedItems.add(item.getEvaluatedProperties());
			}
		}
	}
	
	public abstract FillItem getFillItem(FillContextProvider fillContextProvider, Item item, JRFillObjectFactory factory);
}

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRElementDataset;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;
import net.sf.jasperreports.engine.util.JRCloneUtils;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class StandardItemData implements Serializable, ItemData, JRChangeEventsSupport
{//FIXMEMAP implement clone?

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	public static final String PROPERTY_ITEMS = "items";
	public static final String PROPERTY_DATASET = "dataset";

	private List<Item> itemsList = new ArrayList<>();
	private JRElementDataset dataset;
	
	private transient JRPropertyChangeSupport eventSupport;
	
	public StandardItemData()
	{
	}

	public StandardItemData(ItemData data, JRBaseObjectFactory factory)
	{
		itemsList = getCompiledItems(data.getItems(), factory);
		dataset = factory.getElementDataset(data.getDataset());
	}

	private static List<Item> getCompiledItems(List<Item> items, JRBaseObjectFactory factory)
	{
		if (items == null)
		{
			return null;
		}
		
		List<Item> compiledItems = new ArrayList<>(items.size());
		for (Iterator<Item> it = items.iterator(); it.hasNext();)
		{
			Item item = it.next();
			Item compiledItem = new StandardItem(getCompiledProperties(item.getProperties(), factory));
			compiledItems.add(compiledItem);
		}
		return compiledItems;
	}

	public static List<ItemProperty> getCompiledProperties(List<ItemProperty> properties, JRBaseObjectFactory factory)
	{
		if (properties == null)
		{
			return null;
		}
		
		List<ItemProperty> compiledProperties = new ArrayList<>(properties.size());
		for (Iterator<ItemProperty> it = properties.iterator(); it.hasNext();)
		{
			ItemProperty property = it.next();
			ItemProperty compiledProperty = new StandardItemProperty(property.getName(), property.getValue(), factory.getExpression(property.getValueExpression()));
			compiledProperties.add(compiledProperty);
		}
		return compiledProperties;
	}

	public void collectExpressions(JRExpressionCollector collector) {
		ItemCompiler.collectExpressions(this, collector);
	}

	@Override
	public List<Item> getItems() {
		return itemsList;
	}
	
	/**
	 *
	 */
	public void addItem(Item item)
	{
		itemsList.add(item);
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_ITEMS, item, itemsList.size() - 1);
	}
	
	/**
	 *
	 */
	public void addItem(int index, Item item)
	{
		if (index >=0 && index < itemsList.size())
			itemsList.add(index, item);
		else{
			itemsList.add(item);
			index = itemsList.size() - 1;
		}
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_ITEMS, itemsList, index);
	}

	/**
	 *
	 */
	public Item removeItem(Item item)
	{
		if (item != null)
		{
			int idx = itemsList.indexOf(item);
			if (idx >= 0)
			{
				itemsList.remove(idx);
				getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_ITEMS, item, idx);
			}
		}
		return item;
	}

	@Override
	public JRElementDataset getDataset()
	{
		return dataset;
	}
	
	/**
	 * Sets the dataset information that will be used to create the item list.
	 * 
	 * @param dataset the dataset information
	 * @see #getDataset()
	 */
	public void setDataset(JRElementDataset dataset)
	{
		Object old = this.dataset;
		this.dataset = dataset;
		getEventSupport().firePropertyChange(PROPERTY_DATASET, old, this.dataset);
	}
	
	@Override
	public JRPropertyChangeSupport getEventSupport()
	{
		synchronized (this)
		{
			if (eventSupport == null)
			{
				eventSupport = new JRPropertyChangeSupport(this);
			}
		}
		
		return eventSupport;
	}

	@Override
	public Object clone()
	{
		StandardItemData clone = null;
		try
		{
			clone = (StandardItemData) super.clone();
		} 
		catch (CloneNotSupportedException e)
		{
			// never
			throw new JRRuntimeException(e);
		}
		clone.dataset = JRCloneUtils.nullSafeClone(dataset);
		clone.itemsList = JRCloneUtils.cloneList(itemsList);
		clone.eventSupport = null;
		return clone;
	}

}

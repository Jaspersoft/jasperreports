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
package net.sf.jasperreports.customizers.util;

import net.sf.jasperreports.charts.JRAbstractChartCustomizer;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class CustomizerUtil
{
	public static final String PROPERTY_ALL_ITEMS = "allItems";
	public static final String PROPERTY_ITEM_INDEX = "itemIndex";
	public static final String PROPERTY_ITEM_SERIES = "itemSeries";

	
	/**
	 * Resolve the index of the plot item looking at the properties.
	 * If no item index or series name is specified then all items are considered by returning null.
	 */
	public static Integer resolveIndex(JRAbstractChartCustomizer customizer, ItemsCounter itemsCounter, SeriesNameProvider seriesNameProvider)
	{
		Integer itemIndex = null;

		Boolean allItems = customizer.getBooleanProperty(PROPERTY_ALL_ITEMS);
    	if (Boolean.TRUE.equals(allItems))
    	{
    		itemIndex = -1;
    	}
    	else
    	{
    		itemIndex = customizer.getIntegerProperty(PROPERTY_ITEM_INDEX);
    		if (itemIndex == null)
    		{
    			String seriesName = customizer.getProperty(PROPERTY_ITEM_SERIES);
    			if (seriesName != null)
    			{
    				itemIndex = seriesNameToIndex(itemsCounter, seriesNameProvider, seriesName);
    			}
    		}
    	}
    	
		return itemIndex;
	}

	/**
	 * Convert a series name to an item index
	 */
	public static Integer seriesNameToIndex(ItemsCounter itemsCounter, SeriesNameProvider seriesNameProvider, String seriesName)
	{
		for (int i = 0; i < itemsCounter.getCount(); i++)
		{
			Comparable<?> key = seriesNameProvider.getSeriesName(i);
			if (seriesName.equals(key.toString())){
				return i;
			}
		}
		return null;
	}
}

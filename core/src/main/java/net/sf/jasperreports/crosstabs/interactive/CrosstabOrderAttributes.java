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
package net.sf.jasperreports.crosstabs.interactive;

import java.io.Serializable;

import net.sf.jasperreports.crosstabs.JRCrosstabColumnGroup;
import net.sf.jasperreports.crosstabs.JRCrosstabRowGroup;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstab;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabBucket;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabDataset;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.analytics.dataset.BucketOrder;
import net.sf.jasperreports.engine.fill.JRFillCrosstab;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class CrosstabOrderAttributes implements Serializable
{
	
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private static final Log log = LogFactory.getLog(CrosstabOrderAttributes.class);

	private JRDesignCrosstab crosstab;
	private boolean dataPreSorted;
	private BucketOrder[] rowGroupOrders;
	private BucketOrder[] colGroupOrders;
	private String orderByColumnProp;

	public CrosstabOrderAttributes(JRDesignCrosstab crosstab)
	{
		this.crosstab = crosstab;
		
		dataPreSorted = crosstab.getDataset().isDataPreSorted();
		
		JRCrosstabRowGroup[] rowGroups = crosstab.getRowGroups();
		rowGroupOrders = new BucketOrder[rowGroups.length];
		for (int i = 0; i < rowGroups.length; i++)
		{
			rowGroupOrders[i] = BucketOrder.getValueOrDefault(rowGroups[i].getBucket().getOrder());
		}
		
		JRCrosstabColumnGroup[] colGroups = crosstab.getColumnGroups();
		colGroupOrders = new BucketOrder[colGroups.length];
		for (int i = 0; i < colGroups.length; i++)
		{
			colGroupOrders[i] = BucketOrder.getValueOrDefault(colGroups[i].getBucket().getOrder());
		}
		
		orderByColumnProp = crosstab.getPropertiesMap().getProperty(JRFillCrosstab.PROPERTY_ORDER_BY_COLUMN);
		
		if (log.isDebugEnabled())
		{
			log.debug("crosstab " + crosstab.getUUID() + " has order attributes " + this.toString());
		}
	}
	
	public void prepareSorting()
	{
		if (dataPreSorted)
		{
			if (log.isDebugEnabled())
			{
				log.debug("crosstab " + crosstab.getUUID() + " has the presorted flag set, converting to order NONE");
			}
			
			((JRDesignCrosstabDataset) crosstab.getDataset()).setDataPreSorted(false);
			
			JRCrosstabRowGroup[] rowGroups = crosstab.getRowGroups();
			for (int i = 0; i < rowGroups.length; i++)
			{
				((JRDesignCrosstabBucket) rowGroups[i].getBucket()).setOrder(BucketOrder.NONE);
			}
			
			JRCrosstabColumnGroup[] colGroups = crosstab.getColumnGroups();
			for (int i = 0; i < colGroups.length; i++)
			{
				((JRDesignCrosstabBucket) colGroups[i].getBucket()).setOrder(BucketOrder.NONE);
			}
		}
	}
	
	public void restore()
	{
		if (log.isDebugEnabled())
		{
			log.debug("restoring crosstab " + crosstab.getUUID() + " order attributes");
		}
		
		((JRDesignCrosstabDataset) crosstab.getDataset()).setDataPreSorted(dataPreSorted);
		
		JRCrosstabRowGroup[] rowGroups = crosstab.getRowGroups();
		for (int i = 0; i < rowGroups.length; i++)
		{
			((JRDesignCrosstabBucket) rowGroups[i].getBucket()).setOrder(rowGroupOrders[i]);
		}
		
		JRCrosstabColumnGroup[] colGroups = crosstab.getColumnGroups();
		for (int i = 0; i < colGroups.length; i++)
		{
			((JRDesignCrosstabBucket) colGroups[i].getBucket()).setOrder(colGroupOrders[i]);
		}
		
		crosstab.getPropertiesMap().setProperty(JRFillCrosstab.PROPERTY_ORDER_BY_COLUMN, orderByColumnProp);
	}

	@Override
	public String toString()
	{
		StringBuilder string = new StringBuilder();
		string.append("{").append(dataPreSorted).append(",[");
		for (int i = 0; i < rowGroupOrders.length; i++)
		{
			string.append(rowGroupOrders[i].getName()).append(",");
		}
		string.append("],[");
		for (int i = 0; i < colGroupOrders.length; i++)
		{
			string.append(colGroupOrders[i].getName()).append(",");
		}
		string.append("],").append(orderByColumnProp).append("}");
		return string.toString();
	}
	
	
	
}

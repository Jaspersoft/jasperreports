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
package net.sf.jasperreports.charts.util;

import java.util.Map;

import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRPrintHyperlink;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class TimeSeriesChartHyperlinkProvider implements ChartHyperlinkProvider
{
	
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private Map<Comparable<?>, Map<RegularTimePeriod, JRPrintHyperlink>> itemHyperlinks;
	
	public TimeSeriesChartHyperlinkProvider(Map<Comparable<?>, Map<RegularTimePeriod, JRPrintHyperlink>> itemHyperlinks)
	{
		this.itemHyperlinks = itemHyperlinks;
	}


	@Override
	public JRPrintHyperlink getEntityHyperlink(ChartEntity entity)
	{
		JRPrintHyperlink printHyperlink = null;
		if (hasHyperlinks() && entity instanceof XYItemEntity)
		{
			XYItemEntity itemEntity = (XYItemEntity) entity;
			TimeSeriesCollection dataset = (TimeSeriesCollection) itemEntity.getDataset();
			TimeSeries series = dataset.getSeries(itemEntity.getSeriesIndex());
			Map<RegularTimePeriod, JRPrintHyperlink> serieHyperlinks = itemHyperlinks.get(series.getKey());
			if (serieHyperlinks != null)
			{
				RegularTimePeriod timePeriod = series.getTimePeriod(itemEntity.getItem());
				printHyperlink = serieHyperlinks.get(timePeriod);
			}
		}
		return printHyperlink;
	}

	@Override
	public boolean hasHyperlinks()
	{
		return itemHyperlinks != null && itemHyperlinks.size() > 0;
	}
}

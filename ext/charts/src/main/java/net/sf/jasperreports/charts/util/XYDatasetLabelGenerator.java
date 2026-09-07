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
package net.sf.jasperreports.charts.util;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;

import net.sf.jasperreports.engine.JRConstants;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class XYDatasetLabelGenerator extends StandardXYItemLabelGenerator 
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private Map<Comparable<?>, Map<Number,String>> labelsMap;
	
	public XYDatasetLabelGenerator(Map<Comparable<?>, Map<Number,String>> labelsMap)
	{
		this(labelsMap, Locale.getDefault());
	}
	
	public XYDatasetLabelGenerator(Map<Comparable<?>, Map<Number,String>> labelsMap, Locale locale)
	{
		super(DEFAULT_ITEM_LABEL_FORMAT,
				NumberFormat.getInstance(locale),
				NumberFormat.getInstance(locale));
		
		this.labelsMap = labelsMap;
	}
	
	@Override
	public String generateLabel(XYDataset dataset, int series, int item)
	{
		Comparable<?> seriesName = dataset.getSeriesKey(series);
		Map<Number,String> labels = labelsMap.get(seriesName);
		if (labels != null)
		{
			return labels.get(((XYSeriesCollection)dataset).getX(series, item));
		}
		return super.generateLabel( dataset, series, item );
	}
}
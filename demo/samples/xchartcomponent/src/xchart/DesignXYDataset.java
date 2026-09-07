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
package xchart;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSetter;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.design.JRDesignElementDataset;

/**
 * 
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class DesignXYDataset extends JRDesignElementDataset implements XYDataset
{
	
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private List<XYSeries> xySeriesList;

	@Override
	public void collectExpressions(JRExpressionCollector collector)
	{
		XYChartCompiler.collectExpressions(this, collector);
	}
	
	public void addXYSeries(XYSeries series)
	{
		if (xySeriesList == null)
		{
			xySeriesList = new ArrayList<XYSeries>();
		}
		xySeriesList.add(series);
	}
	
	@Override
	public XYSeries[] getSeries()
	{
		if (xySeriesList != null)
		{
			return xySeriesList.toArray(new XYSeries[xySeriesList.size()]);
		}
		return null;
	}

	@JsonSetter
	private void setSeries(List<XYSeries> series)
	{
		if (series != null)
		{
			for (XYSeries s : series)
			{
				addXYSeries(s);
			}
		}
	}
}

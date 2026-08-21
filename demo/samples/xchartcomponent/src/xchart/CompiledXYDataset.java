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

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.base.JRBaseElementDataset;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;

/**
 * 
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class CompiledXYDataset extends JRBaseElementDataset implements XYDataset
{
	
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private final XYSeries[] xySeries;
	
	public CompiledXYDataset(XYDataset dataset, JRBaseObjectFactory factory)
	{
		super(dataset, factory);
		
		XYSeries[] seriesArray = dataset.getSeries();
		if (seriesArray != null && seriesArray.length > 0)
		{
			xySeries = new DesignXYSeries[seriesArray.length];
			for (int i = 0; i < seriesArray.length; i++)
			{
				xySeries[i] = new DesignXYSeries(seriesArray[i], factory);
			}
		}
		else
		{
			xySeries = null;
		}
	}

	@Override
	public void collectExpressions(JRExpressionCollector collector)
	{
		XYChartCompiler.collectExpressions(this, collector);
	}

	@Override
	public XYSeries[] getSeries()
	{
		return xySeries;
	}
}

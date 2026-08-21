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
package net.sf.jasperreports.charts.base;

import net.sf.jasperreports.charts.ChartsExpressionCollector;
import net.sf.jasperreports.charts.JRChartDataset;
import net.sf.jasperreports.charts.JRXyDataset;
import net.sf.jasperreports.charts.JRXySeries;
import net.sf.jasperreports.charts.design.ChartsVerifier;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.util.JRCloneUtils;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRBaseXyDataset extends JRBaseChartDataset implements JRXyDataset
{


	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	protected JRXySeries[] xySeries;

	
	/**
	 *
	 */
	public JRBaseXyDataset(JRChartDataset dataset)
	{
		super(dataset);
	}
	
	
	/**
	 *
	 */
	public JRBaseXyDataset(JRXyDataset dataset, ChartsBaseObjectFactory factory)
	{
		super(dataset, factory);

		/*   */
		JRXySeries[] srcXySeries = dataset.getSeries();
		if (srcXySeries != null && srcXySeries.length > 0)
		{
			xySeries = new JRXySeries[srcXySeries.length];
			for (int i = 0; i < xySeries.length; i++)
			{
				xySeries[i] = factory.getXySeries(srcXySeries[i]);
			}
		}

	}

	
	@Override
	public JRXySeries[] getSeries()
	{
		return xySeries;
	}


	/* (non-Javadoc)
	 * @see net.sf.jasperreports.engine.JRChartDataset#getDatasetType()
	 */
	@Override
	public byte getDatasetType() {
		return JRChartDataset.XY_DATASET;
	}

	
	@Override
	public void collectExpressions(JRExpressionCollector collector)
	{
		collector.collect(this);
	}


	@Override
	public void collectExpressions(ChartsExpressionCollector collector)
	{
		collector.collect(this);
	}


	@Override
	public void validate(ChartsVerifier verifier)
	{
		verifier.verify(this);
	}

	@Override
	public Object clone() 
	{
		JRBaseXyDataset clone = (JRBaseXyDataset)super.clone();
		clone.xySeries = JRCloneUtils.cloneArray(xySeries);
		return clone;
	}

}

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
import net.sf.jasperreports.charts.JRTimePeriodDataset;
import net.sf.jasperreports.charts.JRTimePeriodSeries;
import net.sf.jasperreports.charts.design.ChartsVerifier;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.util.JRCloneUtils;

/**
 * @author Flavius Sana (flavius_sana@users.sourceforge.net)
 */
public class JRBaseTimePeriodDataset extends JRBaseChartDataset implements JRTimePeriodDataset {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private JRTimePeriodSeries[] timePeriodSeries;
	
	protected JRBaseTimePeriodDataset( JRTimePeriodDataset dataset ){
		super( dataset );
	}
	
	public JRBaseTimePeriodDataset( JRTimePeriodDataset dataset, ChartsBaseObjectFactory factory ){
		super( dataset, factory );
		
		JRTimePeriodSeries[] srcTimePeriodSeries = dataset.getSeries();
		
		if ( srcTimePeriodSeries != null && srcTimePeriodSeries.length > 0 ){
			timePeriodSeries = new JRTimePeriodSeries[srcTimePeriodSeries.length];
			for ( int i = 0; i < timePeriodSeries.length; i++ ){
				timePeriodSeries[i] = factory.getTimePeriodSeries( srcTimePeriodSeries[i] );
			}
		}
	}
	
	@Override
	public JRTimePeriodSeries[] getSeries(){
		return timePeriodSeries;
	}

	@Override
	public byte getDatasetType() {
		return JRChartDataset.TIMEPERIOD_DATASET;
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
		JRBaseTimePeriodDataset clone = (JRBaseTimePeriodDataset)super.clone();
		clone.timePeriodSeries = JRCloneUtils.cloneArray(timePeriodSeries);
		return clone;
	}
}

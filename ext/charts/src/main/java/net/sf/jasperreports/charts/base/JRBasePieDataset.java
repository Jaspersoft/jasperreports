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
import net.sf.jasperreports.charts.JRPieDataset;
import net.sf.jasperreports.charts.JRPieSeries;
import net.sf.jasperreports.charts.design.ChartsVerifier;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRHyperlink;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;
import net.sf.jasperreports.engine.util.JRCloneUtils;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRBasePieDataset extends JRBaseChartDataset implements JRPieDataset, JRChangeEventsSupport
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public static final String PROPERTY_MIN_PERCENTAGE = "minPercentage";
	public static final String PROPERTY_MAX_COUNT = "maxCount";

	private Float minPercentage;
	private Integer maxCount;
	
	protected JRPieSeries[] pieSeries;

	protected JRExpression otherKeyExpression;
	protected JRExpression otherLabelExpression;
	private JRHyperlink otherSectionHyperlink;

	
	/**
	 *
	 */
	public JRBasePieDataset(JRChartDataset dataset)
	{
		super(dataset);
	}
	
	
	/**
	 *
	 */
	public JRBasePieDataset(JRPieDataset dataset, ChartsBaseObjectFactory factory)
	{
		super(dataset, factory);

		JRBaseObjectFactory parentFactory = factory.getParent();

		minPercentage = dataset.getMinPercentage();
		maxCount = dataset.getMaxCount();
		
		/*   */
		JRPieSeries[] srcPieSeries = dataset.getSeries();
		if (srcPieSeries != null && srcPieSeries.length > 0)
		{
			pieSeries = new JRPieSeries[srcPieSeries.length];
			for (int i = 0; i < pieSeries.length; i++)
			{
				pieSeries[i] = factory.getPieSeries(srcPieSeries[i]);
			}
		}

		otherKeyExpression = parentFactory.getExpression(dataset.getOtherKeyExpression());
		otherLabelExpression = parentFactory.getExpression(dataset.getOtherLabelExpression());
		otherSectionHyperlink = parentFactory.getHyperlink(dataset.getOtherSectionHyperlink());
	}

	
	@Override
	public Float getMinPercentage()
	{
		return minPercentage;
	}
	
	@Override
	public void setMinPercentage(Float minPercentage)
	{
		Object old = this.minPercentage;
		this.minPercentage = minPercentage;
		getEventSupport().firePropertyChange(PROPERTY_MIN_PERCENTAGE, old, this.minPercentage);
	}

	@Override
	public Integer getMaxCount()
	{
		return maxCount;
	}
	
	@Override
	public void setMaxCount(Integer maxCount)
	{
		Object old = this.maxCount;
		this.maxCount = maxCount;
		getEventSupport().firePropertyChange(PROPERTY_MAX_COUNT, old, this.maxCount);
	}

	@Override
	public JRPieSeries[] getSeries()
	{
		return pieSeries;
	}

	@Override
	public JRExpression getOtherKeyExpression()
	{
		return otherKeyExpression;
	}


	@Override
	public JRExpression getOtherLabelExpression()
	{
		return otherLabelExpression;
	}


	@Override
	public JRHyperlink getOtherSectionHyperlink()
	{
		return otherSectionHyperlink;
	}


	@Override
	public byte getDatasetType() {
		return JRChartDataset.PIE_DATASET;
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
		JRBasePieDataset clone = (JRBasePieDataset)super.clone();
		clone.pieSeries = JRCloneUtils.cloneArray(pieSeries);
		clone.otherKeyExpression = JRCloneUtils.nullSafeClone(otherKeyExpression);
		clone.otherLabelExpression = JRCloneUtils.nullSafeClone(otherLabelExpression);
		clone.otherSectionHyperlink = JRCloneUtils.nullSafeClone(otherSectionHyperlink);
		clone.eventSupport = null;
		return clone;
	}

	private transient JRPropertyChangeSupport eventSupport;
	
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
}

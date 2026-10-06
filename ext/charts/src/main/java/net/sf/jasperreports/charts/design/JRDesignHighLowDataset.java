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
package net.sf.jasperreports.charts.design;

import com.fasterxml.jackson.annotation.JsonCreator;

import net.sf.jasperreports.charts.ChartsExpressionCollector;
import net.sf.jasperreports.charts.JRChartDataset;
import net.sf.jasperreports.charts.JRHighLowDataset;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRHyperlink;
import net.sf.jasperreports.engine.util.JRCloneUtils;


/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 */
public class JRDesignHighLowDataset extends JRDesignChartDataset implements JRHighLowDataset //FIXMECHART why is this different?
{

	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_CLOSE_EXPRESSION = "closeExpression";
	
	public static final String PROPERTY_DATE_EXPRESSION = "dateExpression";
	
	public static final String PROPERTY_HIGH_EXPRESSION = "highExpression";
	
	public static final String PROPERTY_ITEM_HYPERLINK = "itemHyperlink";
	
	public static final String PROPERTY_LOW_EXPRESSION = "lowExpression";
	
	public static final String PROPERTY_OPEN_EXPRESSION = "openExpression";
	
	public static final String PROPERTY_SERIES_EXPRESSION = "seriesExpression";
	
	public static final String PROPERTY_VOLUME_EXPRESSION = "volumeExpression";

	protected JRExpression seriesExpression;
	protected JRExpression dateExpression;
	protected JRExpression highExpression;
	protected JRExpression lowExpression;
	protected JRExpression openExpression;
	protected JRExpression closeExpression;
	protected JRExpression volumeExpression;
	private JRHyperlink itemHyperlink;


	@JsonCreator
	private JRDesignHighLowDataset()
	{
		this(null);
	}


	/**
	 *
	 */
	public JRDesignHighLowDataset(JRChartDataset dataset)
	{
		super(dataset);
	}


	@Override
	public JRExpression getSeriesExpression()
	{
		return seriesExpression;
	}


	public void setSeriesExpression(JRExpression seriesExpression)
	{
		Object old = this.seriesExpression;
		this.seriesExpression = seriesExpression;
		getEventSupport().firePropertyChange(PROPERTY_SERIES_EXPRESSION, old, this.seriesExpression);
	}

	@Override
	public JRExpression getDateExpression()
	{
		return dateExpression;
	}


	public void setDateExpression(JRExpression dateExpression)
	{
		Object old = this.dateExpression;
		this.dateExpression = dateExpression;
		getEventSupport().firePropertyChange(PROPERTY_DATE_EXPRESSION, old, this.dateExpression);
	}


	@Override
	public JRExpression getHighExpression()
	{
		return highExpression;
	}


	public void setHighExpression(JRExpression highExpression)
	{
		Object old = this.highExpression;
		this.highExpression = highExpression;
		getEventSupport().firePropertyChange(PROPERTY_HIGH_EXPRESSION, old, this.highExpression);
	}


	@Override
	public JRExpression getLowExpression()
	{
		return lowExpression;
	}


	public void setLowExpression(JRExpression lowExpression)
	{
		Object old = this.lowExpression;
		this.lowExpression = lowExpression;
		getEventSupport().firePropertyChange(PROPERTY_LOW_EXPRESSION, old, this.lowExpression);
	}


	@Override
	public JRExpression getOpenExpression()
	{
		return openExpression;
	}


	public void setOpenExpression(JRExpression openExpression)
	{
		Object old = this.openExpression;
		this.openExpression = openExpression;
		getEventSupport().firePropertyChange(PROPERTY_OPEN_EXPRESSION, old, this.openExpression);
	}


	@Override
	public JRExpression getCloseExpression()
	{
		return closeExpression;
	}


	public void setCloseExpression(JRExpression closeExpression)
	{
		Object old = this.closeExpression;
		this.closeExpression = closeExpression;
		getEventSupport().firePropertyChange(PROPERTY_CLOSE_EXPRESSION, old, this.closeExpression);
	}


	@Override
	public JRExpression getVolumeExpression()
	{
		return volumeExpression;
	}


	public void setVolumeExpression(JRExpression volumeExpression)
	{
		Object old = this.volumeExpression;
		this.volumeExpression = volumeExpression;
		getEventSupport().firePropertyChange(PROPERTY_VOLUME_EXPRESSION, old, this.volumeExpression);
	}

	@Override
	public byte getDatasetType() {
		return JRChartDataset.HIGHLOW_DATASET;
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
	public JRHyperlink getItemHyperlink()
	{
		return itemHyperlink;
	}


	/**
	 * Sets the hyperlink specification for chart items.
	 * 
	 * @param itemHyperlink the hyperlink specification
	 * @see #getItemHyperlink()
	 */
	public void setItemHyperlink(JRHyperlink itemHyperlink)
	{
		Object old = this.itemHyperlink;
		this.itemHyperlink = itemHyperlink;
		getEventSupport().firePropertyChange(PROPERTY_ITEM_HYPERLINK, old, this.itemHyperlink);
	}


	@Override
	public void validate(ChartsVerifier verifier)
	{
		verifier.verify(this);
	}


	@Override
	public Object clone() 
	{
		JRDesignHighLowDataset clone = (JRDesignHighLowDataset)super.clone();
		clone.seriesExpression = JRCloneUtils.nullSafeClone(seriesExpression);
		clone.dateExpression = JRCloneUtils.nullSafeClone(dateExpression);
		clone.highExpression = JRCloneUtils.nullSafeClone(highExpression);
		clone.lowExpression = JRCloneUtils.nullSafeClone(lowExpression);
		clone.openExpression = JRCloneUtils.nullSafeClone(openExpression);
		clone.closeExpression = JRCloneUtils.nullSafeClone(closeExpression);
		clone.volumeExpression = JRCloneUtils.nullSafeClone(volumeExpression);
		clone.itemHyperlink = JRCloneUtils.nullSafeClone(itemHyperlink);
		return clone;
	}
}


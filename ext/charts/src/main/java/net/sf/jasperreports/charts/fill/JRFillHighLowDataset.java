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
package net.sf.jasperreports.charts.fill;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jfree.data.general.Dataset;
import org.jfree.data.xy.DefaultHighLowDataset;

import net.sf.jasperreports.charts.ChartsExpressionCollector;
import net.sf.jasperreports.charts.JRChartDataset;
import net.sf.jasperreports.charts.JRHighLowDataset;
import net.sf.jasperreports.charts.design.ChartsVerifier;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRHyperlink;
import net.sf.jasperreports.engine.JRHyperlinkHelper;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.fill.JRCalculator;
import net.sf.jasperreports.engine.fill.JRExpressionEvalException;
import net.sf.jasperreports.engine.fill.JRFillHyperlinkHelper;


/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 */
public class JRFillHighLowDataset extends JRFillChartDataset implements JRHighLowDataset
{
	private static final String EXCEPTION_MESSAGE_KEY_SERIES_NULL_CLOSE_VALUE = "charts.high.low.dataset.series.null.close.value";
	private static final String EXCEPTION_MESSAGE_KEY_SERIES_NULL_DATE_VALUE = "charts.high.low.dataset.series.null.date.value";
	private static final String EXCEPTION_MESSAGE_KEY_SERIES_NULL_HIGH_VALUE = "charts.high.low.dataset.series.null.high.value";
	private static final String EXCEPTION_MESSAGE_KEY_SERIES_NULL_LOW_VALUE = "charts.high.low.dataset.series.null.low.value";
	private static final String EXCEPTION_MESSAGE_KEY_SERIES_NULL_OPEN_VALUE = "charts.high.low.dataset.series.null.open.value";
	private static final String EXCEPTION_MESSAGE_KEY_SERIES_NULL_VOLUME_VALUE = "charts.high.low.dataset.series.null.volume.value";

	/**
	 *
	 */
	private String series;
	private List<HighLowElement> elements = new ArrayList<>();
	private Date date;
	private Number high;
	private Number low;
	private Number open;
	private Number close;
	private Number volume;
	
	private JRPrintHyperlink itemHyperlink;
	private List<JRPrintHyperlink> itemHyperlinks;

	
	/**
	 *
	 */
	public JRFillHighLowDataset(JRHighLowDataset dataset, ChartsFillObjectFactory factory)
	{
		super(dataset, factory.getParent());
	}


	@Override
	protected void customInitialize()
	{
		elements = new ArrayList<>();
		itemHyperlinks = new ArrayList<>();
	}


	@Override
	protected void customEvaluate(JRCalculator calculator) throws JRExpressionEvalException
	{
		series = (String) calculator.evaluate(getSeriesExpression());
		date = (Date) calculator.evaluate(getDateExpression());
		high = (Number) calculator.evaluate(getHighExpression());
		low = (Number) calculator.evaluate(getLowExpression());
		open = (Number) calculator.evaluate(getOpenExpression());
		close = (Number) calculator.evaluate(getCloseExpression());
		volume = (Number) calculator.evaluate(getVolumeExpression());
		
		if (hasItemHyperlink())
		{
			evaluateSectionHyperlink(calculator);
		}
	}


	protected void evaluateSectionHyperlink(JRCalculator calculator) throws JRExpressionEvalException
	{
		try
		{
			itemHyperlink = JRFillHyperlinkHelper.evaluateHyperlink(getItemHyperlink(), calculator, JRExpression.EVALUATION_DEFAULT);
		}
		catch (JRExpressionEvalException e)
		{
			throw e;
		}
		catch (JRException e)
		{
			throw new JRRuntimeException(e);
		}
	}


	@Override
	protected void customIncrement()
	{
		elements.add(new HighLowElement(date, high, low, open, close, volume));
		
		if (hasItemHyperlink())
		{
			itemHyperlinks.add(itemHyperlink);
		}
	}


	@Override
	public Dataset getCustomDataset()
	{
		int size = elements.size();
		if (size > 0)
		{
			Date[] dateArray = new Date[size];
			double[] highArray = new double[size];
			double[] lowArray = new double[size];
			double[] openArray = new double[size];
			double[] closeArray = new double[size];
			double[] volumeArray = new double[size];

			for (int i = 0; i < elements.size(); i++) {
				HighLowElement bean = elements.get(i);
				dateArray[i] = new Date(bean.getDate().getTime());
				highArray[i] = bean.getHigh().doubleValue();
				lowArray[i] = bean.getLow().doubleValue();
				openArray[i] = bean.getOpen().doubleValue();
				closeArray[i] = bean.getClose().doubleValue();
				volumeArray[i] = bean.getVolume().doubleValue();
			}

			return new DefaultHighLowDataset(series, dateArray, highArray, lowArray, openArray, closeArray, volumeArray);
		}
		
		return null;
	}


	@Override
	public Object getLabelGenerator()
	{
		return null;
	}


	@Override
	public JRExpression getSeriesExpression()
	{
		return ((JRHighLowDataset)parent).getSeriesExpression();
	}


	@Override
	public JRExpression getDateExpression()
	{
		return ((JRHighLowDataset)parent).getDateExpression();
	}


	@Override
	public JRExpression getHighExpression()
	{
		return ((JRHighLowDataset)parent).getHighExpression();
	}


	@Override
	public JRExpression getLowExpression()
	{
		return ((JRHighLowDataset)parent).getLowExpression();
	}


	@Override
	public JRExpression getOpenExpression()
	{
		return ((JRHighLowDataset)parent).getOpenExpression();
	}


	@Override
	public JRExpression getCloseExpression()
	{
		return ((JRHighLowDataset)parent).getCloseExpression();
	}


	@Override
	public JRExpression getVolumeExpression()
	{
		return ((JRHighLowDataset)parent).getVolumeExpression();
	}

	/**
	 *
	 */
	private static class HighLowElement
	{
		Date date;
		Number high;
		Number low;
		Number open;
		Number close;
		Number volume;


		public HighLowElement(
			Date date,
			Number high,
			Number low,
			Number open,
			Number close,
			Number volume
			)
		{
			if (date == null)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_SERIES_NULL_DATE_VALUE,
						(Object[])null);
			}
			this.date = date;

			if (high == null)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_SERIES_NULL_HIGH_VALUE,
						(Object[])null);
			}
			this.high = high;
			
			if (low == null)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_SERIES_NULL_LOW_VALUE,
						(Object[])null);
			}
			this.low = low;
			
			if (open == null)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_SERIES_NULL_OPEN_VALUE,
						(Object[])null);
			}
			this.open = open;
			
			if (close == null)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_SERIES_NULL_CLOSE_VALUE,
						(Object[])null);
			}
			this.close = close;
			
			if (volume == null)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_SERIES_NULL_VOLUME_VALUE,
						(Object[])null);
			}
			this.volume = volume;
		}


		public Date getDate()
		{
			return date;
		}

		public Number getHigh()
		{
			return high;
		}

		public Number getLow()
		{
			return low;
		}

		public Number getOpen()
		{
			return open;
		}

		public Number getClose()
		{
			return close;
		}

		public Number getVolume()
		{
			return volume;
		}
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
		return ((JRHighLowDataset) parent).getItemHyperlink();
	}


	public boolean hasItemHyperlink()
	{
		return !JRHyperlinkHelper.isEmpty(getItemHyperlink()); 
	}

	
	public List<JRPrintHyperlink> getItemHyperlinks()
	{
		return itemHyperlinks;
	}


	@Override
	public void validate(ChartsVerifier verifier)
	{
		verifier.verify(this);
	}

}

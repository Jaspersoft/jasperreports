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

import java.awt.Color;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import net.sf.jasperreports.charts.JRCategoryAxisFormat;
import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.charts.JRChartPlot;
import net.sf.jasperreports.charts.type.PlotOrientationEnum;
import net.sf.jasperreports.charts.util.ChartsStyleResolver;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;
import net.sf.jasperreports.engine.util.JRCloneUtils;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class JRBaseChartPlot implements JRChartPlot, Serializable, JRChangeEventsSupport
{


	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_BACKCOLOR = "backcolor";
	
	public static final String PROPERTY_BACKGROUND_ALPHA = "backgroundAlpha";
	
	public static final String PROPERTY_FOREGROUND_ALPHA = "foregroundAlpha";
	
	public static final String PROPERTY_LABEL_ROTATION = "labelRotation";
	
	public static final String PROPERTY_ORIENTATION = "orientation";
	
	public static final String PROPERTY_SERIES_COLORS = "seriesColors";
	
	protected JRChart chart;
	protected Color backcolor;
	protected PlotOrientationEnum orientation;
	protected Float backgroundAlpha;
	protected Float foregroundAlpha;
	protected Double labelRotation;
	protected SortedSet<JRSeriesColor>  seriesColors;


	/**
	 *
	 */
	@SuppressWarnings("deprecation")
	protected JRBaseChartPlot(JRChartPlot plot, JRChart chart)
	{
		this.chart = chart;

		if (plot != null) 
		{
			backcolor = plot.getOwnBackcolor();
			orientation = plot.getOrientation();
			backgroundAlpha = plot.getBackgroundAlpha();
			foregroundAlpha = plot.getForegroundAlpha();
			labelRotation = plot.getLabelRotation();
			seriesColors = new TreeSet<>(plot.getSeriesColors());
		}
		else
		{
			seriesColors = new TreeSet<>();
		}
	}
	
	
	@JsonIgnore
	private void setType(String type)
	{
		throw new UnsupportedOperationException();
	}


	/**
	 *
	 */
	@SuppressWarnings("deprecation")
	protected JRBaseChartPlot(JRChartPlot plot, ChartsBaseObjectFactory factory)
	{
		JRBaseObjectFactory parentFactory = factory.getParent();
		parentFactory.put(plot, this);

		chart = (JRChart)parentFactory.getVisitResult(plot.getChart());

		backcolor = plot.getOwnBackcolor();
		orientation = plot.getOrientation();
		backgroundAlpha = plot.getBackgroundAlpha();
		foregroundAlpha = plot.getForegroundAlpha();
		labelRotation = plot.getLabelRotation();
		seriesColors = new TreeSet<>(plot.getSeriesColors());
	}

	
	/**
	 *
	 */
	protected ChartsStyleResolver getStyleResolver()
	{
		return getChart().getChartsStyleResolver();
	}

	
	@Override
	public JRChart getChart()
	{
		return chart;
	}

	@Override
	public Color getBackcolor()
	{
		return getStyleResolver().getBackcolor(this);
	}

	@Override
	public Color getOwnBackcolor()
	{
		return this.backcolor;
	}

	@Override
	public void setBackcolor(Color backcolor)
	{
		Object old = this.backcolor;
		this.backcolor = backcolor;
		getEventSupport().firePropertyChange(PROPERTY_BACKCOLOR, old, this.backcolor);
	}

	@Override
	public PlotOrientationEnum getOrientation()
	{
		return orientation;
	}

	@Override
	public void setOrientation(PlotOrientationEnum orientation)
	{
		Object old = this.orientation;
		this.orientation = orientation;
		getEventSupport().firePropertyChange(PROPERTY_ORIENTATION, old, this.orientation);
	}

	@Override
	public Float getBackgroundAlpha()
	{
		return backgroundAlpha;
	}

	@Override
	public void setBackgroundAlpha(Float backgroundAlpha)
	{
		Float old = this.backgroundAlpha;
		this.backgroundAlpha = backgroundAlpha;
		getEventSupport().firePropertyChange(PROPERTY_BACKGROUND_ALPHA, old, this.backgroundAlpha);
	}

	@Override
	public Float getForegroundAlpha()
	{
		return foregroundAlpha;
	}

	@Override
	public void setForegroundAlpha(Float foregroundAlpha)
	{
		Float old = this.foregroundAlpha;
		this.foregroundAlpha = foregroundAlpha;
		getEventSupport().firePropertyChange(PROPERTY_FOREGROUND_ALPHA, old, this.foregroundAlpha);
	}

	/**
	 * Gets the angle in degrees to rotate the data axis labels.  The range is -360 to 360.  A positive value angles
	 * the label so it reads downwards wile a negative value angles the label so it reads upwards.  Only charts that
	 * use a category based axis (such as line or bar charts) support label rotation.
	 * @deprecated Replaced by {@link JRCategoryAxisFormat#getCategoryAxisTickLabelRotation()}.
	 */
	@Override
	public Double getLabelRotation()
	{
		return labelRotation;
	}
	
	/**
	 * Sets the angle in degrees to rotate the data axis labels.  The range is -360 to 360.  A positive value angles
	 * the label so it reads downwards wile a negative value angles the label so it reads upwards.  Only charts that
	 * use a category based axis (such as line or bar charts) support label rotation.
	 * @deprecated Replaced by {@link JRCategoryAxisFormat#setCategoryAxisTickLabelRotation(Double)}.
	 */
	@Override
	public void setLabelRotation(Double labelRotation)
	{
		Double old = this.labelRotation;
		this.labelRotation = labelRotation;
		getEventSupport().firePropertyChange(PROPERTY_LABEL_ROTATION, old, this.labelRotation);
	}
	
	
	/**
	 * Returns a list of all the defined series colors.  Every entry in the list is of type JRChartPlot.JRSeriesColor.
	 * If there are no defined series colors this method will return an empty list, not null. 
	 */
	@Override
	public SortedSet<JRSeriesColor> getSeriesColors()
	{
		return seriesColors;
	}
	
	/**
	 * Removes all defined series colors.
	 */
	@Override
	public void clearSeriesColors()
	{
		setSeriesColors(null);
	}
	
	/**
	 * Adds the specified series color to the plot.
	 */
	@Override
	public void addSeriesColor(JRSeriesColor seriesColor)
	{
		seriesColors.add(seriesColor);
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_SERIES_COLORS, 
				seriesColor, seriesColors.size() - 1);
	}
	
	@Override
	public void setSeriesColors(Collection<JRSeriesColor> colors)
	{
		Object old = new TreeSet<JRSeriesColor>(seriesColors);
		seriesColors.clear();
		if (colors != null)
		{
			seriesColors.addAll(colors);
		}
		getEventSupport().firePropertyChange(PROPERTY_SERIES_COLORS, old, seriesColors);
	}
	
	public static class JRBaseSeriesColor implements JRChartPlot.JRSeriesColor, Serializable, Comparable<JRBaseSeriesColor>
	{
		/**
		 *
		 */
		private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
		
		protected int seriesOrder = -1;
		protected Color color;
		
		@JsonCreator
		public JRBaseSeriesColor(
			@JsonProperty("order") int seriesOrder, 
			@JsonProperty("color") Color color
			)
		{
			this.seriesOrder = seriesOrder;
			this.color = color;
		}
		
		/**
		 * Returns the series number (0 based) that this color applies to.
		 */
		@Override
		public int getSeriesOrder()
		{
			return seriesOrder;
		}
		
		/**
		 * Returns the color to use for this series.
		 */
		@Override
		public Color getColor()
		{
			return color;
		}

		@Override
		public int compareTo(JRBaseSeriesColor obj) {
			if (obj == null)
			{
				throw new IllegalArgumentException();
			}
			
			return seriesOrder - obj.getSeriesOrder();
		}
		
		@Override
		public Object clone()
		{
			try
			{
				return super.clone();
			}
			catch (CloneNotSupportedException e)
			{
				throw new JRRuntimeException(e);
			}
		}
	}
	

	@Override
	public Object clone() 
	{
		JRBaseChartPlot clone = null;

		try
		{
			clone = (JRBaseChartPlot)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new JRRuntimeException(e);
		}
		
		if (seriesColors != null)
		{
			clone.seriesColors = new TreeSet<>();
			for (Iterator<JRSeriesColor> it = seriesColors.iterator(); it.hasNext();)
			{
				clone.seriesColors.add(JRCloneUtils.nullSafeClone(it.next()));
			}
		}
		
		return clone;
	}


	@Override
	public Object clone(JRChart parentChart) 
	{
		JRBaseChartPlot clone = (JRBaseChartPlot)this.clone();
		clone.chart = parentChart;
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

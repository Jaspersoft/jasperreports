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

import java.awt.Color;
import java.io.Serializable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.charts.JRDataRange;
import net.sf.jasperreports.charts.base.ChartsBaseObjectFactory;
import net.sf.jasperreports.charts.base.JRBaseDataRange;
import net.sf.jasperreports.engine.JRCloneable;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.util.JRCloneUtils;

/**
 * Defines a subsection of a meter chart.  This section has its own range,
 * a label, and can shade a section of the meter face with its own color.
 * Common usages are to show "critical", "warning" and "good" ranges.
 *
 * @author Barry Klawans (barry@users.sourceforge.net)
 */
public class JRMeterInterval implements JRCloneable, Serializable
{
	public static final double DEFAULT_TRANSPARENCY = 1.0;
	
	/**
	 * The range of this interval.  Must be inside the meter's range.
	 */
	protected JRDataRange dataRange;

	/**
	 * The label of this interval.  Only appears in the meter's legend.
	 */
	protected String label;

	/**
	 * Color to use to shade in this region on the meter's face.
	 */
	protected Color backgroundColor;

	/**
	 * Transparency of the interval's color.  1.0 is fully opaque, 0.0 is
	 * fully transparent.
	 */
	protected Double alpha;

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/**
	 * Construct an empty interval.
	 */
	public JRMeterInterval()
	{
	}

	/**
	 * Construct a new interval by copying an existing one.
	 *
	 * @param meterInterval the interval to copy
	 * @param factory factory object to register expressions with
	 */
	public JRMeterInterval(JRMeterInterval meterInterval, ChartsBaseObjectFactory factory)
	{
		dataRange = new JRBaseDataRange(meterInterval.getDataRange(), factory);
		label = meterInterval.getLabel();
		backgroundColor = meterInterval.getBackgroundColor();
		alpha = meterInterval.getAlpha();
	}

	/**
	 * Returns the range this interval is for.
	 *
	 * @return the range of this interval
	 */
	public JRDataRange getDataRange()
	{
		return dataRange;
	}

	/**
	 * Sets the range for this interval.  The range must be inside the
	 * range of the meter we are going to add the interval to.
	 *
	 * @param dataRange the range of this interval
	 */
	public void setDataRange(JRDataRange dataRange)
	{
		this.dataRange = dataRange;
	}

	/**
	 * The text describing this range.  This text only appears in the
	 * meter's legend.
	 *
	 * @return the text describing this range
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getLabel()
	{
		return label;
	}

	/**
	 * Sets the textual description of this range.  This text only appears
	 * in the meter's legend.
	 *
	 * @param label the textual description of this range
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	/**
	 * Returns the color used to shade this interval.
	 *
	 * @return the color used to shade this interval
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Color getBackgroundColor()
	{
		return backgroundColor;
	}

	/**
	 * Specifies the color to use to shade this interval.
	 *
	 * @param backgroundColor the color to use to shade this interval
	 */
	public void setBackgroundColor(Color backgroundColor)
	{
		this.backgroundColor = backgroundColor;
	}

	/**
	 * Returns the transparency of the interval color, with 0.0 being fully
	 * transparent and 1.0 being fully opaque.
	 *
	 * @return the transparency
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Double getAlpha()
	{
		return alpha;
	}

	/**
	 * Sets the transparency of the interval color, with 0.0 being fully
	 * transparent and 1.0 being fully opaque.
	 *
	 * @param alpha the transparency of the interval color
	 */
	public void setAlpha(Double alpha)
	{
		this.alpha = alpha;
	}

	@Override
	public Object clone() 
	{
		JRMeterInterval clone = null;
		
		try
		{
			clone = (JRMeterInterval)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new JRRuntimeException(e);
		}

		clone.dataRange = JRCloneUtils.nullSafeClone(dataRange);
		
		return clone;
	}
}

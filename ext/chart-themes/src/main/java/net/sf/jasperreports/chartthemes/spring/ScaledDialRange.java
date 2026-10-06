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
package net.sf.jasperreports.chartthemes.spring;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialScale;
import org.jfree.chart.plot.dial.StandardDialRange;

import net.sf.jasperreports.engine.JRConstants;

/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class ScaledDialRange extends StandardDialRange
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private float lineWidth = 2f;
	/**
	 * 
	 */
	public ScaledDialRange()
	{
		super();
	}

	/**
	 * @param lower
	 * @param upper
	 * @param paint
	 */
	public ScaledDialRange(double lower, double upper, Paint paint)
	{
		super(lower, upper, paint);
	}

	/**
	 * @param lower
	 * @param upper
	 * @param paint
	 * @param lineWidth
	 */
	public ScaledDialRange(double lower, double upper, Paint paint, float lineWidth)
	{
		super(lower, upper, paint);
		this.lineWidth = lineWidth;
	}

	/**
	 * Draws the range.
	 * 
	 * @param g2  the graphics target.
	 * @param plot  the plot.
	 * @param frame  the dial's reference frame (in Java2D space).
	 * @param view  the dial's view rectangle (in Java2D space).
	 */
	@Override
	public void draw(Graphics2D g2, DialPlot plot, Rectangle2D frame, 
			Rectangle2D view) {
		
		Rectangle2D arcRectInner = DialPlot.rectangleByRadius(frame, 
				this.getInnerRadius(), this.getInnerRadius());
		Rectangle2D arcRectOuter = DialPlot.rectangleByRadius(frame, 
				this.getOuterRadius(), this.getOuterRadius());
		
		DialScale scale = plot.getScale(this.getScaleIndex());
		if (scale == null) {
			throw new RuntimeException("No scale for scaleIndex = " 
					+ this.getScaleIndex());
		}
		double angleMin = scale.valueToAngle(this.getLowerBound());
		double angleMax = scale.valueToAngle(this.getUpperBound());

		Arc2D arcInner = new Arc2D.Double(arcRectInner, angleMin, 
				angleMax - angleMin, Arc2D.OPEN);
		Arc2D arcOuter = new Arc2D.Double(arcRectOuter, angleMax, 
				angleMin - angleMax, Arc2D.OPEN);
		
		g2.setPaint(this.getPaint());
		g2.setStroke(new BasicStroke(this.lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
		g2.draw(arcInner);
		g2.draw(arcOuter);
	}

	/**
	 * Tests this instance for equality with an arbitrary object.
	 * 
	 * @param obj  the object (<code>null</code> permitted).
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj.getClass().equals(this.getClass()))) {
			return false;
		}
		ScaledDialRange that = (ScaledDialRange) obj;
		if (this.lineWidth != that.lineWidth) {
			return false;
		}
		return super.equals(obj); 
	}

	/**
	 * Returns a hash code for this instance.
	 * 
	 * @return The hash code.
	 */
	@Override
	public int hashCode() {
		int temp = Float.floatToIntBits(this.lineWidth);
		return 37 * super.hashCode() + (temp ^ (temp >>> 32));		
		
	}
	
	/**
	 * Returns a clone of this instance.
	 * 
	 * @return A clone.
	 * 
	 * @throws CloneNotSupportedException if any of the attributes of this 
	 *     instance cannot be cloned.
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}

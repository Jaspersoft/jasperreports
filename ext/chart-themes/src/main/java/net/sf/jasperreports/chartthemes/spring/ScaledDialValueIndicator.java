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

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialValueIndicator;
import org.jfree.chart.text.TextUtils;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.Size2D;

import net.sf.jasperreports.engine.JRConstants;

/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class ScaledDialValueIndicator extends DialValueIndicator
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private int scale;
	/**
	 * Creates a new instance of <code>DialValueIndicator</code>.
	 */
	public ScaledDialValueIndicator() {
		this(0,1);
	}

	/**
	 * Creates a new instance of <code>DialValueIndicator</code>.
	 *
	 * @param datasetIndex  the dataset index.
	 */
	public ScaledDialValueIndicator(int datasetIndex) {
		this(datasetIndex, 1);
	}

	/**
	 * Creates a new instance of <code>DialValueIndicator</code>.
	 *
	 * @param datasetIndex  the dataset index.
	 * @param scale  the scale.
	 */
	public ScaledDialValueIndicator(int datasetIndex, int scale) {
		super(datasetIndex);
		setScale(scale);
	}

	/**
	 * Draws the background to the specified graphics device.  If the dial
	 * frame specifies a window, the clipping region will already have been
	 * set to this window before this method is called.
	 *
	 * @param g2  the graphics device (<code>null</code> not permitted).
	 * @param plot  the plot (ignored here).
	 * @param frame  the dial frame (ignored here).
	 * @param view  the view rectangle (<code>null</code> not permitted).
	 */
	@Override
	public void draw(Graphics2D g2, DialPlot plot, Rectangle2D frame,
			Rectangle2D view) {

		// work out the anchor point
		Rectangle2D f = DialPlot.rectangleByRadius(frame, getRadius(),
				this.getRadius());
		Arc2D arc = new Arc2D.Double(f, this.getAngle(), 0.0, Arc2D.OPEN);
		Point2D pt = arc.getStartPoint();

		// calculate the bounds of the template value
		FontMetrics fm = g2.getFontMetrics(this.getFont());
		String s = this.getNumberFormat().format(this.getTemplateValue());
		Rectangle2D tb = TextUtils.getTextBounds(s, g2, fm);

		// align this rectangle to the frameAnchor
		Rectangle2D bounds = RectangleAnchor.createRectangle(new Size2D(
				tb.getWidth(), tb.getHeight()), pt.getX(), pt.getY(),
				this.getFrameAnchor());

		// add the insets
		Rectangle2D fb = this.getInsets().createOutsetRectangle(bounds);

		// draw the background
		g2.setPaint(this.getBackgroundPaint());
		g2.fill(fb);

		// draw the border
		g2.setStroke(this.getOutlineStroke());
		g2.setPaint(this.getOutlinePaint());
		g2.draw(fb);


		// now find the text anchor point
		String valueStr = this.getNumberFormat().format(ChartThemesUtilities.getScaledValue(plot.getValue(this.getDatasetIndex()), scale));
		Point2D pt2 = this.getValueAnchor().getAnchorPoint(bounds);
		g2.setPaint(this.getPaint());
		g2.setFont(this.getFont());
		TextUtils.drawAlignedString(valueStr, g2, (float) pt2.getX(),
				(float) pt2.getY(), this.getTextAnchor());

	}

	/**
	 * @return the scale
	 */
	public int getScale()
	{
		return this.scale;
	}

	/**
	 * @param scale the scale to set
	 */
	public void setScale(int scale)
	{
		this.scale = scale;
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
		if (!(obj instanceof ScaledDialValueIndicator)) {
			return false;
		}
		ScaledDialValueIndicator that = (ScaledDialValueIndicator) obj;
		if (this.scale != that.scale) {
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
		return 37 * super.hashCode() + scale;
	}

	/**
	 * Returns a clone of this instance.
	 *
	 * @return The clone.
	 *
	 * @throws CloneNotSupportedException if some attribute of this instance
	 *     cannot be cloned.
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}

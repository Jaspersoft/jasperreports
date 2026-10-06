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
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.jfree.chart.HashUtils;
import org.jfree.chart.plot.dial.DialLayerChangeEvent;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialPointer.Pointer;
import org.jfree.chart.plot.dial.DialScale;
import org.jfree.chart.util.SerialUtils;

import net.sf.jasperreports.engine.JRConstants;

/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class ScaledDialPointer extends Pointer
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private Paint fillPaint;

	private int scale;

	/**
	 * Creates a new instance.
	 */
	public ScaledDialPointer() {
		this(0, 0.03, Color.BLACK, Color.BLACK, 1);
	}

	/**
	 * Creates a new instance.
	 */
	public ScaledDialPointer(int scale) {
		this(0, 0.03, Color.BLACK, Color.BLACK, scale);
	}

	/**
	 * Creates a new instance.
	 */
	public ScaledDialPointer(int scale, Paint fillPaint, Paint outlinePaint ) {
		this(0, 0.03, fillPaint, outlinePaint, scale);
	}

//	/**
//	 * Creates a new instance.
//	 *
//	 * @param datasetIndex  the dataset index.
//	 */
//	private ScaledDialPointer(int datasetIndex, double widthRadius, Paint gradientFillPaint, Paint outlinePaint)
//	{
//		this(datasetIndex, widthRadius,gradientFillPaint, outlinePaint, 1);
//	}


	/**
	 * Creates a new instance.
	 */
	public ScaledDialPointer(int scale, double widthRadius)
	{
		this(0, widthRadius, Color.BLACK, Color.BLACK, scale);
	}
	
	/**
	 * Creates a new instance.
	 *
	 * @param datasetIndex  the dataset index.
	 */
	public ScaledDialPointer(int datasetIndex, double widthRadius, Paint fillPaint, Paint outlinePaint, int scale)
	{
		super(datasetIndex);
		setWidthRadius(widthRadius);
		this.fillPaint = fillPaint;
		setOutlinePaint(outlinePaint);
		this.scale = scale;
	}

	/**
	 * Returns the fill paint.
	 *
	 * @return The paint (never <code>null</code>).
	 *
	 * @since 1.0.8
	 */
	@Override
	public Paint getFillPaint() {
		return this.fillPaint;
	}

	/**
	 * Sets the fill paint and sends a {@link DialLayerChangeEvent} to all
	 * registered listeners.
	 *
	 * @param paint  the paint (<code>null</code> not permitted).
	 *
	 * @see #getFillPaint()
	 *
	 * @since 1.0.8
	 */
	@Override
	public void setFillPaint(Paint paint) {
		if (paint == null) {
			throw new IllegalArgumentException("Null 'paint' argument.");
		}
		this.fillPaint = paint;
		notifyListeners(new DialLayerChangeEvent(this));
	}

	/**
	 * Draws the pointer.
	 *
	 * @param g2  the graphics target.
	 * @param plot  the plot.
	 * @param frame  the dial's reference frame.
	 * @param view  the dial's view.
	 */
	@Override
	public void draw(Graphics2D g2, DialPlot plot, Rectangle2D frame,
			Rectangle2D view) {

		g2.setStroke(new BasicStroke(1.0f));
		Rectangle2D lengthRect = DialPlot.rectangleByRadius(frame,
				this.getRadius(), this.getRadius());
		Rectangle2D widthRect = DialPlot.rectangleByRadius(frame,
				this.getWidthRadius(), this.getWidthRadius());
		double value = ChartThemesUtilities.getScaledValue(plot.getValue(this.getDatasetIndex()), scale);
		DialScale scale = plot.getScaleForDataset(this.getDatasetIndex());
		double angle = scale.valueToAngle(value);

		Arc2D arc1 = new Arc2D.Double(lengthRect, angle, 0, Arc2D.OPEN);
		Point2D pt1 = arc1.getEndPoint();
		Arc2D arc2 = new Arc2D.Double(widthRect, angle - 90.0, 180.0,
				Arc2D.OPEN);
		Point2D pt2 = arc2.getStartPoint();
		Point2D pt3 = arc2.getEndPoint();
		Arc2D arc3 = new Arc2D.Double(widthRect, angle - 180.0, 0.0,
				Arc2D.OPEN);
		Point2D pt4 = arc3.getStartPoint();

		GeneralPath gp = new GeneralPath();
		gp.moveTo((float) pt1.getX(), (float) pt1.getY());
		gp.lineTo((float) pt2.getX(), (float) pt2.getY());
		gp.lineTo((float) pt4.getX(), (float) pt4.getY());
		gp.lineTo((float) pt3.getX(), (float) pt3.getY());
		gp.closePath();
		g2.setPaint(this.fillPaint);
		g2.fill(gp);

		g2.setPaint(this.getOutlinePaint());
		Line2D line = new Line2D.Double(frame.getCenterX(),
				frame.getCenterY(), pt1.getX(), pt1.getY());
//		g2.draw(line);

		line.setLine(pt2, pt3);
		g2.draw(line);

		line.setLine(pt3, pt1);
		g2.draw(line);

		line.setLine(pt2, pt1);
		g2.draw(line);

		line.setLine(pt2, pt4);
		g2.draw(line);

		line.setLine(pt3, pt4);
		g2.draw(line);
	}

	/**
	 * Tests this pointer for equality with an arbitrary object.
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
		if (!(obj instanceof ScaledDialPointer)) {
			return false;
		}
		ScaledDialPointer that = (ScaledDialPointer) obj;

		if (!this.fillPaint.equals(that.fillPaint))
		{
			return false;
		}
		return super.equals(obj);
	}

	/**
	 * Returns a hash code for this instance.
	 *
	 * @return A hash code.
	 */
	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = HashUtils.hashCode(result, this.fillPaint);
		return result;
	}

	/**
	 * Provides serialization support.
	 *
	 * @param stream  the output stream.
	 *
	 * @throws IOException  if there is an I/O error.
	 */
	private void writeObject(ObjectOutputStream stream) throws IOException {
		stream.defaultWriteObject();
		SerialUtils.writePaint(this.fillPaint, stream);
	}

	/**
	 * Provides serialization support.
	 *
	 * @param stream  the input stream.
	 *
	 * @throws IOException  if there is an I/O error.
	 * @throws ClassNotFoundException  if there is a classpath problem.
	 */
	private void readObject(ObjectInputStream stream)
			throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		this.fillPaint = SerialUtils.readPaint(stream);
	}

}

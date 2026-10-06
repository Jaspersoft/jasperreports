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
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.chart.text.TextUtils;
import org.jfree.chart.ui.TextAnchor;

import net.sf.jasperreports.engine.JRConstants;

/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class ScaledDialScale extends StandardDialScale
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/**
	 * 
	 */
	public ScaledDialScale()
	{
		super();
	}

	/**
	 * @param lowerBound
	 * @param upperBound
	 * @param startAngle
	 * @param extent
	 * @param majorTickIncrement
	 * @param minorTickCount
	 */
	public ScaledDialScale(double lowerBound, double upperBound,
			double startAngle, double extent, double majorTickIncrement,
			int minorTickCount)
	{
		super(lowerBound, upperBound, startAngle, extent, majorTickIncrement,
				minorTickCount);
	}

	/**
	 * Draws the scale on the dial plot.
	 *
	 * @param g2  the graphics target (<code>null</code> not permitted).
	 * @param plot  the dial plot (<code>null</code> not permitted).
	 * @param frame  the reference frame that is used to construct the
	 *     geometry of the plot (<code>null</code> not permitted).
	 * @param view  the visible part of the plot (<code>null</code> not 
	 *     permitted).
	 */
	@Override
	public void draw(Graphics2D g2, DialPlot plot, Rectangle2D frame, 
			Rectangle2D view) {
		
		Rectangle2D arcRect = DialPlot.rectangleByRadius(frame, 
				this.getTickRadius(), this.getTickRadius());
		Rectangle2D arcRectMajor = DialPlot.rectangleByRadius(frame, 
				this.getTickRadius() - this.getMajorTickLength(), 
				this.getTickRadius() - this.getMajorTickLength());
		Rectangle2D arcRectMinor = arcRect;
		if (this.getMinorTickCount() > 0 && this.getMinorTickLength() > 0.0) {
			arcRectMinor = DialPlot.rectangleByRadius(frame, 
					this.getTickRadius() - this.getMinorTickLength(), 
					this.getTickRadius() - this.getMinorTickLength());
		}
		Rectangle2D arcRectForLabels = DialPlot.rectangleByRadius(frame, 
				this.getTickRadius() - this.getTickLabelOffset(), 
				this.getTickRadius() - this.getTickLabelOffset());
		
		boolean firstLabel = true;
		
		Arc2D arc = new Arc2D.Double();
		Line2D workingLine = new Line2D.Double();
		Stroke arcStroke = new BasicStroke(0.75f);
		
		for (double v = this.getLowerBound(); v <= this.getUpperBound(); 
				v += this.getMajorTickIncrement()) {
			arc.setArc(arcRect, this.getStartAngle(), valueToAngle(v) 
					- this.getStartAngle(), Arc2D.OPEN);
			g2.setPaint(this.getMajorTickPaint());
			g2.setStroke(arcStroke);
			g2.draw(arc);
			
			Point2D pt0 = arc.getEndPoint();
			arc.setArc(arcRectMajor, this.getStartAngle(), valueToAngle(v) 
					- this.getStartAngle(), Arc2D.OPEN);
			Point2D pt1 = arc.getEndPoint();
			g2.setPaint(this.getMajorTickPaint());
			g2.setStroke(this.getMajorTickStroke());
			workingLine.setLine(pt0, pt1);
			g2.draw(workingLine);
			arc.setArc(arcRectForLabels, this.getStartAngle(), valueToAngle(v) 
					- this.getStartAngle(), Arc2D.OPEN);
			Point2D pt2 = arc.getEndPoint();
			
			if (this.getTickLabelsVisible()) {
				if (!firstLabel || this.getFirstTickLabelVisible()) {
					g2.setFont(this.getTickLabelFont());
					TextUtils.drawAlignedString(
							this.getTickLabelFormatter().format(v), g2, 
							(float) pt2.getX(), (float) pt2.getY(), 
							TextAnchor.CENTER);
				}
			}
			firstLabel = false;
			
			// now do the minor tick marks
			if (this.getMinorTickCount() > 0 && this.getMinorTickLength() > 0.0) {
				double minorTickIncrement = this.getMajorTickIncrement() 
						/ (this.getMinorTickCount() + 1);
				for (int i = 0; i < this.getMinorTickCount(); i++) {
					double vv = v + ((i + 1) * minorTickIncrement);
					if (vv >= this.getUpperBound()) {
						break;
					}
					double angle = valueToAngle(vv);

					arc.setArc(arcRect, this.getStartAngle(), angle 
							- this.getStartAngle(), Arc2D.OPEN);
					pt0 = arc.getEndPoint();
					arc.setArc(arcRectMinor, this.getStartAngle(), angle 
							- this.getStartAngle(), Arc2D.OPEN);
					Point2D pt3 = arc.getEndPoint();
					g2.setStroke(this.getMinorTickStroke());
					g2.setPaint(this.getMinorTickPaint());
					workingLine.setLine(pt0, pt3);
					g2.draw(workingLine);
				}
			}
			
		}
	}
	
}

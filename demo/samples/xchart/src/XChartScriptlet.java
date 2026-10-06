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


import java.awt.Color;
import java.awt.image.BufferedImage;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.XYStyler;

import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;

/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class XChartScriptlet extends JRDefaultScriptlet 
{


	@Override
	public void afterReportInit() throws JRScriptletException 
	{
		try 
		{
		    XYChart xyChart = new XYChartBuilder()
		    		.width(515)
		    		.height(400)
		    		.title("Fruits Order")
		    		.xAxisTitle("Day of Week")
		    		.yAxisTitle("Quantity (t)")
		    		.build();

		    xyChart.addSeries("Apples", new double[] { 1, 3, 5}, new double[] {4, 10, 7});
		    xyChart.addSeries("Bananas", new double[] { 1, 2, 3, 4, 5}, new double[] {6, 8, 4, 4, 6});
		    xyChart.addSeries("Cherries", new double[] { 1, 3, 4, 5}, new double[] {2, 6, 1, 9});
		    XYStyler styler = xyChart.getStyler();
		    styler.setLegendPosition(Styler.LegendPosition.InsideNW);
		    styler.setAxisTitlesVisible(true);
		    styler.setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);
		    styler.setChartBackgroundColor(Color.WHITE);
		   
			BufferedImage bufferedImage = BitmapEncoder.getBufferedImage(xyChart);
			super.setVariableValue("ChartImage", bufferedImage);
		}
		catch(Exception e) 
		{
			throw new JRScriptletException(e);
		}
	}
}

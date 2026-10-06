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

/*
 * Contributors:
 * David Gilbert - david.gilbert@object-refinery.com
 */

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.util.Rotation;

import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;
import net.sf.jasperreports.charts.renderers.JCommonDrawableRendererImpl;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JFreeChartScriptlet extends JRDefaultScriptlet
{


	@Override
	public void afterReportInit() throws JRScriptletException
	{
		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue("Java", 43.2d);
		dataset.setValue("Visual Basic", 10.0d);
		dataset.setValue("C/C++", 17.5d);
		dataset.setValue("PHP", 32.5d);
		dataset.setValue("Perl", 1.0d);

		JFreeChart chart = 
			ChartFactory.createPieChart(
				"Pie Chart Demo 1",
				dataset,
				true,
				true,
				false
				);

		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setStartAngle(290);
		plot.setDirection(Rotation.CLOCKWISE);
		plot.setForegroundAlpha(0.5f);
		plot.setNoDataMessage("No data to display");

		/*   */
		this.setVariableValue("Chart", new JCommonDrawableRendererImpl(chart));
	}


}

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
package net.sf.jasperreports.charts.base;

import net.sf.jasperreports.charts.ChartCopyObjectFactory;
import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.charts.JRChartPlot;
import net.sf.jasperreports.charts.JRPie3DPlot;
import net.sf.jasperreports.engine.JRConstants;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @deprecated To be removed.
 */
public class JRBasePie3DPlot extends JRBasePiePlot implements JRPie3DPlot
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_DEPTH_FACTOR = "depthFactor";
		
	protected Double depthFactor;

	/**
	 *
	 */
	public JRBasePie3DPlot(JRChartPlot chartPlot, JRChart chart)
	{
		this(chartPlot, chart, ChartCopyBaseObjectFactory.instance());
	}

	protected JRBasePie3DPlot(JRChartPlot chartPlot, JRChart chart, ChartCopyObjectFactory copyObjectFactory)
	{
		super(chartPlot, chart);
	}

	
	/**
	 *
	 */
	public JRBasePie3DPlot(JRPie3DPlot pie3DPlot, ChartsBaseObjectFactory factory)
	{
		super(pie3DPlot, factory);
		
		depthFactor = pie3DPlot.getDepthFactor();
	}

	
	@Override
	public Double getDepthFactor()
	{
		return depthFactor;
	}
	
	/**
	 *
	 */
	public void setDepthFactor(Double depthFactor)
	{
		Double old = this.depthFactor;
		this.depthFactor = depthFactor;
		getEventSupport().firePropertyChange(PROPERTY_DEPTH_FACTOR, old, this.depthFactor);
	}

}

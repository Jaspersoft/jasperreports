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
import net.sf.jasperreports.charts.JRBar3DPlot;
import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.charts.JRChartPlot;
import net.sf.jasperreports.engine.JRConstants;

/**
 * @author Flavius Sana (flavius_sana@users.sourceforge.net)
 * @deprecated To be removed.
 */
public class JRBaseBar3DPlot extends JRBaseBarPlot implements JRBar3DPlot 
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_X_OFFSET = "xOffset";
	
	public static final String PROPERTY_Y_OFFSET = "yOffset";
	
	private Double xOffset;
	private Double yOffset;
	
	/**
	 * 
	 */
	public JRBaseBar3DPlot(JRChartPlot plot, JRChart chart)
	{
		this(plot, chart, ChartCopyBaseObjectFactory.instance());
	}
	
	protected JRBaseBar3DPlot(JRChartPlot plot, JRChart chart, ChartCopyObjectFactory copyObjectFactory)
	{
		super(plot, chart);
	}


	/**
	 * 
	 */
	public JRBaseBar3DPlot(JRBar3DPlot barPlot, ChartsBaseObjectFactory factory )
	{
		super( barPlot, factory );
		
		xOffset = barPlot.getXOffset();
		yOffset = barPlot.getYOffset();
	}

	@Override
	public Double getXOffset(){
		return xOffset;
	}
	
	@Override
	public void setXOffset( Double xOffset ){
		Double old = this.xOffset;
		this.xOffset = xOffset;
		getEventSupport().firePropertyChange(PROPERTY_X_OFFSET, old, this.xOffset);
	}
	
	@Override
	public Double getYOffset(){
		return yOffset;
	}
	
	@Override
	public void setYOffset( Double yOffset ){
		Double old = this.yOffset;
		this.yOffset = yOffset;
		getEventSupport().firePropertyChange(PROPERTY_Y_OFFSET, old, this.yOffset);
	}
}

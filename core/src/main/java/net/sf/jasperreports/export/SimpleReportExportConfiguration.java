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
package net.sf.jasperreports.export;

import net.sf.jasperreports.engine.export.ExporterFilter;
import net.sf.jasperreports.engine.export.JRExportProgressMonitor;
import net.sf.jasperreports.engine.export.JRHyperlinkProducerFactory;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class SimpleReportExportConfiguration extends SimpleCommonExportConfiguration implements ReportExportConfiguration
{
	private JRExportProgressMonitor progressMonitor;
	private Integer startPageIndex;
	private Integer endPageIndex;
	private Integer pageIndex;
	private ExporterFilter filter;
	private Integer offsetX;
	private Integer offsetY;
	private JRHyperlinkProducerFactory hyperlinkProducerFactory;
	
	
	/**
	 * 
	 */
	public SimpleReportExportConfiguration()
	{
	}
	

	@Override
	public Integer getStartPageIndex()
	{
		return startPageIndex;
	}
	

	/**
	 * 
	 */
	public void setStartPageIndex(Integer startPageIndex)
	{
		this.startPageIndex = startPageIndex;
	}
	

	@Override
	public Integer getEndPageIndex()
	{
		return endPageIndex;
	}
	

	/**
	 * 
	 */
	public void setEndPageIndex(Integer endPageIndex)
	{
		this.endPageIndex = endPageIndex;
	}
	

	@Override
	public Integer getPageIndex()
	{
		return pageIndex;
	}
	

	/**
	 * 
	 */
	public void setPageIndex(Integer pageIndex)
	{
		this.pageIndex = pageIndex;
	}
	

	@Override
	public JRExportProgressMonitor getProgressMonitor()
	{
		return progressMonitor;
	}
	

	/**
	 * 
	 */
	public void setProgressMonitor(JRExportProgressMonitor progressMonitor)
	{
		this.progressMonitor = progressMonitor;
	}
	

	@Override
	public ExporterFilter getExporterFilter()
	{
		return filter;
	}
	

	/**
	 * 
	 */
	public void setExporterFilter(ExporterFilter filter)
	{
		this.filter = filter;
	}
	

	@Override
	public Integer getOffsetX()
	{
		return offsetX;
	}
	

	/**
	 * 
	 */
	public void setOffsetX(Integer offsetX)
	{
		this.offsetX = offsetX;
	}
	

	@Override
	public Integer getOffsetY()
	{
		return offsetY;
	}
	

	/**
	 * 
	 */
	public void setOffsetY(Integer offsetY)
	{
		this.offsetY = offsetY;
	}
	

	@Override
	public JRHyperlinkProducerFactory getHyperlinkProducerFactory()
	{
		return hyperlinkProducerFactory;
	}
	

	/**
	 * 
	 */
	public void setHyperlinkProducerFactory(JRHyperlinkProducerFactory hyperlinkProducerFactory)
	{
		this.hyperlinkProducerFactory = hyperlinkProducerFactory;
	}
}

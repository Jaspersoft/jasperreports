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



/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class SimpleHtmlExporterConfiguration extends SimpleExporterConfiguration implements HtmlExporterConfiguration
{
	private String htmlHeader;
	private String betweenPagesHtml;
	private String htmlFooter;
	private Boolean flushOutput;

	
	/**
	 * 
	 */
	public SimpleHtmlExporterConfiguration()
	{
	}
	
	@Override
	public String getHtmlHeader()
	{
		return htmlHeader;
	}
	
	/**
	 * 
	 */
	public void setHtmlHeader(String htmlHeader)
	{
		this.htmlHeader = htmlHeader;
	}
	
	@Override
	public String getBetweenPagesHtml()
	{
		return betweenPagesHtml;
	}
	
	/**
	 * 
	 */
	public void setBetweenPagesHtml(String betweenPagesHtml)
	{
		this.betweenPagesHtml = betweenPagesHtml;
	}
	
	@Override
	public String getHtmlFooter()
	{
		return htmlFooter;
	}
	
	/**
	 * 
	 */
	public void setHtmlFooter(String htmlFooter)
	{
		this.htmlFooter = htmlFooter;
	}
	
	@Override
	public Boolean isFlushOutput()
	{
		return flushOutput;
	}
	
	/**
	 * 
	 */
	public void setFlushOutput(Boolean flushOutput)
	{
		this.flushOutput = flushOutput;
	}
}

/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2025 Cloud Software Group, Inc. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
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

import net.sf.jasperreports.engine.export.HtmlResourceHandler;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface HtmlExporterOutput extends WriterExporterOutput
{
	/**
	 * 
	 */
	public HtmlResourceHandler getImageHandler(); 

	/**
	 * This handler is needed when generating static, non-interactive HTML output, in server environments
	 * where the JsonExporter is not involved in bringing in the Web font information and the font CSS still 
	 * needs to be generated dynamically and comes from a server URL that is different from the one used 
	 * for static resources.
	 */
	public HtmlResourceHandler getFontHandler(); 

	/**
	 * 
	 */
	public HtmlResourceHandler getResourceHandler(); 
}

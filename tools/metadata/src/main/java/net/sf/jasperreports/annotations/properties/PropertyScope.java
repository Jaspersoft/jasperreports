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
package net.sf.jasperreports.annotations.properties;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public enum PropertyScope
{

	SYSTEM("System"),
	GLOBAL("Global"),
	CONTEXT("Context"),
	REPORT("Report"),
	DATASET("Dataset"),
	SUBDATASET_RUN("Subdataset Run"),
	BAND("Band"),
	PARAMETER("Parameter"),
	FIELD("Field"),
	ELEMENT("Element"),
	SUBREPORT("Subreport"),
	HYPERLINK("Hyperlink"),
	PART("Part"),
	CHART_ELEMENT("Chart"),
	TEXT_ELEMENT("Text Element"),
	BREAK_ELEMENT("Break Element"),
	COMPONENT("Component"),
	CROSSTAB("Crosstab"),
	CROSSTAB_CELL("Crosstab Cell"),
	IMAGE_ELEMENT("Image Element"),
	FRAME("Frame Element"),
	GENERIC_ELEMENT("Generic Element"),
	TABLE("Table"),
	TABLE_COLUMN("Column"),
	TABLE_CELL("Table Cell"),
	EXTENSION("Extension"),
	INTERNAL("Internal"),
	SCRIPTLET("Scriptlet");

	private final String label;
	
	private PropertyScope(String label)
	{
		this.label = label;
	}
	
	@Override
	public String toString()
	{
		return label;
	}
	
}

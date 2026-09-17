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
package net.sf.jasperreports.engine;

import java.io.Serializable;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class SimplePrintPart implements PrintPart, Serializable
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static SimplePrintPart fromJasperPrint(JasperPrint partJasperPrint, String partName)
	{
		return fromJasperPrint(partJasperPrint, partName, null);
	}
	
	public static SimplePrintPart fromJasperPrint(JasperPrint partJasperPrint, 
			String partName, JRPropertiesHolder properties)
	{
		SimplePrintPart printPart = new SimplePrintPart();
		
		if (partName == null)
		{
			partName = partJasperPrint.getName();
		}
		printPart.setName(partName);
		
		if (properties != null && properties.hasProperties())
		{
			printPart.getPropertiesMap().copyProperties(properties.getPropertiesMap());
		}
		
		SimplePrintPageFormat pageFormat = new SimplePrintPageFormat();
		pageFormat.setPageWidth(partJasperPrint.getPageWidth());
		pageFormat.setPageHeight(partJasperPrint.getPageHeight());
		pageFormat.setOrientation(partJasperPrint.getOrientation());
		pageFormat.setLeftMargin(partJasperPrint.getLeftMargin());
		pageFormat.setTopMargin(partJasperPrint.getTopMargin());
		pageFormat.setRightMargin(partJasperPrint.getRightMargin());
		pageFormat.setBottomMargin(partJasperPrint.getBottomMargin());
		// the dimensions above are in the resolution of the part report, which is not
		// necessarily the resolution of the document the part ends up in
		pageFormat.setDpi(partJasperPrint.getDpi());
		printPart.setPageFormat(pageFormat);
		
		return printPart;
	}

	private String name;
	private PrintPageFormat pageFormat;
	private JRPropertiesMap propertiesMap;
	
	@Override
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	@Override
	public PrintPageFormat getPageFormat()
	{
		return pageFormat;
	}
	
	public void setPageFormat(PrintPageFormat pageFormat)
	{
		this.pageFormat = pageFormat;
	}

	@Override
	public boolean hasProperties()
	{
		return propertiesMap != null && propertiesMap.hasProperties();
	}

	@Override
	public synchronized JRPropertiesMap getPropertiesMap()
	{
		if (propertiesMap == null)
		{
			propertiesMap = new JRPropertiesMap();
		}
		return propertiesMap;
	}

	@Override
	public JRPropertiesHolder getParentProperties()
	{
		return null;
	}

	public void update(String partName, JRPropertiesHolder partProperties)
	{
		if (partName != null)
		{
			name = partName;
		}
		
		if (partProperties != null && partProperties.hasProperties())
		{
			getPropertiesMap().copyProperties(partProperties.getPropertiesMap());
		}
	}
}

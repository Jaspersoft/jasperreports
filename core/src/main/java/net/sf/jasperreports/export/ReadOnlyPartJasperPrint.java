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

import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JROrigin;
import net.sf.jasperreports.engine.JRPrintAnchorIndex;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.PrintBookmark;
import net.sf.jasperreports.engine.PrintPart;
import net.sf.jasperreports.engine.PrintParts;
import net.sf.jasperreports.engine.type.OrientationEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class ReadOnlyPartJasperPrint extends JasperPrint
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private JasperPrint parentJasperPrint;
	private PrintPart part;
	private List<JRPrintPage> pages;
	
	public ReadOnlyPartJasperPrint(JasperPrint jasperPrint, PrintPart part, int startPageIndex, int endPageIndex)
	{
		this.parentJasperPrint = jasperPrint;
		this.part = part;
		this.pages = jasperPrint.getPages().subList(startPageIndex, endPageIndex);
	}

	@Override
	public String getName()
	{
		return part == null ? parentJasperPrint.getName() : part.getName();
	}
		
	@Override
	public void setName(String name)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public int getDpi()
	{
		// a part can come from a report whose resolution differs from the one of the
		// document it ends up in, in which case its page format carries that resolution
		return part == null ? parentJasperPrint.getDpi() : part.getPageFormat().getDpi();
	}

	@Override
	public void setDpi(int dpi)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public int getPageWidth()
	{
		return part == null ? parentJasperPrint.getPageWidth() : part.getPageFormat().getPageWidth();
	}
		
	@Override
	public void setPageWidth(int pageWidth)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public int getPageHeight()
	{
		return part == null ? parentJasperPrint.getPageHeight() : part.getPageFormat().getPageHeight();
	}
		
	@Override
	public void setPageHeight(int pageHeight)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer getTopMargin()
	{
		return part == null ? parentJasperPrint.getTopMargin() : part.getPageFormat().getTopMargin();
	}
		
	@Override
	public void setTopMargin(Integer topMargin)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer getLeftMargin()
	{
		return part == null ? parentJasperPrint.getLeftMargin() : part.getPageFormat().getLeftMargin();
	}
		
	@Override
	public void setLeftMargin(Integer leftMargin)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer getBottomMargin()
	{
		return part == null ? parentJasperPrint.getBottomMargin() : part.getPageFormat().getBottomMargin();
	}
		
	@Override
	public void setBottomMargin(Integer bottomMargin)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer getRightMargin()
	{
		return part == null ? parentJasperPrint.getRightMargin() : part.getPageFormat().getRightMargin();
	}
		
	@Override
	public void setRightMargin(Integer rightMargin)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public OrientationEnum getOrientation()
	{
		return part == null ? parentJasperPrint.getOrientation() : part.getPageFormat().getOrientation();
	}
		
	@Override
	public void setOrientation(OrientationEnum orientation)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasProperties()
	{
		return parentJasperPrint.hasProperties();
	}
	
	@Override
	public JRPropertiesMap getPropertiesMap()
	{
		return parentJasperPrint.getPropertiesMap();
	}

	@Override
	public JRPropertiesHolder getParentProperties()
	{
		return null;
	}
	
	@Override
	public String[] getPropertyNames()
	{
		return parentJasperPrint.getPropertyNames();
	}

	@Override
	public String getProperty(String propName)
	{
		return parentJasperPrint.getProperty(propName);
	}

	@Override
	public void setProperty(String propName, String value)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeProperty(String propName)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public JRStyle getDefaultStyle()
	{
		return parentJasperPrint.getDefaultStyle();
	}

	@Override
	public synchronized void setDefaultStyle(JRStyle style)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public JRDefaultStyleProvider getDefaultStyleProvider()
	{
		return parentJasperPrint.getDefaultStyleProvider();
	}
		
	@Override
	public JRStyle[] getStyles()
	{
		return parentJasperPrint.getStyles();
	}

	@Override
	public List<JRStyle> getStylesList()
	{
		return parentJasperPrint.getStylesList();
	}

	@Override
	public Map<String, JRStyle> getStylesMap()
	{
		return parentJasperPrint.getStylesMap();
	}

	@Override
	public synchronized void addStyle(JRStyle style) throws JRException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void addStyle(JRStyle style, boolean isIgnoreDuplicate) throws JRException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized JRStyle removeStyle(String styleName)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized JRStyle removeStyle(JRStyle style)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public JROrigin[] getOrigins()
	{
		return parentJasperPrint.getOrigins();
	}

	@Override
	public List<JROrigin> getOriginsList()
	{
		return parentJasperPrint.getOriginsList();
	}

	@Override
	public Map<JROrigin, Integer> getOriginsMap()
	{
		return parentJasperPrint.getOriginsMap();
	}

	@Override
	public synchronized void addOrigin(JROrigin origin)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized JROrigin removeOrigin(JROrigin origin)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public PrintParts getParts()
	{
		return null;
	}

	@Override
	public synchronized void addPart(int pageIndex, PrintPart part)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized PrintPart removePart(int pageIndex)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public List<JRPrintPage> getPages()
	{
		return pages;
	}

	@Override
	public synchronized void addPage(JRPrintPage page)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void addPage(int index, JRPrintPage page)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized JRPrintPage removePage(int index)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public List<PrintBookmark> getBookmarks()
	{
		return parentJasperPrint.getBookmarks();
	}

	@Override
	public synchronized void addBookmark(PrintBookmark bookmark)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setBookmarks(List<PrintBookmark> bookmarks)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized Map<String,JRPrintAnchorIndex> getAnchorIndexes()
	{
		return parentJasperPrint.getAnchorIndexes();
	}

	@Override
	public String getFormatFactoryClass()
	{
		return parentJasperPrint.getFormatFactoryClass();
	}

	@Override
	public void setFormatFactoryClass(String formatFactoryClass)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getLocaleCode()
	{
		return parentJasperPrint.getLocaleCode();
	}

	@Override
	public void setLocaleCode(String localeCode)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getTimeZoneId()
	{
		return parentJasperPrint.getTimeZoneId();
	}

	@Override
	public void setTimeZoneId(String timeZoneId)
	{
		throw new UnsupportedOperationException();
	}
}
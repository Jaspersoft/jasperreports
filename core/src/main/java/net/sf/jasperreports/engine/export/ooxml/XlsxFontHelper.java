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
package net.sf.jasperreports.engine.export.ooxml;

import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.export.JRExporterGridCell;
import net.sf.jasperreports.engine.util.Pair;
import net.sf.jasperreports.export.XlsReportConfiguration;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class XlsxFontHelper extends BaseHelper
{
	private Map<XlsxFontInfo, Integer> fontCache = new HashMap<>();//FIXMEXLSX use soft cache? check other exporter caches as well
	
	private String exporterKey;

	private boolean fontSizeFixEnabled;

	private Map<Pair<String, Locale>, String> exportFontCache = new HashMap<>();

	/**
	 *
	 */
	public XlsxFontHelper(
		JasperReportsContext jasperReportsContext,
		Writer writer,
		String exporterKey
		)
	{
		super(jasperReportsContext, writer);

		this.exporterKey = exporterKey;
	}
	
	/**
	 * 
	 */
	public void setConfiguration(XlsReportConfiguration configuration)
	{
		fontSizeFixEnabled = configuration.isFontSizeFixEnabled();
	}
	
	/**
	 *
	 */
	public int getFont(JRExporterGridCell gridCell, Locale locale)
	{
		if (gridCell == null)
		{
			return -1;			
		}
		return getFont(gridCell.getElement(), locale);
	}

	/**
	 *
	 */
	public int getFont(JRLineBox box, Locale locale)
	{
		return -1;
	}
	
	public int getFont(JRPrintElement element, Locale locale)
	{
		JRFont font = element instanceof JRFont ? (JRFont)element : null;
		if (font == null)
		{
			return -1;			
		}

		String fontName = exportFont(font, locale);
		XlsxFontInfo xlsxFontInfo = new XlsxFontInfo(element, fontName, fontSizeFixEnabled);
		Integer fontIndex = fontCache.get(xlsxFontInfo);
		if (fontIndex == null)
		{
			fontIndex = fontCache.size();
			export(xlsxFontInfo);
			fontCache.put(xlsxFontInfo, fontIndex);
		}
		return fontIndex;
	}

	protected String exportFont(JRFont font, Locale locale)
	{
		String fontName = font.getFontName();
		Pair<String, Locale> cacheKey = new Pair<>(fontName, locale);
		String exportFont = exportFontCache.get(cacheKey);
		if (exportFont == null)
		{
			exportFont = fontUtil.getExportFontFamily(fontName, locale, exporterKey);
			exportFontCache.put(cacheKey, exportFont);
		}
		return exportFont;
	}

	/**
	 *
	 */
	private void export(XlsxFontInfo fontInfo)
	{
		write(
			"<font><sz val=\"" + fontInfo.fontSize + "\"/>" 
			+ "<color rgb=\"" + fontInfo.color + "\"/>"
			+ "<name val=\"" + fontInfo.fontName + "\"/>"
			+ "<b val=\"" + fontInfo.isBold + "\"/>"
			+ "<i val=\"" + fontInfo.isItalic + "\"/>"
			+ "<u val=\"" + (fontInfo.isUnderline ? "single" : "none") + "\"/>"
			+ "<strike val=\"" + fontInfo.isStrikeThrough + "\"/>"
			+ "<family val=\"2\"/></font>\n"
			);
	}

}

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
package net.sf.jasperreports.engine.export.ooxml;

import java.io.Writer;

import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.export.zip.FileBufferedZip;
import net.sf.jasperreports.engine.fonts.FontFace;

/**
 * @author Sanda Zaharia(shertage@users.sourceforge.net)
 */
public class PptxFontHelper extends BaseFontHelper
{
	/**
	 * 
	 */
	public PptxFontHelper(
		JasperReportsContext jasperReportsContext, 
		Writer writer, 
		Writer relsWriter, 
		FileBufferedZip zip,
		boolean isEmbedFonts
		)
	{
		super(jasperReportsContext, writer, relsWriter, zip, isEmbedFonts);
	}

	@Override
	protected String getExporterKey()
	{
		return JRPptxExporter.PPTX_EXPORTER_KEY;
	}

	@Override
	protected String getEndFontTag()
	{
		return "  </p:embeddedFont>\n";
	}

	@Override
	protected String getFontsDir() 
	{
		return PPT_FONTS_DIR;
	}
	
	@Override
	protected String getStartFontTag(String fontName)
	{
		return "  <p:embeddedFont>\n    <p:font typeface=\"" + fontName + "\"/>\n"; 
	}
	
	@Override
	protected String getRegularEmbedding(String id)
	{
		return "    <p:regular r:id=\"" + id + "\"/>\n"; 
	}
	
	@Override
	protected String getBoldEmbedding(String id)
	{
		return "    <p:bold r:id=\"" + id + "\"/>\n"; 
	}
	
	@Override
	protected String getItalicEmbedding(String id)
	{
		return "    <p:italic r:id=\"" + id + "\"/>\n"; 
	}
	
	@Override
	protected String getBoldItalicEmbedding(String id)
	{
		return "    <p:boldItalic r:id=\"" + id + "\"/>\n"; 
	}

	@Override
	protected String getFontPath(FontFace fontFace)
	{
		return fontFace == null ? null : fontFace.getEot();
	}
}

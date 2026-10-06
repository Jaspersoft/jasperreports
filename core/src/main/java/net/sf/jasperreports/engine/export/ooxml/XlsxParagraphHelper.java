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

import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.VerticalTextAlignEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class XlsxParagraphHelper extends BaseHelper
{
	/**
	 *
	 */
	private static final String HORIZONTAL_ALIGN_LEFT = "left";
	private static final String HORIZONTAL_ALIGN_RIGHT = "right";
	private static final String HORIZONTAL_ALIGN_CENTER = "center";
	private static final String HORIZONTAL_ALIGN_JUSTIFY = "justify";

	private static final String VERTICAL_ALIGN_TOP = "top";
	private static final String VERTICAL_ALIGN_MIDDLE = "center";
	private static final String VERTICAL_ALIGN_BOTTOM = "bottom";
	private static final String VERTICAL_ALIGN_JUSTIFY = "justify";

	/**
	 *
	 */
	private boolean pageBreak;

	/**
	 *
	 */
	public XlsxParagraphHelper(JasperReportsContext jasperReportsContext, Writer writer, boolean pageBreak)
	{
		super(jasperReportsContext, writer);
		
		this.pageBreak = pageBreak;
	}
	
	/**
	 *
	 */
	public void exportProps(JRStyle style)
	{
		exportPropsHeader(null);

		exportAlignment(
			getHorizontalAlignment(
				style.getOwnHorizontalTextAlign() 
				)
			);

		exportPropsFooter();
	}
	
	/**
	 *
	 */
	public void exportProps(JRPrintText text)
	{
		exportPropsHeader(text.getStyle() == null ? null : text.getStyle().getName()); //javadoc says getStyleNameReference is not supposed to work for print elements 

		exportAlignment(
			getHorizontalAlignment(
				text.getOwnHorizontalTextAlign()
				)
			);
		
//		exportRunDirection(text.getRunDirection() == JRPrintText.RUN_DIRECTION_RTL ? "rl" : null);

		exportPropsFooter();
	}
	
	
	/**
	 *
	 */
	private void exportPropsHeader(String styleNameReference)
	{
		write("      <w:pPr>\n");
		if (styleNameReference != null)
		{
			write("        <w:pStyle w:val=\"" + styleNameReference + "\"/>\n");
		}
		if (pageBreak)
		{
			write("        <w:pageBreakBefore/>\n");
			pageBreak = false;
		}
	}
	
	/**
	 *
	 */
	private void exportAlignment(String horizontalAlignment)
	{
		if (horizontalAlignment != null)
		{
			write("   <w:jc w:val=\"" + horizontalAlignment + "\" />\n");
		}
		//FIXMEDOCX line spacing?
	}
	
	/**
	 *
	 */
	private void exportPropsFooter()
	{
		write("      </w:pPr>\n");
	}
	
	/**
	 *
	 */
	public void exportEmptyParagraph()
	{
		write("     <w:p><w:pPr>\n");
		if (pageBreak)
		{
			write("        <w:pageBreakBefore/>\n");
			pageBreak = false;
		}
		write("     </w:pPr></w:p>\n");
	}

	/**
	 *
	 */
	public static String getHorizontalAlignment(HorizontalTextAlignEnum horizontalAlignment)
	{
		if (horizontalAlignment != null)
		{
			switch (horizontalAlignment)
			{
				case RIGHT :
					return HORIZONTAL_ALIGN_RIGHT;
				case CENTER :
					return HORIZONTAL_ALIGN_CENTER;
				case JUSTIFIED :
					return HORIZONTAL_ALIGN_JUSTIFY;
				case LEFT :
				default :
					return HORIZONTAL_ALIGN_LEFT;
			}
		}
		return null;
	}

	/**
	 *
	 */
	public static String getVerticalAlignment(VerticalTextAlignEnum verticalAlignment)
	{
		if (verticalAlignment != null)
		{
			switch (verticalAlignment)
			{
				case JUSTIFIED :
					return VERTICAL_ALIGN_JUSTIFY;
				case BOTTOM :
					return VERTICAL_ALIGN_BOTTOM;
				case MIDDLE :
					return VERTICAL_ALIGN_MIDDLE;
				case TOP :
				default :
					return VERTICAL_ALIGN_TOP;
			}
		}
		return null;
	}
}

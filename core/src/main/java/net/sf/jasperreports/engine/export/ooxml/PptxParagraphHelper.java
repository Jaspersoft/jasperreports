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

import net.sf.jasperreports.engine.JRParagraph;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.TabStop;
import net.sf.jasperreports.engine.export.LengthUtil;
import net.sf.jasperreports.engine.type.HorizontalImageAlignEnum;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.TabStopAlignEnum;


/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class PptxParagraphHelper extends BaseHelper
{
	/**
	 *
	 */
	private static final String HORIZONTAL_ALIGN_LEFT = "left";
	private static final String HORIZONTAL_ALIGN_RIGHT = "right";
	private static final String HORIZONTAL_ALIGN_CENTER = "center";
	private static final String HORIZONTAL_ALIGN_BOTH = "both";

	/**
	 *
	 */
	private static final String TAB_STOP_ALIGN_LEFT = "left";
	private static final String TAB_STOP_ALIGN_RIGHT = "right";
	private static final String TAB_STOP_ALIGN_CENTER = "center";
	
	protected static final int LINE_SPACING_FACTOR = 240; //(int)(240 * 2/3f);

	/**
	 *
	 */
	private int dpi;

	/**
	 *
	 */
	public PptxParagraphHelper(JasperReportsContext jasperReportsContext, Writer writer, int dpi)
	{
		super(jasperReportsContext, writer);
		this.dpi = dpi;
	}
	
	/**
	 *
	 */
	public void exportProps(JRStyle style)
	{
		exportPropsHeader(null, style.getParagraph());

		exportAlignment(
			getHorizontalTextAlign(
				style.getOwnHorizontalTextAlign() 
				)
			);

		exportTabStops(
			style.getParagraph()
			);

		exportLineSpacing(
			style.getParagraph() 
			);

		exportPropsFooter();
	}
	
	/**
	 *
	 */
	public void exportProps(JRPrintText text)
	{
		JRStyle baseStyle = text.getDefaultStyleProvider().getStyleResolver().getBaseStyle(text); 
		exportPropsHeader(baseStyle == null ? null : baseStyle.getName(), text.getParagraph()); //javadoc says getStyleNameReference is not supposed to work for print elements

		exportAlignment(
			getHorizontalTextAlign(
				text.getOwnHorizontalTextAlign()
				)
			);
		
		exportTabStops(
			text.getParagraph() 
			);

		exportLineSpacing(
			text.getParagraph() 
			);

		exportPropsFooter();
	}
	
	
	/**
	 *
	 */
	public void exportProps(JRPrintImage image)
	{
		JRStyle baseStyle = image.getDefaultStyleProvider().getStyleResolver().getBaseStyle(image); 
		exportPropsHeader(baseStyle == null ? null : baseStyle.getName(), null); 

		exportAlignment(
			getHorizontalImageAlign(
				image.getOwnHorizontalImageAlign()
				)
			);
		write("   <a:spacing a:lineRule=\"auto\" a:line=\"240\" a:after=\"0\" a:before=\"0\"/>\n");
		exportPropsFooter();
	}
	
	
	/**
	 *
	 */
	private void exportPropsHeader(String styleNameReference, JRParagraph paragraph)
	{
		write("      <a:pPr>\n");
		if (styleNameReference != null)
		{
			write("        <a:pStyle a:val=\"" + styleNameReference + "\"/>\n");
		}
		if(paragraph != null)
		{
			write("      <a:ind");
			if (paragraph.getOwnFirstLineIndent() != null)
			{
				write(" a:firstLine=\"" + LengthUtil.twip(paragraph.getOwnFirstLineIndent(), dpi) + "\"");
			}
			if (paragraph.getOwnLeftIndent() != null)
			{
				write(" a:left=\"" + LengthUtil.twip(paragraph.getOwnLeftIndent(), dpi) + "\"");
			}
			if (paragraph.getOwnRightIndent() != null)
			{
				write(" a:right=\"" + LengthUtil.twip(paragraph.getOwnRightIndent(), dpi) + "\"");
			}
			write("/>\n");
		}
	}
	
	/**
	 *
	 */
	private void exportAlignment(String horizontalAlignment)
	{
		if (horizontalAlignment != null)
		{
			write("   <a:jc a:val=\"" + horizontalAlignment + "\" />\n");
		}
	}
	
	/**
	 *
	 */
	private void exportTabStops(JRParagraph paragraph)
	{
		TabStop[] tabStops = paragraph.getTabStops();
		if (tabStops != null && tabStops.length > 0)
		{
			write("   <a:tabs>\n");
			for (int i = 0; i < tabStops.length; i++)
			{
				TabStop tabStop = tabStops[i];
				write(
					"   <a:tab a:pos=\"" 
					+ LengthUtil.twip(tabStop.getPosition(), dpi)
					+ "\" a:val=\"" + getTabStopAlignment(TabStopAlignEnum.getValueOrDefault(tabStop.getAlignment())) 
					+ "\"/>\n"
					);
			}
			write("   </a:tabs>\n");
		}
	}
	
	/**
	 *
	 */
	private void exportLineSpacing(JRParagraph paragraph)
	{
		if (
			paragraph.getOwnLineSpacing() != null
			|| paragraph.getOwnLineSpacingSize() != null
			|| paragraph.getOwnSpacingBefore() != null
			|| paragraph.getOwnSpacingAfter() != null
			)
		{
			String lineRule; 
			String lineSpacing; 

			switch (paragraph.getLineSpacing())
			{
				case AT_LEAST :
				{
					lineRule = "atLeast";
					lineSpacing = String.valueOf(LengthUtil.twip(paragraph.getLineSpacingSize(), dpi));
					break;
				}
				case FIXED :
				{
					lineRule = "exact";
					lineSpacing = String.valueOf(LengthUtil.twip(paragraph.getLineSpacingSize(), dpi)); 
					break;
				}
				case PROPORTIONAL :
				{
					lineRule = "auto";
					lineSpacing = String.valueOf((int)(paragraph.getLineSpacingSize() * LINE_SPACING_FACTOR)); 
					break;
				}
				case DOUBLE :
				{
					lineRule = "auto";
					lineSpacing = String.valueOf((int)(2f * LINE_SPACING_FACTOR)); 
					break;
				}
				case ONE_AND_HALF :
				{
					lineRule = "auto";
					lineSpacing = String.valueOf((int)(1.5f * LINE_SPACING_FACTOR)); 
					break;
				}
				case SINGLE :
				default :
				{
					lineRule = "auto";
					lineSpacing = String.valueOf((int)(1f * LINE_SPACING_FACTOR));
				}
			}
			
			write("   <a:spacing a:lineRule=\"" + lineRule + "\" a:line=\"" + lineSpacing + "\"");
			write(" a:after=\"" + LengthUtil.twip(paragraph.getSpacingAfter(), dpi) + "\"");
			write(" a:before=\"" + LengthUtil.twip(paragraph.getSpacingBefore(), dpi) + "\"/>\n");
		}
	}
	
	/**
	 *
	 */
	private void exportPropsFooter()
	{
		write("      </a:pPr>\n");
	}
	
	/**
	 *
	 */
	public void exportEmptyParagraph()
	{
		write("  <a:txBody>\n");
		write("    <a:bodyPr/>\n");
		write("    <a:lstStyle/>\n");
		write("     <a:p><a:pPr><a:defRPr sz=\"100\"/>");
		write("     </a:pPr>");
		write("</a:p>\n");
		write("  </a:txBody>\n");
	}

	/**
	 *
	 */
	public static String getHorizontalTextAlign(HorizontalTextAlignEnum horizontalAlignment)
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
					return HORIZONTAL_ALIGN_BOTH;
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
	public static String getHorizontalImageAlign(HorizontalImageAlignEnum horizontalAlignment)
	{
		if (horizontalAlignment != null)
		{
			switch (horizontalAlignment)
			{
				case RIGHT :
					return HORIZONTAL_ALIGN_RIGHT;
				case CENTER :
					return HORIZONTAL_ALIGN_CENTER;
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
	public static String getTabStopAlignment(TabStopAlignEnum alignment)
	{
		switch (alignment)
		{
			case RIGHT :
				return TAB_STOP_ALIGN_RIGHT;
			case CENTER :
				return TAB_STOP_ALIGN_CENTER;
			case LEFT :
			default :
				return TAB_STOP_ALIGN_LEFT;
		}
	}
	
}

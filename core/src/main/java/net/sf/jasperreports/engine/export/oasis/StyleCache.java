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
package net.sf.jasperreports.engine.export.oasis;

import java.awt.Color;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintGraphicElement;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.export.JRExporterGridCell;
import net.sf.jasperreports.engine.fonts.FontUtil;
import net.sf.jasperreports.engine.util.JRColorUtil;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class StyleCache
{
	/**
	 *
	 */
	private final FontUtil fontUtil;
	private final WriterHelper styleWriter;
	private Set<String> fontFaces = new HashSet<>();
	private final String exporterKey;

	/**
	 *
	 */
	private Map<String,String> tableStyles = new HashMap<>();//FIXMEODT soft cache?
	private int tableStylesCounter;
	private Map<String,String> rowStyles = new HashMap<>();
	private int rowStylesCounter;
	private Map<String,String> columnStyles = new HashMap<>();
	private int columnStylesCounter;
	private Map<String,String> frameStyles = new HashMap<>();
	private int frameStylesCounter;
	private Map<String,String> cellStyles = new HashMap<>();
	private int cellStylesCounter;
	private Map<String,String> graphicStyles = new HashMap<>();
	private int graphicStylesCounter;
	private Map<String,String> paragraphStyles = new HashMap<>();
	private int paragraphStylesCounter;
	private Map<String,String> textSpanStyles = new HashMap<>();
	private int textSpanStylesCounter;


	/**
	 *
	 */
	public StyleCache(
		JasperReportsContext jasperReportsContext, 
		WriterHelper styleWriter, 
		String exporterKey
		)
	{
		this.fontUtil = FontUtil.getInstance(jasperReportsContext);
		this.styleWriter = styleWriter;
		this.exporterKey = exporterKey;
	}


	/**
	 *
	 */
	public Collection<String> getFontFaces()
	{
		return fontFaces;
	}


	/**
	 *
	 */
	public String getTableStyle(int width, int pageFormatIndex, boolean isFrame, boolean isPageBreak, Color tabColor) throws IOException 
	{
		TableStyle tableStyle  = new TableStyle(styleWriter, width, pageFormatIndex, isFrame, isPageBreak, tabColor);
		
		String tableStyleId = tableStyle.getId();
		String tableStyleName = tableStyles.get(tableStyleId);
		
		if (tableStyleName == null)
		{
			tableStyleName = "TBL" + tableStylesCounter++;
			tableStyles.put(tableStyleId, tableStyleName);
			
			tableStyle.write(tableStyleName);
		}
		
		return tableStyleName;
	}
	
	
	/**
	 *
	 */
	public String getRowStyle(int rowHeight) throws IOException 
	{
		RowStyle rowStyle  = new RowStyle(styleWriter, rowHeight);
		
		String rowStyleId = rowStyle.getId();
		String rowStyleName = rowStyles.get(rowStyleId);
		
		if (rowStyleName == null)
		{
			rowStyleName = "TR" + rowStylesCounter++;
			rowStyles.put(rowStyleId, rowStyleName);
			
			rowStyle.write(rowStyleName);
		}
		
		return rowStyleName;
	}
	
	
	/**
	 *
	 */
	public String getColumnStyle(int columnWidth) throws IOException 
	{
		ColumnStyle columnStyle  = new ColumnStyle(styleWriter, columnWidth);
		
		String columnStyleId = columnStyle.getId();
		String columnStyleName = columnStyles.get(columnStyleId);
		
		if (columnStyleName == null)
		{
			columnStyleName = "TC" + columnStylesCounter++;
			columnStyles.put(columnStyleId, columnStyleName);
			
			columnStyle.write(columnStyleName);
		}
		
		return columnStyleName;
	}
	
	
	/**
	 *
	 */
	public String getFrameStyle(JRPrintText text) throws IOException //FIXMEODT is this used?
	{
		FrameStyle frameStyle  = new FrameStyle(styleWriter, text);
		frameStyle.setBox(text.getLineBox());
		
		String frameStyleId = frameStyle.getId();
		String frameStyleName = frameStyles.get(frameStyleId);
		
		if (frameStyleName == null)
		{
			frameStyleName = "F" + frameStylesCounter++;
			frameStyles.put(frameStyleId, frameStyleName);
			
			frameStyle.write(frameStyleName);
		}
		
		return frameStyleName;
	}


	/**
	 *
	 */
	public String getFrameStyle(JRPrintElement element) throws IOException //FIXMEODT is this used?
	{
		FrameStyle frameStyle  = new FrameStyle(styleWriter, element);
		
		String frameStyleId = frameStyle.getId();
		String frameStyleName = frameStyles.get(frameStyleId);
		
		if (frameStyleName == null)
		{
			frameStyleName = "F" + frameStylesCounter++;
			frameStyles.put(frameStyleId, frameStyleName);
			
			frameStyle.write(frameStyleName);
		}
		
		return frameStyleName;
	}


	/**
	 *
	 */
	public String getGraphicStyle(JRPrintGraphicElement element)
	{
		return getGraphicStyle(element, 0, 0, 0, 0);
	}
	
	
	/**
	 *
	 */
	public String getGraphicStyle(
			JRPrintGraphicElement element, 
			double cropTop,
			double cropLeft,
			double cropBottom,
			double cropRight
			)
	{
		GraphicStyle graphicStyle  = new GraphicStyle(
				styleWriter, 
				element, 
				cropTop, 
				cropLeft,
				cropBottom,
				cropRight);
		
		String graphicStyleId = graphicStyle.getId();
		String graphicStyleName = cellStyles.get(graphicStyleId);
		
		if (graphicStyleName == null)
		{
			graphicStyleName = "G" + graphicStylesCounter++;
			graphicStyles.put(graphicStyleId, graphicStyleName);
			
			graphicStyle.write(graphicStyleName);
		}
		
		return graphicStyleName;
	}


	/**
	 *
	 */
	public String getCellStyle(JRExporterGridCell gridCell)
	{
		return getCellStyle(gridCell, false, true);
	}
	
	/**
	 *
	 */
	public String getCellStyle(JRExporterGridCell gridCell, boolean shrinkToFit, boolean wrapText)
	{
		CellStyle cellStyle  = new CellStyle(styleWriter, gridCell, shrinkToFit, wrapText);
		
//		JRPrintElement element = gridCell.getElement();
//
//		if (element instanceof JRBoxContainer)
//			cellStyle.setBox(((JRBoxContainer)element).getLineBox());
//		if (element instanceof JRCommonGraphicElement)
//			cellStyle.setPen(((JRCommonGraphicElement)element).getLinePen());
		
		String cellStyleId = cellStyle.getId();
		String cellStyleName = cellStyles.get(cellStyleId);
		
		if (cellStyleName == null)
		{
			cellStyleName = "C" + cellStylesCounter++;
			cellStyles.put(cellStyleId, cellStyleName);
			
			cellStyle.write(cellStyleName);
		}
		
		return cellStyleName;
	}

	/**
	 *
	 */
	public String getParagraphStyle(JRPrintText text, boolean isIgnoreTextFormatting)
	{
		ParagraphStyle paragraphStyle  = new ParagraphStyle(styleWriter, text, isIgnoreTextFormatting);
		
		String paragraphStyleId = paragraphStyle.getId();
		String paragraphStyleName = paragraphStyles.get(paragraphStyleId);
		
		if (paragraphStyleName == null)
		{
			paragraphStyleName = "P" + paragraphStylesCounter++;
			paragraphStyles.put(paragraphStyleId, paragraphStyleName);
			
			paragraphStyle.write(paragraphStyleName);
		}
		
		return paragraphStyleName;
	}


	/**
	 *
	 */
	public String getTextSpanStyle(Map<Attribute,Object> attributes, String text, Locale locale, boolean isIgnoreTextFormatting)
	{
		if (isIgnoreTextFormatting)
		{
			String textSpanStyleName = textSpanStyles.get("");
			if (textSpanStyleName == null)
			{
				textSpanStyleName = "T" + textSpanStylesCounter++;
				textSpanStyles.put("", textSpanStyleName);
				styleWriter.write("<style:style style:name=\"" + textSpanStyleName + "\"");
				styleWriter.write(" style:family=\"text\">\n");
				styleWriter.write("<style:text-properties>\n");
				styleWriter.write("</style:text-properties>\n");	
				styleWriter.write("</style:style>\n");
			}
			return textSpanStyleName;
		}
		String fontFamilyAttr = (String)attributes.get(TextAttribute.FAMILY);
		String fontFamily = fontUtil.getExportFontFamily(fontFamilyAttr, locale, exporterKey);
		fontFaces.add(fontFamily);
		
		StringBuilder textSpanStyleIdBuilder = new StringBuilder();
		textSpanStyleIdBuilder.append(fontFamily);

		String forecolorHexa = null;
		Color forecolor = (Color)attributes.get(TextAttribute.FOREGROUND);
		if (!Color.black.equals(forecolor))
		{
			forecolorHexa = JRColorUtil.getColorHexa(forecolor);
			textSpanStyleIdBuilder.append(forecolorHexa);
		}

		String backcolorHexa = null;
		Color runBackcolor = (Color)attributes.get(TextAttribute.BACKGROUND);
		if (runBackcolor != null)
		{
			backcolorHexa = JRColorUtil.getColorHexa(runBackcolor);
			textSpanStyleIdBuilder.append(backcolorHexa);
		}

		String size = String.valueOf(attributes.get(TextAttribute.SIZE));
		textSpanStyleIdBuilder.append(size);

		String weight = null;
		if (TextAttribute.WEIGHT_BOLD.equals(attributes.get(TextAttribute.WEIGHT)))
		{
			weight = "bold";
			textSpanStyleIdBuilder.append(weight);
		}
		String posture = null;
		if (TextAttribute.POSTURE_OBLIQUE.equals(attributes.get(TextAttribute.POSTURE)))
		{
			posture = "italic";
			textSpanStyleIdBuilder.append(posture);
		}
		String underline = null;
		if (TextAttribute.UNDERLINE_ON.equals(attributes.get(TextAttribute.UNDERLINE)))
		{
			underline = "single";
			textSpanStyleIdBuilder.append(underline);
		}
		String strikeThrough = null;
		if (TextAttribute.STRIKETHROUGH_ON.equals(attributes.get(TextAttribute.STRIKETHROUGH)))
		{
			strikeThrough = "single";
			textSpanStyleIdBuilder.append(strikeThrough);
		}

//		if (TextAttribute.SUPERSCRIPT_SUPER.equals(attributes.get(TextAttribute.SUPERSCRIPT)))
//		{
//			textSpanStyleIdBuffer.append(" vertical-align: super;");
//		}
//		else if (TextAttribute.SUPERSCRIPT_SUB.equals(attributes.get(TextAttribute.SUPERSCRIPT)))
//		{
//			textSpanStyleIdBuffer.append(" vertical-align: sub;");
//		}

		String textSpanStyleId = textSpanStyleIdBuilder.toString();
		String textSpanStyleName = textSpanStyles.get(textSpanStyleId);
		
		if (textSpanStyleName == null)
		{
			textSpanStyleName = "T" + textSpanStylesCounter++;
			textSpanStyles.put(textSpanStyleId, textSpanStyleName);
			
			styleWriter.write("<style:style style:name=\"" + textSpanStyleName + "\"");
			styleWriter.write(" style:family=\"text\">\n");
			styleWriter.write("<style:text-properties");
			if (forecolorHexa != null)
			{
				styleWriter.write(" fo:color=\"#" + forecolorHexa+ "\"");
			}
			styleWriter.write(" style:font-name=\"" + fontFamily + "\"");
			styleWriter.write(" fo:font-size=\"" + size + "pt\"");
			styleWriter.write(" style:font-size-asian=\"" + size + "pt\"");
			styleWriter.write(" style:font-size-complex=\"" + size + "pt\"");
			if (posture != null)
			{
				styleWriter.write(" fo:font-style=\"" + posture + "\"");
			}
			if (weight != null)
			{
				styleWriter.write(" fo:font-weight=\"" + weight + "\"");
			}
			if (backcolorHexa != null)
			{
				styleWriter.write(" fo:background-color=\"#" + backcolorHexa + "\"");
			}
//			styleWriter.write(" style:text-rotation-angle=\"" + textRotationAngle + "\"");
//			styleWriter.write(" style:text-rotation-scale=\"" + textRotationScale + "\"");
			if (underline != null)
			{
				styleWriter.write(" style:text-underline-type=\"" + underline + "\"");
			}
			if (strikeThrough != null)
			{
				styleWriter.write(" style:text-line-through-type=\"" + strikeThrough + "\"");
			}
			styleWriter.write(">\n");
			styleWriter.write("</style:text-properties>\n");	
			styleWriter.write("</style:style>\n");
		}
		
		return textSpanStyleName;
	}

	
}


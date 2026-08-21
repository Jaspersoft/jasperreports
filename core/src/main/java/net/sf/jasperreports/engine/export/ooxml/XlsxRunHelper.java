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

import java.awt.Color;
import java.awt.font.TextAttribute;
import java.io.StringWriter;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.base.JRBasePrintText;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.util.JRColorUtil;
import net.sf.jasperreports.engine.util.JRStringUtil;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class XlsxRunHelper extends BaseHelper
{
	public static final String COLOR_NONE = "none";
	/**
	 *
	 */
	private String exporterKey;


	/**
	 *
	 */
	public XlsxRunHelper(
		JasperReportsContext jasperReportsContext, 
		String exporterKey
		)
	{
		super(jasperReportsContext, new StringWriter());
		this.exporterKey = exporterKey;
	}


	/**
	 *
	 */
	public String getSharedString()
	{
		return ((StringWriter)writer).toString();
	}

	/**
	 *
	 */
	public void export(JRStyle style, Map<Attribute,Object> attributes, String text, Locale locale, String invalidCharReplacement, boolean isStyledText)
	{
		if (text != null)
		{
			write("<r>");
			if (isStyledText)
			{
				exportProps(getAttributes(style), attributes, locale);
			}
			write("<t xml:space=\"preserve\">");
			write(JRStringUtil.xmlEncode(text, invalidCharReplacement));
			write("</t></r>");
		}
	}

	/**
	 *
	 */
	private void exportProps(Map<Attribute,Object> parentAttrs,  Map<Attribute,Object> attrs, Locale locale)
	{

		boolean isOpen = false;
		
		// tracker #8326: text run properties do not inherit font settings from the cell style in XLSX; we need to set it manually
		Object value = attrs.get(TextAttribute.FAMILY) == null 
				? parentAttrs.get(TextAttribute.FAMILY) 
				: attrs.get(TextAttribute.FAMILY);
		
		if (value != null)	//FIXMEXLSX the text locale might be different from the report locale, resulting in different export font
		{
			String fontFamilyAttr = (String)value;
			String fontFamily = fontUtil.getExportFontFamily(fontFamilyAttr, locale, exporterKey);
			write("       <rPr>\n");
			write("        <rFont val=\"" + fontFamily + "\"/>\n");
			isOpen = true;
		}
		
		if (isOpen)
		{
			value = attrs.get(TextAttribute.FOREGROUND) == null 
					? parentAttrs.get(TextAttribute.FOREGROUND) 
					: attrs.get(TextAttribute.FOREGROUND);
			
			if (value != null)
			{
				write("        <color rgb=\"" + JRColorUtil.getColorHexa((Color)value) + "\" />\n");
			}
	
			
	//		highlighted text run is not allowed in Excel Spreadsheet ML
			
	//		value = attrs.get(TextAttribute.BACKGROUND);
	//		oldValue = parentAttrs.get(TextAttribute.BACKGROUND);
	//		
	//		
	//		if (value != null && (isOwnFont ||!value.equals(oldValue)))
	//		{
	//			String backcolor = ColorEnum.getByColor((Color)value) == null ? COLOR_NONE : ColorEnum.getByColor((Color)value).getName();
	//			if (backcolor != null){
	//				write("        <highlight val=\"" + backcolor + "\" />\n");
	//			}
	//		}
	
			value = attrs.get(TextAttribute.SIZE) == null 
					? parentAttrs.get(TextAttribute.SIZE) 
					: attrs.get(TextAttribute.SIZE);
	
			if (value != null)
			{
				write("        <sz val=\"" + value + "\" />\n");
			}
			
			value = attrs.get(TextAttribute.WEIGHT) == null 
					? parentAttrs.get(TextAttribute.WEIGHT) 
					: attrs.get(TextAttribute.WEIGHT);
	
			if (value != null)
			{
				write("        <b val=\"" + value.equals(TextAttribute.WEIGHT_BOLD) + "\"/>\n");
			}
	
			value = attrs.get(TextAttribute.POSTURE) == null 
					? parentAttrs.get(TextAttribute.POSTURE) 
					: attrs.get(TextAttribute.POSTURE);
	
			if (value != null)
			{
				write("        <i val=\"" + value.equals(TextAttribute.POSTURE_OBLIQUE) + "\"/>\n");
			}
		
			value = attrs.get(TextAttribute.UNDERLINE) == null 
					? parentAttrs.get(TextAttribute.UNDERLINE) 
					: attrs.get(TextAttribute.UNDERLINE);
	
			if (value != null)
			{
				write("        <u val=\"" + (value == null ? "none" : "single") + "\"/>\n");
			}
			
			value = attrs.get(TextAttribute.STRIKETHROUGH) == null 
					? parentAttrs.get(TextAttribute.STRIKETHROUGH) 
					: attrs.get(TextAttribute.STRIKETHROUGH);
	
			if (value != null)
			{
				write("        <strike val=\"" + (value != null) + "\"/>\n");
			}
		}
		
		value = attrs.get(TextAttribute.SUPERSCRIPT);

		if (TextAttribute.SUPERSCRIPT_SUPER.equals(value))
		{
			if (!isOpen)
			{
				write("       <rPr>\n");
				isOpen = true;
			}
			write("        <vertAlign val=\"superscript\" />\n");
		}
		else if (TextAttribute.SUPERSCRIPT_SUB.equals(value))
		{
			if (!isOpen)
			{
				write("       <rPr>\n");
				isOpen = true;
			}
			write("        <vertAlign val=\"subscript\" />\n");
		}
		
		if (isOpen)
		{
			write("       </rPr>\n");
		}
	}


	/**
	 *
	 */
	private Map<Attribute,Object> getAttributes(JRStyle style)//FIXMEDOCX put this in util?
	{
		JRPrintText text = new JRBasePrintText(null);
		text.setStyle(style);
		
		Map<Attribute,Object> styledTextAttributes = new HashMap<>(); 
		//JRFontUtil.getAttributes(styledTextAttributes, text, (Locale)null);//FIXMEDOCX getLocale());
		fontUtil.getAttributesWithoutAwtFont(styledTextAttributes, text);
		styledTextAttributes.put(TextAttribute.FOREGROUND, text.getForecolor());
		if (text.getMode() == ModeEnum.OPAQUE)
		{
			styledTextAttributes.put(TextAttribute.BACKGROUND, text.getBackcolor());
		}

		return styledTextAttributes;
	}

}


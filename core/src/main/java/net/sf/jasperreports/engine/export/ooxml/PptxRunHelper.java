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
import java.io.Writer;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

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
public class PptxRunHelper extends BaseHelper
{
	/**
	 *
	 */
	private final BaseFontHelper pptxFontHelper;
	
	/**
	 *
	 */
	public PptxRunHelper(
		JasperReportsContext jasperReportsContext, 
		Writer writer,
		BaseFontHelper pptxFontHelper
		)
	{
		super(jasperReportsContext, writer);
		this.pptxFontHelper = pptxFontHelper;
	}


	/**
	 *
	 */
	public void export(
		JRStyle style, 
		Map<Attribute,Object> attributes, 
		String text, 
		Locale locale, 
		String invalidCharReplacement
		)
	{
		if (text != null)
		{
			StringTokenizer tkzer = new StringTokenizer(text, "\n", true);
			while (tkzer.hasMoreTokens())
			{
				String token = tkzer.nextToken();
				if ("\n".equals(token))
				{
					write("<a:br/>");
				}
				else
				{
					write("      <a:r>\n");
					exportProps("a:rPr", getAttributes(style), attributes, locale);
					//write("<a:t xml:space=\"preserve\">");
					write("<a:t>");
					write(JRStringUtil.xmlEncode(token, invalidCharReplacement));//FIXMEODT try something nicer for replace
					write("</a:t>\n");
					write("      </a:r>\n");
				}
			}
		}
	}
	
	/**
	 *
	 */
	public void export(
		JRStyle style, 
		Map<Attribute,Object> attributes, 
		String text, 
		Locale locale, 
		String invalidCharReplacement,
		String fieldType,
		String uuid
		)
	{
		if (text != null)
		{
			StringTokenizer tkzer = new StringTokenizer(text, "\n", true);
			while (tkzer.hasMoreTokens())
			{
				String token = tkzer.nextToken();
				if ("\n".equals(token))
				{
					write("<a:br/>");
				}
				else
				{
					write("      <a:fld id=\"{"+ uuid +"}\" type=\"" + fieldType + "\">\n");
					exportProps("a:rPr", getAttributes(style), attributes, locale);
					write("<a:t>#</a:t>\n");
					write("      </a:fld>\n");
				}
			}
		}
	}

	/**
	 *
	 */
	public void exportProps(JRPrintText text, Locale locale)
	{
		Map<Attribute,Object> textAttributes = new HashMap<>(); 
		fontUtil.getAttributesWithoutAwtFont(textAttributes, text);
		textAttributes.put(TextAttribute.FOREGROUND, text.getForecolor());
		if (text.getMode() == null || text.getMode() == ModeEnum.OPAQUE)
		{
			textAttributes.put(TextAttribute.BACKGROUND, text.getBackcolor());
		}

		exportProps("a:defRPr", new HashMap<>(), textAttributes, locale);
	}

	/**
	 *
	 */
	private void exportProps(
		String tag, 
		Map<Attribute,Object> parentAttrs,  
		Map<Attribute,Object> attrs, 
		Locale locale
		)
	{
		write("       <" + tag + "\n");
		
		if (locale != null && "a:rPr".equals(tag))
		{
			write(" lang=\""+locale.getLanguage()+"\"\n");
		}

		Object value = attrs.get(TextAttribute.SIZE);
		Object oldValue = parentAttrs.get(TextAttribute.SIZE);

		if (value != null && !value.equals(oldValue))
		{
			float fontSize = (Float)value;
			fontSize = fontSize == 0 ? 0.5f : fontSize;// only the special EMPTY_CELL_STYLE would have font size zero
			write(" sz=\"" + (int)(100 * fontSize) + "\"");
		}
		else //FIXMEPPTX deal with default values from a style, a theme or something
		{
			float fontSize = (Float)oldValue;
			write(" sz=\"" + (int)(100 * fontSize) + "\"");
		}
		
		Object valueWeight = attrs.get(TextAttribute.WEIGHT);
		Object oldValueWeight = parentAttrs.get(TextAttribute.WEIGHT);

		if (valueWeight != null && !valueWeight.equals(oldValueWeight))
		{
			write(" b=\"" + (valueWeight.equals(TextAttribute.WEIGHT_BOLD) ? 1 : 0) + "\"");
		}

		Object valuePosture = attrs.get(TextAttribute.POSTURE);
		Object oldValuePosture = parentAttrs.get(TextAttribute.POSTURE);

		if (valuePosture != null && !valuePosture.equals(oldValuePosture))
		{
			write(" i=\"" + (valuePosture.equals(TextAttribute.POSTURE_OBLIQUE) ? 1 : 0) + "\"");
		}


		value = attrs.get(TextAttribute.UNDERLINE);
		oldValue = parentAttrs.get(TextAttribute.UNDERLINE);

		if (
			(value == null && oldValue != null)
			|| (value != null && !value.equals(oldValue))
			)
		{
			write(" u=\"" + (value == null ? "none" : "sng") + "\"");
		}
		
		value = attrs.get(TextAttribute.STRIKETHROUGH);
		oldValue = parentAttrs.get(TextAttribute.STRIKETHROUGH);

		if (
			(value == null && oldValue != null)
			|| (value != null && !value.equals(oldValue))
			)
		{
			write(" strike=\"" + (value == null ? "noStrike" : "sngStrike") + "\"");
		}

		value = attrs.get(TextAttribute.SUPERSCRIPT);

		if (TextAttribute.SUPERSCRIPT_SUPER.equals(value))
		{
			//default superscript position above baseline
			write(" baseline=\"30000\"");
		}
		else if (TextAttribute.SUPERSCRIPT_SUB.equals(value))
		{
			//default subscript position below baseline
			write(" baseline=\"-25000\"");
		}

		write(">\n");

		value = attrs.get(TextAttribute.FOREGROUND);
		oldValue = parentAttrs.get(TextAttribute.FOREGROUND);
		
		if (value != null && !value.equals(oldValue))
		{
			write("<a:solidFill><a:srgbClr val=\"" + JRColorUtil.getColorHexa((Color)value) + "\"/></a:solidFill>\n");
		}

		value = attrs.get(TextAttribute.BACKGROUND);
		oldValue = parentAttrs.get(TextAttribute.BACKGROUND);
		
//		if (value != null && !value.equals(oldValue))
//		{
//			write("<a:solidFill><a:srgbClr val=\"" + JRColorUtil.getColorHexa((Color)value) + "\"/></a:solidFill>\n");
//		}

//		Object valueFamily = attrs.get(TextAttribute.FAMILY);
//		Object oldValueFamily = parentAttrs.get(TextAttribute.FAMILY);
		
//		if (
//			pptxFontHelper.isEmbedFonts
//			|| (valueFamily != null && !valueFamily.equals(oldValueFamily))
//			|| (valueWeight != null && !valueWeight.equals(oldValueWeight))
//			|| (valuePosture != null && !valuePosture.equals(oldValuePosture))
//			)
//		{
			String fontName = pptxFontHelper.resolveFontFamily(attrs, locale);
			write("        <a:latin typeface=\"" + fontName + "\"/>\n");
			write("        <a:ea typeface=\"" + fontName + "\"/>\n");
			write("        <a:cs typeface=\"" + fontName + "\"/>\n");
//		}
		
		write("</" + tag + ">\n");
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


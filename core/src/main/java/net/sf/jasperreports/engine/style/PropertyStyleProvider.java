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
package net.sf.jasperreports.engine.style;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JREvaluation;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionChunk;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRPropertyExpression;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.base.JRBaseStyle;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;
import net.sf.jasperreports.engine.type.FillEnum;
import net.sf.jasperreports.engine.type.HorizontalImageAlignEnum;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.LineSpacingEnum;
import net.sf.jasperreports.engine.type.LineStyleEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.type.VerticalImageAlignEnum;
import net.sf.jasperreports.engine.type.VerticalTextAlignEnum;
import net.sf.jasperreports.engine.util.JRColorUtil;
import net.sf.jasperreports.engine.xml.JRXmlConstants;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * 
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class PropertyStyleProvider implements StyleProvider
{
	public static final String STYLE_PROPERTY_PREFIX = JRPropertiesUtil.PROPERTY_PREFIX + "style.";
	public static final String STYLE_PROPERTY_PEN_PREFIX = STYLE_PROPERTY_PREFIX + "pen.";
	public static final String STYLE_PROPERTY_BOX_PREFIX = STYLE_PROPERTY_PREFIX + "box.";
	public static final String STYLE_PROPERTY_BOX_PEN_PREFIX = STYLE_PROPERTY_BOX_PREFIX + "pen.";
	public static final String STYLE_PROPERTY_BOX_LEFT_PEN_PREFIX = STYLE_PROPERTY_BOX_PREFIX + "left.pen.";
	public static final String STYLE_PROPERTY_BOX_TOP_PEN_PREFIX = STYLE_PROPERTY_BOX_PREFIX + "top.pen.";
	public static final String STYLE_PROPERTY_BOX_RIGHT_PEN_PREFIX = STYLE_PROPERTY_BOX_PREFIX + "right.pen.";
	public static final String STYLE_PROPERTY_BOX_BOTTOM_PEN_PREFIX = STYLE_PROPERTY_BOX_PREFIX + "bottom.pen.";
	public static final String STYLE_PROPERTY_PARAGRAPH_PREFIX = STYLE_PROPERTY_PREFIX + "paragraph.";
	
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = ModeEnum.class
			)
	public static final String STYLE_PROPERTY_MODE = STYLE_PROPERTY_PREFIX + JRXmlConstants.ATTRIBUTE_mode;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Color.class
			)
	public static final String STYLE_PROPERTY_BACKCOLOR = STYLE_PROPERTY_PREFIX + JRXmlConstants.ATTRIBUTE_backcolor;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Color.class
			)
	public static final String STYLE_PROPERTY_FORECOLOR = STYLE_PROPERTY_PREFIX + JRXmlConstants.ATTRIBUTE_forecolor;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = FillEnum.class
			)
	public static final String STYLE_PROPERTY_FILL = STYLE_PROPERTY_PREFIX + JRXmlConstants.ATTRIBUTE_fill;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Integer.class
			)
	public static final String STYLE_PROPERTY_RADIUS = STYLE_PROPERTY_PREFIX + JRXmlConstants.ATTRIBUTE_radius;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = ScaleImageEnum.class
			)
	public static final String STYLE_PROPERTY_SCALE_IMAGE = STYLE_PROPERTY_PREFIX + JRXmlConstants.ATTRIBUTE_scaleImage;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_0_2,
			valueType = HorizontalTextAlignEnum.class
			)
	public static final String STYLE_PROPERTY_H_TEXT_ALIGN = STYLE_PROPERTY_PREFIX + JRXmlConstants.ATTRIBUTE_hTextAlign;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_0_2,
			valueType = HorizontalImageAlignEnum.class
			)
	public static final String STYLE_PROPERTY_H_IMAGE_ALIGN = STYLE_PROPERTY_PREFIX + JRXmlConstants.ATTRIBUTE_hImageAlign;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_0_2,
			valueType = VerticalTextAlignEnum.class
			)
	public static final String STYLE_PROPERTY_V_TEXT_ALIGN = STYLE_PROPERTY_PREFIX + JRXmlConstants.ATTRIBUTE_vTextAlign;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_0_2,
			valueType = VerticalImageAlignEnum.class
			)
	public static final String STYLE_PROPERTY_V_IMAGE_ALIGN = STYLE_PROPERTY_PREFIX + JRXmlConstants.ATTRIBUTE_vImageAlign;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = RotationEnum.class
			)
	public static final String STYLE_PROPERTY_ROTATION = STYLE_PROPERTY_PREFIX + JRXmlConstants.ATTRIBUTE_rotation;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0
			)
	public static final String STYLE_PROPERTY_MARKUP = STYLE_PROPERTY_PREFIX + JRXmlConstants.ATTRIBUTE_markup;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0
			)
	public static final String STYLE_PROPERTY_PATTERN = STYLE_PROPERTY_PREFIX + JRXmlConstants.ATTRIBUTE_pattern;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Boolean.class
			)
	public static final String STYLE_PROPERTY_BLANK_WHEN_NULL = STYLE_PROPERTY_PREFIX + JRXmlConstants.ATTRIBUTE_isBlankWhenNull;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0
			)
	public static final String STYLE_PROPERTY_FONT_NAME = STYLE_PROPERTY_PREFIX + JRXmlConstants.ATTRIBUTE_fontName;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Float.class
			)
	public static final String STYLE_PROPERTY_FONT_SIZE = STYLE_PROPERTY_PREFIX + JRXmlConstants.ATTRIBUTE_fontSize;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Boolean.class
			)
	public static final String STYLE_PROPERTY_FONT_BOLD = STYLE_PROPERTY_PREFIX + JRXmlConstants.ATTRIBUTE_isBold;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Boolean.class
			)
	public static final String STYLE_PROPERTY_FONT_ITALIC = STYLE_PROPERTY_PREFIX + JRXmlConstants.ATTRIBUTE_isItalic;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Boolean.class
			)
	public static final String STYLE_PROPERTY_FONT_UNDERLINE = STYLE_PROPERTY_PREFIX + JRXmlConstants.ATTRIBUTE_isUnderline;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Boolean.class
			)
	public static final String STYLE_PROPERTY_FONT_STRIKETHROUGH = STYLE_PROPERTY_PREFIX + JRXmlConstants.ATTRIBUTE_isStrikeThrough;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0
			)
	public static final String STYLE_PROPERTY_PDF_FONT_NAME = STYLE_PROPERTY_PREFIX + JRXmlConstants.ATTRIBUTE_pdfFontName;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0
			)
	public static final String STYLE_PROPERTY_PDF_ENCODING = STYLE_PROPERTY_PREFIX + JRXmlConstants.ATTRIBUTE_pdfEncoding;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Boolean.class
			)
	public static final String STYLE_PROPERTY_PDF_EMBEDDED = STYLE_PROPERTY_PREFIX + JRXmlConstants.ATTRIBUTE_isPdfEmbedded;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Float.class
			)
	public static final String STYLE_PROPERTY_PEN_LINE_WIDTH = STYLE_PROPERTY_PEN_PREFIX + JRXmlConstants.ATTRIBUTE_lineWidth;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = LineStyleEnum.class
			)
	public static final String STYLE_PROPERTY_PEN_LINE_STYLE = STYLE_PROPERTY_PEN_PREFIX + JRXmlConstants.ATTRIBUTE_lineStyle;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Color.class
			)
	public static final String STYLE_PROPERTY_PEN_LINE_COLOR = STYLE_PROPERTY_PEN_PREFIX + JRXmlConstants.ATTRIBUTE_lineColor;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Integer.class
			)
	public static final String STYLE_PROPERTY_BOX_PADDING = STYLE_PROPERTY_BOX_PREFIX + JRXmlConstants.ATTRIBUTE_padding;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Integer.class
			)
	public static final String STYLE_PROPERTY_BOX_TOP_PADDING = STYLE_PROPERTY_BOX_PREFIX + JRXmlConstants.ATTRIBUTE_topPadding;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Integer.class
			)
	public static final String STYLE_PROPERTY_BOX_LEFT_PADDING = STYLE_PROPERTY_BOX_PREFIX + JRXmlConstants.ATTRIBUTE_leftPadding;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Integer.class
			)
	public static final String STYLE_PROPERTY_BOX_RIGHT_PADDING = STYLE_PROPERTY_BOX_PREFIX + JRXmlConstants.ATTRIBUTE_rightPadding;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Integer.class
			)
	public static final String STYLE_PROPERTY_BOX_BOTTOM_PADDING = STYLE_PROPERTY_BOX_PREFIX + JRXmlConstants.ATTRIBUTE_bottomPadding;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Float.class
			)
	public static final String STYLE_PROPERTY_BOX_PEN_LINE_WIDTH = STYLE_PROPERTY_BOX_PEN_PREFIX + JRXmlConstants.ATTRIBUTE_lineWidth;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = LineStyleEnum.class
			)
	public static final String STYLE_PROPERTY_BOX_PEN_LINE_STYLE = STYLE_PROPERTY_BOX_PEN_PREFIX + JRXmlConstants.ATTRIBUTE_lineStyle;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Color.class
			)
	public static final String STYLE_PROPERTY_BOX_PEN_LINE_COLOR = STYLE_PROPERTY_BOX_PEN_PREFIX + JRXmlConstants.ATTRIBUTE_lineColor;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Float.class
			)
	public static final String STYLE_PROPERTY_BOX_LEFT_PEN_LINE_WIDTH = STYLE_PROPERTY_BOX_LEFT_PEN_PREFIX + JRXmlConstants.ATTRIBUTE_lineWidth;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = LineStyleEnum.class
			)
	public static final String STYLE_PROPERTY_BOX_LEFT_PEN_LINE_STYLE = STYLE_PROPERTY_BOX_LEFT_PEN_PREFIX + JRXmlConstants.ATTRIBUTE_lineStyle;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Color.class
			)
	public static final String STYLE_PROPERTY_BOX_LEFT_PEN_LINE_COLOR = STYLE_PROPERTY_BOX_LEFT_PEN_PREFIX + JRXmlConstants.ATTRIBUTE_lineColor;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Float.class
			)
	public static final String STYLE_PROPERTY_BOX_TOP_PEN_LINE_WIDTH = STYLE_PROPERTY_BOX_TOP_PEN_PREFIX + JRXmlConstants.ATTRIBUTE_lineWidth;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = LineStyleEnum.class
			)
	public static final String STYLE_PROPERTY_BOX_TOP_PEN_LINE_STYLE = STYLE_PROPERTY_BOX_TOP_PEN_PREFIX + JRXmlConstants.ATTRIBUTE_lineStyle;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Color.class
			)
	public static final String STYLE_PROPERTY_BOX_TOP_PEN_LINE_COLOR = STYLE_PROPERTY_BOX_TOP_PEN_PREFIX + JRXmlConstants.ATTRIBUTE_lineColor;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Float.class
			)
	public static final String STYLE_PROPERTY_BOX_RIGHT_PEN_LINE_WIDTH = STYLE_PROPERTY_BOX_RIGHT_PEN_PREFIX + JRXmlConstants.ATTRIBUTE_lineWidth;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = LineStyleEnum.class
			)
	public static final String STYLE_PROPERTY_BOX_RIGHT_PEN_LINE_STYLE = STYLE_PROPERTY_BOX_RIGHT_PEN_PREFIX + JRXmlConstants.ATTRIBUTE_lineStyle;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Color.class
			)
	public static final String STYLE_PROPERTY_BOX_RIGHT_PEN_LINE_COLOR = STYLE_PROPERTY_BOX_RIGHT_PEN_PREFIX + JRXmlConstants.ATTRIBUTE_lineColor;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Float.class
			)
	public static final String STYLE_PROPERTY_BOX_BOTTOM_PEN_LINE_WIDTH = STYLE_PROPERTY_BOX_BOTTOM_PEN_PREFIX + JRXmlConstants.ATTRIBUTE_lineWidth;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = LineStyleEnum.class
			)
	public static final String STYLE_PROPERTY_BOX_BOTTOM_PEN_LINE_STYLE = STYLE_PROPERTY_BOX_BOTTOM_PEN_PREFIX + JRXmlConstants.ATTRIBUTE_lineStyle;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Color.class
			)
	public static final String STYLE_PROPERTY_BOX_BOTTOM_PEN_LINE_COLOR = STYLE_PROPERTY_BOX_BOTTOM_PEN_PREFIX + JRXmlConstants.ATTRIBUTE_lineColor;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = LineSpacingEnum.class
			)
	public static final String STYLE_PROPERTY_LINE_SPACING = STYLE_PROPERTY_PARAGRAPH_PREFIX + JRXmlConstants.ATTRIBUTE_lineSpacing;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Float.class
			)
	public static final String STYLE_PROPERTY_LINE_SPACING_SIZE = STYLE_PROPERTY_PARAGRAPH_PREFIX + JRXmlConstants.ATTRIBUTE_lineSpacingSize;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Integer.class
			)
	public static final String STYLE_PROPERTY_FIRST_LINE_INDENT = STYLE_PROPERTY_PARAGRAPH_PREFIX + JRXmlConstants.ATTRIBUTE_firstLineIndent;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Integer.class
			)
	public static final String STYLE_PROPERTY_LEFT_INDENT = STYLE_PROPERTY_PARAGRAPH_PREFIX + JRXmlConstants.ATTRIBUTE_leftIndent;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Integer.class
			)
	public static final String STYLE_PROPERTY_RIGHT_INDENT = STYLE_PROPERTY_PARAGRAPH_PREFIX + JRXmlConstants.ATTRIBUTE_rightIndent;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Integer.class
			)
	public static final String STYLE_PROPERTY_SPACING_BEFORE = STYLE_PROPERTY_PARAGRAPH_PREFIX + JRXmlConstants.ATTRIBUTE_spacingBefore;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Integer.class
			)
	public static final String STYLE_PROPERTY_SPACING_AFTER = STYLE_PROPERTY_PARAGRAPH_PREFIX + JRXmlConstants.ATTRIBUTE_spacingAfter;
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_8_0,
			valueType = Integer.class
			)
	public static final String STYLE_PROPERTY_TABSTOP_WIDTH = STYLE_PROPERTY_PARAGRAPH_PREFIX + JRXmlConstants.ATTRIBUTE_tabStopWidth;

	private final StyleProviderContext context;
	
	private final Map<String, JRPropertyExpression> stylePropertyExpressions;
	private final String[] fields;
	private final String[] variables;
	private final boolean lateEvaluated;
	
	public PropertyStyleProvider(
		StyleProviderContext context, 
		Map<String, JRPropertyExpression> stylePropertyExpressions
		)
	{
		this.context = context;
		this.stylePropertyExpressions = stylePropertyExpressions;
		
		List<String> fieldsList = new ArrayList<>();
		List<String> variablesList = new ArrayList<>();
		
		if (stylePropertyExpressions != null)
		{
			for (JRPropertyExpression stylePropertyExpression : stylePropertyExpressions.values())
			{
				JRExpression expression = stylePropertyExpression.getValueExpression();
				if (expression != null)
				{
					JRExpressionChunk[] chunks = expression.getChunks();
					if (chunks != null)
					{
						for (int i = 0; i < chunks.length; i++)
						{
							JRExpressionChunk chunk = chunks[i];
							switch (chunk.getType())
							{
								case JRExpressionChunk.TYPE_FIELD:
								{
									fieldsList.add(chunk.getText());
									break;
								}
								case JRExpressionChunk.TYPE_VARIABLE:
								{
									variablesList.add(chunk.getText());
									break;
								}
								default:
							}
						}
					}
				}
			}
		}
		
		fields = fieldsList.size() > 0 ? (String[]) fieldsList.toArray(new String[fieldsList.size()]) : null;
		variables = variablesList.size() > 0 ? (String[]) variablesList.toArray(new String[variablesList.size()]) : null;

		JRElement element = context.getElement();
		JREvaluation evaluation = element instanceof JREvaluation ? (JREvaluation)element : null;
		lateEvaluated = evaluation != null && EvaluationTimeEnum.getValueOrDefault(evaluation.getEvaluationTime()) != EvaluationTimeEnum.NOW;
	}

	@Override
	public JRStyle getStyle(byte evaluation) 
	{
		JRStyle style = new JRBaseStyle();
		
		String mode = getPropertyValue(STYLE_PROPERTY_MODE, evaluation);
		if (mode != null)
		{
			style.setMode(ModeEnum.getByName(mode));
		}

		String backcolor = getPropertyValue(STYLE_PROPERTY_BACKCOLOR, evaluation);
		if (backcolor != null)
		{
			style.setBackcolor(JRColorUtil.getColor(backcolor, null));
		}

		String forecolor = getPropertyValue(STYLE_PROPERTY_FORECOLOR, evaluation);
		if (forecolor != null)
		{
			style.setForecolor(JRColorUtil.getColor(forecolor, null));
		}
		
		String fill = getPropertyValue(STYLE_PROPERTY_FILL, evaluation);
		if (fill != null)
		{
			style.setFill(FillEnum.getByName(fill));
		}
		
		String radius = getPropertyValue(STYLE_PROPERTY_RADIUS, evaluation);
		if (radius != null)
		{
			style.setRadius(Integer.valueOf(radius));
		}

		String scaleImage = getPropertyValue(STYLE_PROPERTY_SCALE_IMAGE, evaluation);
		if (scaleImage != null)
		{
			style.setScaleImage(ScaleImageEnum.getByName(scaleImage));
		}
		
		String hAlign = getPropertyValue(STYLE_PROPERTY_H_TEXT_ALIGN, evaluation);
		if (hAlign != null)
		{
			style.setHorizontalTextAlign(HorizontalTextAlignEnum.getByName(hAlign));
		}
		hAlign = getPropertyValue(STYLE_PROPERTY_H_IMAGE_ALIGN, evaluation);
		if (hAlign != null)
		{
			style.setHorizontalImageAlign(HorizontalImageAlignEnum.getByName(hAlign));
		}
		
		String vAlign = getPropertyValue(STYLE_PROPERTY_V_TEXT_ALIGN, evaluation);
		if (vAlign != null)
		{
			style.setVerticalTextAlign(VerticalTextAlignEnum.getByName(vAlign));
		}
		vAlign = getPropertyValue(STYLE_PROPERTY_V_IMAGE_ALIGN, evaluation);
		if (vAlign != null)
		{
			style.setVerticalImageAlign(VerticalImageAlignEnum.getByName(vAlign));
		}
		
		String rotation = getPropertyValue(STYLE_PROPERTY_ROTATION, evaluation);
		if (rotation != null)
		{
			style.setRotation(RotationEnum.getByName(rotation));
		}
		
		String markup = getPropertyValue(STYLE_PROPERTY_MARKUP, evaluation);
		if (markup != null)
		{
			style.setMarkup(markup);
		}
		
		String pattern = getPropertyValue(STYLE_PROPERTY_PATTERN, evaluation);
		if (pattern != null)
		{
			style.setPattern(pattern);
		}
		
		String blankWhenNull = getPropertyValue(STYLE_PROPERTY_BLANK_WHEN_NULL, evaluation);
		if (blankWhenNull != null)
		{
			style.setBlankWhenNull(Boolean.valueOf(blankWhenNull));
		}

		String fontName = getPropertyValue(STYLE_PROPERTY_FONT_NAME, evaluation);
		if (fontName != null)
		{
			style.setFontName(fontName);
		}
		
		String fontSize = getPropertyValue(STYLE_PROPERTY_FONT_SIZE, evaluation);
		if (fontSize != null)
		{
			style.setFontSize(Float.valueOf(fontSize));
		}

		String bold = getPropertyValue(STYLE_PROPERTY_FONT_BOLD, evaluation);
		if (bold != null)
		{
			style.setBold(Boolean.valueOf(bold));
		}
		
		String italic = getPropertyValue(STYLE_PROPERTY_FONT_ITALIC, evaluation);
		if (italic != null)
		{
			style.setItalic(Boolean.valueOf(italic));
		}
		
		String underline = getPropertyValue(STYLE_PROPERTY_FONT_UNDERLINE, evaluation);
		if (underline != null)
		{
			style.setUnderline(Boolean.valueOf(underline));
		}
		
		String strikethrough = getPropertyValue(STYLE_PROPERTY_FONT_STRIKETHROUGH, evaluation);
		if (strikethrough != null)
		{
			style.setStrikeThrough(Boolean.valueOf(strikethrough));
		}

		String pdfFontName = getPropertyValue(STYLE_PROPERTY_PDF_FONT_NAME, evaluation);
		if (pdfFontName != null)
		{
			style.setPdfFontName(pdfFontName);
		}
		
		String pdfEncoding = getPropertyValue(STYLE_PROPERTY_PDF_ENCODING, evaluation);
		if (pdfEncoding != null)
		{
			style.setPdfEncoding(pdfEncoding);
		}
		
		String pdfEmbedded = getPropertyValue(STYLE_PROPERTY_PDF_EMBEDDED, evaluation);
		if (pdfEmbedded != null)
		{
			style.setPdfEmbedded(Boolean.valueOf(pdfEmbedded));
		}
		
		String penLineWidth = getPropertyValue(STYLE_PROPERTY_PEN_LINE_WIDTH, evaluation);
		if (penLineWidth != null)
		{
			style.getLinePen().setLineWidth(Float.valueOf(penLineWidth));
		}
		
		String penLineStyle = getPropertyValue(STYLE_PROPERTY_PEN_LINE_STYLE, evaluation);
		if (penLineStyle != null)
		{
			style.getLinePen().setLineStyle(LineStyleEnum.getByName(penLineStyle));
		}
		
		String penLineColor = getPropertyValue(STYLE_PROPERTY_PEN_LINE_COLOR, evaluation);
		if (penLineColor != null)
		{
			style.getLinePen().setLineColor(JRColorUtil.getColor(penLineColor, null));
		}
		
		String boxPadding = getPropertyValue(STYLE_PROPERTY_BOX_PADDING, evaluation);
		if (boxPadding != null)
		{
			style.getLineBox().setPadding(Integer.valueOf(boxPadding));
		}
		
		String boxLeftPadding = getPropertyValue(STYLE_PROPERTY_BOX_LEFT_PADDING, evaluation);
		if (boxLeftPadding != null)
		{
			style.getLineBox().setLeftPadding(Integer.valueOf(boxLeftPadding));
		}
		
		String boxTopPadding = getPropertyValue(STYLE_PROPERTY_BOX_TOP_PADDING, evaluation);
		if (boxTopPadding != null)
		{
			style.getLineBox().setTopPadding(Integer.valueOf(boxTopPadding));
		}
		
		String boxRightPadding = getPropertyValue(STYLE_PROPERTY_BOX_RIGHT_PADDING, evaluation);
		if (boxRightPadding != null)
		{
			style.getLineBox().setRightPadding(Integer.valueOf(boxRightPadding));
		}
		
		String boxBottomPadding = getPropertyValue(STYLE_PROPERTY_BOX_BOTTOM_PADDING, evaluation);
		if (boxBottomPadding != null)
		{
			style.getLineBox().setBottomPadding(Integer.valueOf(boxBottomPadding));
		}
		
		String boxPenLineWidth = getPropertyValue(STYLE_PROPERTY_BOX_PEN_LINE_WIDTH, evaluation);
		if (boxPenLineWidth != null)
		{
			style.getLineBox().getPen().setLineWidth(Float.valueOf(boxPenLineWidth));
		}
		
		String boxPenLineStyle = getPropertyValue(STYLE_PROPERTY_BOX_PEN_LINE_STYLE, evaluation);
		if (boxPenLineStyle != null)
		{
			style.getLineBox().getPen().setLineStyle(LineStyleEnum.getByName(boxPenLineStyle));
		}
		
		String boxPenLineColor = getPropertyValue(STYLE_PROPERTY_BOX_PEN_LINE_COLOR, evaluation);
		if (boxPenLineColor != null)
		{
			style.getLineBox().getPen().setLineColor(JRColorUtil.getColor(boxPenLineColor, null));
		}
		
		String boxLeftPenLineWidth = getPropertyValue(STYLE_PROPERTY_BOX_LEFT_PEN_LINE_WIDTH, evaluation);
		if (boxLeftPenLineWidth != null)
		{
			style.getLineBox().getLeftPen().setLineWidth(Float.valueOf(boxLeftPenLineWidth));
		}

		String boxLeftPenLineStyle = getPropertyValue(STYLE_PROPERTY_BOX_LEFT_PEN_LINE_STYLE, evaluation);
		if (boxLeftPenLineStyle != null)
		{
			style.getLineBox().getLeftPen().setLineStyle(LineStyleEnum.getByName(boxLeftPenLineStyle));
		}
		
		String boxLeftPenLineColor = getPropertyValue(STYLE_PROPERTY_BOX_LEFT_PEN_LINE_COLOR, evaluation);
		if (boxLeftPenLineColor != null)
		{
			style.getLineBox().getLeftPen().setLineColor(JRColorUtil.getColor(boxLeftPenLineColor, null));
		}
		
		String boxTopPenLineWidth = getPropertyValue(STYLE_PROPERTY_BOX_TOP_PEN_LINE_WIDTH, evaluation);
		if (boxTopPenLineWidth != null)
		{
			style.getLineBox().getTopPen().setLineWidth(Float.valueOf(boxTopPenLineWidth));
		}
		
		String boxTopPenLineStyle = getPropertyValue(STYLE_PROPERTY_BOX_TOP_PEN_LINE_STYLE, evaluation);
		if (boxTopPenLineStyle != null)
		{
			style.getLineBox().getTopPen().setLineStyle(LineStyleEnum.getByName(boxTopPenLineStyle));
		}
		
		String boxTopPenLineColor = getPropertyValue(STYLE_PROPERTY_BOX_TOP_PEN_LINE_COLOR, evaluation);
		if (boxTopPenLineColor != null)
		{
			style.getLineBox().getTopPen().setLineColor(JRColorUtil.getColor(boxTopPenLineColor, null));
		}
		
		String boxRightPenLineWidth = getPropertyValue(STYLE_PROPERTY_BOX_RIGHT_PEN_LINE_WIDTH, evaluation);
		if (boxRightPenLineWidth != null)
		{
			style.getLineBox().getRightPen().setLineWidth(Float.valueOf(boxRightPenLineWidth));
		}
		
		String boxRightPenLineStyle = getPropertyValue(STYLE_PROPERTY_BOX_RIGHT_PEN_LINE_STYLE, evaluation);
		if (boxRightPenLineStyle != null)
		{
			style.getLineBox().getRightPen().setLineStyle(LineStyleEnum.getByName(boxRightPenLineStyle));
		}
		
		String boxRightPenLineColor = getPropertyValue(STYLE_PROPERTY_BOX_RIGHT_PEN_LINE_COLOR, evaluation);
		if (boxRightPenLineColor != null)
		{
			style.getLineBox().getRightPen().setLineColor(JRColorUtil.getColor(boxRightPenLineColor, null));
		}
		
		String boxBottomPenLineWidth = getPropertyValue(STYLE_PROPERTY_BOX_BOTTOM_PEN_LINE_WIDTH, evaluation);
		if (boxBottomPenLineWidth != null)
		{
			style.getLineBox().getBottomPen().setLineWidth(Float.valueOf(boxBottomPenLineWidth));
		}
		
		String boxBottomPenLineStyle = getPropertyValue(STYLE_PROPERTY_BOX_BOTTOM_PEN_LINE_STYLE, evaluation);
		if (boxBottomPenLineStyle != null)
		{
			style.getLineBox().getBottomPen().setLineStyle(LineStyleEnum.getByName(boxBottomPenLineStyle));
		}
		
		String boxBottomPenLineColor = getPropertyValue(STYLE_PROPERTY_BOX_BOTTOM_PEN_LINE_COLOR, evaluation);
		if (boxBottomPenLineColor != null)
		{
			style.getLineBox().getBottomPen().setLineColor(JRColorUtil.getColor(boxBottomPenLineColor, null));
		}
		
		String lineSpacing = getPropertyValue(STYLE_PROPERTY_LINE_SPACING, evaluation);
		if (lineSpacing != null)
		{
			style.getParagraph().setLineSpacing(LineSpacingEnum.getByName(lineSpacing));
		}
		
		String lineSpacingSize = getPropertyValue(STYLE_PROPERTY_LINE_SPACING_SIZE, evaluation);
		if (lineSpacingSize != null)
		{
			style.getParagraph().setLineSpacingSize(Float.valueOf(lineSpacingSize));
		}
		
		String firstLineIndent = getPropertyValue(STYLE_PROPERTY_FIRST_LINE_INDENT, evaluation);
		if (firstLineIndent != null)
		{
			style.getParagraph().setFirstLineIndent(Integer.valueOf(firstLineIndent));
		}
		
		String leftIndent = getPropertyValue(STYLE_PROPERTY_LEFT_INDENT, evaluation);
		if (leftIndent != null)
		{
			style.getParagraph().setLeftIndent(Integer.valueOf(leftIndent));
		}
		
		String rightIndent = getPropertyValue(STYLE_PROPERTY_RIGHT_INDENT, evaluation);
		if (rightIndent != null)
		{
			style.getParagraph().setRightIndent(Integer.valueOf(rightIndent));
		}
		
		String spacingBefore = getPropertyValue(STYLE_PROPERTY_SPACING_BEFORE, evaluation);
		if (spacingBefore != null)
		{
			style.getParagraph().setSpacingBefore(Integer.valueOf(spacingBefore));
		}
		
		String spacingAfter = getPropertyValue(STYLE_PROPERTY_SPACING_AFTER, evaluation);
		if (spacingAfter != null)
		{
			style.getParagraph().setSpacingAfter(Integer.valueOf(spacingAfter));
		}
		
		String tabStopWidth = getPropertyValue(STYLE_PROPERTY_TABSTOP_WIDTH, evaluation);
		if (tabStopWidth != null)
		{
			style.getParagraph().setTabStopWidth(Integer.valueOf(spacingAfter));
		}
		
		//TODO: what about tabstops?

		return style;
	}

	@Override
	public String[] getFields() 
	{
		return fields;
	}

	@Override
	public String[] getVariables() 
	{
		return variables;
	}

	private String getPropertyValue(String propertyName, byte evaluation) 
	{
		String value = null;

		if (lateEvaluated && stylePropertyExpressions != null && stylePropertyExpressions.containsKey(propertyName))
		{
			JRPropertyExpression stylePropertyExpression = stylePropertyExpressions.get(propertyName);
			JRExpression expression = stylePropertyExpression.getValueExpression();
			if (expression != null)
			{
				value = (String) context.evaluateExpression(expression, evaluation);
			}
		}
		else
		{
			value = context.getElement().getPropertiesMap().getProperty(propertyName);
		}
		
		return value;
	}

}

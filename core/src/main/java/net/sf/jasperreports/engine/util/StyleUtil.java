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
package net.sf.jasperreports.engine.util;

import net.sf.jasperreports.engine.JRBoxContainer;
import net.sf.jasperreports.engine.JRConditionalStyle;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRParagraph;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JRStyleContainer;
import net.sf.jasperreports.engine.TabStop;
import net.sf.jasperreports.engine.base.JRBaseStyle;
import net.sf.jasperreports.engine.base.JRBoxPen;
import net.sf.jasperreports.engine.type.ModeEnum;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public final class StyleUtil
{

	private static final StyleUtil INSTANCE = new StyleUtil();
	
	public static StyleUtil instance()
	{
		return INSTANCE;
	}
	
	private StyleUtil()
	{
	}
	
	public ModeEnum resolveMode(JRStyleContainer styleContainer)
	{
		JRStyle style = styleContainer.getStyle();
		if (style != null)
		{
			ModeEnum styleMode = style.getOwnMode();
			if (styleMode != null)
			{
				JRConditionalStyle[] conditionalStyles = style.getConditionalStyles();
				if (conditionalStyles != null)
				{
					for (JRConditionalStyle conditionalStyle : conditionalStyles)
					{
						ModeEnum conditionalMode = conditionalStyle.getOwnMode();
						if (conditionalMode != null && conditionalMode != styleMode)
						{
							// a conditional style overrides the style mode
							return null;
						}
					}
				}
				
				// we have a style
				return styleMode;
			}
			
			// going to the parent
			return resolveMode(style);
		}
		
		if (styleContainer.getStyleNameReference() != null)
		{
			// we can't resolve external style references here
			return null;
		}
		
		// if no style set, default
		return ModeEnum.TRANSPARENT;
	}
	
	public ModeEnum resolveElementMode(JRElement element)
	{
		ModeEnum elementMode = element.getOwnMode();
		if (elementMode != null)
		{
			return elementMode;
		}
		
		return resolveMode(element);
	}
	
	private interface BoxSideSelector
	{
		JRBoxPen getPen(JRLineBox lineBox);
		
		Integer getPadding(JRLineBox lineBox);
	}
	
	private static final BoxSideSelector RIGHT_SIDE = new BoxSideSelector()
	{
		@Override
		public JRBoxPen getPen(JRLineBox lineBox)
		{
			return lineBox.getRightPen();
		}

		@Override
		public Integer getPadding(JRLineBox lineBox)
		{
			return lineBox.getOwnRightPadding();
		}
	};
	
	private static final BoxSideSelector TOP_SIDE = new BoxSideSelector()
	{
		@Override
		public JRBoxPen getPen(JRLineBox lineBox)
		{
			return lineBox.getTopPen();
		}

		@Override
		public Integer getPadding(JRLineBox lineBox)
		{
			return lineBox.getOwnTopPadding();
		}
	};
	
	private static final BoxSideSelector LEFT_SIDE = new BoxSideSelector()
	{
		@Override
		public JRBoxPen getPen(JRLineBox lineBox)
		{
			return lineBox.getLeftPen();
		}

		@Override
		public Integer getPadding(JRLineBox lineBox)
		{
			return lineBox.getOwnLeftPadding();
		}
	};
	
	private static final BoxSideSelector BOTTOM_SIDE = new BoxSideSelector()
	{
		@Override
		public JRBoxPen getPen(JRLineBox lineBox)
		{
			return lineBox.getBottomPen();
		}

		@Override
		public Integer getPadding(JRLineBox lineBox)
		{
			return lineBox.getOwnBottomPadding();
		}
	};

	public boolean hasBox(JRBoxContainer boxContainer)
	{
		return hasBorder(boxContainer, RIGHT_SIDE)
				|| hasBorder(boxContainer, TOP_SIDE)
				|| hasBorder(boxContainer, LEFT_SIDE)
				|| hasBorder(boxContainer, BOTTOM_SIDE)
				|| hasPadding(boxContainer, RIGHT_SIDE)
				|| hasPadding(boxContainer, TOP_SIDE)
				|| hasPadding(boxContainer, LEFT_SIDE)
				|| hasPadding(boxContainer, BOTTOM_SIDE);
	}
	
	protected boolean hasBorder(JRBoxContainer boxContainer, BoxSideSelector selector)
	{
		JRLineBox lineBox = boxContainer.getLineBox();
		Float rightLineWidth = selector.getPen(lineBox).getOwnLineWidth();
		if (rightLineWidth != null)
		{
			return rightLineWidth > .0f;
		}
		
		Float lineWidth = lineBox.getPen().getOwnLineWidth();
		if (lineWidth != null)
		{
			return lineWidth > .0f;
		}
		
		JRStyle style = boxContainer.getStyle();
		if (style != null)
		{
			return hasBorder(style, selector);
		}
		
		String styleReference = boxContainer.getStyleNameReference();
		if (styleReference != null)
		{
			// we can't resolve the style reference, return pessimistically
			return true;
		}
		
		return false;
	}
	
	protected boolean hasPadding(JRBoxContainer boxContainer, BoxSideSelector penSelector)
	{
		JRLineBox lineBox = boxContainer.getLineBox();
		Integer sidePadding = penSelector.getPadding(lineBox);
		if (sidePadding != null)
		{
			return sidePadding > 0;
		}
		
		Integer padding = lineBox.getOwnPadding();
		if (padding != null)
		{
			return padding > 0;
		}
		
		JRStyle style = boxContainer.getStyle();
		if (style != null)
		{
			return hasPadding(style, penSelector);
		}
		
		String styleReference = boxContainer.getStyleNameReference();
		if (styleReference != null)
		{
			// we can't resolve the style reference, return pessimistically
			return true;
		}
		
		return false;
	}

	/**
	 * Merges two styles, by appending the properties of the source style to the ones of the destination style.
	 */
	public static void appendStyle(JRStyle destStyle, JRStyle srcStyle)
	{
		if (srcStyle.getOwnMode() != null)
		{
			destStyle.setMode(srcStyle.getOwnMode());
		}
		if (srcStyle.getOwnForecolor() != null)
		{
			destStyle.setForecolor(srcStyle.getOwnForecolor());
		}
		if (srcStyle.getOwnBackcolor() != null)
		{
			destStyle.setBackcolor(srcStyle.getOwnBackcolor());
		}
		appendPen(destStyle.getLinePen(), srcStyle.getLinePen());
		
		if (srcStyle.getOwnFill() != null)
		{
			destStyle.setFill(srcStyle.getOwnFill());
		}
		if (srcStyle.getOwnRadius() != null)
		{
			destStyle.setRadius(srcStyle.getOwnRadius());
		}
		if (srcStyle.getOwnScaleImage() != null)
		{
			destStyle.setScaleImage(srcStyle.getOwnScaleImage());
		}
		if (srcStyle.getOwnHorizontalTextAlign() != null)
		{
			destStyle.setHorizontalTextAlign(srcStyle.getOwnHorizontalTextAlign());
		}
		if (srcStyle.getOwnHorizontalImageAlign() != null)
		{
			destStyle.setHorizontalImageAlign(srcStyle.getOwnHorizontalImageAlign());
		}
		if (srcStyle.getOwnVerticalTextAlign() != null)
		{
			destStyle.setVerticalTextAlign(srcStyle.getOwnVerticalTextAlign());
		}
		if (srcStyle.getOwnVerticalImageAlign() != null)
		{
			destStyle.setVerticalImageAlign(srcStyle.getOwnVerticalImageAlign());
		}
		appendBox(destStyle.getLineBox(), srcStyle.getLineBox());
		appendParagraph(destStyle.getParagraph(), srcStyle.getParagraph());

		if (srcStyle.getOwnRotation() != null)
		{
			destStyle.setRotation(srcStyle.getOwnRotation());
		}
		if (srcStyle.getOwnMarkup() != null)
		{
			destStyle.setMarkup(srcStyle.getOwnMarkup());
		}
		if (srcStyle.getOwnPattern() != null)
		{
			destStyle.setPattern(srcStyle.getOwnPattern());
		}
		if (srcStyle.getOwnFontName() != null)
		{
			destStyle.setFontName(srcStyle.getOwnFontName());
		}
		if (srcStyle.isOwnBold() != null)
		{
			destStyle.setBold(srcStyle.isOwnBold());
		}
		if (srcStyle.isOwnItalic() != null)
		{
			destStyle.setItalic(srcStyle.isOwnItalic());
		}
		if (srcStyle.isOwnUnderline() != null)
		{
			destStyle.setUnderline(srcStyle.isOwnUnderline());
		}
		if (srcStyle.isOwnStrikeThrough() != null)
		{
			destStyle.setStrikeThrough(srcStyle.isOwnStrikeThrough());
		}
		if (srcStyle.getOwnFontSize() != null)
		{
			destStyle.setFontSize(srcStyle.getOwnFontSize());
		}
		if (srcStyle.getOwnPdfFontName() != null)
		{
			destStyle.setPdfFontName(srcStyle.getOwnPdfFontName());
		}
		if (srcStyle.getOwnPdfEncoding() != null)
		{
			destStyle.setPdfEncoding(srcStyle.getOwnPdfEncoding());
		}
		if (srcStyle.isOwnPdfEmbedded() != null)
		{
			destStyle.setPdfEmbedded(srcStyle.isOwnPdfEmbedded());
		}
		
		if (srcStyle.isOwnBlankWhenNull() != null)
		{
			destStyle.setBlankWhenNull(srcStyle.isOwnBlankWhenNull());
		}
	}

	/**
	 * Merges two pens, by appending the properties of the source pen to the ones of the destination pen.
	 */
	public static void appendPen(JRPen destPen, JRPen srcPen)
	{
		if (srcPen.getOwnLineWidth() != null)
		{
			destPen.setLineWidth(srcPen.getOwnLineWidth());
		}
		if (srcPen.getOwnLineStyle() != null)
		{
			destPen.setLineStyle(srcPen.getOwnLineStyle());
		}
		if (srcPen.getOwnLineColor() != null)
		{
			destPen.setLineColor(srcPen.getOwnLineColor());
		}
	}

	/**
	 * Merges two boxes, by appending the properties of the source box to the ones of the destination box.
	 */
	public static void appendBox(JRLineBox destBox, JRLineBox srcBox)
	{
		appendPen(destBox.getPen(), srcBox.getPen());
		appendPen(destBox.getTopPen(), srcBox.getTopPen());
		appendPen(destBox.getLeftPen(), srcBox.getLeftPen());
		appendPen(destBox.getBottomPen(), srcBox.getBottomPen());
		appendPen(destBox.getRightPen(), srcBox.getRightPen());

		if (srcBox.getOwnPadding() != null)
		{
			destBox.setPadding(srcBox.getOwnPadding());
		}
		if (srcBox.getOwnTopPadding() != null)
		{
			destBox.setTopPadding(srcBox.getOwnTopPadding());
		}
		if (srcBox.getOwnLeftPadding() != null)
		{
			destBox.setLeftPadding(srcBox.getOwnLeftPadding());
		}
		if (srcBox.getOwnBottomPadding() != null)
		{
			destBox.setBottomPadding(srcBox.getOwnBottomPadding());
		}
		if (srcBox.getOwnRightPadding() != null)
		{
			destBox.setRightPadding(srcBox.getOwnRightPadding());
		}
	}

	/**
	 * Merges two paragraphs, by appending the properties of the source paragraph to the ones of the destination paragraph.
	 */
	public static void appendParagraph(JRParagraph destParagraph, JRParagraph srcParagraph)
	{
		if (srcParagraph.getOwnLineSpacing() != null)
		{
			destParagraph.setLineSpacing(srcParagraph.getOwnLineSpacing());
		}
		if (srcParagraph.getOwnLeftIndent() != null)
		{
			destParagraph.setLeftIndent(srcParagraph.getOwnLeftIndent());
		}
		if (srcParagraph.getOwnRightIndent() != null)
		{
			destParagraph.setRightIndent(srcParagraph.getOwnRightIndent());
		}
		if (srcParagraph.getOwnSpacingBefore() != null)
		{
			destParagraph.setSpacingBefore(srcParagraph.getOwnSpacingBefore());
		}
		if (srcParagraph.getOwnSpacingAfter() != null)
		{
			destParagraph.setSpacingAfter(srcParagraph.getOwnSpacingAfter());
		}
		if (srcParagraph.getOwnTabStopWidth() != null)
		{
			destParagraph.setTabStopWidth(srcParagraph.getOwnTabStopWidth());
		}
	}

	/**
	 * Clones a style and scales its dimensional properties from the source DPI to the target DPI.
	 * Non-dimensional properties are left unchanged.
	 */
	public static JRStyle scaleDpiStyle(JRStyle style, int sourceDpi, int targetDpi)
	{
		JRBaseStyle scaled = (JRBaseStyle)((JRBaseStyle)style).clone();
		double factor = (double)targetDpi / sourceDpi;
		scaleDpiStyleAttributes(scaled, factor);

		JRConditionalStyle[] conditionalStyles = scaled.getConditionalStyles();
		if (conditionalStyles != null)
		{
			for (JRConditionalStyle conditionalStyle : conditionalStyles)
			{
				scaleDpiStyleAttributes(conditionalStyle, factor);
			}
		}

		return scaled;
	}

	private static void scaleDpiStyleAttributes(JRStyle style, double factor)
	{
		if (style.getOwnRadius() != null)
		{
			style.setRadius(scaleInt(style.getOwnRadius(), factor));
		}

		scaleDpiPen(style.getLinePen(), factor);
		scaleDpiBox(style.getLineBox(), factor);
		scaleDpiParagraph(style.getParagraph(), factor);
	}

	private static void scaleDpiPen(JRPen pen, double factor)
	{
		if (pen != null && pen.getOwnLineWidth() != null)
		{
			pen.setLineWidth(scaleFloat(pen.getOwnLineWidth(), factor));
		}
	}

	private static void scaleDpiBox(JRLineBox lineBox, double factor)
	{
		if (lineBox == null)
		{
			return;
		}

		if (lineBox.getOwnPadding() != null)
		{
			lineBox.setPadding(scaleInt(lineBox.getOwnPadding(), factor));
		}
		if (lineBox.getOwnTopPadding() != null)
		{
			lineBox.setTopPadding(scaleInt(lineBox.getOwnTopPadding(), factor));
		}
		if (lineBox.getOwnLeftPadding() != null)
		{
			lineBox.setLeftPadding(scaleInt(lineBox.getOwnLeftPadding(), factor));
		}
		if (lineBox.getOwnBottomPadding() != null)
		{
			lineBox.setBottomPadding(scaleInt(lineBox.getOwnBottomPadding(), factor));
		}
		if (lineBox.getOwnRightPadding() != null)
		{
			lineBox.setRightPadding(scaleInt(lineBox.getOwnRightPadding(), factor));
		}

		scaleDpiPen(lineBox.getPen(), factor);
		scaleDpiPen(lineBox.getTopPen(), factor);
		scaleDpiPen(lineBox.getLeftPen(), factor);
		scaleDpiPen(lineBox.getBottomPen(), factor);
		scaleDpiPen(lineBox.getRightPen(), factor);
	}

	private static void scaleDpiParagraph(JRParagraph paragraph, double factor)
	{
		if (paragraph == null)
		{
			return;
		}

		if (paragraph.getOwnFirstLineIndent() != null)
		{
			paragraph.setFirstLineIndent(scaleInt(paragraph.getOwnFirstLineIndent(), factor));
		}
		if (paragraph.getOwnLeftIndent() != null)
		{
			paragraph.setLeftIndent(scaleInt(paragraph.getOwnLeftIndent(), factor));
		}
		if (paragraph.getOwnRightIndent() != null)
		{
			paragraph.setRightIndent(scaleInt(paragraph.getOwnRightIndent(), factor));
		}
		if (paragraph.getOwnSpacingBefore() != null)
		{
			paragraph.setSpacingBefore(scaleInt(paragraph.getOwnSpacingBefore(), factor));
		}
		if (paragraph.getOwnSpacingAfter() != null)
		{
			paragraph.setSpacingAfter(scaleInt(paragraph.getOwnSpacingAfter(), factor));
		}
		if (paragraph.getOwnTabStopWidth() != null)
		{
			paragraph.setTabStopWidth(scaleInt(paragraph.getOwnTabStopWidth(), factor));
		}

		TabStop[] tabStops = paragraph.getOwnTabStops();
		if (tabStops != null)
		{
			for (TabStop tabStop : tabStops)
			{
				tabStop.setPosition(scaleInt(tabStop.getPosition(), factor));
			}
		}
	}

	private static int scaleInt(int value, double factor)
	{
		return (int)Math.floor(value * factor);
	}

	private static float scaleFloat(float value, double factor)
	{
		return (float)(value * factor);
	}

}

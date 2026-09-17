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
			
			if (styleMode != null)
			{
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
			return hasConditionalBorder(style, selector) || hasBorder(style, selector);
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
			return hasConditionalPadding(style, penSelector) || hasPadding(style, penSelector);
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
	 * Returns whether any conditional style of the given style sets a border on the given side.
	 * <p>
	 * Conditional styles are only evaluated at fill time, so a border which they set has to be
	 * accounted for even though it might not end up being applied.
	 */
	protected boolean hasConditionalBorder(JRStyle style, BoxSideSelector selector)
	{
		JRConditionalStyle[] conditionalStyles = style.getConditionalStyles();
		if (conditionalStyles != null)
		{
			for (JRConditionalStyle conditionalStyle : conditionalStyles)
			{
				JRLineBox lineBox = conditionalStyle.getLineBox();
				Float sideLineWidth = selector.getPen(lineBox).getOwnLineWidth();
				if (sideLineWidth != null ? sideLineWidth > .0f : hasOwnPenWidth(lineBox))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Returns whether any conditional style of the given style sets a padding on the given side.
	 * 
	 * @see #hasConditionalBorder(JRStyle, BoxSideSelector)
	 */
	protected boolean hasConditionalPadding(JRStyle style, BoxSideSelector penSelector)
	{
		JRConditionalStyle[] conditionalStyles = style.getConditionalStyles();
		if (conditionalStyles != null)
		{
			for (JRConditionalStyle conditionalStyle : conditionalStyles)
			{
				JRLineBox lineBox = conditionalStyle.getLineBox();
				Integer sidePadding = penSelector.getPadding(lineBox);
				if (sidePadding != null ? sidePadding > 0 : hasOwnPadding(lineBox))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean hasOwnPenWidth(JRLineBox lineBox)
	{
		Float lineWidth = lineBox.getPen().getOwnLineWidth();
		return lineWidth != null && lineWidth > .0f;
	}
	
	private boolean hasOwnPadding(JRLineBox lineBox)
	{
		Integer padding = lineBox.getOwnPadding();
		return padding != null && padding > 0;
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
	
}

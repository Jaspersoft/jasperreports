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
package net.sf.jasperreports.engine.export;

import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;

import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRStyledTextAttributeSelector;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.engine.util.JRStyledTextUtil;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class AwtTextRenderer extends AbstractTextRenderer
{
	protected final JRStyledTextAttributeSelector noBackcolorSelector;
	protected final JRStyledTextUtil styledTextUtil;
	
	/**
	 * 
	 */
	private Graphics2D grx;
	
	
	/**
	 * 
	 */
	public AwtTextRenderer(
		JasperReportsContext jasperReportsContext,
		boolean isMinimizePrinterJobSize,
		boolean ignoreMissingFont,
		boolean defaultIndentFirstLine,
		boolean defaultJustifyLastLine
		)
	{
		this(
			jasperReportsContext,
			isMinimizePrinterJobSize,
			ignoreMissingFont,
			defaultIndentFirstLine,
			defaultJustifyLastLine,
			JasperPrint.DEFAULT_REPORT_DPI
			);
	}

	/**
	 *
	 */
	public AwtTextRenderer(
		JasperReportsContext jasperReportsContext,
		boolean isMinimizePrinterJobSize,
		boolean ignoreMissingFont,
		boolean defaultIndentFirstLine,
		boolean defaultJustifyLastLine,
		int reportDpi
		)
	{
		super(
			jasperReportsContext,
			isMinimizePrinterJobSize,
			ignoreMissingFont,
			defaultIndentFirstLine,
			defaultJustifyLastLine
			);

		this.noBackcolorSelector = JRStyledTextAttributeSelector.getNoBackcolorSelector(jasperReportsContext);
		styledTextUtil = JRStyledTextUtil.getInstance(jasperReportsContext);
		this.fontSizeScale = (float) reportDpi / JasperPrint.DEFAULT_REPORT_DPI;
	}
	

	/**
	 * 
	 */
	public void initialize(Graphics2D grx, JRPrintText text, int offsetX, int offsetY)
	{
		JRStyledText styledText = styledTextUtil.getProcessedStyledText(text, noBackcolorSelector, null);
		if (styledText == null)
		{
			return;
		}
		
		this.grx = grx;
		
		super.initialize(text, styledText, offsetX, offsetY);
	}

	
	@Override
	public void draw()
	{
		if (bulletChunk != null)
		{
			AttributedCharacterIterator bulletIterator = bulletChunk.getIterator();
			LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(bulletIterator, getFontRenderContext());//grx.getFontRenderContext()

			TextLayout bulletLayout = 
				lineMeasurer.nextLayout(
					1000,
					bulletIterator.getEndIndex(),
					true
					);
			
			bulletLayout.draw(
				grx, 
				x + drawPosX - bulletLayout.getVisibleAdvance() - 10, 
				y + topPadding + verticalAlignOffset + drawPosY
				);
		}

		TabSegment segment = segments.get(segmentIndex);
		
// this commented code is here to show that we could have clipped each segment individually,
// but decided against this technique because it was producing too much clipping, for little benefit;
// the rendering would have been closer to PDF one, where trailing spaces are not rendered, 
// but more unlike the HTML, where trailing spaces are rendered and even participate to horizontal alignment;
// the solution that was implemented is a compromise in the sense that it renders trailing spaces, 
// but does not consider them for text alignment
//
//		Shape oldClip = grx.getClip();
//		
//		int clipX = Math.round(x + drawPosX);
//		int clipY = Math.round(y + topPadding + verticalAlignOffset + drawPosY - lineHeight);
//		int clipWidth = Math.round(x + drawPosX + segment.layout.getVisibleAdvance()) - clipX;
//		int clipHeight = Math.round(2 * lineHeight);
//		
//		grx.clipRect(
//			clipX, 
//			clipY, 
//			clipWidth, 
//			clipHeight
//			);
//		
//		try
//		{
			segment.layout.draw(
				grx,
				x + drawPosX,// + leftPadding,
				//y + topPadding + verticalAlignOffset + text.getLeadingOffset() + drawPosY
				y + topPadding + verticalAlignOffset + drawPosY
				);
//		}
//		finally
//		{
//			grx.setClip(oldClip);
//		}
	}

	
	@Override
	public FontRenderContext getFontRenderContext()
	{
		return grx.getFontRenderContext();
	}
	
}

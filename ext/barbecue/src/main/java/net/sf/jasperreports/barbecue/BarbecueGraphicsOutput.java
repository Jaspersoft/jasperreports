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
package net.sf.jasperreports.barbecue;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;

import net.sourceforge.barbecue.output.GraphicsOutput;
import net.sourceforge.barbecue.output.LabelLayout;

/**
 * Barbecue graphics output that keeps the barcode text inside its label area.
 * <p>
 * The Barbecue label layouts place the text baseline at the bottom of the text bounds,
 * as if the text had nothing below the baseline. The part of the text below the baseline
 * (descenders, but also the overshoot of round letters) therefore falls outside the label area,
 * which is the area the barcode size accounts for, and gets clipped when rendering.
 * This output draws the text higher by the extent of the text below its baseline, so that the text bounds
 * occupy the place the layout intended for them. The label area, and thus the barcode size, are not changed.
 */
public class BarbecueGraphicsOutput extends GraphicsOutput
{
	// GraphicsOutput keeps its graphics context private
	private final Graphics2D grx;

	public BarbecueGraphicsOutput(Graphics2D grx, Font font, Color fgColor, Color bgColor)
	{
		super(grx, font, fgColor, bgColor);
		this.grx = grx;
	}

	@Override
	public int drawText(String text, LabelLayout labelLayout)
	{
		if (font == null)
		{
			return 0;
		}

		TextLayout textLayout = new TextLayout(text, font, grx.getFontRenderContext());
		labelLayout.setTextLayout(textLayout);

		if (painting)
		{
			grx.setColor(backgroundColour);
			grx.fillRect(labelLayout.getBackgroundX(), labelLayout.getBackgroundY(), labelLayout.getBackgroundWidth(), labelLayout.getBackgroundHeight());
			grx.setColor(foregroundColour);
			// the bounds are relative to the baseline, so their max Y is the extent of the text below the baseline
			float belowBaseline = (float) textLayout.getBounds().getMaxY();
			textLayout.draw(grx, labelLayout.getTextX(), labelLayout.getTextY() - belowBaseline);
		}

		return labelLayout.getBackgroundHeight();
	}

}

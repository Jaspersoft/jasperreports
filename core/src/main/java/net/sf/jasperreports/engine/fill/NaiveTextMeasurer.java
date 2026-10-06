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
package net.sf.jasperreports.engine.fill;

import net.sf.jasperreports.engine.JRCommonText;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.engine.util.SimpleMeasuredText;
import net.sf.jasperreports.engine.util.TextUtils;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class NaiveTextMeasurer implements JRTextMeasurer
{

	private final JRCommonText text;

	public NaiveTextMeasurer(JRCommonText text)
	{
		this.text = text;
	}

	@Override
	public JRMeasuredText measure(JRStyledText styledText, 
			int remainingTextStart, int availableStretchHeight,
			boolean indentFirstLine, boolean canOverflow)
	{
		SimpleMeasuredText simpleMeasuredText = new SimpleMeasuredText();
		simpleMeasuredText.setTextOffset(styledText.length());
		
		JRLineBox box = text.getLineBox();
		int textWidth;
		int textHeight;
		switch (text.getRotation())
		{
		case LEFT:
		case RIGHT:
			textWidth = text.getHeight() - box.getTopPadding() - box.getBottomPadding();
			textHeight = text.getWidth() - box.getLeftPadding() - box.getRightPadding();
			break;
		default:
			textWidth = text.getWidth() - box.getLeftPadding() - box.getRightPadding();
			textHeight = text.getHeight() - box.getTopPadding() - box.getBottomPadding();
			break;
		}
		simpleMeasuredText.setTextWidth(textWidth);
		simpleMeasuredText.setTextHeight(textHeight);
		
		boolean leftToRight = TextUtils.isLeftToRight(styledText.getText().toCharArray());
		simpleMeasuredText.setLeftToRight(leftToRight);
		
		return simpleMeasuredText;
	}
}

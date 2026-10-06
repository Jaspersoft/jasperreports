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
package net.sf.jasperreports.engine.util;

import net.sf.jasperreports.engine.fill.JRMeasuredText;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class SimpleMeasuredText implements JRMeasuredText 
{

	private float leadingOffset;
	private float lineSpacingFactor;
	private float textWidth;
	private float averageCharWidth;
	private float textHeight;
	private int textOffset;
	private boolean paragraphCut;
	private String textSuffix;
	private short[] lineBreakOffsets;
	private boolean leftToRight;
	
	public SimpleMeasuredText()
	{
	}

	public void setLeadingOffset(float leadingOffset)
	{
		this.leadingOffset = leadingOffset;
	}

	@Override
	public float getLeadingOffset()
	{
		return leadingOffset;
	}

	public void setLineSpacingFactor(float lineSpacingFactor)
	{
		this.lineSpacingFactor = lineSpacingFactor;
	}

	@Override
	public float getLineSpacingFactor()
	{
		return lineSpacingFactor;
	}

	public void setTextWidth(float textWidth)
	{
		this.textWidth = textWidth;
	}

	@Override
	public float getTextWidth()
	{
		return textWidth;
	}

	@Override
	public float getAverageCharWidth() 
	{
		return averageCharWidth;
	}

	public void setAverageCharWidth(float averageCharWidth)
	{
		this.averageCharWidth = averageCharWidth;
	}

	public void setTextHeight(float textHeight)
	{
		this.textHeight = textHeight;
	}

	@Override
	public float getTextHeight()
	{
		return textHeight;
	}

	public void setTextOffset(int textOffset)
	{
		this.textOffset = textOffset;
	}

	@Override
	public int getTextOffset()
	{
		return textOffset;
	}

	public void setLeftToRight(boolean leftToRight)
	{
		this.leftToRight = leftToRight;
	}

	@Override
	public boolean isLeftToRight()
	{
		return leftToRight;
	}

	public void setParagraphCut(boolean paragraphCut)
	{
		this.paragraphCut = paragraphCut;
	}

	@Override
	public boolean isParagraphCut()
	{
		return paragraphCut;
	}

	public void setTextSuffix(String textSuffix)
	{
		this.textSuffix = textSuffix;
	}

	@Override
	public String getTextSuffix()
	{
		return textSuffix;
	}

	public void setLineBreakOffsets(short[] lineBreakOffsets)
	{
		this.lineBreakOffsets = lineBreakOffsets;
	}

	@Override
	public short[] getLineBreakOffsets()
	{
		return lineBreakOffsets;
	}

}

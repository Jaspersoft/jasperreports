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

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class SimpleTextLine implements TextLine
{

	private float ascent;
	private float descent;
	private float leading;
	private int characterCount;
	private boolean leftToRight;
	private float advance;
	
	@Override
	public float getAscent()
	{
		return ascent;
	}

	@Override
	public float getDescent()
	{
		return descent;
	}

	@Override
	public float getLeading()
	{
		return leading;
	}

	@Override
	public int getCharacterCount()
	{
		return characterCount;
	}

	@Override
	public boolean isLeftToRight()
	{
		return leftToRight;
	}

	@Override
	public float getAdvance()
	{
		return advance;
	}

	public void setAscent(float ascent)
	{
		this.ascent = ascent;
	}

	public void setDescent(float descent)
	{
		this.descent = descent;
	}

	public void setLeading(float leading)
	{
		this.leading = leading;
	}

	public void setCharacterCount(int characterCount)
	{
		this.characterCount = characterCount;
	}

	public void setLeftToRight(boolean leftToRight)
	{
		this.leftToRight = leftToRight;
	}

	public void setAdvance(float advance)
	{
		this.advance = advance;
	}
	
	@Override
	public String toString()
	{
		return "{ascent: " + ascent
				+ ", descent: " + descent
				+ ", leading: " + leading
				+ ", characterCount: " + characterCount
				+ ", leftToRight: " + leftToRight
				+ ", advance: " + advance
				+ "}";
	}

}

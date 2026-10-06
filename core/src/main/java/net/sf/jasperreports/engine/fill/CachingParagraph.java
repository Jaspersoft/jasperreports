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

import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRParagraph;
import net.sf.jasperreports.engine.JRParagraphContainer;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.TabStop;
import net.sf.jasperreports.engine.type.LineSpacingEnum;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class CachingParagraph implements JRParagraph
{

	private final JRParagraph base;
	
	private final LineSpacingEnum lineSpacing;
	private final Float lineSpacingSize;
	private final Integer firstLineIndent;
	private final Integer leftIndent;
	private final Integer rightIndent;
	private final Integer spacingBefore;
	private final Integer spacingAfter;
	
	public CachingParagraph(JRParagraph base)
	{
		this.base = base;
		
		this.lineSpacing = base.getLineSpacing();
		this.lineSpacingSize = base.getLineSpacingSize();
		this.firstLineIndent = base.getFirstLineIndent();
		this.leftIndent = base.getLeftIndent();
		this.rightIndent = base.getRightIndent();
		this.spacingBefore = base.getSpacingBefore();
		this.spacingAfter = base.getSpacingAfter();
	}

	/**
	 * @deprecated Replaced by {@link #getParagraphContainer()}.
	 */
	@Override
	public JRDefaultStyleProvider getDefaultStyleProvider()
	{
		return base.getParagraphContainer().getDefaultStyleProvider();
	}

	/**
	 * @deprecated Replaced by {@link #getParagraphContainer()}.
	 */
	@Override
	public JRStyle getStyle()
	{
		return base.getParagraphContainer().getStyle();
	}

	/**
	 * @deprecated Replaced by {@link #getParagraphContainer()}.
	 */
	@Override
	public String getStyleNameReference()
	{
		return base.getParagraphContainer().getStyleNameReference();
	}

	@Override
	public JRParagraphContainer getParagraphContainer()
	{
		return base.getParagraphContainer();
	}

	@Override
	public JRParagraph clone(JRParagraphContainer paragraphContainer)
	{
		// cloning the base paragraph
		return base.clone(paragraphContainer);
	}

	@Override
	public void populateStyle()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public LineSpacingEnum getLineSpacing()
	{
		return lineSpacing;
	}

	@Override
	public LineSpacingEnum getOwnLineSpacing()
	{
		return base.getOwnLineSpacing();
	}

	@Override
	public void setLineSpacing(LineSpacingEnum lineSpacing)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Float getLineSpacingSize()
	{
		return lineSpacingSize;
	}

	@Override
	public Float getOwnLineSpacingSize()
	{
		return base.getOwnLineSpacingSize();
	}

	@Override
	public void setLineSpacingSize(Float lineSpacingSize)
	{
		throw new UnsupportedOperationException();
	}

	
	@Override
	public Integer getLeftIndent()
	{
		return leftIndent;
	}

	@Override
	public Integer getOwnLeftIndent()
	{
		return base.getOwnLeftIndent();
	}

	@Override
	public void setLeftIndent(Integer leftIndent)
	{
		throw new UnsupportedOperationException();
	}

	
	@Override
	public Integer getFirstLineIndent()
	{
		return firstLineIndent;
	}

	@Override
	public Integer getOwnFirstLineIndent()
	{
		return base.getOwnFirstLineIndent();
	}

	@Override
	public void setFirstLineIndent(Integer firstLineIndent)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer getRightIndent()
	{
		return rightIndent;
	}

	@Override
	public Integer getOwnRightIndent()
	{
		return base.getOwnRightIndent();
	}

	@Override
	public void setRightIndent(Integer rightIndent)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer getSpacingBefore()
	{
		return spacingBefore;
	}

	@Override
	public Integer getOwnSpacingBefore()
	{
		return base.getOwnSpacingBefore();
	}

	@Override
	public void setSpacingBefore(Integer spacingBefore)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer getSpacingAfter()
	{
		return spacingAfter;
	}

	@Override
	public Integer getOwnSpacingAfter()
	{
		return base.getOwnSpacingAfter();
	}

	@Override
	public void setSpacingAfter(Integer spacingAfter)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer getTabStopWidth()
	{
		//FIXME cache?
		return base.getTabStopWidth();
	}

	@Override
	public Integer getOwnTabStopWidth()
	{
		return base.getOwnTabStopWidth();
	}

	@Override
	public void setTabStopWidth(Integer tabStopWidth)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public TabStop[] getTabStops()
	{
		return base.getTabStops();
	}

	@Override
	public TabStop[] getOwnTabStops()
	{
		return base.getOwnTabStops();
	}

	@Override
	public void addTabStop(TabStop tabStop)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void addTabStop(int index, TabStop tabStop)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeTabStop(int index)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeTabStop(TabStop tabStop)
	{
		throw new UnsupportedOperationException();
	}

}

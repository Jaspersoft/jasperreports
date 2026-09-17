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
package net.sf.jasperreports.engine.fill;

import java.awt.Color;

import net.sf.jasperreports.engine.JRBoxContainer;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.base.JRBoxPen;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class CachingLineBox implements JRLineBox
{
	private final JRLineBox base;
	
	private final Integer padding;
	private final Integer topPadding;
	private final Integer bottomPadding;
	private final Integer leftPadding;
	private final Integer rightPadding;
	
	public CachingLineBox(JRLineBox base)
	{
		this.base = base;
		
		padding = base.getPadding();
		topPadding = base.getTopPadding();
		bottomPadding = base.getBottomPadding();
		leftPadding = base.getLeftPadding();
		rightPadding = base.getRightPadding();
	}

	@Override
	public JRDefaultStyleProvider getDefaultStyleProvider()
	{
		return base.getDefaultStyleProvider();
	}

	@Override
	public JRStyle getStyle()
	{
		return base.getStyle();
	}

	@Override
	public Color getDefaultLineColor()
	{
		return base.getDefaultLineColor();
	}

	@Override
	public String getStyleNameReference()
	{
		return base.getStyleNameReference();
	}

	@Override
	public JRBoxContainer getBoxContainer()
	{
		return base.getBoxContainer();
	}

	@Override
	public JRLineBox clone(JRBoxContainer boxContainer)
	{
		// cloning the base line box
		return base.clone(boxContainer);
	}

	@Override
	public void populateStyle()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public JRBoxPen getPen()
	{
		return base.getPen();
	}

	@Override
	public void copyPen(JRBoxPen pen)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public JRBoxPen getTopPen()
	{
		return base.getTopPen();
	}

	@Override
	public void copyTopPen(JRBoxPen topPen)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public JRBoxPen getLeftPen()
	{
		return base.getLeftPen();
	}

	@Override
	public void copyLeftPen(JRBoxPen leftPen)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public JRBoxPen getBottomPen()
	{
		return base.getBottomPen();
	}

	@Override
	public void copyBottomPen(JRBoxPen bottomPen)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public JRBoxPen getRightPen()
	{
		return base.getRightPen();
	}

	@Override
	public void copyRightPen(JRBoxPen rightPen)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer getPadding()
	{
		return padding;
	}

	@Override
	public Integer getOwnPadding()
	{
		return base.getOwnPadding();
	}

	@Override
	public void setPadding(Integer padding)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer getTopPadding()
	{
		return topPadding;
	}

	@Override
	public Integer getOwnTopPadding()
	{
		return base.getOwnTopPadding();
	}

	@Override
	public void setTopPadding(Integer padding)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer getLeftPadding()
	{
		return leftPadding;
	}

	@Override
	public Integer getOwnLeftPadding()
	{
		return base.getOwnLeftPadding();
	}

	@Override
	public void setLeftPadding(Integer padding)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer getBottomPadding()
	{
		return bottomPadding;
	}

	@Override
	public Integer getOwnBottomPadding()
	{
		return base.getOwnBottomPadding();
	}

	@Override
	public void setBottomPadding(Integer padding)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer getRightPadding()
	{
		return rightPadding;
	}

	@Override
	public Integer getOwnRightPadding()
	{
		return base.getOwnRightPadding();
	}

	@Override
	public void setRightPadding(Integer padding)
	{
		throw new UnsupportedOperationException();
	}

}

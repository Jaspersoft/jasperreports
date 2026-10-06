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
package net.sf.jasperreports.components.list;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.fill.JRFillCloneFactory;
import net.sf.jasperreports.engine.fill.JRFillCloneable;
import net.sf.jasperreports.engine.fill.JRFillElementContainer;
import net.sf.jasperreports.engine.fill.JRFillObjectFactory;

/**
 * List contents fill element container.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class FillListContents extends JRFillElementContainer
{

	private final int contentsHeight;
	
	protected FillListContents(ListContents listContents,
			JRFillObjectFactory factory)
	{
		super(factory.getFiller(), listContents, factory);
		
		this.contentsHeight = listContents.getHeight();
		
		initElements();
		initConditionalStyles();
	}

	public FillListContents(FillListContents fillListContents,
			JRFillCloneFactory factory)
	{
		super(fillListContents, factory);
		
		this.contentsHeight = fillListContents.contentsHeight;

		initElements();
		initConditionalStyles();
	}

	public int getHeight()
	{
		return contentsHeight;
	}
	
	@Override
	protected int getContainerHeight()
	{
		return contentsHeight;
	}

	@Override
	protected int getActualContainerHeight()
	{
		return getContainerHeight(); 
	}
	
	protected void evaluateContents() throws JRException
	{
		evaluateConditionalStyles(JRExpression.EVALUATION_DEFAULT);
		evaluate(JRExpression.EVALUATION_DEFAULT);
	}
	
	protected void prepare(int availableHeight) throws JRException
	{
		initFill();
		resetElements();
		prepareElements(availableHeight, true);
	}
	
	@Override
	public JRFillCloneable createClone(JRFillCloneFactory factory)
	{
		return new FillListContents(this, factory);
	}
	
	public FillListContents createClone()
	{
		JRFillCloneFactory factory = new JRFillCloneFactory();
		return new FillListContents(this, factory);
	}
	
	// overridden for access
	@Override
	protected int getStretchHeight()
	{
		return super.getStretchHeight();
	}
	
	// overridden for access
	@Override
	protected void rewind() throws JRException
	{
		super.rewind();
	}
	
	protected void stretchTo(int height)
	{
		setStretchHeight(height);
	}

	// double check the usefulness of this whole method when removing legacy stretch;
	// it looks like it will be needed only on HorizontalFillList, in which case its content could be copied only there
	// and this method could be removed
	protected void finalizeElementPositions()
	{
		stretchElementsToContainer();
		moveBandBottomElements();
	}

	@Override
	public boolean isSplitTypePreventInhibited(boolean isTopLevelCall)
	{
		//FIXME implement logic
		return false;
	}
}

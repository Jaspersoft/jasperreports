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

import java.util.List;

import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.type.FooterPositionEnum;

/**
 * 
 */
public final class ElementRangeUtil
{
	/**
	 *
	 */
	private ElementRangeUtil()
	{
	}
	
	
	/**
	 *
	 */
	public static void expandOrIgnore(ElementRange elementRange, ElementRange newElementRange)
	{
		if (
			elementRange != null
			&& elementRange.getPage() == newElementRange.getPage()
			&& elementRange.getColumnIndex() == newElementRange.getColumnIndex()
			)
		{
			// if the new element range is on the same page/column, 
			// we just move the marker on the existing element range 
			elementRange.expand(newElementRange.getBottomY());
		}
	}
	
	/*
	 *
	 *
	public static ElementRange expand(ElementRange elementRange, ElementRange newElementRange)
	{
		if (elementRange == null)
		{
			elementRange = newElementRange;
		}
		else
		{
			// check to see if the new element range is on the same page/column as the previous one
			
			if (
				elementRange.getPage() == newElementRange.getPage()
				&& elementRange.getColumnIndex() == newElementRange.getColumnIndex()
				)
			{
				// if the new element range is on the same page/column, 
				// we just move the marker on the existing element range 
				elementRange.expand(newElementRange.getBottomY());
			}
			else
			{
				// page/column break occurred, so we simply discard the old element range and keep the new one;
				// for group footer element ranges, this is where the move operation is performed on the old element range,
				// but for normal element ranges, such move is not applicable
//				moveContent(elementRange, columnFooterOffsetY);
				elementRange = newElementRange;
			}
		}
		
		return elementRange;
	}
	*/
	
	/**
	 *
	 */
	public static GroupFooterElementRange expandOrMove(
		GroupFooterElementRange groupFooterElementRange, 
		GroupFooterElementRange newGroupFooterElementRange, 
		int columnFooterOffsetY
		)
	{
		if (groupFooterElementRange == null)
		{
			groupFooterElementRange = newGroupFooterElementRange;
		}
		else
		{
			// check to see if the new element range is on the same page/column as the previous one
			
			if (
				groupFooterElementRange.getElementRange().getPage() == newGroupFooterElementRange.getElementRange().getPage()
				&& groupFooterElementRange.getElementRange().getColumnIndex() == newGroupFooterElementRange.getElementRange().getColumnIndex()
				)
			{
				// if the new element range is on the same page/column, 
				// we just move the marker on the existing element range 
				groupFooterElementRange.getElementRange().expand(newGroupFooterElementRange.getElementRange().getBottomY());
			}
			else
			{
				// page/column break occurred, so the move operation 
				// must be performed on the previous group footer element range
				moveContent(groupFooterElementRange, columnFooterOffsetY);
				groupFooterElementRange = newGroupFooterElementRange;
			}
		}
		
		return groupFooterElementRange;
	}
	
	/**
	 *
	 */
	public static void moveContent(GroupFooterElementRange groupFooterElementRange, int columnFooterOffsetY)
	{
		if (groupFooterElementRange.getMasterFooterPosition() != FooterPositionEnum.NORMAL)//FIXME is footerPosition testing required here?
		{
			ElementRange elementRange = groupFooterElementRange.getElementRange();
			int distanceToColumnFooter = columnFooterOffsetY - elementRange.getBottomY();
			//no page/column break occurred
			for (int i = elementRange.getFirstElementIndex(); i <= elementRange.getLastElementIndex(); i++)
			{
				JRPrintElement printElement = elementRange.getPage().getElements().get(i);
				printElement.setY(printElement.getY() + distanceToColumnFooter);
			}
		}
	}
	
	/**
	 *
	 */
	public static void moveContent(ElementRange elementRange, int topY)
	{
		int distanceToNewTopY = topY - elementRange.getTopY();
		for (int i = elementRange.getFirstElementIndex(); i <= elementRange.getLastElementIndex(); i++)
		{
			JRPrintElement printElement = elementRange.getPage().getElements().get(i);
			printElement.setY(printElement.getY() + distanceToNewTopY);
		}
	}
	
	/**
	 *
	 */
	public static ElementRangeContents removeContent(ElementRange elementRange, 
			DelayedFillActions delayedActions) 
	{
		ElementRangeContents contents = new ElementRangeContents();
		if (elementRange.getFirstElementIndex() <= elementRange.getLastElementIndex())
		{
			for (int i = elementRange.getLastElementIndex(); i >= elementRange.getFirstElementIndex(); i--)
			{
				contents.addElement(elementRange.getPage().getElements().remove(i));
			}
		}
		
		if (delayedActions != null)
		{
			delayedActions.collectElementEvaluations(elementRange.getPage(), 
					contents.getElements(), contents);
		}
		
		return contents;
	}
	
	/**
	 *
	 */
	public static void addContent(JRPrintPage printPage, int pageIndex,
			ElementRangeContents elementsToMove, int xdelta, int ydelta,
			DelayedFillActions delayedActions)
	{
		if (elementsToMove != null && !elementsToMove.getElements().isEmpty())
		{
			List<JRPrintElement> elements = elementsToMove.getElements();
			for (int i = elements.size() - 1; i >= 0; i--)// elementsToMove were added in reverse order
			{
				JRPrintElement printElement = elements.get(i);
	
				printElement.setX(printElement.getX() + xdelta);
				printElement.setY(printElement.getY() + ydelta);
	
				printPage.addElement(printElement);
			}
			
			if (delayedActions != null && elementsToMove.hasEvaluations())
			{
				delayedActions.addElementEvaluations(printPage, pageIndex, elementsToMove);
			}
		}
	}
}
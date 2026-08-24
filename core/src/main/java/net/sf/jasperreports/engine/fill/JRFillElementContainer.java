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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.JRConditionalStyle;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRElementGroup;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRFrame;
import net.sf.jasperreports.engine.JROrigin;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintElementContainer;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.base.JRBaseStyle;
import net.sf.jasperreports.engine.type.PositionTypeEnum;
import net.sf.jasperreports.engine.type.StretchTypeEnum;
import net.sf.jasperreports.engine.util.StyleUtil;

/**
 * Abstract implementation of an element container filler.
 * <p>
 * This is the base for band, frame and crosstab cell fillers.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public abstract class JRFillElementContainer extends JRFillElementGroup implements FillContainerContext
{
	protected JRBaseFiller filler;
	
	private JRFillElement[] ySortedElements;
	private JRFillElement[] stretchElements;
	private JRFillElement[] bandBottomElements;
	private JRFillElement[] removableElements;
	
	protected boolean willOverflowWithElements;
	protected boolean willOverflowWithWhiteSpace;
	protected boolean isOverflow;
	protected boolean currentOverflowWithElements;
	protected boolean currentOverflowWithWhiteSpace;
	private boolean currentOverflowAllowed;
	
	private int stretchHeight;
	private int firstY;
	
	protected final JRFillExpressionEvaluator expressionEvaluator;
	
	protected JRFillElement[] deepElements;

	/**
	 *
	 */
	protected Set<JRStyle> stylesToEvaluate = new HashSet<>();
	protected Map<JRStyle,JRStyle> evaluatedStyles = new HashMap<>();
	
	protected boolean hasPrintWhenOverflowElement;

	
	protected JRFillElementContainer(JRBaseFiller filler, JRElementGroup container, JRFillObjectFactory factory)
	{
		super(container, factory);
		
		expressionEvaluator = factory.getExpressionEvaluator();
		initDeepElements();
		
		this.filler = filler;
	}
	
	protected JRFillElementContainer(JRFillElementContainer container, JRFillCloneFactory factory)
	{
		super(container, factory);
		
		expressionEvaluator = container.expressionEvaluator;
		initDeepElements();
		
		this.filler = container.filler;
	}


	protected void initDeepElements()
	{
		if (elements == null)
		{
			deepElements = new JRFillElement[0];
		}
		else
		{
			List<JRFillElement> deepElementsList = new ArrayList<>(elements.length);
			collectDeepElements(elements, deepElementsList);
			deepElements = new JRFillElement[deepElementsList.size()];
			deepElementsList.toArray(deepElements);
		}
	}

	private static void collectDeepElements(JRElement[] elements, List<JRFillElement> deepElementsList)
	{
		for (JRElement element : elements)
		{
			deepElementsList.add((JRFillElement)element);
			
			if (element instanceof JRFillFrame)
			{
				JRFrame frame = (JRFrame) element;
				collectDeepElements(frame.getElements(), deepElementsList);
			}
		}
	}


	protected final void initElements()
	{
		hasPrintWhenOverflowElement = false;
		
		if (elements != null && elements.length > 0)
		{
			List<JRFillElement> stretchElemsList = new ArrayList<>();
			List<JRFillElement> bandBottomElemsList = new ArrayList<>();
			List<JRFillElement> removableElemsList = new ArrayList<>();
			
			JRYComparator yComparator = new JRYComparator();

			/*   */
			ySortedElements = Arrays.copyOf(elements, elements.length);
			Arrays.sort(ySortedElements, yComparator);

			topElementInGroup = null;
			bottomElementInGroup = null;

			for (JRFillElement element : ySortedElements)
			{
				if (element.getPositionType() == PositionTypeEnum.FIX_RELATIVE_TO_BOTTOM)
				{
					bandBottomElemsList.add(element);
				}

				if (element.getStretchType() != StretchTypeEnum.NO_STRETCH)
				{
					stretchElemsList.add(element);
				}
				
				if (element.isRemoveLineWhenBlank())
				{
					removableElemsList.add(element);
				}
				
				if (element.isPrintWhenDetailOverflows())
				{
					hasPrintWhenOverflowElement = true;
				}

				if (
					topElementInGroup == null ||
					(
					element.getY() + element.getHeight() <
					topElementInGroup.getY() + topElementInGroup.getHeight())
					)
				{
					topElementInGroup = element;
				}

				if (
					bottomElementInGroup == null ||
					(
					element.getY() + element.getHeight() >
					bottomElementInGroup.getY() + bottomElementInGroup.getHeight())
					)
				{
					bottomElementInGroup = element;
				}
			}

			/*   */
			stretchElements = new JRFillElement[stretchElemsList.size()];
			stretchElemsList.toArray(stretchElements);

			/*   */
			bandBottomElements = new JRFillElement[bandBottomElemsList.size()];
			bandBottomElemsList.toArray(bandBottomElements);

			/*   */
			removableElements = new JRFillElement[removableElemsList.size()];
			removableElemsList.toArray(removableElements);
		}
		
		/*   */
		setDependentElements();
	}

	/**
	 *
	 */
	private void setDependentElements()
	{
		if (ySortedElements != null && ySortedElements.length > 0)
		{
			for (int i = 0; i < ySortedElements.length - 1; i++)
			{
				JRFillElement iElem = ySortedElements[i];
				boolean isBreakElem = iElem instanceof JRFillBreak;

				for (int j = i + 1; j < ySortedElements.length; j++)
				{
					JRFillElement jElem = ySortedElements[j];
					
					int left = Math.min(iElem.getX(), jElem.getX());
					int right = Math.max(iElem.getX() + iElem.getWidth(), jElem.getX() + jElem.getWidth());
					
					if (
						((isBreakElem && jElem.getPositionType() == PositionTypeEnum.FIX_RELATIVE_TO_TOP) || jElem.getPositionType() == PositionTypeEnum.FLOAT) &&
						iElem.getY() + iElem.getHeight() <= jElem.getY() &&
						iElem.getWidth() + jElem.getWidth() > right - left // FIXME band bottom elements should not have dependent elements
						)
					{
						iElem.addDependantElement(jElem);
					}
				}

				/*
				if (iElem.getParent().getElementGroup() != null) //parent might be null
				{
					iElem.setGroupElements(
						iElem.getParent().getElementGroup().getElements()
						);
				}
				*/
			}
		}
	}

	
	/**
	 *
	 */
	protected void evaluate(byte evaluation) throws JRException
	{
		//evaluatePrintWhenExpression(evaluation);

		//if (
		//	(isPrintWhenExpressionNull() ||
		//	(!isPrintWhenExpressionNull() && 
		//	isPrintWhenTrue()))
		//	)
		//{
			JRElement[] allElements = getElements();
			if (allElements != null && allElements.length > 0)
			{
				for (int i = 0; i < allElements.length; i++)
				{
					JRFillElement element = (JRFillElement)allElements[i];
					element.setCurrentEvaluation(evaluation);
					element.evaluate(evaluation);
				}
			}
		//}
	}


	/**
	 *
	 */
	protected void resetElements()
	{
		if (ySortedElements != null && ySortedElements.length > 0)
		{
			for (JRFillElement element : ySortedElements)
			{
				element.reset();
				
				if (!isOverflow)
				{
					element.setAlreadyPrinted(false);
				}
			}
		}
	}
	

	/**
	 * Indicates whether the elements in this container will overflow.
	 * 
	 * @return whether this container will overflow
	 */
	public boolean willOverflow()
	{
		return willOverflowWithElements || willOverflowWithWhiteSpace;
	}


	protected void initFill()
	{
		isOverflow = willOverflow();
		firstY = 0;
	}

	
	/**
	 *
	 */
	protected void prepareElements(
		int availableHeight,
		boolean isOverflowAllowed
		) throws JRException
	{
		currentOverflowWithElements = false;
		currentOverflowWithWhiteSpace = false;
		currentOverflowAllowed = isOverflowAllowed;

		firstY = isOverflow ? getActualContainerHeight() : 0;
		boolean isFirstYFound = false;

		if (ySortedElements != null && ySortedElements.length > 0)
		{
			for (JRFillElement element : ySortedElements)
			{
				currentOverflowWithElements = 
					element.prepare(
						availableHeight + getElementFirstY(element),
						isOverflow,
						isOverflowAllowed
						) 
					|| currentOverflowWithElements;
				
				// it does not seem to make sense for elements that do not print because of their isToPrint() returning false,
				// to push other dependent elements, but it was always like that; furthermore, such disappearing elements are pushed by 
				// other elements and also participate in white space collapse later on, so it is somewhat fair to also allow them 
				// to push others 
				element.moveDependantElements();

				if (element.isToPrint())
				{
					if (isOverflow)
					{
						if (element.isReprinted())
						{
							firstY = 0;
						}
						else if (!isFirstYFound)
						{
							firstY = element.getY();
						}
						isFirstYFound = true;
					}

					filler.atLeastOneElementIsToPrint = true;
				}
			}
		}
		
		// normally, we should add firstY here, because it says below stretchHeight contains firstY; 
		// this initialization matters when band overflows with white space and no element is rendered on the next page;
		// but we don't add it because, historically, bands did not preserve the white space when overflowing, unlike frames for example,
		// which re-render at their design height even when overflowing with white space
		stretchHeight = getContainerHeight();// + firstY;

		// certain elements have stretched to their natural height, while others have been moved in the process;
		// we are now ready to calculate the stretch height of the current container, so that we can use that for
		// moving elements to bottom, before attempting to remove blank elements;
		// ATTENTION: this calculation needed to be in a separate ySortedElement loop as the above one, because
		// it needs to take into consideration the displacement of dependent elements made after each element prepare
		prepareStretchHeight(availableHeight, isOverflowAllowed);
		
		moveBandBottomElements();
		
		// removing blank elements and thus collapsing white space performs both
		// element height shrinking and relative Y repositioning, also changing the current
		// container stretch height
		removeBlankElements();

		// we are first stretching elements relative to group, because they do not need container height and might
		// actually cause the container to stretch further
		stretchElementsToElementGroup();

		// recalculating container stretch height to account for element group stretching, just before triggering
		// container related stretch
		prepareStretchHeight(availableHeight, isOverflowAllowed);

		moveBandBottomElements();

		// container based element stretching is the last one to be performed
		stretchElementsToContainer();
	}

	/**
	 *
	 */
	protected void prepareStretchHeight(
		int availableHeight,
		boolean isOverflowAllowed
		) throws JRException
	{
		int calculatedStretchHeight = calculateStretchHeight();
		
		if (calculatedStretchHeight > availableHeight + firstY)
		{
			currentOverflowWithWhiteSpace = true;
		}
		
		// stretchHeight includes firstY, which is subtracted in fillElements
		if (currentOverflowWithElements || currentOverflowWithWhiteSpace)
		{
			stretchHeight = availableHeight + firstY;
		}
		else
		{
			stretchHeight = calculatedStretchHeight;
		}

		willOverflowWithElements = currentOverflowWithElements && isOverflowAllowed;
		willOverflowWithWhiteSpace = currentOverflowWithWhiteSpace && isOverflowAllowed;
	}

	/**
	 *
	 */
	protected int calculateStretchHeight() throws JRException
	{
		int calculatedStretchHeight = -1;

		if (ySortedElements != null && ySortedElements.length > 0)
		{
			int containerHeight = getContainerHeight();

			for (JRFillElement element : ySortedElements)
			{
				if (element.isToPrint())
				{
					int spaceToBottom = containerHeight - (element.getY() + element.getHeight()) - element.getCollapsedHeightBelow();
					if (spaceToBottom < 0)
					{
						spaceToBottom = 0;
					}
					
					if (calculatedStretchHeight < element.getRelativeY() + element.getStretchHeight() + spaceToBottom)
					{
						calculatedStretchHeight = element.getRelativeY() + element.getStretchHeight() + spaceToBottom;
					}
				}
			}
		}
		
		if (calculatedStretchHeight < 0)
		{
			// there was no element printing; so trying to preserve stretchHeight
			calculatedStretchHeight = stretchHeight;
		}
		
		return calculatedStretchHeight;
	}

	@Override
	public boolean isCurrentOverflow()
	{
		return currentOverflowWithElements || currentOverflowWithWhiteSpace;
	}

	@Override
	public boolean isCurrentOverflowAllowed()
	{
		return currentOverflowAllowed;
	}

	private int getElementFirstY(JRFillElement element)
	{
		int elemFirstY;
		if (!isOverflow || hasPrintWhenOverflowElement)
		{
			elemFirstY = 0;
		}
		else if (element.getY() >= firstY)
		{
			elemFirstY = firstY;
		}
		else
		{
			elemFirstY = element.getY();
		}
		return elemFirstY;
	}

	protected void setStretchHeight(int stretchHeight)
	{
		this.stretchHeight = stretchHeight;
	}

	/**
	 *
	 */
	protected void stretchElementsToElementGroup()
	{
		if (stretchElements != null && stretchElements.length > 0)
		{
			for (JRFillElement element : stretchElements)
			{
				if (element.isToPrint())
				{
					boolean applied = element.stretchElementToElementGroup();
					
					if (applied)
					{
						element.moveDependantElements();
					}
				}
			}
		}
	}

	/**
	 *
	 */
	protected void stretchElementsToContainer()
	{
		if (stretchElements != null && stretchElements.length > 0)
		{
			int containerStretch = stretchHeight - getContainerHeight();
			
			for (JRFillElement element : stretchElements)
			{
				if (element.isToPrint())
				{
					boolean applied = element.stretchElementToContainer(containerStretch);
					
					if (applied)
					{
						element.moveDependantElements();
					}
				}
			}
		}
	}

	
	protected int getStretchHeight()
	{
		return stretchHeight;
	}

	
	/**
	 *
	 */
	protected void moveBandBottomElements()
	{
		//if (!willOverflow)
		//{
			if (bandBottomElements != null && bandBottomElements.length > 0)
			{
				for (JRFillElement element : bandBottomElements)
				{
					if (element.isToPrint())
					{
						// band bottom elements do not print if there will be an overflow
						if (currentOverflowWithElements || currentOverflowWithWhiteSpace)
						{
							currentOverflowWithElements = true;
						}
						
						element.setToPrint(!((currentOverflowWithElements || willOverflowWithWhiteSpace) && currentOverflowAllowed));// don't use willOverflow() method as it is overridden at least in bands
					}
					
					if (element.isToPrint())
					{
						element.setRelativeY(
							element.getY() + stretchHeight - getActualContainerHeight()
							);
					}
				}
			}
		//}
	}


	/**
	 *
	 */
	protected void removeBlankElements()
	{
		if (removableElements != null && removableElements.length > 0)
		{
			for (JRFillElement remElem : removableElements)
			{
				int blankHeight;
				if (remElem.isToPrint())
				{
					blankHeight = remElem.getHeight() - remElem.getStretchHeight();
				}
				else
				{
					blankHeight = remElem.getHeight();//FIXME subreports that stretch and then don't print, will not remove all space
				}
				
				if (
					blankHeight > 0 && 
					remElem.getRelativeY() + remElem.getStretchHeight() <= stretchHeight &&
					remElem.getRelativeY() >= firstY
					)
				{
					int blankY = remElem.getRelativeY() + remElem.getHeight() - blankHeight;
					boolean isToRemove = true;
					
					for (JRFillElement jElem : ySortedElements)
					{
						if (remElem != jElem && jElem.isToPrint())
						{
							int top = 
								Math.min(blankY, jElem.getRelativeY());
							int bottom = 
								Math.max(
									blankY + blankHeight, 
									jElem.getRelativeY() + jElem.getStretchHeight()
									);
							
							if (blankHeight + jElem.getStretchHeight() > bottom - top)
							{
								isToRemove = false;
								break;
							}
						}
					}
					
					if (isToRemove)
					{
						for (JRFillElement jElem : ySortedElements)
						{
							if (jElem.getRelativeY() + jElem.getStretchHeight() <= blankY)
							{
								jElem.setCollapsedHeightBelow(jElem.getCollapsedHeightBelow() + blankHeight);
							}

							if (jElem.getRelativeY() >= blankY + blankHeight)
							{
								jElem.setCollapsedHeightAbove(jElem.getCollapsedHeightAbove() + blankHeight);
								jElem.setRelativeY(jElem.getRelativeY() - blankHeight);
							}
						}
						
						stretchHeight = stretchHeight - blankHeight;
					}
				}
			}
		}
	}


	/**
	 * Fills the elements from this container into a print element container.
	 * 
	 * @param printContainer the print element container
	 * @throws JRException
	 */
	public void fillElements(JRPrintElementContainer printContainer) throws JRException
	{
		//int maxStretch = 0;
		//int stretch = 0;
		int maxWidth = 0;
		JRElement[] allElements = getElements();
		if (allElements != null && allElements.length > 0)
		{
			for (int i = 0; i < allElements.length; i++)
			{
				JRFillElement element = (JRFillElement)allElements[i];
				
				element.setRelativeY(element.getRelativeY() - firstY);

				if (element.getRelativeY() + element.getStretchHeight() > stretchHeight - firstY)
				{
					element.setToPrint(false);
				}
				
				element.setAlreadyPrinted(element.isToPrint() || element.isAlreadyPrinted());
				
				if (element.isToPrint())
				{
					JRPrintElement printElement = element.fill();
					//printElement.setY(printElement.getY() - firstY);

					if (printElement != null)
					{
						//FIXME not all elements affect height
						//stretch = printElement.getY() + firstY + printElement.getHeight() - element.getY() - element.getHeight();
						//if (stretch > maxStretch)
						//{
						//	maxStretch = stretch;
						//}
						printContainer.addElement(printElement);
						if (printElement.getX() + printElement.getWidth() > maxWidth)
						{
							maxWidth = printElement.getX() + printElement.getWidth();
						}
					}
					
					if (element instanceof JRFillSubreport)
					{
						JRFillSubreport subreport = (JRFillSubreport)element;
						
						List<JRStyle> styles = subreport.subreportFiller.getJasperPrint().getStylesList();
						for (JRStyle style : styles)
						{
							filler.addPrintStyle(style);
						}
						
						List<JROrigin> origins = subreport.subreportFiller.getJasperPrint().getOriginsList();
						for (JROrigin origin : origins)
						{
							filler.getJasperPrint().addOrigin(origin);
						}
						
						int parentDpi = filler.getDpi();
						int subreportDpi = subreport.subreportFiller.getDpi();
						double dpiScale = (parentDpi != subreportDpi) ? (double) parentDpi / subreportDpi : 1d;

						Collection<JRPrintElement> printElements = subreport.getPrintElements();
						addSubElements(printContainer, element, printElements, dpiScale);
						int contentsWidth = subreport.getPrintContentsWidth();
						if (dpiScale != 1d)
						{
							contentsWidth = (int) Math.round(contentsWidth * dpiScale);
						}
						if (subreport.getX() + contentsWidth > maxWidth)
						{
							maxWidth = subreport.getX() + contentsWidth;
						}
						
						subreport.subreportPageFilled();
					}
					
					// crosstabs do not return a fill() element
					if (element instanceof JRFillCrosstab)
					{
						JRFillCrosstab crosstab = (JRFillCrosstab) element;
						List<? extends JRPrintElement> printElements = crosstab.getPrintElements();
						addSubElements(printContainer, element, printElements);
						if (crosstab.getX() + crosstab.getPrintElementsWidth() > maxWidth)
						{
							maxWidth = crosstab.getX() + crosstab.getPrintElementsWidth();
						}
					}
				}
			}
		}
		
		//printBand.setHeight(getHeight() + maxStretch - firstY);
		printContainer.setHeight(stretchHeight - firstY);
		printContainer.setContentsWidth(maxWidth);
	}


	protected void addSubElements(JRPrintElementContainer printContainer, JRFillElement element, 
			Collection<? extends JRPrintElement> printElements)
	{
		addSubElements(printContainer, element, printElements, 1d);
	}

	protected void addSubElements(JRPrintElementContainer printContainer, JRFillElement element,
			Collection<? extends JRPrintElement> printElements, double dpiScale)
	{
		if (printContainer instanceof OffsetElementsContainer)
		{
			((OffsetElementsContainer) printContainer).addOffsetElements(printElements, 
					element.getX(), element.getRelativeY(), dpiScale);
		}
		else
		{
			if (printElements != null && printElements.size() > 0)
			{
				for (JRPrintElement printElement : printElements)
				{
					if (dpiScale != 1d)
					{
						printElement.setX(element.getX() + (int) Math.round(printElement.getX() * dpiScale));
						printElement.setY(element.getRelativeY() + (int) Math.round(printElement.getY() * dpiScale));
						printElement.setWidth((int) Math.round(printElement.getWidth() * dpiScale));
						printElement.setHeight((int) Math.round(printElement.getHeight() * dpiScale));
					}
					else
					{
						printElement.setX(element.getX() + printElement.getX());
						printElement.setY(element.getRelativeY() + printElement.getY());
					}
					printContainer.addElement(printElement);
				}
			}
		}
	}

	
	/**
	 *
	 */
	protected void rewind() throws JRException
	{
		if (ySortedElements != null && ySortedElements.length > 0)
		{
			for (JRFillElement element : ySortedElements)
			{
				element.rewind();

				element.setAlreadyPrinted(false);
			}
		}
		
		willOverflowWithElements = false;
		willOverflowWithWhiteSpace = false;
	}
	
	protected int getFirstY()
	{
		return firstY;
	}

	
	/**
	 * Returns the actual height of the element container.
	 * Some element containers such as frames have a larger calculated container height, resulting from content being placed beyond container declared height.
	 * 
	 * @return the height of the element container
	 */
	protected abstract int getActualContainerHeight();


	/**
	 * Returns the height of the element container.
	 * 
	 * @return the height of the element container
	 */
	protected abstract int getContainerHeight();


	/**
	 * Find all styles containing conditional styles which are referenced by elements in this band.
	 */
	protected void initConditionalStyles()
	{
		filler.addDefaultStyleListener(this::collectConditionalStyle);
		
		for (JRFillElement deepElement : deepElements)
		{
			JRStyle style = deepElement.initStyle;
			collectConditionalStyle(style);
		}
		
		if (deepElements.length > 0)
		{
			for (JRFillElement deepElement : deepElements)
			{
				deepElement.setConditionalStylesContainer(this);
			}
		}
	}

	protected void collectConditionalStyle(JRStyle style)
	{
		if (style != null)// && style.getConditionalStyles() != null)
		{
			stylesToEvaluate.add(style);
		}
	}


	protected void evaluateConditionalStyles(byte evaluation) throws JRException
	{
		for (Iterator<JRStyle> it = stylesToEvaluate.iterator(); it.hasNext();) 
		{
			evaluateConditionalStyle(it.next(), evaluation);
		}
	}


	protected JRStyle evaluateConditionalStyle(JRStyle initialStyle, byte evaluation) throws JRException
	{
		JRStyle consolidatedStyle = initialStyle;

		StringBuilder code = new StringBuilder();
		List<JRStyle> condStylesToApply = new ArrayList<>();
		
		boolean anyTrue = buildConsolidatedStyle(initialStyle, evaluation, code, condStylesToApply);
		
		if (anyTrue)
		{
			String consolidatedStyleName = initialStyle.getName() + "|" + code.toString();
			consolidatedStyle = filler.getJasperPrint().getStylesMap().get(consolidatedStyleName);
			if (consolidatedStyle == null)
			{
				JRBaseStyle style = new JRBaseStyle(initialStyle.getDefaultStyleProvider(), consolidatedStyleName);
				for (int j = condStylesToApply.size() - 1; j >= 0; j--)
				{
					StyleUtil.appendStyle(style, condStylesToApply.get(j));
				}

				// deduplicate to previously created identical instances
				style = filler.fillContext.deduplicate(style);
				filler.addPrintStyle(style);
				
				consolidatedStyle = style;
			}
		}

		evaluatedStyles.put(initialStyle, consolidatedStyle);
		
		return consolidatedStyle;
	}


	protected boolean buildConsolidatedStyle(JRStyle style, byte evaluation, StringBuilder code, List<JRStyle> condStylesToApply) throws JRException
	{
		boolean anyTrue = false;
		
		JRConditionalStyle[] conditionalStyles = style.getConditionalStyles();
		if (conditionalStyles != null && conditionalStyles.length > 0)
		{
			for (JRConditionalStyle conditionalStyle : conditionalStyles) 
			{
				Boolean expressionValue = 
					(Boolean) expressionEvaluator.evaluate(
						conditionalStyle.getConditionExpression(),
						evaluation
						);
				
				boolean condition;
				if (expressionValue == null)
				{
					condition = false;
				}
				else
				{
					condition = expressionValue;
				}
				
				code.append(condition ? '1' : '0');
				anyTrue = anyTrue | condition;

				if (condition)
				{
					condStylesToApply.add(conditionalStyle);
				}
			}
		}

		condStylesToApply.add(style);
		
		if (style.getStyle() != null)
		{
			anyTrue = anyTrue | buildConsolidatedStyle(style.getStyle(), evaluation, code, condStylesToApply);
		}
		return anyTrue;
	}


	public JRStyle getEvaluatedConditionalStyle(JRStyle parentStyle)
	{
		return evaluatedStyles.get(parentStyle);
	}
	
	protected final void setElementOriginProvider(JROriginProvider originProvider)
	{
		if (originProvider != null)
		{
			for (JRFillElement deepElement : deepElements)
			{
				deepElement.setOriginProvider(originProvider);
			}
		}
	}
}

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRFrame;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JRVisitor;
import net.sf.jasperreports.engine.base.JRBaseElementGroup;
import net.sf.jasperreports.engine.type.BorderSplitType;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.util.ElementsVisitorUtils;
import net.sf.jasperreports.engine.util.JRBoxUtil;
import net.sf.jasperreports.engine.util.StyleUtil;

/**
 * Fill time implementation of a frame element.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRFillFrame extends JRFillElement implements JRFrame
{
	protected final JRFrame parentFrame;
	
	protected final JRLineBox lineBox;
	
	protected final BorderSplitType borderSplitType;
	
	protected final boolean widthStretchEnabled;
	
	private final boolean overflowOnStretch;
	
	/**
	 * Element container used for filling.
	 */
	private JRFillFrameElements frameContainer;
	
	/**
	 * Template frame without the bottom border.
	 */
	private Map<JRStyle,JRTemplateElement> bottomTemplateFrames;
	
	/**
	 * Template frame without the top border
	 */
	private Map<JRStyle,JRTemplateElement> topTemplateFrames;
	
	/**
	 * Template frame without the top and bottom borders
	 */
	private Map<JRStyle,JRTemplateElement> topBottomTemplateFrames;
	
	private int currentAvailableHeight;
	private boolean fillTopBorder;
	private boolean fillBottomBorder;
	
	/**
	 * Whether the frame has started filling and not ended.
	 */
	private boolean filling;
	
	private JRLineBox styleLineBox;

	public JRFillFrame(JRBaseFiller filler, JRFrame frame, JRFillObjectFactory factory)
	{
		super(filler, frame, factory);
		
		parentFrame = frame;
		
		lineBox = frame.getLineBox().clone(this);
		borderSplitType = initBorderSplitType(filler, frame);
		widthStretchEnabled = initWidthStretchEnabled(filler, frame);
		overflowOnStretch = initOverflowOnStretch(filler, frame);
		
		frameContainer = new JRFillFrameElements(factory);
		
		bottomTemplateFrames = new HashMap<>();
		topTemplateFrames = new HashMap<>();
		topBottomTemplateFrames = new HashMap<>();
		
		setShrinkable(true);
	}

	protected JRFillFrame(JRFillFrame frame, JRFillCloneFactory factory)
	{
		super(frame, factory);
		
		parentFrame = frame.parentFrame;
		
		lineBox = frame.getLineBox().clone(this);
		borderSplitType = frame.borderSplitType;
		widthStretchEnabled = frame.widthStretchEnabled;
		overflowOnStretch = frame.overflowOnStretch;
		
		frameContainer = new JRFillFrameElements(frame.frameContainer, factory);
		
		bottomTemplateFrames = frame.bottomTemplateFrames;
		topTemplateFrames = frame.topTemplateFrames;
		topBottomTemplateFrames = frame.topBottomTemplateFrames;
	}
	
	private BorderSplitType initBorderSplitType(JRBaseFiller filler, JRFrame frame)
	{
		BorderSplitType splitType = frame.getBorderSplitType();
		if (splitType == null)
		{
			String splitTypeProp = filler.getPropertiesUtil().getProperty(filler.getJasperReport(), PROPERTY_BORDER_SPLIT_TYPE); // property expression does not work, 
			// but even if we would call filler.getMainDataset(), it would be too early as it is null here for frame elements placed in group bands
			if (splitTypeProp != null)
			{
				splitType = BorderSplitType.byName(splitTypeProp);
			}
		}
		return splitType;
	}
	
	private boolean initWidthStretchEnabled(JRBaseFiller filler, JRFrame frame)
	{
		boolean stretchDisabled = filler.getPropertiesUtil().getBooleanProperty(
				PROPERTY_FRAME_WIDTH_STRETCH_DISABLED, false, 
				frame, filler.getJasperReport());
		return !stretchDisabled;
	}
	
	private boolean initOverflowOnStretch(JRBaseFiller filler, JRFrame frame)
	{
		boolean overflowOnStretch = filler.getPropertiesUtil().getBooleanProperty(
				PROPERTY_OVERFLOW_ON_STRETCH, false, frame, filler.getJasperReport());
		return overflowOnStretch;
	}

	@Override
	public ModeEnum getMode()
	{
		return getStyleResolver().getMode(this, ModeEnum.TRANSPARENT);
	}

	@Override
	public Color getDefaultLineColor() 
	{
		return getForecolor();
	}

	
	@Override
	protected void evaluate(byte evaluation) throws JRException
	{
		reset();

		evaluatePrintWhenExpression(evaluation);
		if (isPrintWhenExpressionNull() || isPrintWhenTrue())
		{
			evaluateProperties(evaluation);
			evaluateStyle(evaluation);

			frameContainer.evaluate(evaluation);
			
			boolean repeating = true;
			JRFillElement[] elements = (JRFillElement[]) getElements();
			for (int i = 0; repeating && i < elements.length; i++)
			{
				repeating &= elements[i].isValueRepeating();
			}
			setValueRepeating(repeating);
		}
		
		filling = false;
	}

	@Override
	protected void evaluateStyle(byte evaluation) throws JRException
	{
		super.evaluateStyle(evaluation);
		
		styleLineBox = null;
		
		if (providerStyle != null)
		{
			styleLineBox = lineBox.clone(this);
			StyleUtil.appendBox(styleLineBox, providerStyle.getLineBox());
		}
	}
	
	@Override
	protected void rewind() throws JRException
	{
		frameContainer.rewind();
		
		filling = false;
	}

	protected boolean drawTopBorderOnSplit()
	{
		return borderSplitType == BorderSplitType.DRAW_BORDERS;
	}

	protected boolean drawBotomBorderOnSplit()
	{
		return borderSplitType == BorderSplitType.DRAW_BORDERS;
	}
	
	@Override
	protected boolean prepare(
		int availableHeight, 
		boolean isOverflow,
		boolean isOverflowAllowed
		) throws JRException
	{
		super.prepare(availableHeight, isOverflow, isOverflowAllowed);

		this.currentAvailableHeight = availableHeight;
		
		if (!isToPrint())
		{
			return false;
		}
		
		// whether the current frame chunk is the first one.
		boolean first = !isOverflow || !filling;
		
		int topPadding = getLineBox().getTopPadding();
		int bottomPadding = getLineBox().getBottomPadding();		
		
		if (!filling && !isPrintRepeatedValues() && isValueRepeating() &&
				(!isPrintInFirstWholeBand() || !getBand().isFirstWholeOnPageColumn()) &&
				(getPrintWhenGroupChanges() == null || !getBand().isNewGroup(getPrintWhenGroupChanges())) &&
				(!isOverflow || !isPrintWhenDetailOverflows())
			)
		{
			setToPrint(false);
			return false;
		}

		// FIXME reprinted when isAlreadyPrinted() || !isPrintRepeatedValues()?
		boolean printed = !filling && isOverflow && isAlreadyPrinted();
		if (printed && !isPrintWhenDetailOverflows())
		{
			setToPrint(false);
			return false;
		}
		
		if (availableHeight < getRelativeY() + getHeight() - topPadding - bottomPadding)
		{
			setToPrint(false);
			return true;
		}
		
		if (printed)
		{
			rewind();
			setReprinted(true);
		}
		
		frameContainer.initFill();
		frameContainer.resetElements();
		
		frameContainer.prepareElements(availableHeight - getRelativeY() - topPadding - bottomPadding, isOverflowAllowed);
		
		boolean willOverflow = frameContainer.willOverflow();
		fillTopBorder = first || drawTopBorderOnSplit();
		fillBottomBorder = !willOverflow || drawBotomBorderOnSplit();
		
		if (willOverflow)
		{
			setPrepareHeight(availableHeight - getRelativeY());
		}
		else
		{
			int neededStretch = frameContainer.getStretchHeight() - frameContainer.getFirstY() + topPadding + bottomPadding;
			if (neededStretch <= availableHeight - getRelativeY()) 
			{
				setPrepareHeight(neededStretch);
			}
			else
			{
				//FIXME is this case possible?
				setPrepareHeight(availableHeight - getRelativeY());
			}
		}

		filling = willOverflow;

		return willOverflow;
	}

	@Override
	protected void setStretchHeight(int stretchHeight)
	{
		super.setStretchHeight(stretchHeight);
		
		int topPadding = getLineBox().getTopPadding();
		int bottomPadding = getLineBox().getBottomPadding();		
		frameContainer.setStretchHeight(stretchHeight + frameContainer.getFirstY() - topPadding - bottomPadding);
		
		if (overflowOnStretch
				&& stretchHeight == this.currentAvailableHeight 
				&& fillContainerContext != null && fillContainerContext.isCurrentOverflow()
				&& !frameContainer.isCurrentOverflow())
		{
			frameContainer.willOverflowWithWhiteSpace = true;
			filling = true;
			fillBottomBorder = drawBotomBorderOnSplit();
		}
	}
	
	
	@Override
	protected boolean stretchElementToHeight(int stretchHeight)
	{
		boolean applied = super.stretchElementToHeight(stretchHeight); 
		if (applied)
		{
			frameContainer.stretchElementsToContainer();
			frameContainer.moveBandBottomElements();
		}
		return applied;
	}


	@Override
	protected JRPrintElement fill() throws JRException
	{		
		JRTemplatePrintFrame printFrame = new JRTemplatePrintFrame(getTemplate(), printElementOriginator);
		printFrame.setUUID(getUUID());
		printFrame.setX(getX());
		printFrame.setY(getRelativeY());
		
		VirtualizableFrame virtualizableFrame = new VirtualizableFrame(printFrame, 
				filler.getVirtualizationContext(), filler.getCurrentPage());
		frameContainer.fillElements(virtualizableFrame);
		virtualizableFrame.fill();
		
		int width = getWidth();
		if (widthStretchEnabled)
		{
			JRLineBox printBox = printFrame.getLineBox();
			int padding = (printBox.getLeftPadding() == null ? 0 : printBox.getLeftPadding())
					+ (printBox.getRightPadding() == null ? 0 : printBox.getRightPadding());
			if (virtualizableFrame.getContentsWidth() + padding > width)
			{
				width = virtualizableFrame.getContentsWidth() + padding;
			}
		}
		printFrame.setWidth(width);
		
		printFrame.setHeight(getStretchHeight());
		transferProperties(printFrame);
		
		return printFrame;
	}

	protected JRTemplateFrame getTemplate()
	{
		JRStyle style = getStyle();

		Map<JRStyle,JRTemplateElement> templatesMap;
		if (fillTopBorder)
		{
			if (fillBottomBorder)
			{
				templatesMap = templates;
			}
			else //remove the bottom border
			{
				templatesMap = bottomTemplateFrames;
			}
		}
		else
		{
			if (fillBottomBorder) //remove the top border
			{
				templatesMap = topTemplateFrames;
			}
			else //remove the top and bottom borders
			{
				templatesMap = topBottomTemplateFrames;
			}
		}
		
		JRTemplateFrame boxTemplate = (JRTemplateFrame) templatesMap.get(style);
		if (boxTemplate == null)
		{
			boxTemplate = createFrameTemplate();
			transferProperties(boxTemplate);
			
			//FIXME up to revision 2006 (Dec 5 2007) we were resetting both the border and the padding.
			// now we are only resetting the border and not the padding, prepare() assumes that the top and bottom paddings are always used.
			if (fillTopBorder)
			{
				if (!fillBottomBorder) //remove the bottom border
				{				
					boxTemplate.copyBox(getLineBox());
					JRBoxUtil.reset(boxTemplate.getLineBox(), false, false, false, true);
				}
			}
			else
			{
				if (fillBottomBorder) //remove the top border
				{
					boxTemplate.copyBox(getLineBox());
					JRBoxUtil.reset(boxTemplate.getLineBox(), false, false, true, false);
				}
				else //remove the top and bottom borders
				{
					boxTemplate.copyBox(getLineBox());
					JRBoxUtil.reset(boxTemplate.getLineBox(), false, false, true, true);					
				}
			}
			
			if (toPopulateTemplateStyle())
			{
				boxTemplate.populateStyle();
			}
			
			boxTemplate = filler.fillContext.deduplicate(boxTemplate);
			templatesMap.put(style, boxTemplate);
		}
		
		return boxTemplate;
	}

	protected JRTemplateFrame createFrameTemplate()
	{
		return new JRTemplateFrame(getElementOrigin(), 
				filler.getJasperPrint().getDefaultStyleProvider(), this);
	}

	@Override
	protected JRTemplateElement createElementTemplate()
	{
		return createFrameTemplate();
	}

	@Override
	protected void resolveElement(JRPrintElement element, byte evaluation)
	{
		// nothing
	}

	@Override
	public JRElement[] getElements()
	{
		return frameContainer.getElements();
	}
	
	@Override
	public List<JRChild> getChildren()
	{
		return frameContainer.getChildren();
	}

	@Override
	public void collectExpressions(JRExpressionCollector collector)
	{
		collector.collect(this);
	}

	@Override
	public JRLineBox getLineBox()
	{
		return styleLineBox == null ? lineBox : styleLineBox;
	}


	@Override
	public BorderSplitType getBorderSplitType()
	{
		return borderSplitType;
	}
	
	@Override
	public void visit(JRVisitor visitor)
	{
		visitor.visitFrame(this);
		
		if (ElementsVisitorUtils.visitDeepElements(visitor))
		{
			ElementsVisitorUtils.visitElements(visitor, getChildren());
		}
	}
	
	
	@Override
	public JRElement getElementByKey(String key)
	{
		return JRBaseElementGroup.getElementByKey(getElements(), key);
	}

	@Override
	public JRFillCloneable createClone(JRFillCloneFactory factory)
	{
		return new JRFillFrame(this, factory);
	}
	

	/**
	 * Frame element container filler.
	 */
	protected class JRFillFrameElements extends JRFillElementContainer
	{
		JRFillFrameElements(JRFillObjectFactory factory)
		{
			super(JRFillFrame.this.filler, parentFrame, factory);
			initElements();
		}

		JRFillFrameElements(JRFillFrameElements frameElements, JRFillCloneFactory factory)
		{
			super(frameElements, factory);
			initElements();
		}

		@Override
		protected int getContainerHeight()
		{
			return JRFillFrame.this.getHeight() - getLineBox().getTopPadding() - getLineBox().getBottomPadding(); 
		}

		@Override
		protected int getActualContainerHeight()
		{
			int containerHeight = JRFillFrame.this.getHeight() - getLineBox().getTopPadding() - getLineBox().getBottomPadding(); 
			
			if (JRFillFrame.this.frameContainer.bottomElementInGroup != null)
			{
				if (
					getLineBox().getTopPadding() 
					+ JRFillFrame.this.frameContainer.bottomElementInGroup.getY() 
					+ JRFillFrame.this.frameContainer.bottomElementInGroup.getHeight() > JRFillFrame.this.getHeight()
					)
				{
					containerHeight = 
						JRFillFrame.this.frameContainer.bottomElementInGroup.getY() 
						+ JRFillFrame.this.frameContainer.bottomElementInGroup.getHeight();
				}
			}

			return containerHeight; 
		}

		@Override
		public boolean isSplitTypePreventInhibited(boolean isTopLevelCall)
		{
			//not actually called because fillContainerContext in the children is actually the band
			return JRFillFrame.this.fillContainerContext.isSplitTypePreventInhibited(isTopLevelCall);
		}
	}
	
}

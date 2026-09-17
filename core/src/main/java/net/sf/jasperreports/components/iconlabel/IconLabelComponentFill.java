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
package net.sf.jasperreports.components.iconlabel;

import java.awt.Color;

import net.sf.jasperreports.engine.JRBoxContainer;
import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JRImageAlignment;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.base.JRBasePrintText;
import net.sf.jasperreports.engine.component.BaseFillComponent;
import net.sf.jasperreports.engine.component.ConditionalStyleAwareFillComponent;
import net.sf.jasperreports.engine.component.FillPrepareResult;
import net.sf.jasperreports.engine.component.StretchableFillComponent;
import net.sf.jasperreports.engine.fill.JRFillCloneFactory;
import net.sf.jasperreports.engine.fill.JRFillCloneable;
import net.sf.jasperreports.engine.fill.JRFillElementContainer;
import net.sf.jasperreports.engine.fill.JRFillObjectFactory;
import net.sf.jasperreports.engine.fill.JRFillTextField;
import net.sf.jasperreports.engine.fill.JRTemplateFrame;
import net.sf.jasperreports.engine.fill.JRTemplatePrintFrame;
import net.sf.jasperreports.engine.type.HorizontalImageAlignEnum;
import net.sf.jasperreports.engine.type.TextAdjustEnum;
import net.sf.jasperreports.engine.type.VerticalImageAlignEnum;
import net.sf.jasperreports.engine.util.JRBoxUtil;
import net.sf.jasperreports.engine.util.StyleResolver;
import net.sf.jasperreports.export.AccessibilityUtil;
import net.sf.jasperreports.export.type.AccessibilityTagEnum;

/**
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class IconLabelComponentFill extends BaseFillComponent implements StretchableFillComponent, ConditionalStyleAwareFillComponent, JRBoxContainer, JRImageAlignment
{
	private final IconLabelComponent iconLabelComponent;

	private final JRLineBox lineBox;
	private final JRFillTextField labelTextField;
	private final JRFillTextField iconTextField;
	
	//private	JRTemplateGenericElement template;
	private	JRTemplateFrame template;
	//private JRTemplateGenericPrintElement printElement;
	private JRTemplatePrintFrame printElement;
	private JRPrintText labelPrintText;
	private JRPrintText iconPrintText;
//	private boolean contentVisible;
	private boolean iconsVisible = true;
	private IconLabelDirectionEnum direction = IconLabelDirectionEnum.HORIZONTAL;
	private int middlePadding;
	
	private int stretchHeight;

	public IconLabelComponentFill(IconLabelComponent iconLabelComponent, JRFillObjectFactory factory)
	{
		this.iconLabelComponent = iconLabelComponent;
		this.lineBox = iconLabelComponent.getLineBox().clone(this);
		this.labelTextField = (JRFillTextField)factory.getVisitResult(iconLabelComponent.getLabelTextField());
		this.iconTextField = (JRFillTextField)factory.getVisitResult(iconLabelComponent.getIconTextField());
	}
	
	public IconLabelComponentFill(IconLabelComponent iconLabelComponent, JRFillCloneFactory factory)
	{
		this.iconLabelComponent = iconLabelComponent;
		this.lineBox = iconLabelComponent.getLineBox().clone(this);
		this.labelTextField = null;//FIXMEINPUT (JRFillTextField)factory.getVisitResult(iconLabelComponent.getTextField());
		this.iconTextField = null;//FIXMEINPUT (JRFillTextField)factory.getVisitResult(iconLabelComponent.getTextField());
	}
	
	protected IconLabelComponent getIconLabelComponent()
	{
		return iconLabelComponent;
	}
	
	@Override
	public void evaluate(byte evaluation) throws JRException
	{
		labelTextField.evaluate(evaluation);
		iconTextField.evaluate(evaluation);
	}

	/**
	 * Converts a length expressed in points, such as a font size, into the pixel length that the
	 * resolution of the report requires.
	 */
	private int toPixels(float points)
	{
		int reportDpi = fillContext.getFiller().getDpi();
		if (reportDpi == JasperPrint.DEFAULT_REPORT_DPI)
		{
			return (int)points;
		}
		return (int)(points * reportDpi / JasperPrint.DEFAULT_REPORT_DPI);
	}
	
	@Override
	public JRPrintElement fill()
	{
		JRComponentElement element = fillContext.getComponentElement();
		if (template == null) 
		{
			template = 
				new JRTemplateFrame(
					fillContext.getElementOrigin(), 
					fillContext.getDefaultStyleProvider()
					);
//			template = 
//				new JRTemplateGenericElement(
//					fillContext.getElementOrigin(), 
//					fillContext.getDefaultStyleProvider(),
//					IconLabelElement.ICONLABEL_ELEMENT_TYPE
//					);
		
			template.setStyle(fillContext.getComponentElement().getStyle());
			template.setMode(fillContext.getComponentElement().getOwnMode());
			template.setBackcolor(fillContext.getComponentElement().getOwnBackcolor());
			template.setForecolor(fillContext.getComponentElement().getOwnForecolor());
			JRBoxUtil.copy(getLineBox(), template.getLineBox());
			
			template = deduplicate(template);
		}
		
		printElement = new JRTemplatePrintFrame(template, printElementOriginator);
//		printElement = new JRTemplateGenericPrintElement(template, elementId);
		printElement.setUUID(element.getUUID());
		printElement.setX(element.getX());
		printElement.setY(fillContext.getElementPrintY());

		printElement.setWidth(element.getWidth());
		//printElement.setHeight(element.getHeight());
		printElement.setHeight(stretchHeight);
		
		fillContext.getFiller().getPropertiesUtil().transferProperties(
			iconLabelComponent.getContext().getComponentElement(),//FIXMEICONLABEL copy from fill element? 
			printElement, JasperPrint.PROPERTIES_PRINT_TRANSFER_PREFIX
			);

		printElement.getPropertiesMap().setProperty(AccessibilityUtil.PROPERTY_ACCESSIBILITY_TAG, AccessibilityTagEnum.TABLE_LAYOUT.getName());
		
//		if (contentVisible)
//		{
			if (direction == IconLabelDirectionEnum.HORIZONTAL)
			{
				fillHorizontal();
			}
			else
			{
				fillVertical();
			}
//		}
		
		copy(printElement);
		
		return printElement;
	}
	
	public void fillHorizontal()
	{
		if (labelTextField.isToPrint())
		{
			try
			{
				labelPrintText = (JRPrintText)labelTextField.fill();
			}
			catch (JRException e)
			{
				throw new JRRuntimeException(e);
			}
		}
		else
		{
			// create dummy print text to keep position and size calculations below simple
			labelPrintText = new JRBasePrintText(getDefaultStyleProvider());
			labelPrintText.setX(labelTextField.getX());
			labelPrintText.setY(labelTextField.getX());
			labelPrintText.setWidth(labelTextField.getWidth());
			labelPrintText.setHeight(labelTextField.getHeight());
		}
		
		if (
			iconLabelComponent.getLabelFill() != ContainerFillEnum.HORIZONTAL
			&& iconLabelComponent.getLabelFill() != ContainerFillEnum.BOTH
			)
		{
			int calculatedLabelWidth =
				(int)labelTextField.getTextWidth() 
				+ labelTextField.getLineBox().getLeftPadding() 
				+ labelTextField.getLineBox().getRightPadding()
				+ toPixels(3);//we do +3 to avoid text wrap in html (+1 was enough for pdf)
			labelPrintText.setWidth(Math.min(labelTextField.getWidth(), calculatedLabelWidth));//for some reason, calculated text width seems to be larger then available text width
		}
		
		if (
			iconLabelComponent.getLabelFill() == ContainerFillEnum.VERTICAL
			|| iconLabelComponent.getLabelFill() == ContainerFillEnum.BOTH
			)
		{
			labelPrintText.setHeight(
				Math.max(
					labelTextField.getStretchHeight(), 
					stretchHeight 
						- getLineBox().getTopPadding()
						- getLineBox().getBottomPadding()
					)
				);
		}

		if (iconTextField.isToPrint())
		{
			try
			{
				iconPrintText = (JRPrintText)iconTextField.fill();
			}
			catch (JRException e)
			{
				throw new JRRuntimeException(e);
			}
		}
		else
		{
			// create dummy print text to keep position and size calculations below simple
			iconPrintText = new JRBasePrintText(getDefaultStyleProvider());
			iconPrintText.setX(iconTextField.getX());
			iconPrintText.setY(iconTextField.getX());
			iconPrintText.setWidth(iconTextField.getWidth());
			iconPrintText.setHeight(iconTextField.getHeight());
		}
		
		iconPrintText.setWidth(
			(int)Math.ceil(
				iconTextField.getTextWidth()
				+ iconTextField.getLineBox().getLeftPadding() 
				+ iconTextField.getLineBox().getRightPadding() 
				)
			);
		
		int commonHeight = Math.max(labelPrintText.getHeight(), iconPrintText.getHeight());
		labelPrintText.setHeight(commonHeight);
		iconPrintText.setHeight(commonHeight);
		
		switch (getHorizontalImageAlign())
		{
			case LEFT :
			{
				if (iconLabelComponent.getIconPosition() == IconPositionEnum.START)
				{
					labelPrintText.setX(iconPrintText.getWidth() + middlePadding);
					iconPrintText.setX(0);
				}
				else
				{
					labelPrintText.setX(0);
					iconPrintText.setX(labelPrintText.getWidth() + middlePadding);
				}
				break;
			}
			case CENTER :
			{
				if (iconLabelComponent.getIconPosition() == IconPositionEnum.START)
				{
					iconPrintText.setX(
						(
						iconLabelComponent.getContext().getComponentElement().getWidth() 
						- getLineBox().getLeftPadding()
						- getLineBox().getRightPadding()
						- middlePadding
						- labelPrintText.getWidth() 
						- iconPrintText.getWidth()
						) / 2
						);
					labelPrintText.setX(iconPrintText.getX() + iconPrintText.getWidth() + middlePadding);
				}
				else
				{
					labelPrintText.setX(
						(
						iconLabelComponent.getContext().getComponentElement().getWidth() 
						- getLineBox().getLeftPadding()
						- getLineBox().getRightPadding()
						- middlePadding
						- labelPrintText.getWidth() 
						- iconPrintText.getWidth()
						) / 2
						);
					iconPrintText.setX(labelPrintText.getX() + labelPrintText.getWidth() + middlePadding);
				}
				break;
			}
			case RIGHT :
			{
				if (iconLabelComponent.getIconPosition() == IconPositionEnum.START)
				{
					labelPrintText.setX(
						iconLabelComponent.getContext().getComponentElement().getWidth()
						- getLineBox().getLeftPadding()
						- getLineBox().getRightPadding()
						- labelPrintText.getWidth()
						);
					iconPrintText.setX(labelPrintText.getX() - iconPrintText.getWidth() - middlePadding);
				}
				else
				{
					iconPrintText.setX(
						iconLabelComponent.getContext().getComponentElement().getWidth() 
						- getLineBox().getLeftPadding()
						- getLineBox().getRightPadding()
						- iconPrintText.getWidth()
						);
					labelPrintText.setX(iconPrintText.getX() - labelPrintText.getWidth() - middlePadding);
				}
				break;
			}
		}

		switch (getVerticalImageAlign())
		{
			case TOP :
			{
				labelPrintText.setY(0);
				iconPrintText.setY(0);
				break;
			}
			case MIDDLE :
			{
				labelPrintText.setY(
					(
					stretchHeight
					- getLineBox().getTopPadding()
					- getLineBox().getBottomPadding()
					- labelPrintText.getHeight()
					) / 2
					);
				iconPrintText.setY(
					(
					stretchHeight
					- getLineBox().getTopPadding()
					- getLineBox().getBottomPadding()
					- iconPrintText.getHeight()
					) / 2
					);
				break;
			}
			case BOTTOM :
			{
				labelPrintText.setY(
					stretchHeight
					- getLineBox().getTopPadding()
					- getLineBox().getBottomPadding()
					- labelPrintText.getHeight()
					);
				iconPrintText.setY(
					stretchHeight
					- getLineBox().getTopPadding()
					- getLineBox().getBottomPadding()
					- iconPrintText.getHeight()
					);
				break;
			}
		}
	}

	
	public void fillVertical()
	{
		try
		{
			labelPrintText = (JRPrintText)labelTextField.fill();
		}
		catch (JRException e)
		{
			throw new JRRuntimeException(e);
		}
		
//		if (
//			iconLabelComponent.getLabelFill() != ContainerFillEnum.HORIZONTAL
//			&& iconLabelComponent.getLabelFill() != ContainerFillEnum.BOTH
//			)
//		{
//			int calculatedLabelWidth =
//				(int)labelTextField.getTextWidth() 
//				+ labelTextField.getLineBox().getLeftPadding() 
//				+ labelTextField.getLineBox().getRightPadding() 
//				+ 3;//we do +3 to avoid text wrap in html (+1 was enough for pdf)
//			labelPrintText.setWidth(Math.min(labelTextField.getWidth(), calculatedLabelWidth));//for some reason, calculated text width seems to be larger then available text width
//		}
		
		if (
			iconLabelComponent.getLabelFill() == ContainerFillEnum.VERTICAL
			|| iconLabelComponent.getLabelFill() == ContainerFillEnum.BOTH
			)
		{
			labelPrintText.setHeight(
				Math.max(
					labelTextField.getStretchHeight(), 
					stretchHeight - (direction == IconLabelDirectionEnum.HORIZONTAL ? 0 : (iconTextField.getStretchHeight() + middlePadding))
						- getLineBox().getTopPadding()
						- getLineBox().getBottomPadding()
					)
				);
		}

		try
		{
			iconPrintText = (JRPrintText)iconTextField.fill();
		}
		catch (JRException e)
		{
			throw new JRRuntimeException(e);
		}

//		iconPrintText.setWidth(
//			(int)iconTextField.getTextWidth()
//			+ iconTextField.getLineBox().getLeftPadding() 
//			+ iconTextField.getLineBox().getRightPadding() 
//			);
		
//		int commonHeight = Math.max(labelPrintText.getHeight(), iconPrintText.getHeight());
//		labelPrintText.setHeight(commonHeight);
//		iconPrintText.setHeight(commonHeight);
		
		switch (getHorizontalImageAlign())
		{
			case LEFT :
			{
				labelPrintText.setX(0);
				iconPrintText.setX(0);
				break;
			}
			case CENTER :
			{
				labelPrintText.setX(
					(
					iconLabelComponent.getContext().getComponentElement().getWidth()
					- getLineBox().getLeftPadding()
					- getLineBox().getRightPadding()
					- labelPrintText.getWidth()
					) / 2
					);
				iconPrintText.setX(
					(
					iconLabelComponent.getContext().getComponentElement().getWidth()
					- getLineBox().getLeftPadding()
					- getLineBox().getRightPadding()
					- iconPrintText.getWidth()
					) / 2
					);
				break;
			}
			case RIGHT :
			{
				labelPrintText.setX(
					iconLabelComponent.getContext().getComponentElement().getWidth()
					- getLineBox().getLeftPadding()
					- getLineBox().getRightPadding()
					- labelPrintText.getWidth()
					);
				iconPrintText.setX(
					iconLabelComponent.getContext().getComponentElement().getWidth()
					- getLineBox().getLeftPadding()
					- getLineBox().getRightPadding()
					- iconPrintText.getWidth()
					);
				break;
			}
		}
		
		switch (getVerticalImageAlign())
		{
			case TOP :
			{
				if (iconLabelComponent.getIconPosition() == IconPositionEnum.START)
				{
					labelPrintText.setY(iconPrintText.getHeight() + middlePadding);
					iconPrintText.setY(0);
				}
				else
				{
					labelPrintText.setY(0);
					iconPrintText.setY(labelPrintText.getHeight() + middlePadding);
				}
				break;
			}
			case MIDDLE :
			{
				if (iconLabelComponent.getIconPosition() == IconPositionEnum.START)
				{
					iconPrintText.setY(
						(
						stretchHeight 
						- getLineBox().getTopPadding()
						- getLineBox().getBottomPadding()
						- middlePadding
						- labelPrintText.getHeight() 
						- iconPrintText.getHeight()
						) / 2
						);
					labelPrintText.setY(iconPrintText.getY() + iconPrintText.getHeight() + middlePadding);
				}
				else
				{
					labelPrintText.setY(
						(
						stretchHeight 
						- getLineBox().getTopPadding()
						- getLineBox().getBottomPadding()
						- middlePadding
						- labelPrintText.getHeight() 
						- iconPrintText.getHeight()
						) / 2
						);
					iconPrintText.setY(labelPrintText.getY() + labelPrintText.getHeight() + middlePadding);
				}
				break;
			}
			case BOTTOM :
			{
				if (iconLabelComponent.getIconPosition() == IconPositionEnum.START)
				{
					labelPrintText.setY(
						stretchHeight 
						- getLineBox().getTopPadding()
						- getLineBox().getBottomPadding()
						- labelPrintText.getHeight()
						);
					iconPrintText.setY(labelPrintText.getY() - iconPrintText.getHeight() - middlePadding);
				}
				else
				{
					iconPrintText.setY(
						stretchHeight 
						- getLineBox().getTopPadding()
						- getLineBox().getBottomPadding()
						- iconPrintText.getHeight()
						);
					labelPrintText.setY(iconPrintText.getY() - labelPrintText.getHeight() - middlePadding);
				}
				break;
			}
		}
	}

	@Override
	public void setStretchHeight(int stretchHeight) //FIXMEICONLABEL the text fields inside the icon label component do not actually participate into stretch behavior
	{
		this.stretchHeight = stretchHeight;
	}
	
	@Override
	public void setConditionalStylesContainer(JRFillElementContainer conditionalStylesContainer)
	{
		labelTextField.setConditionalStylesContainer(conditionalStylesContainer);
		iconTextField.setConditionalStylesContainer(conditionalStylesContainer);
	}

	@SuppressWarnings("deprecation")
	@Override
	public FillPrepareResult prepare(int availableHeight)
	{
		return prepare(availableHeight, true);
	}

	@Override
	public FillPrepareResult prepare(int availableHeight, boolean isOverflowAllowed)
	{
		float paddingDiff = 
			iconLabelComponent.getContext().getComponentElement().getWidth()
			- getLineBox().getLeftPadding() 
			- getLineBox().getRightPadding()
			- toPixels(1);

		if (paddingDiff < 0)
		{
			paddingDiff = paddingDiff / 2;

			int leftPadding = getLineBox().getLeftPadding() + (int)Math.floor(paddingDiff);
			leftPadding = leftPadding < 0 ? 0 : leftPadding;
			getLineBox().setLeftPadding((Integer)leftPadding);
			
			int rightPadding = getLineBox().getRightPadding() + (int)Math.ceil(paddingDiff);
			rightPadding = rightPadding < 0 ? 0 : rightPadding;
			getLineBox().setRightPadding((Integer)rightPadding);
		}

		int availableWidth = 
			iconLabelComponent.getContext().getComponentElement().getWidth()
			- getLineBox().getLeftPadding() 
			- getLineBox().getRightPadding();

		middlePadding = toPixels(iconTextField.getFontSize() / 2);

		if (availableWidth <= middlePadding)
		{
			middlePadding = 0; 
		}
		else
		{
			middlePadding = Math.min(availableWidth - middlePadding, middlePadding); 
		}

		availableWidth = 
			iconLabelComponent.getContext().getComponentElement().getWidth()
			- getLineBox().getLeftPadding() 
			- getLineBox().getRightPadding()
			- middlePadding;

//		if (availableWidth <= 0)
//		{
//			return FillPrepareResult.printStretch(iconLabelComponent.getContext().getComponentElement().getHeight(), false);
//		}
//
//		contentVisible = true;

		int textAvailableHeight = 
			availableHeight
			- getLineBox().getTopPadding()
			- getLineBox().getBottomPadding();
		
		iconTextField.setWidth(availableWidth);
		
		boolean overflow = false;
		
		try
		{
			// in the absence of the real overflow flag, passing true is best,
			// because on the first prepare attempt, all text is still remaining to be rendered
			overflow = iconTextField.prepare(textAvailableHeight, true, isOverflowAllowed);
		}
		catch (JRException e)
		{
			throw new JRRuntimeException(e);
		}
		
		boolean changeDirection = false;
		int labelAvailableWidth;

		int iconsUsedWidth = 0; 

		if (iconTextField.getTextWidth() > iconTextField.getWidth() - iconTextField.getLineBox().getLeftPadding() - iconTextField.getLineBox().getRightPadding())
		{
			changeDirection = true;
		}
		else
		{
			iconsUsedWidth = 
				(int)iconTextField.getTextWidth() 
				+ iconTextField.getLineBox().getLeftPadding() 
				+ iconTextField.getLineBox().getRightPadding();
			changeDirection = availableWidth <= iconsUsedWidth;
			labelAvailableWidth = changeDirection ? availableWidth : (availableWidth - iconsUsedWidth);
		}
		
		if (changeDirection)
		{
			labelAvailableWidth = availableWidth + middlePadding;
		}
		else
		{
			labelAvailableWidth = availableWidth - iconsUsedWidth;
		}
		
		labelTextField.setWidth(labelAvailableWidth);

		try
		{
			// in the absence of the real overflow flag, passing true is best,
			// because on the first prepare attempt, all text is still remaining to be rendered
			overflow = labelTextField.prepare(textAvailableHeight, true, isOverflowAllowed) || overflow;
		}
		catch (JRException e)
		{
			throw new JRRuntimeException(e);
		}

		if (
			changeDirection 
//			&& labelTextField.getTextWidth() > labelTextField.getWidth() - labelTextField.getLineBox().getLeftPadding() - labelTextField.getLineBox().getRightPadding()
			)//FIXMEICONLABEL here we might get to hide icons simply because label is blank
		{
			direction = IconLabelDirectionEnum.VERTICAL;
			middlePadding = toPixels(iconTextField.getFontSize() / 2);
//			labelTextField.setWidth(availableWidth);
			int iconAvailableHeight =
				(labelTextField.getTextAdjust() == TextAdjustEnum.STRETCH_HEIGHT
				? textAvailableHeight - labelTextField.getStretchHeight() 
				: iconLabelComponent.getContext().getComponentElement().getHeight() - labelTextField.getStretchHeight())
				 - middlePadding;
			iconsVisible = iconAvailableHeight > 0;
			if (iconsVisible)
			{
				try
				{
//					labelTextField.rewind();
//					labelTextField.prepare(textAvailableHeight - iconTextField.getStretchHeight(), fillContext.getFillContainerContext().isCurrentOverflow());
					iconTextField.rewind();
					iconTextField.reset();
					iconTextField.setWidth(labelAvailableWidth);
					overflow = iconTextField.prepare(iconAvailableHeight, true, isOverflowAllowed) || overflow; // overflow true is the next best thing, when not having the real thing
					iconsVisible = iconTextField.getStretchHeight() <= iconAvailableHeight;
					//iconsVisible = iconTextField.getPrintElementHeight() <= iconAvailableHeight;
				}
				catch (JRException e)
				{
					throw new JRRuntimeException(e);
				}
			}
		}
		
		stretchHeight = 
			(direction == IconLabelDirectionEnum.HORIZONTAL 
				? Math.max(labelTextField.getStretchHeight(), (iconsVisible ? iconTextField.getStretchHeight() : 0))
				: (labelTextField.getStretchHeight() + (iconsVisible ? iconTextField.getStretchHeight() + middlePadding : 0)))
			+ getLineBox().getTopPadding()
			+ getLineBox().getBottomPadding();

		return FillPrepareResult.printStretch(stretchHeight, overflow);
	}
	
	public JRFillCloneable createClone(JRFillCloneFactory factory)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void evaluateDelayedElement(JRPrintElement element, byte evaluation) throws JRException
	{
		evaluate(evaluation);
		copy((JRGenericPrintElement) element);
	}

	protected void copy(JRGenericPrintElement printElement)
	{
		printElement.setParameterValue(IconLabelElement.PARAMETER_LINE_BOX, iconLabelComponent.getLineBox().clone(null));
		printElement.setParameterValue(IconLabelElement.PARAMETER_LABEL_TEXT_ELEMENT, labelPrintText);
		printElement.setParameterValue(IconLabelElement.PARAMETER_ICON_TEXT_ELEMENT, iconPrintText);
	}

	protected void copy(JRPrintFrame printFrame)
	{
		//printElement.iconLabelComponent.getLineBox().clone(printElement);
//		if (contentVisible)
//		{
			labelTextField.setAlreadyPrinted(labelTextField.isToPrint() || labelTextField.isAlreadyPrinted());
			iconTextField.setAlreadyPrinted(iconTextField.isToPrint() || iconTextField.isAlreadyPrinted());

			if (labelTextField.isToPrint())
			{
				printElement.addElement(labelPrintText);
			}
			if (iconTextField.isToPrint() && iconsVisible)
			{
				printElement.addElement(iconPrintText);
			}
//		}
	}

	@Override
	public Color getDefaultLineColor() 
	{
		return Color.black;
	}

	@Override
	public JRDefaultStyleProvider getDefaultStyleProvider() {
		return fillContext.getComponentElement().getDefaultStyleProvider();
	}

	protected StyleResolver getStyleResolver() {
		return getDefaultStyleProvider().getStyleResolver();
	}

	@Override
	public JRStyle getStyle() {
		return fillContext.getComponentElement().getStyle();
	}

	@Override
	public String getStyleNameReference() {
		return fillContext.getComponentElement().getStyleNameReference();
	}

	@Override
	public JRLineBox getLineBox() {
		return lineBox;
	}

	@Override
	public HorizontalImageAlignEnum getHorizontalImageAlign()
	{
		return getStyleResolver().getHorizontalImageAlign(this);
	}
		
	@Override
	public HorizontalImageAlignEnum getOwnHorizontalImageAlign()
	{
		return iconLabelComponent.getOwnHorizontalImageAlign();
	}

	@Override
	public void setHorizontalImageAlign(HorizontalImageAlignEnum horizontalAlignment)
	{
		throw new UnsupportedOperationException();
	}
		
	@Override
	public VerticalImageAlignEnum getVerticalImageAlign()
	{
		return getStyleResolver().getVerticalImageAlign(this);
	}
		
	@Override
	public VerticalImageAlignEnum getOwnVerticalImageAlign()
	{
		return iconLabelComponent.getOwnVerticalImageAlign();
	}

	@Override
	public void setVerticalImageAlign(VerticalImageAlignEnum verticalAlignment)
	{
		throw new UnsupportedOperationException();
	}
}

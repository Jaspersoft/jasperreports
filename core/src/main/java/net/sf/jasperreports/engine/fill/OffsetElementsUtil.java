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

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import net.sf.jasperreports.engine.JRBoxContainer;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRParagraph;
import net.sf.jasperreports.engine.JRParagraphContainer;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.TabStop;
import net.sf.jasperreports.engine.base.VirtualizableElementList;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class OffsetElementsUtil
{

	public static void transfer(List<Object> elements, Consumer<JRPrintElement> consumer)
	{
		elements.stream().forEach(item ->
		{
			if (item instanceof JRPrintElement)
			{
				consumer.accept((JRPrintElement) item);
			}
			else
			{
				OffsetElements offsetElements = (OffsetElements) item;
				double dpiScale = offsetElements.getDpiScale();
				Consumer<JRPrintElement> offsetElementConsumer;
				if (dpiScale != 1d)
				{
					offsetElementConsumer = (element ->
					{
						element.setX(offsetElements.getOffsetX() + (int) Math.round(element.getX() * dpiScale));
						element.setY(offsetElements.getOffsetY() + (int) Math.round(element.getY() * dpiScale));
						element.setWidth((int) Math.round(element.getWidth() * dpiScale));
						element.setHeight((int) Math.round(element.getHeight() * dpiScale));
						scaleChildren(element, dpiScale);
					});
				}
				else
				{
					offsetElementConsumer = (element ->
					{
						element.setX(offsetElements.getOffsetX() + element.getX());
						element.setY(offsetElements.getOffsetY() + element.getY());
					});
				}
				offsetElementConsumer = offsetElementConsumer.andThen(consumer);
				
				Collection<? extends JRPrintElement> subElements = offsetElements.getElements();
				if (subElements instanceof VirtualizableElementList)
				{
					((VirtualizableElementList) subElements).transferElements(offsetElementConsumer);
				}
				else
				{
					subElements.stream().forEach(offsetElementConsumer);
					subElements.clear();
				}
			}
		});
	}

	static void scaleChildren(JRPrintElement element, double dpiScale)
	{
		scaleTextProperties(element, dpiScale);
		if (element instanceof JRPrintFrame)
		{
			for (JRPrintElement child : ((JRPrintFrame) element).getElements())
			{
				child.setX((int) Math.round(child.getX() * dpiScale));
				child.setY((int) Math.round(child.getY() * dpiScale));
				child.setWidth((int) Math.round(child.getWidth() * dpiScale));
				child.setHeight((int) Math.round(child.getHeight() * dpiScale));
				scaleChildren(child, dpiScale);
			}
		}
	}

	static void scaleTextProperties(JRPrintElement element, double dpiScale)
	{
		if (element instanceof JRPrintText)
		{
			JRPrintText textElement = (JRPrintText) element;
			textElement.setTextHeight((float)(textElement.getTextHeight() * dpiScale));
			textElement.setLeadingOffset((float)(textElement.getLeadingOffset() * dpiScale));
			Float avgCharWidth = textElement.getAverageCharWidth();
			if (avgCharWidth != null)
			{
				textElement.setAverageCharWidth((float)(avgCharWidth * dpiScale));
			}
		}
	}

	static void scaleTemplatePenWidths(JRPrintElement element, double dpiScale, Set<Object> scaledTemplates)
	{
		if (element instanceof JRTemplatePrintElement)
		{
			JRTemplateElement template = ((JRTemplatePrintElement) element).getTemplate();
			if (template != null && scaledTemplates.add(template))
			{
				if (template instanceof JRTemplateGraphicElement)
				{
					scalePenWidth(((JRTemplateGraphicElement) template).getLinePen(), dpiScale);
				}
				if (template instanceof JRBoxContainer)
				{
					scaleLineBoxPens(((JRBoxContainer) template).getLineBox(), dpiScale);
				}
				if (template instanceof JRParagraphContainer)
				{
					scaleParagraph(((JRParagraphContainer) template).getParagraph(), dpiScale);
				}
			}
		}
		if (element instanceof JRPrintFrame)
		{
			for (JRPrintElement child : ((JRPrintFrame) element).getElements())
			{
				scaleTemplatePenWidths(child, dpiScale, scaledTemplates);
			}
		}
	}

	private static void scalePenWidth(JRPen pen, double dpiScale)
	{
		Float ownLineWidth = pen.getOwnLineWidth();
		if (ownLineWidth != null)
		{
			pen.setLineWidth((float)(ownLineWidth * dpiScale));
		}
	}

	private static void scaleLineBoxPens(JRLineBox lineBox, double dpiScale)
	{
		scalePenWidth(lineBox.getPen(), dpiScale);
		scalePenWidth(lineBox.getTopPen(), dpiScale);
		scalePenWidth(lineBox.getLeftPen(), dpiScale);
		scalePenWidth(lineBox.getBottomPen(), dpiScale);
		scalePenWidth(lineBox.getRightPen(), dpiScale);

		scaleBoxPadding(lineBox, dpiScale);
	}

	private static void scaleParagraph(JRParagraph paragraph, double dpiScale)
	{
		Integer ownLeftIndent = paragraph.getOwnLeftIndent();
		if (ownLeftIndent != null)
		{
			paragraph.setLeftIndent((int) Math.round(ownLeftIndent * dpiScale));
		}
		Integer ownFirstLineIndent = paragraph.getOwnFirstLineIndent();
		if (ownFirstLineIndent != null)
		{
			paragraph.setFirstLineIndent((int) Math.round(ownFirstLineIndent * dpiScale));
		}
		Integer ownRightIndent = paragraph.getOwnRightIndent();
		if (ownRightIndent != null)
		{
			paragraph.setRightIndent((int) Math.round(ownRightIndent * dpiScale));
		}
		Integer ownSpacingBefore = paragraph.getOwnSpacingBefore();
		if (ownSpacingBefore != null)
		{
			paragraph.setSpacingBefore((int) Math.round(ownSpacingBefore * dpiScale));
		}
		Integer ownSpacingAfter = paragraph.getOwnSpacingAfter();
		if (ownSpacingAfter != null)
		{
			paragraph.setSpacingAfter((int) Math.round(ownSpacingAfter * dpiScale));
		}
		Integer ownTabStopWidth = paragraph.getOwnTabStopWidth();
		if (ownTabStopWidth != null)
		{
			paragraph.setTabStopWidth((int) Math.round(ownTabStopWidth * dpiScale));
		}
		TabStop[] ownTabStops = paragraph.getOwnTabStops();
		if (ownTabStops != null)
		{
			for (TabStop tabStop : ownTabStops)
			{
				tabStop.setPosition((int) Math.round(tabStop.getPosition() * dpiScale));
			}
		}
	}

	private static void scaleBoxPadding(JRLineBox lineBox, double dpiScale)
	{
		Integer ownPadding = lineBox.getOwnPadding();
		if (ownPadding != null)
		{
			lineBox.setPadding((int) Math.round(ownPadding * dpiScale));
		}
		Integer ownTopPadding = lineBox.getOwnTopPadding();
		if (ownTopPadding != null)
		{
			lineBox.setTopPadding((int) Math.round(ownTopPadding * dpiScale));
		}
		Integer ownLeftPadding = lineBox.getOwnLeftPadding();
		if (ownLeftPadding != null)
		{
			lineBox.setLeftPadding((int) Math.round(ownLeftPadding * dpiScale));
		}
		Integer ownBottomPadding = lineBox.getOwnBottomPadding();
		if (ownBottomPadding != null)
		{
			lineBox.setBottomPadding((int) Math.round(ownBottomPadding * dpiScale));
		}
		Integer ownRightPadding = lineBox.getOwnRightPadding();
		if (ownRightPadding != null)
		{
			lineBox.setRightPadding((int) Math.round(ownRightPadding * dpiScale));
		}
	}

}

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

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRStaticText;
import net.sf.jasperreports.engine.JRVisitor;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRFillStaticText extends JRFillTextElement implements JRStaticText
{

	private final String text;

	/**
	 *
	 */
	protected JRFillStaticText(
			JRBaseFiller filler,
			JRStaticText staticText, 
			JRFillObjectFactory factory
			)
		{
			super(filler, staticText, factory);
			
			String text = processMarkupText(staticText.getText());
			if (text == null)
			{
				text = "";
			}
			this.text = text;
		}


	protected JRFillStaticText(JRFillStaticText staticText, JRFillCloneFactory factory)
	{
		super(staticText, factory);

		this.text = staticText.text;
	}


	@Override
	public void setText(String text)
	{
	}


	/**
	 *
	 */
	protected JRTemplateText getJRTemplateText()
	{
		return (JRTemplateText) getElementTemplate();
	}


	@Override
	protected JRTemplateElement createElementTemplate()
	{
		JRTemplateText template = new JRTemplateText(
				getElementOrigin(), 
				filler.getJasperPrint().getDefaultStyleProvider(), 
				this
				);
		template.copyParagraph(getPrintParagraph());
		template.copyLineBox(getPrintLineBox());
		return template;
	}


	@Override
	protected void evaluate(
		byte evaluation
		) throws JRException
	{
		reset();
		
		evaluatePrintWhenExpression(evaluation);
		evaluateProperties(evaluation);
		evaluateStyle(evaluation);

		//setting the text each time so that super.rewind() works fine
		setRawText(this.text);
		resetTextChunk();
		
		setValueRepeating(true);
	}


	@Override
	protected boolean prepare(
		int availableHeight,
		boolean isOverflow,
		boolean isOverflowAllowed
		) throws JRException
	{
		boolean willOverflow = false;

		super.prepare(availableHeight, isOverflow, isOverflowAllowed);
		
		if (!isToPrint())
		{
			return willOverflow;
		}
		
		boolean isToPrint = true;
		boolean isReprinted = false;

		if (isOverflow && isAlreadyPrinted() && !isPrintWhenDetailOverflows())
		{
			isToPrint = false;
		}

		if (
			isToPrint && 
			isPrintWhenExpressionNull() &&
			!isPrintRepeatedValues()
			)
		{
			if (
				( !isPrintInFirstWholeBand() || !getBand().isFirstWholeOnPageColumn()) &&
				( getPrintWhenGroupChanges() == null || !getBand().isNewGroup(getPrintWhenGroupChanges()) ) &&
				( !isOverflow || !isPrintWhenDetailOverflows() )
				)
			{
				isToPrint = false;
			}
		}

		if (
			isToPrint && 
			availableHeight < getRelativeY() + getHeight()
			)
		{
			isToPrint = false;
			willOverflow = true;
		}
		
		if (
			isToPrint && 
			isOverflow && 
			//(isAlreadyPrinted() || !isPrintRepeatedValues())
			(isPrintWhenDetailOverflows() && (isAlreadyPrinted() || (!isAlreadyPrinted() && !isPrintRepeatedValues())))
			)
		{
			isReprinted = true;
		}

		resetTextChunk();

		if (isToPrint)
		{
			chopTextElement(0);
		}
		
		setToPrint(isToPrint);
		setReprinted(isReprinted);
		
		return willOverflow;
	}


	@Override
	protected JRPrintElement fill()
	{
		JRTemplatePrintText text = new JRTemplatePrintText(getJRTemplateText(), printElementOriginator);
		text.setUUID(getUUID());
		text.setX(getX());
		text.setY(getRelativeY());
		text.setWidth(getWidth());
//		if (getRotation() == ROTATION_NONE)
//		{
			//text.setHeight(getPrintElementHeight());
			text.setHeight(getStretchHeight());
//		}
//		else
//		{
//			text.setHeight(getHeight());
//		}
		text.setRunDirection(getRunDirection());
		text.setLineSpacingFactor(getLineSpacingFactor());
		text.setLeadingOffset(getLeadingOffset());
		if (keepAverageCharWidth)
		{
			text.setAverageCharWidth(getAverageCharWidth());
		}
		text.setTextHeight(getTextHeight());
		
		transferProperties(text);

		//text.setText(getRawText());
		setPrintText(text);
		
		return text;
	}


	@Override
	public void collectExpressions(JRExpressionCollector collector)
	{
		collector.collect(this);
	}

	@Override
	public void visit(JRVisitor visitor)
	{
		visitor.visitStaticText(this);
	}

	
	@Override
	protected void resolveElement(JRPrintElement element, byte evaluation)
	{
		// nothing
	}


	@Override
	public JRFillCloneable createClone(JRFillCloneFactory factory)
	{
		return new JRFillStaticText(this, factory);
	}


	@Override
	protected boolean canOverflow()
	{
		return false;
	}


	@Override
	protected boolean scaleFontToFit()
	{
		return false;
	}


	@Override
	public String getText()
	{
		return ((JRStaticText) parent).getText();
	}

}

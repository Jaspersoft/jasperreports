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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRBreak;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRVisitor;
import net.sf.jasperreports.engine.type.BreakTypeEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRFillBreak extends JRFillElement implements JRBreak
{

	private static final Log log = LogFactory.getLog(JRFillBreak.class);

	/**
	 *
	 */
	protected JRFillBreak(
		JRBaseFiller filler,
		JRBreak breakElement, 
		JRFillObjectFactory factory
		)
	{
		super(filler, breakElement, factory);
	}


	protected JRFillBreak(JRFillBreak breakElement, JRFillCloneFactory factory)
	{
		super(breakElement, factory);
	}


	@Override
	public int getWidth()
	{
		int width;
		switch (getType())
		{
			case PAGE:
				width = filler.pageWidth - filler.leftMargin - filler.rightMargin;
				break;
			default:
				width = filler.columnWidth;
				break;
		}
		return width;
	}

	@Override
	public BreakTypeEnum getType()
	{
		return BreakTypeEnum.getValueOrDefault(((JRBreak)parent).getType());
	}

	@Override
	public void setType(BreakTypeEnum type)
	{
		throw new UnsupportedOperationException();
	}


	@Override
	protected void evaluate(
		byte evaluation
		) throws JRException
	{
		this.reset();
		
		this.evaluatePrintWhenExpression(evaluation);
		evaluateProperties(evaluation);
		evaluateStyle(evaluation);
		
		setValueRepeating(true);
	}


	@Override
	protected JRPrintElement fill()
	{
		return null;
//		JRPrintLine printLine = null;
//
//		printLine = new JRBasePrintLine(filler.getJasperPrint().getDefaultStyleProvider());
//		printLine.setX(0);
//		printLine.setY(this.getRelativeY());
//		printLine.setWidth(getWidth());
//		printLine.setHeight(1);
//		printLine.setPen(JRGraphicElement.PEN_DOTTED);
//		printLine.setForecolor(getForecolor());
//		
//		return printLine;
	}

	@Override
	protected JRTemplateElement createElementTemplate()
	{
		// not called
		return null;
	}


	@Override
	public void collectExpressions(JRExpressionCollector collector)
	{
		collector.collect(this);
	}

	@Override
	public void visit(JRVisitor visitor)
	{
		visitor.visitBreak(this);
	}

	@Override
	protected void resolveElement(JRPrintElement element, byte evaluation)
	{
		// nothing
	}


	@Override
	public JRFillCloneable createClone(JRFillCloneFactory factory)
	{
		return new JRFillBreak(this, factory);
	}


	@Override
	public void rewind()
	{
	}


	@Override
	protected boolean prepare(
		int availableHeight,
		boolean isOverflow,
		boolean isLoverflowAllowed
		) throws JRException
	{
		super.prepare(availableHeight, isOverflow, isLoverflowAllowed);
		
		if (!this.isToPrint())
		{
			return false;
		}
		
		boolean isToPrint = true;

		if (isOverflow && this.isAlreadyPrinted())// && !this.isPrintWhenDetailOverflows())
		{
			isToPrint = false;
		}

		//boolean willOverflow = false;

		if (
			isToPrint && 
			availableHeight < getRelativeY() + getHeight()
			)
		{
			isToPrint = false;
			//willOverflow = true;
		}
		
		if (isToPrint)
		{
			boolean paginationIgnored = filler.isIgnorePagination();
			if (getType() == BreakTypeEnum.COLUMN)
			{
				//column break
				if (paginationIgnored)
				{
					// unpaginated report, column breaks not honoured
					if (log.isTraceEnabled())
					{
						log.trace("unpaginated report, column break not triggered");
					}
				}
				else if (!filler.isFirstColumnBand || filler.atLeastOneElementIsToPrint)
				{
					setPrepareHeight(availableHeight - getRelativeY());
				}
			}
			else
			{
				//page break
				if (!filler.isPageBreakInhibited())
				{
					boolean apply = true;
					if (paginationIgnored)
					{
						String propValue = filler.getPropertiesUtil().getProperty(this, PROPERTY_PAGE_BREAK_NO_PAGINATION);
						apply = propValue != null && propValue.equals(PAGE_BREAK_NO_PAGINATION_APPLY);
						if (log.isTraceEnabled())
						{
							log.trace("unpaginated report, page break appied " + apply);
						}
					}
					
					if (apply)
					{
						setPrepareHeight(availableHeight - getRelativeY());
						filler.columnIndex = filler.columnCount - 1;
					}
				}
			}
		}
			
		this.setToPrint(isToPrint);
		this.setReprinted(false);

		return false;
	}
	
	
}

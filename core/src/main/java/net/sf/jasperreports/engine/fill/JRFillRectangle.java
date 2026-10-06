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
import net.sf.jasperreports.engine.JRPrintRectangle;
import net.sf.jasperreports.engine.JRRectangle;
import net.sf.jasperreports.engine.JRVisitor;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRFillRectangle extends JRFillGraphicElement implements JRRectangle
{


	/**
	 *
	 */
	protected JRFillRectangle(
		JRBaseFiller filler,
		JRRectangle rectangle, 
		JRFillObjectFactory factory
		)
	{
		super(filler, rectangle, factory);
	}


	protected JRFillRectangle(JRFillRectangle rectangle, JRFillCloneFactory factory)
	{
		super(rectangle, factory);
	}


	@Override
	public int getRadius()
	{
		return getStyleResolver().getRadius(this);
	}
		
	@Override
	public Integer getOwnRadius()
	{
		return providerStyle == null || providerStyle.getOwnRadius() == null ? ((JRRectangle)this.parent).getOwnRadius() : providerStyle.getOwnRadius();
	}

	@Override
	public void setRadius(Integer radius)
	{
	}

	/**
	 *
	 */
	protected JRTemplateRectangle getJRTemplateRectangle()
	{
		return (JRTemplateRectangle) getElementTemplate();
	}


	@Override
	protected JRTemplateElement createElementTemplate()
	{
		return new JRTemplateRectangle(
				getElementOrigin(), 
				filler.getJasperPrint().getDefaultStyleProvider(), 
				this
				);
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
		JRPrintRectangle printRectangle = null;

		printRectangle = new JRTemplatePrintRectangle(this.getJRTemplateRectangle(), printElementOriginator);
		printRectangle.setUUID(this.getUUID());
		printRectangle.setX(this.getX());
		printRectangle.setY(this.getRelativeY());
		printRectangle.setWidth(getWidth());
		printRectangle.setHeight(this.getStretchHeight());
		transferProperties(printRectangle);
		
		return printRectangle;
	}


	@Override
	public void collectExpressions(JRExpressionCollector collector)
	{
		collector.collect(this);
	}

	@Override
	public void visit(JRVisitor visitor)
	{
		visitor.visitRectangle(this);
	}

	@Override
	protected void resolveElement(JRPrintElement element, byte evaluation)
	{
		// nothing
	}


	@Override
	public JRFillCloneable createClone(JRFillCloneFactory factory)
	{
		return new JRFillRectangle(this, factory);
	}

}

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
package net.sf.jasperreports.engine.base;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRStaticText;
import net.sf.jasperreports.engine.JRVisitor;


/**
 * This class is used for representing a static text element. Together with its parents, it contains the full
 * functionality needed for handling a static text.
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRBaseStaticText extends JRBaseTextElement implements JRStaticText
{


	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_TEXT = "text";
	/**
	 *
	 */
	protected String text;

	/**
	 * Initializes the element properties. 
	 */
	protected JRBaseStaticText(JRStaticText staticText, JRBaseObjectFactory factory)
	{
		super(staticText, factory);
		
		text = staticText.getText();
	}
		

	@Override
	public String getText()
	{
		return this.text;
	}

	@Override
	public void setText(String text)
	{
		Object old = this.text;
		this.text = text;
		getEventSupport().firePropertyChange(PROPERTY_TEXT, old, this.text);
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


}

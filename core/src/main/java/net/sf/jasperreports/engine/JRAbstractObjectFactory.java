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
package net.sf.jasperreports.engine;

import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.base.JRBaseFont;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class JRAbstractObjectFactory implements JRVisitor
{


	/**
	 *
	 */
	private Map<Object,Object> objectsMap = new HashMap<>();
	private Object visitResult;


	/**
	 *
	 */
	public Object get(Object object)
	{
		return objectsMap.get(object);
	}

	/**
	 *
	 */
	public void put(Object object, Object copy)
	{
		objectsMap.put(object, copy);
	}


	/**
	 *
	 */
	public Object getVisitResult(JRVisitable visitable)
	{
		if (visitable != null)
		{
			visitable.visit(this);
			return visitResult;
		}
		return null;
	}


	/**
	 *
	 */
	public void setVisitResult(Object visitResult)
	{
		this.visitResult = visitResult;
	}


	/**
	 *
	 */
	public abstract JRDefaultStyleProvider getDefaultStyleProvider();


	/**
	 *
	 */
	public abstract JRStyle getStyle(JRStyle style);

	/**
	 * Sets a style or a style reference on an object.
	 * <p/>
	 * If the container includes a style (see {@link JRStyleContainer#getStyle() getStyle()},
	 * a copy of this style will be created via {@link #getStyle(JRStyle) getStyle(JRStyle)}
	 * and set on the object.
	 * <p/>
	 * In addition to this, the implementation needs to handle the case when the container includes
	 * an external style reference (see {@link JRStyleContainer#getStyleNameReference() getStyleNameReference()}.
	 * 
	 * @param setter a setter for the object on which the style should be set.
	 * @param styleContainer the original style container
	 * @see #getStyle(JRStyle)
	 */
	public abstract void setStyle(JRStyleSetter setter, JRStyleContainer styleContainer);
	
	
	/**
	 *
	 */
	public JRFont getFont(JRStyleContainer styleContainer, JRFont font)
	{
		JRBaseFont baseFont = null;

		if (font != null)
		{
			baseFont = (JRBaseFont)get(font);
			if (baseFont == null)
			{
				baseFont = new JRBaseFont(styleContainer, font, this);
			}
		}

		return baseFont;
	}


	/**
	 *
	 */
	public abstract JRConditionalStyle getConditionalStyle(JRConditionalStyle conditionalStyle, JRStyle parentStyle);

	public abstract JRExpression getExpression(JRExpression expression, boolean assignNotUsedId);
	
	public JRExpression getExpression(JRExpression expression)
	{
		return getExpression(expression, false);
	}
}

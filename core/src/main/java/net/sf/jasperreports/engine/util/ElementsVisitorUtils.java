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
package net.sf.jasperreports.engine.util;

import java.util.Collection;

import net.sf.jasperreports.engine.ElementsVisitor;
import net.sf.jasperreports.engine.JRVisitable;
import net.sf.jasperreports.engine.JRVisitor;

/**
 * Utility methods for element visitors.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ElementsVisitorUtils
{
	
	/**
	 * Determines whether a visitor is to visit deep/nested elements.
	 * 
	 * @param visitor the visitor
	 * @return whether a visitor is to visit deep/nested elements
	 */
	public static boolean visitDeepElements(JRVisitor visitor)
	{
		return visitor instanceof ElementsVisitor 
				&& ((ElementsVisitor) visitor).visitDeepElements();
	}

	/**
	 * Visits a collections of elements.
	 * 
	 * @param visitor the visitor
	 * @param elements the elements
	 */
	public static void visitElements(JRVisitor visitor, 
			Collection<? extends JRVisitable> elements)
	{
		if (elements != null)
		{
			for (JRVisitable element : elements)
			{
				element.visit(visitor);
			}
		}
	}
	
}

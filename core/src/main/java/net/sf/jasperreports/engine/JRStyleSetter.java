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


/**
 * An interface implemented by objects upon which style attributes can be set.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see JRAbstractObjectFactory#setStyle(JRStyleSetter, JRStyleContainer)
 */
public interface JRStyleSetter
{

	/**
	 * Set the style on the object.
	 * <p/>
	 * In some cases, this method is invoked after the object has requested its style to be set.
	 * 
	 * @param style the style to be used by the object
	 */
	void setStyle(JRStyle style);
	
	/**
	 * Set the name of an external style that is to be used by the object.
	 * 
	 * @param name the name of an external style
	 */
	void setStyleNameReference(String name);

}

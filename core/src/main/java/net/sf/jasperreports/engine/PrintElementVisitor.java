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
 * Print element visitor interface.
 * 
 * <p>
 * The visit methods can receive one argument of a specified type.
 * </p>
 *
 * @param <T> the type of the argument that gets passed to the visit operation
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see JRPrintElement#accept(PrintElementVisitor, Object)
 */
public interface PrintElementVisitor<T>
{

	/**
	 * Visits a text print element.
	 * 
	 * @param textElement the element to visit
	 * @param arg an argument passed for the visit
	 */
	void visit(JRPrintText textElement, T arg);

	/**
	 * Visits a print image.
	 * 
	 * @param image the image to visit
	 * @param arg an argument passed for the visit
	 */
	void visit(JRPrintImage image, T arg);

	/**
	 * Visits a print rectangle.
	 * 
	 * @param rectangle the rectangle to visit
	 * @param arg an argument passed for the visit
	 */
	void visit(JRPrintRectangle rectangle, T arg);

	/**
	 * Visits a print line.
	 * 
	 * @param line the line to visit
	 * @param arg an argument passed for the visit
	 */
	void visit(JRPrintLine line, T arg);

	/**
	 * Visits a print ellipse.
	 * 
	 * @param ellipse the ellipse to visit
	 * @param arg an argument passed for the visit
	 */
	void visit(JRPrintEllipse ellipse, T arg);

	/**
	 * Visits a print frame.
	 * 
	 * @param frame the frame to visit
	 * @param arg an argument passed for the visit
	 */
	void visit(JRPrintFrame frame, T arg);

	/**
	 * Visits a generic print element.
	 * 
	 * @param printElement the element to visit
	 * @param arg an argument passed for the visit
	 */
	void visit(JRGenericPrintElement printElement, T arg);
	
}

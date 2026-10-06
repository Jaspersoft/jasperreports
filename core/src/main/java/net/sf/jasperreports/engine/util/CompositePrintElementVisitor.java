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

import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JRPrintEllipse;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRPrintLine;
import net.sf.jasperreports.engine.JRPrintRectangle;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.PrintElementVisitor;

/**
 * A print element visitor that contains several visitors to which it delegates calls.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class CompositePrintElementVisitor<T> implements PrintElementVisitor<T>
{
	private final PrintElementVisitor<T>[] visitors;

	/**
	 * Creates a composite visitor.
	 * 
	 * @param visitors the visitors to delegate calls to
	 */
	public CompositePrintElementVisitor(PrintElementVisitor<T> ... visitors)
	{
		this.visitors = visitors;
	}
	
	@Override
	public void visit(JRPrintText textElement, T arg)
	{
		for (PrintElementVisitor<T> visitor : visitors)
		{
			visitor.visit(textElement, arg);
		}
	}

	@Override
	public void visit(JRPrintImage image, T arg)
	{
		for (PrintElementVisitor<T> visitor : visitors)
		{
			visitor.visit(image, arg);
		}
	}

	@Override
	public void visit(JRPrintRectangle rectangle, T arg)
	{
		for (PrintElementVisitor<T> visitor : visitors)
		{
			visitor.visit(rectangle, arg);
		}
	}

	@Override
	public void visit(JRPrintLine line, T arg)
	{
		for (PrintElementVisitor<T> visitor : visitors)
		{
			visitor.visit(line, arg);
		}
	}

	@Override
	public void visit(JRPrintEllipse ellipse, T arg)
	{
		for (PrintElementVisitor<T> visitor : visitors)
		{
			visitor.visit(ellipse, arg);
		}
	}

	@Override
	public void visit(JRPrintFrame frame, T arg)
	{
		for (PrintElementVisitor<T> visitor : visitors)
		{
			visitor.visit(frame, arg);
		}
	}

	@Override
	public void visit(JRGenericPrintElement printElement, T arg)
	{
		for (PrintElementVisitor<T> visitor : visitors)
		{
			visitor.visit(printElement, arg);
		}
	}

}

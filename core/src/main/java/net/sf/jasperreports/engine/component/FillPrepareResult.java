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
package net.sf.jasperreports.engine.component;

/**
 * A result of a {@link FillComponent#prepare(int) component fill preparation}.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class FillPrepareResult
{

	/**
	 * A result that indicates that the component will not print and does not
	 * require a band overflow.
	 * 
	 * <p>
	 * This result would be used when the component does not intend to include
	 * any print element in the generated report.
	 */
	public static final FillPrepareResult NO_PRINT_NO_OVERFLOW = new FillPrepareResult(false, 0, false);

	/**
	 * A result that indicates that the component will print, but will not
	 * stretch vertically.
	 * 
	 * <p>
	 * This would be used when the component will produce a print element that
	 * fits the space allocated at design time for the component element.
	 */
	public static final FillPrepareResult PRINT_NO_STRETCH = new FillPrepareResult(true, 0, false);

	/**
	 * Returns a result that indicates that the component will not print
	 * (at the current position in the generated report), but requires
	 * an overflow so that it can print on a new column/page.
	 * 
	 * @param stretchHeight the height that is consumed by the component;
	 * usually this would be the same as the <code>availableHeight</code>
	 * argument passed to {@link FillComponent#prepare(int)}
	 * @return a result as described above
	 */
	public static FillPrepareResult noPrintOverflow(int stretchHeight)
	{
		return new FillPrepareResult(false, stretchHeight, true);
	}

	/**
	 * Returns a result which indicates that the component will print at the
	 * current position, and optionally consume more vertical space
	 * and/or require a band overflow.
	 * 
	 * @param stretchHeight the stretched height of the component
	 * @param overflow whether a band overflow is required so that the
	 * component would continue printing on a new column/page
	 * @return a result that the component will print with the specified
	 * stretch height and overflow flag
	 */
	public static FillPrepareResult printStretch(int stretchHeight, boolean overflow)
	{
		return new FillPrepareResult(true, stretchHeight, overflow);
	}
	
	private final boolean toPrint;
	private final boolean overflow;
	private final int stretchHeight;

	/**
	 * Creates a fill preparation result.
	 * 
	 * @param toPrint indicates whether the component will produce a print
	 * element at the current position in the generated report
	 * @param stretchHeight the amount of vertical space consumed
	 * by the component
	 * @param overflow indicates whether a band overflow is required in order
	 * to continue the component fill on a new column/page 
	 */
	public FillPrepareResult(boolean toPrint,
			int stretchHeight, boolean overflow)
	{
		this.stretchHeight = stretchHeight;
		this.toPrint = toPrint;
		this.overflow = overflow;
	}
	
	/**
	 * Returns whether the component will produce a print element at the current
	 * position in the generated report.
	 * 
	 * @return whether the component will print
	 */
	public boolean isToPrint()
	{
		return toPrint;
	}

	/**
	 * Returns whether a band overflow is required in order to continue the 
	 * component fill on a new column/page.
	 * 
	 * @return whether a band overflow is required
	 */
	public boolean willOverflow()
	{
		return overflow;
	}

	/**
	 * Returns the amount of vertical space consumed by the component.
	 * 
	 * @return the amount of vertical space consumed by the component
	 */
	public int getStretchHeight()
	{
		return stretchHeight;
	}
	
	public FillPrepareResult addStretch(int delta)
	{
		return new FillPrepareResult(toPrint, stretchHeight + delta, overflow);
	}

}

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

import java.util.StringTokenizer;



/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRPrintElementIndex
{


	/**
	 *
	 */
	private int reportIndex;
	private int pageIndex;
	private String address;


	/**
	 *
	 */
	public JRPrintElementIndex(
		int reportIndex,
		int pageIndex,
		String address
		)
	{
		this.reportIndex = reportIndex;
		this.pageIndex = pageIndex;
		this.address = address;
	}


	/**
	 *
	 */
	public int getReportIndex()
	{
		return this.reportIndex;
	}
		

	/**
	 *
	 */
	public int getPageIndex()
	{
		return this.pageIndex;
	}
		

	/**
	 *
	 *
	public Integer[] getElementIndexes()
	{
		return (Integer[]) elementIndexes.toArray(new Integer[elementIndexes.size()]);
	}


	/**
	 * Returns a String representation of this element index.
	 * <p>
	 * The representation is obtained by appending all the indexes that compose this instance.
	 * The result is compatible with {@link #parsePrintElementIndex(String) parsePrintElementIndex(String)},
	 * which can be used to recreate the elemetn index instance from a String representation.
	 * </p>
	 */
	@Override
	public String toString()
	{
		StringBuilder str = new StringBuilder();
		str.append(reportIndex);
		str.append('_');
		str.append(pageIndex);
		str.append('_');
		str.append(address);
		return str.toString();
	}

	
	/**
	 * 
	 */
	public Integer[] getAddressArray()
	{
		StringTokenizer tkzer = new StringTokenizer(address, "_");
		
		Integer[] elementIndexes = new Integer[tkzer.countTokens()];
		int c = 0;
		while (tkzer.hasMoreTokens())
		{
			elementIndexes[c++] = Integer.valueOf(tkzer.nextToken());
		}

		return elementIndexes;
	}
	
	
	/**
	 * Parses a String representation as obtained by {@link #toString() toString()} back
	 * into an element index instance.
	 * 
	 * @param indexStr the String representation of an element index
	 * @return an element index instance corresponding to the String representation
	 */
	public static JRPrintElementIndex parsePrintElementIndex(String indexStr)
	{
		StringTokenizer tkzer = new StringTokenizer(indexStr, "_");
		
		String reportIndexToken = tkzer.nextToken();
		String pageIndexToken = tkzer.nextToken();
		
		return
			new JRPrintElementIndex(
				Integer.parseInt(reportIndexToken),
				Integer.parseInt(pageIndexToken),
				indexStr.substring(reportIndexToken.length() + pageIndexToken.length() + 2)
				);
	}

}

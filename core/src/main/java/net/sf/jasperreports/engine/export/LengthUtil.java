/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2025 Cloud Software Group, Inc. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
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
package net.sf.jasperreports.engine.export;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class LengthUtil 
{

	/**
	 * Converts pixels to inches without decimal truncation.
	 */
	public static double inch(double pixels, int dpi)
	{
		return pixels / dpi;
	}
	
	/**
	 * 
	 */
	public static double inchFloor2Dec(double pixels, int dpi)
	{
		double inches = pixels / dpi;
		return (Math.floor(inches * 100.0)) / 100.0;
	}
	
	/**
	 * 
	 */
	public static double inchFloor4Dec(double pixels, int dpi)
	{
		double inches = pixels / dpi;
		return (Math.floor(inches * 10000.0)) / 10000.0;
	}

	/**
	 * 
	 */
	public static double inchRound2Dec(double pixels, int dpi)
	{
		double inches = pixels / dpi;
		return (Math.round(inches * 100.0)) / 100.0;
	}
	
	/**
	 * 
	 */
	public static double inchRound4Dec(double pixels, int dpi)
	{
		double inches = pixels / dpi;
		return (Math.round(inches * 10000.0)) / 10000.0;
	}

	/**
	 * Convert a float value to twips
	 * @param pixels value that need to be converted
	 * @return converted value in twips
	 */
	public static int twip(float pixels, int dpi)
	{
		return (int)(pixels * 1440.0f / dpi);
	}

	/**
	 * Convert an int value from pixels to EMU
	 * @param pixels value that needs to be converted
	 * @return converted value in EMU
	 */
	public static int emu(float pixels, int dpi)
	{
		return (int)(pixels * 914400.0f / dpi);
	}
	
	/**
	 * 
	 */
	public static int halfPoint(float pixels, int dpi)
	{
		return (int)(pixels * 576.0f / dpi);
	}

	
	private LengthUtil()
	{
	}

}

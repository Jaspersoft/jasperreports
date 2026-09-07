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
package net.sf.jasperreports.chartthemes.spring;

import java.awt.Font;
import java.math.BigDecimal;

import net.sf.jasperreports.engine.JRFont;

/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class ChartThemesUtilities
{
	public static int getScale(double value)
	{
		return BigDecimal.valueOf(value).precision() - BigDecimal.valueOf(value).scale() - 1;
	}

	public static double getTruncatedValue(double value, int scale)
	{
		String newValue;
		String sign = value < 0 ? "-" : "";
		value = Math.abs(value);

		if (scale < 0)
		{
			value *= Math.pow(10.0, -scale);
			newValue = (String.valueOf(value) + "0000").substring(0,4);
		}
		else if (scale > 2)
		{
			newValue =  (String.valueOf(value / Math.pow(10.0, scale - 2)) + "000").substring(0,3);
		}
		else
		{
			newValue = String.valueOf(value);
			if (newValue.length() > 4)
				newValue = newValue.substring(0,4);
		}
		return Double.valueOf(sign + newValue);
	}

	public static double getScaledValue(double value, int scale)
	{
		if (scale < 0)
		{
			return value * Math.pow(10.0, -scale);
		}
		else if (scale > 2)
		{
			return value / Math.pow(10.0, scale-2);
		}

		return value;
	}
	
	public static int getAwtFontStyle(JRFont font, int defaultBoldStyle, int defaultItalicStyle)
	{
		if (font == null)
			return Font.PLAIN;
		
		int style = Font.PLAIN;
		if ((font.isOwnBold() == null && defaultBoldStyle == Font.BOLD) ||
		(font.isOwnBold() != null && font.isOwnBold()))
		{
				style = Font.BOLD;
		}
		
		if ((font.isOwnItalic() == null && defaultItalicStyle == Font.ITALIC) ||
		(font.isOwnItalic() != null && font.isOwnItalic()))
		{
			if (style == Font.BOLD)
				style |= Font.ITALIC;
			else
				style = Font.ITALIC;
		}
		return style;
	}
}

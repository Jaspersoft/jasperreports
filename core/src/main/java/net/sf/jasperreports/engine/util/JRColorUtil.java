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
package net.sf.jasperreports.engine.util;

import java.awt.Color;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.type.ColorEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class JRColorUtil
{
	private static final String EXCEPTION_MESSAGE_KEY_INVALID_COLOR = "util.color.invalid.color";

	/**
	 *
	 */
	public static final int COLOR_MASK = Integer.parseInt("FFFFFF", 16);
	private static final String RGB = "rgb";
	public static final String RGB_PREFIX = RGB + "(";
	public static final String RGBA_PREFIX = RGB + "a(";
	public static final String RGBA_SUFFIX = ")";

	/**
	 *
	 */
	public static String getColorHexa(Color color)
	{
		if (color.equals(Color.WHITE))
		{
			return "FFFFFF";
		}
		if (color.equals(Color.BLACK))
		{
			return "000000";
		}
		String hexa = Integer.toHexString(color.getRGB() & COLOR_MASK).toUpperCase();
		return ("000000" + hexa).substring(hexa.length());
	}

	/**
	 *
	 */
	public static String getCssColor(Color color)
	{
		if (color.getAlpha() == 255)
		{
			return "#" + JRColorUtil.getColorHexa(color);
		}
		return 
			"rgba(" 
			+ color.getRed() + ", " 
			+ color.getGreen() + ", "
			+ color.getBlue() + ", "
			+ ((float)color.getAlpha() / 255) + ")";
	}

	/**
	 *
	 */
	public static Color getColor(String strColor, Color defaultColor)
	{
		Color color = null;

		if (strColor != null)
		{
			strColor = strColor.trim();
			
			if (strColor.length() > 0)
			{
				char firstChar = strColor.charAt(0);
				if (firstChar == '#')
				{
					color = new Color(Integer.parseInt(strColor.substring(1), 16));
				}
				else if ('0' <= firstChar && firstChar <= '9')
				{
					color = new Color(Integer.parseInt(strColor));
				}
				else if (
					strColor.toLowerCase().startsWith(RGB)
					&& strColor.endsWith(RGBA_SUFFIX)
					)
				{
					strColor = strColor.toLowerCase();
					boolean hasAlpha = false;
					String prefix = null;
					int numArrayLength = 0;
					if (strColor.startsWith(RGBA_PREFIX))
					{
						hasAlpha = true;
						prefix = RGBA_PREFIX;
						numArrayLength = 4;
					}
					else if (strColor.startsWith(RGB_PREFIX))
					{
						hasAlpha = false;
						prefix = RGB_PREFIX;
						numArrayLength = 3;
					}
					else
					{
						throw 
							new JRRuntimeException(
								EXCEPTION_MESSAGE_KEY_INVALID_COLOR,
								new Object[]{strColor});
					}
					
					String numStr = strColor.substring(prefix.length(), strColor.length() - RGBA_SUFFIX.length());
					String[] numArray = numStr.split(",");
					
					if (numArray == null || numArray.length != numArrayLength)
					{
						throw 
							new JRRuntimeException(
								EXCEPTION_MESSAGE_KEY_INVALID_COLOR,
								new Object[]{strColor});
					}
					else
					{
						for (int i = 0; i < numArray.length; i++)
						{
							numArray[i] = numArray[i].trim();
							if (numArray[i].length() == 0)
							{
								throw 
									new JRRuntimeException(
										EXCEPTION_MESSAGE_KEY_INVALID_COLOR,
										new Object[]{strColor});
							}
						}

						int red = Integer.parseInt(numArray[0]);
						int green = Integer.parseInt(numArray[1]);
						int blue = Integer.parseInt(numArray[2]);
						
						if (hasAlpha)
						{
							float alpha = Float.parseFloat(numArray[3]);
							color = new Color(red, green, blue, (int)(alpha * 255));
						}
						else
						{
							color = new Color(red, green, blue);
						}
					}
				}
				else
				{
					ColorEnum colorEnum = ColorEnum.getByName(strColor);
					if (colorEnum == null)
					{
						color = defaultColor;
					}
					else
					{
						color = colorEnum.getColor();
					}
				}
			}
		}

		return color;
	}

	public static int getOpaqueArgb(Color color, Color defaultColor) {
		Color result = color == null ? defaultColor : color;
		return result == null ? 0x00000000 : result.getRGB() & 0xffffffff;
	}

	private JRColorUtil()
	{
	}
}

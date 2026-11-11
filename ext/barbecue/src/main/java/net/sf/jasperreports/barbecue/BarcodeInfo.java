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
package net.sf.jasperreports.barbecue;

import java.awt.Color;
import java.awt.Font;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class BarcodeInfo
{

	private String type;
	private String code;
	private String applicationIdentifier;
	private boolean drawText;
	private boolean requiresChecksum;
	private Integer barWidth;
	private Integer barHeight;
	private Font font;
	private Color foreground;
	private Color background;

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public boolean isDrawText()
	{
		return drawText;
	}

	public void setDrawText(boolean drawText)
	{
		this.drawText = drawText;
	}

	public Integer getBarWidth()
	{
		return barWidth;
	}

	public void setBarWidth(Integer barWidth)
	{
		this.barWidth = barWidth;
	}

	public Integer getBarHeight()
	{
		return barHeight;
	}

	public void setBarHeight(Integer barHeight)
	{
		this.barHeight = barHeight;
	}

	public Font getFont()
	{
		return font;
	}

	/**
	 * Sets the font used for the barcode text.
	 * When not set, the Barbecue library default applies, which is <code>null</code>
	 * (no text drawn) in headless environments.
	 */
	public void setFont(Font font)
	{
		this.font = font;
	}

	public Color getForeground()
	{
		return foreground;
	}

	/**
	 * Sets the color of the bars and of the barcode text.
	 * When not set, the Barbecue library default applies, which is black.
	 */
	public void setForeground(Color foreground)
	{
		this.foreground = foreground;
	}

	public Color getBackground()
	{
		return background;
	}

	/**
	 * Sets the color of the spaces between the bars and of the area behind the barcode text.
	 * A fully transparent color leaves these areas unpainted.
	 * When not set, the Barbecue library default applies, which is white.
	 */
	public void setBackground(Color background)
	{
		this.background = background;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public boolean getRequiresChecksum()
	{
		return requiresChecksum;
	}

	public void setRequiresChecksum(boolean requiresChecksum)
	{
		this.requiresChecksum = requiresChecksum;
	}

	public String getApplicationIdentifier()
	{
		return applicationIdentifier;
	}

	public void setApplicationIdentifier(String applicationIdentifier)
	{
		this.applicationIdentifier = applicationIdentifier;
	}
	
}

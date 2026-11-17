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
package net.sf.jasperreports.barbecue;

import java.io.Serializable;

import net.sf.jasperreports.engine.JRConstants;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class BarcodeInfo implements Serializable
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private String type;
	private String code;
	private String applicationIdentifier;
	private boolean drawText;
	private boolean requiresChecksum;
	private Integer barWidth;
	private Integer barHeight;

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

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

import net.sf.jasperreports.engine.JRRuntimeException;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class Bounds implements Cloneable
{
	private int startX;
	private int endX;
	private int startY;
	private int endY;
	
	public Bounds(int startX, int endX, int startY, int endY)
	{
		this.startX = startX;
		this.endX = endX;
		this.startY = startY;
		this.endY = endY;
	}
	
	@Override
	public String toString()
	{
		return "[" + startX + "," + endX
				+ "),[" + startY + "," + endY + ")";
	}

	public int getStartX()
	{
		return startX;
	}

	public int getEndX()
	{
		return endX;
	}

	public int getStartY()
	{
		return startY;
	}

	public int getEndY()
	{
		return endY;
	}

	public void setStartX(int startX)
	{
		this.startX = startX;
	}

	public void setEndX(int endX)
	{
		this.endX = endX;
	}

	public void setStartY(int startY)
	{
		this.startY = startY;
	}

	public void setEndY(int endY)
	{
		this.endY = endY;
	}
	
	public void grow(int startX, int endX, int startY, int endY)
	{
		if (this.startX > startX)
		{
			this.startX = startX;
		}
		if (this.endX < endX)
		{
			this.endX = endX;
		}
		if (this.startY > startY)
		{
			this.startY = startY;
		}
		if (this.endY < endY)
		{
			this.endY = endY;
		}
	}
	
	public boolean contains(int startX, int endX, int startY, int endY)
	{
		return this.startX <= startX && this.endX >= endX
				&& this.startY <= startY && this.endY >= endY;
	}

	public Bounds cloneBounds()
	{
		try
		{
			return (Bounds) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			//should not happen
			throw new JRRuntimeException(e);
		}
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Bounds))
		{
			return false;
		}
		
		Bounds b = (Bounds) obj;
		return startX == b.startX && endX == b.endX
				&& startY == b.startY && endY == b.endY;
	}
}

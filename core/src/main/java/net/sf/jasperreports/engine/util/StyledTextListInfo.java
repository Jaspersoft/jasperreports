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

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class StyledTextListInfo
{
	private final boolean ordered;
	private final String type;
	private Integer start;
	private final boolean hasParentLi;
	
	private int itemCount = 0;
	private int itemIndex = 0;
	private Integer cutStart = 0;
	
	private boolean atLiStart = false;
	private boolean atLiEnd = false;

	public StyledTextListInfo(
		boolean ordered,
		String type,
		Integer start,
		boolean hasParentLi
		)
	{
		this.ordered = ordered;
		this.type = type;
		this.start = start;
		this.cutStart = start;
		this.hasParentLi = hasParentLi;
	}

	public boolean ordered()
	{
		return ordered;
	}

	public String getType()
	{
		return type;
	}

	public int getStart()
	{
		return start == null ? 1 : start;
	}

	public void setStart(int start)
	{
		this.start = start;
	}

	public int getCutStart()
	{
		return cutStart == null ? 1 : cutStart;
	}

	public void setCutStart(int cutStart)
	{
		this.cutStart = cutStart;
	}

	public int getItemIndex()
	{
		return itemIndex;
	}

	public void setItemIndex(int itemIndex)
	{
		this.itemIndex = itemIndex;
	}

	public boolean hasParentLi()
	{
		return hasParentLi;
	}

	public int getItemCount()
	{
		return itemCount;
	}

	public void setItemCount(int itemCount)
	{
		this.itemCount = itemCount;
	}

	public boolean atLiStart()
	{
		return atLiStart;
	}

	public void setAtLiStart(boolean atLiStart)
	{
		this.atLiStart = atLiStart;
	}

	public boolean atLiEnd()
	{
		return atLiEnd;
	}

	public void setAtLiEnd(boolean atLiEnd)
	{
		this.atLiEnd = atLiEnd;
	}
}

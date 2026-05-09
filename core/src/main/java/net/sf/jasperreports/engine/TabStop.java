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
package net.sf.jasperreports.engine;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.type.TabStopAlignEnum;
import net.sf.jasperreports.engine.util.ObjectUtils;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonPropertyOrder({
	"position",
	"alignment"
	})
public class TabStop implements JRCloneable, Serializable, Deduplicable
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	/**
	 * 
	 */
	private int position;
	private TabStopAlignEnum alignment;

	/**
	 * 
	 */
	public TabStop()
	{
	}

	/**
	 * 
	 */
	public TabStop(int position, TabStopAlignEnum alignment)
	{
		this.position = position;
		this.alignment = alignment;
	}

	/**
	 * Gets the tab stop alignment.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public TabStopAlignEnum getAlignment()
	{
		return alignment;
	}
	
	/**
	 * Sets the tab stop alignment.
	 */
	public void setAlignment(TabStopAlignEnum alignment)
	{
		this.alignment = alignment;
	}
	
	/**
	 * Gets the tab stop position.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public int getPosition()
	{
		return position;
	}
	
	/**
	 * Sets the tab stop position.
	 */
	public void setPosition(int position)
	{
		this.position = position;
	}
	
	@Override
	public Object clone()
	{
		TabStop clone = null;

		try
		{
			clone = (TabStop)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new JRRuntimeException(e);
		}
		
		return clone;
	}

	@Override
	public int getHashCode()
	{
		ObjectUtils.HashCode hash = ObjectUtils.hash();
		hash.add(position);
		hash.add(alignment);
		return hash.getHashCode();
	}

	@Override
	public boolean isIdentical(Object object)
	{
		if (this == object)
		{
			return true;
		}
		
		if (!(object instanceof TabStop))
		{
			return false;
		}
		
		TabStop tab = (TabStop) object;
		return position == tab.position && alignment == tab.alignment;
	}
	
}
